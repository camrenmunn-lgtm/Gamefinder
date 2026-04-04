package org.springframework.samples.petclinic.game;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.game.Game;

import java.util.List;

@Entity
@Table(name = "publishers")
@Getter
@Setter
@AttributeOverride(name = "name", column = @Column(name = "publisher_name"))
public class Publisher extends NamedEntity {

	@Column(name = "PublisherCountry")
	private String publisherCountry;

	@Column(name = "FoundedYear")
	private Integer foundedYear;

	@Column(name = "PublisherSite")
	private String publisherSite;

	@Column(name = "PublisherDescription")
	private String publisherDescription;

	@OneToMany(mappedBy = "publisher")
	private List<Game> games;

}
