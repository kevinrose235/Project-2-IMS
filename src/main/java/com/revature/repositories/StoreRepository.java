package com.revature.repositories;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

import com.revature.beans.Store;

public interface StoreRepository extends ReactiveCassandraRepository<Store, Integer>{
	

}
