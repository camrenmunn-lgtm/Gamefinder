package org.springframework.samples.petclinic.game;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;

public interface PublisherRepository extends Repository<Publisher, Integer> {

	@Transactional(readOnly = true)
	Collection<Publisher> findAll();

}
