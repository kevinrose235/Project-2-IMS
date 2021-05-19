package com.revature.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

import com.revature.utils.ResponseBuilder;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class ControllerExceptionHandling {

	@ExceptionHandler(value = {ServerWebInputException.class}) // this may cause issues later on
	public Mono<ResponseEntity<Object>> handleServerWebInputException(ServerWebInputException e) {
		return ResponseBuilder.bad(e.getMessage());
	}
	
	@ExceptionHandler(value = {InvalidDataException.class})
	public Mono<ResponseEntity<Object>> handleInvalidDataException(InvalidDataException e) {
		return ResponseBuilder.bad(e.getMessage());
	}
	
	@ExceptionHandler(value = {InvalidTokenException.class})
	public Mono<ResponseEntity<Object>> handleTokenException(InvalidTokenException e) {
		return ResponseBuilder.bad(e.getMessage());
	}
	
	@ExceptionHandler(value = {UserNotFound.class})
	public Mono<ResponseEntity<Object>> handleUserNotFoundException(UserNotFound e) {
		return ResponseBuilder.bad(e.getMessage());
	}
	
	@ExceptionHandler(value = {WrongPassword.class})
	public Mono<ResponseEntity<Object>> handleWrongPasswordException(WrongPassword e) {
		return ResponseBuilder.bad(e.getMessage());
	}
	
	@ExceptionHandler(value = {DataAlreadyExistsException.class})
	public Mono<ResponseEntity<Object>> handleDataAlreadyExistsException(DataAlreadyExistsException e) {
		return ResponseBuilder.conflict(e.getMessage());
	}	
	
	@ExceptionHandler(value = {NoStoreFoundException.class, NoContractFoundException.class})
	public Mono<ResponseEntity<Object>> handleDataAlreadyExistsException(RuntimeException e) {
		return ResponseBuilder.notFound(e.getMessage());
	}
	
	@ExceptionHandler(value = {UserAlreadyLoggedIn.class})
	public Mono<ResponseEntity<Object>> handleUserAlreadyLoggedIn(UserAlreadyLoggedIn e) {
		return ResponseBuilder.ok(e.getMessage());
	}
}
