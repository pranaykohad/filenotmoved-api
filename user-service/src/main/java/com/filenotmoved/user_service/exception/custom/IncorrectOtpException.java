package com.filenotmoved.user_service.exception.custom;

public class IncorrectOtpException extends RuntimeException {

	private static final long serialVersionUID = 3034387251891425271L;
	
	public IncorrectOtpException(String message) {
		super(message);
	}
}