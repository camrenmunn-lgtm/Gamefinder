package org.springframework.samples.petclinic.review;

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
public class ReviewController {

	private final ReviewRepository reviewRepository;
	private final GameRepository gameRepository;
	private final UserService userService;

	public ReviewController(ReviewRepository reviewRepository,
							GameRepository gameRepository,
							UserService userService) {
		this.reviewRepository = reviewRepository;
		this.gameRepository = gameRepository;
		this.userService = userService;
	}

	// -------------------------------------------------------------------------
	// READ — all reviews by current user
	// -------------------------------------------------------------------------
	@GetMapping("/collector/reviews")
	public String myReviews(Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		List<Review> reviews = reviewRepository.findByUserId(user.getId());
		model.addAttribute("reviews", reviews);
		return "reviews/myReviews";
	}

	// -------------------------------------------------------------------------
	// CREATE — show form (linked from game detail page)
	// -------------------------------------------------------------------------
	@GetMapping("/games/{gameId:\\d+}/reviews/new")
	public String initCreateForm(@PathVariable int gameId, Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		// check they haven't already reviewed this game
		reviewRepository.findByUserIdAndGameId(user.getId(), gameId).ifPresent(r -> {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reviewed this game");
		});
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		Review review = new Review();
		model.addAttribute("review", review);
		model.addAttribute("game", game);
		return "reviews/createOrUpdateReviewForm";
	}

	// -------------------------------------------------------------------------
	// CREATE — process form
	// -------------------------------------------------------------------------
	@PostMapping("/games/{gameId:\\d+}/reviews/new")
	public String processCreateForm(@PathVariable int gameId,
									@Valid Review review, BindingResult result,
									Model model, Principal principal,
									RedirectAttributes redirectAttributes) {
		Game game = gameRepository.findById(gameId);
		if (game == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		System.out.println("Errors: " + result.getAllErrors());
		if (result.hasErrors()) {
			model.addAttribute("game", game);
			return "reviews/createOrUpdateReviewForm";
		}
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		review.setUser(user);
		review.setGame(game);
		reviewRepository.save(review);
		redirectAttributes.addFlashAttribute("messageSuccess", "Review posted!");
		return "redirect:/games/" + gameId;
	}

	// -------------------------------------------------------------------------
	// UPDATE — show form
	// -------------------------------------------------------------------------
	@GetMapping("/collector/reviews/{reviewId:\\d+}/edit")
	public String initEditForm(@PathVariable int reviewId, Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!review.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		model.addAttribute("review", review);
		model.addAttribute("game", review.getGame());
		return "reviews/createOrUpdateReviewForm";
	}

	// -------------------------------------------------------------------------
	// UPDATE — process form
	// -------------------------------------------------------------------------
	@PostMapping("/collector/reviews/{reviewId:\\d+}/edit")
	public String processEditForm(@PathVariable int reviewId,
								  @Valid Review review, BindingResult result,
								  Model model, Principal principal,
								  RedirectAttributes redirectAttributes) {
		Review existing = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (result.hasErrors()) {
			model.addAttribute("game", existing.getGame());
			return "reviews/createOrUpdateReviewForm";
		}
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		if (!existing.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		existing.setRating(review.getRating());
		existing.setTitle(review.getTitle());
		existing.setBody(review.getBody());
		reviewRepository.save(existing);
		redirectAttributes.addFlashAttribute("messageSuccess", "Review updated!");
		return "redirect:/collector/reviews";
	}

	// -------------------------------------------------------------------------
	// DELETE
	// -------------------------------------------------------------------------
	@PostMapping("/collector/reviews/{reviewId:\\d+}/delete")
	public String deleteReview(@PathVariable int reviewId, Principal principal,
							   RedirectAttributes redirectAttributes) {
		User user = userService.findByEmail(principal.getName())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (!review.getUser().getId().equals(user.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		reviewRepository.delete(review);
		redirectAttributes.addFlashAttribute("messageSuccess", "Review deleted.");
		return "redirect:/collector/reviews";
	}
}
