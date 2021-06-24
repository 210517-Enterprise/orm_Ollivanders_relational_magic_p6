package com.ollivanders.services;

import java.lang.reflect.Field;

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
		Person pTable = new Person();
		Class pClass = pTable.getClass();
		ClassService<Person> cs = new ClassService<>(Person.class);
		
		cs.dropClassTable();   //This should remove the class table
		cs.createClassTable(); //This should make a person table
		
		cs.save(p);
	}
}
