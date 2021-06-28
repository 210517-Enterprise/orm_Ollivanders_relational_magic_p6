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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ollivanders.annotations.Column;
import com.ollivanders.annotations.Id;
import com.ollivanders.model.SQLConstraints;
import com.ollivanders.util.ColumnField;

import com.ollivanders.util.SessionManager;

/**
 * A repository that can run CRUD operations for any instance of a class.
 * 
 * @author Kyle Castillo
 */
public class GenericClassReposistory<T> implements CrudRepository<T> {
	private Connection conn = SessionManager.getConnection();
	private Class<T> tClass;
	private boolean allowNoPrimaryKey;
	private boolean hasClassTable;

	/**
	 * Constructor that takes in any instance of a class.
	 * 
	 * @param tClass the class instance that will be used within the repo.
	 */
	public GenericClassReposistory(Class<T> tClass) {
		super();
		this.tClass = tClass;
		allowNoPrimaryKey = true;
	}

	/**
	 * Constructor that accepts an instance of a class and allowing no primary key.
	 * 
	 * @param tClass            the class instance that will be used within the
	 *                          repo.
	 * @param allowNoPrimaryKey specifies whether or not to allow for no primary
	 *                          key.
	 */
	public GenericClassReposistory(Class<T> tClass, boolean allowNoPrimaryKey) {
		super();
		this.tClass = tClass;
		this.allowNoPrimaryKey = allowNoPrimaryKey;
	}

	public boolean hasClassTable() {
		return hasClassTable;
	}

	public String getClassTableName() {
		return tClass.getSimpleName().toLowerCase();
	}

