package com.ollivanders.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Wizard;
import com.ollivanders.services.ClassService;

public class WizardDAO {
	
	private static ClassService<Wizard> wizRepo = new ClassService<Wizard>(Wizard.class);
	private static final Logger log = LoggerFactory.getLogger(WizardDAO.class);
	
	/**
	 * 
	 * @param wizard to be saved
	 * @return true if wizard was saved, false otherwise
	 */
	public boolean save(Wizard wizard) { // adds Wizard if it doesn't exist or updated if it does
		try {
			wizRepo.save(wizard);
			return true;
		} catch (Exception e) {
			log.warn("Unable to save: " + wizard.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param wizard to be deleted from table
	 * @return true if wizard was deleted, false otherwise
	 */
	public boolean delete(Wizard wizard) { // delete Wizard if it exists
		try {
			wizRepo.delete(wizard);
			return true;
		} catch (Exception e) {
			log.warn("Unable to delete: " + wizard.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param wizard to check existence of
	 * @return true if wizard exist, false otherwise
	 */
	public boolean exists(Wizard wizard) { // find if Wizard exists
		try {
			return wizRepo.isInstanceSaved(wizard);
		} catch (Exception e) {
			log.warn("Unable to detect: " + wizard.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param filterBy Map by Key,Value pair where Key=table_column_name
	 * @return List of Wizards that match the filter
	 */
	public List<Wizard> find(Map<String, Object> filterBy) { // find wizard by field
		try {
			return wizRepo.find(filterBy);
		} catch (Exception e) {
			log.warn("Unable to find wizards using: " + filterBy.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @return all Wizards in the wizard table
	 */
	public List<Wizard> getAll() {
		try {
			return wizRepo.getAll();
		} catch (Exception e) {
			log.warn("Unable to get all ingredients" + e);
		}
		return new ArrayList<Wizard>();
	}
}
















