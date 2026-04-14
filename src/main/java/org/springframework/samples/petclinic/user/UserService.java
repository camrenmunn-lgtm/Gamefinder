package org.springframework.samples.petclinic.user;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface UserService {
	/**
	 * Handles business logic for registering a new user(password hashing, role assignments)
	 * @param user The User object containing new user details.
	 * @return The saved User object
	 */
	User registerNewCollector(User user);

	Optional<User> findByEmail(String email);
}
