package com.bushbungalo;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.bushbungalo.model.CityData;
import com.bushbungalo.utils.ConnectionManager;
import com.bushbungalo.utils.JSONHelper;
import com.bushbungalo.utils.LionSecurityManager;
import com.bushbungalo.utils.UtilityMethod;
import com.bushbungalo.utils.XMLHelper;

/**
 * @author Paul O. Patterson
 * @version     1.1
 * @since       1.0
 * 
 * <p>
 * This class is responsible for the main execution of the program and to ensure
 * that all required components and access is available before the program's launch. 
 * </p>
 * 
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>11/29/18 - Added check for Internet Connectivity and proceed accordingly.</li>
 * 		<li>01/28/19 - Added constants for default weather icons.</li>
 * 		<li>
 * 			01/28/19 - Added fields {@code iconPackList}, {@code iconSetComponents}, and
 *                     {@code iconPacksLoaded} to for dynamic icon loading support.
 *       </li>
 *       <li>02/05/19 - Moved assets out of the jar file to eliminate path headaches.</li>
 *       <li>02/06/19 - Added method {@code healthCheck} to ensure that required files are
 *                        found before program launch.</li>
 *       <li>03/02/19 - Added method {@link #locationCheck()}.</li>
 *       <li>05/04/19 - Added field {@code connectedToInternet} for monitoring Internet connectivity.</li>
 * </ul>
 */

public abstract class WeatherLionMain
{
	public static Connection conn = null;
	
	public static final String PROGRAM_NAME = "Weather Lion";
	public static final String MAIN_STORAGE_DIR = "res/storage/";
	public static final String MAIN_DATABASE_NAME = "WeatherLion.db";
	public static final String CITIES_DATABASE_NAME = "WorldCities.db";
	public static final String WAK_DATABASE_NAME = "wak.db";
	
	// local weather data file
	public static final String WEATHER_DATA_XML = "WeatherData.xml";	
	public static final String DATA_DIRECTORY_PATH = "res/storage/weather_data/";
	
	// preferences constants
	public static final String WEATHER_SOURCE_PREFERENCE = "wx_source";
	public static final String UPDATE_INTERVAL = "update_interval";
	public static final String CURRENT_LOCATION_PREFERENCE = "location";
	public static final String USE_SYSTEM_LOCATION_PREFERENCE = "use_system_location";
	public static final String USE_METRIC_PREFERENCE = "use_metric";
	public static final String WIDGET_BACKGROUND_PREFERENCE = "widget_background";
	public static final String ICON_SET_PREFERENCE = "icon_set";
	
	// weather provider and web API constants
	public static final String DARK_SKY = "Dark Sky Weather";
	public static final String GEO_NAMES = "GeoNames";
	public static final String HERE_MAPS = "Here Maps Weather";
	public static final String OPEN_WEATHER = "Open Weather Map";
	public static final String WEATHER_BIT = "Weather Bit";
	public static final String YAHOO_WEATHER = "Yahoo! Weather";
	public static final String YR_WEATHER = "Yr.no (Norwegian Meteorological Institute)";
	@Deprecated
	public static final String WEATHER_UNDERGROUND = "Weather Underground";
	public static String[] providerNames = new String[] { 
			DARK_SKY, GEO_NAMES, HERE_MAPS, OPEN_WEATHER, WEATHER_BIT, YAHOO_WEATHER, YR_WEATHER };
	
	public static String[] authorizedProviders;
	
	// Main Icon 
	public static final Image PROGRAM_ICON = UtilityMethod.createImage( "res/assets/img/icons/icon.png" ).getImage();
	
	// Default program icon set name
	public static final String DEFAULT_ICON_SET = "miui";
	public static final String WEATHER_ICONS_PATH = "res/assets/img/weather_images/";
	public static final String WIDGET_BACKGROUNDS_PATH = "res/assets/img/backgrounds/";
	public static final String WIDGET_ICONS_PATH = "res/assets/img/icons/";
	public static String iconSet = null; // To be updated
	
