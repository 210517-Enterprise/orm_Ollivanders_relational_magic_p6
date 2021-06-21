package com.ollivanders.model;

public class Wand {
	
	public int id;
	public String wood;
	public String core;
	public double cost;
	public int wizard_id;
	public Wand() {
		super();
	}
	public Wand(int id, String wood, String core, double cost, int wizard_id) {
		super();
		this.id = id;
		this.wood = wood;
		this.core = core;
		this.cost = cost;
		this.wizard_id = wizard_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWood() {
		return wood;
	}
	public void setWood(String wood) {
		this.wood = wood;
	}
	public String getCore() {
		return core;
	}
	public void setCore(String core) {
		this.core = core;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public int getWizard_id() {
		return wizard_id;
	}
	public void setWizard_id(int wizard_id) {
		this.wizard_id = wizard_id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((core == null) ? 0 : core.hashCode());
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + id;
		result = prime * result + wizard_id;
		result = prime * result + ((wood == null) ? 0 : wood.hashCode());
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
		Wand other = (Wand) obj;
		if (core == null) {
			if (other.core != null)
				return false;
		} else if (!core.equals(other.core))
			return false;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		if (id != other.id)
			return false;
		if (wizard_id != other.wizard_id)
			return false;
		if (wood == null) {
			if (other.wood != null)
				return false;
		} else if (!wood.equals(other.wood))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Wand [id=" + id + ", wood=" + wood + ", core=" + core + ", cost=" + cost + ", wizard_id=" + wizard_id
				+ "]";
	}
	
	
	
}
