package org.springframework.samples.petclinic.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;

	private User testUser;
	private Role studentRole;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail("example-student@kirkwood.edu");
		testUser.setPassword("hashedPassword");

		Role studentRole = new Role();
		studentRole.setName("STUDENT");

		Permission viewLeagues = new Permission();
		viewLeagues.setName("VIEW_LEAGUES");
		studentRole.setPermissions(Set.of(viewLeagues));

		testUser.setRoles(Set.of(studentRole));
	}

	@Test
	void loadUserByUsername() {
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

		UserDetails userDetails = userDetailsService.loadUserByUsername(testUser.getEmail());

		assertNotNull(userDetails);
		assertEquals(testUser.getEmail(), userDetails.getUsername());
		assertEquals(testUser.getPassword(), userDetails.getPassword());

		// Role must have ROLE_ prefix
		assertTrue(userDetails.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));

		// Permission loaded without prefix
		assertTrue(userDetails.getAuthorities().stream()
			.anyMatch(a -> a.getAuthority().equals("VIEW_LEAGUES")));

		verify(userRepository, times(1)).findByEmail(testUser.getEmail());
	}

	@Test
	void loadUserByUsername_UserNotFound_ThrowsException() {
		when(userRepository.findByEmail("notfound@kirkwood.edu")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () ->
			userDetailsService.loadUserByUsername("notfound@kirkwood.edu"));
	}

	@Test
	void loadUserByUsername_SoftDeletedUser_ThrowsException() {
		testUser.setDeletedAt(LocalDateTime.now());
		when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

		assertThrows(UsernameNotFoundException.class, () ->
			userDetailsService.loadUserByUsername(testUser.getEmail()));
	}
}
