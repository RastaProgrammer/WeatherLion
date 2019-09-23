package com.bushbungalo.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.SwingWorker;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.bushbungalo.FiveDayForecast;
import com.bushbungalo.WeatherLionMain;
import com.bushbungalo.utils.UtilityMethod;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 */

public class WeatherDataXMLService  extends SwingWorker< String, Object >
{	
	private static final String TAG = "WeatherDataXMLService";
	
	private String providerName;
	private Date datePublished;
	private String cityName;
	private String countryName;
	private String currentConditions;
	private String currentTemperature;
	private String currentFeelsLikeTemperature;
	private String currentHigh;
	private String currentLow;
	private String currentWindSpeed;
	private String currentWindDirection;
	private String currentHumidity; 
	private String sunriseTime;
	private String sunsetTime;
	private List<FiveDayForecast> fiveDayForecast;

	public WeatherDataXMLService() 
	{
	}// end of default constructor
	
	public WeatherDataXMLService( String providerName, Date datePublished,
			String cityName, String countryName, String currentConditions,
			String currentTemperature, String feelsLikeTemperature, String currentHigh,
			String currentLow, String currentWindSpeed, String currentWindDirection,
			String currentHumidity, String sunriseTime, String sunsetTime,
			List<FiveDayForecast> fiveDayForecast ) 
	{
		this.providerName = providerName;
		this.datePublished = datePublished;
		this.cityName = cityName;
		this.countryName = countryName;
		this.currentConditions = currentConditions;
		this.currentTemperature = currentTemperature;
		this.currentFeelsLikeTemperature = feelsLikeTemperature;
		this.currentHigh = currentHigh;
		this.currentLow = currentLow;
		this.currentWindSpeed = currentWindSpeed;
		this.currentWindDirection = currentWindDirection;
		this.currentHumidity = currentHumidity;
		this.sunriseTime = sunriseTime;
		this.sunsetTime = sunsetTime;
		this.fiveDayForecast = fiveDayForecast;
	}// end of fifteen-argument constructor	
	
	 public String getProviderName() 
	 {
		return providerName;
	}

	public void setProviderName( String providerName ) 
	{
		this.providerName = providerName;
	}

	public Date getDatePublished() 
	{
		return datePublished;
	}

	public void setDatePublished( Date datePublished )
	{
		this.datePublished = datePublished;
	}

	public String getCityName()
	{
		return cityName;
	}

	public void setCityName( String cityName )
	{
		this.cityName = cityName;
	}

	public String getCountryName() 
	{
		return countryName;
	}

	public void setCountryName( String countryName ) 
	{
		this.countryName = countryName;
	}

	public String getCurrentConditions()
	{
		return currentConditions;
	}

	public void setCurrentConditions( String currentConditions )
	{
		this.currentConditions = currentConditions;
	}

	public String getCurrentTemperature()
	{
		return currentTemperature;
	}

	public void setCurrentTemperature( String currentTemperature )
	{
		this.currentTemperature = currentTemperature;
	}
	
	public String getCurrentFeelsLikeTemperature()
	{
		return this.currentFeelsLikeTemperature;
	}

	public void setCurrentFeelsLikeTemperature( String currentFeelsLikeTemperature )
	{
		this.currentFeelsLikeTemperature = currentFeelsLikeTemperature;
	}

	public String getCurrentHigh() 
	{
		return currentHigh;
	}

	public void setCurrentHigh( String currentHigh )
	{
		this.currentHigh = currentHigh;
	}

	public String getCurrentLow()
	{
		return currentLow;
	}

	public void setCurrentLow( String currentLow )
	{
		this.currentLow = currentLow;
	}

	public String getCurrentWindSpeed()
	{
		return currentWindSpeed;
	}

	public void setCurrentWindSpeed( String currentWindSpeed )
	{
		this.currentWindSpeed = currentWindSpeed;
	}

	public String getCurrentWindDirection()
	{
		return currentWindDirection;
	}

	public void setCurrentWindDirection( String currentWindDirection )
	{
		this.currentWindDirection = currentWindDirection;
	}

	public String getCurrentHumidity() 
	{
		return currentHumidity;
	}

	public void setCurrentHumidity( String currentHumidity )
	{
		this.currentHumidity = currentHumidity;
	}

	public String getSunriseTime()
	{
		return sunriseTime;
	}

	public void setSunriseTime( String sunriseTime ) 
	{
		this.sunriseTime = sunriseTime;
	}

	public String getSunsetTime() 
	{
		return sunsetTime;
	}

	public void setSunsetTime( String sunsetTime ) 
	{
		this.sunsetTime = sunsetTime;
	}

	public List<FiveDayForecast> getFiveDayForecast()
	{
		return fiveDayForecast;
	}

	public void setFiveDayForecast( List<FiveDayForecast> fiveDayForecast ) 
	{
		this.fiveDayForecast = fiveDayForecast;
	}

	@Override
	protected String doInBackground() throws Exception
	{
		return null;
	}
	
	 @Override
     protected void done()
	 {
		 saveCurrentWeatherXML( getProviderName(), getDatePublished(), 
	        		getCityName(), getCountryName(), getCurrentConditions(), 
	        		getCurrentTemperature(), getCurrentFeelsLikeTemperature(), 
	        		getCurrentHigh(), getCurrentLow(), getCurrentWindSpeed(),
	        		getCurrentWindDirection(), getCurrentHumidity(), getSunriseTime(),
	        		getSunsetTime(), getFiveDayForecast() );
     }// end of overridden method done	

