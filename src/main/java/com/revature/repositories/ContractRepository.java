package com.revature.repositories;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.beans.Contract;

@Repository
public interface ContractRepository extends ReactiveCassandraRepository<Contract, Integer>{
	
}

