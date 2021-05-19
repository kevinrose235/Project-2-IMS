package com.revature.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.beans.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

@Service
public class JWTParser {

	@Autowired
	private ObjectMapper mapper;

	public User parser(String jwtStr) throws JsonMappingException, JsonProcessingException {
		Jws<Claims> jws = Jwts.parser().setSigningKey(TextCodec.BASE64.decode(
				System.getenv("SECRET_KEY"))).parseClaimsJws(jwtStr);

		return mapper.readValue(jws.getBody().get("user").toString(), User.class);

	}

	public String makeToken(User user) throws JsonProcessingException {
		String secretKey = System.getenv("SECRET_KEY");//xEiiXQ43NxyXIV7sNiGZ
		String userString = mapper.writeValueAsString(user);
		// Create json web token
		System.out.println(user);
		return Jwts.builder().claim("user", userString).signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(
				secretKey
		)).compact();
	}
	

}
