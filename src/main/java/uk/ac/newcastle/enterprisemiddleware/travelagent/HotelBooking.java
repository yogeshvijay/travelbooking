package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class HotelBooking implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long customerId;

	private Long hotelId;

	private Date bookingDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHotelId() {
		return hotelId;
	}

	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookingDate, customerId, hotelId, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HotelBooking other = (HotelBooking) obj;
		return Objects.equals(bookingDate, other.bookingDate) && Objects.equals(customerId, other.customerId)
				&& Objects.equals(hotelId, other.hotelId) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "HotelBooking [id=" + id + ", customerId=" + customerId + ", hotelId=" + hotelId + ", bookingDate="
				+ bookingDate + "]";
	}

}