	 public static boolean saveCurrentWeatherXML( String providerName, Date datePublished, String cityName,
	    		String countryName,	String currentConditions, String currentTemperature, String currentFeelsLikeTemperture,
	    		String currentHigh, String currentLow, String currentWindSpeed, String currentWindDirection, String currentHumidity, 
	    		String sunriseTime, String sunsetTime, List<FiveDayForecast> fiveDayForecast )
    {
		 File weatherDataDirectory = new File( WeatherLionMain.DATA_DIRECTORY_PATH );
		
		 // create all necessary files if they are not present
		 if( !weatherDataDirectory.exists() ) 
		 {
			 weatherDataDirectory.mkdirs();
		 }// end of if block
		 
		 try 
		 {	
			 Element weatherData = null; 
			 Document doc = null; 
			 Element provider = new Element( "Provider" );
			 Element location = new Element( "Location" );
			 Element atmosphere = new Element( "Atmosphere" );
			 Element wind = new Element( "Wind" );
			 Element astronomy = new Element( "Astronomy" );
			 Element current = new Element( "Current" );
	    			    	
	    	 // Root element
			 weatherData = new Element( "WeatherData" ); 
			 doc = new Document( weatherData );     		     		
        	
			 	// Provider Details
			 provider.addContent( new Element( "Name" ).setText( providerName ) );
			 provider.addContent( new Element( "Date" ).setText( datePublished.toString() ) );
			 doc.getRootElement().addContent( provider );
    	
			 // Location Readings
			 location.addContent( new Element( "City" ).setText( cityName ) );
			 location.addContent( new Element( "Country" ).setText( String.valueOf( countryName ) ) );
			 doc.getRootElement().addContent( location );
    	
			 // Atmospheric Readings
			 atmosphere.addContent( new Element( "Humidity" ).setText( String.valueOf( currentHumidity ) ) ); 
			 doc.getRootElement().addContent( atmosphere );
    	
			 // Wind Readings
			 wind.addContent( new Element( "WindSpeed" ).setText( String.valueOf( currentWindSpeed ) ) ); 
			 wind.addContent( new Element( "WindDirection" ).setText( String.valueOf( currentWindDirection ) ) );
			 doc.getRootElement().addContent( wind );
    	
			 // Astronomy readings
			 astronomy.addContent( new Element( "Sunrise" ).setText( String.valueOf( sunriseTime ) ) ); 
			 astronomy.addContent( new Element( "Sunset" ).setText( String.valueOf( sunsetTime ) ) ); 
			 doc.getRootElement().addContent( astronomy );
    	
			 // Current Weather
			 current.addContent( new Element( "Condition" ).setText( 
					 String.valueOf( UtilityMethod.toProperCase( currentConditions ) ) ) ); 
			 current.addContent( new Element( "Temperature" ).setText( String.valueOf( currentTemperature ) ) ); 
			 current.addContent( new Element( "FeelsLike" ).setText( String.valueOf( currentFeelsLikeTemperture ) ) ); 
			 current.addContent( new Element( "HighTemperature" ).setText( String.valueOf( currentHigh ) ) ); 
			 current.addContent( new Element( "LowTemperature" ).setText( String.valueOf( currentLow ) ) ); 
			 doc.getRootElement().addContent( current );
			 
			 // list of forecast data
			 Element forecastDataList = new Element( "DailyForecast" );
        				 
			 // Five Day Forecast                
			 for ( FiveDayForecast forecast : fiveDayForecast )
			 {				 
				 Element forecastData = new Element( "DayForecast" );
	    	
				 forecastData.addContent( new Element( "Date" ).setText( forecast.getForecastDate().toString() ) ); 
				 forecastData.addContent( new Element( "Condition" ).setText( UtilityMethod.toProperCase( 
    				forecast.getForecastCondition() ) ) ); 
				 forecastData.addContent( new Element( "LowTemperature" ).setText( forecast.getForecastLowTemp() ) );
				 forecastData.addContent( new Element( "HighTemperature" ).setText( forecast.getForecastHighTemp() ) ); 
    		
				 forecastDataList.addContent( forecastData );				 
			 }// end of for each loop
			 
			 doc.getRootElement().addContent( forecastDataList );
    	
			 // new XMLOutputter().output(doc, System.out);
			 XMLOutputter xmlOutput = new XMLOutputter();
    		
			 // display nice nice
			 xmlOutput.setFormat( Format.getPrettyFormat() );
			 xmlOutput.output( doc, new FileWriter( WeatherLionMain.DATA_DIRECTORY_PATH +
				 WeatherLionMain.WEATHER_DATA_XML ) );
			 
			 UtilityMethod.logMessage( "info", providerName + "'s weather data was stored locally!",
					 TAG + "::saveCurrentWeatherXML" );
	    				
		 }// end of try block
		 catch ( FileNotFoundException e )
		 {
			 UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::saveCurrentWeatherXML [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		 }// end of catch block
		 catch ( IOException e )
		 {
			 UtilityMethod.logMessage( "severe", e.getMessage(),
		        TAG + "::saveCurrentWeatherXML [line: " +
		        UtilityMethod.getExceptionLineNumber( e )  + "]" );
		 }// end of catch block
		 
		 return true;
    }// end of method saveCurrentWeatherXML	 
}// end of class WeatherDataXMLService
