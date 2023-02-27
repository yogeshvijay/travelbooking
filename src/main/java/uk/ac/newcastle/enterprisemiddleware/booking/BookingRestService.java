package uk.ac.newcastle.enterprisemiddleware.booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;

import uk.ac.newcastle.enterprisemiddleware.contact.Contact;
import uk.ac.newcastle.enterprisemiddleware.contact.ContactService;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerNotFoundException;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiNotFoundException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

@Path("/booking")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingRestService {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	BookingService bookService;

	/**
	 * <p>
	 * Creates a new booking from the values provided. Performs validation and will
	 * return a JAX-RS response with either 201 (Resource created) or with a map of
	 * fields, and related errors.
	 * </p>
	 *
	 * @param Booking The Booking object, constructed automatically from JSON input,
	 *                to be <i>created</i> via
	 *                {@link ContactService#create(Contact)}
	 * @return A Response indicating the outcome of the create operation
	 */
	@SuppressWarnings("unused")
	@POST
	@Operation(description = "Add a new Booking to the database")
	@APIResponses(value = { @APIResponse(responseCode = "201", description = "Booking created successfully."),
			@APIResponse(responseCode = "400", description = "Invalid Booking supplied in request body"),
			@APIResponse(responseCode = "409", description = "Booking supplied in request body conflicts with an existing Booking"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	@Transactional
	public Response createBooking(
			@Parameter(description = "JSON representation of booking object to be added to the database", required = true) Booking booking) {

		if (booking == null) {
			throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
		}

		Response.ResponseBuilder builder;

		try {

			booking.setId(null);

			// Go add the new booking.
			bookService.create(booking);

			// Create a "Resource Created" 201 Response and pass the contact back in case it
			// is needed.
			builder = Response.status(Response.Status.CREATED).entity(booking);

		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues

			log.info("inside constraint exception");
			Map<String, String> responseObj = new HashMap<>();

			for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
				responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

		} catch (TaxiNotFoundException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("TaxiId", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.NOT_FOUND, e);
		} catch (CustomerNotFoundException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("CustomerId", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.NOT_FOUND, e);
		} catch (BookingPresentException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("Booking", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
		} catch (Exception e) {
			// Handle generic exceptions
			log.info("inside generic exception " + e.getMessage());
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("Bad Request", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
		}

		log.info("createBooking completed. booking = " + booking);
		return builder.build();
	}

	/**
	 * <p>
	 * Search for and return a Booking identified by email address.
	 * </p>
	 *
	 * @param email The string parameter value provided as a customer's email
	 * @return A list of Bookings by Customer
	 */
	@GET
	@Operation(summary = "Fetch all Bookings by CustId", description = "Returns a JSON representation of the Booking object with the provided CustId.")
	@APIResponses(value = { @APIResponse(responseCode = "200", description = "Booking found"),
			@APIResponse(responseCode = "404", description = "Booking with CustId not found") })
	public Response retrieveBookings(@QueryParam("customerId") Long customerId) {

		List<Booking> bookingList;

		if (customerId == null) {
			
			bookingList = bookService.getAll();

			for (Booking book : bookingList) {
				book.setCustomerId(book.getCustomer().getId());
				book.setTaxiId(book.getTaxi().getId());
			}

		} else {
			try {

				bookingList = bookService.findByCustId(customerId);

				for (Booking book : bookingList) {
					book.setCustomerId(book.getCustomer().getId());
					book.setTaxiId(book.getTaxi().getId());
				}

			} catch (NoResultException e) {
				// Verify that the customer exists. Return 404, if not present.
				throw new RestServiceException("No customer with the custId : " + customerId + " was found!",
						Response.Status.NOT_FOUND);
			}
		}

		return Response.ok(bookingList).build();
	}

	/**
	 * <p>
	 * Deletes a Booking using the ID provided. If the ID is not present then
	 * nothing can be deleted.
	 * </p>
	 *
	 * <p>
	 * Will return a JAX-RS response with either 204 NO CONTENT or with a map of
	 * fields, and related errors.
	 * </p>
	 *
	 * @param id The Long parameter value provided as the id of the Booking to be
	 *           deleted
	 * @return A Response indicating the outcome of the delete operation
	 */
	@DELETE
	@Path("/{id:[0-9]+}")
	@Operation(description = "Delete a Booking from the database")
	@APIResponses(value = {
			@APIResponse(responseCode = "204", description = "The Booking has been successfully deleted"),
			@APIResponse(responseCode = "400", description = "Invalid Booking id supplied"),
			@APIResponse(responseCode = "404", description = "Booking with id not found"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	@Transactional
	public Response deleteBooking(
			@Parameter(description = "Id of Booking to be deleted", required = true) @Schema(minimum = "0") @PathParam("id") long id) {

		Response.ResponseBuilder builder;

		Booking booking = bookService.findById(id);

		if (booking == null) {
			// Verify that the booking exists. Return 404, if not present.
			throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}

		try {
			bookService.delete(booking);

			builder = Response.noContent();

		} catch (ConstraintViolationException ce) {
			Map<String, String> responseObj = new HashMap<>();

			for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
				responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

		} catch (Exception e) {
			// Handle generic exceptions
			throw new RestServiceException(e);
		}
		log.info("deleteBooking completed. Booking = " + booking);
		return builder.build();
	}

	@GET
	@Cache
	@Path("/{id:[0-9]+}")
	@Operation(summary = "Fetch a Booking by id", description = "Returns a JSON representation of the Booking object with the provided id.")
	@APIResponses(value = { @APIResponse(responseCode = "200", description = "Booking found"),
			@APIResponse(responseCode = "404", description = "Booking with id not found") })
	public Response retrieveBookingById(
			@Parameter(description = "Id of Booking to be fetched") @Schema(minimum = "0", required = true) @PathParam("id") long id) {

		Booking booking = bookService.findById(id);

		if (booking == null) {
			// Verify that the customer exists. Return 404, if not present.
			throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}

		booking.setCustomerId(booking.getCustomer().getId());
		booking.setTaxiId(booking.getTaxi().getId());

		log.info("findById " + id + ": found Booking = " + booking);

		return Response.ok(booking).build();
	}

}
