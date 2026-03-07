package com.filenotmoved.user_service.exception.custom;

public class InvalidFileFormatException extends RuntimeException {

	private static final long serialVersionUID = -1998601405739608400L;
	public InvalidFileFormatException(String message) {
		super(message);
	}
}