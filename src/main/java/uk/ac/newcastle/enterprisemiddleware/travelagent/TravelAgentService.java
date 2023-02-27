package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingValidator;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

@Dependent
public class TravelAgentService {

	@RestClient
	HotelService hotelService;

	@RestClient
	ForeignTaxiService foreignTaxiService;

	@Inject
	BookingService bookingService;

	@Inject
	BookingValidator taxiBookingValidator;

	@Inject
	TravelAgentRepository travelRepo;

	@Inject
	@Named("logger")
	Logger log;

	TravelAgent createBooking(TravelAgent travelAgent) {

		/*
		 * Start of of Booking for Hotel and validation
		 * 
		 */

		Customer hotelCustomer = new Customer();

		try {

			hotelCustomer = hotelService.getCustomerByEmail(TravelAgentStatic.createTravelAgent().getEmail());

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {

				try {
					hotelCustomer = hotelService.createHotelCustomer(TravelAgentStatic.createTravelAgent());

				} catch (ClientWebApplicationException nestedE) {

					throw new RestServiceException("Cannot process request", e);
				}

			}
		}

		HotelBooking hotelBooking = new HotelBooking();

		hotelBooking = travelAgent.getHotelBooking();

		try {

			Hotel checkHotel = hotelService.getHotelById(hotelBooking.getHotelId());

		} catch (ClientWebApplicationException e) {

			e.printStackTrace();
			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {

				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Hotel", "Hotel Id not found in 3rd Party!");
				throw new RestServiceException("Bad Request", responseObj, Response.Status.NOT_FOUND, e);
			}

			throw new RestServiceException("Cannot process request", e);

		}

		// Setting customer Id to hotel booking
		hotelBooking.setCustomerId(hotelCustomer.getId());

		try {
			hotelBooking = hotelService.createHotelBooking(hotelBooking);

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Bad Request", "Please check Request");
				throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
			} else if (e.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Hotel Booking Conflict", "Hotel Already Booked on that day!");
				throw new RestServiceException("Conflict", responseObj, Response.Status.CONFLICT, e);

			}
		}

		/*
		 * End of Booking for Hotel
		 * 
		 */

		/*
		 * Start of Booking for Foreign Taxi
		 */

		Customer foreignTaxiCustomer = new Customer();

		try {

			foreignTaxiCustomer = foreignTaxiService
					.getCustomerByEmail(TravelAgentStatic.createTravelAgent().getEmail());

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {

				foreignTaxiCustomer = foreignTaxiService.createTaxiCustomer(TravelAgentStatic.createTravelAgent());

			}
		}

		ForeignTaxiBooking foreignBooking = new ForeignTaxiBooking();

		foreignBooking = travelAgent.getForeignTaxiBooking();

		try {

			ForeignTaxi checkForeignTaxi = foreignTaxiService.getTaxiById(foreignBooking.getTaxiId());

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {

				// Delete hotel booking since taxi failed
				hotelService.deleteHotelBooking(hotelBooking.getId());

				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Foreign Taxi", "Foreign Taxi Id not found in 3rd Party!");
				throw new RestServiceException("Bad Request", responseObj, Response.Status.NOT_FOUND, e);
			}
		}

		// Setting customer Id to hotel booking
		foreignBooking.setCustomerId(foreignTaxiCustomer.getId());

		try {
			foreignBooking = foreignTaxiService.createTaxiBooking(foreignBooking);

		} catch (ClientWebApplicationException e) {

			// Delete hotel booking since taxi failed
			hotelService.deleteHotelBooking(hotelBooking.getId());

			if (e.getResponse().getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Bad Request", "Please check Request");
				throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
			} else if (e.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Foreign Taxi Booking Conflict", "Foreign Taxi Already Booked on that day!");
				throw new RestServiceException("Conflict", responseObj, Response.Status.CONFLICT, e);

			}
		}

		/*
		 * End of Booking for Foreign Taxi
		 */

		/*
		 * Start of Booking for Local Taxi
		 */

		Booking localBooking = new Booking();

		localBooking = travelAgent.getTaxiBooking();

		try {
			localBooking.setId(null);
			taxiBookingValidator.validateBooking(localBooking);
			bookingService.create(localBooking);
		} catch (Exception e) {

			/*
			 * delete method for hotel and flight to roll back
			 */

			hotelService.deleteHotelBooking(hotelBooking.getId());
			foreignTaxiService.deleteTaxiBooking(foreignBooking.getId());

			log.info("inside generic exception " + e.getMessage());
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("Validation Error", e.getMessage());
			throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
		}

		/*
		 * End of Booking for Local Taxi
		 */

