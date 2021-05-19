package com.revature.services;

import com.revature.beans.User;

import reactor.core.publisher.Mono;

public interface UserService {

	

	public Mono<User> getUser(int id);


	public Mono<User> updateUser(User u);


	public Mono<User> addUser(User newUser);

}
