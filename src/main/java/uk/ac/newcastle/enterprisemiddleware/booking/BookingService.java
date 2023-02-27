package uk.ac.newcastle.enterprisemiddleware.booking;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;

@Dependent
public class BookingService {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	BookingValidator validator;

	@Inject
	BookingRespository bookRepo;

	@Inject
	CustomerService custService;

	@Inject
	TaxiService taxiService;

	public Booking create(Booking booking) throws Exception {

		Customer customer = custService.findById(booking.getCustomerId());

		Taxi taxi = taxiService.findById(booking.getTaxiId());

		booking.setCustomer(customer);
		booking.setTaxi(taxi);
		
		// Check to make sure the data fits with the parameters in the Booking model and
		// passes validation.
		validator.validateBooking(booking);

		// Write the Customer to the database.
		return bookRepo.create(booking);
	}

	Booking findById(Long id) {
		return bookRepo.findById(id);
	}

	List<Booking> findByCustId(Long custId) {
		
		Customer customer = custService.findById(custId);

		return bookRepo.findByCustId(customer);

	}

	List<Booking> getAll() {

		return bookRepo.findByCustId();

	}

	Booking findByAll(Taxi taxi, Date bookDate) {

		return bookRepo.findByAll(taxi, bookDate);

	}

	Booking delete(Booking booking) throws Exception {

		log.info("delete() - Deleting " + booking.toString());

		Booking bookingDeleted = null;

		if (booking.getId() != null) {
			bookingDeleted = bookRepo.delete(booking);
		} else {
			log.info("delete() - No ID was found so can't Delete.");
		}

		return bookingDeleted;
	}

}
