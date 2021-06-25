package com.ollivanders.services;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.ollivanders.repos.GenericClassReposistory;

/**
 * ClassService handles the conversion from objects and classes to SQL.
 * @author Kyle Castillo
 *
 * @param <T> The class type that ClassService will use.
 */
public class ClassService<T> {
	
	GenericClassReposistory<T> repo;
	Class<T> tClass;
	
	/**
	 * Constructor for ClassService<br>
	 * In order for a class to be used as a table in PostgresSQL it needs
	 * to have its fields annotated with @Column or @Id. Additionally this class
	 * will create an instance of GenericClassReposistory using tClass.
	 * @param tClass The representation of the class in java that will be made into a table in SQL.
	 * this will also be used to initialize GenericClassReposistory.
	 */
	public ClassService(Class<T> tClass) {
		this.repo = new GenericClassReposistory<>(tClass);
		this.tClass = tClass;
	}
	
	/**
	 * Creates a class table if one does not exist already.
	 */
	public void createClassTable() {
		repo.createClassTable();
	}
	
	/**
	 * Drops the class table regardless of foreign key references
	 */
	public void dropClassTable() {
		repo.dropClassTable(true);
	}
	
	/**
	 * Drops the class table will maintaining foreign key references
	 */
	public void dropClassTableSafe() {
		repo.dropClassTable(false);
	}
	
	/**
	 * Drops the current class table and creates a new one.
	 * @apiNote This method will always cascade delete.
	 */
	public void dropThenCreateClassTable() {
		repo.dropClassTable(true);
		repo.createClassTable();
	}
	
	/**
	 * Saves the object to the current class table.<br>
	 * If the object does not exist in the class table then its inserted.<br>
	 * If the object is in class table then it is updated.
	 * @param save the object instance being saved to tClass
	 */
	public void save(T save) {
		
		//Acquires the primary key or null if it doesn't exist.
		Object pk = getPrimaryKey(save);
		
		try {
			//If the primary key does not exist or nothing is found the instance is inserted.
			if(repo.findByPrimaryKey(pk) == null || pk == null)
				repo.saveNewToClassTable(save);
			
			//Otherwise the value is updated based on its primary key.
			else
				repo.updateByPrimaryKey(save);
		} catch (SQLException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * Deletes the object instance from the class table
     * @param delete The object instance to remove from the class table.
     * @return true if something was deleted, false otherwise
     */
    public boolean delete(T delete) {
    	
    	//Acquires the primary key based on the object instance.
        Object pk = getPrimaryKey(delete);

        boolean deleted = false;

        try {
        	//Checks to see if the object is saved within the database.
            if (isInstanceSaved(delete)) {
            	//Removes the object from the database, cascading on deletion.
                repo.deleteByPrimaryKey(pk, true);
                deleted = true;
            }
        } catch (SQLException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return deleted;
    }
    
    /**
     * Searches the database for the object instance.
     * @param search the object to search for
     * @return returns true if the object is found, false otherwise
     */
    private boolean isInstanceSaved(T search) {
    	
    	//Acquires the primary key based on the object instance.
        Object pk = getPrimaryKey(search);

        try {
        	//Returns the result of finding by the primary key.
            return (repo.findByPrimaryKey(pk) != null);
        } catch (SQLException | NoSuchFieldException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return false;
    }
    
    /**
     * Retrieves a list of objects based on column names and values. This method uses a key value pair to create a complex query.
     * The map's key represents the column to search by while the value is the data within the column.
     * @param fields A map of representing the column to search by followed by the value in the column.
     * @return ArrayList<T> a list of object instances acquired from the query.
     */
    public ArrayList<T> find(Map<String, Object> fields){
    	return repo.searchByFields(fields);
    }
    
    /**
     * Retrieves an instance of an object within the database of the class table.
     * @param pk the primary key that will be used to query the database.
     * @return T a single object instance found by the primary key.
     */
    public T findByPrimaryKey(Object pk) {
    	try {
			return repo.findByPrimaryKey(pk);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Retrieves all object instances within the database of the class table.
     * @return ArrayList<T> a list of all object instances of class table.
     */
    public ArrayList<T> getAll(){
    	try {
			return repo.getAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
	
	/**
     * A helper method to get the primary key from an object
     * @param instance the object to get a primary key from
     * @return returns the primary key object
     */
    private Object getPrimaryKey(T instance) {
        Field pk = null;
        try {
            pk = repo.getPKField();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        if (Modifier.isPrivate(pk.getModifiers())) {
            pk.setAccessible(true);
        }

        try {
            return pk.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
