package org.springframework.samples.petclinic.gamelist;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "game_lists")
@Getter
@Setter
public class GameList extends BaseEntity {

	// user is set by the controller after validation — no @NotNull here
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@NotBlank(message = "List name is required")
	@Size(max = 100, message = "List name must be 100 characters or fewer")
	@Column(name = "list_name", nullable = false, length = 100)
	private String listName;

	@Size(max = 500, message = "Description must be 500 characters or fewer")
	@Column(name = "description")
	private String description;

	@NotNull(message = "List type is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "list_type", nullable = false)
	private ListType listType = ListType.Custom;

	@Column(name = "is_public", nullable = false)
	private Boolean isPublic = false;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "game_list_games",
		joinColumns = @JoinColumn(name = "game_list_id"),
		inverseJoinColumns = @JoinColumn(name = "game_id")
	)
	private Set<Game> games = new HashSet<>();

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public enum ListType {
		Collection, Wishlist, Completed, Custom
	}
}
