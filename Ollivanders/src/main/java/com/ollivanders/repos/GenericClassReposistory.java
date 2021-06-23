package com.ollivanders.repos;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ollivanders.model.SQLConstraints;
import com.ollivanders.util.ColumnField;
import com.ollivanders.util.ConnectionUtil;

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
	 * Creates a class table based on the tClass field. The createClassTable method
	 * will first get all the fields that are present within the class then check the
	 * annotations to see if any fields are labeled. The annotations used are:
	 * 
	 * Column - specifies a column within tClass.
	 * Id 	  - specifies that the field is a primary key.
	 * 
	 */
	@Override
	public void createClassTable() throws NoSuchFieldException, SQLException {
		//Acquires the list of fields given by the class table.
		List<Field> fields = new ArrayList<Field>(Arrays.asList(tClass.getFields()));
		
		//For each field in the tClass loop through and add them to a columns Array
		//If no id is available one will be created. ColumnField will be preallocated
		ColumnField[] columns = new ColumnField[fields.size()];
		
		for(Field f : fields) {
			boolean isPrimaryKey = false;
			boolean fieldIsColumn = false;
			
			//If the modifier happens to be private set it to true.
			if(Modifier.isPrivate(f.getModifiers())) f.setAccessible(true);
			
			//Check to ensure the field has all necessary components to be added to the columns.
			try {
				//Make sure the field has an annotation for the column.
				if(f.getAnnotations().length != 0) {
					//Check all annotations just to be safe.
					Annotation[] annoArr = f.getAnnotations();
					for(Annotation a : annoArr) {
						//Check to see if the field is a column
						if(a.annotationType().equals(com.ollivanders.annotations.Column.class)) 
							fieldIsColumn = true;
						if(a.annotationType().equals(com.ollivanders.annotations.Id.class))
							isPrimaryKey = true;
					}
				}
				
				//So long as the field is indeed a field, get the name and type, and constraint.
				//Note if no constraint is given no SQL constraints will be used.
				if(fieldIsColumn) {
					ColumnField tClassColumn;
					//Check to see if the column is an ID and if it is specify it as a primary key.
					if(isPrimaryKey)
						tClassColumn = new ColumnField(f.getName().toString(),f.getType().toString(),SQLConstraints.PRIMARY_KEY);
					tClassColumn = new ColumnField(f.getName().toString(),f.getType().toString(),SQLConstraints.NONE);
				}
			
				//Catch any exceptions that may occur.
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		//Create the query that will be used to create a table.
		StringBuilder queryStr = new StringBuilder("CREATE TABLE " + tClass.getName() + "(\n");
		
		//Assert that the columns are not null and then continue.
		assert columns != null;
		for(ColumnField c : columns) {
			String line = c.getRowAsString();
			queryStr.append(line);
		}
		
		//Replace the last comma with the closing part of a SQL query.
		queryStr.replace(queryStr.lastIndexOf(","), queryStr.length(), ");");
		
		//Establish the Connection to the DB and execute the query.
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(queryStr.toString());
			pstmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
		
	/**
	 * Returns an arraylist that is every object stored in the class table
	 * @return returns an arraylist of all objects in teh class table
	 */
	@Override
	public List<T> getAll() throws SQLException {
		
		//Establishing a connection to the DB
		Connection conn = ConnectionUtil.getConnection();
		ArrayList<T> objects = new ArrayList<>();
		
		//Query the table to get all the objects.
		try {
			assert conn != null;
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ?");
			pstmt.setString(1,getTableName());
			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
		
		return objects;
	}

	@Override
	public List<T> getAllJoined(T otherTableObj) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * A method that will drop the class table if it exists or not
	 */
	@Override
	public void dropClassTable(boolean cascade) throws NoSuchFieldException, SQLException {
		Connection conn = ConnectionUtil.getConnection();
		
		//Intial string of a SQL drop
		String stmt = "DROP TABLE IF EXISTS " + getTableName();
		
		//Creating a string builder with the statement
		StringBuilder sql = new StringBuilder(stmt);
		
		//Check to see if the table is allowed to cascade and if it is append cascade.
		if(cascade) sql.append(" CASCADE");
		
		try {
			assert conn != null;
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			pstmt.execute();
			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Overloaded method to allow for dropping a class without providing whether to
	 * cascade or not.
	 */
	public void dropClassTable() throws NoSuchFieldException, SQLException {
		Connection conn = ConnectionUtil.getConnection();
		
		//Intial string of a SQL drop
		String stmt = "DROP TABLE IF EXISTS " + getTableName();
		
		try {
			assert conn != null;
			PreparedStatement pstmt = conn.prepareStatement(stmt);
			pstmt.execute();
			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Takes in an object of any type to save in the class table. 
	 */
	@Override
	public void saveNewToClassTable(T newObj) {
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
	
	private boolean isColumnNameSafe(String col) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-0_]+$");
		Matcher matcher = pattern.matcher(col);
		return matcher.matches();
	}
	
	/**
	 * Helper method that returns a string builder after all periods have been placed with underscores
	 * @param builder
	 * @return
	 */
	private StringBuilder replacePeriods(StringBuilder builder) {
		int index = builder.indexOf(".");
		
		while(index != -1) {
			builder.replace(index, index+1, "_");
			index = builder.indexOf(".");
		}
		return builder;
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
		if(name == null) name = replacePeriods(new StringBuilder(tClass.getName())).toString();
		
		if(!isColumnNameSafe(name)) try {
			throw new SQLSyntaxErrorException();
		} catch (SQLSyntaxErrorException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
		//FIXME Once you get through with some testing try edge cases where this could break.
		return name;
	}

}
