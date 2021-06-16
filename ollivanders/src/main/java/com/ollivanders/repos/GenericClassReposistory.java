package com.ollivanders.repos;

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
		
		} catch (IllegalArgumentException | IllegalAccessException e) {
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
		Connection conn = ConnectionUtil.getConnection();
		
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
	 * @param newObj the object that needs to be inserted into the DB.
	 */
	@Override
	public void saveNewToClassTable(T newObj) {
		//Retrieve the proper insertion string.
		String sql = getInsertString();
		
		try {
			Field field = tClass.getField("columns");
			ColumnField[] columns = (ColumnField[]) field.get(null);
			
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			int count = 1;
			
			//Finding the new column field that matches the class column field.
			for(ColumnField column : columns) {
				String fieldName = column.getColumnName();
				Field fieldToStore = newObj.getClass().getDeclaredField(fieldName);
				
				//If the field happens to be private set the accesibility to true
				if(Modifier.isPrivate(fieldToStore.getModifiers()))
					fieldToStore.setAccessible(true);
				
				
				//If you come across the serial ignore this loop
				if(column.getColumnType().equalsIgnoreCase("serial")) 
					continue;
				
				else 
					pstmt.setObject(count, fieldToStore.get(newObj));
				
				//Update the count
				count++;
			}
			
			//Execute the query.
			pstmt.execute();
			
		} catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	/**
	 * Finds an object based on the primary key.
	 * @param primaryKey the key to query by
	 * @return the Object found by the primary key.
	 */
	@Override
	public T findByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException {
		Field pk = null;
		
		//Try and see if the primary key exists within the class table
		try {
			pk = getPKField();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//Check and see if the column name is safe
		if(!isColumnNameSafe(pk.getName())) throw new SQLSyntaxErrorException("Name contains invalid characters");
		
		//Creating a SQL string to query by.
		String sql = "SELECT * FROM " + getTableName() + " WHERE " + pk.getName()+" = ?";
		
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, primaryKey);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<T> objects = getTObjects(rs);
			
			if(objects.size() > 0)
				return objects.get(0);
			else
				return null;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
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
	
	/**
	 * Helper method to create an insertion string.
	 * @return returns a string that is a SQL insert for the table class
	 */
	private String getInsertString() {
		StringBuilder ib = new StringBuilder("INSERT INTO " + getTableName() + "(");
		StringBuilder vb = new StringBuilder(" VALUES (");
		
		try {
			//Getting the fields from the class
			Field field = tClass.getField("columns");
			ColumnField[] columns = (ColumnField[]) field.get(null);
			
			//Iterating through each column to get the values for the table
			//If the column type is serial move on and do not add it.
			for(ColumnField column : columns) {
				if(column.getColumnType().equals("serial")) continue;
				ib.append(column.getColumnName()).append(", ");
				vb.append("?, ");
			}
			
		} catch(NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//Remove the last comma and whitespace to avoid errors
		//In both the values and insert parts of the SQL statement
		int index = vb.lastIndexOf(", ");
		vb.delete(index, index+2);
		
		index = ib.lastIndexOf(", ");
		ib.delete(index, index+2);
		
		//Append the values to the insert statement
		ib.append(vb);
		
		//Return the completed SQL statement.
		return ib.toString();
	}
	
	/**
	 * Helper method to find the primary key field in an object
	 * @return the primary key field.
	 * @throws NoSuchFieldException if there isn't a column labelled as a primary key
	 */
	public Field getPKField() throws NoSuchFieldException{
		ColumnField[] columns = getColumns();
		
		for(ColumnField column : columns) {
			if(column.getConstraint().equals(SQLConstraints.PRIMARY_KEY))
				return tClass.getDeclaredField(column.getColumnName());
		}
		
		throw new NoSuchFieldException("This class does not have a primary key constraint");
	}
	
	/**
	 * Helper method that gets the columns field from tClass
	 * @return returns the column field.
	 */
	private ColumnField[] getColumns() {
		try {
			Field dbColumns = tClass.getField("columns");
			
			if(Modifier.isPrivate(dbColumns.getModifiers()))
				dbColumns.setAccessible(true);
			
			return(ColumnField[]) dbColumns.get(null);
			
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		try {
			throw new NoSuchFieldException("Missing a column field");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * Helper method that converts a result set into an arraylist<T>.
	 * @param rs the result set to be converted
	 * @return returns an arraylist from the result set
	 * @throws SQLException
	 */
	private ArrayList<T> getTObjects(ResultSet rs) throws SQLException {
        ColumnField[] columns = getColumns();

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
}