	// Default Icon Set Images
	public static final Icon DEFAULT_ICON_IMAGE = new ImageIcon( WEATHER_ICONS_PATH + DEFAULT_ICON_SET + "/weather_10.png" );
	public static final Icon COLOR_ICON_IMAGE = new ImageIcon( WEATHER_ICONS_PATH + "color/weather_10.png" );
	public static final Icon IKONO_ICON_IMAGE = new ImageIcon( WEATHER_ICONS_PATH + "ikono/weather_10.png" );
	public static final Icon MONO_ICON_IMAGE = new ImageIcon( WEATHER_ICONS_PATH + "mono/display_icon.png" );
		
	// Default Background Icon Images
	public static final Icon DEFAULT_BACKGROUND_IMAGE = new ImageIcon( "res/assets/img/backgrounds/default_background.png" );
	public static final Icon ANDROID_BACKGROUND_IMAGE = new ImageIcon( "res/assets/img/backgrounds/android_bg.png" );
	public static final Icon RABALAC_BACKGROUND_IMAGE = new ImageIcon( "res/assets/img/backgrounds/rabalac_bg.png" );
	
	// Other Images
	public static final Icon LOADING_IMAGE = new ImageIcon( "res/assets/img/icons/loading.gif" );
	
	// look and Feel Constants
	public final static int SYSTEM = 1;
	public final static int METAL = 2;
	public final static int NIMBUS = 3;
	
	// system location
	public static String systemLocation;
	
	// stored preferences
	public static Preference storedPreferences;
	
	// miscellaneous
	public static CityData currentCity;
	public static WeatherLionWidget frmWeatherWidget;
	
	// track whether the data was received from the provider or
	// locally due to Internet connectivity
	public static boolean weatherLoadedFromProvider;
	public static boolean noAccessToStoredProvider;
	
	public static ArrayList< String > iconPackList = new ArrayList<>();
	public static HashMap< String, ArrayList< Component > > iconSetComponents;
	public static boolean iconPacksLoaded;
	public static boolean connectedToInternet = UtilityMethod.hasInternetConnection();
	
	public static File previousCitySearchFile = null;
	public static File previousSearchesPath = new File( "res/storage/previous_searches/" );
	
	// The name of this class
	private static final String TAG = "WeatherLionMain";
	
	public static void main( String[] args )
	{
		// use the operating system's look and feel
		setLookAndFeel( SYSTEM ); 
		
		// clean up any lock files that may have remained after a crash 
		UtilityMethod.cleanLockFiles();
		
		UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, "Initiating startup...", 
				TAG + "::main" );
		
		// build the required storage files
		if( buildRequiredDatabases() == 1 ) 
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, 
					"All required databases constructed successfully.", 
					"WeatherLionMain::main" );
		}// end of if block
		else
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, 
					"All required databases were not constructed successfully.", 
					"WeatherLionMain::main" );
		}// end of else block
		
		// check that the required access keys are available
		LionSecurityManager.init();
		
		// Load only the providers who have access keys assigned to them
		ArrayList< String > wxOnly = LionSecurityManager.webAccessGranted;
		
		Collections.sort( wxOnly );	// sort the list
		
		// GeoNames is not a weather provider so it cannot be select here
		wxOnly.remove( "GeoNames" );
		
		authorizedProviders = wxOnly.toArray( new String[ 0 ] );
		
		// ensure that program has all the default assets needed for functioning properly
		healthCheck();
		
		storedPreferences = Preference.getSavedPreferences();
		
