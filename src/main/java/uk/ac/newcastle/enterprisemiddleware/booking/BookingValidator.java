package uk.ac.newcastle.enterprisemiddleware.booking;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerNotFoundException;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerValidator;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiNotFoundException;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiValidator;

@ApplicationScoped
public class BookingValidator {

	@Inject
	Validator validator;

	@Inject
	BookingService bookService;

	@Inject
	CustomerValidator custValidator;

	@Inject
	TaxiValidator taxiValidator;
	
	@Inject
	TaxiService taxiService;
	
	@Inject
	CustomerService custService;

	public void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {

		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		if (isBookingPresent(booking)) {
			throw new BookingPresentException("Taxi already booked for that date");
		}

		if (isTaxiIdPresent(booking.getTaxiId())) {
			throw new TaxiNotFoundException("No Taxi with that ID found");
		}
		
		if (isCustIdPresent(booking.getCustomerId())) {
			throw new CustomerNotFoundException("No Customer with that ID found");
		}
	}

	boolean isTaxiIdPresent(Long taxiId) {
		
		Taxi isTaxi = null;
		
		try {
			isTaxi = taxiService.findById(taxiId);
		} catch (NoResultException e) {
			// ignore
		}
				
		return isTaxi == null;
	}
	
	boolean isCustIdPresent(Long custId) {
		
		Customer isCustomer = null;
		
		try {
			isCustomer = custService.findById(custId);
		} catch (NoResultException e) {
			// ignore
		}
				
		return isCustomer == null;
	}

	boolean isBookingPresent(Booking booking) {

		Booking isBooking = null;

		try {
			isBooking = bookService.findByAll(booking.getTaxi(), booking.getBookingDate());
		} catch (NoResultException e) {

		}

		return isBooking != null;
	}

}
