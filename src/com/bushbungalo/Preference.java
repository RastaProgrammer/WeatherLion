package com.bushbungalo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.bushbungalo.utils.UtilityMethod;

/**
 * @author Paul O. Patterson
 * @version     1.0
 * @since       1.0
 * 
 * <p>
 * This class is responsible for preserving the user's preferences on the local disk. 
 * </p>
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>01/29/19 - Changed the default weather update period to 30 mins (1800000 ms).</li>
 * 		<li>05/11/19 - Removed printing stack trace errors to console for logging.</li>
 * </ul>
 */

public class Preference 
{
	private String m_provider;
	private int  m_interval;
	private String m_location;
	private boolean m_use_metric;
	private boolean m_use_system_location;	
	private String m_widget_background;	
	private String m_icon_set;	
	
	// private static Properties configProps;
	public static final File CONFIG_FILE = new File( "res/settings/config.properties" );
	public static final File PREFERENCE_FILE = new File( "res/settings/preferences.properties" );
	public static final File SETTING_DIRECTORY = new File( "res/settings/" );
	
	public Preference()
	{
	}// end of default constructor
	
	public Preference( String provider, int interval, String location,
			boolean useMetric, boolean useSystemLocation, String widgetBackground, String iconSet )
	{
		this.m_provider = provider;
		this.m_interval = interval;
		this.m_location = location;
		this.m_use_metric = useMetric;
		this.m_use_system_location = useSystemLocation;
		this.m_widget_background = widgetBackground;
		this.m_icon_set = iconSet;
	}// end of five argument constructor

	/**
	 * @return the m_provider
	 */
	public String getProvider()
	{
		return m_provider;
	}
	
	/**
	 * @param m_provider the m_provider to set
	 */
	public void setProvider(String provider)
	{
		this.m_provider = provider;
	}

	/**
	 * @return the m_interval
	 */
	public int getInterval()
	{
		return m_interval;
	}

	/**
	 * @param m_interval the m_interval to set
	 */
	public void setInterval(int interval)
	{
		this.m_interval = interval;
	}
	
	/**
	 * @return the m_location
	 */
	public String getLocation()
	{
		return m_location;
	}
	
	/**
	 * @param m_location the m_location to set
	 */
	public void setLocation(String location)
	{
		this.m_location = location;
	}
	
	/**
	 * @return the m_use_metric
	 */
	public boolean getUseMetric()
	{
		return m_use_metric;
	}
	
	/**
	 * @param m_use_metric the m_use_metric to set
	 */
	public void setUseMetric(boolean useMetric)
	{
		this.m_use_metric = useMetric;
	}
	
	/**
	 * @return the m_system_location
	 */
	public boolean getUseSystemLocation()
	{
		return m_use_system_location;
	}
	
	/**
	 * @param m_use_system_location the m_system_location to set
	 */
	public void setUseSystemLocation(boolean systemLocation)
	{
		this.m_use_system_location = systemLocation;
	}
	
	public String getWidgetBackground() 
	{
		return m_widget_background;
	}

	public void setWidgetBackground( String m_widget_background ) 
	{
		this.m_widget_background = m_widget_background;
	}
	
	public String getIconSet() 
	{
		return m_icon_set;
	}

	public void setIconSet( String m_icon_set ) 
	{
		this.m_icon_set = m_icon_set;
	}
	
	public static Preference getSavedPreferences()
	{
		Properties props = new Properties();
		
		try
		{
		    FileReader reader = new FileReader( PREFERENCE_FILE );
		    props = new Properties();
		    props.load( reader );
		    reader.close();
		}// end of try block
		catch ( FileNotFoundException ex )
		{
			// file does not exist so create the default one
			createDefaultPreferencesPropertiesFile();
			
			try
			{
			    FileReader reader = new FileReader( PREFERENCE_FILE );
			    props = new Properties();
			    props.load( reader );
			    reader.close();
			}// end of try block
			catch ( FileNotFoundException e )
			{
				UtilityMethod.logMessage( "severe", e.getMessage(),
					"Preference::getSavedPreferences [line: " + e.getStackTrace()[1].getLineNumber()+ "]" );	
			}// end of try block
			catch ( IOException e )
			{
				UtilityMethod.logMessage( "severe", e.getMessage(),
						"Preference::getSavedPreferences [line: " + e.getStackTrace()[1].getLineNumber()+ "]" );	
			}// end of try block
		}// end of try block
		catch ( IOException ex )
		{
			UtilityMethod.logMessage( "severe", ex.getMessage(),
					"Preference::getSavedPreferences [line: " + ex.getStackTrace()[1].getLineNumber()+ "]" );	
		}// end of try block
			
		// get the property value and use it
		String wxProvider = props.getProperty( "wx_source" );
		int updateInterval  = Integer.parseInt( props.getProperty( "update_interval" ) );
		boolean useSystemLocation = Boolean.parseBoolean( props.getProperty( "use_system_location" ) );
		String wxLocation = props.getProperty( "location" );
		boolean useMetric = Boolean.parseBoolean( props.getProperty( "use_metric" ) );
		String widgetBackground = props.getProperty( "widget_background" );
		String iconSet = props.getProperty( "icon_set" );
		
		return new Preference( wxProvider, updateInterval,
												wxLocation, useMetric, useSystemLocation, widgetBackground, iconSet );
	}// end of method getSavedPreferences
	
