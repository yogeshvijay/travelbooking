package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
public class TaxiService {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	TaxiValidator validator;

	@Inject
	TaxiRepository taxiRepo;

	public Taxi create(Taxi taxi) throws Exception {

		log.info("TaxiService.create() - Creating " + taxi.getRegistrationNo() + " " + taxi.getNoOfSeats());

		// Check to make sure the data fits with the parameters in the Customer model
		// and
		// passes validation.
		validator.validateTaxi(taxi);

		// Write the Customer to the database.
		return taxiRepo.create(taxi);
	}

	List<Taxi> findAll() {

		return taxiRepo.findAll();

	}

	Taxi delete(Taxi taxi) throws Exception {
		log.info("delete() - Deleting " + taxi.toString());

		Taxi deletedTaxi = null;

		if (taxi.getId() != null) {
			deletedTaxi = taxiRepo.delete(taxi);
		} else {
			log.info("delete() - No ID was found so can't Delete.");
		}

		return deletedTaxi;
	}

	public Taxi findById(Long id) {
		return taxiRepo.findById(id);
	}

	Taxi update(Taxi taxi) throws Exception {
		log.info("TaxiService.update() - Updating " + taxi.getRegistrationNo());

		// Check to make sure the data fits with the parameters in the Taxi model and
		// passes validation.
		validator.validateTaxi(taxi);

		// Either update the Taxi or add it if it can't be found.
		return taxiRepo.update(taxi);
	}

}
