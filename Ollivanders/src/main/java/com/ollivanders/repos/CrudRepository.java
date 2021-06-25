package com.ollivanders.repos;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface for implementing a CrudReposistory. Requires classes implementing the interface to provide
 * functionality for a variety of CRUD operations.
 * 
 * @param <T> the element type that will be accessed in the database.
 * @author Kyle Castillo
 */
public interface CrudRepository<T> {
	
	/**
	 * Creates a table of the specified object
	 */
	void createClassTable() throws NoSuchFieldException, SQLException;
	
	/**
	 * Get all returns all the data from the table.
	 * @return all entries within the database.
	 * @throws SQLException
	 */
	
	List<T> getAll() throws SQLException;
	
	/**
	 * Removes a table with the specified object.
	 * @param cascade specifies whether to allow cascade deleting or not.
	 */
	void dropClassTable(boolean cascade) throws NoSuchFieldException, SQLException;
	
	/**
	 * Saves a specified object.
	 * @param newObj
	 */
	T saveNewToClassTable(T newObj);
	
	
	/**
	 * Finds an entry by its primary key
	 * @param primaryKey is the key used to search an entry for.
	 * @return returns the object with the corresponding id.
	 */
	
	T findByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException;
	
	/**
	 * Finds an entry by its column name
	 * @param columnName the column name that you wish to query by.
	 * @return a single entry
	 */
	T findByColumnName(Object columnName) throws NoSuchFieldException, SQLException;
	
	/**
	 * Finds all entries that match the columnName.
	 * @param columnName the column that you wish to query by.
	 * @return a list of all entries that match the column.
	 */
	List<T> findAllByColumnName(Object columnName) throws NoSuchFieldException, SQLException;
	
	/**
	 * Updates the given object in the database
	 * @param updatedObj this is the object that needs to be updated.
	 * @return returns true if changed, false if anything else happens.
	 */
	boolean updateByPrimaryKey(T updatedObj);
		
	/**
	 * Removes the object from the database based on the primaryKey
	 * @param primaryKey the key used to locate the entry from the database
	 * @param cascade specifies whether or not to cascade delete.
	 * @return returns true if the entry is deleted, false if anything else occurs.
	 */
	boolean deleteByPrimaryKey(Object primaryKey, boolean cascade) throws NoSuchFieldException, SQLException;
}
