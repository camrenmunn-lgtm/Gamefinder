package org.springframework.samples.petclinic.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link GameController}
 */
@WebMvcTest(GameController.class)
class GameControllerTest {

	private static final int TEST_GAME_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GameRepository games;

	@MockitoBean
	private PublisherRepository publishers;

	private Game game;

	@BeforeEach
	void setup() {
		// Create a dummy game to be returned by the mocked repository
		game = new Game();
		game.setId(TEST_GAME_ID);
		game.setName("The Legend of Zelda");
		game.setGameType(Game.GameType.VideoGame);
		game.setReleaseYear(1986);
		game.setRarityScore(Game.RarityScore.VeryRare);
		game.setAvgValue(new BigDecimal("85.00"));

		// Stub publishers to return an empty list by default
		given(this.publishers.findAll()).willReturn(List.of());
	}

	@Test
	void testShowGameList() throws Exception {
		// 1. Arrange: Create a "Page" of games to mock the database response
		// matches the 5 items per page logic in your controller
		Pageable pageable = PageRequest.of(0, 5);
		Page<Game> gamePage = new PageImpl<>(List.of(game), pageable, 1);

		// Tell the mock: "When the controller asks for all games, give them this list"
		given(this.games.findAll(any(Pageable.class))).willReturn(gamePage);

		// 2. Act & Assert: Perform the GET request and verify the results
		mockMvc.perform(get("/games").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("listGames"))
			.andExpect(model().attributeExists("totalPages"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(view().name("games/gamesList"));
	}

	@Test
	void testInitCreationForm() throws Exception {
		// GET /games/new should return a blank form with an empty Game object
		mockMvc.perform(get("/games/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("game"))
			.andExpect(model().attributeExists("publishers"))
			.andExpect(view().name("games/createOrUpdateGameForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		// POST /games/new with valid data should save and redirect to the games list
		mockMvc.perform(post("/games/new")
				.param("name", "Tetris")
				.param("gameType", "VideoGame")
				.param("releaseYear", "1984")
				.param("rarityScore", "Common")
				.param("avgValue", "12.50"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/games"));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		// POST /games/new with a blank name should re-display the form with errors
		mockMvc.perform(post("/games/new")
				.param("name", "")         // blank name — required field
				.param("gameType", "")     // blank type — required field
				.param("releaseYear", "")) // blank year — required field
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("game"))
			.andExpect(model().attributeHasFieldErrors("game", "name"))
			.andExpect(view().name("games/createOrUpdateGameForm"));
	}

}
