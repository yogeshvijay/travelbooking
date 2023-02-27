package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@RequestScoped
public class TravelAgentRepository {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	EntityManager em;

	TravelAgent createTravelBooking(TravelAgent travelAgent) {

		em.persist(travelAgent);

		return travelAgent;
	}

	List<TravelAgent> findAll() {

		TypedQuery<TravelAgent> query = em.createNamedQuery(TravelAgent.FIND_ALL, TravelAgent.class);

		return query.getResultList();

	}

	TravelAgent findById(Long id) {
		return em.find(TravelAgent.class, id);
	}
	
	TravelAgent delete(TravelAgent travelAgent) throws Exception {

		if (travelAgent.getId() != null) {

			em.remove(em.merge(travelAgent));

		} else {
			log.info("TravelAgentRepository.delete() - No ID was found so can't Delete.");
		}

		return travelAgent;
	}

}
