package com.ollivanders.repos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ollivanders.exceptions.InvalidSQLType;

/**
 * An enum that restricts the SQL types that are used. Note this should never be called
 * by the user and should only be used by the GenericClassRepository.
 * @author castl
 *
 */
public enum SQLType {
	INTEGER, SERIAL, VARCHAR, BOOLEAN;
	
	/**
	 * Convience method that 
	 * @param type the SQL type being used
	 * @return the string representation of the SQL type
	 * @throws InvalidSQLType whenever a SQLType that is unexpected is encountered.
	 */
	public static String stringRepresentation(SQLType type) throws InvalidSQLType{
		switch (type) {
		case INTEGER:
			return "INTEGER";
		case SERIAL:
			return "SERIAL";
		case VARCHAR:
			return "VARCHAR";
		case BOOLEAN:
			return "BOOLEAN";
		default:
			throw new InvalidSQLType();
		}
	}
}
