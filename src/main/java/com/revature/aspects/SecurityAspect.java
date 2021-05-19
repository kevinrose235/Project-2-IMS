package com.revature.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.revature.beans.User;
import com.revature.exceptions.InvalidTokenException;
import com.revature.exceptions.NoTokenException;
import com.revature.exceptions.NoWebExchangeException;
import com.revature.utils.JWTParser;
import com.revature.utils.ResponseBuilder;

import reactor.core.publisher.Mono;

@Service
@Aspect
public class SecurityAspect {
	/*
	 * Use @Authenticated to check if a user is logged in
	 *     IF not authenticated:
	 *         Sends a 401 to client
	 *         Will not proceed with method call
	 *     IF authenticated
	 *         Will proceed with method call
	 *         
	 * Use @CorporateAccess to check if a user's role is Role.CORPORATE
	 *     IF not authenticated:
	 *         Sends a 403 to client
	 *         Will not proceed with method call
	 *     IF authenticated
	 *         Will proceed with method call
	 */
	
	private JWTParser tokenService;

	@Autowired
	public void setTokenServicer(JWTParser parser) {
		this.tokenService = parser;
	}

	// Only lets a corporate user run annotated methods
	@SuppressWarnings("unchecked")
	@Around("userAuthorized()")
	public Mono<ResponseEntity<Object>> checkIfUserIsCorporate(ProceedingJoinPoint pjp) throws Throwable {
		// variables for ease of tracing the code
		Object[] methodArgs = pjp.getArgs();
		ServerWebExchange webExchange = null;
		User token = null;
		
		try {
			webExchange = getWebExchange(methodArgs);
			token = getUserFromToken(webExchange);
		} catch(NoWebExchangeException e) {
			return null;
		} catch (NoTokenException | InvalidTokenException e) {
			return ResponseBuilder.bad("You are not logged in");
		} catch (Exception e ) { 
			return null;
		}
		
		if (token.getRole() != User.Role.CORPORATE) {
			return ResponseBuilder.forbidden("You are not a corporate user"); // user is not authorized
		} else {
			return (Mono<ResponseEntity<Object>>) pjp.proceed();
		}
	}

	// User must be logged in to run annotated methods
	@SuppressWarnings("unchecked")
	@Around("userAuthenticated()")
	public Object checkIfUserIsLoggedIn(ProceedingJoinPoint pjp) throws Throwable {
		// variables for ease of tracing the code
		Object[] methodArgs = pjp.getArgs();
		ServerWebExchange webExchange = null;
		
		try {
			webExchange = getWebExchange(methodArgs);
			getUserFromToken(webExchange);
		} catch(NoWebExchangeException e) {
			return null;
		} catch (NoTokenException | InvalidTokenException e) {
			return ResponseBuilder.bad("You are not logged in");
		} catch (Exception e) {
			return null; // Something really bad went wrong.
		}
		
		return pjp.proceed();
	}
	
	// returns the ServerWebExchange from the methods arguments
	// returns null if one is not found
	private ServerWebExchange getWebExchange(Object[] args) throws NoWebExchangeException {
		for (Object arg : args) {
			if (arg instanceof ServerWebExchange) {
				return (ServerWebExchange) arg;
			}
		}
		throw new NoWebExchangeException("No ServerWebExchange found");
	}
	
	// parses the token and returns the User object within it.
	private User getUserFromToken(ServerWebExchange exchange) throws NoTokenException {
		HttpCookie cookie = exchange.getRequest().getCookies().getFirst("token");
		if (cookie == null) throw new NoTokenException("No cookie found"); // cookie wasn't found
		
		String token = cookie.getValue();
		if (token.isEmpty()) throw new NoTokenException("No token found"); // token wasn't found
		
		try {
			return tokenService.parser(token); // return the User object in token
		} catch (JsonProcessingException e) {
			throw new InvalidTokenException("Invalid Token");
		}
	}

	@Pointcut("@annotation(com.revature.aspects.CorporateAccess)")
	private void userAuthorized() {
		/* Empty Method For Hook */ }

	@Pointcut("@annotation(com.revature.aspects.Authenticated)")
	private void userAuthenticated() {
		/* Empty Method For Hook */ }
}
