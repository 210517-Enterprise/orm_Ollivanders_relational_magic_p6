package com.ollivanders.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.management.modelmbean.XMLParseException;

import com.ollivanders.util.XMLReader;

public class SessionManager {

    private static SessionManager sess;

    static {
        try {
            sess = new SessionManager();
        } catch (XMLParseException e) {
            e.printStackTrace();
        }
    }

    private static SeshFactory connections;

    /**
     * Parses the xml file for database information, then uses that to create a database session
     * @throws XMLParseException
     */
    private SessionManager() throws XMLParseException {
        Database db = XMLReader.getDatabaseSet();
        String dbName = db.getSqlDatabase();

        if (dbName.equals("postgresql")) {
            connections = new PostgreSQLSessionFactory(db);
        } else {
            throw new XMLParseException("An invalid database name given");
        }
    }

    /**
     * Access method for the connection.
     * @return Returns a connection to the database
     */
    public static Connection getConnection() {
        try {
            return connections.getConnection();
        } catch (SQLException throwables) {
            System.out.println("Couldn't make connection to database");
            throwables.printStackTrace();
        }
        return null;
    }
}