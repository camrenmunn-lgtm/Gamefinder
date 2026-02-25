package org.springframework.samples.petclinic.game;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.model.NamedEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter // Auto-generates getters for all fields
@Setter // Auto-generates setters for all fields
@AttributeOverride(name = "name", column = @Column(name = "GameTitle"))
public class Game extends NamedEntity {

	@Enumerated(EnumType.STRING)
	@Column(name = "GameType")
	private GameType gameType;

	@Column(name = "ReleaseYear")
	private Integer releaseYear;

	@ManyToOne
	@JoinColumn(name = "Publishers_ID")
	private Publisher publisher;

	@Column(name = "Description")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "RarityScore")
	private RarityScore rarityScore;

	@Column(name = "EstimatedCopiesMade")
	private Double estimatedCopiesMade;

	@Column(name = "AvgValue")
	private BigDecimal avgValue;

	@Lob
	@Column(name = "GameImage")
	private byte[]  gameImage;

	@Column(name = "CreatedAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt", insertable = false, updatable = false)
	private  LocalDateTime updatedAt;

	public enum GameType {
		VideoGame, BoardGame
	}

	public enum RarityScore{
		UltraRare, VeryRare, Rare, Uncommon, Common
	}

}
