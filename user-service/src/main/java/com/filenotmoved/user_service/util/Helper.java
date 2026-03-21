package com.filenotmoved.user_service.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.filenotmoved.user_service.constant.CommonConstants;
import com.filenotmoved.user_service.constant.SortOrder;
import com.filenotmoved.user_service.constant.TableConfig;
import com.filenotmoved.user_service.dto.IssuesDto;
import com.filenotmoved.user_service.dto.SearchRequest;
import com.filenotmoved.user_service.entity.Issues;
import com.filenotmoved.user_service.exception.custom.FileSizeExceedsException;
import com.filenotmoved.user_service.exception.custom.InvalidFileFormatException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		return size < CommonConstants.MAX_IMAGE_SIZE;
	}

	private static boolean isValidFormat(String fileName) {
		final String fileExtension = getFileExtension(fileName);
		return fileExtension != null && CommonConstants.VALID_IMAGE_FORMAT.contains(fileExtension.toLowerCase());
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

	public static Point parseLocation(String locationStr) {
		GeometryFactory geometryFactory = new GeometryFactory();
		try {
			if (locationStr == null || locationStr.isBlank())
				return null;
			if (locationStr.toUpperCase().startsWith("POINT")) {
				WKTReader reader = new WKTReader();
				return (Point) reader.read(locationStr);
			} else {
				String[] parts = locationStr.split(",");
				if (parts.length == 2) {
					double lat = Double.parseDouble(parts[0].trim());
					double lng = Double.parseDouble(parts[1].trim());
					return geometryFactory.createPoint(new Coordinate(lng, lat));
				}
			}
		} catch (Exception e) {
			log.error("Error parsing location: {}", e.getMessage());
		}
		return geometryFactory.createPoint(new Coordinate(0, 0));
	}

	public static IssuesDto issueEntityToDto(Issues issue) {
		return new IssuesDto(issue.getId(), issue.getDescription(),
				issue.getLocation().getY() + "," + issue.getLocation().getX(), issue.getLocality(), issue.getCity(),
				issue.getCreatedBy(),
				issue.getCreateAt(), issue.getIssueType(), issue.getImageKey());
	}

}