	/***
	 * method that creates a default properties files
	 */
	public static void createDefaultConfigPropertiesFile()
	{
		Properties props = new Properties();
		
		props.setProperty( "xy", "0,0" );
		props.setProperty( "skin", Integer.toString( WeatherLionMain.METAL ) );
		props.setProperty( "icons_set", "miui" );
		OutputStream outputStream;		
		
		try
		{
			outputStream = new FileOutputStream( CONFIG_FILE );
			props.store( outputStream, WeatherLionMain.PROGRAM_NAME + " Configuration" );
			outputStream.close();
		}// end of try block
		catch ( FileNotFoundException e )
		{
		}// end of catch block
		catch ( IOException e )
		{
		    // I/O error
		}// end of catch block	
	
	}// end of method createDefaultPropertiesFile
		
	/***
	 * method that creates a default properties files
	 */
	public static void createDefaultPreferencesPropertiesFile()
	{
		// if the directory does not exist then no previous configuration exists
		if( !SETTING_DIRECTORY.exists() ) 
		{
			SETTING_DIRECTORY.mkdirs();
			
			try 
			{
				PREFERENCE_FILE.createNewFile();
			}// end of try block
			catch ( IOException e )
			{
				UtilityMethod.logMessage( "severe" , e.getMessage(),
					"Preference::createdefaultPreferencesPropertiesFile [line: " 
					+ e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
		}// end of if block	
		
		Properties props = new Properties();
		
		props.setProperty( "wx_source", WeatherLionMain.authorizedProviders[ 0 ] );
		props.setProperty( "update_interval", "1800000" ); //default to 30 minutes
		props.setProperty( "location", "" );
		props.setProperty( "use_metric", "false" );
		props.setProperty( "use_system_location", "false" );		
		props.setProperty( "widget_background", "default" );		
		props.setProperty( "icon_set", "miui" );		
		OutputStream outputStream;		
		
		try
		{
			outputStream = new FileOutputStream( PREFERENCE_FILE );
			props.store( outputStream, WeatherLionMain.PROGRAM_NAME + " Preferences" );
			outputStream.close();
		}// end of try block
		catch ( FileNotFoundException e )
		{
			
		}// end of catch block
		catch ( IOException e )
		{
		    // I/O error
		}// end of catch block	
	
	}// end of method createDefaultPropertiesFile
	
	public String getPropValues( File propertyFile, String property )
	{
		Properties props = new Properties();
		
		try
		{
		    FileReader reader = new FileReader( propertyFile );
		    props = new Properties();
		    props.load( reader );
		    reader.close();
		}// end of try block
		catch ( FileNotFoundException ex )
		{
			switch( propertyFile.getName() ) 
			{
				case "preferences.properties":
					createDefaultPreferencesPropertiesFile();
					break;
				case "config.properties":
					createDefaultConfigPropertiesFile();
					break;
				default:
					break;
			}// end of switch block
			
			try
			{
			    FileReader reader = new FileReader( propertyFile );
			    props = new Properties();
			    props.load( reader );
			    reader.close();
			}// end of try block
			catch ( FileNotFoundException e )
			{
			    
			}// end of try block
			catch ( IOException e )
			{
			    // I/O error
			}// end of try block
		}// end of try block
		catch ( IOException ex )
		{
		    // I/O error
		}// end of try block
		
		// get the property value and use it
		String prop = props.getProperty( property );
		
		// if the configuration file exists then a skin value would already be set. If not, use the default skin Metal.
		if ( prop == null )
		{
			return "0";
		}// end of if block
		else
		{
			return prop;
		}// end of else block
	}// end of method getPropValues()	
	
	public static void setPropValues( File propertyFile, String propertyName,
			String propertyValue ) 
	{
		Properties props = new Properties();
				
		try 
		{
		    FileInputStream in = new FileInputStream( propertyFile );
		    props.load( in );
		    in.close();
		    
		    FileOutputStream out = new FileOutputStream( propertyFile );
		    props.setProperty( propertyName , propertyValue );
		    
		    switch( propertyFile.getName() ) 
			{
				case "preferences.properties":
					props.store( out, WeatherLionMain.PROGRAM_NAME + " Preferences" );
					break;
				case "config.properties":
					props.store( out, WeatherLionMain.PROGRAM_NAME + " Configuration" );
					break;
				default:
					break;
			}// end of switch block
		    
		    out.close();
		}// end of try block                       
		catch ( FileNotFoundException ex )
		{
			switch( propertyFile.getName() ) 
			{
				case "preferences.properties":
					createDefaultPreferencesPropertiesFile();
					break;
				case "config.properties":
					createDefaultConfigPropertiesFile();
					break;
				default:
					break;
			}// end of switch block
		}// end of catch block
		catch ( IOException ex )
		{
		    // I/O error
		}// end of catch block
	}// end of method setPropValues
	
}// end of class Preference
