package org.springframework.samples.petclinic.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.samples.petclinic.school.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisabledInNativeImage
@DisabledInAotMode
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SchoolRepository schoolRepository;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private UserDetailsService userDetailsService;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	// -------------------------------------------------------------------------
	// Registration tests
	// -------------------------------------------------------------------------

	@Test
	void testInitRegisterFormLoadsCorrectly() throws Exception {
		mockMvc.perform(get("/register-student"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/registerForm"))
			.andExpect(model().attributeExists("user"));
	}

	@Test
	void testProcessRegisterValidationFailsWithBlankFields() throws Exception {
		mockMvc.perform(post("/register-student")
				.with(csrf())
				.param("email", "")
				.param("password", ""))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/registerForm"))
			.andExpect(model().attributeHasFieldErrors("user", "email", "password"));
	}

	@Test
	void testProcessRegisterRedirectsToHomeOnSuccess() throws Exception {
		given(userService.registerNewCollector(any(User.class))).willReturn(new User());
		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willReturn(new TestingAuthenticationToken("user", "password", "ROLE_COLLECTOR"));

		mockMvc.perform(post("/register-student")
				.with(csrf())
				.param("email", "collector@gmail.com")
				.param("password", "StrongPass1!"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(flash().attributeExists("messageSuccess"));
	}

	@Test
	void testProcessRegisterDuplicateEmailShowsError() throws Exception {
		given(userService.registerNewCollector(any(User.class)))
			.willThrow(new RuntimeException("Email already registered"));

		mockMvc.perform(post("/register-student")
				.with(csrf())
				.param("email", "duplicate@gmail.com")
				.param("password", "StrongPass1!"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/registerForm"))
			.andExpect(model().attributeHasFieldErrors("user", "email"));
	}

	// -------------------------------------------------------------------------
	// Login form tests
	// -------------------------------------------------------------------------

	@Test
	void testInitLoginFormLoadsCorrectly() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/loginForm"))
			.andExpect(model().attributeExists("user"));
	}

	@Test
	void testInitLoginFormRemembersFailedEmail() throws Exception {
		mockMvc.perform(get("/login").sessionAttr("LAST_EMAIL", "wrong@gmail.com"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("user"))
			.andExpect(content().string(containsString("wrong@gmail.com")));
	}

	// -------------------------------------------------------------------------
	// Login-success redirect tests
	// -------------------------------------------------------------------------

	@Test
	void testLoginSuccessRedirectsToHomeWithWelcomeMessage() throws Exception {
		User mockUser = new User();
		mockUser.setEmail("collector@gmail.com");
		mockUser.setNickname("GameFan");

		given(userService.findByEmail(anyString())).willReturn(Optional.of(mockUser));

		Principal mockPrincipal = () -> "collector@gmail.com";

		mockMvc.perform(get("/login-success").principal(mockPrincipal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(flash().attributeExists("messageSuccess"));
	}

	@Test
	void testLoginSuccessRedirectsToHomeWhenUserNotFound() throws Exception {
		given(userService.findByEmail(anyString())).willReturn(Optional.empty());

		Principal mockPrincipal = () -> "unknown@gmail.com";

		mockMvc.perform(get("/login-success").principal(mockPrincipal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(flash().attributeExists("messageSuccess"));
	}
}
