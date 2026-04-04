package org.springframework.samples.petclinic.game;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
class GameController {

	private final GameRepository gameRepository;

	private final PublisherRepository publisherRepository;

	public GameController(GameRepository gameRepository, PublisherRepository publisherRepository) {
		this.gameRepository = gameRepository;
		this.publisherRepository = publisherRepository;
	}

	@GetMapping("/games")
	public String showGameList(@RequestParam(defaultValue = "1") int page, Model model) {
		Pageable pageable = PageRequest.of(page - 1, 5);
		Page<Game> gamePage = gameRepository.findAll(pageable);

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", gamePage.getTotalPages());
		model.addAttribute("totalItems", gamePage.getTotalElements());
		model.addAttribute("listGames", gamePage.getContent());

		return "games/gamesList";
	}

	/**
	 * Show game details by numeric ID — e.g. /games/1
	 * The \\d+ regex ensures this only matches numbers,
	 * so it never conflicts with /games/new or the slug route.
	 */
	@GetMapping("/games/{gameId:\\d+}")
	public String showGameById(@PathVariable("gameId") int gameId, Model model) {
		Game game = gameRepository.findById(gameId);
		if (game == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		}
		model.addAttribute("game", game);
		return "games/gameDetails";
	}

	@GetMapping("/games/{slug:[a-zA-Z0-9\\-]+[a-zA-Z][a-zA-Z0-9\\-]*}")
	public String showGameBySlug(@PathVariable("slug") String slug, Model model) {
		String title = java.util.Arrays.stream(slug.split("-"))
			.map(word -> word.isEmpty() ? word
				: Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
			.collect(java.util.stream.Collectors.joining(" "));

		Game game = gameRepository.findByName(title);
		if (game == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		}
		model.addAttribute("game", game);
		return "games/gameDetails";
	}

	private void populateFormModel(Model model) {
		model.addAttribute("gameTypes", Game.GameType.values());
		model.addAttribute("rarityScores", Game.RarityScore.values());
		model.addAttribute("publishers", publisherRepository.findAll());
	}

	@GetMapping("/games/new")
	public String initCreationForm(Model model) {
		model.addAttribute("game", new Game());
		populateFormModel(model);
		return "games/createOrUpdateGameForm";
	}

	@PostMapping("/games/new")
	public String processCreationForm(@Valid Game game, BindingResult result, Model model) {
		if (result.hasErrors()) {
			populateFormModel(model);
			return "games/createOrUpdateGameForm";
		}
		try {
			gameRepository.save(game);
		}
		catch (DataIntegrityViolationException e) {
			result.rejectValue("name", "duplicate", "A game with this title already exists");
			populateFormModel(model);
			return "games/createOrUpdateGameForm";
		}
		return "redirect:/games";
	}

}
