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
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRestService;

@QuarkusTest
@TestHTTPEndpoint(CustomerRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class CustomerRestServiceIntegrationTest {

	private static Customer customer;

	@BeforeAll
	static void setup() {
		customer = new Customer();
		customer.setName("Test");
		customer.setEmail("test@email.com");
		customer.setPhoneNumber("01234567891");
	}

	@Test
	@Order(1)
	public void testCanCreateCustomer() {
		given().contentType(ContentType.JSON).body(customer).when().post().then().statusCode(201);
	}

	@Test
	@Order(2)
	public void testCanGetCustomers() {
		Response response = when().get().then().statusCode(200).extract().response();

		Customer[] result = response.body().as(Customer[].class);

		System.out.println(result[0]);

		assertEquals(1, result.length);
		assertTrue(customer.getName().equals(result[0].getName()), "Name not equal");
		assertTrue(customer.getEmail().equals(result[0].getEmail()), "Email not equal");
		assertTrue(customer.getPhoneNumber().equals(result[0].getPhoneNumber()), "Phone number not equal");
	}

	@Test
	@Order(3)
	public void testCustomerWithId() {

		Response responseCust = when().get().then().statusCode(200).extract().response();

		Customer[] resultCust = responseCust.body().as(Customer[].class);

		Response response = when().get("/" + resultCust[0].getId()).then().statusCode(200).extract().response();

		Customer result = response.body().as(Customer.class);

		assertTrue(customer.getName().equals(result.getName()), "Name not equal");
		assertTrue(customer.getEmail().equals(result.getEmail()), "Email not equal");
		assertTrue(customer.getPhoneNumber().equals(result.getPhoneNumber()), "Phone number not equal");
	}

	@Test
	@Order(4)
	public void testNoCustomerWithId() {
		when().get("20").then().statusCode(404).extract().response();
	}

	@Test
	@Order(5)
	public void testCanGetCustomersByEmail() {
		Response response = when().get("/email/" + customer.getEmail()).then().statusCode(200).extract().response();

		Customer result = response.body().as(Customer.class);

		System.out.println(result);

		assertTrue(customer.getName().equals(result.getName()), "Name not equal");
		assertTrue(customer.getEmail().equals(result.getEmail()), "Email not equal");
		assertTrue(customer.getPhoneNumber().equals(result.getPhoneNumber()), "Phone number not equal");
	}

	@Test
	@Order(6)
	public void testNoCustomerWithEmail() {
		when().get("/email/" + "random@test.com").then().statusCode(404).extract().response();

	}

	@Test
	@Order(7)
	public void testDuplicateEmailCausesError() {
		given().contentType(ContentType.JSON).body(customer).when().post().then().statusCode(409).body("reasons.email",
				containsString("email is already used"));
	}

	@Test
	@Order(8)
	public void testUpdateCustomer() {

		customer.setName("YogeshV");

		Response response = when().get().then().statusCode(200).extract().response();

		Customer[] result = response.body().as(Customer[].class);

		given().contentType(ContentType.JSON).body(customer).when().put(result[0].getId().toString()).then()
				.statusCode(400);
	}

	@Test
	@Order(9)
	public void testNoDeleteCustomer() {
		when().delete("0").then().statusCode(404);
	}

	@Test
	@Order(10)
	public void testCanDeleteCustomer() {
		Response response = when().get().then().statusCode(200).extract().response();

		Customer[] result = response.body().as(Customer[].class);

		when().delete(result[0].getId().toString()).then().statusCode(204);
	}

	@Test
	@Order(11)
	public void testWrongPhoneNumber() {
		customer = new Customer();
		customer.setName("Test");
		customer.setEmail("test@email.com");
		customer.setPhoneNumber("012345678913");
		given().contentType(ContentType.JSON).body(customer).when().post().then().statusCode(400);
	}

	@Test
	@Order(12)
	public void testWrongEmail() {
		customer = new Customer();
		customer.setName("Test");
		customer.setEmail("testemail.com");
		customer.setPhoneNumber("01234567891");
		given().contentType(ContentType.JSON).body(customer).when().post().then().statusCode(400);
	}

}
