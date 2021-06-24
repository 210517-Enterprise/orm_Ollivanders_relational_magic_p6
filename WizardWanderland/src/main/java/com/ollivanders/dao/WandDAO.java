package com.ollivanders.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Wand;
import com.ollivanders.services.ClassService;

public class WandDAO {
	
	private static ClassService<Wand> wandRepo = new ClassService<Wand>(Wand.class);
	private static final Logger log = LoggerFactory.getLogger(WandDAO.class);
	
	public boolean save(Wand wand) { // adds Wand if it doesn't exist or updated if it does
		try {
			wandRepo.save(wand);
			return true;
		} catch (Exception e) {
			log.warn("Unable to save: " + wand.toString());
		}
		return false;
	}
	
	public boolean delete(Wand wand) { // delete Wand if it exists
		try {
			wandRepo.delete(wand);
			return true;
		} catch (Exception e) {
			log.warn("Unable to delete: " + wand.toString());
		}
		return false;
	}
	
	public boolean exists(Wand wand) { // find if Wand exists
		try {
			return wandRepo.isInstanceSaved(wand);
		} catch (Exception e) {
			log.warn("Unable to detect: " + wand.toString());
		}
		return false;
	}
	
	// FIXME TODO 
	public List<Wand> find(Wand wand) { // find Wand by field
		try {
			return wandRepo.delete(wand);
		} catch (Exception e) {
			log.warn("Unable to find: " + wand.toString());
		}
		return null;
	}
	
}
