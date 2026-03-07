package com.filenotmoved.user_service.exception.custom;

public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 5078613952145593373L;
	
	public UnauthorizedException(String message) {
        super(message);
    }

}
