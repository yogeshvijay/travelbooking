package uk.ac.newcastle.enterprisemiddleware.guestbooking;

import java.io.Serializable;
import java.util.Objects;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;

public class GuestBooking implements Serializable {

	private static final long serialVersionUID = 1L;

	private Customer customer;

	private Booking booking;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	@Override
	public int hashCode() {
		return Objects.hash(booking, customer);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuestBooking other = (GuestBooking) obj;
		return Objects.equals(booking, other.booking) && Objects.equals(customer, other.customer);
	}

}
