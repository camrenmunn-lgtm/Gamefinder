package org.springframework.samples.petclinic.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends BaseEntity {

	// user and game are set by the controller after validation — no @NotNull here
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be at least 1")
	@Max(value = 5, message = "Rating must be at most 5")
	@Column(name = "rating", nullable = false)
	private Integer rating;

	@NotBlank(message = "Review title is required")
	@Size(max = 150, message = "Title must be 150 characters or fewer")
	@Column(name = "title", nullable = false, length = 150)
	private String title;

	@Size(max = 2000, message = "Review body must be 2000 characters or fewer")
	@Column(name = "body", columnDefinition = "TEXT")
	private String body;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}
