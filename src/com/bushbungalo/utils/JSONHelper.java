package com.bushbungalo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.bushbungalo.model.CityData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Paul O. Patterson 
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/28/17
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>06/27/19 - Added method {@code saveToJSONFile}.</li>
 * 		<li>06/28/19 - Added method {@code jsonPrettify}.</li>
 * 		<li>06/29/19 - Added methods {@code toJSONArray} and {@code toJSONObject}.</li>
 * </ul>
 */

public class JSONHelper
{
    public static String TAG = "JSONHelper";
    public static List<CityData> cityDataList = new ArrayList<>();
    public static final String PREVIOUSLY_FOUND_CITIES_JSON = "res/storage/locations_used/json/previous_cities.json";

    /**
     * Accepts a CityData object and exports it to an JSON file.
     * 
     * @param dataItem	The CityData object to be written to a JSON file
     * @return	A {@code boolean} value representing success or failure
     */
    public static boolean exportToJSON( CityData dataItem )
    {
    	if (dataItem != null)
        {
    		DataItems cityData = new DataItems();
            cityData.setDataItem( dataItem );
            Gson gson = new Gson();
            String jsonString = null;
            
            if( cityDataList == null ) cityDataList = new ArrayList<>();
                        
            FileOutputStream fileOutputStream = null;
            File previousCities = new File( PREVIOUSLY_FOUND_CITIES_JSON );
            File parentDir = new File( 
        		Paths.get( previousCities.toString() ).getParent().toAbsolutePath().toString() );
            
            // check to see if the parent directory exists
            if( !parentDir.exists() )
            {
            	parentDir.mkdirs();
            }// end of if block

            // attempt to import data from local storage        
            if( previousCities.exists() )
            {
            	cityDataList = importFromJSON() == null ?  
            				   new  ArrayList<>() :
            				   importFromJSON();
            	cityDataList.add( dataItem );
            	jsonString = gson.toJson( cityDataList );
            }// end of if block
            else
            {
            	cityDataList.add( dataItem );
            	jsonString = gson.toJson( cityDataList );
            }// end of else block                      
            
            try
            {
                fileOutputStream = new FileOutputStream( previousCities );
                fileOutputStream.write( jsonPrettify( jsonString ).getBytes() );
                
                return true;
            }// end of try block
            catch ( IOException e )
            {
            	UtilityMethod.logMessage( "severe", e.getMessage(),
			        TAG + "::exportToJSON [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );            	
            }// end of catch block
            finally
            {
                // close the file writer object
                if( fileOutputStream != null )
                {
                    try
                    {
                        fileOutputStream.close();
                    } // end of try block
                    catch ( IOException e )
                    {
                    	UtilityMethod.logMessage( "severe", e.getMessage(),
        			        TAG + "::exportToJSON [line: " +
        			        UtilityMethod.getExceptionLineNumber( e )  + "]" );   
                    }// end of catch block
                }// end of if block
            }// end of finally block
        }// end of if block
    	
        return false;
        
    }// end of method exportToJSON

    /**
     * Converts JSON data and converts them into a list of CityData objects.
     * 
     * @return	A {@code List} containing CityData objects that were converted from JSON
     */
    public static List< CityData > importFromJSON()
    {
        // if there is a file present then it will contain a list with at least one object
        File file = new File( PREVIOUSLY_FOUND_CITIES_JSON );
        cityDataList = null;
        
        try(
        		 FileReader reader = new FileReader( file );
    	)
    	{
            Gson gson = new Gson();

            // convert the file JSON into a list of objects
            cityDataList = gson.fromJson( reader, new TypeToken< List< CityData> >() {}.getType() );
        }// end of try block
        catch ( FileNotFoundException e )
        {
        	UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::importFromJSON [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );        	
        }// end of catch block
        catch ( IOException e )
        {
        	UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::importFromJSON [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
        }// end of catch block 

        return cityDataList;
    }// end of method importFromJSON
    
    /***
     * Saves JSON data to a local file for quicker access later.
     * 
     * @param jsonData	JSON data formatted as a {@code String}.
     * @param path The path where the data will reside locally.
     * @return	True/False depending on the success of the operation.
     */    
    public static boolean saveToJSONFile( String jsonData, String path )
    {
        boolean fileSaved = false;
        FileOutputStream fileOutputStream = null;
        File storageFile = new File( path );
        File parentDirectory = new File( 
        		Paths.get( storageFile.toString() ).getParent().toAbsolutePath().toString() );

        if (jsonData != null)
	    {
	        // the storage directory must be present before file creation
	        if (!parentDirectory.exists())
	        {
	        	parentDirectory.mkdirs();
	        }// end of if block
	
	        if (!storageFile.exists())
	        {
	            try
	            {
	                fileOutputStream = new FileOutputStream( path );
	                fileOutputStream.write( jsonPrettify( jsonData ).getBytes() );
	
	                fileSaved = true;
	            }// end of try block
	            catch ( IOException e )
	            {
	                fileSaved = false;
	                UtilityMethod.logMessage( "severe", e.getMessage(),
                		TAG + "::saveToJSONFile [line: " +
        		        UtilityMethod.getExceptionLineNumber( e )  + "]" );	                
	            }// end of catch block
	            finally
	            {
	                // close the file writer object
	                if( fileOutputStream != null )
	                {
	                    try
	                    {
	                        fileOutputStream.close();
	                    } // end of try block
	                    catch ( IOException e )
	                    {
	                    	UtilityMethod.logMessage( "severe", e.getMessage(),
	                    		TAG + "::saveToJSONFile [line: " +
	            		        UtilityMethod.getExceptionLineNumber( e )  + "]" );	 
	                    }// end of catch block
	                }// end of if block
	            }// end of finally block	
	        }// end of if block
	        else
	        {
	            // The file already exists
	            fileSaved = false;
	        }// end of else block                             
	    }// end of if block
        
        return fileSaved;             
                   
    }// end of method saveToJSONFile
    
    /**
     * Attempt to convert a string to a {@code JsonObject}. 
     * @param strJSON	A string representation of JSON data.
     * @return	A {@code JsonObject} or {@code null} if unsuccessful
     */
    public static JsonObject toJSONObject( String strJSON )
    {
    	JsonObject json = null;
        JsonParser parser = new JsonParser();
        
        try 
        {
        	json = parser.parse( strJSON ).getAsJsonObject();           
        }// end of try block
        catch ( Exception e ) 
        {
        	UtilityMethod.logMessage( "severe", e.getMessage(),
    			TAG + "::toJSONObject [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );	        	
        }// end of catch block       
        
        return json;
    }// end of method toJSONObject

    /**
     * Attempt to convert a string to a {@code JsonObject}. 
     * @param strJSON	A string representation of JSON data.
     * @return	A {@code JsonArray} or {@code null} if unsuccessful
     */
    public static JsonArray toJSONArray( String strJSON )
    {
    	JsonArray json = null;
        JsonParser parser = new JsonParser();
        
        try 
        {
        	json = parser.parse( strJSON) .getAsJsonArray();           
        }// end of try block
        catch ( Exception e ) 
        {
        	UtilityMethod.logMessage( "severe", e.getMessage(),
    			TAG + "::toJSONArray [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );       	
        }// end of catch block       
        
        return json;
    }// end of method toJSONArray
    
    /**
     * Returns JSON data to in a formatted (pretty) structure
     * @param jsonString	JSON data formatted as a {@code String}.
     * * </p>
	 * @author kencoder {@link https://coderwall.com/kenlakoo}
	 * <br />
	 * <b style="margin-left:-40px">Retrieved from:</b><br />
	 * <a href='https://coderwall.com/p/ab5qha/convert-json-string-to-pretty-print-java-gson'>Stack Overflow</a>
     * @return
     */
    public static String jsonPrettify( String jsonString ) 
    {
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = null;
        JsonArray jArray = toJSONArray( jsonString );
        JsonObject jObject = toJSONObject( jsonString );
        
        if( jArray != null )
        {
        	prettyJson = gson.toJson( jArray );
        }// end of if block
        else if( jObject != null )
        {
        	prettyJson = gson.toJson( jObject );
        }// end of else if block
        else 
        {
        	UtilityMethod.logMessage( "severe", "The string passed is not valid JSON data.", 
    			TAG + "::jsonPrettify" );
        }// end of else if block        

        return prettyJson;       
    }// end of method jsonPrettify

    public static class DataItems
    {
        CityData dataItems;

        public CityData getDataItems()
        {
            return dataItems;
        }

        public void setDataItem( CityData dataItems )
        {
            this.dataItems = dataItems;
        }
    }// end of class DataItems     
}// end of class JSONHelper
