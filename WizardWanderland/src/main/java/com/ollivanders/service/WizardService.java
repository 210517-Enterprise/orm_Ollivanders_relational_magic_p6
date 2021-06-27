package com.ollivanders.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.dao.WizardDAO;
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
	public Wizard save(Wizard wizard) {
		Wizard result = wizard;
		try {
			log.info("Attempting to save "+ wizard.toString());
			if(!wizDAO.exists(wizard)) {
				log.info("Saving wizard is overwriting old wizard...");
			}
			else {
				log.info("Saving wizard as a new wizard...");
			}
			result = wizDAO.save(wizard);
			if(result.getId() > 0) {
				return result;
			}

			log.info("Save action was completed in WizardService");
		} catch(Exception e) {
			log.debug("Unable to save wizard");
		}
		return new Wizard();
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
	
	/**
	 * 
	 * @param wizard to check existence of (just need the id field filled)
	 * @return true if wizard exist, false otherwise
	 */
	public boolean exists(Wizard wiz) {
		return wizDAO.exists(wiz);
	}
	
	/**
	 * Find wizard using their id
	 * @param id of wizard
	 * @return Wizard object
	 */
	public Wizard findById(int id) {
		return wizDAO.findById(id);
	}
	
}
