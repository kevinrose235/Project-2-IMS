package com.revature.beans;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ItemPrimaryKey {

	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
	private int id;
	@PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
	private int storeId;
	
	public ItemPrimaryKey() {
		super();
		this.id=0;
		this.storeId=0;
	}
	public ItemPrimaryKey(int id, int storeId) {
		super();
		this.id=id;
		this.storeId=storeId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + storeId;
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
		ItemPrimaryKey other = (ItemPrimaryKey) obj;
		if (id != other.id)
			return false;
		if (storeId != other.storeId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ItemPrimaryKey [id=" + id + ", storeId=" + storeId + "]";
	}
	
}
