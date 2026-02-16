package org.springframework.samples.petclinic.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;
	private final AuthenticationManager authenticationManager;

	public AuthController(UserService userService, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user){
		try {
			userService.registerNewUser(user);
			return new  ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
		} catch(RuntimeException e){
			return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> authenticateUser(@RequestBody LoginRequest loginRequest) {
		// 1. Create a token with the user's plain text credentials
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
			loginRequest.getEmail(),
			loginRequest.getPassword()
		);

		// 2. Process authentication using the manager (which uses your UserDetailsService)
		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		// 3. Optional: Set the authenticated user in the security context (needed for session-based security)
		// Since your app is stateless, you would typically generate a JWT token here.
		// For testing, we'll confirm success.

		// If the line above didn't throw an exception, authentication succeeded.
		return new ResponseEntity<>("User logged in successfully!", HttpStatus.OK);
	}
}