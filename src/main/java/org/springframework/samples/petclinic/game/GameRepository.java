package org.springframework.samples.petclinic.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface GameRepository extends Repository<Game, Integer> {

	/**
	 * Retrieve all Games from the data store.
	 */
	@Transactional(readOnly = true)
	Collection<Game> findAll();

	/**
	 * Retrieve Games by page (for pagination in the UI)
	 */
	@Transactional(readOnly = true)
	Page<Game> findAll(Pageable pageable);

	/**
	 * Save a Game to the data store, either inserting or updating it.
	 */
	void save(Game game);

	/**
	 * Delete a Game from the data store.
	 */
	void delete(Game game);

	/**
	 * Retrieve a Game by its id.
	 */
	@Transactional(readOnly = true)
	Game findById(Integer id);

	/**
	 * Retrieve a Game by its exact title.
	 * Used for slug-based routing (/games/super-mario-bros).
	 */
	@Transactional(readOnly = true)
	Game findByName(String name);

}
