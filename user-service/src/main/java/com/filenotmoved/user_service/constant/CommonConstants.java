package com.filenotmoved.user_service.constant;

import java.util.ArrayList;
import java.util.List;

public class CommonConstants {

	private CommonConstants() {
	}

	public static final List<String> VALID_IMAGE_FORMAT = new ArrayList<>(
			List.of("jpg", "jpeg", "png", "heic", "webp"));

	public static final int PAGE_SIZE = 20;

	public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB

}
