package com.bushbungalo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 02/08/19
 * <br />
 * <b style="margin-left:-40px">Description:</b><br />
 * <p>
 * 		This class is responsible for formatting the HTML output log file.
 * </p>
 */

public class WidgetHtmlLoggingFormatter extends Formatter
{
	private Date currentDate = new Date();
	private String logDate = new SimpleDateFormat( "MMM-dd-yy" ).format( currentDate );
	private String previousLog = "res/log/html/WidgetLog_" + logDate + ".html";
	
	// this method is called for every log records
	public String format( LogRecord rec )
	{
		StringBuffer buf = new StringBuffer( 1000 );
		buf.append( "<tr>" );

		// highlight any levels >= WARNING in red
		if( rec.getLevel().intValue() >= Level.SEVERE.intValue() )
		{
			buf.append( "<td style=\"color:red\">" );
			buf.append( "<b>" );
			buf.append( rec.getLevel() );
			buf.append( "</b>" );
		}// end of if block
		else if( rec.getLevel().intValue() >= Level.WARNING.intValue() )
		{
			buf.append( "<td style=\"color:orange\">" );
			buf.append( "<b>" );
			buf.append( rec.getLevel() );
			buf.append( "</b>" );
		}// end of if block
		else
		{
			buf.append( "\t<td>" );
			buf.append( rec.getLevel() );
		}// end of else block

		buf.append( "</td>\n" );
		buf.append( "\t<td>" );
		buf.append( calcDate( rec.getMillis() ) );
		buf.append( "</td>\n" );
		buf.append( "\t<td>" );
		buf.append( rec.getLoggerName() );
		buf.append( "</td>\n" );
		buf.append( "\t<td>" );
		buf.append( formatMessage( rec ) );
		buf.append( "</td>\n" );
		buf.append( "</tr>\n" );

		return buf.toString();
	}// end of method format

	private String calcDate( long millisecs )
	{
		SimpleDateFormat date_format = new SimpleDateFormat( "MMM dd,yyyy h:mm a" );
		Date resultdate = new Date( millisecs );
		return date_format.format( resultdate );
	}// end of method calDate

	/** this method is called just after the handler using this
	 * formatter is created
	 */
	public String getHead( Handler h )
	{		
		if( isFileEmpty( previousLog ) )
		{
			return "<html>" 
					+ "<head>"
					+ "<style type='text/css'>"
					+ "table { width: 100%; border-collapse: collapse; }\n"
					+ "th { font:bold 10pt Tahoma; }\n"
					+ "td { font:normal 10pt Tahoma; }\n"
					+ "h1 {font:normal 11pt Tahoma;}\n"
					+ "</style>\n"
					+ "</head>\n"
					+ "<body>\n"
					+ "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
					+ "<tr align=\"left\">\n"
					+ "\t<th style=\"width:10%\">Log Level</th>\n"
					+ "\t<th style=\"width:15%\">Time</th>\n"
					+ "\t<th style=\"width:15%\">Source</th>\n"
					+ "\t<th style=\"width:75%\">Log Message</th>\n"
					+ "</tr>\n";
		}// end of if block
		else 
		{
			return "<table border=\"0\" cellpadding=\"5\" cellspacing=\"3\">\n"
					+ "<tr align=\"left\">\n"
					+ "\t<th style=\"width:10%\"></th>\n"
					+ "\t<th style=\"width:15%\"></th>\n"
					+ "\t<th style=\"width:15%\"></th>\n"
					+ "\t<th style=\"width:75%\"></th>\n"
					+ "</tr>\n";
		}// end of else block
	}// end of method getHead

	// this method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h)
	{
		if( isFileEmpty( previousLog ) )
		{
			return "</table>\n</body>\n</html>";
		}// end of if block
		else
		{
			return "</table>\n";
		}// end of else block
	}// end of method getTail
	
	private boolean isFileEmpty( String filePath )
	{
		FileInputStream fis;
		
		try
		{
			fis = new FileInputStream( new File( filePath ) );
			int b = fis.read();
			
			fis.close();
			
			if ( b == -1 )  
			{  
			  return true;  
			}// end of if block
		}// end of try block
		catch ( FileNotFoundException e )
		{
			return false;
		} // end of catch block 
		catch ( IOException e )
		{
			return false;
		} // end of catch block
		
		return false;
	}// end of method isFileEmpty
}// end of class WidgetHtmlLoggingFormatter
