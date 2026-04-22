package org.springframework.samples.petclinic.gamelist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameListRepository extends JpaRepository<GameList, Integer> {

	List<GameList> findByUserId(Integer userId);

	@Query("SELECT gl FROM GameList gl LEFT JOIN FETCH gl.games WHERE gl.id = :id")
	Optional<GameList> findByIdWithGames(@Param("id") Integer id);

	@Query("SELECT gl FROM GameList gl WHERE gl.user.id = :userId AND gl.listType = :listType")
	List<GameList> findByUserIdAndListType(@Param("userId") Integer userId,
										   @Param("listType") GameList.ListType listType);
}
