package com.bptn.weatherapp.exception.domain;

public class FavouriteCityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FavouriteCityNotFoundException(String message) {
		super(message);
	}

}
