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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ollivanders.annotations.Id;
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
	public void createClassTable() {
		
		//Make a call to the helper method to acquire the column fields from tClass
		ColumnField[] columns = getColFields();
		
		//Create the query that will be used to create a table.
		StringBuilder queryStr = new StringBuilder("CREATE TABLE "+ tClass.getSimpleName().toLowerCase() + "(");
		//Assert that the columns are not null and then continue.
		assert columns != null;
		for(ColumnField c : columns) {
			String line;
			if(c.getConstraint().equals(SQLConstraints.PRIMARY_KEY)) {
				line = c.getColumnName() + " serial, ";
			} else {
				line = c.getRowAsString();
			}
			
			queryStr.append(line);
		}
		
		//Replace the last comma with the closing part of a SQL query.
		queryStr.replace(queryStr.lastIndexOf(","), queryStr.length(), ");");
		
		//Establish the Connection to the DB and execute the query.
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(queryStr.toString().toLowerCase());
			System.out.println("Current query string: " + queryStr.toString().toLowerCase());
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
	public ArrayList<T> getAll() throws SQLException {
		
		ArrayList<T> objects = new ArrayList<>();
		
		//Query the table to get all the objects.
		try {
			//Establishing a connection to the DB
			Connection conn = ConnectionUtil.getConnection();
			

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " + tClass.getSimpleName().toLowerCase());
			ResultSet rs = pstmt.executeQuery();
			objects = getTObjects(rs);
			
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
	public void dropClassTable(boolean cascade) {
		
		
		//Intial string of a SQL drop
		String stmt = "DROP TABLE IF EXISTS " + tClass.getSimpleName();
		
		//Creating a string builder with the statement
		StringBuilder sql = new StringBuilder(stmt);
		
		//Check to see if the table is allowed to cascade and if it is append cascade.
		if(cascade) sql.append(" CASCADE");
		
		sql.append(";");
		
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString().toLowerCase());
			System.out.println("Executing query: " + sql.toString().toLowerCase());
			pstmt.execute();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	/**
	 * Overloaded method to allow for dropping a class without providing whether to
	 * cascade or not.
	 */
	public void dropClassTable() {
		Connection conn = ConnectionUtil.getConnection();
		
		//Intial string of a SQL drop
		String stmt = "DROP TABLE IF EXISTS " + tClass.getSimpleName().toLowerCase();
		
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
		String sql = getInsertString();
		
		try {
			ColumnField[] columns = getColFields();
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			int count = 1;
			
			for(ColumnField c : columns) {
				if(c.getConstraint().equals(SQLConstraints.PRIMARY_KEY))
					continue;
				String fieldName = c.getColumnName();
				Field fieldToStore = newObj.getClass().getDeclaredField(fieldName);
				System.out.println("Current field: " + fieldToStore.getName());
				Annotation[] fieldAnnotations = fieldToStore.getAnnotations();
				for(Annotation a : fieldAnnotations) {
					System.out.println(a.toString());
				}
				Set<Class> annoSet = new HashSet<>();
				for(Annotation a : fieldAnnotations)
					annoSet.add(a.getClass());
				
				//IF the field happens to be private set the accessibility to true
				if(Modifier.isPrivate(fieldToStore.getModifiers()))
					fieldToStore.setAccessible(true);
				
				if(annoSet.contains(Id.class)) {
					continue;
				}
				else {
					pstmt.setObject(count, fieldToStore.get(newObj));
					count++;
				}
				
			}
			
			pstmt.execute();
		} catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds an object by its primary key value.
	 * @param primaryKey is the primary key that will be queried by.
	 * @return T an object found by the primary key or null if none exists.
	 * @deprecated replaced by 
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
		String sql = "Select * FROM " + tClass.getSimpleName().toLowerCase() + " WHERE " + pk.getName() +"= ?";
		
		//Establish a connection and query the database.
		try {
			Connection conn = ConnectionUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, primaryKey);
			ResultSet rs = pstmt.executeQuery();
			System.out.println("Successfully located the field");
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
		
		String sql = "Select * from " + tClass.getSimpleName().toLowerCase() + " WHERE " + entry.getName() + "= ?";
		
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
	public ArrayList<T> findAllByColumnName(Object columnName) throws NoSuchFieldException, SQLException {
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
		
		String sql = "Select * from " + tClass.getSimpleName() + " WHERE " + entry.getName() + "= ?";
		
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
		
		//Attempt to see if the class has a pk field.
		try {
			Field pk = getPKField();
			if(Modifier.isPrivate(pk.getModifiers()))
				pk.setAccessible(true);
			
			//Set an ID equal to the pk of the updated object.
			Object id = pk.get(updatedObj);
			
			//Check to see if the primary key exists within the DB.
			if(findByPrimaryKey(id) == null) return false;
		} catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			System.err.println("No object found by that primary key.");
			return false;
		}
		
		//Attempt to query the DB
		try {
			Connection conn = ConnectionUtil.getConnection();
			String sql = getUpdateString().toLowerCase();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			//Modify the updated sql statement then execute.
			pstmt = getPreparedUpdate(pstmt, updatedObj);
			
			return pstmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public boolean deleteByPrimaryKey(Object primaryKey, boolean cascade) throws NoSuchFieldException, SQLException {
		Field pk = null;
		boolean executed = false;

		pk = getPKField();

		if(!isColumnNameSafe(pk.getName())) throw new SQLException("Name contains invalid characters");

		//Create a query to delete by
		String sql = "DELETE FROM " + tClass.getSimpleName().toLowerCase()+" WHERE "+pk.getName()+" = ?";

		//Establish a connection and attempt to query
		try {
		Connection conn = ConnectionUtil.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql.toLowerCase());
		pstmt.setObject(1, primaryKey);
		executed = pstmt.execute();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return false;
		}
		return executed;
	}
	
	private boolean isColumnNameSafe(String col) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-0_]+$");
		Matcher matcher = pattern.matcher(col);
		return matcher.matches();
	}
	
	public ArrayList<T> searchByFields(Map<String, Object> qualifiers) {
        StringBuilder sql = new StringBuilder("SELECT * FROM "+tClass.getSimpleName()+" WHERE ");

        

        for (Map.Entry<String, Object> entry : qualifiers.entrySet()) {
            sql.append(entry.getKey()).append( " = ? AND ");
        }

        int index = sql.lastIndexOf(" AND ");
        sql.delete(index, index+5);

        try {
        	Connection conn = ConnectionUtil.getConnection();
            PreparedStatement stmt= conn.prepareStatement(sql.toString().toLowerCase());

            int counter = 1;
            for (Map.Entry<String, Object> entry : qualifiers.entrySet()) {
                stmt.setObject(counter, entry.getValue());
                counter++;
            }
            System.out.println("Query being executed: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            ArrayList<T> found = getTObjects(rs);

            return found;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(1);
        }

        return null;
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
	 * @deprecated tClass will no longer have a field called tableName and is replaced by getTClassName().
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
	public Field getPKField() throws NoSuchFieldException{
		//Make a call to the helper method to acquire the column fields.
		ColumnField[] columns = getColFields();
		
		//For each column field check to see if there is a sql constraint for the primary key.
		for(ColumnField c : columns) {
			if(c.getConstraint().equals(SQLConstraints.PRIMARY_KEY))
				//Returns the name of the primary key field.
				return tClass.getDeclaredField(c.getColumnName());
		}
		
		throw new NoSuchFieldException("Class does not have a column with a primary key constraint");
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
	 * Helper method that creates the update string
	 * @return returns a string version of the update statement.
	 * @throws SQLSyntaxErrorException if the column field in the class has invalid characters.
	 */
	private String getUpdateString() throws SQLSyntaxErrorException {
		 StringBuilder builder = new StringBuilder("Update "+tClass.getSimpleName()+" SET ");
	        StringBuilder qualifier = new StringBuilder("WHERE ");

	        ColumnField[] columns = getColFields();

	        for (ColumnField column: columns){

	            String columnName = column.getColumnName();
	            if (!isColumnNameSafe(columnName)) throw new SQLSyntaxErrorException("Column name contains invalid characters!");

	            if (column.getConstraint() == SQLConstraints.PRIMARY_KEY) {
	                qualifier.append(columnName).append(" = ?");
	            } else {
	                if (column.getColumnType().equals(SQLType.SERIAL)) {
	                    continue;
	                }
	                builder.append(columnName).append(" = ?, ");
	            }
	        }

	        int index = builder.lastIndexOf(", ");
	        builder.delete(index, index+2);
	        builder.append(qualifier);

	        return builder.toString();
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
						tClassColumn = new ColumnField(f.getName().toString(), determineSQLType(f.getType().getSimpleName()),
								SQLConstraints.PRIMARY_KEY);
					else
						tClassColumn = new ColumnField(f.getName().toString(), determineSQLType(f.getType().getSimpleName()),
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
	
	/**
     * A helper method that preps the Update Prepared Statement
     * @param pstmt the update statement to be updated
     * @param updatedObject the object to be converted into an update string
     * @return returns a {re[aredStatement that is the update statement
     */
    private PreparedStatement getPreparedUpdate(PreparedStatement pstmt, T updatedObject){
        ColumnField[] columns = getColFields();

        int count = 1;

        for (ColumnField column: columns) {

            Object insert = null;

            try {
                Field fieldToInsert = tClass.getDeclaredField(column.getColumnName());

                if (Modifier.isPrivate(fieldToInsert.getModifiers())) {
                    fieldToInsert.setAccessible(true);
                }

                insert = fieldToInsert.get(updatedObject);

            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
                System.exit(1);
            }
            try {
                if (column.getConstraint() == SQLConstraints.PRIMARY_KEY) {
                    pstmt.setObject(columns.length, insert);
                } else {
                    if (insert.getClass().isEnum()) {
                        int store = ((Enum) insert).ordinal()+1;
                        pstmt.setInt(count, store);
                    } else {
                        pstmt.setObject(count, insert);
                    }
                    count++;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                System.exit(1);
            }
        }

        return pstmt;
    }
    
	private String getInsertString() {
    	StringBuilder ib = new StringBuilder("INSERT INTO " + tClass.getSimpleName() + "(");
    	StringBuilder vb = new StringBuilder("VALUES (");
    	
    	ColumnField[] columns = getColFields();
    	
    	for(ColumnField c : columns) {
    		if(c.getConstraint().equals(SQLConstraints.PRIMARY_KEY)) continue;
    		ib.append(c.getColumnName()).append(", ");
    		vb.append("?, ");
    	}
    	
    	int index = vb.lastIndexOf(", ");
    	vb.delete(index, index+2);
    	vb.append(") ");
    	
    	index = ib.lastIndexOf(", ");
    	ib.delete(index, index+2);
    	ib.append(") ");
    	
    	ib.append(vb);
    	System.out.print(ib.toString());
    	return ib.toString();
    }
	
	private SQLType determineSQLType(String type) {
		if(type.equals("Integer")) return SQLType.INTEGER;
		if(type.equals("String")) return SQLType.VARCHAR;
		if(type.equals("Boolean")) return SQLType.BOOLEAN;
		else return null;
	}
}
