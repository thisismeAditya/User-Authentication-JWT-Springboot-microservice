package com.example.demo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final Environment environment;
	
	private final UserService userService;
	
	private final BCryptPasswordEncoder passwordEncoder;
	
	public WebSecurityConfig(Environment environment, UserService userService, BCryptPasswordEncoder passwordEncoder) {
		this.environment = environment;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		
		//http.authorizeRequests().antMatchers("/user/").permitAll(); without IP walla rule
		
		http.authorizeRequests().anyRequest().hasIpAddress(environment.getProperty("gateway.ip"))
			.and()
			.addFilter(getAuthenticationFilter());
		
		http.headers().frameOptions().disable();
	}

	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment, authenticationManager());
		//authenticationFilter.setAuthenticationManager(authenticationManager());
		
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return authenticationFilter;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
	}

}
