package com.revature.repositories;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.beans.Item;
import com.revature.beans.ItemPrimaryKey;

@Repository
public interface ItemRepository extends ReactiveCassandraRepository<Item, ItemPrimaryKey>{

}
