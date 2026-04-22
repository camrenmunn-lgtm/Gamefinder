package org.springframework.samples.petclinic.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface PublisherRepository extends Repository<Publisher, Integer> {

	@Transactional(readOnly = true)
	Collection<Publisher> findAll();

	@Transactional(readOnly = true)
	Page<Publisher> findAll(Pageable pageable);

	/**
	 * Find by ID without games — used for list page and edit/delete.
	 */
	@Transactional(readOnly = true)
	Optional<Publisher> findById(Integer id);

	/**
	 * Find by ID with games eagerly fetched — used for detail page only.
	 */
	@Transactional(readOnly = true)
	@Query("SELECT p FROM Publisher p LEFT JOIN FETCH p.games WHERE p.id = :id")
	Optional<Publisher> findByIdWithGames(@Param("id") Integer id);

	void save(Publisher publisher);

	void delete(Publisher publisher);
}