		travelAgent.setHotelBooking(hotelBooking);
		travelAgent.setTaxiBooking(localBooking);
		travelAgent.setForeignTaxiBooking(foreignBooking);
		travelAgent.setHotelBookingId(hotelBooking.getId());
		travelAgent.setForeignTaxiBookingId(foreignBooking.getId());

		travelRepo.createTravelBooking(travelAgent);

		return travelAgent;

	}

	List<TravelAgent> fetchAllTravelAgentBookings() {

		List<TravelAgent> listAgentsBook = new ArrayList<TravelAgent>();

		listAgentsBook = travelRepo.findAll();

		/*
		 * Set Booking for local taxi
		 * 
		 */

		for (TravelAgent travelAgent : listAgentsBook) {
			travelAgent.getTaxiBooking().setCustomerId(travelAgent.getTaxiBooking().getCustomer().getId());
			travelAgent.getTaxiBooking().setTaxiId(travelAgent.getTaxiBooking().getTaxi().getId());
		}

		/*
		 * Get all bookings for Hotels
		 */

		Customer hotelCustomer = new Customer();

		try {

			hotelCustomer = hotelService.getCustomerByEmail(TravelAgentStatic.createTravelAgent().getEmail());

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Hotel Customer", "Customer with email not found in 3rd Party!");
				throw new RestServiceException("Not Found", responseObj, Response.Status.NOT_FOUND, e);
			}
		}

		List<HotelBooking> listHotelBook = new ArrayList<HotelBooking>();

		try {
			listHotelBook = hotelService.getAllBookingForCustomer(hotelCustomer.getId());

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Hotel Booking", "Booking with id not found in 3rd Party!");
				throw new RestServiceException("Not Found", responseObj, Response.Status.NOT_FOUND, e);
			}

		}

		for (TravelAgent ta : listAgentsBook) {

			for (HotelBooking hb : listHotelBook) {

				if (ta.getHotelBookingId().equals(hb.getId())) {
					ta.setHotelBooking(hb);
				}
			}
		}
		/*
		 * -----------------------------------------------------------------------------
		 * -----------------------------------------------------------------------------
		 */

		/*
		 * Get all bookings for foreign Taxi
		 */

		Customer taxiCustomer = new Customer();

		try {

			taxiCustomer = foreignTaxiService.getCustomerByEmail(TravelAgentStatic.createTravelAgent().getEmail());

		} catch (ClientWebApplicationException e) {

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Foreign Taxi", "Customer with email not found in 3rd Party!");
				throw new RestServiceException("Not Found", responseObj, Response.Status.NOT_FOUND, e);
			}
		}

		List<ForeignTaxiBooking> listTaxiBook = new ArrayList<ForeignTaxiBooking>();

		// add try catch empty

		try {

			listTaxiBook = foreignTaxiService.getAllBookingForCustomer(taxiCustomer.getId());

		} catch (ClientWebApplicationException e) {

			log.info("inside foreign taxi get all exception");
			log.info(e.getMessage());

			if (e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
				
				log.info("inside if for 404");
				
				Map<String, String> responseObj = new HashMap<>();
				responseObj.put("Foreign Taxi Booking", "Foreign Taxi Booking with Id not found in 3rd Party!");
				throw new RestServiceException("Not Found", responseObj, Response.Status.NOT_FOUND, e);
			}
		}

		listTaxiBook = foreignTaxiService.getAllBookingForCustomer(taxiCustomer.getId());

		log.info(listTaxiBook.toString());

		for (TravelAgent ta : listAgentsBook) {

			log.info("testing for booking id");
			log.info(ta.getForeignTaxiBookingId().toString());

			for (ForeignTaxiBooking ftb : listTaxiBook) {
				log.info("outside here if");
				log.info(ftb.getId().toString());

				if (ta.getForeignTaxiBookingId().equals(ftb.getId())) {
					ta.setForeignTaxiBooking(ftb);

					log.info("inside here if");
					log.info(ftb.toString());
				}
			}
		}

		return listAgentsBook;

	}

	TravelAgent findById(Long id) {
		return travelRepo.findById(id);
	}

	TravelAgent delete(TravelAgent travelAgent) throws Exception {

		TravelAgent deletedTravelAgent = null;

		try {
			hotelService.deleteHotelBooking(travelAgent.getHotelBookingId());
			foreignTaxiService.deleteTaxiBooking(travelAgent.getForeignTaxiBookingId());
		} catch (ClientWebApplicationException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("Deletion", "Error Occured during deletion");
			throw new RestServiceException("Not Found", responseObj, Response.Status.INTERNAL_SERVER_ERROR, e);
		}

		if (travelAgent.getId() != null) {
			deletedTravelAgent = travelRepo.delete(travelAgent);
		} else {
			log.info("delete() - No ID was found so can't Delete.");
		}

		return deletedTravelAgent;
	}

}
