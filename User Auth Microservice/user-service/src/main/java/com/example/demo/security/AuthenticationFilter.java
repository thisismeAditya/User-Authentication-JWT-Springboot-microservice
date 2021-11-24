package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.DTO.UserDto;
import com.example.demo.model.UserLoginRequestModel;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private UserService userService;
	
	private Environment environment;
	
	public AuthenticationFilter(UserService userService, Environment environment, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.environment = environment;
		super.setAuthenticationManager(authenticationManager);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		try {
			
			UserLoginRequestModel cred = new ObjectMapper()
					.readValue(request.getInputStream(), UserLoginRequestModel.class);
			
			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(
							cred.getEmail(),
							cred.getPassword(),
							new ArrayList<>()
						)
				);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		String email = ((User) authResult.getPrincipal()).getUsername();
		
		UserDto dto = userService.getUserDetailsByEmail(email);
		
		String token = Jwts.builder()
			.setSubject(dto.getUserId())
			.setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_date"))))
			.signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
			.compact();
		
		response.addHeader("Token", token);
		response.addHeader("Public UserID", dto.getUserId());
		
	}

}
