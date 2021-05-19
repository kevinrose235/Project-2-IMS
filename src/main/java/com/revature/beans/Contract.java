package com.revature.beans;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Contract{
	
	@PrimaryKey private int itemId;
	@Column private String supplier;
	@Column private double itemCost;
	@Column private double itemMSRP;
	@Column private double minOrderCost;
	@Column private int quantity;
	@Column private double shippingCost;
	
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public double getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}

	public Contract() {
		super();
	}
	
	public String getSupplier() {
		return supplier;
	}
	
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	public double getItemCost() {
		return itemCost;
	}
	
	public void setItemCost(double itemCost) {
		this.itemCost = itemCost;
	}
	
	public double getItemMSRP() {
		return itemMSRP;
	}
	
	public void setItemMSRP(double itemMSRP) {
		this.itemMSRP = itemMSRP;
	}
	
	public double getMinOrderCost() {
		return minOrderCost;
	}
	
	public void setMinOrderCost(double minOrderCost) {
		this.minOrderCost = minOrderCost;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(itemCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + itemId;
		temp = Double.doubleToLongBits(itemMSRP);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minOrderCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + quantity;
		temp = Double.doubleToLongBits(shippingCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((supplier == null) ? 0 : supplier.hashCode());
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
		Contract other = (Contract) obj;
		if (Double.doubleToLongBits(itemCost) != Double.doubleToLongBits(other.itemCost))
			return false;
		if (itemId != other.itemId)
			return false;
		if (Double.doubleToLongBits(itemMSRP) != Double.doubleToLongBits(other.itemMSRP))
			return false;
		if (Double.doubleToLongBits(minOrderCost) != Double.doubleToLongBits(other.minOrderCost))
			return false;
		if (quantity != other.quantity)
			return false;
		if (Double.doubleToLongBits(shippingCost) != Double.doubleToLongBits(other.shippingCost))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contract [itemId=" + itemId + ", supplier=" + supplier + ", itemCost=" + itemCost + ", itemMSRP="
				+ itemMSRP + ", minOrderCost=" + minOrderCost + ", quantity=" + quantity + ", shippingCost="
				+ shippingCost + "]";
	}
}
