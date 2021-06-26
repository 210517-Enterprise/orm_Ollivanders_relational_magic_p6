package com.ollivanders.test;

import com.ollivanders.annotations.Column;
import com.ollivanders.annotations.Id;
import com.ollivanders.model.SQLConstraints;
import com.ollivanders.repos.SQLType;

/**
 * A simple person class that will act as a table.
 * @author castl
 *
 */
public class PersonTest {
	
	@Id(columnName="id", isSerial=true,isUnique=true)
	@Column(columnName="id",columnType = SQLType.SERIAL, columnConstraint = SQLConstraints.PRIMARY_KEY)
	public Integer id;
	@Column(columnName = "name", columnType = SQLType.VARCHAR, columnConstraint = SQLConstraints.NONE)
	public SQLType name;
	@Column(columnName = "age", columnType = SQLType.INTEGER, columnConstraint = SQLConstraints.NONE)
	public Integer age;
	
	public PersonTest(SQLType name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	public PersonTest() {
		super();
	}

	public SQLType getName() {
		return name;
	}

	public void setName(SQLType name) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		PersonTest other = (PersonTest) obj;
		if (age != other.age)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + "]";
	}
	
	
}
