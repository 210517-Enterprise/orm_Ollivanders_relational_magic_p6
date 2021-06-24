package com.ollivanders.exceptions;

/**
 * This exception describes when an invalid SQL type is used.
 * @author castl
 */
public class InvalidSQLType extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public InvalidSQLType(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
	
	public InvalidSQLType(String errorMessage) {
		super(errorMessage);
	}
	
	public InvalidSQLType() {
		super();
	}
}
