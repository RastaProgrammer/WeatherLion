package com.bushbungalo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bushbungalo.model.CityData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Paul O. Patterson 
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/28/17
 * <br />
 */

public class JSONHelper
{
    public static String TAG = "JSONHelper";
    public static List<CityData> cityDataList = new ArrayList<>();
    public static final String PREVIOUSLY_FOUND_CITIES_JSON = "res/storage/previous_cities.json";

    public static boolean exportToJSON( CityData dataItem )
    {
        DataItems cityData = new DataItems();
        cityData.setDataItem( dataItem );
        Gson gson = new Gson();
        cityDataList.add( dataItem );        

        FileOutputStream fileOutputStream = null;

        // attempt to import data from local storage        
        if( new File( PREVIOUSLY_FOUND_CITIES_JSON ).exists() )
        {
        	cityDataList = importFromJSON();
        }// end of if block        

        // only add the object if it is not already there
        if( !cityDataList.contains( dataItem ) )
        {
            cityDataList.add( dataItem );
        }//end of if block 
        
        // convert the list to a JSON string
        String jsonString = gson.toJson( cityDataList );
        
        // write a new file with the new list
        File previousCities = new File( PREVIOUSLY_FOUND_CITIES_JSON );
        
        try
        {
            fileOutputStream = new FileOutputStream( previousCities );
            fileOutputStream.write( jsonString.getBytes() );

            return true;
        }// end of try block
        catch ( IOException e )
        {
        	UtilityMethod.logMessage( "severe", e.getMessage(), "JSONHelper::exportToJSON" );
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
                	UtilityMethod.logMessage( "severe", e.getMessage(), "JSONHelper::exportToJSON" );
                }// end of catch block
            }// end of if block
        }// end of finally block

        return false;
    }// end of method  exportToJSON

    public static List< CityData > importFromJSON()
    {
        // if there is a file present then it will contain a list with at least one object
        File file = new File( PREVIOUSLY_FOUND_CITIES_JSON );

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
        	cityDataList = null;
            UtilityMethod.logMessage( "severe", e.getMessage(), "JSONHelper::importFromJSON" );
        }// end of catch block
        catch ( IOException e )
        {
        	cityDataList = null;
        	UtilityMethod.logMessage( "severe", e.getMessage(), "JSONHelper::importFromJSON" );
        }// end of catch block 

        return cityDataList;
    }// end of method importFromJSON

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
    }
}// end of class JSONHelper
