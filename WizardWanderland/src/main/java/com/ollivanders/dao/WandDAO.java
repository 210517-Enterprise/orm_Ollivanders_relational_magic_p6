package com.ollivanders.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public List<Wand> find(Map<String, Object> filterBy) { // find wand by fields
		try {
			return wandRepo.find(filterBy);
		} catch (Exception e) {
			log.warn("Unable to find wands using: " + filterBy.toString());
		}
		return null;
	}
	
	public List<Wand> getAll() {
		try {
			return wandRepo.getAll();
		} catch (Exception e) {
			log.warn("Unable to get all wands" + e);
		}
		return new ArrayList<Wand>();
	}
	
}