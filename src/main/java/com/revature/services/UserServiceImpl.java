package com.revature.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.beans.User;
import com.revature.repositories.UserRepository;
import com.revature.utils.IdGenerator;

import reactor.core.publisher.Mono;
@Service
public class UserServiceImpl implements UserService{

	private UserRepository userRepo;
	@Autowired
	public void setUserRepo(UserRepository userRepo) {
		this.userRepo=userRepo;
	}

	@Override
	public Mono<User> getUser(int id) {
		return userRepo.findById(id);
	}

	@Override
	public Mono<User> updateUser(User u) {
		return userRepo.save(u);
		
	}

	@Override
	public Mono<User> addUser(User newUser) {
		newUser.setId(IdGenerator.generate(userRepo));
		return userRepo.save(newUser);
		
	}

}
