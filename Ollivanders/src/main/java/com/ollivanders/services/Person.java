package com.ollivanders.services;

import com.ollivanders.annotations.Column;
import com.ollivanders.model.SQLConstraints;
import com.ollivanders.repos.SQLType;

/**
 * A simple person class that will act as a table.
 * @author castl
 *
 */
public class Person {
	
	@Column(columnName="id",columnType = SQLType.SERIAL, columnConstraint = SQLConstraints.PRIMARY_KEY)
	public Integer id;
	@Column(columnName = "name", columnType = SQLType.VARCHAR, columnConstraint = SQLConstraints.NONE)
	public String name;
	@Column(columnName = "age", columnType = SQLType.INTEGER, columnConstraint = SQLConstraints.NONE)
	public Integer age;
	@Column(columnName="otherPersonID", columnConstraint = SQLConstraints.FOREIGN_KEY, columnType = SQLType.INTEGER)
	public Integer otherPersonID;
	
	public Person(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	public Person(String name, int age, int otherPersonID) {
		super();
		this.name = name;
		this.age = age;
		this.otherPersonID = otherPersonID;
	}
	
	public Person() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	
	public Integer getOtherPersonID() {
		return otherPersonID;
	}

	public void setOtherPersonID(Integer otherPersonID) {
		this.otherPersonID = otherPersonID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((otherPersonID == null) ? 0 : otherPersonID.hashCode());
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
		Person other = (Person) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (otherPersonID == null) {
			if (other.otherPersonID != null)
				return false;
		} else if (!otherPersonID.equals(other.otherPersonID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", age=" + age + ", otherPersonID=" + otherPersonID + "]";
	}
	
	
}
