package com.revature.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

import com.revature.interfaces.FetchableID;

public class IdGenerator {
	
	private static Logger logger = LogManager.getLogger(IdGenerator.class);
	
	// This is so this Util class is never
	// instantiated  as an object
	private IdGenerator() { 
		super();
	}
	
	public static int generate(ReactiveCassandraRepository<? extends FetchableID, ?> repo) {
		Callable<Integer> generate = () -> {
			int highestId = 0;
			Iterable<? extends FetchableID> iterable = repo.findAll().toIterable();
			
			for(FetchableID entity : iterable) {
				if(entity.getId() > highestId) {
					highestId = entity.getId();
				}
			}
			return highestId + 1;
		};
		
		Future<Integer> future = Executors.newSingleThreadExecutor().submit(generate);
		int id = 0;
		try {
			id = future.get().intValue();
		}catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}catch(Exception e) {
			logger.warn(e.getMessage());
			for(StackTraceElement el : e.getStackTrace()) {
				logger.debug(el);
			}
		}
		return id;
	}

}
