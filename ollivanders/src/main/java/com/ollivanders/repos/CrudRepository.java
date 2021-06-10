package com.ollivanders.repos;

import java.sql.SQLException;

/**
 * Interface for implementing a CrudReposistory. Requires classes implementing the interface to provide
 * functionality for a variety of CRUD operations.
 * @param <T> the element type that will be accessed in the database.
 */
public interface CrudRepository<T> {
	
	/**
	 * Creates a table of the specified object
	 */
	
	void createClassTable() throws NoSuchFieldException, SQLException;
	
	/**
	 * Removes a table with the specified object.
	 * @param tableObj is the table that is supposed to be removed.
	 * @param cascade specifies whether to allow cascade deleting or not.
	 */
	
	void removeClassTable(T tableObj, boolean cascade) throws NoSuchFieldException, SQLException;
	
	/**
	 * Saves a specified object into a specified table
	 * @param newObj is the object to be saved
	 * @param tableObj is the table of where its being saved
	 */
	
	void saveNewToClassTable(T newObj, T tableObj);
	
	/**
	 * Finds an entry by its primary key
	 * @param primaryKey is the key used to search an entry for.
	 * @return returns the object with the corresponding id.
	 */
	
	T findByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException;
}
