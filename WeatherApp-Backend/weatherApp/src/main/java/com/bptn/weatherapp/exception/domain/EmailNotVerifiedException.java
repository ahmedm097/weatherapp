package com.bptn.weatherapp.exception.domain;

public class EmailNotVerifiedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailNotVerifiedException(String message) {
		super(message);
	}

}
