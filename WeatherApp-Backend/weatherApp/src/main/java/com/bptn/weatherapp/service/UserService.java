package com.bptn.weatherapp.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bptn.weatherapp.exception.domain.EmailExistException;
import com.bptn.weatherapp.exception.domain.EmailNotVerifiedException;
import com.bptn.weatherapp.exception.domain.UserNotFoundException;
import com.bptn.weatherapp.exception.domain.UsernameExistException;
import com.bptn.weatherapp.jpa.User;
import com.bptn.weatherapp.provider.ResourceProvider;
import com.bptn.weatherapp.repository.UserRepository;
import com.bptn.weatherapp.security.JwtService;

@Service
public class UserService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EmailService emailService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtService jwtService;

	@Autowired
	ResourceProvider provider;

	private void validateUsernameAndEmail(String username, String emailId) {

		// Check if Username exists
		this.userRepository.findByUsername(username).ifPresent(x -> {
			throw new UsernameExistException(String.format("Username already exists, %s", x.getUsername()));
		});

		// Check if email address exists
		this.userRepository.findByEmailId(emailId).ifPresent(x -> {
			throw new EmailExistException(String.format("Email already exists, %s", x.getEmailId()));
		});
	}

	public User signup(User user) {

		// Set username and email values to lowercase characters
		user.setUsername(user.getUsername().toLowerCase());
		user.setEmailId(user.getEmailId().toLowerCase());

		// Validate if username or email exist
		this.validateUsernameAndEmail(user.getUsername(), user.getEmailId());

		// Set emailVerified to a default value, false
		user.setEmailVerified(false);

		// Encode password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// Set createdOn value to the current DateTime
		user.setCreatedOn(Timestamp.from(Instant.now()));

		// Save to the database
		this.userRepository.save(user);

		// Send an email to verify the emailId
		this.emailService.sendVerificationEmail(user);

		return user;

	}

	public void verifyEmail() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		user.setEmailVerified(true);

		this.userRepository.save(user);
	}

	private static User isEmailVerified(User user) {

		if (user.getEmailVerified().equals(false)) {
			throw new EmailNotVerifiedException(String.format("Email requires verification, %s", user.getEmailId()));
		}

		return user;
	}

	private Authentication authenticate(String username, String password) {
		return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

	public User authenticate(User user) {

		this.authenticate(user.getUsername(), user.getPassword());

		return this.userRepository.findByUsername(user.getUsername()).map(UserService::isEmailVerified).get();
	}

	public HttpHeaders generateJwtHeader(String username) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, this.jwtService.generateJwtToken(username, this.provider.getJwtExpiration()));

		return headers;
	}

	public void resetPassword(String password) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		user.setPassword(this.passwordEncoder.encode(password));

		this.userRepository.save(user);

	}

	public void sendResetPasswordEmail(String emailId) {

		Optional<User> op = this.userRepository.findByEmailId(emailId);

		if (op.isPresent()) {
			this.emailService.sendResetPasswordEmail(op.get());
		} else {
			logger.debug("Email doesn't exist, {}", emailId);
		}
	}
}
