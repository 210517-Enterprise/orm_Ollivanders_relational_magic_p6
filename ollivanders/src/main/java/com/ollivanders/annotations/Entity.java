package com.ollivanders.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Annotations in Java are a type of marker interface
 * 
 * Annotations start with @
 * Annotations do not change the ACTION of a compiled program
 * Annotations help to associate metadata to the program elements (constructors, fields, methods, classes)
 * 
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

	String tableName();
	
	
	
}
