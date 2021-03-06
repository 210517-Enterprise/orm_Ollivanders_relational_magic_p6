package com.ollivanders.util;

import java.util.Objects;

import com.ollivanders.model.SQLConstraints;
import com.ollivanders.repos.SQLType;


public class ColumnField {
	 //
    private String columnName;
    private SQLType columnType;
    private SQLConstraints constraint;

    /**
     * Constructor that creates the specific column. One column should be made as a primary key
     * @param columnName The name of the column constraint. Should only contain alpha-numeric and underscore characters
     * @param columnType The column type. Not enforced, but if not correct sql syntax will crash the orm later
     * @param constraint The constraint of the column. One column should be made a primary key, and currently only one
     *                   constraint can be given per column
     */
    public ColumnField(String columnName, SQLType columnType, SQLConstraints constraint) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.constraint = constraint;
    }

    /**
     * Helper method that returns the column as a string
     * @return a string version of the column
     */
    public String getRowAsString() {
        String line = columnName+" "+ SQLType.stringRepresentation(columnType);
       
        if (constraint != null && !constraint.equals(SQLConstraints.FOREIGN_KEY)) { //Foreign key relationships are not established
            line = line+" "+ SQLConstraints.stringReprestation(constraint)+",";
        } else {
            line += ",";
        }
        return line;
    }

    /**
     * Getter method for the column name
     * @return returns the column name
     */
    public String getColumnName() {
        return columnName;
    }

    public ColumnField setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Getter method for the column type
     * @return returns the column type
     */
    public SQLType getColumnType() {
        return columnType;
    }

    public ColumnField setColumnType(SQLType columnType) {
        this.columnType = columnType;
        return this;
    }

    /**
     * Getter method for the constraint
     * @return returns the constraint
     */
    public SQLConstraints getConstraint() {
        return constraint;
    }

    public ColumnField setConstraint(SQLConstraints constraint) {
        this.constraint = constraint;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnField that = (ColumnField) o;
        return Objects.equals(columnName, that.columnName) && Objects.equals(columnType, that.columnType) && constraint == that.constraint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, columnType, constraint);
    }

    @Override
    public String toString() {
        return "ColumnField{" +
                "columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", constraint=" + constraint +
                '}';
    }
}