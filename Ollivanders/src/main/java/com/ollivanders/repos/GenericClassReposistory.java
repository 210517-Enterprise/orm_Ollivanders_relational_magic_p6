package com.ollivanders.repos;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	 */
	@Override
	public void createClassTable() throws NoSuchFieldException, SQLException {
		
		//Make a call to the helper method to acquire the column fields from tClass
		ColumnField[] columns = getColFields();
		
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
			PreparedStatement pstmt = conn.prepareStatement(queryStr.toString().toLowerCase());
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
	 * Drops the class table associated with the reference of tClass.
	 * @param cascade determines whether or not to cascade on drop or not.
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
	 * Inserts a new object to the tClass table. The method will first get
	 * the fields of the class and format it as a string 
	 * @param newObj is the object that is going to be inserted into the DB.
	 */
	@Override
	public void saveNewToClassTable(T newObj) {
		
		
	}
	
	/**
	 * Finds an object by its primary key value.
	 * @param primaryKey is the primary key that will be queried by.
	 * @return T an object found by the primary key or null if none exists.
	 */
	@Override
	public T findByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException {
		
		//Check to see if the primary key exists within the class table.
		Field pk = null;
		ArrayList<T> objs = null;
		try {
			//Find the primary key field if one exists.
			pk = getPKField();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//Make another check to see if the column name is safe.
		if(!isColumnNameSafe(pk.getName())) throw new SQLException("Name contains invalid characters.");
		
		//Create a SQL string to query the table by.
		String sql = "Select * FROM " + tClass.getName().toLowerCase() + " WHERE " + pk.getName() +"= ?";
		
		//Establish a connection and query the database.
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, primaryKey);
			ResultSet rs = pstmt.executeQuery();
			objs = getTObjects(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//Return the result of the query.
		return ((!objs.isEmpty()) ? objs.get(0) : null);
	}


	@Override
	public T findByColumnName(Object columnName) throws NoSuchFieldException, SQLException {
		Field entry = null;
		ArrayList<T> objects = null;
		//See if the field exists and if not do not return anything
		try {
			entry = tClass.getField(columnName.toString());
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
		
		//Check to ensure that the columnname is safe.
		if(!isColumnNameSafe(entry.getName())) throw new SQLException("Column name contains invalid characters.");
		
		//Create a query to locate the entry by its columnname
		//As a note this could return more then one entry but only the first will be considered.
		
		String sql = "Select * from " + getTableName() + " WHERE " + entry.getName() + "= ?";
		
		//Connect to the DB and attempt the query.
		
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, columnName);
			ResultSet rs = pstmt.executeQuery();
			objects = getTObjects(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ((!objects.isEmpty()) ? objects.get(0) : null);
	}


	@Override
	public List<T> findAllByColumnName(Object columnName) throws NoSuchFieldException, SQLException {
		Field entry = null;
		ArrayList<T> objects = null;
		//See if the field exists and if not do not return anything
		try {
			entry = tClass.getField(columnName.toString());
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
		
		//Check to ensure that the columnname is safe.
		if(!isColumnNameSafe(entry.getName())) throw new SQLException("Column name contains invalid characters.");
		
		//Create a query to locate the entry by its columnname
		//As a note this could return more then one entry but only the first will be considered.
		
		String sql = "Select * from " + getTableName() + " WHERE " + entry.getName() + "= ?";
		
		//Connect to the DB and attempt the query.
		
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, columnName);
			ResultSet rs = pstmt.executeQuery();
			objects = getTObjects(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ((!objects.isEmpty()) ? objects : null);
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
	/**
	 * Helper method to find which field is the primary key within tClass.
	 * @return the field that is the primary key.
	 * @throws NoSuchFieldException if no field is a primary key.
	 */
	private Field getPKField() throws NoSuchFieldException{
		//Make a call to the helper method to acquire the column fields.
		ColumnField[] columns = getColFields();
		
		//For each column field check to see if there is a sql constraint for the primary key.
		for(ColumnField c : columns) {
			if(c.getConstraint().equals(SQLConstraints.PRIMARY_KEY))
				//Returns the name of the primary key field.
				return tClass.getDeclaredField(c.getColumnName());
		}
		
		throw new NoSuchFieldException("This class does not have a primary key constraint");
	}
	
	private ArrayList<T> getTObjects(ResultSet rs) throws SQLException{
		//Acquire the column fields from tClass
		ColumnField[] columns = getColFields();
		
		//Iterate through the result set to construct the new object
		 ArrayList<T> objects = new ArrayList<>();

	        while (rs.next()) {
	            Constructor<T> emptyCon= null;
	            try {
	                emptyCon = tClass.getDeclaredConstructor();
	            } catch (NoSuchMethodException e) {
	                e.printStackTrace();
	                System.exit(1);
	            }

	            if (Modifier.isPrivate(emptyCon.getModifiers())) {
	                emptyCon.setAccessible(true);
	            }

	            T emptyObject = null;
	            try {
	                emptyObject = emptyCon.newInstance();
	            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
	                e.printStackTrace();
	                System.exit(1);
	            }

	            for (int i = 1; i <= columns.length; i++) {
	                //System.out.println("On iteration: "+i);
	                Field field = null;

	                String columnName = columns[i-1].getColumnName();

	                try {
	                    field = tClass.getDeclaredField(columnName);
	                } catch (NoSuchFieldException e) {
	                    e.printStackTrace();
	                    System.exit(1);
	                }
	                //System.out.println("On field: "+columnName);

	                if (Modifier.isPrivate(field.getModifiers())) {
	                    field.setAccessible(true);
	                }

	                try {

	                    if (field.getType().isEnum()) {
	                        int constant = (int) rs.getObject(columnName) - 1;
	                        field.set(emptyObject, field.getType().getEnumConstants()[constant]);
	                    }
	                    else {
	                        Object insert = rs.getObject(columnName);
	                        if (insert.getClass().equals(BigDecimal.class)) {
	                            if (field.getType().getName().equals(Double.class.getName()) ||
	                                    field.getType().getName().equals(double.class.getName())) {
	                                field.set(emptyObject, ((BigDecimal) insert).doubleValue());
	                            }
	                        }
	                        else {
	                            field.set(emptyObject, rs.getObject(columnName));
	                        }
	                    }
	                } catch (IllegalAccessException e) {
	                    e.printStackTrace();
	                    System.exit(1);
	                }
	            }

	            objects.add(emptyObject);
	        }

	        return objects;
	}
	
	/**
	 * Helper method to find the column fields from tClass. Effectively a ColumnField is:
	 * - The name of the field.
	 * - The type of the field.
	 * - The SQL constraint that the field has.
	 * - It also double checks to ensure the the column field is indeed annotated as a field.
	 * @return ColumnField[], returns an array of fields that represent
	 */
	private ColumnField[] getColFields() {
		// Acquires the list of fields given by the class table.
		List<Field> fields = new ArrayList<Field>(Arrays.asList(tClass.getFields()));

		// For each field in the tClass loop through and add them to a columns Array
		// If no id is available one will be created. ColumnField will be preallocated
		ColumnField[] columns = new ColumnField[fields.size()];
		int index = 0;
		for (Field f : fields) {
			boolean isPrimaryKey = false;
			boolean fieldIsColumn = false;
			ColumnField tClassColumn = null;
			// If the modifier happens to be private set it to true.
			if (Modifier.isPrivate(f.getModifiers()))
				f.setAccessible(true);

			// Check to ensure the field has all necessary components to be added to the
			// columns.
			try {
				// Make sure the field has an annotation for the column.
				if (f.getAnnotations().length != 0) {
					// Check all annotations just to be safe.
					Annotation[] annoArr = f.getAnnotations();
					for (Annotation a : annoArr) {
						// Check to see if the field is a column
						if (a.annotationType().equals(com.ollivanders.annotations.Column.class))
							fieldIsColumn = true;
						if (a.annotationType().equals(com.ollivanders.annotations.Id.class))
							isPrimaryKey = true;
					}
				}

				// So long as the field is indeed a field, get the name and type, and
				// constraint.
				// Note if no constraint is given no SQL constraints will be used.
				if (fieldIsColumn) {
					// Check to see if the column is an ID and if it is specify it as a primary key.
					if (isPrimaryKey)
						tClassColumn = new ColumnField(f.getName().toString(), f.getType().toString(),
								SQLConstraints.PRIMARY_KEY);
					else
						tClassColumn = new ColumnField(f.getName().toString(), f.getType().toString(),
								SQLConstraints.NONE);
				}
				// Add the newly created Column to ColumnField
				columns[index] = tClassColumn;
				index++;
				// Catch any exceptions that may occur.
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return columns;
	}
}
