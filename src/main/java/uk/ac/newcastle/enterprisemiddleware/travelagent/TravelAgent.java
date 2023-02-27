package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.smallrye.common.constraint.NotNull;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

@Entity
@XmlRootElement
@NamedQueries({ @NamedQuery(name = TravelAgent.FIND_ALL, query = "SELECT c FROM TravelAgent c") })
@Table(name = "travel_agent")
public class TravelAgent implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "TravelAgent.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@OneToOne(cascade = { CascadeType.REMOVE })
	@JoinColumn(name = "booking_id")
	private Booking taxiBooking;

	@NotNull
	@Transient
	private HotelBooking hotelBooking;

	@JsonIgnore
	@Column(name = "hotel_booking_id")
	private Long hotelBookingId;

	@NotNull
	@Transient
	private ForeignTaxiBooking foreignTaxiBooking;

	@JsonIgnore
	@Column(name = "taxi_booking_id")
	private Long foreignTaxiBookingId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Booking getTaxiBooking() {
		return taxiBooking;
	}

	public void setTaxiBooking(Booking taxiBooking) {
		this.taxiBooking = taxiBooking;
	}

	public HotelBooking getHotelBooking() {
		return hotelBooking;
	}

	public void setHotelBooking(HotelBooking hotelBooking) {
		this.hotelBooking = hotelBooking;
	}

	public Long getHotelBookingId() {
		return hotelBookingId;
	}

	public void setHotelBookingId(Long hotelBookingId) {
		this.hotelBookingId = hotelBookingId;
	}

	public ForeignTaxiBooking getForeignTaxiBooking() {
		return foreignTaxiBooking;
	}

	public void setForeignTaxiBooking(ForeignTaxiBooking foreignTaxiBooking) {
		this.foreignTaxiBooking = foreignTaxiBooking;
	}

	public Long getForeignTaxiBookingId() {
		return foreignTaxiBookingId;
	}

	public void setForeignTaxiBookingId(Long foreignTaxiBookingId) {
		this.foreignTaxiBookingId = foreignTaxiBookingId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(foreignTaxiBooking, foreignTaxiBookingId, hotelBooking, hotelBookingId, id, taxiBooking);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TravelAgent other = (TravelAgent) obj;
		return Objects.equals(foreignTaxiBooking, other.foreignTaxiBooking)
				&& Objects.equals(foreignTaxiBookingId, other.foreignTaxiBookingId)
				&& Objects.equals(hotelBooking, other.hotelBooking)
				&& Objects.equals(hotelBookingId, other.hotelBookingId) && Objects.equals(id, other.id)
				&& Objects.equals(taxiBooking, other.taxiBooking);
	}

}
