package com.ollivanders.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.model.Ingredient;
import com.ollivanders.services.ClassService;

public class IngredientDAO {
	
	private static ClassService<Ingredient> ingRepo = new ClassService<Ingredient>(Ingredient.class);
	private static final Logger log = LoggerFactory.getLogger(IngredientDAO.class);
	
	/**
	 * 
	 * @param ingredient to be saved
	 * @return true if ingredient was saved, false otherwise
	 */
	public boolean save(Ingredient ingredient) { // adds ingredient if it doesn't exist or updated if it does
		try {
			ingRepo.save(ingredient);
			return true;
		} catch (Exception e) {
			log.warn("Unable to save: " + ingredient.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param ingredient to be deleted from table
	 * @return true if ingredient was deleted, false otherwise
	 */
	public boolean delete(Ingredient ingredient) { // delete ingredient if it exists
		try {
			ingRepo.delete(ingredient);
			return true;
		} catch (Exception e) {
			log.warn("Unable to delete: " + ingredient.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param ingredient to check existence of
	 * @return true if ingredient exist, false otherwise
	 */
	public boolean exists(Ingredient ingredient) { // find if ingredient exists
		try {
			return ingRepo.isInstanceSaved(ingredient);
		} catch (Exception e) {
			log.warn("Unable to detect: " + ingredient.toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param filterBy Map by Key,Value pair where Key=table_column_name
	 * @return List of Ingredients that match the filter
	 */
	public List<Ingredient> find(Map<String, Object> filterBy) { // find ingredient by field
		try {
			return ingRepo.find(filterBy);
		} catch (Exception e) {
			log.warn("Unable to find ingredients using: " + filterBy.toString());
		}
		return new ArrayList<Ingredient>();
	}
	
	/**
	 * 
	 * @return all Ingredients in the ingredient table
	 */
	public List<Ingredient> getAll() {
		try {
			return ingRepo.getAll();
		} catch (Exception e) {
			log.warn("Unable to get all ingredients" + e);
		}
		return new ArrayList<Ingredient>();
	}
	

}












