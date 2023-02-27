package uk.ac.newcastle.enterprisemiddleware.customer;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;

/**
 * <p>
 * This is a Repository class and connects the Service/Control layer (see
 * {@link CustomerService} with the Domain/Entity Object (see {@link Customer}).
 * <p/>
 *
 * <p>
 * There are no access modifiers on the methods making them 'package' scope.
 * They should only be accessed by a Service/Control object.
 * <p/>
 *
 * @author Joshua Wilson
 * @see Customer
 * @see javax.persistence.EntityManager
 */
@RequestScoped
public class CustomerRepository {

	@Inject
	@Named("logger")
	Logger log;

	@Inject
	EntityManager em;

	/**
	 * <p>
	 * Returns a List of all persisted {@link Customer} objects
	 * </p>
	 *
	 * @return List of Customer objects
	 */
	List<Customer> fetchAllCustomers() {
		
		TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
		return query.getResultList();
	}

	/**
	 * <p>
	 * Returns a single Customer object, specified by a Long id.
	 * <p/>
	 *
	 * @param id The id field of the Customer to be returned
	 * @return The Customer with the specified id
	 */
	Customer findById(Long id) {
		return em.find(Customer.class, id);
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
		TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email",
				email);
		return query.getSingleResult();
	}

	/**
	 * <p>
	 * Persists the provided Customer object to the application database using the
	 * EntityManager.
	 * </p>
	 * 
	 * <p>
	 * persist(Object) will set the @GeneratedValue @Id for an object.
	 * </p>
	 *
	 * @param Customer The Customer object to be persisted
	 * @return The Customer object that has been persisted
	 * @throws ConstraintViolationException, ValidationException, Exception
	 */
	Customer create(Customer customer) throws Exception {

		log.info("CustomerRepository.create() - Creating " + customer.getName());

		// Write the Customer to the database.
		em.persist(customer);

		return customer;
	}

	/**
	 * <p>
	 * Updates an existing Customer object in the application database with the
	 * provided Customer object.
	 * </p>
	 *
	 * <p>
	 * {@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a
	 * new instance of your entity, copies the state from the supplied entity, and
	 * makes the new copy managed. The instance you pass in will not be managed (any
	 * changes you make will not be part of the transaction - unless you call merge
	 * again).
	 * </p>
	 *
	 * <p>
	 * merge(Object) however must have an object with the @Id already generated.
	 * </p>
	 *
	 * @param Customer The Customer object to be merged with an existing Customer
	 * @return The Customer that has been merged
	 * @throws ConstraintViolationException, ValidationException, Exception
	 */
	Customer update(Customer customer) throws Exception {
		log.info("CustomerRepository.update() - Updating " + customer.getName());

		// Either update the Customer or add it if it can't be found.
		em.merge(customer);

		return customer;
	}

	/**
	 * <p>
	 * Deletes the provided Customer object from the application database if found
	 * there
	 * </p>
	 *
	 * @param Customer The Customer object to be removed from the application
	 *                 database
	 * @return The Customer object that has been successfully removed from the
	 *         application database; or null
	 * @throws Exception
	 */
	Customer delete(Customer customer) throws Exception {
		log.info("CustomerRepository.delete() - Deleting " + customer.getName());

		if (customer.getId() != null) {

			em.remove(em.merge(customer));

		} else {
			log.info("CustomerRepository.delete() - No ID was found so can't Delete.");
		}

		return customer;
	}

}
