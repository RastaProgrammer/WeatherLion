package com.bushbungalo.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bushbungalo.WeatherLionMain;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 */

public class ConnectionManager
{
	private static final String TAG = "ConnectionManager";
	private static ConnectionManager instance = null;

	private final String SQLITE_CONN_STRING = "jdbc:sqlite:" 
											+ WeatherLionMain.MAIN_STORAGE_DIR 
											+ WeatherLionMain.MAIN_DATABASE_NAME;	
	private DBType dbType = DBType.SQLite;
	private Connection conn = null;
	

	// default constructor
	private ConnectionManager()
	{
	}// end of default constructor
	
	// gets an instance of the database connection
	public static ConnectionManager getInstance()
	{
		if ( instance == null )
		{
			instance = new ConnectionManager();
		}// end of if block
		return instance;
	}// end of method getInstance
	
	// method that sets the database type
	public void setDBType( DBType dbType )
	{
		this.dbType = dbType;
	}// end of method
	
	// method that opens a database connection
	private boolean openConnection()
	{
		try
		{
			switch ( dbType )
			{
				case SQLite:
					// automatically creates the database if it does not exist
					conn = DriverManager.getConnection( SQLITE_CONN_STRING );
					return true;
				default: 
					return false;
			}// end of switch block
		}// end of try black
		catch ( SQLException e )
		{
			 UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::openConnection [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
			 
			return false;
		}// end of catch block
	}// end of method openConnection
	
	// method that gets a database connection
	public Connection getConnection()
	{
		if ( conn == null )
		{
			if ( openConnection() )
			{
				return conn;
			}// end of if block
			else
			{
				return null;
			}// end of else block
		}// end of if block
		return conn;
	}// end of method getConnection
	
	// method that closes the database connection
	public void close()
	{
		try
		{
			conn.close();
			conn = null;
		}// end of try block 
		catch ( Exception e )
		{
			UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::close [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
	}// end of method close()
}// end of class ConnectionManager
