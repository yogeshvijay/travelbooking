package uk.ac.newcastle.enterprisemiddleware.contact;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import uk.ac.newcastle.enterprisemiddleware.guestbooking.GuestBookingRestService;

@QuarkusTest
@TestHTTPEndpoint(GuestBookingRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class TravelAgentRestServiceTesting {

}
