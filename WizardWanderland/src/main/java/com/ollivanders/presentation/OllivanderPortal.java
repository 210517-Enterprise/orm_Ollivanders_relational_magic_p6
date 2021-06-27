package com.ollivanders.presentation;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Ingredient;
import com.ollivanders.model.Wand;
import com.ollivanders.model.Wizard;
import com.ollivanders.service.IngredientsService;
import com.ollivanders.service.WandService;
import com.ollivanders.service.WizardService;

public class OllivanderPortal {
	
	private static final Logger log = LoggerFactory.getLogger(OllivanderPortal.class);
	
	/**
	 * Method to control flow of Ollivander's options
	 * @param scan for console inputs
	 */
	public static void run(Scanner scan) {
		
		log.info("Using OllivanderPortal run method...");
		
		WizardService wizService = new WizardService();
		IngredientsService ingService = new IngredientsService();
		WandService wandService = new WandService();
		
		int adminOption = 0;
		do {
			showAdminOptions();
			
			adminOption = scan.nextInt();
			scan.nextLine();
			
			switch(adminOption) {
			case 1: // View All Ingredients
				List<Ingredient> ingredients = ingService.getAll();
				printIngredientsOf(ingredients);
				break;
			case 2: // View All Wizards
				List<Wizard> wizards = wizService.getAll();
				printWizardsOf(wizards);
				break;
			case 3: // View All Wands
				List<Wand> wands = wandService.getAll();
				printWandsOf(wands);
				break;
			case 4: // Return to Store Options
				adminOption = -50;
				break;
			default: // catch any other input
				adminOption = 0;
				System.out.println("Please select a valid option (1-4).");
			}
			
			
			
		} while(adminOption >= 0 || adminOption <=4);
		
		log.info("Leaving OllivanderPortal run method");
		System.out.println("Returning to Store Options");
	}
	
	/**
	 * Console print method for showing wand building options
	 */
	public static void showAdminOptions() {
		System.out.println("");
		System.out.println("================================");
		System.out.println("+++ Ollivander Admin Options +++");
		System.out.println("================================");
		System.out.println("[1] View All Ingredients");
		System.out.println("[2] View All Wizards");
		System.out.println("[3] View All Wands");
		System.out.println("[4] Return to Store Options");
		System.out.println("");
		System.out.println("Please Select an Option: ");
		System.out.println("");
	}
	
	/**
	 * Print all ingredients in a pseudo table format
	 * @param ingredients to be printed
	 */
	public static void printIngredientsOf(List<Ingredient> ingredients) {
		System.out.println("=================================");
		System.out.println("+++ All Available Ingredients +++");
		System.out.println("=================================");
		System.out.println("     Name     | type | Cost ");
		ingredients.forEach((i) -> System.out.println(i.getName() + " | " 
														+ i.getType() + " | £" 
														+ i.getCost()));
		System.out.println("");
	}
	
	/**
	 * Print all wizards in a pseudo table format
	 * @param wizards to be printed
	 */
	public static void printWizardsOf(List<Wizard> wizards) {
		System.out.println("==============================");
		System.out.println("+++ All Registered Wizards +++");
		System.out.println("==============================");
		System.out.println(" ID |     Name      |  Birthdate ");
		wizards.forEach((w) -> System.out.println( w.getId() + " | "
													+ w.getFirstName() + " "
													+ w.getLastName() + " | "
													+ w.getBirthdate()));
		System.out.println("");
	}
	
	/**
	 * Print all wands in a pseudo table format
	 * @param wands to be printed
	 */
	public static void printWandsOf(List<Wand> wands) {
		System.out.println("============================");
		System.out.println("+++ All Registered Wands +++");
		System.out.println("============================");
		System.out.println(" ID |   Wood   |   Core   | Length | Cost | Wizard ID");
		wands.forEach((w) -> System.out.println( w.getId() + " | "
													+ w.getWood() + " | "
													+ w.getCore() + " | "
													+ w.getLength() + " in | £"
													+ w.getCost() + " | "
													+ w.getWizard_id()));
		System.out.println("");
	}
	
}


