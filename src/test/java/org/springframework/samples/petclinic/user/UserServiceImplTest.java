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
	private Role studentRole;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setEmail("test@kirkwood.edu");
		testUser.setPassword("rawPassword");

		studentRole = new Role();
		studentRole.setName("STUDENT");
	}

	@Test
	void registerNewStudent_HashesPasswordAndAssignsRole() {
		when(passwordEncoder.encode(testUser.getPassword())).thenReturn("hashedPassword");
		when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(studentRole));
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User registeredUser = userService.registerNewStudent(testUser);

		assertNotNull(registeredUser);
		assertEquals("hashedPassword", registeredUser.getPassword(),
			"Password must be hashed before saving.");
		assertTrue(registeredUser.getRoles().contains(studentRole),
			"User must be assigned the STUDENT role.");

		verify(passwordEncoder, times(1)).encode("rawPassword");
		verify(roleRepository, times(1)).findByName("STUDENT");
		verify(userRepository, times(1)).save(testUser);
	}

	@Test
	void registerNewStudent_RoleNotFound_ThrowsException() {
		when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
		when(roleRepository.findByName("STUDENT")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () ->
				userService.registerNewStudent(testUser),
			"Should throw when STUDENT role is not in the database.");
	}
}
