package com.ollivanders.service.test;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.ollivanders.model.Wizard;

public class WizServTest {
	private List<Wizard> wizards = new ArrayList<Wizard>();
	
	 @Before
	    public void setUp() {
	        // make sure we always have an empty list for future tests
	        wizards.clear();

	        // add some different test types using the following params: FirstName, LastName, Birthday (yyyy-mm-dd)
//	        wizards.add(new Wizard("hewwy","pottah", Date(1999,11,11)));
	        
//	        wizards.add(new Wizard());
//	        wizards.add(new Wizard("sparkly", "wine", 1));
//	        wizards.add(new Wizard("razzledazzle"));
	    }

	@Test
	    public void getWizardTest() {
	        // gets all wizard with matching birthdays from the list, using a Lambda!
	        // also, using Collectors.toList() to get all the results into a List at the end
	        final List<Wizard> wizardTest = wizards.stream().filter(w -> w.getFirstName().equals("hewwy")).collect(Collectors.toList());
	        assertEquals(wizardTest.size(), 1);
	    }
}
