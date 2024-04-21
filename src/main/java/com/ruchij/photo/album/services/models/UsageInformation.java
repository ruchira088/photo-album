package com.ruchij.photo.album.services.models;

import com.ruchij.photo.album.daos.usage.Usage;

public record UsageInformation(int albums, int photos, long dataUsed) {
	public static UsageInformation from(Usage usage) {
		return new UsageInformation(usage.getAlbumCount(), usage.getPhotoCount(), usage.getBytesUsed());
	}
}
