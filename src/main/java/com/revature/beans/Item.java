package com.revature.beans;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.revature.interfaces.FetchableID;

@Table
public class Item implements FetchableID{

	@PrimaryKey private ItemPrimaryKey primaryKey;
	@Column private String name;
	@Column private double weight;
	@Column private double cost;
	@Column private int popularity;
	@Column private int overstockThreshold;
	@Column private int lowThreshold;
	@Column private int quantity;
	
	public Item() {
		super();
		this.primaryKey=null;
	}

	public ItemPrimaryKey getPrimaryKey() {
		return this.primaryKey;
	}
	
	public void setPrimaryKey(ItemPrimaryKey key) {
		this.primaryKey = key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String itemName) {
		this.name = itemName;
	}


	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public int getOverstockThreshold() {
		return overstockThreshold;
	}

	public void setOverstockThreshold(int overstockThreshold) {
		this.overstockThreshold = overstockThreshold;
	}

	public int getLowThreshold() {
		return lowThreshold;
	}

	public void setLowThreshold(int lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quanttity) {
		this.quantity = quanttity;
	}
	
	@Override //For FetchableID
	public int getId() {
		return this.getPrimaryKey().getId();
	}
	public int getStoreId() {
		return this.getPrimaryKey().getStoreId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
		result = prime * result + lowThreshold;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + overstockThreshold;
		result = prime * result + popularity;
		result = prime * result + quantity;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Item other = (Item) obj;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		if (primaryKey == null) {
			if (other.primaryKey != null)
				return false;
		} else if (!primaryKey.equals(other.primaryKey))
			return false;
		if (lowThreshold != other.lowThreshold)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (overstockThreshold != other.overstockThreshold)
			return false;
		if (popularity != other.popularity)
			return false;
		if (quantity != other.quantity)
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Item [key=" + primaryKey + ", name=" + name + ", weight=" + weight + ", cost=" + cost + ", popularity="
				+ popularity + ", overstockThreshold=" + overstockThreshold + ", lowThreshold=" + lowThreshold
				+ ", quantity=" + quantity + "]";
	}
	
}
