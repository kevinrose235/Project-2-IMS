package com.revature.beans;

public class Order {

	private int itemId;
	private int storeId;
	private int quantity;
	private double price;
	private String supplier;
	private boolean fromDistributor;
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Order() {
		super();
	}
	public Order( int itemId, int storeId, int quantity, double price, String supplier, boolean fromDistributor) {
		this.itemId=itemId;
		this.price=price;
		this.storeId=storeId;
		this.quantity=quantity;
		this.supplier=supplier;
		this.fromDistributor=fromDistributor;
	}

	public boolean getFromDistributor() {
		return fromDistributor;
	}

	public void setFromDistributor(boolean fromDistributor) {
		this.fromDistributor = fromDistributor;
	}

	@Override
	public String toString() {
		return "Order [itemId=" + itemId + ", storeId=" + storeId + ", quantity=" + quantity + ", price=" + price
				+ ", supplier=" + supplier + ", fromDistributor=" + fromDistributor + "]";
	}
	
	

}
