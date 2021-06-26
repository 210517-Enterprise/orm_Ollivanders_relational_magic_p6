package com.ollivanders.presentation;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Wizard;
import com.ollivanders.service.WizardService;


public class BuyWandPortal {
	
	private static final Logger log = LoggerFactory.getLogger(BuyWandPortal.class);
	
	/**
	 * Method to control user flow of purchasing a wand
	 * @param scan for console inputs
	 */
	public static void run(Scanner scan, BuildWandPortal WandBuild) {
		
		log.info("Using BuyWandPortal run method...");
		
		System.out.println("The proposed wand will cost £" + WandBuild.wandCost());
		
		if(WandBuild.wandCost()==0) {
			System.out.println("Your Wand is missing Ingredients...");
			return;
		}
		else if(WandBuild.getCore().getName() == "placeholder" || WandBuild.getWood().getName() == "placeholder") {
			System.out.println("Your Wand is missing Ingredients...");
			return;
		}
		
		System.out.println("Are you sure you would like to purchase the Wand?(yes/no)");
		String purchase = scan.nextLine();
		
		if(purchase.toLowerCase() != "yes" || purchase.toLowerCase() != "y") {
			System.out.println("Returning to store options...");
			return;
		}
		
		WizardService wizService = new WizardService();
		
		// loop until valid wizard is found
		boolean loop = true;
//		do {
			System.out.println("Do you have a Wizard ID?(yes/no)");
			String haveId = scan.nextLine();
			if(haveId.toLowerCase() == "no" || haveId.toLowerCase() == "n") {
				Wizard newWizard = new Wizard();
				
			}
			
			System.out.println("Enter your wizard ID: ");
			
			
			
//		} while(loop);
		
		
	}
	
	public static Wizard verifyWizard(Scanner scan) {
		Wizard returnWizard = new Wizard();
		boolean valid = true;
		
		do {
			System.out.println("");
			System.out.println("What is your first name?");
			returnWizard.setFirstName(scan.nextLine());
			
			System.out.println("What is your surname?");
			returnWizard.setLastName(scan.nextLine());
		
		
			System.out.println("What is your birthday?(mm-dd-yyyy)");
			String birthday = scan.nextLine();
		} while(valid);
		
	}
	
	
	/**
	 * Check if name is valid
	 * @param name to check validity of
	 * @return true if name is valid, false otherwise
	 */
	public static boolean isValidName(String name) {
		// taken from https://www.geeksforgeeks.org/check-if-a-string-contains-only-alphabets-in-java-using-regex/
	    return ((!name.equals(""))
	            && (name != null)
	            && (name.matches("^[a-zA-Z]*$")));
	}
	
	
	public static boolean isValidDate(String date) {
		if(date.contains("-")) {
			
		}
	}
	
}







