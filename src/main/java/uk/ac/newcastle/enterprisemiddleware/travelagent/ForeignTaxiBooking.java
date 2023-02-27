package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ForeignTaxiBooking implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Long customerId;

	private Long taxiId;

	private Date bookingDate;

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

	@Override
	public int hashCode() {
		return Objects.hash(bookingDate, customerId, id, taxiId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForeignTaxiBooking other = (ForeignTaxiBooking) obj;
		return Objects.equals(bookingDate, other.bookingDate) && Objects.equals(customerId, other.customerId)
				&& Objects.equals(id, other.id) && Objects.equals(taxiId, other.taxiId);
	}

	@Override
	public String toString() {
		return "ForeignTaxiBooking [id=" + id + ", customerId=" + customerId + ", taxiId=" + taxiId + ", bookingDate="
				+ bookingDate + "]";
	}

}
