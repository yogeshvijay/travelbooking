package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.exception.ConstraintViolationException;

@RequestScoped
public class TaxiRepository {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	EntityManager em;

	Taxi create(Taxi taxi) {

		log.info("TaxiRepository.create() - Creating " + taxi.getRegistrationNo() + " " + taxi.getNoOfSeats());

		// Write the Customer to the database.
		try {
			em.persist(taxi);
		} catch (Exception e) {
			throw new ConstraintViolationException(e.getMessage(), null, null);
		}

		return taxi;
	}

	/**
	 * <p>
	 * Returns a List of all persisted {@link Taxi} objects.
	 * </p>
	 *
	 * @return List of Taxi objects
	 */
	List<Taxi> findAll() {

		TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_ALL, Taxi.class);

		return query.getResultList();

	}

	Taxi findByRegNo(String registrationNo) {
		TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_BY_REGNO, Taxi.class).setParameter("registrationNo",
				registrationNo);
		return query.getSingleResult();
	}

	Taxi delete(Taxi taxi) throws Exception {
		log.info("TaxiRepository.delete() - Deleting " + taxi.getRegistrationNo());

		if (taxi.getId() != null) {

			em.remove(em.merge(taxi));

		} else {
			log.info("TaxiRepository.delete() - No ID was found so can't Delete.");
		}

		return taxi;
	}

	Taxi findById(Long id) {
		return em.find(Taxi.class, id);
	}

	Taxi update(Taxi taxi) throws Exception {

		log.info("TaxiRepository.update() - Updating " + taxi.getRegistrationNo());

		// Either update the Customer or add it if it can't be found.
		em.merge(taxi);

		return taxi;
	}

}
