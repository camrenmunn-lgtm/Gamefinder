package org.springframework.samples.petclinic.codesignal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

	private final RecipeService recipeService;

	private final RecipeRepository recipeRepository;

	public RecipeController(RecipeService recipeService, RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
		this.recipeService = recipeService;
	}



}
