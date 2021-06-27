package com.ollivanders.dao.initialdatabase;

import java.util.ArrayList;
import java.util.List;

import com.ollivanders.model.Ingredient;
import com.ollivanders.services.ClassService;

public class DML {
	
	public static void setInitialIngredients() {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		System.out.println("Attempting to add Ingredients...");
		
		// add wood ingredients
		ingredients.add(new Ingredient( "wood", "Acacia", 217));
		ingredients.add(new Ingredient( "wood", "Alder", 244));
		ingredients.add(new Ingredient( "wood", "Apple", 247));
		ingredients.add(new Ingredient( "wood", "Ash", 61));
		ingredients.add(new Ingredient( "wood", "Aspen", 74));
		ingredients.add(new Ingredient( "wood", "Beech", 183));
		ingredients.add(new Ingredient( "wood", "Blackthorn", 162));
		ingredients.add(new Ingredient( "wood", "Black Walnut", 52));
		ingredients.add(new Ingredient( "wood", "Cedar", 230));
		ingredients.add(new Ingredient( "wood", "Cherry", 76));
		ingredients.add(new Ingredient( "wood", "Chestnut", 129));
		ingredients.add(new Ingredient( "wood", "Cypress", 92));
		ingredients.add(new Ingredient( "wood", "Dogwood", 215));
		ingredients.add(new Ingredient( "wood", "Ebony", 104));
		ingredients.add(new Ingredient( "wood", "English Oak", 242));
		ingredients.add(new Ingredient( "wood", "Elder", 950));
		ingredients.add(new Ingredient( "wood", "Elm", 107));
		ingredients.add(new Ingredient( "wood", "Fir", 242));
		ingredients.add(new Ingredient( "wood", "Hawthorn", 140));
		ingredients.add(new Ingredient( "wood", "Hazel", 306));
		ingredients.add(new Ingredient( "wood", "Holly", 293));
		ingredients.add(new Ingredient( "wood", "Hornbeam", 268));
		ingredients.add(new Ingredient( "wood", "Larch", 98));
		ingredients.add(new Ingredient( "wood", "Laurel", 234));
		ingredients.add(new Ingredient( "wood", "Maple", 127));
		ingredients.add(new Ingredient( "wood", "Pear", 192));
		ingredients.add(new Ingredient( "wood", "Pine", 191));
		ingredients.add(new Ingredient( "wood", "Poplar", 124));
		ingredients.add(new Ingredient( "wood", "Red Oak", 289));
		ingredients.add(new Ingredient( "wood", "Redwood", 500));
		ingredients.add(new Ingredient( "wood", "Reed", 67));
		ingredients.add(new Ingredient( "wood", "Rosewood", 230));
		ingredients.add(new Ingredient( "wood", "Rowan", 177));
		ingredients.add(new Ingredient( "wood", "Silver Lime", 127));
		ingredients.add(new Ingredient( "wood", "Spruce", 99));
		ingredients.add(new Ingredient( "wood", "Snakewood", 292));
		ingredients.add(new Ingredient( "wood", "Sugar Maple", 85));
		ingredients.add(new Ingredient( "wood", "Sycamore", 143));
		ingredients.add(new Ingredient( "wood", "Tamarack", 220));
		ingredients.add(new Ingredient( "wood", "Vine", 108));
		ingredients.add(new Ingredient( "wood", "Walnut", 239));
		ingredients.add(new Ingredient( "wood", "Willow", 138));
		ingredients.add(new Ingredient( "wood", "Yew", 108));
		
		// add core ingredients
		ingredients.add(new Ingredient( "core", "unicorn hair", 681));
		ingredients.add(new Ingredient( "core", "dragon heartstring", 466));
		ingredients.add(new Ingredient( "core", "phoenix feather", 609));
		ingredients.add(new Ingredient( "core", "veela hair", 703));
		ingredients.add(new Ingredient( "core", "thestral tail hari", 660));
		ingredients.add(new Ingredient( "core", "troll whisker", 660));
		ingredients.add(new Ingredient( "core", "kelpie hair", 530));
		ingredients.add(new Ingredient( "core", "thunderbird tail feather", 490));
		ingredients.add(new Ingredient( "core", "wampus cat hair", 703));
		ingredients.add(new Ingredient( "core", "white river monseter spine", 545));
		ingredients.add(new Ingredient( "core", "rougarou hair", 557));
		ingredients.add(new Ingredient( "core", "kneazle whiskers", 150));
		ingredients.add(new Ingredient( "core", "horned serpent horn", 377));
		ingredients.add(new Ingredient( "core", "snallygaster heartstring", 318));
		ingredients.add(new Ingredient( "core", "jackalope antler", 723));
		ingredients.add(new Ingredient( "core", "basilisk horn", 15999));
		
		// generate service obj
		ClassService<Ingredient> ingredientService = new ClassService<Ingredient>(Ingredient.class);
		
		// add items to Table
		for(Ingredient ing : ingredients) {
			ingredientService.save(ing);
		}
		System.out.println("Done Adding Ingredients");
	}
	

}





