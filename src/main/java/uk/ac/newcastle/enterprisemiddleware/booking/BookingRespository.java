package uk.ac.newcastle.enterprisemiddleware.booking;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;

@RequestScoped
public class BookingRespository {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	EntityManager em;

	Booking create(Booking booking) throws Exception {

		// Write the Booking to the database.
		em.persist(booking);

		return booking;
	}

	/**
	 * <p>
	 * Returns a list of Booking, specified by a String email.
	 * </p>
	 *
	 *
	 * @param custId The customerId field of the Booking to be returned
	 * @return The list of Bookings made by Customer
	 */
	List<Booking> findByCustId(Customer customer) {

		TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_CUSTID, Booking.class)
				.setParameter("customer", customer);

		return query.getResultList();

	}
	
	List<Booking> findByCustId() {

		TypedQuery<Booking> query = em.createNamedQuery(Booking.GET_ALL, Booking.class);

		return query.getResultList();

	}

	Booking findByAll(Taxi taxi, Date bookingDate) {

		TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_ALL, Booking.class)
				.setParameter("taxi", taxi).setParameter("bookingDate", bookingDate);

		return query.getSingleResult();
	}

	Booking findById(Long id) {

		return em.find(Booking.class, id);

	}

	Booking delete(Booking booking) throws Exception {

		if (booking.getId() != null) {

			em.remove(em.merge(booking));

		} else {
			log.info("BookingRespository.delete() - No ID was found so can't Delete.");
		}

		return booking;
	}

}
