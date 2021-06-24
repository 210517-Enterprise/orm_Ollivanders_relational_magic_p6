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
	
	public List<Ingredient> getAll() {
		

		// TODO return all using DAO layer
		
		return new ArrayList<Ingredient>();
		
	}
	
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
	
	
}




















