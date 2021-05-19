package com.revature.repositories;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.beans.User;
@Repository
public interface UserRepository extends ReactiveCassandraRepository<User,Integer>{



}
