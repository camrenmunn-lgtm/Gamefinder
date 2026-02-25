package org.springframework.samples.petclinic.school;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;

public interface SchoolRepository extends Repository<School, Integer> {

	/**
	 * Retrieve all Schools from the data store.
	 */
	@Transactional(readOnly = true)
	Collection<School> findAll();

	/**
	 * Retrieve Schools by page (for pagination in the UI)
	 */
	@Transactional(readOnly = true)
	Page<School> findAll(Pageable pageable);

	/**
	 * Save a School to the data store, either inserting or updating it.
	 */
	void save(School school);

	/**
	 * Retrieve a School by its id.
	 */
	@Transactional(readOnly = true)
	School findById(Integer id);
}
