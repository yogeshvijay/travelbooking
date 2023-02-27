package uk.ac.newcastle.enterprisemiddleware.travelagent;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

public class TravelAgentStatic {

	public static Customer createTravelAgent() {

		Customer customer = new Customer();
		
		customer.setId(null);
		customer.setEmail("agentyogesh@test.com");
		customer.setName("Agentyogesh");
		customer.setPhoneNumber("01234567891");

		return customer;
	}

}
