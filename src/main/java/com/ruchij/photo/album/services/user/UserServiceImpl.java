package com.ruchij.photo.album.services.user;

import com.ruchij.photo.album.components.id.IdGenerator;
import com.ruchij.photo.album.daos.user.Credentials;
import com.ruchij.photo.album.daos.user.User;
import com.ruchij.photo.album.daos.user.UserRepository;
import com.ruchij.photo.album.services.exceptions.ResourceConflictException;
import com.ruchij.photo.album.services.usage.UsageService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UsageService usageService;
	private final PasswordEncoder passwordEncoder;
	private final IdGenerator idGenerator;
	private final Clock clock;

	public UserServiceImpl(
		UserRepository userRepository,
		UsageService usageService,
		PasswordEncoder passwordEncoder,
		IdGenerator idGenerator,
		Clock clock
	) {
		this.userRepository = userRepository;
		this.usageService = usageService;
		this.passwordEncoder = passwordEncoder;
		this.idGenerator = idGenerator;
		this.clock = clock;
	}

	@Override
	public User create(String email, String password, String firstName, Optional<String> lastName) throws ResourceConflictException {
		Optional<User> existingUserOptional = userRepository.findByEmail(email);

		if (existingUserOptional.isPresent()) {
			throw new ResourceConflictException("Email address already exist");
		}

		String userId = idGenerator.generateId(User.class);
		Instant instant = clock.instant();

		User user = new User();
		user.setId(userId);
		user.setCreatedAt(instant);
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);

		String encodedPassword = passwordEncoder.encode(password);

		Credentials credentials = new Credentials();
		credentials.setUser(user);
		credentials.setPassword(encodedPassword);

		user.setCredentials(credentials);

		User savedUser = userRepository.save(user);

		usageService.create(userId);

		return savedUser;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("Email NOT found: %s".formatted(username)));

		return user;
	}
}
