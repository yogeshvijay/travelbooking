package uk.ac.newcastle.enterprisemiddleware.customer;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;

/**
 * @author Yogesh Vijayan
 * @see CustomerValidator
 * @see CustomerRepository
 */
@Dependent
public class CustomerService {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	CustomerValidator validator;

	@Inject
	CustomerRepository custRepo;

	/**
	 * <p>
	 * Returns a List of all persisted {@link Customer} objects, sorted
	 * alphabetically by last name.
	 * <p/>
	 *
	 * @return List of Customer objects
	 */
	List<Customer> fetchAllCustomers() {
		
		return custRepo.fetchAllCustomers();
	}

	/**
	 * <p>
	 * Returns a single Customer object, specified by a Long id.
	 * <p/>
	 *
	 * @param id The id field of the Customer to be returned
	 * @return The Customer with the specified id
	 */
	public Customer findById(Long id) {
		return custRepo.findById(id);
	}

	/**
	 * <p>
	 * Returns a single Customer object, specified by a String email.
	 * </p>
	 *
	 * <p>
	 * If there is more than one Customer with the specified email, only the first
	 * encountered will be returned.
	 * <p/>
	 *
	 * @param email The email field of the Customer to be returned
	 * @return The first Customer with the specified email
	 */
	Customer findByEmail(String email) {
		return custRepo.findByEmail(email);
	}

	/**
	 * <p>
	 * Writes the provided Customer object to the application database.
	 * <p/>
	 *
	 * <p>
	 * Validates the data in the provided Customer object using a
	 * {@link CustomerValidator} object.
	 * <p/>
	 *
	 * @param Customer The Customer object to be written to the database using a
	 *                 {@link CustomerRepository} object
	 * @return The Customer object that has been successfully written to the
	 *         application database
	 * @throws ConstraintViolationException, ValidationException, Exception
	 */
	public Customer create(Customer customer) throws Exception {
		log.info("CustomerService.create() - Creating " + customer.getName());

		// Check to make sure the data fits with the parameters in the Customer model
		// and
		// passes validation.
		validator.validateCustomer(customer);

		// Write the Customer to the database.
		return custRepo.create(customer);
	}

	/**
	 * <p>
	 * Updates an existing Customer object in the application database with the
	 * provided Customer object.
	 * <p/>
	 *
	 * <p>
	 * Validates the data in the provided Customer object using a CustomerValidator
	 * object.
	 * <p/>
	 *
	 * @param Customer The Customer object to be passed as an update to the
	 *                 application database
	 * @return The Customer object that has been successfully updated in the
	 *         application database
	 * @throws ConstraintViolationException, ValidationException, Exception
	 */
	Customer update(Customer customer) throws Exception {
		log.info("customerService.update() - Updating " + customer.getName());

		// Check to make sure the data fits with the parameters in the Customer model
		// and
		// passes validation.
		validator.validateCustomer(customer);

		// Either update the Customer or add it if it can't be found.
		return custRepo.update(customer);
	}

	/**
	 * <p>
	 * Deletes the provided Customer object from the application database if found
	 * there.
	 * <p/>
	 *
	 * @param Customer The Customer object to be removed from the application
	 *                 database
	 * @return The Customer object that has been successfully removed from the
	 *         application database; or null
	 * @throws Exception
	 */
	Customer delete(Customer customer) throws Exception {
		log.info("delete() - Deleting " + customer.toString());

		Customer deletedCustomer = null;

		if (customer.getId() != null) {
			deletedCustomer = custRepo.delete(customer);
		} else {
			log.info("delete() - No ID was found so can't Delete.");
		}

		return deletedCustomer;
	}
}
