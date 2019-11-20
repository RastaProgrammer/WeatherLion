package com.bushbungalo.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Inherited;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.SwingWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bushbungalo.PreferenceForm;
import com.bushbungalo.WeatherLionMain;
import com.bushbungalo.WeatherLionWidget.WidgetUpdateService;
import com.bushbungalo.model.CityData;
import com.bushbungalo.model.GeoNamesGeoLocation;
import com.bushbungalo.model.GeoNamesGeoLocation.GeoNames;
import com.bushbungalo.model.HereGeoLocation;
import com.bushbungalo.model.YahooGeoLocation;
import com.bushbungalo.utils.HttpHelper;
import com.bushbungalo.utils.JSONHelper;
import com.bushbungalo.utils.UtilityMethod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>01/21/19 - Added param {@code service} to default constructor</li>
 * 		<li>03/23/19 - Added method {@link #getHereSuggestions} for Here Maps data</li>
 * </ul>
 */

@SuppressWarnings("unused")
public class CityDataService extends SwingWorker< String, Object >
{
	private static final String TAG = "CityDataService";
	
	private String m_url;
	private String m_service;
	private static String cityName = null;
	private static String countryName = null;	
	private static String countryCode = null;
	private static String regionName = null;
	private static String regionCode = null;
	private static String Latitude = null;
	private static String Longitude = null;		
	
	private int matchCount;
	
	public static boolean serviceRequest = false;
	
	private static final String QUERY_COMMAND = "name_equals";
	private String dataURL = null;
	private String response;

	public CityDataService( String url, String service )
	{
		this.m_url = url;
		this.m_service = service;
	}// end of default constructor

	public String getService() 
	{
		return m_service;
	}

	public void setService( String m_service )
	{
		this.m_service = m_service;
	}
	
	public String getUrl() 
	{
		return m_url;
	}

	public void setUrl( String m_url )
	{
		this.m_url = m_url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doInBackground() throws Exception
	{
		String uri = getUrl();
		dataURL = uri != null ? uri.toString() : null;		 		
		
		if ( dataURL != null )
        {
            if( dataURL.contains( "geonames" ) )
            {
                setService( "geo" );
            }// end of if block'
            else if( dataURL.contains( "api.here" ) )
            {
                setService( "here" );
            }// end of else block
        }// end of if block
		
		switch( getService() )
        {
        	case "geo":
	            String cityName = null;
	
	            if( UtilityMethod.hasInternetConnection() )
	            {
	                // the means that we only have GPS coordinates
	                if( dataURL.contains( "findNearbyPlaceNameJSON" ) )
	                {
	                    try
	                    {
	                        response = HttpHelper.downloadUrl( dataURL );
	
	                        String city;
	                        String countryCode;
	                        String countryName;
	                        String regionCode;
	                        String regionName;
	                        String currentLocation = null;
	
	                        try
	                        {
	                            Object json = new JSONTokener( response ).nextValue();
	
	                            // Check if a JSON was returned from the web service
	                            if ( json instanceof JSONObject)
	                            {
	                                // Get the full HTTP Data as JSONObject
	                                JSONObject geoNamesJSON = new JSONObject( response );
	                                // Get the JSONObject "geonames"
	                                JSONArray geoNames = geoNamesJSON.optJSONArray( "geonames" );
	
	                                JSONObject place = geoNames.getJSONObject(0);
	
	                                city = place.getString( "name" );
	                                countryCode = place.getString( "countryCode" );
	                                countryName = place.getString( "countryName" );
	                                regionCode = place.getString( "adminCode1" );
	                                regionName = countryCode.equalsIgnoreCase( "US" ) ?
	                                        UtilityMethod.usStatesByCode.get(regionCode) :
	                                        null;
	
	                                if ( regionName != null )
	                                {
	                                    currentLocation = city + ", " + regionName + ", "
	                                            + countryName;
	                                }// end of if block
	                                else
	                                {
	                                    currentLocation = city + ", " + countryName;
	                                }// end of else block
	                            }// end of if block
	                            else
	                            {
	                                // this means that the user entered a city manually
	                                currentLocation = response;
	                            }// end of else block
	                        }// end of try block
	                        catch ( JSONException e )
	                        {
	                            UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
	                                    TAG + "::handleWeatherData [line: " + UtilityMethod.getExceptionLineNumber( e ) + "]" );
	                        }// end of catch block
	
	                        cityName = currentLocation;
	                    }// end of try block
	                    catch ( IOException e )
	                    {
	                        UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
	                                TAG + "::handleWeatherData [line: " +
	                                        UtilityMethod.getExceptionLineNumber( e )  + "]" );
	
	                        response = null;
	                    }// end of catch block
	                }// end of if block
	                else // the URL contains the city name which can be extracted
	                {
	                    int start = dataURL.indexOf( QUERY_COMMAND ) + QUERY_COMMAND.length() + 1;
	                    int end = dataURL.indexOf( "&" );
	
	                    try
	                    {
	                        cityName = URLDecoder.decode( dataURL.substring( start, end ).toLowerCase(), "UTF-8" );
	                        cityName = cityName.replaceAll("\\W", " ");
	                    }// end of try block
	                    catch ( UnsupportedEncodingException e )
	                    {
	                        UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
	                                TAG + "::handleWeatherData" );
	                    }// end of else block
	
	                }// end of else block
	
	                // just the city name is required and nothing else
	                if ( cityName != null && cityName.contains( "," ) )
	                {
	                    cityName = cityName.substring( 0, cityName.indexOf( ",") ).toLowerCase();
	                }// end of if block // end of if block
	
	                String ps;
	                StringBuilder fileData = new StringBuilder();
	
	                if ( cityName != null )
	                {
	                    ps = String.format( "%s%s%s", "gn_sd_", cityName.replaceAll( " ", "_" ), ".json" );
	                    WeatherLionMain.previousCitySearchFile = new File(
	                    		WeatherLionMain.previousSearchesPath.getPath() + "/" +  ps );
	                }// end of if block
	
	                if( WeatherLionMain.previousCitySearchFile.exists() )
	                {
	                	try(
	                			FileReader fr = new FileReader( WeatherLionMain.previousCitySearchFile );	// declare and initialize the file reader object
	                			BufferedReader br = new BufferedReader( fr ); 	// declare and initialize the buffered reader object
	                	)
	                	{
	                		String line;                                                 
	                        
	                        while( ( line = br.readLine() ) != null) 
	                        {
	                            fileData.append( line );
	                        }// end of while loop
	                        
	                		response = fileData.toString();
	                	}// end of try block
	                	catch ( FileNotFoundException e )
	                    {
	                		 UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
	         			        TAG + "::doInBackground [line: " +
	         			        UtilityMethod.getExceptionLineNumber( e )  + "]" );
	                    }// end of catch block
	                }// end of if block
	                else
	                {
	                    saveGeoNamesSearchResults( WeatherLionMain.previousCitySearchFile, cityName );
	                }// end of else block
	            }// end of if block
	
	            break;
	        case "here":
	            // I prefer to use the GeoNames search results as the Here results only returns a single city.
	            // I might just add if in the future though.
	            break;
	        default:
	            break;
        }// end of switch block		

		return response;
	}// end of message doInBackground
	
	private void saveGeoNamesSearchResults( File previousCitySearchFile, String cityName )
    {
        String searchURL;

        if( UtilityMethod.hasInternetConnection() )
        {
            // now that we have the name of the city, we need some search results from GeoNames
            try
            {
                int maxRows = 100;

                // All spaces must be replaced with the + symbols for the HERE Maps web service
                if( cityName.contains( " " ) )
                {
                    cityName = cityName.replace( " ", "+" );
                }// end of if block

                searchURL = String.format(
                		"http://api.geonames.org/searchJSON?%s=%s&maxRows=%s&username=%s",
                        QUERY_COMMAND, 
                        cityName.toLowerCase(),
                        maxRows,
                        WidgetUpdateService.geoNameAccount );

                response = HttpHelper.downloadUrl( searchURL ); // get the search results from the GeoNames web service
            }// end of try block
            catch ( Exception e )
            {
                UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
                        TAG + "::handleWeatherData [line: " +
                                UtilityMethod.getExceptionLineNumber( e )  + "]" );

                response = null;
            }// end of catch block

            // attempt to store the search results locally if this search was not performed before
            if( !previousCitySearchFile.exists() )
            {
                if( response != null )
                {
                    try
                    {
                        if(  JSONHelper.saveToJSONFile( response, previousCitySearchFile.toString() ) )
                        {
                            UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, "JSON search data stored locally for " + cityName + ".",
                                    TAG + "::saveGeoNamesSearchResults" );
                        }// end of if block
                    }// end of try block
                    catch ( Exception e )
                    {
                        UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
                                TAG + "::handleWeatherData [line: " + UtilityMethod.getExceptionLineNumber( e )  + "]" );
                    }// end of catch block
                }// end of if block
            }// end of if block
        }// end of if block
    }// end of method saveGeoNamesSearchResults

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void done()
	{
		switch ( getService() )
		{
			case "geo":
				getGeoNamesSuggestions();
				break;
			case "here":
				getHereSuggestions();
				break;
			case "yahoo":
				getYahooSuggestions();
				break;
			default:
				break;
		}// end of switch block
	}// end of overridden method done

	private void getGeoNamesSuggestions()
	{
		String response;
		ArrayList< String > matches = new ArrayList< String >();
		
		try 
		{
			response = get();
			
			Object json = new JSONTokener( response ).nextValue();
    		
			// Check if a JSON was returned from the web service
			if ( json instanceof JSONObject )
			{
				// Get the full HTTP Data as JSONObject
				JSONObject geoNamesJSON = new JSONObject( response );
				// Get the JSONObject "geonames"
				JSONArray geoNames = geoNamesJSON.optJSONArray( "geonames" );
				matchCount = geoNamesJSON.getInt( "totalResultsCount" );

				// if the place array only contains one object, then only one
				// match was found
				if ( matchCount == 1 ) 
				{
					JSONObject place = geoNames.getJSONObject( 0 );
					
					cityName = place.getString( "name" );
					countryCode = place.getString( "countryCode" );
					countryName = place.getString( "countryName" );
					regionCode = place.getString( "adminCode1" );					
					regionName = countryCode.equalsIgnoreCase( "US" ) ?
							     UtilityMethod.usStatesByCode.get( regionCode ) :
							     null;
					Latitude = place.getString( "lat" );
					Longitude = place.getString( "lng" );					
					
					if ( regionName != null )
					{
						response = cityName + ", " + regionName + ", "
								+ countryName;
					}// end of if block
					else
					{
						response = cityName + ", " + countryName;
					}// end of else block
				}// end of if block
				else
				{					
					// Multiple matches were found
					// Store the data in local storage
					Gson gson = new GsonBuilder()
							.registerTypeAdapter(
									GeoNamesGeoLocation.class,
									new GeoNamesGeoLocation.GeoNamesGeoLocationDeserializer() )
							.create();
					GeoNamesGeoLocation.cityGeographicalData = gson.fromJson(
							response, GeoNamesGeoLocation.class );
				}// end of else block
			}// end of if block			
		}// end of try block
		catch ( InterruptedException e )
		{
			 UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getGeoNamesSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		catch ( ExecutionException e )
		{
			 UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getGeoNamesSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		catch ( JSONException e )
		{
			 UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getGeoNamesSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		
		for ( GeoNames place : GeoNamesGeoLocation.cityGeographicalData.getGeoNames() )
		{
			StringBuilder match = new StringBuilder();
			
			// We only need the cities with the same name
			if( !place.getName().equalsIgnoreCase( PreferenceForm.searchCity.toString() ) ) continue;
				
			match.append( place.getName() );				
						
			// Geo Names may not return adminCodes1 for some results 
			if( place.getAdminCodes1() != null )
			{
				if ( place.getAdminCodes1().getISO() != null && 
						!UtilityMethod.isNumeric( place.getAdminCodes1().getISO() ) )
				{
					String region = place.getAdminCodes1().getISO() != null ? 
										place.getAdminCodes1().getISO() :
											null;
					
					match.append( ", " + region );
				}// end of outer if block
			}// end of if block

			match.append( ", "  + place.getCountryName() );
			
			// Always verify that the adminName1 and countryName does not indicate a city already added 
			if( !matches.contains( place.getAdminName1() + ", " + place.getCountryName() ) )
			{
				// Redundancy check
				if( !matches.contains( match.toString() ) )
				{
					matches.add( match.toString() );				
				}// end of if block
			}// end of if block
		}// end of for each loop
		
		if( !serviceRequest )
		{
			if( matches.size() > 0 )
			{
				String[] s = matches.toArray ( new String[ matches.size() ] );
				PreferenceForm.cityNames = new DefaultComboBoxModel< String >( s );
			}// end of if block
			else
			{
				matches.clear();
				
				String[] s = new String[] { "No match found..." };
				PreferenceForm.cityNames = new DefaultComboBoxModel< String >( s );
			}// end of else block
				
			PreferenceForm.jlMatches.setModel( PreferenceForm.cityNames );
			
			int dropDownHeight = PreferenceForm.jlMatches.getPreferredSize().height > 308
					? 310
					: PreferenceForm.jlMatches.getPreferredSize().height + 6;
			
			PreferenceForm.jlMatches.setSize( PreferenceForm.txtLocation.getWidth(),
					dropDownHeight );
			PreferenceForm.matchesScrollPane.setSize( PreferenceForm.txtLocation.getWidth(),
					dropDownHeight );
			
			// force the list to appear below the text field like a combo box list
			PreferenceForm.matchesScrollPane.setBounds( PreferenceForm.txtLocation.getX(), 
					PreferenceForm.txtLocation.getY() + PreferenceForm.txtLocation.getHeight(),
					PreferenceForm.txtLocation.getWidth(), 
					PreferenceForm.jlMatches.getPreferredSize().height );
			
			PreferenceForm.btnSearch.setIcon( null );
			PreferenceForm.btnSearch.setText( "Search" );
			PreferenceForm.btnSearch.setEnabled( true );
			PreferenceForm.matchesScrollPane.setVisible( true );
		}// end of if block
	}// end of method getGeoNamesSuggestions
	
	private void getHereSuggestions()
	{
		String response;
		ArrayList< String > matches = new ArrayList< String >();
		
		try 
		{
			response = get();
			Object json = new JSONTokener( response ).nextValue();
    		
			// Check if a JSON was returned from the web service
			if ( json instanceof JSONObject )
			{
				// Get the full HTTP Data as JSONObject
				JSONObject reader = new JSONObject( response );
				// Get the JSONObject "Response"
				JSONObject hResponse = reader.getJSONObject( "Response" );
				JSONObject view = hResponse.getJSONArray( "View" ).getJSONObject( 0 );
				JSONArray places = view.optJSONArray( "Result" );
				matchCount = places.length();

				// if the place array only contains one object, then only one
				// match was found
				if ( matchCount == 1 ) 
				{
					JSONObject place = places.getJSONObject( 0 );
					JSONObject location = place.getJSONObject( "Location" );					
					JSONObject address = location.getJSONObject( "Address" );
					JSONObject displayPosition = location.getJSONObject( "DisplayPosition" );
					JSONObject country = address.getJSONArray( "AdditionalData" ).getJSONObject( 0 );
					String label = address.getString( "Label" );
					
					cityName = label.substring( 0, label.indexOf( "," ) );
					countryName = UtilityMethod.toProperCase( country
							.getString( "value" ) );
					countryCode = UtilityMethod.worldCountryCodes.get( countryName );
					regionName = null;
					regionCode = null;					
					Latitude = displayPosition.getString( "Latitude" );
					Longitude = displayPosition.getString( "Longitude" );					
					
					response = label;
					matches.add( response );
				}// end of if block
				else
				{					
					// Multiple matches were found
					// Store the data in local storage
					Gson gson = new GsonBuilder()
							.registerTypeAdapter(
									HereGeoLocation.class,
									new HereGeoLocation.HereGeoLocationDeserializer() )
							.create();
					HereGeoLocation.cityGeographicalData = gson.fromJson(
							response, HereGeoLocation.class );
					
					for ( HereGeoLocation.Response.View.Result place : 
						  HereGeoLocation.cityGeographicalData.getResponse().getView().getResult() )
					{
						String match = place.getLocation().getAddress().getLabel();				
						
						if( !matches.contains( match.toString() ) )
						{
							matches.add( match.toString() );				
						}// end of if block
					}// end of for each loop
				}// end of else block
			}// end of if block			
		}// end of try block
		catch ( InterruptedException e )
		{
			 UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getHereSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		catch ( ExecutionException e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getHereSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		catch ( JSONException e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getHereSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		
		String[] s = matches.toArray ( new String[ matches.size() ] );
		PreferenceForm.cityNames = new DefaultComboBoxModel< String >( s );
		
		PreferenceForm.jlMatches.setModel( PreferenceForm.cityNames );
		PreferenceForm.jlMatches.setSize( PreferenceForm.jlMatches.getWidth(),
				PreferenceForm.jlMatches.getPreferredSize().height );
		PreferenceForm.jlMatches.setVisible( true );
	}// end of method getHereSuggestions
	
	private void getYahooSuggestions()
	{
		String response;
		int matchCount;
		
		try 
		{
			response = get();
			Object json = new JSONTokener( response ).nextValue();

			// Check if a JSON was returned from the web service
			if ( json instanceof JSONObject )
			{
				// Get the full HTTP Data as JSONObject
				JSONObject reader = new JSONObject( response );
				// Get the JSONObject "query"
				JSONObject query = reader.getJSONObject( "query" );
				JSONObject results = query.getJSONObject( "results" );
				JSONArray places = results.getJSONArray( "place" );
				matchCount = places.length();

				// if the place array only contains one object, then only one
				// match was found
				if ( matchCount == 1 ) 
				{
					JSONObject place = places.getJSONObject( 0 );
					JSONObject centroid = place.getJSONObject( "centroid" );
					JSONObject admin1 = place.getJSONObject( "admin1" );
					JSONObject country = place.getJSONObject( "country" );
					
					cityName = UtilityMethod.toProperCase(place.getString( "name" ) );
					countryName = UtilityMethod.toProperCase( country
							.getString( "content" ) );
					countryCode = country.getString( "code" ).toUpperCase();
					regionName = UtilityMethod.toProperCase( admin1.getString( "content" ) );
					regionCode = admin1.getString( "code" ).toUpperCase();					
					Latitude = centroid.getString( "latitude" );
					Longitude = centroid.getString( "longitude" );					

					if ( admin1.getString( "type" ) != null )
					{
						response = cityName + ", " + admin1.getString( "type" ) + ", "
								+ countryName;
					}// end of if block
					else
					{
						response = cityName + ", " + countryName;
					}// end of else block
				}// end of if block
				else
				{					
					// Multiple matches were found
					// Store the data in local storage
					Gson gson = new GsonBuilder()
							.registerTypeAdapter(
									YahooGeoLocation.class,
									new YahooGeoLocation.YahooGeoLocationDeserializer() )
							.create();
					YahooGeoLocation.cityGeographicalData = gson.fromJson(
							response, YahooGeoLocation.class );					

				}// end of else block
			}// end of if block			
		}// end of try block
		catch ( InterruptedException e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getYahooSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		catch ( ExecutionException e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getYahooSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		catch ( JSONException e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getYahooSuggestions [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		}// end of catch block
		
		ArrayList< String > matches = new ArrayList< String >();
		
		for (YahooGeoLocation.Query.Results.Place place : YahooGeoLocation.cityGeographicalData
				.getQuery().getResults().getPlace() )
		{
			StringBuilder match = new StringBuilder();			
			match.append( place.getName() );				

			if ( place.getAdmin1() != null )
			{
				String region = UtilityMethod.usStatesByName.get(
									place.getAdmin1().getContent() ) != null ? 
											UtilityMethod.usStatesByName.get( 
													place.getAdmin1().getContent() ) :
														place.getAdmin1().getContent();
				
				match.append( ", " + region );
			}// end of outer if block

			match.append( ", "  + place.getCountry().getContent() );
			
			if( !matches.contains( match.toString() ) )
			{
				matches.add( match.toString() );				
			}// end of if block
		}// end of for each loop
		
		String[] s = matches.toArray ( new String[ matches.size() ] );
		PreferenceForm.cityNames = new DefaultComboBoxModel< String >( s );
		
		PreferenceForm.jlMatches.setModel( PreferenceForm.cityNames );
		PreferenceForm.jlMatches.setSize( PreferenceForm.jlMatches.getWidth(),
				PreferenceForm.jlMatches.getPreferredSize().height );
		PreferenceForm.jlMatches.setVisible( true );
	}// end of method getYahooSuggestions
}// end of class CityDataService
