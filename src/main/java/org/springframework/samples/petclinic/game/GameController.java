package org.springframework.samples.petclinic.game;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	@GetMapping("/games/new")
	public String initCreationForm(Model model) {
		model.addAttribute("game", new Game());
		model.addAttribute("gameTypes", Game.GameType.values());
		model.addAttribute("rarityScores", Game.RarityScore.values());
		model.addAttribute("publishers", publisherRepository.findAll());
		return "games/createOrUpdateGameForm";
	}

	@PostMapping("/games/new")
	public String processCreationForm(@Valid Game game, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("gameTypes", Game.GameType.values());
			model.addAttribute("rarityScores", Game.RarityScore.values());
			model.addAttribute("publishers", publisherRepository.findAll());
			return "games/createOrUpdateGameForm";
		}
		gameRepository.save(game);
		return "redirect:/games";
	}

}
