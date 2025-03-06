package com.bptn.weatherapp.exception.domain;

public class FavouriteCityNotUserException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FavouriteCityNotUserException(String message) {
		super(message);
	}

}
