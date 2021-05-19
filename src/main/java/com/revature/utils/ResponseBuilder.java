package com.revature.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public class ResponseBuilder {

	private ResponseBuilder() { /*Empty private constructor*/ }
	
	public static Mono<ResponseEntity<Object>> ok(Object o) {
		return Mono.just(ResponseEntity.ok(o));
	}
	
	public static Mono<ResponseEntity<Object>> created(Object o) {
		return Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(o));
	}
	
	public static Mono<ResponseEntity<Object>> bad(Object o) {
		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(o));
	}
	
	public static Mono<ResponseEntity<Object>> conflict(Object o) {
		return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(o));
	}
	
	public static Mono<ResponseEntity<Object>> unauthorized(Object o) {
		return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(o));
	}
	
	public static Mono<ResponseEntity<Object>> forbidden(Object o) {
		return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(o));
	}
	
	public static Mono<ResponseEntity<Object>> notAcceptable(Object o){
		return Mono.just(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(o));
	}

	public static Mono<ResponseEntity<Object>> notFound(Object o) {
		return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(o));
	}
}
