package com.bushbungalo.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/16/17
 * <br />
 * <b style="margin-left:-40px">Comments:</b>
 * <br />
 * Helper class for working with a remote server.
 */

public class HttpHelper
{
	private static final String TAG = "HttpHelper";
	
    /**
     * Returns text from a URL on a web server
     *
     * @param address The address of the web service
     * @return Text from a URL on a web server
     * @throws IOException
     */
    public static String downloadUrl( String address ) throws IOException
    {

        InputStream is = null;

        try
        {
            URL url = new URL( address );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout( 10000 );
            conn.setConnectTimeout( 15000 );
            conn.setRequestMethod( "GET" );
            conn.setDoInput( true );
            conn.connect();

            int responseCode = conn.getResponseCode();

            if ( responseCode != 200 )
            {
                throw new IOException( "Got response code " + responseCode );
            }// end of if block

            is = conn.getInputStream();
            return readStream( is );

        }// end of try block
        catch ( IOException e )
        {
        	UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::downloadUrl [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );   	
        }// end of catch block
        finally
        {
            if ( is != null )
            {
                is.close();
            }// end of if block
        }// end of finally block

        return null;
    }// end of method downloadUrl

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream Data received from the service
     * @return A JSON formatted {@code String} received from web service
     * @throws IOException
     */
    private static String readStream( InputStream stream ) throws IOException
    {

        byte[] buffer = new byte[ 1024 ];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream out = null;

        try
        {
            int length = 0;
            out = new BufferedOutputStream( byteArray );

            while ( ( length = stream.read( buffer ) ) > 0 )
            {
                out.write( buffer, 0, length );
            }// end of while loop

            out.flush();

            return byteArray.toString();
        }// end of try block
        catch ( IOException e )
        {
        	UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::readStream [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
        	
            return null;
        }// end of catch block
        finally
        {
            if ( out != null )
            {
                out.close();
            }// end of if block
        }// end of finally block
    }// end of method downloadUrl
}// end of class HttpHelper