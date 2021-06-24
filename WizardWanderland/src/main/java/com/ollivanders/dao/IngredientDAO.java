package com.ollivanders.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Ingredient;
import com.ollivanders.services.ClassService;

public class IngredientDAO {
	
	private static ClassService<Ingredient> ingRepo = new ClassService<Ingredient>(Ingredient.class);
	private static final Logger log = LoggerFactory.getLogger(IngredientDAO.class);
	
	public boolean save(Ingredient ingredient) { // adds ingredient if it doesn't exist or updated if it does
		try {
			ingRepo.save(ingredient);
			return true;
		} catch (Exception e) {
			log.warn("Unable to save: " + ingredient.toString());
		}
		return false;
	}
	
	public boolean delete(Ingredient ingredient) { // delete ingredient if it exists
		try {
			ingRepo.delete(ingredient);
			return true;
		} catch (Exception e) {
			log.warn("Unable to delete: " + ingredient.toString());
		}
		return false;
	}
	
	public boolean exists(Ingredient ingredient) { // find if ingredient exists
		try {
			return ingRepo.isInstanceSaved(ingredient);
		} catch (Exception e) {
			log.warn("Unable to detect: " + ingredient.toString());
		}
		return false;
	}
	
	public List<Ingredient> find(Map<String, Object> filterBy) { // find ingredient by field
		try {
			return ingRepo.find(filterBy);
		} catch (Exception e) {
			log.warn("Unable to find ingredients using: " + filterBy.toString());
		}
		return null;
	}
	

}













