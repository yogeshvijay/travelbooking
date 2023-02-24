package uk.ac.newcastle.enterprisemiddleware.contact;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
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
import uk.ac.newcastle.enterprisemiddleware.booking.BookingRestService;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;

@QuarkusTest
@TestHTTPEndpoint(BookingRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class BookingRestServiceIntegrationTest {

	@Inject
	CustomerService customerService;

	@Inject
	TaxiService taxiService;

	private static Customer customer;

	private static Taxi taxi;

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

	}

	@Transactional
	void initializeObjects() throws Exception {

		customer = customerService.create(customer);

		taxi = taxiService.create(taxi);

		booking.setCustomerId(customer.getId());
		booking.setTaxiId(taxi.getId());

	}

	@Test
	@Order(1)
	public void testCanCreateBooking() throws Exception {

		initializeObjects();

		Response response = given().contentType(ContentType.JSON).body(booking).when().post().then().statusCode(201)
				.extract().response();

		Booking newBooking = response.body().as(Booking.class);

		booking.setId(newBooking.getId());

	}

	@Test
	@Order(2)
	public void testRetrieveBookings() throws Exception {

		Response response = when().get().then().statusCode(200).extract().response();

		Booking[] result = response.body().as(Booking[].class);

		assertTrue(booking.getCustomerId().equals(result[0].getCustomerId()), "Customer Id not equal");
		assertTrue(booking.getTaxiId().equals(result[0].getTaxiId()), "Taxi Id not equal");
		assertTrue(booking.getBookingDate().equals(result[0].getBookingDate()), "Booking Date number not equal");

	}

	@Test
	@Order(3)
	public void testBookingWithId() {

		Response response = when().get(booking.getId().toString()).then().statusCode(200).extract().response();

		Booking result = response.body().as(Booking.class);

		assertTrue(booking.getCustomerId().equals(result.getCustomerId()), "Customer Id not equal");
		assertTrue(booking.getTaxiId().equals(result.getTaxiId()), "Taxi Id not equal");
		assertTrue(booking.getBookingDate().equals(result.getBookingDate()), "Booking Date number not equal");
	}

	@Test
	@Order(5)
	public void testGetBookingWithCustomer() {

		Response response = given().queryParam("customerId", customer.getId()).get().then().statusCode(200).extract()
				.response();

		Booking[] result = response.body().as(Booking[].class);

		assertTrue(booking.getCustomerId().equals(result[0].getCustomerId()), "Customer Id not equal");
		assertTrue(booking.getTaxiId().equals(result[0].getTaxiId()), "Taxi Id not equal");
		assertTrue(booking.getBookingDate().equals(result[0].getBookingDate()), "Booking Date number not equal");

	}

	@Test
	@Order(6)
	public void testGetBookingWithInvalidId() {
		when().get("10").then().statusCode(404);
	}

	@Test
	@Order(7)
	public void testDuplicateBookingError() {
		given().contentType(ContentType.JSON).body(booking).when().post().then().statusCode(409);
	}

	@Test
	@Order(8)
	public void testDeleteBookingError() {
		when().delete("0").then().statusCode(404);
	}

	@Test
	@Order(9)
	public void testDeleteBooking() {
		when().delete(booking.getId().toString()).then().statusCode(204);
	}

}