//		PreferenceForm pf = new PreferenceForm( null );
//		pf.showForm();
		
		File previousWeatherData = new File( WeatherLionMain.DATA_DIRECTORY_PATH  
			+ WeatherLionMain.WEATHER_DATA_XML );
		
		// check for an Internet connection or previous weather data stored local
		if( !connectedToInternet && !previousWeatherData.exists() )
		{
			JOptionPane.showMessageDialog( null, "The program will not run without a working internet connection or "
					+ "data that was previously stored locally\nResolve your Internet connection and relaunch the program.",
	    			"Weather Lion", JOptionPane.ERROR_MESSAGE  );
			
			System.exit( 0 );	// terminate the program
		}// end of if block
		else if( connectedToInternet )
		{
			// obtain the current city of the connected Internet service
			currentCity = UtilityMethod.getSystemLocation();
			
			if( currentCity != null )
			{
				if( currentCity.getRegionCode() != null )
				{
					systemLocation = currentCity.getCityName() + ", " + currentCity.getRegionCode();
				}// end of if block
				else
				{
					systemLocation = currentCity.getCityName() + ", " + currentCity.getCountryName();
				}// end of else block
			}// end of if block
			
			// if the user requires the current detected city location to be used as default
			if( storedPreferences.getUseSystemLocation() ) 
			{
				if( systemLocation != null )
				{
					// use the detected city location as the default
					storedPreferences.setLocation( systemLocation );
					
					if( !storedPreferences.getLocation().equals( systemLocation ) ) 
					{
						// update the preferences file
						Preference.setPropValues( Preference.PREFERENCE_FILE,
								CURRENT_LOCATION_PREFERENCE, systemLocation );
						
						// save the city to the local WorldCites database
						UtilityMethod.addCityToDatabase(
								currentCity.getCityName(), currentCity.getCountryName(),
								currentCity.getCountryCode(), currentCity.getRegionName(),
								currentCity.getRegionCode(), currentCity.getLatitude(),
								currentCity.getLongitude() );
						
						JSONHelper.exportToJSON( currentCity );
						XMLHelper.exportToXML( currentCity );
					}// end of if block
				}// end of if block
				else 
				{
					JOptionPane.showMessageDialog( null, "The program was unable to obtain your system's location."
							+ "\nYour location will have to be set manually using the preferences dialog.",
			    			"Weather Lion", JOptionPane.ERROR_MESSAGE  );
					
					PreferenceForm pf = new PreferenceForm( null );
					pf.showForm();
				}// end of else block
			}// end of if block
		}// end of else if block

		init();	
		
	}// end of method main

	private static void healthCheck() 
	{
		String assetsPath = "res/assets/";
		File assetsDirectory = new File( assetsPath );
		
		// the program CANNOT RUN with the assets directory
		if( !assetsDirectory.exists() ) 
		{
			UtilityMethod.missingRequirementsPrompt( "Missing Assets" );			
		}// end of if block
		else
		{
			UtilityMethod.subDirectoriesFound.clear(); // clear any previous list
			
			ArrayList<String> iconPacks = UtilityMethod.getSubdirectories( WEATHER_ICONS_PATH );
			
			if( iconPacks == null || iconPacks.size() == 0 )
			{
				UtilityMethod.missingRequirementsPrompt( "Empty Assets Directory" );				
			}// end of if block
			else 
			{
				UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, 
					"Found " + iconPacks.size() + " icon " +
						( iconPacks.size() > 1 ? "packs..." : "pack..."), 
						TAG 
						+ "::healthCheck" );				
				
				if( !iconPacks.contains( DEFAULT_ICON_SET ) ) 
				{
					UtilityMethod.missingRequirementsPrompt( "Missing Default Icons" );
				}// end of if block
				else if( !iconPacks.contains( Preference.getSavedPreferences().getIconSet() ) ) 
				{
					UtilityMethod.logMessage( UtilityMethod.LogLevel.WARNING, 
						"The " + Preference.getSavedPreferences().getIconSet().toUpperCase() +
						" icon pack could not be found so the default " + DEFAULT_ICON_SET.toUpperCase() +
						" will be used!", TAG + "::healthCheck" );
					
					Preference.setPropValues( Preference.PREFERENCE_FILE,
							WeatherLionMain.ICON_SET_PREFERENCE, DEFAULT_ICON_SET );
				}// end of else if block
				else 
				{
					String iconsInUse = 
							WEATHER_ICONS_PATH + Preference.getSavedPreferences().getIconSet() + "/";
					int imageCount = UtilityMethod.getFileCount( iconsInUse );
										
					if( imageCount < 23 )
					{
						UtilityMethod.missingRequirementsPrompt( "Insufficient Icon Count" );
					}// end of if block
					else
					{
						UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, "Found " + imageCount + 
							( imageCount > 1 ? " images" : " image" ) + " in the " +
							UtilityMethod.toProperCase( Preference.getSavedPreferences().getIconSet() )  +
							" icon pack...", TAG + "::healthCheck" );
					}// end of else block
					
					// check for the background and icon  images
					File backgroundDirectory = new File( WIDGET_BACKGROUNDS_PATH );
					File iconsDirectory = new File( WIDGET_ICONS_PATH );
					
					if( !backgroundDirectory.exists() ) 
					{
						UtilityMethod.missingRequirementsPrompt( "Missing Background Image Directory" );
					}// end of if block
					else
					{
						imageCount = UtilityMethod.getFileCount( WIDGET_BACKGROUNDS_PATH );
						
						if( imageCount < 3 )
						{
							UtilityMethod.missingRequirementsPrompt( imageCount > 1 ? "Missing Background Images" : 
								"Missing Background Image" );
						}// end of if block
						else
						{
							UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, 
								"Found " + imageCount + ( imageCount > 1 ? " images" : " image" )
								+ " in the backgrounds directory...", TAG
								+ "::healthCheck" );
						}// end of else block
					}// end of else block
					
					if( !iconsDirectory.exists() ) 
					{
						UtilityMethod.missingRequirementsPrompt( "Missing Background Image Directory" );
					}// end of if block
					else
					{
						imageCount = UtilityMethod.getFileCount( WIDGET_ICONS_PATH );
						
						if( imageCount < 11 )
						{
							UtilityMethod.missingRequirementsPrompt( imageCount > 1 ? "Missing Icon Images" : 
								"Missing Icon Image" );
						}// end of if block
						else 
						{
							UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO,
								"Found " + imageCount + 
								( imageCount > 1 ? " images" : " image" ) +
								" in the icons directory...", 
								TAG + "::healthCheck" );
						}// end of else block
					}// end of else block
				}// end of else block
			}// end of else block
		}// end of else block
	}// end of method healthCheck
	
	/***
	 * Build the required storage files
	 *
	 * @return An {@code int} value 0 or 1 representing success or failure
	 */
	public static int buildRequiredDatabases()
	{
		File storageDirectory = new File( MAIN_STORAGE_DIR );
		File mainStorageFile = new File( MAIN_STORAGE_DIR + MAIN_DATABASE_NAME );
		File cityStorageFile = new File( MAIN_STORAGE_DIR + CITIES_DATABASE_NAME );
		File wakStorageFile = new File( MAIN_STORAGE_DIR + WAK_DATABASE_NAME );
		int success = 0;
		
		// create all necessary files if they are not present
		if( !storageDirectory.exists() ) 
		{
			storageDirectory.mkdirs();
			
			// build all required sub-files			
			try
			{
				mainStorageFile.createNewFile();
				cityStorageFile.createNewFile();
				wakStorageFile.createNewFile();
			}// end of try black 
			catch ( IOException e )
			{
				UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
			        TAG + "::buildRequiredDatabases [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );				
			}// end of catch block
			
		}// end of if block
		
		if( !mainStorageFile.exists() )
		{
			try
			{
				mainStorageFile.createNewFile();				
			}// end of try black 
			catch ( IOException e )
			{
				UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
			        TAG + "::buildRequiredDatabases [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );				
			}// end of catch block
		}// end of if block	
		
		if( !cityStorageFile.exists() )
		{
			try
			{
				cityStorageFile.createNewFile();				
			}// end of try black 
			catch ( IOException e )
			{
				UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
			        TAG + "::buildRequiredDatabases [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );				
			}// end of catch block
		}// end of if block
		
		if( !wakStorageFile.exists() )
		{
			try
			{
				wakStorageFile.createNewFile();				
			}// end of try black 
			catch ( IOException e )
			{
				UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, e.getMessage(),
			        TAG + "::buildRequiredDatabases [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );				
			}// end of catch block
		}// end of if block
		
		if ( mainStorageFile.exists() && cityStorageFile.exists() && wakStorageFile.exists())
        {
            UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, "The required storage files are present.",
                TAG + "::buildRequiredDatabases" );

            // get a connection to the main DB
            if (conn == null) conn = ConnectionManager.getInstance().getConnection();           

        }// end of if block
        else
        {
            UtilityMethod.logMessage(UtilityMethod.LogLevel.SEVERE, "All the required storage files are not present.",
            		 TAG + "::buildRequiredDatabases");
            return 0;
        }// end of else block	
		
		// attach required databases to the main database file
		if( attachDatabase( wakStorageFile.toString(), "wak" ) == 1 )
		{
			UtilityMethod.createWSADatabase();
			success = 1;		
		}// end of if block
		else
		{
			success = 0;
		}// end of else block
		
		if( attachDatabase( cityStorageFile.toString(), "WorldCities" ) == 1 )
		{
			UtilityMethod.createWorldCitiesDatabase();
			success = 1;			
		}// end of if block
		else
		{
			success = 0;
		}// end of else block
		
		return success;
	}// end of method buildRequiredDatabases
	
	/***
	 * Attach additional databases to the main SQLite database
	 *  
	 * @param dbName	The name of the database to be attached
	 * @param alias		The alias for the database
	 * @return			An {@code int} value of 0 or 1 representing successful execution
	 */
	private static int attachDatabase( String dbName, String alias ) 
	{
		String attachSQL =
				 "ATTACH DATABASE '" + dbName + "' as " + alias + "";
		
		try
		(
			Statement stmt = conn.createStatement();
		)
		{
			stmt.executeUpdate( attachSQL );
			return 1;
		}// end of try block
		catch( SQLException e )
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.SEVERE, "Could not attach database to main file!",
				"WeatherLionMain::buildRequiredDatabase [line: "
				+ e.getStackTrace()[1].getLineNumber() + "]" );
			
			return 0;
		}// end of catch block
	}// end of method attachDatabase
	
	/***
	 * Prepare the program for execution
	 */
	private static void init()
	{		
		if( !locationCheck() )
		{
			JOptionPane.showMessageDialog( null, "The program will not run without a location set.\n"
    			+ "Enjoy the weather!", PROGRAM_NAME + " Setup",
    			JOptionPane.INFORMATION_MESSAGE  );
			
			System.exit( 0 );	// terminate the program
		}// end of if block
		else 
		{
			UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO, "Necessary requirements met...", 
					TAG + "::init" );
			UtilityMethod.logMessage( UtilityMethod.LogLevel.INFO,"Launching Weather Widget...", 
					TAG + "::init" );				
			
			frmWeatherWidget = WeatherLionWidget.getInstance();
		}// end of else block
	}// end of method init
	
	/***
	 * Attempt to store the user's current location
	 * 
	 * @return A {@code boolean} value true/false if successful
	 */
	public static boolean locationCheck() 
	{
		boolean locationSet = false;
		
		if( storedPreferences.getLocation().length() == 0 ) 
		{
			int setCurrentCity;
			
			if( systemLocation != null )
			{
				String prompt = "You must specify a current location in order to run the program.\n" +
						"Your current location is detected as " + systemLocation + ".\n" +
						"Would you like to use it as your current location?";
							
				int useSystemLocation = UtilityMethod.responseBox( prompt, PROGRAM_NAME + " Setup", 
						 new String[] { "Yes", "No" }, JOptionPane.QUESTION_MESSAGE, null );
				
				if ( useSystemLocation == JOptionPane.YES_OPTION )
				{
					storedPreferences.setUseSystemLocation( true );
					storedPreferences.setLocation( systemLocation );
					
					Preference.setPropValues( Preference.PREFERENCE_FILE, 
							USE_SYSTEM_LOCATION_PREFERENCE,
							Boolean.toString( true ) );
					
					Preference.setPropValues( Preference.PREFERENCE_FILE, 
							CURRENT_LOCATION_PREFERENCE,
							systemLocation );
					
					// save the city to the local WorldCites database
	        		UtilityMethod.addCityToDatabase(
	        				currentCity.getCityName(), currentCity.getCountryName(), currentCity.getCountryCode(),
	        				currentCity.getRegionName(), currentCity.getRegionCode(), currentCity.getLatitude(),
	        				currentCity.getLongitude() );
	        		
	        		JSONHelper.exportToJSON( currentCity );
	        		XMLHelper.exportToXML( currentCity );
	        		
	        		locationSet = true;
					frmWeatherWidget = WeatherLionWidget.getInstance();
				}// end of if block
				else 
				{
					String cityPrompt = "You must specify a current location in order to run the program.\n" +
										"Would you like to specify it now?";
								
					setCurrentCity = UtilityMethod.responseBox( cityPrompt, PROGRAM_NAME + " Setup", 
							 new String[] { "Yes", "No" }, JOptionPane.QUESTION_MESSAGE, null );
					
					if ( setCurrentCity == JOptionPane.YES_OPTION )
					{
						PreferenceForm pf = new PreferenceForm( null );
						pf.showForm();
						
						// loop until a city is selected
						while( !PreferenceForm.locationSelected )
						{
							System.out.println( "Waiting for location to be set!" );
						}// end of while loop
						
						locationSet = PreferenceForm.locationSelected;
					}// end of if block		
				}// end of else block
			}// end of if block
			else 
			{
				setCurrentCity = JOptionPane.showConfirmDialog( null,
						"You must specify a current location in order to run the program.\n"
						+ "Would you like to specify it now?",
				        "Setup", JOptionPane.YES_NO_OPTION , 
				        JOptionPane.QUESTION_MESSAGE );
				
				if ( setCurrentCity == JOptionPane.YES_OPTION )
				{
					PreferenceForm pf = new PreferenceForm( null );
					pf.showForm();
					
					// loop until a city is selected
					while( !PreferenceForm.locationSelected )
					{
						System.out.println( "Waiting for location to be set!" );
					}// end of while loop
					
					locationSet = PreferenceForm.locationSelected;
				}// end of if block		
				else
				{
					JOptionPane.showMessageDialog( null, 
							"The program will not run without a location set.\nGoodbye.",
			    			"Look and Feel", JOptionPane.INFORMATION_MESSAGE  );					
					System.exit( 0 );	// terminate the program						
				}// end of else block
			}// end of else block
		}// end of if block
		else
		{
			// the location was already set
			locationSet = true;
		}// end of else block
		
		return locationSet;
	}// end of method locationCheck
	
	/***
	 * Changes the appearance of the user interface throughout the application
	 * based on the user's selection.
	 * 
	 * @param option The specific look and feel that was selected.
	 */
	// set the program's default look and feel
	public static void setLookAndFeel( int option )
	{
		// determine the selected program look and feel
		switch( option )
		{
			case SYSTEM:
				try
				{
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			    }// end of try block
			    catch ( UnsupportedLookAndFeelException e )
			    {
			       // handle exception
			    }// end of UnsupportedLookAndFeelException catch block 
			    catch ( ClassNotFoundException e )
			    {
			       // handle exception
			    }// end of ClassNotFoundException catch block
			    catch ( InstantiationException e ) 
			    {
			       // handle exception
			    }// end of InstantiationException catch block
			    catch ( IllegalAccessException e )
			    {
			       // handle exception
			    }// end of IllegalAccessException catch block
				finally
				{
//					Preference.setPropValues( Preference.PREFERENCE_FILE,
//							"skin", "1" );
				}// end of finally block
				
				break;
			case METAL:
				try
				{
		            // Set cross-platform Java L&F (also called "Metal")
			        UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
				} // end of try block
				catch ( UnsupportedLookAndFeelException e )
			    {
			       // handle exception
			    }// end of UnsupportedLookAndFeelException catch block 
			    catch ( ClassNotFoundException e )
			    {
			       // handle exception
			    }// end of ClassNotFoundException catch block
			    catch ( InstantiationException e ) 
			    {
			       // handle exception
			    }// end of InstantiationException catch block
			    catch ( IllegalAccessException e )
			    {
			       // handle exception
			    }// end of IllegalAccessException catch block
				finally
				{
//					Preference.setPropValues( Preference.PREFERENCE_FILE,
//							"skin", "2" );
				}// end of finally block
				
				break;
			case NIMBUS: default:
				try
				{
					for ( LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) 
				    {
				        if ( "Nimbus".equals( info.getName() ) )
				        {
				            UIManager.setLookAndFeel( info.getClassName() );
				            break;
				        }// end of if block
				    }// end of for block
				}// end of try block
			    catch ( UnsupportedLookAndFeelException e )
			    {
			       // handle exception
			    }// end of UnsupportedLookAndFeelException catch block 
			    catch ( ClassNotFoundException e )
			    {
			       // handle exception
			    	JOptionPane.showMessageDialog( null, "The Nimbus Look and Feel does not "
			    			+ "seem to be installed on your system.",
			    			"Look and Feel", JOptionPane.INFORMATION_MESSAGE  );
			    }// end of ClassNotFoundException catch block
			    catch ( InstantiationException e ) 
			    {
			       // handle exception
			    }// end of InstantiationException catch block
			    catch ( IllegalAccessException e )
			    {
			       // handle exception
			    }// end of IllegalAccessException catch block
				finally
				{
//					Preference.setPropValues( Preference.PREFERENCE_FILE,
//							"skin", "3" );
				}// end of finally block
				
				break;
		}// end of switch
	}// end of method setLookAndFeel
}// end of class WeatherLionMain
