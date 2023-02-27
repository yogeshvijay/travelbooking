package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

@RegisterRestClient(configKey = "hotel-api")
public interface HotelService {

	@POST
	@Path("/bookings")
	HotelBooking createHotelBooking(
			@Parameter(description = "JSON representation of customer object to be added to the database", required = true) HotelBooking booking);
	
	@GET
	@Path("/bookings")
	List<HotelBooking> getAllBookingForCustomer(@QueryParam("customerId") Long customerId);

	@DELETE
	@Path("/bookings/{id:[0-9]+}")
	HotelBooking deleteHotelBooking(@PathParam("id") long id);

	@GET
	@Path("/hotels/{id:[0-9]+}")
	Hotel getHotelById(@PathParam("id") long id);

	@GET
	@Path("/customers/email/{email:.+[%40|@].+}")
	Customer getCustomerByEmail(
			@Parameter(description = "Email of customer to be fetched", required = true) @PathParam("email") String email);

	@POST
	@Path("/customers")
	Customer createHotelCustomer(
			@Parameter(description = "JSON representation of customer object to be added to the database", required = true) Customer customer);

}
