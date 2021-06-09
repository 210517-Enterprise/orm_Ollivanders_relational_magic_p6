package com.ollivanders.util;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class PostgreSQLSessionFactory implements SeshFactory{
	
	   private static BasicDataSource ds = new BasicDataSource();
	    private final String databaseName = "postgresql";

	    // For the postgreSQL connection to exist, need the postgresql driver
	    static {
	        try {
	            Class.forName("org.postgresql.Driver");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }

	    /**
	     * Public constructor for the class. Since we "ostensibly" don't know the SessionFactory that will be made,
	     * need public access so that it can be instantiated at a later date.
	     * @param db the database object to use to set the connection
	     */
	    public PostgreSQLSessionFactory(Database db) {
	        ds.setUrl(db.getUrl());
	        ds.setUsername(db.getLoginName());
	        ds.setPassword(db.getPassword());
	        ds.setMinIdle(db.getMinIdle());
	        ds.setMaxIdle(db.getMaxIdle());
	        ds.setMaxOpenPreparedStatements(db.getMaxOpenPreparedStatements());
	    }

	    /**
	     * Public getter for the connection. Used by the session manager to reutn a connection to the user.
	     * @return returns a Connection
	     * @throws SQLException throws an SQLException if there is some problem with connecting to the database
	     */
	    public Connection getConnection() throws SQLException {
	        return ds.getConnection();
	    }
	}
