package org.springframework.samples.petclinic.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	List<Review> findByGameId(Integer gameId);

	List<Review> findByUserId(Integer userId);

	Optional<Review> findByUserIdAndGameId(Integer userId, Integer gameId);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.game.id = :gameId")
	Double findAverageRatingByGameId(@Param("gameId") Integer gameId);
}
