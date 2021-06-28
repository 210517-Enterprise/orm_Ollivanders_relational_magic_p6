package com.ollivanders.service.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.ollivanders.model.Wand;

public class WandServTest {
	private List<Wand> wands = new ArrayList<Wand>();
	
	 @Before
	    public void setUp() {
	        // make sure we always have an empty list for future tests
	        wands.clear();

	        // add some different test types with params: wood, core, cost , wizard_id
	        wands.add(new Wand("notwood","ham",500, 33));
	        wands.add(new Wand("woodywood","cheese",500, 34));
	        wands.add(new Wand("maybewood","swiss",666, 35));
	    }

	@Test
	    public void getCoreTest() {
	        // get only wands with a ham core from the list, using a Lambda!
	        // also, using Collectors.toList() to get all the results into a List at the end
	        final List<Wand> coreTest = wands.stream().filter(w -> w.getCore().equals("ham")).collect(Collectors.toList());
	        assertEquals(coreTest.size(), 1);
	    }
}
