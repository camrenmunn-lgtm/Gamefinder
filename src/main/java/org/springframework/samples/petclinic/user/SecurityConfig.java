package org.springframework.samples.petclinic.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(authorize -> authorize

				// -------------------------------------------------------
				// PUBLIC — no login required
				// -------------------------------------------------------
				.requestMatchers(
					"/",
					"/register-student",
					"/login",
					"/resources/**",
					"/recipes/**"
				).permitAll()

				// Games list and detail pages are public (read-only)
				.requestMatchers(HttpMethod.GET, "/games", "/games/**").permitAll()

				// Schools (legacy AthlEagues routes — kept as-is)
				.requestMatchers(HttpMethod.GET, "/schools", "/schools/{slug:[a-zA-Z-]+}").permitAll()

				// -------------------------------------------------------
				// COLLECTOR — authority stored as "COLLECTOR" in DB
				// -------------------------------------------------------
				.requestMatchers("/collector/**").hasAuthority("COLLECTOR")

				// Collectors can write/view reviews on game pages
				.requestMatchers(HttpMethod.POST, "/games/*/reviews/**").hasAuthority("COLLECTOR")
				.requestMatchers(HttpMethod.GET,  "/games/*/reviews/**").hasAuthority("COLLECTOR")

				// -------------------------------------------------------
				// ADMIN — authority stored as "ADMIN" in DB
				// -------------------------------------------------------
				.requestMatchers("/admin/**").hasAuthority("ADMIN")

				// Admin-only write operations on games
				.requestMatchers(HttpMethod.GET,  "/games/new").hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.POST, "/games/new").hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.GET,  "/games/*/edit").hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.POST, "/games/*/edit").hasAuthority("ADMIN")
				.requestMatchers(HttpMethod.POST, "/games/*/delete").hasAuthority("ADMIN")

				// Legacy school management
				.requestMatchers("/schools/new").hasAuthority("MANAGE_ALL_SCHOOLS")

				// -------------------------------------------------------
				// AUTHENTICATED — any logged-in user
				// -------------------------------------------------------
				.requestMatchers("/users/profile", "/users/delete").authenticated()

				// -------------------------------------------------------
				// CATCH-ALL
				// -------------------------------------------------------
				.anyRequest().authenticated()
			)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(form -> form
				.loginPage("/login")
				.usernameParameter("email")
				.defaultSuccessUrl("/login-success", true)
				.failureUrl("/login?error=true")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			);

		return http.build();
	}
}
