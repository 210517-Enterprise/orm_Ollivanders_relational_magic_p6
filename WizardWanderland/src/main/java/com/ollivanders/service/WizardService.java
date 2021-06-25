package com.ollivanders.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.dao.WizardDAO;
import com.ollivanders.model.Ingredient;
import com.ollivanders.model.Wizard;

public class WizardService {
	
	private static final Logger log = LoggerFactory.getLogger(WizardService.class);
	private static WizardDAO wizDAO = new WizardDAO();
	
	/**
	 * 
	 * @return all wizards from wizard table
	 */
	public List<Wizard> getAll() {
		List<Wizard> wizards = new ArrayList<Wizard>();
		try {
			wizards = wizDAO.getAll();
			log.info("Grabbed all wizards");
		} catch (Exception e) {
			log.debug("Unable to grab all wizards");
		}
		return wizards;
		
	}
	
	/**
	 * 
	 * @param wizard to be saved to database (either new or updated)
	 * @return true if wizard was saved, false otherwise
	 */
	public boolean save(Wizard wizard) {
		boolean result = false;
		try {
			log.info("Attempting to save "+ wizard.toString());
			if(!wizDAO.exists(wizard)) {
				log.info("Saving wizard is overwriting old wizard...");
			}
			else {
				log.info("Saving wizard as a new wizard...");
			}
			result = wizDAO.save(wizard);
			log.info("Save action was completed in WizardService");
		} catch(Exception e) {
			log.debug("Unable to save wizard");
		}
		return result;
	}
	
	/**
	 * 
	 * @param wizard to be deleted from database table
	 * @return true if wizard was successfully removed, false otherwise
	 */
	public boolean delete(Wizard wizard) {
		boolean result = false;
		try {
			log.info("Attempting to delete : " + wizard);
			result = wizDAO.delete(wizard);
			log.info("Delete was successful");
		} catch (Exception e) {
			log.debug("Unable to delete wizard");
		}
		return result;
	}
	
	
}
