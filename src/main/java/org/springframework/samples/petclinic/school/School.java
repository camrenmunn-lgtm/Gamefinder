package org.springframework.samples.petclinic.school;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.samples.petclinic.school.Location;
import org.springframework.samples.petclinic.model.NamedEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schools")
@Getter
@Setter
// Intercept the delete command and turn it into an update
@SQLDelete(sql = "UPDATE dbf0fuuftl3rhr.schools SET deleted_at = NOW() WHERE id = ?")
// Automatically filter out deleted rows when reading data
@SQLRestriction("deleted_at IS NULL")
public class School extends NamedEntity {

	// Inherits 'id' and 'name'

	@Column(name = "domain")
	@NotEmpty
	private String domain;

	@Enumerated(EnumType.STRING)
	@Column(name = "status_id")
	private SchoolStatus status = SchoolStatus.ACTIVE;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "school", fetch = FetchType.EAGER)
	private List<Location> locations = new ArrayList<>();

	// We keep this helper method manually because Lombok @Setter
	// simply replaces the list, it doesn't handle the bidirectional link.
	public void addLocation(Location location) {
		location.setSchool(this);
		getLocations().add(location);
	}

	public enum SchoolStatus {

		ACTIVE, INACTIVE, SUSPENDED

	}

}
