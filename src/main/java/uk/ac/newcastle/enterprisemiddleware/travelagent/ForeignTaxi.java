package uk.ac.newcastle.enterprisemiddleware.travelagent;

import java.io.Serializable;
import java.util.Objects;

public class ForeignTaxi implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String registrationNumber;

	private Integer numOfSeats;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public Integer getNumOfSeats() {
		return numOfSeats;
	}

	public void setNumOfSeats(Integer numOfSeats) {
		this.numOfSeats = numOfSeats;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, numOfSeats, registrationNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForeignTaxi other = (ForeignTaxi) obj;
		return Objects.equals(id, other.id) && Objects.equals(numOfSeats, other.numOfSeats)
				&& Objects.equals(registrationNumber, other.registrationNumber);
	}

}
