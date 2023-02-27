package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

@Path("/travelagent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TravelAgentRestService {

	@Inject
	TravelAgentService travelService;

	@Inject
	private @Named("logger") Logger log;

	@POST
	@Operation(summary = "Add a new Booking to the database")
	@Transactional
	public Response createTravelAgentBooking(
			@Parameter(description = "JSON representation of customer object to be added to the database", required = true) TravelAgent travelAgent) {
		try {

			travelAgent.setId(null);

			travelAgent = travelService.createBooking(travelAgent);

		} catch (Exception e) {
			throw e;
		}

		return Response.ok(travelAgent).build();
	}

	@GET
	@Operation(summary = "Fetch all TravelAgent Bookings", description = "Returns a JSON array of all stored customer objects.")
	public Response retrieveAllBookings() {

		// Create an empty collection to contain the intersection of Bookings to be
		// returned
		List<TravelAgent> TravelAgent;

		TravelAgent = travelService.fetchAllTravelAgentBookings();

		return Response.ok(TravelAgent).build();
	}

	@DELETE
	@Path("/{id:[0-9]+}")
	@Operation(description = "Delete a TravelAgent Booking from the database")
	@APIResponses(value = {
			@APIResponse(responseCode = "204", description = "The Booking has been successfully deleted"),
			@APIResponse(responseCode = "400", description = "Invalid Booking id supplied"),
			@APIResponse(responseCode = "404", description = "Booking with id not found"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	@Transactional
	public Response deleteTravelBooking(
			@Parameter(description = "Id of customer to be deleted", required = true) @Schema(minimum = "0") @PathParam("id") long id) {

		Response.ResponseBuilder builder;

		TravelAgent travelAgent = travelService.findById(id);

		if (travelAgent == null) {
			// Verify that the customer exists. Return 404, if not present.
			throw new RestServiceException("No Travel Agent Booking with the id " + id + " was found!",
					Response.Status.NOT_FOUND);
		}

		try {
			travelService.delete(travelAgent);

			builder = Response.noContent();

		} catch (Exception e) {
			// Handle generic exceptions
			throw new RestServiceException(e);
		}
		log.info("deleteTravelBooking completed. travelAgent = " + travelAgent);
		return builder.build();
	}

}
