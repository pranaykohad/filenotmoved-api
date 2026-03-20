package com.filenotmoved.user_service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.filenotmoved.user_service.constant.ComonConstants;
import com.filenotmoved.user_service.constant.SortOrder;
import com.filenotmoved.user_service.constant.TableConfig;
import com.filenotmoved.user_service.dto.SearchRequest;
import com.filenotmoved.user_service.exception.custom.FileSizeExceedsException;
import com.filenotmoved.user_service.exception.custom.InvalidFileFormatException;

public final class Helper {

	private Helper() {
	}

	public static String getFileNameWithoutTimeAndExtension(String fileName) {
		final int dotIndex = fileName.lastIndexOf('.');
		String newFileName = "";
		newFileName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
		final int dashIndex = newFileName.lastIndexOf('-');
		return dashIndex > 0 ? fileName.substring(0, dashIndex + 1) : fileName;
	}

	public static boolean isPhonenumber(String phone) {
		return phone.length() == 10 && phone.chars().allMatch(Character::isDigit);
	}

	public static void validateBlob(String originalFileName, long fileSize) {
		if (!isValidFormat(originalFileName)) {
			throw new InvalidFileFormatException("File format is not correct: " + originalFileName);
		}
		if (!isFileSizeExceeds(fileSize)) {
			throw new FileSizeExceedsException("File size exceeds: " + fileSize);
		}
	}

	public static String getFileExtension(String fileName) {
		final int lastDotIndex = fileName.lastIndexOf('.');
		return (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) ? fileName.substring(lastDotIndex + 1)
				: null;
	}

	private static boolean isFileSizeExceeds(long size) {
		return size < ComonConstants.MAX_IMAGE_SIZE;
	}

	private static boolean isValidFormat(String fileName) {
		final String fileExtension = getFileExtension(fileName);
		return fileExtension != null && ComonConstants.VALID_IMAGE_FORMAT.contains(fileExtension.toLowerCase());
	}

	public static Pageable buildPage(SearchRequest searchDto) {
		Pageable pageable = PageRequest.of(0, TableConfig.PAGE_SIZE);
		if (StringUtils.hasText(searchDto.getSortColumn())) {
			final Sort sort = searchDto.getSortOrder().equals(SortOrder.DESC)
					? Sort.by(searchDto.getSortColumn()).descending()
					: Sort.by(searchDto.getSortColumn()).ascending();
			pageable = PageRequest.of(searchDto.getCurrentPage(), TableConfig.PAGE_SIZE, sort);
		}
		return pageable;
	}

}