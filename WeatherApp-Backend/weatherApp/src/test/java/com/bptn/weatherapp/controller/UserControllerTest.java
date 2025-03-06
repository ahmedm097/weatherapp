package com.bptn.weatherapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.bptn.weatherapp.jpa.User;
import com.bptn.weatherapp.repository.UserRepository;
import com.bptn.weatherapp.security.JwtService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

	User user;
	String otherUsername;
	String otherPassword;

	@MockBean
	Server server;

	@Autowired
	MockMvc mockMvc; // Perform HTTP requests against Spring MVC controllers without running a full
						// HTTP server

	@Autowired
	ObjectMapper objectMapper; // Serialize and deserialize Java objects to/from JSON

	@Autowired
	JwtService jwtService; // Provides JSON Web Token (JWT) creation and validation functionality

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder; // Spring Security component used to encode and decode passwords

	@BeforeEach
	public void setup() {

		this.user = new User();

		this.user.setFirstName("John");
		this.user.setLastName("Doe");
		this.user.setUsername("johndoe");
		this.user.setPassword("mypassword");
		this.user.setPhone("987654321");
		this.user.setEmailId("johndoe@example.com");

		this.otherUsername = "janedoe";
		this.otherPassword = "letmein";
	}

	@Test
	@Order(1)
	public void signupIntegrationTest() throws Exception {

		ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build(); // Convert User
																											// object to
																											// JSON
																											// format

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/signup").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.firstName", is(this.user.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(this.user.getLastName())))
				.andExpect(jsonPath("$.username", is(this.user.getUsername())))
				.andExpect(jsonPath("$.phone", is(this.user.getPhone())))
				.andExpect(jsonPath("$.emailId", is(this.user.getEmailId())));

		/* Check the DB */
		Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());

		assertTrue(opt.isPresent(), "User Should Exist");

		assertEquals(1, opt.get().getUserId());
		assertEquals(this.user.getFirstName(), opt.get().getFirstName());
		assertEquals(this.user.getLastName(), opt.get().getLastName());
		assertEquals(this.user.getUsername(), opt.get().getUsername());
		assertEquals(this.user.getPhone(), opt.get().getPhone());
		assertEquals(this.user.getEmailId(), opt.get().getEmailId());
		assertEquals(false, opt.get().getEmailVerified());

		assertTrue(this.passwordEncoder.matches(this.user.getPassword(), opt.get().getPassword()));
	}

	@Test
	@Order(2)
	public void signupUsernameExistsIntegrationTest() throws Exception {

		ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build(); // Convert User
																											// object to
																											// JSON
																											// format

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/signup").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
				.andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.reason", is("BAD REQUEST"))).andExpect(jsonPath("$.message",
						is(String.format("Username already exists, %s", this.user.getUsername()))));
	}

	@Test
	@Order(3)
	public void signupEmailExistsIntegrationTest() throws Exception {

		ObjectMapper objectMapper = JsonMapper.builder().disable(MapperFeature.USE_ANNOTATIONS).build(); // Convert User
																											// object to
																											// JSON
																											// format

		this.user.setUsername(this.otherUsername);

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/signup").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(this.user)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
				.andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.reason", is("BAD REQUEST"))).andExpect(
						jsonPath("$.message", is(String.format("Email already exists, %s", this.user.getEmailId()))));
	}

	@Test
	@Order(4)
	public void verifyEmailIntegrationTest() throws Exception {

		/* Check if the User exists */
		Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());
		assertTrue(opt.isPresent(), "User Should Exist");

		/* Check the user emailVerified flag initial value */
		assertEquals(false, opt.get().getEmailVerified());

		String jwt = String.format("Bearer %s", this.jwtService.generateJwtToken(this.user.getUsername(), 10_000));

		/* Check the Rest End Point Response */
		this.mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email").header(AUTHORIZATION, jwt))
				.andExpect(status().isOk());

		/* Check if the User exists */
		opt = this.userRepository.findByUsername(this.user.getUsername());
		assertTrue(opt.isPresent(), "User Should Exist");

		/* Check the user emailVerified flag was updated */
		assertEquals(true, opt.get().getEmailVerified());

	}

	@Test
	@Order(5)
	public void verifyEmailUsernameNotFoundIntegrationTest() throws Exception {

		/* Check if the User exists */
		Optional<User> opt = this.userRepository.findByUsername(this.otherUsername);
		assertTrue(opt.isEmpty(), "User Should Not Exist");

		String jwt = String.format("Bearer %s", this.jwtService.generateJwtToken(this.otherUsername, 10_000));

		/* Check the Rest End Point Response */
		this.mockMvc.perform(MockMvcRequestBuilders.get("/user/verify/email").header(AUTHORIZATION, jwt))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
				.andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.reason", is("BAD REQUEST")))
				.andExpect(jsonPath("$.message", is(String.format("Username doesn't exist, %s", this.otherUsername))));

	}

	@Test
	@Order(6)
	public void loginIntegrationTest() throws Exception {

		/* Create object to generate JSON */
		ObjectNode root = this.objectMapper.createObjectNode();
		root.put("username", this.user.getUsername());
		root.put("password", this.user.getPassword());

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/login").contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(root)))
				.andExpect(header().exists(AUTHORIZATION)).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName", is(this.user.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(this.user.getLastName())))
				.andExpect(jsonPath("$.username", is(this.user.getUsername())))
				.andExpect(jsonPath("$.phone", is(this.user.getPhone())))
				.andExpect(jsonPath("$.emailId", is(this.user.getEmailId())))
				.andExpect(jsonPath("$.emailVerified", is(true)));
	}

	@Test
	@Order(7)
	public void loginEmailNotVerifiedIntegrationTest() throws Exception {

		/* Create object to generate JSON */
		ObjectNode root = this.objectMapper.createObjectNode();
		root.put("username", this.user.getUsername());
		root.put("password", this.user.getPassword());

		/* Check if the User exists */
		Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());
		assertTrue(opt.isPresent(), "User Should Exist");

		/* Set user.emailVerified to false */
		opt.ifPresent(u -> {
			u.setEmailVerified(false);
			this.userRepository.save(u);
		});

		/* Check the Rest End Point Response */
		this.mockMvc
				.perform(MockMvcRequestBuilders.post("/user/login").contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(root)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.httpStatusCode", is(400)))
				.andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")))
				.andExpect(jsonPath("$.reason", is("BAD REQUEST"))).andExpect(jsonPath("$.message",
						is(String.format("Email requires verification, %s", this.user.getEmailId()))));

		/* Check if the User exists */
		opt = this.userRepository.findByUsername(this.user.getUsername());
		assertTrue(opt.isPresent(), "User Should Exist");

		/* Set user.emailVerified to false */
		opt.ifPresent(u -> {
			u.setEmailVerified(true);
			this.userRepository.save(u);
		});

	}

	@Test
	@Order(8)
	public void resetPasswordEmailIntegrationTest() throws Exception {

		/* Check the Rest End Point Response */
		this.mockMvc.perform(MockMvcRequestBuilders.get("/user/reset/{}", this.user.getEmailId()))
				.andExpect(status().isOk());
	}

	@Test
	@Order(9)
	public void resetPasswordIntegrationTest() throws Exception {

		// Retrieve user from the database
		Optional<User> optUser = this.userRepository.findByUsername(this.user.getUsername());

		// Check that the encrypted initial password of the user matches the password
		// stored in the database
		assertTrue(this.passwordEncoder.matches(this.user.getPassword(), optUser.get().getPassword()));

		// Generate a token
		String jwt = String.format("Bearer %s", this.jwtService.generateJwtToken(this.user.getUsername(), 10_000));

		// Send a POST request and check the End point response
		this.mockMvc.perform(MockMvcRequestBuilders.post("/user/reset").param("password", this.otherPassword)
				.header("AUTHORIZATION", jwt)).andExpect(status().isOk());

		// Check if the user still exists in the database
		Optional<User> opt = this.userRepository.findByUsername(this.user.getUsername());

		// Verify that the user was successfully retrieved from the database
		assertTrue(opt.isPresent(), "User Should Exist");

		// Check that the encrypted newly updated password matches with the password in
		// the database
		assertTrue(this.passwordEncoder.matches(this.otherPassword, opt.get().getPassword()));
	}

}
