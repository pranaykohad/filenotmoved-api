package com.filenotmoved.user_service.exception.custom;

public class BlobNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6212924197746832775L;

	public BlobNotFoundException(String message) {
		super(message);
	}

}
