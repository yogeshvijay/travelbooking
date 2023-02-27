package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

@ApplicationScoped
public class TaxiValidator {

	@Inject
	Validator validator;

	@Inject
	TaxiRepository taxiRepo;

	void validateTaxi(Taxi taxi) throws ConstraintViolationException, ValidationException {

		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Taxi>> violations = validator.validate(taxi);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the taxi registration
		if (registrationPresent(taxi.getRegistrationNo())) {
			throw new UniqueRegNoException("Unique RegNo Violation");
		}
	}

	public boolean registrationPresent(String regNo) {

		Taxi taxi = null;

		try {
			taxi = taxiRepo.findByRegNo(regNo);
		} catch (NoResultException e) {
			// ignore
		}

		return taxi != null;
	}

}
