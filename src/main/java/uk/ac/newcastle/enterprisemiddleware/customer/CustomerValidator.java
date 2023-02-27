package uk.ac.newcastle.enterprisemiddleware.customer;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import uk.ac.newcastle.enterprisemiddleware.contact.UniqueEmailException;

/**
 * <p>
 * This class provides methods to check customer objects against arbitrary
 * requirements.
 * </p>
 *
 * @author Yogesh Vijayan
 * @see Customer
 * @see CustomerRepository
 * @see javax.validation.Validator
 */
@ApplicationScoped
public class CustomerValidator {

	@Inject
	Validator validator;

	@Inject
	CustomerService custService;

	/**
	 * <p>
	 * Validates the given customer object and throws validation exceptions based on
	 * the type of error. If the error is standard bean validation errors then it
	 * will throw a ConstraintValidationException with the set of the constraints
	 * violated.
	 * <p/>
	 *
	 *
	 * <p>
	 * If the error is caused because an existing customer with the same email is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 *
	 *
	 * @param customer The customer object to be validated
	 * @throws ConstraintViolationException If Bean Validation errors exist
	 * @throws ValidationException          If customer with the same email already
	 *                                      exists
	 */
	void validateCustomer(Customer customer) throws ConstraintViolationException, ValidationException {

		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the email address
		if (emailAlreadyExists(customer.getEmail(), customer.getId())) {
			throw new UniqueEmailException("Unique Email Violation");
		}
	}

	/**
	 * <p>
	 * Checks if a customer with the same email address is already registered. This
	 * is the only way to easily capture the "@UniqueConstraint(columnNames =
	 * "email")" constraint from the customer class.
	 * </p>
	 *
	 * <p>
	 * Since Update will being using an email that is already in the database we
	 * need to make sure that it is the email from the record being updated.
	 * </p>
	 *
	 * @param email The email to check is unique
	 * @param id    The user id to check the email against if it was found
	 * @return boolean which represents whether the email was found, and if so if it
	 *         belongs to the user with id
	 */
	boolean emailAlreadyExists(String email, Long id) {

		Customer customer = null;
		Customer customerWithID = null;
		try {
			customer = custService.findByEmail(email);
		} catch (NoResultException e) {
			// ignore
		}

		if (customer != null && id != null) {
			try {
				customerWithID = custService.findById(id);
				if (customerWithID != null && customerWithID.getEmail().equals(email)) {
					customer = null;
				}
			} catch (NoResultException e) {
				// ignore
			}
		}
		return customer != null;
	}

	public boolean isCustomerValid(String email) {

		Customer cust = null;

		try {
			cust = custService.findByEmail(email);
		} catch (NoResultException e) {
			// ignore
		}

		return cust != null;
	}
}
