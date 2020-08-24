package com.revature.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;


/**
 * Class used to retrieve DAO Implementations. Serves as a factory. Also manages a single instance of the database connection.
 */
public class DAOUtilities {

	private static final String CONNECTION_USERNAME = "postgres";
	private static final String CONNECTION_PASSWORD = System.getenv("postgres_pw");
	private static final String URL = System.getenv("db_url");
	private static Connection connection;
	private static Logger log = Logger.getLogger(DAOUtilities.class);
	
	public static synchronized Connection getConnection() throws SQLException {
		if (connection == null) {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				log.fatal("Could not register driver in DAOUtilities.");
				e.printStackTrace();
			}
			connection = DriverManager.getConnection(URL, CONNECTION_USERNAME, CONNECTION_PASSWORD);
		}
		
		//If connection was closed then retrieve a new connection
		if (connection.isClosed()){
			// log.info("Opening new connection to " + URL + " with account " + CONNECTION_USERNAME);
			connection = DriverManager.getConnection(URL, CONNECTION_USERNAME, CONNECTION_PASSWORD);
		}
		return connection;
	}
}