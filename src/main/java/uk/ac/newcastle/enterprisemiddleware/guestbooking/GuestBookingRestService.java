package uk.ac.newcastle.enterprisemiddleware.guestbooking;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingPresentException;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.contact.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiNotFoundException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

/**
 * <p>
 * This class produces a RESTful service exposing the functionality of
 * {@link customerService}.
 * </p>
 * *
 * 
 * @author Yogesh Vijayan
 * @see GuestBookingRestService
 * @see javax.ws.rs.core.Response
 */
@Path("/guestbooking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	UserTransaction transaction;

	@Inject
	CustomerService customerService;

	@Inject
	BookingService bookingService;

	/**
	 * <p>
	 * Creates a new customer from the values provided. Performs validation and will
	 * return a JAX-RS response with either 201 (Resource created) or with a map of
	 * fields, and related errors.
	 * </p>
	 *
	 * @param customer The customer object, constructed automatically from JSON
	 *                 input, to be <i>created</i> via
	 *                 {@link customerService#create(customer)}
	 * @return A Response indicating the outcome of the create operation
	 * @throws SystemException
	 * @throws SecurityException
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("unused")
	@POST
	@Operation(description = "Add a new Guest Booking to the database")
	@APIResponses(value = {
			@APIResponse(responseCode = "201", description = "Customer and Booking created successfully."),
			@APIResponse(responseCode = "400", description = "Invalid Guest Booking supplied in request body"),
			@APIResponse(responseCode = "409", description = "customer supplied in request body conflicts with an existing customer"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	public Response createGuestBooking(
			@Parameter(description = "JSON representation of GuestBooking object to be added to the database", required = true) GuestBooking guestBooking)
			throws IllegalStateException, SecurityException, SystemException {

		if (guestBooking == null) {
			throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
		}

		Response.ResponseBuilder builder;

		try {
			// Create a customer for GuestBooking
			transaction.begin();

			Customer customer = new Customer();
			customer = guestBooking.getCustomer();

			// Clear the ID if accidentally set
			customer.setId(null);

			// Go add the new customer.
			customer = customerService.create(customer);

			// Create a booking for GuestBooking

			Booking booking = new Booking();
			booking = guestBooking.getBooking();

			booking.setId(null);
			booking.setCustomerId(customer.getId());

			// Go add the new booking.
			bookingService.create(booking);

			transaction.commit();

			builder = Response.status(Response.Status.CREATED).entity(guestBooking);

		} catch (ConstraintViolationException ce) {

			transaction.rollback();

			// Handle bean validation issues
			Map<String, String> responseObj = new HashMap<>();

			for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
				responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

		} catch (UniqueEmailException e) {

			transaction.rollback();

			// Handle the unique constraint violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("email", "That email is already used, please use a unique email");
			throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);

		} catch (TaxiNotFoundException e) {

			transaction.rollback();

			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("TaxiId", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.NOT_FOUND, e);

		} catch (BookingPresentException e) {

			transaction.rollback();

			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("Booking", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);

		} catch (Exception e) {

			// Handle generic exceptions
			transaction.rollback();
			log.info("inside generic exception");
			log.info(e.getStackTrace().toString());

			throw new RestServiceException(e.getMessage());
		}

		log.info("Booking completed. Booking = " + guestBooking);

		return Response.status(Response.Status.CREATED).entity(guestBooking).build();
	}

}
