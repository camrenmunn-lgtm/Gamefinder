package org.springframework.samples.petclinic.game;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.gamelist.GameListRepository;
import org.springframework.samples.petclinic.review.ReviewRepository;
import org.springframework.samples.petclinic.gamelist.GameListRepository;
import org.springframework.samples.petclinic.user.UserService;
import java.security.Principal;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class GameController {

	private final GameRepository gameRepository;

	private final PublisherRepository publisherRepository;

	private final ReviewRepository reviewRepository;

	private final GameListRepository gameListRepository;

	private final UserService userService;

	public GameController(GameRepository gameRepository, PublisherRepository publisherRepository,
						  ReviewRepository reviewRepository, GameListRepository gameListRepository,
						  UserService userService) {
		this.gameRepository = gameRepository;
		this.publisherRepository = publisherRepository;
		this.reviewRepository = reviewRepository;
		this.gameListRepository = gameListRepository;
		this.userService = userService;

	}

	// -------------------------------------------------------------------------
	// LIST
	// -------------------------------------------------------------------------

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

	// -------------------------------------------------------------------------
	// DETAIL - by numeric ID e.g. /games/1
	// The \\d+ regex ensures this only matches numbers,
	// so it never conflicts with /games/new or the slug route.
	// -------------------------------------------------------------------------

	@GetMapping("/games/{gameId:\\d+}")
	public String showGameById(@PathVariable("gameId") int gameId, Model model,
							   Principal principal) {
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		model.addAttribute("game", game);
		model.addAttribute("reviews", reviewRepository.findByGameId(gameId));

		// Load collector's lists for the "Add to List" dropdown
		if (principal != null) {
			userService.findByEmail(principal.getName()).ifPresent(user -> {
				boolean isCollector = user.getRoles().stream()
					.anyMatch(r -> r.getName().equals("COLLECTOR"));
				if (isCollector) {
					model.addAttribute("userLists", gameListRepository.findByUserId(user.getId()));
				}
			});
		}

		return "games/gameDetails";
	}

	// -------------------------------------------------------------------------
	// DETAIL - by slug e.g. /games/super-mario-bros
	// -------------------------------------------------------------------------

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

	// -------------------------------------------------------------------------
	// SHARED form helper
	// -------------------------------------------------------------------------

	private void populateFormModel(Model model) {
		model.addAttribute("gameTypes", Game.GameType.values());
		model.addAttribute("rarityScores", Game.RarityScore.values());
		model.addAttribute("publishers", publisherRepository.findAll());
	}

	// -------------------------------------------------------------------------
	// CREATE (kept at original /games/new)
	// -------------------------------------------------------------------------

	@GetMapping("/games/new")
	public String initCreationForm(Model model) {
		model.addAttribute("game", new Game());
		populateFormModel(model);
		return "games/createOrUpdateGameForm";
	}

	@PostMapping("/games/new")
	public String processCreationForm(@Valid Game game, BindingResult result, Model model,
									  RedirectAttributes redirectAttributes) {
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
		redirectAttributes.addFlashAttribute("messageSuccess",
			"Game \"" + game.getName() + "\" created!");
		return "redirect:/games";
	}

	// -------------------------------------------------------------------------
	// UPDATE
	// -------------------------------------------------------------------------

	@GetMapping("/games/{gameId:\\d+}/edit")
	public String initEditForm(@PathVariable int gameId, Model model) {
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		model.addAttribute("game", game);
		populateFormModel(model);
		return "games/createOrUpdateGameForm";
	}

	@PostMapping("/games/{gameId:\\d+}/edit")
	public String processEditForm(@PathVariable int gameId,
								  @Valid Game game, BindingResult result, Model model,
								  RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			populateFormModel(model);
			return "games/createOrUpdateGameForm";
		}
		Game existing = gameRepository.findById(gameId);
		if (existing == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");

		existing.setName(game.getName());
		existing.setGameType(game.getGameType());
		existing.setReleaseYear(game.getReleaseYear());
		existing.setPublisher(game.getPublisher());
		existing.setDescription(game.getDescription());
		existing.setRarityScore(game.getRarityScore());
		existing.setEstimatedCopiesMade(game.getEstimatedCopiesMade());
		existing.setAvgValue(game.getAvgValue());

		try {
			gameRepository.save(existing);
		}
		catch (DataIntegrityViolationException e) {
			result.rejectValue("name", "duplicate", "A game with this title already exists");
			populateFormModel(model);
			return "games/createOrUpdateGameForm";
		}
		redirectAttributes.addFlashAttribute("messageSuccess", "Game updated!");
		return "redirect:/games/" + gameId;
	}

	// -------------------------------------------------------------------------
	// DELETE
	// -------------------------------------------------------------------------

	@PostMapping("/games/{gameId:\\d+}/delete")
	public String deleteGame(@PathVariable int gameId, RedirectAttributes redirectAttributes) {
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		try {
			gameRepository.delete(game);
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("messageDanger",
				"Cannot delete this game — it is referenced by other records.");
			return "redirect:/games/" + gameId;
		}
		redirectAttributes.addFlashAttribute("messageSuccess",
			"Game \"" + game.getName() + "\" deleted.");
		return "redirect:/games";
	}
}
