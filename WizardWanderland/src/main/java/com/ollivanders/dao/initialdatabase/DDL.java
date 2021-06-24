package com.ollivanders.dao.initialdatabase;

import com.ollivanders.model.Ingredient;
import com.ollivanders.model.Wand;
import com.ollivanders.model.Wizard;
import com.ollivanders.services.ClassService;

public class DDL {
	
	// methods for generating tables using Ollivanders-Relational-Magic (ORM)
	
	public static void createTables() {
		// TODO DDL for Ingredients
		ClassService<Ingredient> ingredientService = new ClassService<Ingredient>(Ingredient.class);
		ingredientService.dropThenCreateClassTable();
	
		// TODO DDL for Wizards
		ClassService<Wizard> wizardService = new ClassService<Wizard>(Wizard.class);
		wizardService.dropThenCreateClassTable();
		
		// TODO DDL for Wands
		ClassService<Wand> wandService = new ClassService<Wand>(Wand.class);
		wandService.dropThenCreateClassTable();
	}

	
}





