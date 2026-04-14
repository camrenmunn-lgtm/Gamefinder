package org.springframework.samples.petclinic.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	private User testUser;
	private Role collectorRole;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail("test@gmail.com");
		testUser.setPassword("rawPassword");

		collectorRole = new Role();
		collectorRole.setName("COLLECTOR");
	}
	@Test
	void registerNewCollector_HashesPasswordAndAssignsRole() {
		when(passwordEncoder.encode(testUser.getPassword())).thenReturn("hashedPassword");
		when(roleRepository.findByName("COLLECTOR")).thenReturn(Optional.of(collectorRole));
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User registeredUser = userService.registerNewCollector(testUser);

		assertNotNull(registeredUser);
		assertEquals("hashedPassword", registeredUser.getPassword(),
			"Password must be hashed before saving.");
		assertTrue(registeredUser.getRoles().contains(collectorRole),
			"User must be assigned the COLLECTOR  role.");

		verify(passwordEncoder, times(1)).encode("rawPassword");
		verify(roleRepository, times(1)).findByName("COLLECTOR");
		verify(userRepository, times(1)).save(testUser);
	}

	@Test
	void registerNewCollector_RoleNotFound_ThrowsException() {
		when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
		when(roleRepository.findByName("COLLECTOR")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () ->
				userService.registerNewCollector(testUser),
			"Should throw when COLLECTOR  role is not in the database.");
	}
}
