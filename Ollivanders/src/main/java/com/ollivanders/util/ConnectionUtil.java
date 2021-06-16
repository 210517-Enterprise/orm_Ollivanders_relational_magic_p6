package com.ollivanders.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionUtil {

private static Connection conn = null;
	
	private static final Logger log = LoggerFactory.getLogger(ConnectionUtil.class);
	

	private ConnectionUtil() {
		super();
	}
	
	// this is our getInstance() method
	public static Connection getConnection() {
	
		try {
			if (conn  != null && !conn.isClosed()) {
				return conn;
			}
		} catch (SQLException e) {
			log.error("We failed to reuse a Connection", e);
			return null;
		}
	String url = System.getenv("DB_URL");
	String username = System.getenv("DB_USERNAME");
	String password = System.getenv("DB_PASSWORD");
	
	try {
		conn = DriverManager.getConnection(url, username, password);
		log.info("DB connection established.");
	} catch (SQLException e) {
		log.error("failed to establish a connection");
		return null;
	}
	
	return conn;
	
}
	
	
}
