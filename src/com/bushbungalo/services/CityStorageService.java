package com.bushbungalo.services;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.bushbungalo.model.CityData;
import com.bushbungalo.model.GeoNamesGeoLocation;
import com.bushbungalo.utils.JSONHelper;
import com.bushbungalo.utils.UtilityMethod;
import com.bushbungalo.utils.XMLHelper;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Class Description:</b>
 * <br />
 * 		<span>Service that calls method <b><i>checkAstronomy</i></b> in the <b>WeatherLionWidget</b> class which checks the current time of day</span>
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 06/29/19
 * <br /> 
 */
public class CityStorageService extends SwingWorker< String, Object >
{
	private static final String TAG = "CityStorageService";
	
	private int m_index;
	private String m_city;
	
	public CityStorageService()
	{
	}// end of default constructor
	
	public CityStorageService( int listIndex, String cityName )
	{
		m_index = listIndex;
		m_city = cityName;
	}// end of one-argument constructor
	
	public int getIndex()
	{
		return m_index;
	}

	public void setIndex(int m_index)
	{
		this.m_index = m_index;
	}

	public String getCity()
	{
		return m_city;
	}

	public void setCity( String cityName )
	{
		this.m_city = cityName;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doInBackground() throws Exception
	{
		if( getCity().trim().length() > 0 &&
				!UtilityMethod.isKnownCity( getCity() ) )
		{
			if( GeoNamesGeoLocation.cityGeographicalData == null )
			{
				CityDataService.serviceRequest = true;
				
				// use the web service it was not used before
				UtilityMethod.findGeoNamesCity( getCity() );
			}// end of if block
			
			storeNewLocationLocally();
		}// end of if block
		
		return getCity() + " was successfully saved to local storage.";
	}// end of method doInBackground
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void done()
	{
		String log = null;
		
		try 
		 {
			log = get();
		 }// end of try block
		 catch ( InterruptedException e )
		 {
			 UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::done [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		 }// end of catch block
		 catch ( ExecutionException e )
		 {
			 UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::done [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		 }// end of catch block	
		
		UtilityMethod.logMessage( "info", log, "CityStorageService::done" );
	}// end of method done
	
	/***
	 * Add new location locally if it does not already exists
	 */
	private void storeNewLocationLocally()
	{
		GeoNamesGeoLocation.GeoNames gn = GeoNamesGeoLocation.cityGeographicalData
				.getGeoNames()
				.get( m_index );
		
		String cityName = UtilityMethod.toProperCase( GeoNamesGeoLocation.cityGeographicalData
				.getGeoNames().get( 0 ).getName() );
		String countryName = UtilityMethod
				.toProperCase( gn.getCountryName() );
		String countryCode = gn.getCountryCode().toUpperCase(); 
		String regionName = UtilityMethod
				.toProperCase( gn.getAdminName1() );
		
		String regionCode = null;
		regionCode = gn.getAdminCodes1().getISO() != null ?
				gn.getAdminCodes1().getISO().toUpperCase() :
					null;
		
		Float Latitude = gn.getLatitude();
		Float Longitude = gn.getLongitude();	

		CityData cityData = new CityData( cityName, countryName, countryCode, 
				regionName, regionCode,	Latitude, Longitude );
		
		String currentCity = regionCode != null ? cityName + ", " + regionCode : cityName + ", " + countryName;
		
		if( !UtilityMethod.isFoundInDatabase( currentCity ) )
		{
			UtilityMethod.addCityToDatabase( cityName, countryName, countryCode, 
					regionName, regionCode,	Latitude, Longitude );
		}// end of if block
		
		if( !UtilityMethod.isFoundInJSONStorage( currentCity ) )
		{
			JSONHelper.exportToJSON( cityData );
		}// end of if block
		
		if( !UtilityMethod.isFoundInXMLStorage( currentCity ) )
		{
			XMLHelper.exportToXML( cityData );
		}// end of if block
	}// end of method storeLocationLocally
}// end of class CityStorageService
