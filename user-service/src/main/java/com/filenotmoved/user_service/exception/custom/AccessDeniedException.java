package com.filenotmoved.user_service.exception.custom;

public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = -2616248295979851428L;

	public AccessDeniedException(String message) {
		super(message);
	}
}