package org.springframework.samples.petclinic.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class GameController {

	private final GameRepository gameRepository;

	public GameController(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
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

}
