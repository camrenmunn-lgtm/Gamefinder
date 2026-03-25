package org.springframework.samples.petclinic.codesignal;

import java.util.List;

public interface RecipeService {
	List<Recipe> findByCategoryAndDietaryPreferenceIgnoreCase(String category, String dietaryPreference);
}
