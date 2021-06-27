package com.ollivanders.presentation;

import java.sql.Date;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Wand;
import com.ollivanders.model.Wizard;
import com.ollivanders.service.WandService;
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
		
		/* ===========================================
		 *         Handle Wizard Verifications
		 * ===========================================
		 */
		WizardService wizService = new WizardService();
		Wizard wizard = new Wizard();
		
		System.out.println("Do you have a Wizard ID?(yes/no)");
		String haveId = scan.nextLine();
		
		if(haveId.toLowerCase() == "no" || haveId.toLowerCase() == "n") {
			wizard = verifyWizard(scan);
			wizService.save(wizard);
		}
		else {
			boolean loop = true;
			do {
				wizard.setId(-1);
				System.out.println("Enter your wizard ID: ");
				int wizId = scan.nextInt();
				scan.nextLine();
				wizard.setId(wizId);
				
				if(wizService.exists(wizard)) {
					System.out.println("Please Verify Your Information(first name, surname, and birthday).");
					wizard = verifyWizard(scan);
					wizard.setId(wizId); 
					// check if entered info matches db info
					if(wizard.equals(wizService.findById(wizId))) {
						loop = false;
					}
					else {
						System.out.println("Information did not match our records...");
						System.out.println("Try again?(yes/no)");
						String response = scan.nextLine();
						if(response.toLowerCase() == "no" || response.toLowerCase() == "n") {
							System.out.println("Returning to store options...");
							return;
						}
					}
				}
			} while(loop);
		}
			
		/* ================================================
		 *                 Saving the Wand
		 * ================================================
		 */
		
		WandService wandService = new WandService();
		
		Wand customerWand = new Wand(WandBuild.getWood().getName(), WandBuild.getCore().getName(), WandBuild.wandCost(), wizard.getId());
		boolean wandLengthLoop = true;
		do {
			System.out.println("");
			System.out.println("Select a length for your wand between 9-18 inches");
			int wandLength = scan.nextInt();
			scan.nextLine();
			if(wandLength>=9 && wandLength<=18) {
				customerWand.setLength(wandLength);
				wandLengthLoop = false;
			}
			else {
				System.out.println("Invalid wand length...");
			}
		}while(wandLengthLoop);
		
		log.debug("Attempt to save the wand: " + customerWand);
		wandService.save(customerWand, wizard);
		log.debug("Wand save has been called in BuyWandPortal");
		
		System.out.println("Thank you for your wand purchase for £" + customerWand.getCost());
		
	}
	
	/**
	 * Separate code block to purely check if inputs are valid for making a Wizard obj
	 * @param scan for continuous user inputs to console
	 * @return A valid Wizard
	 */
	public static Wizard verifyWizard(Scanner scan) {
		Wizard returnWizard = new Wizard();
		boolean valid = false;
		
		do {
			System.out.println("");
			System.out.println("What is your first name?");
			String firstName = scan.nextLine(); // get first name
			if(!isValidName(firstName)) { // check if name is valid
				System.out.println(firstName+" is an invalid name");
				continue;
			}
			
			System.out.println("What is your surname?");
			String lastName = scan.nextLine(); // get last name
			if(!isValidName(lastName)) { // check if name is valid
				System.out.println(lastName+" is an invalid name");
				continue;
			}
			
			System.out.println("What is your birthday?(mm/dd/yyyy)");
			String birthday = scan.nextLine(); // get birthday
			if(!isValidDate(birthday)) { // check if date is valid
				System.out.println(birthday+" is an invalid date. Ensure format is yyyy-mm-dd");
				continue;
			}
			Date birthDate = Date.valueOf(birthday);
			
			returnWizard.setFirstName(firstName);
			returnWizard.setLastName(lastName);
			returnWizard.setBirthdate(birthDate);
			
			valid = true; // while loop will exit if the flow makes it this far
		} while(!valid);
		
		return returnWizard;
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
	
	/**
	 * Check if date is a valid format and value
	 * @param date to check validity of
	 * @return true if date is valid, false otherwise
	 */
	public static boolean isValidDate(String date) {
		//several if conditions to check for bad inputs
		
		if(!date.contains("-")) {return false;}
		
		String[] datearr = date.split("-");
		if(datearr.length != 3) {return false;}
		
		if(datearr[0].length() != 4) {return false;}
		if(datearr[1].length() != 2) {return false;}
		if(datearr[2].length() != 2) {return false;}
		
		int[] intarr = new int[3];
		for(int i=0 ; i<3 ; i++) {
			intarr[i] = Integer.parseInt(datearr[i]);
		}
		
		if(!(intarr[0]>=0 && intarr[0]<=2021)) {return false;}
		if(!(intarr[0]>=1 && intarr[0]<=12)) {return false;}
		if(!(intarr[1]>=1 && intarr[1]<=31)) {return false;}
		
		return true;
		
	}
	
}







