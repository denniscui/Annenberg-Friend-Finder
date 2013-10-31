package com.harvard.annenberg;

/*
 * This is a food.
 */
public class FoodItem {
	private String meal;
	private String category;
	private String recipe;
	private String name;

	public FoodItem(String meal, String category, String recipe, String name) {
		this.meal = meal;
		this.category = category;
		this.recipe = recipe;
		this.name = name;
	}

	public String getMeal() {
		return meal;
	}

	public String getCategory() {
		return category;
	}

	public String getRecipe() {
		return recipe;
	}

	public String getName() {
		return name;
	}

}
