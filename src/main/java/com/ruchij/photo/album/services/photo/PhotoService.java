package com.ruchij.photo.album.services.photo;

import com.ruchij.photo.album.daos.photo.Photo;
import com.ruchij.photo.album.services.models.Dimensions;
import com.ruchij.photo.album.services.models.FileData;
import com.ruchij.photo.album.services.models.ImageData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

public interface PhotoService {
	Photo insert(
		String albumId,
		FileData fileData,
		Optional<String> title,
		Optional<String> description,
		Optional<Dimensions> dimensions
	) throws IOException;

	Photo getByPhotoId(String photoId);

	FileData getFileDataByPhotoId(String photoId) throws IOException;

	Photo deletePhotoById(String photoId) throws IOException;

	static ImageData getImageDimensions(FileData input, Optional<Dimensions> dimensionsOptional) throws IOException {
		if (dimensionsOptional.isPresent()) {
			Dimensions dimensions = dimensionsOptional.get();

			return new ImageData(dimensions, input);
		} else {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			input.data().transferTo(outputStream);

			byte[] bytes = outputStream.toByteArray();
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

			if (bufferedImage == null) {
				throw new UnsupportedOperationException("%s file type is NOT supported");
			} else {
				int height = bufferedImage.getHeight();
				int width = bufferedImage.getWidth();

				Dimensions dimensions = new Dimensions(width, height);

				FileData fileData = new FileData(input.name(), input.contentType(), input.size(), new ByteArrayInputStream(bytes));

				return new ImageData(dimensions, fileData);
			}
		}
	}
}
