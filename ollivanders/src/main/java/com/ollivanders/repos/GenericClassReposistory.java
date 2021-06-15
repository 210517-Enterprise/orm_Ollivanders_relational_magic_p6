package com.ollivanders.repos;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/*
 * A repository that can run CRUD methods for any class that extends the BaseModel.
 * @param <T> The class that is using the Reposistory.
 */
public class GenericClassReposistory<T> implements CrudRepository<T>{
	
	private Class<T> tClass;
	
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
		Field field = null;
		
		//Looks for the columns from the class if they exist.
		try {
			field = tClass.getField("columns");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		ColumnField[] columns = null; //FIXME ColumnField class needs to be implemented for this to work
		
		//Checking to see if the field in question is private, if it is make it accessible.
		try {
			assert field != null;
			if(Modifier.isPrivate(field.getModifiers())) field.setAccessible(true);
		 
		//Setting the columns equal to the field.
		columns = (ColumnField[]) field.get(null);
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//Creating the query that will be used to create a new table.
		StringBuilder b = new StringBuilder("CREATE TABLE " + getTableName()+"(\n");
		
		//Make sure the columns is not null then append it to the builder
		assert columns != null;
		for(ColumnField c : columns) {
			String line = c.getRowAsString()+"\n";
			b.append(line);
		}
		
		//Remove the last comma and replace it with a closing sql statement.
		b.replace(b.lastIndexOf(","), b.length(), ");");
		
		//Establish a connection to the DB.
		//FIXME This might be something that can be encapsulated in a single method.
		Connection conn = SessionManager.getConnection();
		
		try {
			assert conn != null;
			PreparedStatement pstmt = conn.prepareStatement(b.toString());
			pstmt.execute();
			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
	}
	
		

	@Override
	public List<T> getAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<T> getAllOrdered(T order) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<T> getAllJoined(T otherTableObj) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeClassTable(boolean cascade) throws NoSuchFieldException, SQLException {
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
	public T findByColumnName(Object columnName) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<T> findAllByColumnName(Object columnName) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean updateByPrimaryKey(T updatedObj) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean deleteByPrimaryKey(Object primaryKey, boolean cascade) throws NoSuchFieldException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Gets the name of the table being inserted
	 * @return the name of the table.
	 */
	private String getTableName() {
		String name = null;
		
		//Attempt to see if there is a field for the table name.
		try {
			Field tableName = tClass.getField("tableName");
			
			//If the table name is private set the accessibility to true.
			if(Modifier.isPrivate(tableName.getModifiers())) tableName.setAccessible(true);
			name = (String) tableName.get(null);
			
		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//If no name is present get the class name and use it for the table name.
		if(name == null) name = new StringBuilder(tClass.getName()).toString();
		
		//FIXME Once you get through with some testing try edge cases where this could break.
		return name;
	}

}
