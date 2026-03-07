package com.filenotmoved.user_service.exception.custom;

public class GenericException extends RuntimeException {

	private static final long serialVersionUID = -6280993494230335878L;
	
	public GenericException(String message) {
        super(message);
    }
}