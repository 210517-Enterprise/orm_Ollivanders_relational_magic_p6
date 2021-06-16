package com.ollivanders.util;

import java.sql.Connection;
import java.sql.SQLException;

public interface SeshFactory {

	
	Connection getConnection() throws SQLException;
}
