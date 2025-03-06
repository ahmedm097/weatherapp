package com.bptn.weatherapp.exception.domain;

public class FavouriteCityExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FavouriteCityExistsException(String message) {
		super(message);
	}
}
