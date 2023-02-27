package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.validation.ValidationException;

import uk.ac.newcastle.enterprisemiddleware.contact.Contact;

/**
 * <p>
 * ValidationException caused if a Contact's email address conflicts with that
 * of another Contact.
 * </p>
 *
 * <p>
 * This violates the uniqueness constraint.
 * </p>
 *
 * @author hugofirth
 * @see Contact
 */
public class UniqueRegNoException extends ValidationException {

	public UniqueRegNoException(String message) {
		super(message);
	}

	public UniqueRegNoException(String message, Throwable cause) {
		super(message, cause);
	}

	public UniqueRegNoException(Throwable cause) {
		super(cause);
	}
}