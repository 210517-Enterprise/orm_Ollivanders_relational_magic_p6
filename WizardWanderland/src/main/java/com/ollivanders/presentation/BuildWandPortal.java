package com.ollivanders.presentation;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Ingredient;
import com.ollivanders.service.IngredientsService;

public class BuildWandPortal {
	
	private String wood;
	private String core;
	
	private static final Logger log = LoggerFactory.getLogger(BuildWandPortal.class);
	
	/**
	 * Method to control user flow of selecting wand ingredients for a wand
	 * @param scan for console inputs
	 */
	public static void run(Scanner scan) {
		
		log.info("Using BuildWandPortal run method...");
		
		int userOption = 0;
		IngredientsService ingServ = new IngredientsService();
		
		do {
			showBuildOptions();
			
			userOption = scan.nextInt();
			scan.nextLine();
			
			switch(userOption) {
			case 1: // View All Ingredients"
				List<Ingredient> allIngredients = ingServ.getAll();
				printIngredientsOf(allIngredients);
				break;
			case 2: // View All Available Woods
				List<Ingredient> woodIngredients = ingServ.getByType("wood");
				printIngredientsOf(woodIngredients);
				break; 
			case 3: // View All Available Cores
				List<Ingredient> coreIngredients = ingServ.getByType("core");
				printIngredientsOf(coreIngredients);
				break;
			case 4: // Add Ingredient to Wand
				
				break;
			case 5: // Exit Builder (return to store options)
				userOption = -50;
				break;
			default: // invalid input
				userOption = 0;
				System.out.println("Invalid Input, select an option 1 - 5");
		}

		} while(userOption>=0 && userOption<=5);
		
		log.info("Leaving Wand Builder Portal");
		
	}
	
	
	/**
	 * 
	 * @return current wand wood selection
	 */
	public String getWood() {
		return wood;
	}

	/**
	 * Set wood type for wand build
	 * @param wood
	 */
	public void setWood(String wood) {
		this.wood = wood;
	}
	
	
	/**
	 * 
	 * @return current wand core selection
	 */
	public String getCore() {
		return core;
	}
	
	/**
	 * Set core type for wand build
	 * @param core
	 */
	public void setCore(String core) {
		this.core = core;
	}
	
	/**
	 * Console print method for showing wand building options
	 */
	public static void showBuildOptions() {
		System.out.println("");
		System.out.println("+++ Wand Building Options +++");
		System.out.println("==============================");
		System.out.println("[1] View All Ingredients");
		System.out.println("[2] View All Available Woods");
		System.out.println("[3] View All Available Cores");
		System.out.println("[4] Add Ingredient to Wand");
		System.out.println("[5] Exit Builder (return to store options)");
		System.out.println("");
		System.out.println("Please Select an Option: ");
		System.out.println("");
	}
	
	public static void printIngredientsOf(List<Ingredient> ingredients) {
		System.out.println("     Name     | type | Cost ");
		ingredients.forEach((i) -> System.out.println(i.getName() + " | " 
														+ i.getType() + " | £" 
														+ i.getCost()));
		
	}
	
	
}
