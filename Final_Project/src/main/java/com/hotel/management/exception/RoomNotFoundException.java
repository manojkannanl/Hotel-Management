package com.hotel.management.exception;

public  class RoomNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoomNotFoundException(String message) {
        super(message);
    }
}