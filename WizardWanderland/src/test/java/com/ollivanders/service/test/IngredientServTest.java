package com.ollivanders.service.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.ollivanders.model.Ingredient;
public class IngredientServTest{
	private List<Ingredient> ingredients = new ArrayList<Ingredient>();
	
	 @Before
	    public void setUp() {
	        // make sure we always have an empty list for future tests
	        ingredients.clear();

	        // add some different test types
	        ingredients.add(new Ingredient("plasteel"));
	        ingredients.add(new Ingredient("golden"));
	        ingredients.add(new Ingredient("sparkly"));
	        ingredients.add(new Ingredient("razzledazzle"));
	    }

	@Test
	    public void sortplasteelWands() {
	        // get only plasteel wands from the list, using a Lambda!
	        // also, using Collectors.toList() to get all the results into a List at the end
	        final List<Ingredient> plasteelWands = ingredients.stream().filter(i -> i.getType().equals("plasteel")).collect(Collectors.toList());
	        assertEquals(plasteelWands.size(), 1);
	    }
	
}
