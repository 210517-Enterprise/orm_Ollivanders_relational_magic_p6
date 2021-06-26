package com.ollivanders.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ollivanders.model.SQLConstraints;
import com.ollivanders.repos.SQLType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Column {
	String columnName();
	SQLType columnType();
	SQLConstraints columnConstraint();
}
