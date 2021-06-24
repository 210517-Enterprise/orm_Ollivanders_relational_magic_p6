package com.ollivanders.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Wizard;
import com.ollivanders.services.ClassService;

public class WizardDAO {
	
	private static ClassService<Wizard> wizRepo = new ClassService<Wizard>(Wizard.class);
	private static final Logger log = LoggerFactory.getLogger(WizardDAO.class);
	
	public boolean save(Wizard wizard) { // adds Wizard if it doesn't exist or updated if it does
		try {
			wizRepo.save(wizard);
			return true;
		} catch (Exception e) {
			log.warn("Unable to save: " + wizard.toString());
		}
		return false;
	}
	
	public boolean delete(Wizard wizard) { // delete Wizard if it exists
		try {
			wizRepo.delete(wizard);
			return true;
		} catch (Exception e) {
			log.warn("Unable to delete: " + wizard.toString());
		}
		return false;
	}
	
	public boolean exists(Wizard wizard) { // find if Wizard exists
		try {
			return wizRepo.isInstanceSaved(wizard);
		} catch (Exception e) {
			log.warn("Unable to detect: " + wizard.toString());
		}
		return false;
	}
	
	// FIXME TODO 
	public List<Wizard> find(Wizard wizard) { // find Wizard by field
		try {
			return wizRepo.delete(wizard);
		} catch (Exception e) {
			log.warn("Unable to find: " + wizard.toString());
		}
		return null;
	}
	
}
















