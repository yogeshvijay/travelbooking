package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

/**
 * <p>
 * This is a the Domain object. The Contact class represents how contact
 * resources are represented in the application database.
 * </p>
 *
 * <p>
 * The class also specifies how a contacts are retrieved from the database
 * (with @NamedQueries), and acceptable values for Contact fields
 * (with @NotNull, @Pattern etc...)
 * <p/>
 *
 * @author Yogesh Vijayan
 */
/*
 * The @NamedQueries included here are for searching against the table that
 * reflects this object. This is the most efficient form of query in JPA though
 * is it more error prone due to the syntax being in a String. This makes it
 * harder to debug.
 */
@Entity
@XmlRootElement
@NamedQueries({ @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT c FROM Taxi c"),
		@NamedQuery(name = Taxi.FIND_BY_REGNO, query = "SELECT c FROM Taxi c WHERE c.registrationNo = :registrationNo") })
@Table(name = "taxi", uniqueConstraints = @UniqueConstraint(columnNames = "registrationNo"))
public class Taxi implements Serializable {

	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "Taxi.findAll";
	public static final String FIND_BY_REGNO = "Taxi.findByRegNo";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 1, max = 7)
	@Pattern(regexp = "^[a-zA-Z0-9]{7}$", message = "Please alpha-numerical string of 7 characters only")
	@Column(name = "registrationNo")
	private String registrationNo;

	@NotNull
	@Range(min = 2, max = 20, message = "Please enter a number between 2 and 20")
	@Column(name = "noOfSeats")
	private Integer noOfSeats;

	@JsonIgnore
	@OneToMany(mappedBy = "taxi", cascade = CascadeType.REMOVE)
	private List<Booking> booking;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public Integer getNoOfSeats() {
		return noOfSeats;
	}

	public void setNoOfSeats(Integer noOfSeats) {
		this.noOfSeats = noOfSeats;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(noOfSeats, registrationNo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Taxi other = (Taxi) obj;
		return Objects.equals(noOfSeats, other.noOfSeats) && Objects.equals(registrationNo, other.registrationNo);
	}

}
