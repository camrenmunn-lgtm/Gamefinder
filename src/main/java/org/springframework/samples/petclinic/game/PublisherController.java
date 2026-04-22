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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/publishers")
public class PublisherController {

	private final PublisherRepository publisherRepository;
	private final GameRepository gameRepository;

	public PublisherController(PublisherRepository publisherRepository,
							   GameRepository gameRepository) {
		this.publisherRepository = publisherRepository;
		this.gameRepository = gameRepository;
	}

	// -------------------------------------------------------------------------
	// READ — paginated list
	// -------------------------------------------------------------------------
	@GetMapping
	public String listPublishers(@RequestParam(defaultValue = "1") int page, Model model) {
		Pageable pageable = PageRequest.of(page - 1, 10);
		Page<Publisher> publisherPage = publisherRepository.findAll(pageable);
		model.addAttribute("publishers", publisherPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", publisherPage.getTotalPages());
		model.addAttribute("totalItems", publisherPage.getTotalElements());
		return "publishers/publishersList";
	}

	// -------------------------------------------------------------------------
	// READ — single publisher detail (uses eager fetch to avoid LazyInitializationException)
	// -------------------------------------------------------------------------
	@GetMapping("/{publisherId:\\d+}")
	public String viewPublisher(@PathVariable int publisherId, Model model) {
		Publisher publisher = publisherRepository.findByIdWithGames(publisherId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
		model.addAttribute("publisher", publisher);
		return "publishers/publisherDetail";
	}

	// -------------------------------------------------------------------------
	// CREATE — show form
	// -------------------------------------------------------------------------
	@GetMapping("/new")
	public String initCreateForm(Model model) {
		model.addAttribute("publisher", new Publisher());
		return "publishers/createOrUpdatePublisherForm";
	}

	// -------------------------------------------------------------------------
	// CREATE — process form
	// -------------------------------------------------------------------------
	@PostMapping("/new")
	public String processCreateForm(@Valid Publisher publisher, BindingResult result,
									RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "publishers/createOrUpdatePublisherForm";
		}
		try {
			publisherRepository.save(publisher);
		} catch (DataIntegrityViolationException e) {
			result.rejectValue("name", "duplicate", "A publisher with this name already exists");
			return "publishers/createOrUpdatePublisherForm";
		}
		redirectAttributes.addFlashAttribute("messageSuccess",
			"Publisher \"" + publisher.getName() + "\" created!");
		return "redirect:/admin/publishers";
	}

	// -------------------------------------------------------------------------
	// UPDATE — show form
	// -------------------------------------------------------------------------
	@GetMapping("/{publisherId:\\d+}/edit")
	public String initEditForm(@PathVariable int publisherId, Model model) {
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("publisher", publisher);
		return "publishers/createOrUpdatePublisherForm";
	}

	// -------------------------------------------------------------------------
	// UPDATE — process form
	// -------------------------------------------------------------------------
	@PostMapping("/{publisherId:\\d+}/edit")
	public String processEditForm(@PathVariable int publisherId,
								  @Valid Publisher publisher, BindingResult result,
								  RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "publishers/createOrUpdatePublisherForm";
		}
		Publisher existing = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		existing.setName(publisher.getName());
		existing.setPublisherCountry(publisher.getPublisherCountry());
		existing.setFoundedYear(publisher.getFoundedYear());
		existing.setPublisherSite(publisher.getPublisherSite());
		existing.setPublisherDescription(publisher.getPublisherDescription());
		try {
			publisherRepository.save(existing);
		} catch (DataIntegrityViolationException e) {
			result.rejectValue("name", "duplicate", "A publisher with this name already exists");
			return "publishers/createOrUpdatePublisherForm";
		}
		redirectAttributes.addFlashAttribute("messageSuccess", "Publisher updated!");
		return "redirect:/admin/publishers/" + publisherId;
	}

	// -------------------------------------------------------------------------
	// DELETE
	// -------------------------------------------------------------------------
	@PostMapping("/{publisherId:\\d+}/delete")
	public String deletePublisher(@PathVariable int publisherId,
								  RedirectAttributes redirectAttributes) {
		Publisher publisher = publisherRepository.findById(publisherId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		try {
			publisherRepository.delete(publisher);
		} catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("messageDanger",
				"Cannot delete \"" + publisher.getName() + "\" — it still has games assigned to it.");
			return "redirect:/admin/publishers/" + publisherId;
		}
		redirectAttributes.addFlashAttribute("messageSuccess",
			"Publisher \"" + publisher.getName() + "\" deleted.");
		return "redirect:/admin/publishers";
	}
}
