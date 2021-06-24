package com.ollivanders.dao.initialdatabase;

public class DataDriver {

	public static void main(String[] args) {
		// Run DDL to define database tables and structure
		DDL.createTables();
		
		// Run DML to insert data into tables
		DML.setInitialIngredients();

	}

}
