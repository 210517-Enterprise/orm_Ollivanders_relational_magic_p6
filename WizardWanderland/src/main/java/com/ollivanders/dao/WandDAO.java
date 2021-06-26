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
	
	/**
	 * 
	 * @param wand to be saved
	 * @return true if wand was saved, false otherwise
	 */
	public Wand save(Wand wand) { // adds Wand if it doesn't exist or updated if it does
		try {
			Wand resultWand = wandRepo.save(wand);
			return resultWand;
		} catch (Exception e) {
			log.warn("Unable to save: " + wand.toString());
		}
		return new Wand();
	}
	
	/**
	 * 
	 * @param wand to be deleted from table
	 * @return true if wand was deleted, false otherwise
	 */
	public boolean delete(Wand wand) { // delete Wand if it exists
		try {
			wandRepo.delete(wand);
			return true;
		} catch (Exception e) {
			log.warn("Unable to delete: " + wand.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param wand to check existence of
	 * @return true if wand exist, false otherwise
	 */
	public boolean exists(Wand wand) { // find if Wand exists
		try {
			return wandRepo.isInstanceSaved(wand);
		} catch (Exception e) {
			log.warn("Unable to detect: " + wand.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param filterBy Map by Key,Value pair where Key=table_column_name
	 * @return List of Wands that match the filter
	 */
	public List<Wand> find(Map<String, Object> filterBy) { // find wand by fields
		try {
			return wandRepo.find(filterBy);
		} catch (Exception e) {
			log.warn("Unable to find wands using: " + filterBy.toString());
		}
		return null;
	}
	
	/**
	 * 
	 * @return all Wands in the wand table
	 */
	public List<Wand> getAll() {
		try {
			return wandRepo.getAll();
		} catch (Exception e) {
			log.warn("Unable to get all wands" + e);
		}
		return new ArrayList<Wand>();
	}
	
}