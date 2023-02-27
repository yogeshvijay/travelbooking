package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.jboss.resteasy.reactive.Cache;

import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

@Path("/taxi")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaxiRestService {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	TaxiService taxiService;

	/**
	 * <p>
	 * Creates a new Taxi from the values provided. Performs validation and will
	 * return a JAX-RS response with either 201 (Resource created) or with a map of
	 * fields, and related errors.
	 * </p>
	 *
	 * @param taxi The Taxi object, constructed automatically from JSON input, to be
	 *             <i>created</i> via
	 * 
	 * @return A Response indicating the outcome of the create operation
	 */
	@SuppressWarnings("unused")
	@POST
	@Operation(description = "Add a new Taxi to the database")
	@APIResponses(value = { @APIResponse(responseCode = "201", description = "Taxi created successfully."),
			@APIResponse(responseCode = "400", description = "Invalid Taxi supplied in request body"),
			@APIResponse(responseCode = "409", description = "Taxi supplied in request body conflicts with an existing Taxi"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	@Transactional
	public Response createTaxi(
			@Parameter(description = "JSON representation of taxi object to be added to the database", required = true) Taxi taxi) {

		if (taxi == null) {
			throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
		}

		Response.ResponseBuilder builder;

		try {

			// Go add the new Taxi.
			log.info("before createTaxi completed. Taxi = " + taxi);

			taxi.setId(null);

			taxiService.create(taxi);

			// Create a "Resource Created" 201 Response and pass the contact back in case it
			// is needed.
			builder = Response.status(Response.Status.CREATED).entity(taxi);

		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues

			log.info("inside constraint exception");
			Map<String, String> responseObj = new HashMap<>();

			for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
				responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

		} catch (UniqueRegNoException e) {
			// Handle the unique constraint violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("RegNo", "That Registration No is already used, Please enter a unique one");
			throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
		} catch (Exception e) {
			// Handle generic exceptions
			log.info("inside generic exception");
			throw new RestServiceException(e);
		}

		log.info("createTaxi completed. Taxi = " + taxi);
		return builder.build();
	}

	@GET
	@Operation(summary = "Fetch all Taxis", description = "Returns a JSON array of all stored Taxi objects.")
	public Response retrieveAllTaxis() {
		// Create an empty collection to contain the intersection of Contacts to be
		// returned
		List<Taxi> taxisList;

		taxisList = taxiService.findAll();

		return Response.ok(taxisList).build();
	}

	@DELETE
	@Path("/{id:[0-9]+}")
	@Operation(description = "Delete a taxi from the database")
	@APIResponses(value = { @APIResponse(responseCode = "204", description = "The Taxi has been successfully deleted"),
			@APIResponse(responseCode = "400", description = "Invalid Taxi id supplied"),
			@APIResponse(responseCode = "404", description = "Taxi with id not found"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	@Transactional
	public Response deleteTaxi(
			@Parameter(description = "Id of Taxi to be deleted", required = true) @Schema(minimum = "0") @PathParam("id") long id) {

		Response.ResponseBuilder builder;

		Taxi taxi = taxiService.findById(id);

		if (taxi == null) {
			// Verify that the customer exists. Return 404, if not present.
			throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}

		try {
			taxiService.delete(taxi);

			builder = Response.noContent();

		} catch (Exception e) {
			// Handle generic exceptions
			throw new RestServiceException(e);
		}
		log.info("deleteTaxi completed. Taxi = " + taxi);

		return builder.build();
	}

	/**
	 * <p>
	 * Search for and return a taxi identified by id.
	 * </p>
	 *
	 * @param id The long parameter value provided as a taxi's id
	 * @return A Response containing a single Taxi
	 */
	@GET
	@Cache
	@Path("/{id:[0-9]+}")
	@Operation(summary = "Fetch a Taxi by id", description = "Returns a JSON representation of the Taxi object with the provided id.")
	@APIResponses(value = { @APIResponse(responseCode = "200", description = "Taxi found"),
			@APIResponse(responseCode = "404", description = "Taxi with id not found") })
	public Response retrieveTaxiById(
			@Parameter(description = "Id of Taxi to be fetched") @Schema(minimum = "0", required = true) @PathParam("id") long id) {

		Taxi taxi = taxiService.findById(id);

		if (taxi == null) {
			// Verify that the Taxi exists. Return 404, if not present.
			throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}

		log.info("findById " + id + ": found Taxi = " + taxi);

		return Response.ok(taxi).build();
	}

	/**
	 * <p>
	 * Updates the customer with the ID provided in the database. Performs
	 * validation, and will return a JAX-RS response with either 200 (ok), or with a
	 * map of fields, and related errors.
	 * </p>
	 *
	 * @param customer The customer object, constructed automatically from JSON
	 *                 input, to be <i>updated</i> via
	 *                 {@link customerService#update(customer)}
	 * @param id       The long parameter value provided as the id of the customer
	 *                 to be updated
	 * @return A Response indicating the outcome of the create operation
	 */
	@PUT
	@Path("/{id:[0-9]+}")
	@Operation(description = "Update a Taxi in the database")
	@APIResponses(value = { @APIResponse(responseCode = "200", description = "Taxi updated successfully"),
			@APIResponse(responseCode = "400", description = "Invalid Taxi supplied in request body"),
			@APIResponse(responseCode = "404", description = "Taxi with id not found"),
			@APIResponse(responseCode = "409", description = "Taxi details supplied in request body conflict with another existing Taxi"),
			@APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request") })
	@Transactional
	public Response updateTaxi(
			@Parameter(description = "Id of Taxi to be updated", required = true) @Schema(minimum = "0") @PathParam("id") long id,
			@Parameter(description = "JSON representation of Taxi object to be updated in the database", required = true) Taxi taxi) {

		if (taxi == null || taxi.getId() == null) {
			throw new RestServiceException("Invalid Taxi supplied in request body", Response.Status.BAD_REQUEST);
		}

		if (taxi.getId() != null && taxi.getId() != id) {
			// The client attempted to update the read-only Id. This is not permitted.
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("id", "The Taxi ID in the request body must match that of the customer being updated");
			throw new RestServiceException("customer Taxi supplied in request body conflict with another customer",
					responseObj, Response.Status.CONFLICT);
		}

		if (taxiService.findById(taxi.getId()) == null) {
			// Verify that the taxi exists. Return 404, if not present.
			throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}

		Response.ResponseBuilder builder;

		try {
			// Apply the changes the taxi.
			taxiService.update(taxi);

			// Create an OK Response and pass the customer back in case it is needed.
			builder = Response.ok(taxi);

		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			Map<String, String> responseObj = new HashMap<>();

			for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
				responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
		} catch (UniqueRegNoException e) {
			// Handle the unique constraint violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("RegNo", "That Registration No is already used, Please enter a unique one");
			throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
		} catch (Exception e) {
			// Handle generic exceptions
			throw new RestServiceException(e);
		}

		log.info("updateTaxi completed. taxi = " + taxi);
		return builder.build();
	}

}
