package com.ollivanders.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is purely used to test the functionality of the GenericClassRepository
 * as well as ClassService. This should not be implemented anywhere else within 
 * the ORM and will likely be removed upon release.
 * @author castl
 *
 */
public class TestDriver {

	public static void main(String[] args) {
		Person p = new Person("Bob",20);
		ClassService<Person> cs = new ClassService<>(Person.class);
		
		cs.dropClassTable();   //This should remove the class table
		cs.createClassTable(); //This should make a person table
		
		cs.save(p);
		
		p.setId(1); //Purely for testing
		
		p.setAge(22);
		
		cs.save(p); //Checking to see if updating works
		
		Map<String,Object> findByName = new HashMap<String,Object>();
		findByName.put("name", "Bob");
		findByName.put("age", 21);
		
		ArrayList<Person> people = cs.find(findByName);
		for(Person peep : people) {
			System.out.println("Peep found! " + peep.getName());
		}
		
		Person dude = cs.findByPrimaryKey(1);
		System.out.println(dude.getName());
		
		Person bruh = cs.findByColumnName("name");
	}
}
