package com.revature.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.aspects.Authenticated;
import com.revature.aspects.CorporateAccess;
import com.revature.beans.PassResetBean;
import com.revature.beans.Store;
import com.revature.beans.User;
import com.revature.beans.User.Role;
import com.revature.exceptions.InvalidDataException;
import com.revature.exceptions.InvalidTokenException;
import com.revature.exceptions.UserAlreadyLoggedIn;
import com.revature.exceptions.UserNotFound;
import com.revature.exceptions.WrongPassword;
import com.revature.services.StoreService;
import com.revature.services.UserService;
import com.revature.utils.JWTParser;
import com.revature.utils.ResponseBuilder;

import reactor.core.publisher.Mono;

@RestController
public class UserController {
	private UserService userService;
	private StoreService storeService;
	private JWTParser tokenService;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public void setStoreService(StoreService storeService) {
		this.storeService = storeService;
	}

	@Autowired
	public void setTokenServicer(JWTParser parser) {
		this.tokenService = parser;
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	// LOGGING IN
	@PostMapping("/login")
	public Mono<ResponseEntity<Object>> login(@CookieValue(value = "token", defaultValue = "") String token, ServerWebExchange exchange, @RequestBody User info) {
		// If user is logged in, send back a 200
		if (userLoggedIn(token)) throw new UserAlreadyLoggedIn("You are already logged in");
		
		// checks if fields are null
		if (info.getId() == 0) throw new InvalidDataException("Must provide a ID");
		if (info.getPassword() == null || info.getPassword().isEmpty()) throw new InvalidDataException("Must provide a Password");
		
		// returns User if everything was ok
		return userService.getUser(info.getId()).switchIfEmpty(Mono.just(new User())).flatMap(u -> {
			if (u.getName() == null) throw new UserNotFound("Could not find a user with id: " + info.getId());

			boolean passwordMatch = passwordEncoder.matches(info.getPassword(), u.getPassword());
			if (!passwordMatch) throw new WrongPassword("Incorrect password");

			setToken(exchange, u);
			
			return ResponseBuilder.ok(u);
		});
	}

	// LOGGING OUT
	@DeleteMapping("/login")
	public Mono<ResponseEntity<Object>> logout(ServerWebExchange exchange) {
		exchange.getResponse().addCookie(ResponseCookie.from("token", "").httpOnly(true).build());
		return ResponseBuilder.ok("Logged out successfully");
	}

	// CHANGING PASSWORDS
	@PutMapping("/login")
	@Authenticated
	public Mono<ResponseEntity<Object>> changePassword(@CookieValue(value = "token", defaultValue = "") String token, ServerWebExchange exchange, @RequestBody PassResetBean passwords) {
		User user = null;
		try {
			user = tokenService.parser(token);
		} catch (JsonProcessingException e) {
			throw new InvalidTokenException("Problem parsing json token, contact your administrator");
		}
		
		String oldPass = passwords.getOldPass();
		String newPass = passwords.getNewPass();
		String confirmPass = passwords.getConfirm();
		
		// input validation
		if (oldPass == null || oldPass.isEmpty()) throw new InvalidDataException("Must provide old password");
		if (newPass == null || newPass.isEmpty()) throw new InvalidDataException("Must provide new password");
		if (confirmPass == null || confirmPass.isEmpty()) throw new InvalidDataException("Must provide comfirm password");
		
		return userService.getUser(user.getId()).flatMap(u -> {
			if (!passwordEncoder.matches(oldPass, u.getPassword())) throw new InvalidDataException("provided old password does not match current password");
			if (!newPass.equals(confirmPass)) throw new InvalidDataException("confirm password does not match provided new password");
			
			u.setPassword(passwordEncoder.encode(newPass));
			return userService.updateUser(u).flatMap(ResponseBuilder::ok);
		});
	}

	@PostMapping("/users")
	@CorporateAccess
	public Mono<ResponseEntity<Object>> register(ServerWebExchange exchange, @RequestBody User newUser) {
		String name = newUser.getName();
		String password = newUser.getPassword();
		Role role = newUser.getRole(); // may need to do a valueOf()
		Integer storeId = newUser.getStoreid();
		
		// null fields check
		if (name == null || name.isEmpty()) throw new InvalidDataException("Must provide a name");
		if (password == null || password.isEmpty()) throw new InvalidDataException("Must provide a password");
		if (role == null) throw new InvalidDataException("Must provide a role");
		if (storeId == null) throw new InvalidDataException("Must provide a store id");
		
		//check to see if the given storeId exists
		storeService.getStoreById(storeId).switchIfEmpty(Mono.just(new Store())).map(s -> {
			if (s.getName() == null || s.getName().isEmpty()) throw new InvalidDataException("Invalid store id");
			return null;
		});
		
		// Encode the password
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		
		return userService.addUser(newUser).flatMap(ResponseBuilder::created);
	}

	private boolean userLoggedIn(String token) {
		return !token.equals("");
	}

	private void setToken(ServerWebExchange exchange, User user) {
		try {
			exchange.getResponse().addCookie(ResponseCookie.from("token", tokenService.makeToken(user)).httpOnly(true).path("/").build());
		} catch (JsonProcessingException e) {
			throw new InvalidTokenException("Problem creating authentication token, contact your administrator");
		}
	}
}
