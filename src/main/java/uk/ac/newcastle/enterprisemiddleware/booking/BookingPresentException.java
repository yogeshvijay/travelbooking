package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.validation.ValidationException;

public class BookingPresentException extends ValidationException {
	
	public BookingPresentException(String message) {
		super(message);
	}

	public BookingPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookingPresentException(Throwable cause) {
		super(cause);
	}
}
