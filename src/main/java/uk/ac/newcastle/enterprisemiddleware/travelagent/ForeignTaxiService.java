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

@RegisterRestClient(configKey = "taxi-api")
public interface ForeignTaxiService {

	@POST
	@Path("/bookings")
	ForeignTaxiBooking createTaxiBooking(
			@Parameter(description = "JSON representation of customer object to be added to the database", required = true) ForeignTaxiBooking booking);

	@GET
	@Path("/bookings/customerId/{customerId:[0-9]+}")
	List<ForeignTaxiBooking> getAllBookingForCustomer(@PathParam("customerId") Long customerId);

	@DELETE
	@Path("/bookings/id/{id:[0-9]+}")
	ForeignTaxiBooking deleteTaxiBooking(@PathParam("id") long id);

	@GET
	@Path("/taxis/id/{id:[0-9]+}")
	ForeignTaxi getTaxiById(@PathParam("id") long id);

	@GET
	@Path("/customers/email/{email:.+[%40|@].+}")
	Customer getCustomerByEmail(
			@Parameter(description = "Email of customer to be fetched", required = true) @PathParam("email") String email);

	@POST
	@Path("/customers")
	Customer createTaxiCustomer(
			@Parameter(description = "JSON representation of customer object to be added to the database", required = true) Customer customer);

}
