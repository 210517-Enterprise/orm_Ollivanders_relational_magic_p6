package com.ollivanders.model;

import java.sql.Date;

import com.ollivanders.annotations.Column;
import com.ollivanders.annotations.Entity;
import com.ollivanders.annotations.Id;
import com.ollivanders.repos.SQLType;

@Entity(tableName="wizard")
public class Wizard {
	
	@Column(columnName="id", columnConstraint = SQLConstraints.PRIMARY_KEY, columnType = SQLType.SERIAL)
	private int id = -1;
	
	@Column(columnName="firstName", columnConstraint = SQLConstraints.NONE, columnType = SQLType.VARCHAR)
	private String firstName;
	
	@Column(columnName="lastName", columnConstraint = SQLConstraints.NONE, columnType = SQLType.VARCHAR)
	private String lastName;
	
	@Column(columnName="birthdate", columnConstraint = SQLConstraints.NONE, columnType = SQLType.DATE)
	private Date birthdate;
	
	public Wizard() {
		super();
	}
	public Wizard(int id, String firstName, String lastName, Date birthdate) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}
	public Wizard(String firstName, String lastName, Date birthdate) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Date getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birthdate == null) ? 0 : birthdate.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wizard other = (Wizard) obj;
		if (birthdate == null) {
			if (other.birthdate != null)
				return false;
		} else if (!birthdate.equals(other.birthdate))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Wizard [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", birthdate=" + birthdate
				+ "]";
	}
	
	
	

}