	/**
	 * Creates a class table for the tClass instance.
	 */
	@Override
	public void createClassTable() {

		// Make a call to the helper method to acquire the column fields from tClass
		ColumnField[] columns = getColumnFields();

		// Create the query that will be used to create a table.
		StringBuilder queryStr = new StringBuilder("CREATE TABLE " + tClass.getSimpleName().toLowerCase() + "(");

		for (ColumnField c : columns) {
			String line = c.getRowAsString();
			queryStr.append(line);
		}		
		

		// Replace the last comma with the closing part of a SQL query.
		queryStr.replace(queryStr.lastIndexOf(","), queryStr.length(), ");");
		
		// Establish the Connection to the DB and execute the query.
		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(queryStr.toString().toLowerCase());
			pstmt.execute();
//			conn.close();
			hasClassTable = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a class table and adds a constraint for a foreign key.<br>
	 * The foreign key will always reference the primary key of a separate table if
	 * it exists. If no primary key exists in the separate table the class table
	 * will not be created. Also if not foreign key constraint is found in the
	 * current class table the constraint will not be added and the table class will
	 * not be made.
	 * 
	 * @param parentTable the foreign key table that the
	 */
	@SuppressWarnings("rawtypes")
	public void createClassTable(GenericClassReposistory parentTable) {

		// Ensure that the foreignTable Repo exists.
		assert parentTable != null : "The foreign table repository does not exist";

		// Ensure that the foreignTable has a class table
		assert parentTable.hasClassTable != false : "The foreign table does not exist";

		// Get the column fields of the class table.
		ColumnField[] columns = getColumnFields();

		// Get the fields of the foreign key of the tClass and the Primary key of the
		// foreignTable
		try {
			Field tClassFK = getFKField();
			Field tParentPK = parentTable.getPKField();

			// Ensure that the SQL Types match
			//Note Integer is equal to serial.
			if ((tClassFK.getAnnotation(Column.class).columnType().equals(tParentPK.getAnnotation(Column.class).columnType())) || 
					(tClassFK.getAnnotation(Column.class).columnType().equals(SQLType.INTEGER) 
							&& tParentPK.getAnnotation(Column.class).columnType().equals(SQLType.SERIAL))) {
				// Create the class table
				createClassTable();
				setParentTables(parentTable);

			} else {
				throw new SQLException("The foreign key and primary key data types do not match");
			}
		} catch (NoSuchFieldException | SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Allows you to set a foreign key constraint on tClass.
	 * @apiNote The foreign key will always reference the primary key of the parent class table.
	 * @param parentTable the foreign table that the constraint will be made on
	 */
	public void setParentTables(GenericClassReposistory...parentTable) {

		List<Field> tClassFK;
		List<Field> tParentPK = new ArrayList<>();
		List<String> tParentTableNames = new ArrayList<>();

		try {
			//Find the primary and foreign keys for their respective tables
			tClassFK = getFKFields();
			
			//Acquire all primary keys from the parent tables.
			for(GenericClassReposistory parent : parentTable) {
				tParentPK.add(parent.getPKField());
				tParentTableNames.add(parent.getClassTableName());
			}
			
			//Ensure that both the class table and the parent tables have a foreign keys and primary keys.
			assert tClassFK != null : "There is no foreign key for the class table";
			assert tParentPK != null && !tParentPK.isEmpty(): "There is no public keys for the parent class tables.";
			assert tClassFK.size() == tParentPK.size() : "Primary key and foreign key amounts do not match";
			
			for(int i = 0; i < tClassFK.size(); i++) {
			Field cField = tClassFK.get(i);
			Field pField = tParentPK.get(i);
			//Note Integer is equal to serial.
			if ((cField.getAnnotation(Column.class).columnType().equals(pField.getAnnotation(Column.class).columnType())) || 
					(cField.getAnnotation(Column.class).columnType().equals(SQLType.INTEGER) 
							&& pField.getAnnotation(Column.class).columnType().equals(SQLType.SERIAL))) {

				// Creating the SQL query.
				StringBuilder sql = new StringBuilder("ALTER TABLE " + tClass.getSimpleName().toLowerCase()
						+ " ADD CONSTRAINT " + cField.getName() + "_FK " + "FOREIGN KEY (" + cField.getName() + ")"
						+ " REFERENCES " + tParentTableNames.get(i) + " (" + pField.getName() + ");");
				// Establish the Connection to the DB and execute the query.

//				Connection conn = SessionManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString().toLowerCase());
				pstmt.execute();
//				conn.close();

			} else {
				throw new SQLException("The foreign key and primary key data type do not match");
			}
			}
		} catch (NoSuchFieldException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Returns an arraylist that is every object stored in the class table
	 * 
	 * @return returns an arraylist of all objects in teh class table
	 */
	@Override
	public ArrayList<T> getAll() throws SQLException {

		ArrayList<T> objects = new ArrayList<>();

		// Query the table to get all the objects.
		try {
			// Establishing a connection to the DB
//			Connection conn = SessionManager.getConnection();

			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " + tClass.getSimpleName().toLowerCase());
			ResultSet rs = pstmt.executeQuery();
			objects = getTObjects(rs);
//			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}

		return objects;
	}

	/**
	 * Drops the class table associated with the reference of tClass.
	 * 
	 * @param cascade determines whether or not to cascade on drop or not.
	 */
	@Override
	public void dropClassTable(boolean cascade) {

		// Intial string of a SQL drop
		String stmt = "DROP TABLE IF EXISTS " + tClass.getSimpleName();

		// Creating a string builder with the statement
		StringBuilder sql = new StringBuilder(stmt);

		// Check to see if the table is allowed to cascade and if it is append cascade.
		if (cascade)
			sql.append(" CASCADE");

		sql.append(";");

		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString().toLowerCase());
			pstmt.execute();
//			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	/**
	 * Overloaded method to allow for dropping a class without providing whether to
	 * cascade or not.
	 */
	public void dropClassTable() {
//		Connection conn = ConnectionUtil.getConnection();

		// Intial string of a SQL drop
		String stmt = "DROP TABLE IF EXISTS " + tClass.getSimpleName().toLowerCase();

		try {
			assert conn != null;
			PreparedStatement pstmt = conn.prepareStatement(stmt);
			pstmt.execute();
//			conn.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Inserts a new object to the tClass table. The method will first get the
	 * fields of the class and format it as a string
	 * 
	 * @param newObj is the object that is going to be inserted into the DB.
	 * @return The object that was inserted into the DB with the updated primary
	 *         key.
	 */
	@Override
	public T saveNewToClassTable(T newObj) {
		String sql = getInsertString();

		try {
			ColumnField[] columns = getColumnFields();
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			boolean primaryKeyIsSerial = false;

			int count = 1;

			for (ColumnField c : columns) {
				if (c.getConstraint().equals(SQLConstraints.PRIMARY_KEY) && c.getColumnType().equals(SQLType.SERIAL)) {
					pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					primaryKeyIsSerial = true;
					continue;
				} else if (c.getConstraint().equals(SQLConstraints.PRIMARY_KEY) && !c.getColumnType().equals(SQLType.SERIAL)) {
					String returnKey = c.getColumnName();
				}
				
				String fieldName = c.getColumnName();
				Field fieldToStore = newObj.getClass().getDeclaredField(fieldName);
				Annotation[] fieldAnnotations = fieldToStore.getAnnotations();

				Set<Class> annoSet = new HashSet<>();
				for (Annotation a : fieldAnnotations)
					annoSet.add(a.getClass());

				// IF the field happens to be private set the accessibility to true
				if (Modifier.isPrivate(fieldToStore.getModifiers()))
					fieldToStore.setAccessible(true);

				if (annoSet.contains(Id.class)) {
					continue;
				} else {
					//Check to see if the primary key is serial or not.
					
					pstmt.setObject(count, fieldToStore.get(newObj));
					count++;
				}

			}
			pstmt.execute();
			ResultSet rs = pstmt.getGeneratedKeys();
			
			if (rs.next() && primaryKeyIsSerial)
				return findByPrimaryKey(rs.getObject(1));
			else if(rs.next()) {
				return findByPrimaryKey(rs.getObject(1));
			}
				return null;
			
		} catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Finds an object by its primary key value.
	 * 
	 * @param primaryKey is the primary key that will be queried by.
	 * @return T an object found by the primary key or null if none exists.
	 */
	@Override
	public T findByPrimaryKey(Object primaryKey) throws NoSuchFieldException, SQLException {

		// Check to see if the primary key exists within the class table.
		Field pk = null;
		ArrayList<T> objs = null;
		try {
			// Find the primary key field if one exists.
			pk = getPKField();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Make another check to see if the column name is safe.
		if (!isColumnNameSafe(pk.getName()))
			throw new SQLException("Name contains invalid characters.");

		// Create a SQL string to query the table by.
		String sql = "Select * FROM " + tClass.getSimpleName().toLowerCase() + " WHERE " + pk.getName() + "= ?";

		// Establish a connection and query the database.
		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, primaryKey);
			ResultSet rs = pstmt.executeQuery();
			objs = getTObjects(rs);
//			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// Return the result of the query.
		return ((!objs.isEmpty()) ? objs.get(0) : null);
	}

	@Override
	public T findByColumnName(Object columnName) throws NoSuchFieldException, SQLException {
		Field entry = null;
		ArrayList<T> objects = null;
		// See if the field exists and if not do not return anything
		try {
			entry = tClass.getField(columnName.toString());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}

		// Check to ensure that the columnname is safe.
		if (!isColumnNameSafe(entry.getName()))
			throw new SQLException("Column name contains invalid characters.");

		// Create a query to locate the entry by its columnname
		// As a note this could return more then one entry but only the first will be
		// considered.

		String sql = "Select * from " + tClass.getSimpleName().toLowerCase() + " WHERE " + entry.getName() + "= ?";

		// Connect to the DB and attempt the query.

		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, columnName);
			ResultSet rs = pstmt.executeQuery();
			objects = getTObjects(rs);
//			conn.close();
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
		// See if the field exists and if not do not return anything
		try {
			entry = tClass.getField(columnName.toString());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}

		// Check to ensure that the columnname is safe.
		if (!isColumnNameSafe(entry.getName()))
			throw new SQLException("Column name contains invalid characters.");

		// Create a query to locate the entry by its columnname
		// As a note this could return more then one entry but only the first will be
		// considered.

		String sql = "Select * from " + tClass.getSimpleName() + " WHERE " + entry.getName() + "= ?";

		// Connect to the DB and attempt the query.

		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, columnName);
			ResultSet rs = pstmt.executeQuery();
			objects = getTObjects(rs);
//			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ((!objects.isEmpty()) ? objects : null);
	}

	@Override
	public boolean updateByPrimaryKey(T updatedObj) {

		// Attempt to see if the class has a pk field.
		try {
			Field pk = getPKField();
			if (Modifier.isPrivate(pk.getModifiers()))
				pk.setAccessible(true);

			// Set an ID equal to the pk of the updated object.
			Object id = pk.get(updatedObj);

			// Check to see if the primary key exists within the DB.
			if (findByPrimaryKey(id) == null)
				return false;
		} catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			System.err.println("No object found by that primary key.");
			return false;
		}

		// Attempt to query the DB
		try {
//			Connection conn = SessionManager.getConnection();
			String sql = getUpdateString().toLowerCase();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			// Modify the updated sql statement then execute.
			pstmt = getPreparedUpdate(pstmt, updatedObj);
			boolean excuted = pstmt.execute();
//			conn.close();
			return excuted;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteByPrimaryKey(Object primaryKey, boolean cascade) throws NoSuchFieldException, SQLException {
		Field pk = null;
		boolean executed = false;

		pk = getPKField();

		if (!isColumnNameSafe(pk.getName()))
			throw new SQLException("Name contains invalid characters");

		// Create a query to delete by
		String sql = "DELETE FROM " + tClass.getSimpleName().toLowerCase() + " WHERE " + pk.getName() + " = ?";

		// Establish a connection and attempt to query
		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toLowerCase());
			pstmt.setObject(1, primaryKey);
			executed = pstmt.execute();
//			conn.close();
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
		StringBuilder sql = new StringBuilder("SELECT * FROM " + tClass.getSimpleName() + " WHERE ");

		for (Map.Entry<String, Object> entry : qualifiers.entrySet()) {
			sql.append(entry.getKey()).append(" = ? AND ");
		}

		int index = sql.lastIndexOf(" AND ");
		sql.delete(index, index + 5);

		try {
//			Connection conn = SessionManager.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql.toString().toLowerCase());

			int counter = 1;
			for (Map.Entry<String, Object> entry : qualifiers.entrySet()) {
				stmt.setObject(counter, entry.getValue());
				counter++;
			}
			ResultSet rs = stmt.executeQuery();
			ArrayList<T> found = getTObjects(rs);
//			conn.close();
			return found;

		} catch (SQLException throwables) {
			throwables.printStackTrace();
			System.exit(1);
		}

		return null;
	}

	/**
	 * Helper method that returns a string builder after all periods have been
	 * placed with underscores
	 * 
	 * @param builder
	 * @return
	 */
	private StringBuilder replacePeriods(StringBuilder builder) {
		int index = builder.indexOf(".");

		while (index != -1) {
			builder.replace(index, index + 1, "_");
			index = builder.indexOf(".");
		}
		return builder;
	}

	/**
	 * Helper method to find which field is the primary key within tClass.
	 * 
	 * @return the field that is the primary key.
	 * @throws NoSuchFieldException if no field is a primary key.
	 */
	public Field getPKField() throws NoSuchFieldException {
		// Make a call to the helper method to acquire the column fields.
		ColumnField[] columns = getColumnFields();

		// For each column field check to see if there is a sql constraint for the
		// primary key.
		for (ColumnField c : columns) {
			if (c.getConstraint().equals(SQLConstraints.PRIMARY_KEY))
				// Returns the name of the primary key field.
				return tClass.getDeclaredField(c.getColumnName());
		}

		throw new NoSuchFieldException("Class does not have a column with a primary key constraint");
	}

	/**
	 * Helper method to find which field is the foreign key within tClass.
	 * 
	 * @return the field that is the foreign key.
	 * @throws NoSuchFieldException if no field has the foreign key constraint.
	 * @deprecated Replaced by getFKFields.
	 */
	public Field getFKField() throws NoSuchFieldException {
		ColumnField[] columns = getColumnFields();

		// For each column, check to see if the SQL constraint is a foreign key.
		for (ColumnField c : columns) {
			if (c.getConstraint().equals(SQLConstraints.FOREIGN_KEY))
				return tClass.getDeclaredField(c.getColumnName());
		}
		throw new NoSuchFieldException("Class does not have a column with a foreign key constraint");
	}
	
	/**
	 * Helper method to find the fields that are labeled as foreign keys.
	 * @return A list containing all foreign keys.
	 * @throws NoSuchFieldException if no field is found with a foreign key constraint.
	 */
	public ArrayList<Field> getFKFields() throws NoSuchFieldException{
		ColumnField[] columns = getColumnFields();
		
		ArrayList<Field> foreignKeys = new ArrayList<Field>();
		
		for(ColumnField c : columns) {
			if(c.getConstraint().equals(SQLConstraints.FOREIGN_KEY))
				foreignKeys.add(tClass.getDeclaredField(c.getColumnName()));
		}
		
		if(foreignKeys.isEmpty())
			throw new NoSuchFieldException("Class does not have any foreign key constraints");
		
		return foreignKeys;
	}

	private ArrayList<T> getTObjects(ResultSet rs) throws SQLException {
		// Acquire the column fields from tClass
		ColumnField[] columns = getColumnFields();

		// Iterate through the result set to construct the new object
		ArrayList<T> objects = new ArrayList<>();

		while (rs.next()) {
			Constructor<T> emptyCon = null;
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
				Field field = null;

				String columnName = columns[i - 1].getColumnName();

				try {
					field = tClass.getDeclaredField(columnName);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
					System.exit(1);
				}

				if (Modifier.isPrivate(field.getModifiers())) {
					field.setAccessible(true);
				}

				try {

					if (field.getType().isEnum()) {
						int constant = (int) rs.getObject(columnName) - 1;
						field.set(emptyObject, field.getType().getEnumConstants()[constant]);
					} else {
						Object insert = rs.getObject(columnName);
						if (insert.getClass().equals(BigDecimal.class)) {
							if (field.getType().getName().equals(Double.class.getName())
									|| field.getType().getName().equals(double.class.getName())) {
								field.set(emptyObject, ((BigDecimal) insert).doubleValue());
							}
						} else {
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
	 * 
	 * @return returns a string version of the update statement.
	 * @throws SQLSyntaxErrorException if the column field in the class has invalid
	 *                                 characters.
	 */
	private String getUpdateString() throws SQLSyntaxErrorException {
		StringBuilder builder = new StringBuilder("Update " + tClass.getSimpleName() + " SET ");
		StringBuilder qualifier = new StringBuilder("WHERE ");

		ColumnField[] columns = getColumnFields();

		for (ColumnField column : columns) {

			String columnName = column.getColumnName();
			if (!isColumnNameSafe(columnName))
				throw new SQLSyntaxErrorException("Column name contains invalid characters!");

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
		builder.delete(index, index + 2);
		builder.append(qualifier);

		return builder.toString();
	}

	/**
	 * Helper method to find the column fields from tClass. Effectively a
	 * ColumnField is:<br>
	 * - The {@code name} of the field.<br>
	 * - The {@code type} of the field.<br>
	 * - The {@code SQL constraint} that the field has.<br>
	 * It also double checks to ensure the the column field is indeed annotated as
	 * {@code @Column}
	 * 
	 * @return ColumnField[], returns an array of fields that represent
	 * @throws SQLException Throws this exception if anny of the annotated fields
	 *                      are invalid.
	 */
	private ColumnField[] getColumnFields() {

		// Acquires the list of fields given by the class table.
		List<Field> fields = new ArrayList<Field>(Arrays.asList(tClass.getDeclaredFields()));
		boolean hasPrimaryKey = false;
		// For each field in the tClass, loop through and add them to an array.
		ColumnField[] columns = new ColumnField[fields.size()];
		int index = 0;

		for (Field f : fields) {

			ColumnField tClassColumn = null;

			if (Modifier.isPrivate(f.getModifiers()))
				f.setAccessible(true);

			/*
			 * Check the field to ensure that the field has the following annotations filled
			 * out. columnName - the column has a name. columnType - the column has a type.
			 * columnConstraint - the column's constraint. Finally when the entire list
			 */
			try {
				// Make sure the field has an annotation for the column.
				if (f.getAnnotations().length != 0) {
					// Check all annotations just to be safe.
					Annotation[] annoArr = f.getAnnotations();
					for (Annotation a : annoArr) {
						// Check to see if the field is a column
						if (a.annotationType().equals(com.ollivanders.annotations.Column.class)) {
							Column c = f.getAnnotation(Column.class);
							// Check the column's information before continuing.
							if (!isColumnNameSafe(c.columnName()))
								throw new SQLException("Column name contains invalid characters");

							tClassColumn = new ColumnField(c.columnName(), c.columnType(), c.columnConstraint());
							if(tClassColumn.getConstraint().equals(SQLConstraints.PRIMARY_KEY))
								hasPrimaryKey = true;
						}
					}
					
					//Check to see if a primary key is required and if one exists.
					if(allowNoPrimaryKey == false && hasPrimaryKey != true)
						throw new SQLException("No primary key found in the class table");
				}

				columns[index] = tClassColumn;
				index++;
				// Catch any exceptions that may occur.
			} catch (IllegalArgumentException | SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		return columns;
	}

	/**
	 * A helper method that preps the Update Prepared Statement
	 * 
	 * @param pstmt         the update statement to be updated
	 * @param updatedObject the object to be converted into an update string
	 * @return returns a {re[aredStatement that is the update statement
	 */
	private PreparedStatement getPreparedUpdate(PreparedStatement pstmt, T updatedObject) {
		ColumnField[] columns = getColumnFields();

		int count = 1;

		for (ColumnField column : columns) {

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
						int store = ((Enum) insert).ordinal() + 1;
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

		ColumnField[] columns = getColumnFields();

		for (ColumnField c : columns) {
			if (c.getConstraint().equals(SQLConstraints.PRIMARY_KEY) && c.getColumnType().equals(SQLType.SERIAL))
				continue;
			ib.append(c.getColumnName()).append(", ");
			vb.append("?, ");
		}

		int index = vb.lastIndexOf(", ");
		vb.delete(index, index + 2);
		vb.append(") ");

		index = ib.lastIndexOf(", ");
		ib.delete(index, index + 2);
		ib.append(") ");

		ib.append(vb);
		return ib.toString();
	}
	
	/**
	 * Closes the current connection
	 */
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a new connection.
	 */
	public void openConnection() {
		conn = SessionManager.getConnection();
	}
	
	/**
	 * Determines the SQL type of a column based on the string given of its data
	 * type.
	 * 
	 * @deprecated Column annotation now contains the information for the SQL type.
	 * @param type The type being checked for
	 * @return SQLType A ENUM of the type matching the string.
	 */
	@SuppressWarnings("unused")
	private SQLType determineSQLType(SQLType type) {
		if (type.equals("Integer"))
			return SQLType.INTEGER;
		if (type.equals("String"))
			return SQLType.VARCHAR;
		if (type.equals("Boolean"))
			return SQLType.BOOLEAN;
		else
			return null;
	}
}
