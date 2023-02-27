package uk.ac.newcastle.enterprisemiddleware.travelagent;

import javax.validation.ValidationException;

public class HotelNotFoundException extends ValidationException {

	public HotelNotFoundException(String message) {
		super(message);
	}

	public HotelNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public HotelNotFoundException(Throwable cause) {
		super(cause);
	}

}
