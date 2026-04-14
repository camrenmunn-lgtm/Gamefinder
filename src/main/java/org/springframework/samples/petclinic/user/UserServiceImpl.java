package org.springframework.samples.petclinic.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// This method is to register user's for Marc's demo project
	public User registerNewCollector(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Role collectorRole = roleRepository.findByName("COLLECTOR").orElseThrow(() -> new RuntimeException("Collector Role Not Found"));
		Set<Role> roles = new HashSet<>();
		roles.add(collectorRole);
		user.setRoles(roles);
		return userRepository.save(user);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}
