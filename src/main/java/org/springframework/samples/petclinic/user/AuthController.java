package org.springframework.samples.petclinic.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.samples.petclinic.school.School;
import org.springframework.samples.petclinic.school.SchoolRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.samples.petclinic.validation.OnRegister;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AuthController {
	private final UserService userService;
	private final SchoolRepository schoolRepository;
	private final AuthenticationManager authenticationManager; // Add this field

	// Add to Constructor
	public AuthController(UserService userService, SchoolRepository schoolRepository, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.schoolRepository = schoolRepository;
		this.authenticationManager = authenticationManager;
	}

	@GetMapping("/register-student")
	public String initRegisterForm(Model model) {
		model.addAttribute("user", new User());
		return "auth/registerForm";
	}

	@PostMapping("/register-student")
	public String processRegisterForm(@Validated(OnRegister.class) @ModelAttribute("user") User user,
									  BindingResult result,
									  RedirectAttributes redirectAttributes,
									  HttpServletRequest request) {
		if (result.hasErrors()) {
			return "auth/registerForm";
		}

		String rawPassword = user.getPassword();

		// 1. Save the User (UserService handles password hashing)
		try {
			userService.registerNewCollector(user);
		} catch (RuntimeException ex) {
			// Handle duplicate email or other service errors
			result.rejectValue("email", "duplicateEmail", "This email is already registered");
			return "auth/registerForm";
		}

		// To do: Send email verification before auto log in.
		// 2. LOGIN using the authenticationManager.
		try {
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), rawPassword);
			Authentication authentication = authenticationManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			HttpSession session = request.getSession(true);
			session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext()
			);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("messageDanger", "Account created, but auto-login failed.");
			return "redirect:/login";
		}

		// 3. Redirect new user to homepage with welcome message
		String nickname = user.getNickname() != null ? user.getNickname() : user.getEmail();
		redirectAttributes.addFlashAttribute("messageSuccess",
			"Welcome, " + nickname + "! Your account has been created.");
		return "redirect:/";
	}

	private Optional<School> findSchoolByRecursiveDomain(String email) {
		// 1. Extract the initial domain (e.g., "student.kirkwood.edu")
		String domain = email.substring(email.indexOf("@") + 1);

		// 2. Loop while the domain is valid (has at least one dot)
		while (domain.contains(".")) {
			// 3. Check Database
			Optional<School> school = schoolRepository.findByDomain(domain);
			if (school.isPresent()) {
				return school; // Found match (e.g., "kirkwood.edu")
			}

			// 4. Strip the first part (e.g., "student.kirkwood.edu" -> "kirkwood.edu")
			int dotIndex = domain.indexOf(".");
			domain = domain.substring(dotIndex + 1);
		}

		return Optional.empty();
	}

	@GetMapping("/login-success")
	public String processLoginSuccess(Principal principal, RedirectAttributes redirectAttributes) {
		String email = principal.getName();
		// Look up the user to get their display name
		String displayName = userService.findByEmail(email)
			.map(u -> u.getNickname() != null ? u.getNickname() : u.getEmail())
			.orElse(email);
		redirectAttributes.addFlashAttribute("messageSuccess", "Welcome back, " + displayName + "!");
		return "redirect:/";
	}

	@GetMapping("/login")
	public String initLoginForm(Model model, HttpSession session,
								@RequestParam(value = "error", required = false) String error) {
		User user = new User();
		String lastEmail = (String) session.getAttribute("LAST_EMAIL");
		if (lastEmail != null) {
			user.setEmail(lastEmail);
			session.removeAttribute("LAST_EMAIL");
		}
		if (error != null) {
			model.addAttribute("messageDanger", "Invalid email or password.");
		}
		model.addAttribute("user", user);
		return "auth/loginForm";
	}

}
