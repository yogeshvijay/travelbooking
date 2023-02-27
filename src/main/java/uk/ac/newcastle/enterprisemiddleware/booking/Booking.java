package uk.ac.newcastle.enterprisemiddleware.booking;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.taxi.Taxi;
import uk.ac.newcastle.enterprisemiddleware.travelagent.TravelAgent;

@Entity
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = Booking.FIND_BY_ALL, query = "SELECT b FROM Booking b where b.taxi = :taxi and b.bookingDate = :bookingDate"),
		@NamedQuery(name = Booking.FIND_BY_CUSTID, query = "SELECT b FROM Booking b WHERE b.customer = :customer"),
		@NamedQuery(name = Booking.GET_ALL, query = "SELECT b FROM Booking b") })
@Table(name = "booking")
public class Booking implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_ALL = "Booking.findByAll";
	public static final String FIND_BY_CUSTID = "Booking.findByCustId";
	public static final String GET_ALL = "Booking.getAll";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@JoinColumn(name = "customer_id")
	@ManyToOne()
	private Customer customer;

	@NotNull
	@Transient
	private Long customerId;

	@JsonIgnore
	@JoinColumn(name = "taxi_id")
	@ManyToOne()
	private Taxi taxi;

	@NotNull
	@Transient
	private Long taxiId;

	@NotNull
	@Future(message = "Booking date cannot be in the past")
	@Column(name = "booking_date")
	@Temporal(TemporalType.DATE)
	private Date bookingDate;

	@JsonIgnore
	@OneToOne(mappedBy = "taxiBooking", cascade = CascadeType.REMOVE)
	private TravelAgent travelAgent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getTaxiId() {
		return taxiId;
	}

	public void setTaxiId(Long taxiId) {
		this.taxiId = taxiId;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public TravelAgent getTravelAgent() {
		return travelAgent;
	}

	public void setTravelAgent(TravelAgent travelAgent) {
		this.travelAgent = travelAgent;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Taxi getTaxi() {
		return taxi;
	}

	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookingDate, customer, id, taxi);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Booking other = (Booking) obj;
		return Objects.equals(bookingDate, other.bookingDate) && Objects.equals(customer, other.customer)
				&& Objects.equals(id, other.id) && Objects.equals(taxi, other.taxi);
	}

	@Override
	public String toString() {
		return "Booking [id=" + id + ", customerId=" + customerId + ", taxiId=" + taxiId + ", bookingDate="
				+ bookingDate + "]";
	}

}