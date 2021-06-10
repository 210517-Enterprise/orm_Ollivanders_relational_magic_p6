package com.ollivanders.repos;

import java.sql.SQLException;

/*
 * A repository that can run CRUD methods for any class that extends the BaseModel.
 * @param <T> The class that is using the Reposistory.
 */
public class GenericClassReposistory<T> implements CrudRepository<T>{
	
	private Class<T> tClass;
	private String query = "SELECT ? " +
						   "FROM ?";
	
	
	/**
	 * Construct that takes in a class and stores it as a reference
	 * @param tClass the class that this repository is for.
	 */
	public GenericClassReposistory(Class<T> tClass) {
		super();
		this.tClass = tClass;
	}
	
	/**
	 * Creates the class table.
	 */
	@Override
	public void createClassTable() throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeClassTable(T tableObj, boolean cascade) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveNewToClassTable(T newObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveNewToClassTable(T newObj, T tableObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T findByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T findByPrimaryKey(Object primaryKey, Object table) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateByPrimaryKey(T updatedObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateByPrimaryKey(T updatedObj, T tableObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteByPrimaryKey(Object primaryKey, boolean cascade) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteByPrimaryKey(Object primaryKey, T tableObj, boolean cascade)
			throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
