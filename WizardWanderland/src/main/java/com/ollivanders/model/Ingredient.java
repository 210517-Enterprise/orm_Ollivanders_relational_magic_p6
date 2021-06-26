package com.ollivanders.model;

import com.ollivanders.annotations.Column;
import com.ollivanders.annotations.Entity;
import com.ollivanders.annotations.Id;
import com.ollivanders.repos.SQLType;

@Entity(tableName="ingredient")
public class Ingredient {
	
	@Column(columnName="name_id", columnConstraint = SQLConstraints.PRIMARY_KEY, columnType = SQLType.VARCHAR)
	private String name = "placeholder";
	
	@Column(columnName="type", columnConstraint = SQLConstraints.NONE, columnType = SQLType.VARCHAR)
	private String type;
	
	@Column(columnName="cost", columnConstraint = SQLConstraints.NONE, columnType = SQLType.VARCHAR)
	private int cost = 0;
	
	public Ingredient() {
		super();
	}
	
	public Ingredient(String type) {
		super();
		this.type = type;
	}

	public Ingredient(String type, String name, int cost) {
		super();
		this.name = name;
		this.type = type;
		this.cost = cost;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Ingredient other = (Ingredient) obj;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Ingredient [name=" + name + ", type=" + type + ", cost=" + cost + "]";
	}
	
	
	
	
}
