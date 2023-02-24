package uk.ac.newcastle.enterprisemiddleware.contact;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.guestbooking.GuestBooking;
import uk.ac.newcastle.enterprisemiddleware.guestbooking.GuestBookingRestService;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;

@QuarkusTest
@TestHTTPEndpoint(GuestBookingRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class GuestBookingRestServiceIntegrationTest {

	@Inject
	CustomerService customerService;

	@Inject
	TaxiService taxiService;

	private static Customer customer;

	private static Taxi taxi;

	private static GuestBooking guestBooking;

	private static Booking booking;

	@BeforeAll
	static void setup() throws ParseException {

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date current = df.parse(df.format(c.getTime()));

		c.add(Calendar.DATE, 1);
		Date future = df.parse(df.format(c.getTime()));

		customer = new Customer();
		customer.setName("Test");
		customer.setEmail("test@email.com");
		customer.setPhoneNumber("01234567891");

		taxi = new Taxi();
		taxi.setNoOfSeats(2);
		taxi.setRegistrationNo("ABCD123");

		booking = new Booking();
		booking.setBookingDate(future);

		guestBooking = new GuestBooking();
		guestBooking.setBooking(booking);
		guestBooking.setCustomer(customer);

	}

	@Transactional
	void initializeObjects() throws Exception {

		taxi = taxiService.create(taxi);

		guestBooking.getBooking().setTaxiId(taxi.getId());

	}

	@Test
	@Order(1)
	public void testNoTaxiIdError() throws Exception {

		guestBooking.getBooking().setTaxiId(new Long(20));

		given().contentType(ContentType.JSON).body(guestBooking).when().post().then().statusCode(404);

	}

	@Test
	@Order(2)
	public void testCanCreateBooking() throws Exception {

		initializeObjects();

		Response response = given().contentType(ContentType.JSON).body(guestBooking).when().post().then()
				.statusCode(201).extract().response();

		GuestBooking result = response.body().as(GuestBooking.class);

		assertTrue(booking.getTaxiId().equals(result.getBooking().getTaxiId()), "Taxi Id not equal");
		assertTrue(booking.getBookingDate().equals(result.getBooking().getBookingDate()),
				"Booking Date number not equal");

		// booking.setId(newBooking.getId());

	}

	@Test
	@Order(3)
	public void testAlreadyExistingCustomerError() throws Exception {

		given().contentType(ContentType.JSON).body(guestBooking).when().post().then().statusCode(409);

	}

}
