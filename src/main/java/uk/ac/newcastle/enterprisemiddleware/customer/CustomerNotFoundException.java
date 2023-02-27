package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.validation.ValidationException;

public class CustomerNotFoundException extends ValidationException {

	public CustomerNotFoundException(String message) {
		super(message);
	}

	public CustomerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerNotFoundException(Throwable cause) {
		super(cause);
	}

}
