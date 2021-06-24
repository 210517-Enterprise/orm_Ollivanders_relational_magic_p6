package com.ollivanders.model;

import com.ollivanders.annotations.Column;
import com.ollivanders.annotations.Entity;
import com.ollivanders.annotations.Id;

@Entity(tableName="ingredient")
public class Ingredient {
	
	@Id(columnName="name_id", isSerial = false, isUnique = true)
	private String name;
	
	@Column(columnName="type")
	private String type;
	
	@Column(columnName="cost")
	private double cost;
	
	public Ingredient() {
		super();
	}

	public Ingredient(String name, String type, double cost) {
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

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Ingredient [name=" + name + ", type=" + type + ", cost=" + cost + "]";
	}
	
	
	
	
}
