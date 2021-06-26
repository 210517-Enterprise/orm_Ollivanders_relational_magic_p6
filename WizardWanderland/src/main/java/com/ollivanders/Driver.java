package com.ollivanders;

import java.sql.SQLException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Driver {
	
	private static final Logger log = LoggerFactory.getLogger(Driver.class);
	
	public static void main(String[] args) {
		
		log.info("Application Started");
		
		System.out.println("Welcome to Ollivander's Wizard Wanderland!");
		
		Scanner input = new Scanner(System.in);
		
		try {
			run(input);
		} catch (Exception e2) {
			if (e2 instanceof SQLException) {
				log.debug("SQL ERROR: " + e2.getLocalizedMessage());
				System.out.println("SQL ERROR: " + e2.getLocalizedMessage());
				System.exit(0);
			}
		}
		
		log.info("Application Stopped");
		System.out.println("Thank you for coming to Ollie's!");
		input.close();
		System.exit(0);
	}

	/**
	 * Method to show control flow of user options
	 * @param scan for console inputs
	 */
	public static void run(Scanner scan) {
		
		log.info("run method called.");
		
		// continue asking for input until user exits app
		int userOption = 0;
		do {
			showInitialOptions();
			
			userOption = scan.nextInt();
			scan.nextLine();
			
			switch(userOption) {
			case 1:
				
				break;
			case 2:
				
				break;
			case 3:
				
				break;
			case 4499:
				
			default:
				userOption = 0;
				System.out.println("Invalid input... select valid option( ex: 1 )");
			}
			
			
			
		} while(userOption>=0 && userOption<=3);
		
	}
	
	
	/**
	 * Console print method for showing initial store options
	 */
	public static void showInitialOptions() {
		System.out.println("");
		System.out.println("+++ Customer Options +++");
		System.out.println("===========================");
		System.out.println("1. View Wand Ingredients");
		System.out.println("2. Purchase Wand");
		System.out.println("3. Leave Store");
		System.out.println("");
		System.out.println("Please select an option...(1,2,3)");
		System.out.println("");
	}

}
