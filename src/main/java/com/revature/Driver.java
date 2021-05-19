package com.revature;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableCassandraRepositories
public class Driver {

	public static void main(String[] args) {
		SpringApplication.run(Driver.class, args);	
	}

}
 