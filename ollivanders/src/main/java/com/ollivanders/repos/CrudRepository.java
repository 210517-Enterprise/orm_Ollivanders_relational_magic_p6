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
	 * Saves a specified object.
	 * @param newObj
	 */
	void saveNewToClassTable(T newObj);
	
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
	
	/**
	 * Finds an etry by its primary key from a table
	 * @param primaryKey is the key used to search an entry for.
	 * @param table is the table to be searched through.
	 * @return returns the object with the corresponding id.
	 */
	
	T findByPrimaryKey(Object primaryKey, Object table) throws NoSuchFieldException, SQLException;
	
	/**
	 * Updates the given object in the database
	 * @param updatedObj this is the object that needs to be updated.
	 * @return returns true if changed, false if anything else happens.
	 */
	boolean updateByPrimaryKey(T updatedObj);
	
	/**
	 * Updates the given object in the database within a table.
	 * @param updatedObj this is the object that needs to be updated.
	 * @param tableObj this is the table where the object is located.
	 * @return returns true if changed, false if anything else happens.
	 */
	boolean updateByPrimaryKey(T updatedObj, T tableObj);
	
	/**
	 * Removes the object from the database based on the primaryKey
	 * @param primaryKey the key used to locate the entry from the database
	 * @return returns true if the entry is deleted, false if anything else occurs.
	 */
	boolean deleteByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException;
	
	/**
	 * Removes the object from the database based on the primaryKey and table.
	 * @param primaryKey the key used to locate the entry from the database
	 * @param tableObj
	 * @return true if the entry is deleted, false if anything else occurs.
	 */
	boolean deleteByPrimaryKey(Object primaryKey, T tableObj) throws NoSuchFieldException, SQLException;
}
