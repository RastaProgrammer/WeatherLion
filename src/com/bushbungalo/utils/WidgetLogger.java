package com.bushbungalo.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 02/08/19
 * <br />
 * <b style="margin-left:-40px">Description:</b><br />
 * <p>
 * 		The Widget logger class for the Weather Lion Widget project.
 * 		This serves as the root of the logging hierarchy for the application.
 * </p>
 */

public class WidgetLogger
{
	private static FileHandler fileTxt;
	private static SimpleFormatter formatterTxt;
	private static FileHandler fileHTML;
	private static Formatter formatterHTML;
	
	public static String Widget_LOGGER = "Widget_LOGGER";
	private static Logger widgetLogger;
	
	/**
	 * static initializer
	 */
	static 
	{
		widgetLogger = Logger.getLogger( Widget_LOGGER );
	}
	
	/** 
	 * Returns the singleton instance of the logger
	 * @return The WidgetLogger instance
	 */
	public static Logger getWidgetLogger()
	{
		return widgetLogger;
	}// end of method getWidgetLogger
	
	/**
	 * Initializes the logging system for our purposes
	 * 
	 * @throws IOException on error
	 */
	public static void setup() throws IOException
	{
		// create an get the global logger to configure it
		//Logger logger = Logger.getLogger(Widget_LOGGER);

		// suppress the logging output to the console
		// removes any handlers to the console logger
		Logger rootLogger = Logger.getLogger( "" );
		Handler[] handlers = rootLogger.getHandlers();
		
		if( handlers.length > 0 )
		{
			if( handlers[ 0 ] instanceof ConsoleHandler )
			{
				rootLogger.removeHandler( handlers[ 0 ] );
			}// end of if block
		}// end of if block

		String logPath = "res/log";
		File logDirectory = new File( logPath );
		File textLogDirectory = new File( logPath + "/text" );
		File htmlLogDirectory = new File( logPath + "/html" );
		boolean logFolderExists = logDirectory.exists();
		
		if( !logFolderExists )
		{
			/* if the log folder does not exist this means the log files do not
			 * either. 
			 */
			logDirectory.mkdirs();
			textLogDirectory.mkdirs();
			htmlLogDirectory.mkdirs();
		}// end of if block
		
		if( !textLogDirectory.exists() )
		{
			textLogDirectory.mkdirs();
		}// end of if block
		
		if( !htmlLogDirectory.exists() )
		{
			htmlLogDirectory.mkdirs();
		}// end of if block
		
		
		widgetLogger.setLevel( Level.INFO );
		
		Date currentDate = new Date();
		String logDate = new SimpleDateFormat( "MMM-dd-yy" ).format( currentDate );
		
		// all logs will be time coded
		fileTxt = new FileHandler( logPath + "/text/WidgetLog_" + logDate + ".txt", true );
		fileHTML = new FileHandler( logPath + "/html/WidgetLog_" + logDate + ".html", true );

		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter( formatterTxt );
		widgetLogger.addHandler( fileTxt );

		// create an HTML formatter
		formatterHTML = new WidgetHtmlLoggingFormatter();
		fileHTML.setFormatter( formatterHTML );

		widgetLogger.addHandler( fileHTML );
	}// end of method setup
	
	public static void closeFileHandlers()
	{
		try
		{
			fileTxt.close();
			fileHTML.close();
		}// end of if block 
		catch ( SecurityException e ) 
		{
		}// end of catch block
		catch( Exception e )
		{
		}// end of catch block
	}// end of method closeFileHandlers

	/**
	 * Adds all the handlers defined by the application to the provided logger.
	 * Thus any locally defined logger added to the WidgetLogger, will get 
	 * the same handlers applied uniquely to that logger.
	 * 
	 * @param logger Logger handler instance to add
	 */
	public static void addWidgetLoggerHandlers( Logger logger )
	{
		logger.addHandler( fileTxt );
		logger.addHandler( fileHTML );
	}// end of method addWidgetLoggerHandlers
}// end of class WidgetLogger
