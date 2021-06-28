package com.ollivanders.presentation;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Ingredient;
import com.ollivanders.service.IngredientsService;

public class BuildWandPortal {
	
	private Ingredient wood;
	private Ingredient core;
	
	private static final Logger log = LoggerFactory.getLogger(BuildWandPortal.class);
	
	/**
	 * Method to control user flow of selecting wand ingredients for a wand
	 * @param scan for console inputs
	 */
	public void run(Scanner scan) {
		
		log.info("Using BuildWandPortal run method...");
		
		this.wood = new Ingredient();
		
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
				String response = "yes";
				do {
					System.out.println("Ingredient name: ");
					String iName = scan.nextLine();
					
					Ingredient ingredient = ingServ.getIngredient(iName);
					if(ingredient.getName() != "placeholder") {
						if(ingredient.getType().equals("wood")) {
							this.wood = ingredient;
							log.info("this.wood set to: " + ingredient.toString());
						}
						else if(ingredient.getType().equals("core")) {
							this.core = ingredient;
							log.info("this.core set to: " + ingredient.toString());
						}
						else { // this shouldn't ever occur
							log.error("ingredient has an invalid type: " + ingredient.getType());
						}
					}
					
					System.out.println("Your current wand is wood="+this.wood.getName() + 
														" and core="+ this.core.getName() +
														" for a cost of £"+(this.wood.getCost()+this.core.getCost()));
					System.out.println("");
					System.out.println("Do you want to pick another/different ingredient?(yes/no)");
					response = scan.nextLine();
					System.out.println("");
				} while(response == "yes" | response == "y");
				System.out.println("");
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
		return;
	}
	
	/**
	 * BuildWandPortal Default Constructor
	 */
	public BuildWandPortal() {
		super();
		this.wood = new Ingredient("wood");
		this.core = new Ingredient("core");
	}
	
	/**
	 * Find the cost of the proposed wand based off the two ingredients selected
	 * @return Total cost of the wand
	 */
	public int wandCost() {
		return (this.wood.getCost()+this.core.getCost());
	}
	
	/**
	 * 
	 * @return current wand wood selection
	 */
	public Ingredient getWood() {
		return wood;
	}

	/**
	 * Set wood type for wand build
	 * @param wood
	 */
	public void setWood(Ingredient wood) {
		this.wood = wood;
	}
	
	
	/**
	 * 
	 * @return current wand core selection
	 */
	public Ingredient getCore() {
		return core;
	}
	
	/**
	 * Set core type for wand build
	 * @param core
	 */
	public void setCore(Ingredient core) {
		this.core = core;
	}
	
	/**
	 * Console print method for showing wand building options
	 */
	public static void showBuildOptions() {
		System.out.println("");
		System.out.println("==============================");
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
	
	/**
	 * Print all ingredients in a pseudo table format
	 * @param ingredients to be printed
	 */
	public static void printIngredientsOf(List<Ingredient> ingredients) {
		System.out.println("     Name     | type | Cost ");
		ingredients.forEach((i) -> System.out.println(i.getName() + " | " 
														+ i.getType() + " | £" 
														+ i.getCost()));
		
	}
	
	
}
