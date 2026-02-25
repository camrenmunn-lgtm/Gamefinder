package org.springframework.samples.petclinic.school;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
class SchoolController {

	private final SchoolRepository schoolRepository;

	public SchoolController(SchoolRepository schoolRepository) {
		this.schoolRepository = schoolRepository;
	}

	@GetMapping("/schools")
	public String showSchoolList(@RequestParam(defaultValue = "1") int page, Model model) {
		// Pagination setup (5 items per page)
		Pageable pageable = PageRequest.of(page - 1, 5);
		Page<School> schoolPage = schoolRepository.findAll(pageable);

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", schoolPage.getTotalPages());
		model.addAttribute("totalItems", schoolPage.getTotalElements());
		model.addAttribute("listSchools", schoolPage.getContent());

		return "schools/schoolList"; // You will need to create this HTML file
	}
}
