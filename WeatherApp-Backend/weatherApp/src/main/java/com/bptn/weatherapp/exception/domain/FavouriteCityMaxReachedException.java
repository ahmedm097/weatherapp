package com.bptn.weatherapp.exception.domain;

public class FavouriteCityMaxReachedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FavouriteCityMaxReachedException(String message) {
		super(message);
	}

}
