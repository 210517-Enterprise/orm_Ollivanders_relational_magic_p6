package com.ollivanders.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.dao.IngredientDAO;
import com.ollivanders.model.Ingredient;

public class IngredientsService {
	
	private static final Logger log = LoggerFactory.getLogger(IngredientsService.class);
	private static IngredientDAO ingDAO = new IngredientDAO();
	
	/**
	 * 
	 * @return all ingredients from ingredient table
	 */
	public List<Ingredient> getAll() {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		try {
			ingredients = ingDAO.getAll();
			log.info("Grabbed all ingredients");
		} catch (Exception e) {
			log.debug("Unable to grab all ingredients");
		}
		return ingredients;
		
	}
	
	/**
	 * 
	 * @param type of ingredient (wood/core)
	 * @return List of Ingredients of specified type
	 */
	public List<Ingredient> getByType(String type){
		
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		try {
			Map<String, Object> typeMap = new HashMap<String, Object>();
			typeMap.put("type", type);
			
			ingredients = ingDAO.find(typeMap);
			
			log.debug("Find by type: " + type + " was successful.");
		} catch (Exception e) {
			log.debug("Find by type: " + type + " was not completed.");
		}
		
		return ingredients;
	}
	
	/**
	 * 
	 * @param ingredient to be saved to database (either new or updated)
	 * @return true if ingredient was saved, false otherwise
	 */
	public boolean save(Ingredient ingredient) {
		boolean result = false;
		try {
			log.info("Attempting to save "+ ingredient.toString());
			if(!ingDAO.exists(ingredient)) {
				log.info("Saving ingredient is overwriting old ingredient...");
			}
			else {
				log.info("Saving ingredient as a new ingredient...");
			}
			result = (ingDAO.save(ingredient).getName() != "placeholder");
			log.info("Save action was completed in IngredientsService");
		} catch(Exception e) {
			log.debug("Unable to save ingredient");
		}
		return result;
	}
	
	/**
	 * 
	 * @param ingredient to be deleted from database table
	 * @return true if ingredient was successfully removed, false otherwise
	 */
	public boolean delete(Ingredient ingredient) {
		boolean result = false;
		try {
			log.info("Attempting to delete : " + ingredient);
			result = ingDAO.delete(ingredient);
			log.info("Delete was successful");
		} catch (Exception e) {
			log.debug("Unable to delete Ingredient");
		}
		return result;
	}
	
}




















