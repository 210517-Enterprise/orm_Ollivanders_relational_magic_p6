package com.ollivanders.services;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import com.ollivanders.repos.GenericClassReposistory;

/**
 * Handles the logical class to the class repo, this class should not make any calls to the database itself.
 * @author castl
 *
 * @param <T>
 */
public class ClassService<T> {
	
	GenericClassReposistory<T> repo;
	Class<T> tClass;
	
	/**
	 * Constructor for class service based on the class passed in.
	 * As a note the classes fields need to be labeled with the
	 * Entity parameter otherwise those fields will be ignored.
	 * @param tClass
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
	
	public void dropClassTable() {
		repo.dropClassTable(true);
	}
	
	/**
	 * Drops the current class table and creates a new one.
	 * This method assumes the user wants to cascade on delete.
	 */
	public void dropThenCreateClassTable() {
		repo.dropClassTable(true);
		repo.createClassTable();
	}
	
	/**
	 * Saves the object to the current class table. If the object does not yet exist a new value is inserted
	 * if the object is in the class table then its updated.
	 * @param save the object instance being saved.
	 */
	public void save(T save) {
		Object pk = getPrimaryKey(save);
		
		try {
			if(repo.findByPrimaryKey(pk) == null || pk == null)
				repo.saveNewToClassTable(save);
			else
				repo.updateByPrimaryKey(save);
		} catch (SQLException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * Deletes the given object from the class table. Returns true if an item was deleted, false if nothing was deleted.
     * @param delete The object to delete
     * @return returns true if something was deleted, false otherwise
     */
    public boolean delete(T delete) {
        Object pk = getPrimaryKey(delete);

        boolean deleted = false;

        try {
            if (isInstanceSaved(delete)) {
                repo.deleteByPrimaryKey(pk, true);
                deleted = true;
            }
        } catch (SQLException | NoSuchFieldException throwables) {
            throwables.printStackTrace();
        }
        return deleted;
    }
    
    /**
     * Searches the database for the object by its primary key, returns true if it finds it, false otherwise
     * @param search the object to search for
     * @return returns true if the object is found, false otherwise
     */
    public boolean isInstanceSaved(T search) {
        Object pk = getPrimaryKey(search);

        try {
            return (repo.findByPrimaryKey(pk) != null);
        } catch (SQLException | NoSuchFieldException throwables) {
            throwables.printStackTrace();
            System.exit(1);
        }
        return false;
    }
    
    public ArrayList<T> find(Map<String, Object> fields){
    	return repo.searchByFields(fields);
    }
    
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
