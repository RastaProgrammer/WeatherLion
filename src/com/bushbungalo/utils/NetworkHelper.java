package com.bushbungalo.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/16/17
 * <br />
 */

public class NetworkHelper
{
	private static final String TAG = "NetworkHelper";
	
	/**
	 * Determines is the computer has an open Internet connection
	 * @return True/False dependent on the outcome of the check.
	 */
	public static boolean hasNetworkAccess()
	{
		Socket socket = new Socket();
		InetSocketAddress address = new InetSocketAddress( "www.google.com", 80 );
	   
		try 
		{
			socket.connect( address, 3000 );
			return true;
		}// end of try block
		catch ( Exception e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::hasNetworkAccess [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
			
			return false;
		}// end of catch block
		finally 
		{
			try
			{
				socket.close();
			}// end of try block
			catch ( IOException e )
			{
				UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
			        TAG + "::hasNetworkAccess [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );
			}// end of catch block
		}// end of finally block		
	}// end of method hasNetworkAccess
}// end of class NetworkHelper
