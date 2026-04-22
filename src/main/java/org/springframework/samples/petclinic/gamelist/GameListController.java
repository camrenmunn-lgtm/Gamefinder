package org.springframework.samples.petclinic.gamelist;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/collector/lists")
public class GameListController {

	private final GameListRepository gameListRepository;
	private final GameRepository gameRepository;
	private final UserService userService;

	public GameListController(GameListRepository gameListRepository,
							  GameRepository gameRepository,
							  UserService userService) {
		this.gameListRepository = gameListRepository;
		this.gameRepository = gameRepository;
		this.userService = userService;
	}

	// -------------------------------------------------------------------------
	// READ — list all lists for current user
	// -------------------------------------------------------------------------
	@GetMapping
	public String listAll(Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		List<GameList> lists = gameListRepository.findByUserId(user.getId());
		model.addAttribute("gameLists", lists);
		return "gamelists/gameListsIndex";
	}

	// -------------------------------------------------------------------------
	// READ — view a single list with its games
	// -------------------------------------------------------------------------
	@GetMapping("/{listId:\\d+}")
	public String viewList(@PathVariable int listId, Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList gameList = gameListRepository.findByIdWithGames(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List not found"));
		if (!gameList.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		model.addAttribute("gameList", gameList);
		return "gamelists/gameListDetail";
	}

	// -------------------------------------------------------------------------
	// CREATE — show form
	// -------------------------------------------------------------------------
	@GetMapping("/new")
	public String initCreateForm(Model model) {
		model.addAttribute("gameList", new GameList());
		model.addAttribute("listTypes", GameList.ListType.values());
		return "gamelists/createOrUpdateGameListForm";
	}

	// -------------------------------------------------------------------------
	// CREATE — process form
	// -------------------------------------------------------------------------
	@PostMapping("/new")
	public String processCreateForm(@Valid GameList gameList, BindingResult result,
									Model model, Principal principal,
									RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("listTypes", GameList.ListType.values());
			return "gamelists/createOrUpdateGameListForm";
		}
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		gameList.setUser(user);
		gameListRepository.save(gameList);
		redirectAttributes.addFlashAttribute("messageSuccess", "List \"" + gameList.getListName() + "\" created!");
		return "redirect:/collector/lists";
	}

	// -------------------------------------------------------------------------
	// UPDATE — show form
	// -------------------------------------------------------------------------
	@GetMapping("/{listId:\\d+}/edit")
	public String initEditForm(@PathVariable int listId, Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList gameList = gameListRepository.findById(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!gameList.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		model.addAttribute("gameList", gameList);
		model.addAttribute("listTypes", GameList.ListType.values());
		return "gamelists/createOrUpdateGameListForm";
	}

	// -------------------------------------------------------------------------
	// UPDATE — process form
	// -------------------------------------------------------------------------
	@PostMapping("/{listId:\\d+}/edit")
	public String processEditForm(@PathVariable int listId,
								  @Valid GameList gameList, BindingResult result,
								  Model model, Principal principal,
								  RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			model.addAttribute("listTypes", GameList.ListType.values());
			return "gamelists/createOrUpdateGameListForm";
		}
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList existing = gameListRepository.findById(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!existing.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		existing.setListName(gameList.getListName());
		existing.setDescription(gameList.getDescription());
		existing.setListType(gameList.getListType());
		existing.setIsPublic(gameList.getIsPublic());
		gameListRepository.save(existing);
		redirectAttributes.addFlashAttribute("messageSuccess", "List updated!");
		return "redirect:/collector/lists/" + listId;
	}

	// -------------------------------------------------------------------------
	// DELETE
	// -------------------------------------------------------------------------
	@PostMapping("/{listId:\\d+}/delete")
	public String deleteList(@PathVariable int listId, Principal principal,
							 RedirectAttributes redirectAttributes) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList gameList = gameListRepository.findById(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!gameList.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		gameListRepository.delete(gameList);
		redirectAttributes.addFlashAttribute("messageSuccess", "List \"" + gameList.getListName() + "\" deleted.");
		return "redirect:/collector/lists";
	}

	// -------------------------------------------------------------------------
	// ADD GAME FROM GAME DETAIL PAGE (dropdown form)
	// -------------------------------------------------------------------------
	@PostMapping("/add-game")
	public String addGameFromDetail(@RequestParam int gameId,
									@RequestParam int listId,
									Principal principal,
									RedirectAttributes redirectAttributes) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList gameList = gameListRepository.findByIdWithGames(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!gameList.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		gameList.getGames().add(game);
		gameListRepository.save(gameList);
		redirectAttributes.addFlashAttribute("messageSuccess",
			"\"" + game.getName() + "\" added to \"" + gameList.getListName() + "\"!");
		return "redirect:/games/" + gameId;
	}
	@PostMapping("/{listId:\\d+}/games/add")
	public String addGame(@PathVariable int listId,
						  @RequestParam int gameId,
						  Principal principal,
						  RedirectAttributes redirectAttributes) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList gameList = gameListRepository.findByIdWithGames(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!gameList.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		gameList.getGames().add(game);
		gameListRepository.save(gameList);
		redirectAttributes.addFlashAttribute("messageSuccess", game.getName() + " added to list!");
		return "redirect:/collector/lists/" + listId;
	}

	// -------------------------------------------------------------------------
	// REMOVE GAME FROM LIST
	// -------------------------------------------------------------------------
	@PostMapping("/{listId:\\d+}/games/{gameId:\\d+}/remove")
	public String removeGame(@PathVariable int listId,
							 @PathVariable int gameId,
							 Principal principal,
							 RedirectAttributes redirectAttributes) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		GameList gameList = gameListRepository.findByIdWithGames(listId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!gameList.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		gameList.getGames().removeIf(g -> g.getId().equals(gameId));
		gameListRepository.save(gameList);
		redirectAttributes.addFlashAttribute("messageSuccess", "Game removed from list.");
		return "redirect:/collector/lists/" + listId;
	}
}
