package com.ruchij.photo.album.services.monitoring;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.flyway.FlywaySchema;
import com.ruchij.photo.album.daos.flyway.FlywaySchemaRepository;
import com.ruchij.photo.album.services.models.BuildInformation;
import com.ruchij.photo.album.services.models.HealthCheck;
import com.ruchij.photo.album.services.models.HealthCheck.Status;
import com.ruchij.photo.album.services.models.ServiceInformation;
import com.ruchij.photo.album.services.storage.StorageBackend;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.StreamSupport;

@Service
public class MonitoringServiceImpl implements MonitoringService {
	private static final String SERVICE_NAME = "photo-album";

	private final String serviceVersion;
	private final String javaVersion;
	private final String gitBranch;
	private final String gitCommit;
	private final String buildTimestamp;

	private final EntityManager entityManager;
	private final FlywaySchemaRepository flywaySchemaRepository;
	private final ExecutorService executorService;
	private final StorageBackend storageBackend;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public MonitoringServiceImpl(
		BuildInformation buildInformation,
		Properties properties,
		EntityManager entityManager,
		FlywaySchemaRepository flywaySchemaRepository,
		StorageBackend storageBackend,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.serviceVersion = buildInformation.getBuildVersion();
		this.gitBranch = buildInformation.getGitBranch();
		this.gitCommit = buildInformation.getGitCommit();
		this.javaVersion = properties.getProperty("java.version", "unknown");
		this.buildTimestamp = buildInformation.getBuildTimestamp();
		this.entityManager = entityManager;
		this.flywaySchemaRepository = flywaySchemaRepository;
		this.storageBackend = storageBackend;
		this.idGenerator = idGenerator;
		this.clock = clock;

		this.executorService = Executors.newCachedThreadPool();
	}

	@Override
	public ServiceInformation getServiceInformation() {
		return new ServiceInformation(
			SERVICE_NAME,
			serviceVersion,
			clock.instant(),
			javaVersion,
			gitBranch,
			gitCommit,
			buildTimestamp
		);
	}

	@Override
	public HealthCheck performHealthCheck() {
		Status database = runHealthCheck(this::databaseHealthCheck);
		Status storage = runHealthCheck(this::storageHealthCheck);
		HealthCheck healthCheck = new HealthCheck(database, storage);

		return healthCheck;
	}

	private Status runHealthCheck(Callable<Status> healthCheck) {
		Future<Status> future = executorService.submit(healthCheck);

		try {
			return future.get(10, TimeUnit.SECONDS);
		} catch (Exception exception) {
			future.cancel(true);
			return Status.DOWN;
		}
	}

	private Status databaseHealthCheck() {
		TypedQuery<Integer> query = entityManager.createQuery("SELECT 123", Integer.class);
		Integer result = query.getSingleResult();

		return result == 123 ? Status.UP : Status.DOWN;
	}

	private Status storageHealthCheck() throws IOException {
		String key = idGenerator.generateId();
		String word = "Hello World";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(word.getBytes());

		storageBackend.save(key, inputStream);

		InputStream fileContents = storageBackend.get(key);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		fileContents.transferTo(outputStream);

		byte[] byteArray = outputStream.toByteArray();

		storageBackend.delete(key);

		return Arrays.equals(byteArray, word.getBytes()) ? Status.UP : Status.DOWN;
	}

	@Override
	public List<FlywaySchema> flywaySchemas() {
		Comparator<FlywaySchema> comparator = Comparator.comparing(FlywaySchema::getVersion);

		return StreamSupport.stream(flywaySchemaRepository.findAll(Sort.by("version")).spliterator(), false)
			.sorted(comparator)
			.toList();
	}
}
