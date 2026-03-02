package org.springframework.samples.petclinic.game;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.model.NamedEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter
@Setter
@AttributeOverride(name = "name", column = @Column(name = "GameTitle"))
public class Game extends NamedEntity {

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "GameType")
	private GameType gameType;

	@NotNull
	@Min(value = 1800, message = "Release year must be 1800 or later")
	@Max(value = 2027, message = "Release year must be 2027 or earlier")
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

	@Digits(integer = 10, fraction = 0, message = "Estimated copies must be a valid number")
	@Column(name = "EstimatedCopiesMade")
	private Double estimatedCopiesMade;

	@Digits(integer = 10, fraction = 2, message = "Average value must be a valid amount (e.g. 12.50)")
	@Column(name = "AvgValue")
	private BigDecimal avgValue;

	@Lob
	@Column(name = "GameImage")
	private byte[] gameImage;

	@Column(name = "CreatedAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt", insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public enum GameType {
		VideoGame, BoardGame
	}

	public enum RarityScore {
		UltraRare, VeryRare, Rare, Uncommon, Common
	}

}
