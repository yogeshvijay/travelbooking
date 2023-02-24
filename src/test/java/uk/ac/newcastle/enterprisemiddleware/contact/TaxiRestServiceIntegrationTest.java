package uk.ac.newcastle.enterprisemiddleware.contact;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiRestService;

@QuarkusTest
@TestHTTPEndpoint(TaxiRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class TaxiRestServiceIntegrationTest {

	private static Taxi taxi;

	@BeforeAll
	static void setup() {
		taxi = new Taxi();
		taxi.setNoOfSeats(2);
		taxi.setRegistrationNo("ABCD123");
	}

	@Test
	@Order(1)
	public void testCanCreateTaxi() {
		given().contentType(ContentType.JSON).body(taxi).when().post().then().statusCode(201);
	}

	@Test
	@Order(2)
	public void testCanGetTaxi() {
		Response response = when().get().then().statusCode(200).extract().response();

		Taxi[] result = response.body().as(Taxi[].class);

		System.out.println(result[0]);

		assertEquals(1, result.length);
		assertTrue(taxi.getNoOfSeats().equals(result[0].getNoOfSeats()), "No of Seats not equal");
		assertTrue(taxi.getRegistrationNo().equals(result[0].getRegistrationNo()), "Registration not equal");
	}

	@Test
	@Order(3)
	public void testTaxiWithId() {

		Response responseTaxi = when().get().then().statusCode(200).extract().response();

		Taxi[] resultTaxi = responseTaxi.body().as(Taxi[].class);

		Response response = when().get("/" + resultTaxi[0].getId()).then().statusCode(200).extract().response();

		Taxi result = response.body().as(Taxi.class);

		assertTrue(taxi.getNoOfSeats().equals(result.getNoOfSeats()), "No of Seats not equal");
		assertTrue(taxi.getRegistrationNo().equals(result.getRegistrationNo()), "Registration not equal");
	}

	@Test
	@Order(4)
	public void testNoTaxiWithId() {
		when().get("20").then().statusCode(404).extract().response();
	}

	@Test
	@Order(5)
	public void testDuplicateRegistrationCausesError() {
		given().contentType(ContentType.JSON).body(taxi).when().post().then().statusCode(409).body("reasons.RegNo",
				containsString("That Registration No is already used, Please enter a unique one"));
	}

	@Test
	@Order(6)
	public void testNoDeleteTaxi() {
		when().delete("0").then().statusCode(404);
	}

	@Test
	@Order(7)
	public void testCanDeleteTaxi() {
		Response response = when().get().then().statusCode(200).extract().response();

		Taxi[] result = response.body().as(Taxi[].class);

		when().delete(result[0].getId().toString()).then().statusCode(204);
	}
	
	@Test
	@Order(8)
	public void testWrongNoOfSeats() {
		taxi = new Taxi();
		taxi.setNoOfSeats(24);
		taxi.setRegistrationNo("ABCD123");
		given().contentType(ContentType.JSON).body(taxi).when().post().then().statusCode(400).body("reasons.noOfSeats",
				containsString("Please enter a number between 2 and 20"));
	}

	@Test
	@Order(9)
	public void testWrongRegNoFormat() {
		taxi = new Taxi();
		taxi.setNoOfSeats(2);
		taxi.setRegistrationNo("ABCDE123");
		given().contentType(ContentType.JSON).body(taxi).when().post().then().statusCode(400);
	}
	
	@Test
	@Order(10)
	public void testWrongRegNoFormatWithSpecialChars() {
		taxi = new Taxi();
		taxi.setNoOfSeats(2);
		taxi.setRegistrationNo("ABC#123");
		given().contentType(ContentType.JSON).body(taxi).when().post().then().statusCode(400).body("reasons.registrationNo",
				containsString("Please alpha-numerical string of 7 characters only"));
	}

}
