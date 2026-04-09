package org.springframework.samples.petclinic.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.samples.petclinic.school.School;
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
	void testProcessRegisterWithSubdomainRedirect() throws Exception {
		School kirkwood = new School();
		kirkwood.setId(1);
		kirkwood.setName("Kirkwood");
		kirkwood.setDomain("kirkwood.edu");

		given(schoolRepository.findByDomain("kirkwood.edu")).willReturn(Optional.of(kirkwood));
		given(schoolRepository.findByDomain("student.kirkwood.edu")).willReturn(Optional.empty());
		given(userService.registerNewStudent(any(User.class))).willReturn(new User());
		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willReturn(new TestingAuthenticationToken("user", "password", "ROLE_STUDENT"));

		mockMvc.perform(post("/register-student")
				.with(csrf())
				.param("email", "alex@student.kirkwood.edu")
				.param("password", "StrongPass1!"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/schools/kirkwood"));
	}

	@Test
	void testProcessRegisterRedirectsToSchoolsIfNoMatch() throws Exception {
		given(schoolRepository.findByDomain(anyString())).willReturn(Optional.empty());
		given(userService.registerNewStudent(any(User.class))).willReturn(new User());
		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willReturn(new TestingAuthenticationToken("user", "password", "ROLE_STUDENT"));

		mockMvc.perform(post("/register-student")
				.with(csrf())
				.param("email", "someone@gmail.com")
				.param("password", "StrongPass1!"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/schools"));
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
		mockMvc.perform(get("/login").sessionAttr("LAST_EMAIL", "wrong@kirkwood.edu"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("user"))
			.andExpect(content().string(containsString("wrong@kirkwood.edu")));
	}

	// -------------------------------------------------------------------------
	// Login-success redirect tests
	// -------------------------------------------------------------------------

	@Test
	void testLoginSuccessRedirectsToSchool() throws Exception {
		School mockSchool = new School();
		mockSchool.setName("Kirkwood Community College");
		mockSchool.setDomain("kirkwood.edu");

		given(schoolRepository.findByDomain(anyString())).willReturn(Optional.of(mockSchool));

		Principal mockPrincipal = () -> "student@kirkwood.edu";

		mockMvc.perform(get("/login-success").principal(mockPrincipal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/schools/kirkwood"))
			.andExpect(flash().attributeExists("messageSuccess"));
	}

	@Test
	void testLoginSuccessRedirectsToSchoolsListIfNotFound() throws Exception {
		given(schoolRepository.findByDomain(anyString())).willReturn(Optional.empty());

		Principal mockPrincipal = () -> "student@unknown.com";

		mockMvc.perform(get("/login-success").principal(mockPrincipal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/schools"))
			.andExpect(flash().attributeExists("messageWarning"));
	}
}
