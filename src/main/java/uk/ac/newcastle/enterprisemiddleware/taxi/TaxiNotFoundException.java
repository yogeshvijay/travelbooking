package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.validation.ValidationException;

public class TaxiNotFoundException extends ValidationException {

	public TaxiNotFoundException(String message) {
		super(message);
	}

	public TaxiNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaxiNotFoundException(Throwable cause) {
		super(cause);
	}

}
