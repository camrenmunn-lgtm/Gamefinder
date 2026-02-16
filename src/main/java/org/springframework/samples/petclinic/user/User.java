package org.springframework.samples.petclinic.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name="last_name", nullable = false, length = 50)
	private String lastName;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(name="password_hash", nullable = false, length = 255)
	private String password;

	// Many-to-Many Relationship with Role
	@ManyToMany(fetch = FetchType.EAGER) // Fetch roles immediately when a user is loaded
	@JoinTable(
		name = "user_roles", // Name of the junction table in MySQL
		joinColumns = @JoinColumn(name = "user_id"), // Column in user_roles that references the 'users' table
		inverseJoinColumns = @JoinColumn(name = "role_id") // Column in user_roles that references the 'roles' table
	)
	@EqualsAndHashCode.Exclude
	private Set<Role> roles;
}