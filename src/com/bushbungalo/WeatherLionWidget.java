package com.bushbungalo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bushbungalo.model.CityData;
import com.bushbungalo.model.DarkSkyWeatherDataItem;
import com.bushbungalo.model.HereMapsWeatherDataItem;
import com.bushbungalo.model.OpenWeatherMapWeatherDataItem;
import com.bushbungalo.model.WeatherBitWeatherDataItem;
import com.bushbungalo.model.WeatherUndergroundDataItem;
import com.bushbungalo.model.YahooWeatherDataItem;
import com.bushbungalo.model.YahooWeatherYdnDataItem;
import com.bushbungalo.model.YrWeatherDataItem;
import com.bushbungalo.model.YrWeatherDataItem.Forecast;
import com.bushbungalo.services.IconUpdateService;
import com.bushbungalo.services.WeatherDataXMLService;
import com.bushbungalo.utils.LionSecurityManager;
import com.bushbungalo.utils.UtilityMethod;
import com.google.gson.Gson;

/**
 * @author Paul O. Patterson
 * @version     1.1
 * @since       1.0
 * 
 * <p>
 * This class is responsible for the widget's construction and operation. 
 * </p>
 * 
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 *		<li>09/27/18 - Added tool tip texts for some components</li>
 * 		<li>10/11/18 - Refactored Code and Updated thunderstorms to t-storms whenever applicable</li>
 * 		<li>10/13/18 - Added runnable to monitor instant time changes and update the current condition icon to match the time of day. Updated sunset calculation</li>
 * 		<li>10/18/18 - Added the time based on the system clock as well as coloured both time and current temperature</li>
 * 		<li>11/12/18 - Modified the positions of the sunrise and sunset time label icons</li>
 * 		<li>
 * 			11/29/18 
 * 				<ol>
 * 					<li>Added check for Internet Connectivity and load from local data until connection is restored.</li>
 * 					<li>Added method {@code loadPreviousWeatherData} in class {@code WidgetUpdateService}.</li>
 * 					<li>Updated method {@code updateTemps} in class {@code WidgetUpdateService} to include a {@code boolean} param.</li>
 * 					<li>Added method {@code addRightClickPopup} to ensure that all components will display a pop-up menu on right-click.</li>
 * 				</ol>
 * 		</li>
 * 		<li>12/01/18 - Updated configuration file check to be aware of corrupted files in method {@code createUserForm}.</li>
 * 		<li>01/02/19 - Updated handling of no Internet connection.</li>
 * 		<li>01/22/19 - Updated handling of null icons due to unknown weather condition.</li>
 * 		<li>
 * 			1/29/19 
 * 				<ol>
 * 					<li>Added method {@link #updateIconSet} for the ability to select different weather icons.</li>
 * 					<li>Added methods {@link #createComponentMap}, {@link #getComponentByName} and {@link #getAllComponents} </li>
 * 				</ol>
 * 		</li>
 * 		<li>02/05/19 - Moved assets out of the jar file to eliminate path headaches.</li>
 * 		<li>05/04/19 - Method {@link #checkAstronomy} no longer calls the widget service.</li>
 * 		<li>05/11/19 - Added field {@code dataLoadedSuccessfully} to track loading success.</li>
 * 		<li>07/03/19 - Updated preference saving method.</li>
 * </ul>
 */

@SuppressWarnings({ "unused", "deprecation" })
public class WeatherLionWidget extends JFrame implements Runnable
{
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;

	private static WeatherLionWidget m_instance = null;
	
	//	private JComponent caller;
    private static int xPos;
 	private static int yPos;
	
	// form objects
	public static JDialog frmWeatherWidget;
	
	// main label
	private static JLabel lblWidget;
	
	// labels that will display images
	private static JLabel lblCurrentConditionImage;
	private static JLabel lblRefresh;
	public static JLabel lblOffline;

	// labels that display text	
	private static JLabel lblCurrentTemperature;
	private static JLabel lblFeelsLike;
	private static JLabel lblDayHigh;
	private static JLabel lblDayLow;
	private static JLabel lblWeatherCondition;
	private static JLabel lblWindReading;
	private static JLabel lblHumidity;
	private static JLabel lblLocation;
	private static JLabel lblSunrise;
	private static JLabel lblSunset;
	private static JLabel lblClock;
	private static JLabel lblWeatherProvider;
		
	// forecast labels
	private static JLabel lblDay1Day;
	private static JLabel lblDay1Image;
	private static JLabel lblDay1Temps;
	private static JLabel lblDay2Day;
	private static JLabel lblDay2Image;
	private static JLabel lblDay2Temps;
	private static JLabel lblDay3Day;
	private static JLabel lblDay3Image;
	private static JLabel lblDay3Temps;
	private static JLabel lblDay4Day;
	private static JLabel lblDay4Image;
	private static JLabel lblDay4Temps;
	private static JLabel lblDay5Day;
	private static JLabel lblDay5Image;
	private static JLabel lblDay5Temps;
	
	// panels
	private static JPanel pnlWindAndAptomosphere;
	
	// right click pop-up
	private static JPopupMenu popRightClick;
	
	// right click menu items
	private static JMenuItem mitRefresh;
	private static JMenuItem mitAddKey;
	private static JMenuItem mitSettings;
	private static JMenuItem mitExit;
	
	// Placeholder Constant
	private static final String NO_READING_FORECAST = "0° 0°";
	private static final String NO_READING = "0°F";
	private static final String NO_READING_H = "0°";
	private static final String FEELS_LIKE = "Feels Like";
	private static final String NO_CONDITION = "Current Condition";
	private static final String NO_LOCATION = "Current Location";
	private static final String NO_WIND = "N 0 mph";
	private static final String NO_HUMIDITY = "0%";
	private static final String MONDAY = "Mon";
	private static final String TUESDAY = "Tue";
	private static final String WEDNESDAY = "Wed";
	private static final String THURSDAY = "Thu";
	private static final String FRIDAY = "Fri";	
	private static final String SUNRISE = "6:00 AM";
	private static final String SUNSET = "6:00 PM";
	private static final String PROVIDER = "Weather Provider";
		
	// Default Icon Images
	private static final Icon WIDGET_BACKGROUND_IMAGE = new ImageIcon( "res/assets/img/backgrounds/default_background.png" );
	private static final Icon DEFAULT_WEATHER_IMAGE = new ImageIcon( "res/assets/img/weather_images/miui/weather_15.png" );
	private static final Icon OFFLINE_IMAGE = new ImageIcon( "res/assets/img/icons/offline.png" );
	private static final Icon REFRESH_IMAGE = new ImageIcon( "res/assets/img/icons/refresh.png" );
	private static final Icon WIND_IMAGE = new ImageIcon( "res/assets/img/icons/wind.png" );
	private static final Icon HUMIDITY_IMAGE = new ImageIcon( "res/assets/img/icons/humidity.png" );
	private static final Icon GEOLOCATION_IMAGE = new ImageIcon( "res/assets/img/icons/geolocation.png" );
	private static final Icon SUNRISE_IMAGE = new ImageIcon( "res/assets/img/icons/sunrise.png" );
	private static final Icon SUNSET_IMAGE = new ImageIcon( "res/assets/img/icons/sunset.png" );
	
	// fonts
 	private static Font samsungsans = null;
 	private static Font samsungsansRegular = null;
 	 	
	private static boolean sunriseIconsInUse;
 	private static boolean sunriseUpdatedPerformed;
 	private static boolean sunsetIconsInUse;
 	private static boolean sunsetUpdatedPerformed;
 	
 	public static boolean usingPreviousData = false; // flag for old weather data
 	
 	private static HashMap< String, Component > componentMap;
 	
 	// The name of this class
 	private static Class<?> thisClass = new Object(){}.getClass();
 	
	public static boolean running;
	public static boolean iconSetSwtich;
	public static boolean reAttempted;
	public static boolean dataLoadedSuccessfully;
	
	public static final String WEATHER_IMAGE_PATH_PREFIX =  "res/assets/img/weather_images/";
	
	public static boolean applyPreferenceUpdates;
	
	public static LinkedHashMap<String, String> preferenceUpdated = new LinkedHashMap<String, String>();
	public static StringBuilder previousWeatherProvider = new StringBuilder();
	
	public static Thread widgetThread;
	public static Thread iconThread;
	public static WidgetUpdateService ws;	
	
	private static final String TAG = "WeatherLionWidget";

	private WeatherLionWidget()
	{		
		// build all the components that the user form will contain as well as the form itself
 		InitializeComponents();
 		 		
 		 // indicate that the program is running
 		running = true;
 		
 	    // start the object that is implemented by the WeatherLionWidget class
 		widgetThread = new Thread( this ); 
		
 		Runnable iconService = new IconUpdateService();
 		iconThread = new Thread( iconService );
 		
 		widgetThread.start();		
	}// default constructor
	
	/**
	 * Monitor the system clock and display the appropriate icons
	 * 
	 * <p>
	 * This method monitors the system's clock and take into account 
	 * the weather provider's sunrise and sunset times and update the
	 * weather icons to be consistent with the time of day.
	 * </p>
	 * 
	 */
	public static void checkAstronomy() 
	{
		lblSunrise.setText( WidgetUpdateService.sunriseTime.toString() );
        lblSunset.setText( WidgetUpdateService.sunsetTime.toString() );
        
		// update icons based on the time of day in relation to sunrise and sunset times
        if( WidgetUpdateService.sunriseTime != null && WidgetUpdateService.sunsetTime != null )
        {
        	// Load current condition weather image
            Calendar rightNow = Calendar.getInstance();
            Calendar nightFall = Calendar.getInstance();
            Calendar sunUp = Calendar.getInstance();
            String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
            		+ " " + UtilityMethod.get24HourTime( WidgetUpdateService.sunsetTime.toString() );
            String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
            		+ " " + UtilityMethod.get24HourTime( WidgetUpdateService.sunriseTime.toString() );
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
            Date rn = null; // date time right now (rn)
    		Date nf = null; // date time night fall (nf)
    		Date su = null; // date time sun up (su)
    		
    		updateClock( rightNow );
    		    		    		
            try 
            {
    			rn = sdf.parse( sdf.format( rightNow.getTime() ) );
    			nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
    			nightFall.set( Calendar.MINUTE, 
    					Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
    			sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
    			
    			nf = sdf.parse( sdf.format( nightFall.getTime() ) );
    			su = sdf.parse( sdf.format( sunUp.getTime() ) );
    		} // end of try block
            catch ( ParseException e )
            {
    			UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::checkAstronomy [line: " + e.getStackTrace()[ 1 ].getLineNumber() + "]" );
    		}// end of catch block
            	        
            String currentConditionIcon = null;
            
            if ( ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) ) )
            {
                if ( WidgetUpdateService.currentCondition.toString().toLowerCase().contains( "(night)" ) )
                {
                    currentConditionIcon = UtilityMethod.weatherImages.get(
                		WidgetUpdateService.currentCondition.toString().toLowerCase() );
                }// end of if block
                else
                {
                	// Yahoo has a habit of having sunny nights
                	if( WidgetUpdateService.currentCondition.toString().equalsIgnoreCase( "sunny" ) )
					{
                		WidgetUpdateService.currentCondition.setLength( 0 );
                		WidgetUpdateService.currentCondition.append( "Clear" );
                		lblWeatherCondition.setText( UtilityMethod.toProperCase( 
                				WidgetUpdateService.currentCondition.toString() ) );
					}// end of if block
                	
                    if( UtilityMethod.weatherImages.containsKey( 
                		WidgetUpdateService.currentCondition.toString().toLowerCase() + " (night)" ) )
                    {
                        currentConditionIcon =
                    		UtilityMethod.weatherImages.get( 
                				WidgetUpdateService.currentCondition.toString().toLowerCase() + " (night)" );
                    }// end of if block
                    else
                    {
                        currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		WidgetUpdateService.currentCondition.toString().toLowerCase() );
                    }// end of else block
                }// end of else block
            	            	
            	if( !sunsetUpdatedPerformed && !sunsetIconsInUse )
            	{
            		lblCurrentConditionImage.setToolTipText( UtilityMethod.toProperCase(
            			WidgetUpdateService.currentCondition.toString() ) );
            		
            		sunsetIconsInUse = true;
                	sunriseIconsInUse = false;
            		sunsetUpdatedPerformed = true;
            		sunriseUpdatedPerformed = false;
            	}// end of if block 
            	else if( WeatherLionWidget.iconSetSwtich )
                {
                	// reset the flag after switch is made
                	WeatherLionWidget.iconSetSwtich = false;
                }// end of else if block

            }// end of if block
            else if( WeatherLionWidget.iconSetSwtich )
            {
            	currentConditionIcon = UtilityMethod.weatherImages.get(
    				WidgetUpdateService.currentCondition.toString().toLowerCase() );
            	
            	// reset the flag after switch is made
            	WeatherLionWidget.iconSetSwtich = false;
            }// end of else if block
            else
            {
            	currentConditionIcon = UtilityMethod.weatherImages.get(
    				WidgetUpdateService.currentCondition.toString().toLowerCase() );
        		
        		if( !sunriseUpdatedPerformed && !sunriseIconsInUse )
            	{
            		lblCurrentConditionImage.setToolTipText(
        				UtilityMethod.toProperCase( WidgetUpdateService.currentCondition.toString() ) );
            		
            		sunriseUpdatedPerformed = true;
            		sunsetUpdatedPerformed = false;
            	}// end of if block
        		else if( WeatherLionWidget.iconSetSwtich )
                {
                	currentConditionIcon = UtilityMethod.weatherImages.get(
        				WidgetUpdateService.currentCondition.toString().toLowerCase() );            	
                	
                	// reset the flag after switch is made
                	WeatherLionWidget.iconSetSwtich = false;
                }// end of else if block
        		else 
        		{
        			sunriseIconsInUse = true;
            		sunsetIconsInUse = false;
        		}// end of else block
        		
            }// end of else block
            
            WidgetUpdateService.loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
    				WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );
        }// end of if block
	}// end of method checkAstronomy

	/**
	 * Display the system current time on the widget.
	 * 
	 * @param rightNow The current system date and time
	 */
	private static void updateClock( Calendar rightNow )
	{
		String currentTime =  new SimpleDateFormat( "h:mm" ).format( rightNow.getTime() );
		String timeOfDay = new SimpleDateFormat( "a" ).format( rightNow.getTime() );
		lblClock.setText( "<html>" + currentTime + "<sup style='font-size: 0.5em'>" +
					      timeOfDay + "</sup></html>" );	// Update the clock
	}// end of method updateClock

	/**
	 * Return a singleton instance of the weather widget.
	 * 
	 * @return The only instance of the {@code WeatherLionWidget} class.
	 */
	public static WeatherLionWidget getInstance()
	{
		if( m_instance == null ) 
		{
			return new  WeatherLionWidget();
		}// end of if block
		else
		{
			return m_instance;
		}// end of else block
	}// end of method getInstance
	
	/**
	 * Load all the necessary components for the program operation
	 */
	private void InitializeComponents()
	{
		// create the user form ( JFrame )
		createUserForm();
		
		// create the right-click pop-up menu
		createUserFormPopupMenu(); 
				
		WeatherLionMain.iconSet = WeatherLionMain.storedPreferences.getIconSet();
		
		switch ( WeatherLionMain.storedPreferences.getWidgetBackground() )
		{
			case "default": default:
				lblWidget.setIcon( WeatherLionMain.DEFAULT_BACKGROUND_IMAGE );
				frmWeatherWidget.repaint();
				break;
			case "android":
				lblWidget.setIcon( WeatherLionMain.ANDROID_BACKGROUND_IMAGE );
				frmWeatherWidget.repaint();
				break;	
			case "rabalac":
				lblWidget.setIcon( WeatherLionMain.RABALAC_BACKGROUND_IMAGE );
				frmWeatherWidget.repaint();
				break;								
		}// end of switch block
		
		createComponentMap();
		
		ws = new WidgetUpdateService( false );
		
		File wxDataFile = new File( WeatherLionMain.DATA_DIRECTORY_PATH + 
			WeatherLionMain.WEATHER_DATA_XML );
		
		if( wxDataFile.exists() )
		{
			// Load old data into the widget first before contacting the provider
			ws.loadPreviousWeatherData(); 
			
			// update the current time label
			updateClock( Calendar.getInstance() );
		}// end of if block
			
		// run the weather service
		ws.execute();		
	}// end of method InitializeComponent	
	
	/***
	 * Add all the components of the JDialog into a {@code HashMap}
	 * Credit to https://stackoverflow.com/questions/4958600/get-a-swing-component-by-name
	 * 
	 * @author Jesse Strickland
	 */
	private void createComponentMap()
	{
		componentMap = new HashMap< String, Component >();
        getAllComponents( frmWeatherWidget.getContentPane() );
	}// end of method createComponentMap
	
	/***
	 * Add all the components of the JDialog into a {@code HashMap}
	 * https://stackoverflow.com/questions/6495769/how-to-get-all-elements-inside-a-jframe
	 * 
	 * @author aioobe
	 */
	private static HashMap< String, Component > getAllComponents( final Container c )
	{
	    Component[] comps = c.getComponents();
	    	    
	    for ( Component comp : comps )
	    {
	    	componentMap.put( comp.getName(), comp );
            
            if ( comp instanceof Container )
            {
            	componentMap.putAll( getAllComponents( (Container) comp ) );
            }// end of if block
	    }// end of for loop
	    
	    return componentMap;
	}// end of method getAllComponents

	/***
	 * Return a {@code JDialog} component referenced in a {@code HashMap}
	 * Credit to https://stackoverflow.com/questions/4958600/get-a-swing-component-by-name
	 * @author Jesse Strickland
	 * 
	 * @param name A {@code String} value representing the name of the component
	 * @return	The requested {@code Component}
	 */
	public static Component getComponentByName( String name )
	{
        if( componentMap.containsKey( name ) )
        {
                return (Component) componentMap.get( name );
        }// end of if block
        else
        {
        	UtilityMethod.logMessage( "severe", "Component " + name + " is not among the loaded components!",
    			thisClass.getEnclosingClass().getSimpleName() + "::getComponentByName" );
        	return null;
        }// end of else block
	}// end of method getComponentByName
  	
  	/***
	 * Creates a right-click menu for the widget.
	 */
	private void createUserFormPopupMenu()
	{
		PopupMenuHandler pmh = new PopupMenuHandler();
		
		//
		// txaPopup
		//
		popRightClick = new JPopupMenu();
		        
	    mitAddKey = new JMenuItem( "Add/Delete Keys" );
	    mitAddKey.setIcon( new ImageIcon( "res/assets/img/icons/key.png" ) );
	    mitAddKey.addActionListener( pmh );
	    popRightClick.add( mitAddKey );
	    
	    mitSettings = new JMenuItem( "Preferences" );
	    mitSettings.setIcon( new ImageIcon( "res/assets/img/icons/preferences.png" ) );
	    mitSettings.addActionListener( pmh );
	    popRightClick.add( mitSettings );
	    
	    mitRefresh = new JMenuItem( "Refresh" );
	    mitRefresh.setIcon( new ImageIcon( "res/assets/img/icons/refresh_m.png" ) );
	    mitRefresh.addActionListener( pmh );
	    popRightClick.add( mitRefresh );
	    
	    popRightClick.addSeparator();	    
	    
	    mitExit = new JMenuItem( "Exit" );
	    mitExit.setIcon( new ImageIcon( "res/assets/img/icons/exit.png" ) );
	    mitExit.addActionListener( pmh );
	    popRightClick.add( mitExit );
	    
	    frmWeatherWidget.addMouseListener( pmh );

	}// end of method createUserFormPopupMenu
  	
	/***
	 * Creates the main user form that interfaces with the user.
	 */
	private void createUserForm()
	{
		// attempt to enable Font Anti-aliasing 
		String property = "swing.aatext";
		
		Border bottom_border = BorderFactory.createMatteBorder( 0, 0, 1 , 0,
				makeTransparent( Color.WHITE, 70 ) );
		
		if ( null == System.getProperty( property ) )
		{
			System.setProperty( "awt.useSystemAAFontSettings", "on" );
			System.setProperty( property, "true" );
		}// end of if block
		
		InputStream bs = null;
		InputStream rs = null;	
				
		// create program fonts
		try 
		{
			bs = new FileInputStream( "res/assets/fonts/Samsungsans.ttf" );
			rs = new FileInputStream( "res/assets/fonts/Samsungsans-Regular.ttf" );		
			samsungsans = Font.createFont( Font.TRUETYPE_FONT, bs );
			samsungsansRegular = Font.createFont( Font.TRUETYPE_FONT, rs );			
		}// end of try block 
		catch ( FontFormatException e )
		{
			UtilityMethod.logMessage( "severe",  "FontFormatException: " + e.getMessage(),
				thisClass.getEnclosingClass().getSimpleName() + "::createUserForm" );
		}// end of catch block
		catch ( IOException e )
		{
			UtilityMethod.logMessage( "severe",  "IOException: " + e.getMessage(),
				thisClass.getEnclosingClass().getSimpleName() + "::createUserForm" );
		}// end of catch block
		
		//
		// frmWeatherWidget
		//
		frmWeatherWidget = new JDialog();
		frmWeatherWidget.setName( "frmWeatherWidget" );
		frmWeatherWidget.setIconImage( UtilityMethod.createImage( "res/assets/img/icons/icon.png" ).getImage() );
		frmWeatherWidget.setTitle( "Weather Lion Widget" );
		frmWeatherWidget.setUndecorated( true );
		frmWeatherWidget.setBackground( new Color( 0, 255, 0, 0 ) ); // set window's background as transparent to match image		
		frmWeatherWidget.setSize( 340, 290 );	// scale the user form ( JFrame )
		
		// Custom Fonts
		Font font14 = samsungsans.deriveFont( 14f );
		Font font16 = samsungsans.deriveFont( 16f );
		Font fontBold18 = samsungsansRegular.deriveFont( Font.BOLD, 18f );
		
		boolean exists = Preference.CONFIG_FILE.exists();
		
		if( exists )
		{
			Preference p = new Preference();
			String tempProp = p.getPropValues( Preference.CONFIG_FILE, "xy" );
			String[] pos;	// store the screen position (x,y) in an array
			
			// if for any reason the file was created and become corrupted
			if( tempProp == null || tempProp.equals( "0" ) )
			{
				Preference.createDefaultConfigPropertiesFile();
				tempProp = p.getPropValues( Preference.CONFIG_FILE, "xy" );
			}// end of if block			
			
			pos = tempProp.split( "," );
			frmWeatherWidget.setLocation( Integer.parseInt( pos[ 0 ] ), Integer.parseInt( pos[ 1 ] ) );
		}// end of if block
		else
		{
			frmWeatherWidget.setLocationRelativeTo( null );			
		}// end of else block
		
		frmWeatherWidget.addWindowListener( new WindowAdapter() 
		{
		    @Override
		    public void windowClosing( WindowEvent windowEvent )
		    {
		    	terminateProgram();
		    }// end of method windowClosing
		});	
		
		lblWidget = new JLabel();
		lblWidget.setName( "lblWidget" );
		lblWidget.setIcon( WIDGET_BACKGROUND_IMAGE );
		
		lblOffline = new JLabel();
		lblOffline.setName( "lblOffline" );
		lblOffline.setIcon( UtilityMethod.scaleImageIcon( OFFLINE_IMAGE, 20, 20 ) );
		lblOffline.setBounds( 284, 8, 20, 20 );
		lblOffline.setToolTipText( "No Internet Connection" );
		lblOffline.setVisible( false );
		lblWidget.add( lblOffline );
        
		lblRefresh = new JLabel();
		lblRefresh.setName( "lblRefresh" );
		lblRefresh.setIcon( UtilityMethod.scaleImageIcon( REFRESH_IMAGE, 20, 20 ) );
		lblRefresh.setBounds( 310, 8, 20, 20 );
		lblRefresh.setToolTipText( "Refresh Weather Data" );
		lblWidget.add( lblRefresh );
		
		lblRefresh.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				long updatePeriod = UtilityMethod.subtractTime( new Date(), UtilityMethod.lastUpdated );
				
				if(  updatePeriod < 10 ) 
				{
					JOptionPane.showMessageDialog( frmWeatherWidget, "The last update was " +
							updatePeriod + ( updatePeriod > 1 ? " mins " : " min " ) + "ago and " +
							"weather conditions don't change that frequently.\nWait at least 10 mins before trying to refresh the data.",
			    			WeatherLionMain.PROGRAM_NAME, JOptionPane.INFORMATION_MESSAGE  );
				}// end of if block
				else
				{
					lblLocation.setText( "Refreshing..." );
					// run the weather service
					ws = new WidgetUpdateService( false );
					ws.execute();
				}// end of else block
			}
		});
		
		lblCurrentConditionImage = new JLabel();
		lblCurrentConditionImage.setName( "lblCurrentConditionImage" );
		lblCurrentConditionImage.setIcon( UtilityMethod.scaleImageIcon( DEFAULT_WEATHER_IMAGE, 140, 140 ) );
		lblCurrentConditionImage.setBounds( 4, 6, 140, 140 );
		lblCurrentConditionImage.setToolTipText( "Clear" );
		lblWidget.add( lblCurrentConditionImage );
		
		lblCurrentTemperature = new JLabel( NO_READING );
		lblCurrentTemperature.setName( "lblCurrentTemperature" );
		lblCurrentTemperature.setFont( samsungsansRegular.deriveFont( 42f ) );		
		lblCurrentTemperature.setForeground( Color.WHITE );
		lblCurrentTemperature.setToolTipText( "Current Temperature" );
		lblCurrentTemperature.setBounds( 144, 14, 100, 40 );
		lblWidget.add( lblCurrentTemperature );
		
		lblFeelsLike = new JLabel( FEELS_LIKE + " " + NO_READING_H, SwingConstants.LEFT );
		lblFeelsLike.setName( "lblFeelsLike" );
		lblFeelsLike.setFont( font16 );
		lblFeelsLike.setForeground( Color.WHITE );
		lblFeelsLike.setToolTipText( "Feels Like Temperature" );
		lblFeelsLike.setBounds( 146, 52, 120, 20 );
		lblWidget.add( lblFeelsLike );
		
		lblDayHigh = new JLabel( NO_READING_H, SwingConstants.CENTER );
		lblDayHigh.setName( "lblDayHigh" );
		lblDayHigh.setFont( font16 );
		lblDayHigh.setForeground( Color.WHITE );
		lblDayHigh.setToolTipText( "Highest Temperature Today" );
		lblDayHigh.setBounds( 248, 16, 30, 20 );
		lblDayHigh.setBorder( bottom_border );
		lblWidget.add( lblDayHigh );
		
		lblDayLow = new JLabel( NO_READING_H, SwingConstants.CENTER );
		lblDayLow.setName( "lblDayLow" );
		lblDayLow.setFont( font16 );
		lblDayLow.setForeground( Color.WHITE );
		lblDayLow.setToolTipText( "Lowest Temperature Today" );
		lblDayLow.setBounds( 248, 35, 30, 20 );
		lblWidget.add( lblDayLow );	
		
		lblWeatherCondition = new JLabel( NO_CONDITION );
		lblWeatherCondition.setName( "lblWeatherCondition" );
		lblWeatherCondition.setForeground( Color.WHITE );
		lblWeatherCondition.setFont( fontBold18 );
		lblWeatherCondition.setToolTipText( "Current Conditions" );
		lblWeatherCondition.setBounds( 146, 64, 200, 40 );
		lblWidget.add( lblWeatherCondition );			
			
		lblWindReading = new JLabel( NO_WIND, WIND_IMAGE, JLabel.LEFT );
		lblWindReading.setName( "lblWindReading" );
		lblWindReading.setForeground( Color.WHITE );
		lblWindReading.setFont( font14 );
		lblWindReading.setToolTipText( "Current Wind Reading" );
		lblWindReading.setIconTextGap( 2 );
		lblWindReading.setBorder( new EmptyBorder( 0, 0, 0, 10 ) ); // pad the label 10px to the right 
				
		lblHumidity = new JLabel( NO_HUMIDITY, HUMIDITY_IMAGE, JLabel.LEFT );
		lblHumidity.setName( "lblHumidity" );
		lblHumidity.setForeground( Color.WHITE );
		lblHumidity.setFont( font14 );
		lblHumidity.setToolTipText( "Humidity" );
		lblHumidity.setIconTextGap( 1 );	
		
		pnlWindAndAptomosphere = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
		pnlWindAndAptomosphere.setName( "pnlWindAndAptomosphere" );
		pnlWindAndAptomosphere.setBackground( new Color( 0, 255, 0, 0 ) );
		pnlWindAndAptomosphere.setBounds( 146, 98, 192, 16 );
		pnlWindAndAptomosphere.add( lblWindReading );
		pnlWindAndAptomosphere.add( lblHumidity );
		lblWidget.add( pnlWindAndAptomosphere );		
		
		lblLocation = new JLabel( NO_LOCATION, GEOLOCATION_IMAGE, JLabel.LEFT  );
		lblLocation.setName( "lblLocation" );
		lblLocation.setForeground( Color.WHITE );
		lblLocation.setFont( font14 );
		lblLocation.setToolTipText( "Current Location" );
		lblLocation.setBounds( 146, 108, 200, 40 );		
		lblWidget.add( lblLocation );	
				
		lblSunrise = new JLabel( SUNRISE, SUNRISE_IMAGE, JLabel.CENTER );
		lblSunrise.setName( "lblSunrise" );
		//Set the position of the text, relative to the icon
		lblSunrise.setVerticalTextPosition( JLabel.BOTTOM );
		lblSunrise.setHorizontalTextPosition( JLabel.CENTER );
		lblSunrise.setForeground( Color.WHITE );
		lblSunrise.setFont( font16 );
		lblSunrise.setToolTipText( "Sunrise Time" );
		lblSunrise.setIconTextGap( 0 );
		lblSunrise.setBounds( 6, 140, 100, 36 );
		lblWidget.add( lblSunrise );
								
		lblSunset = new JLabel( SUNRISE, SUNSET_IMAGE, JLabel.CENTER );
		lblSunset.setName( "lblSunset" );
		//Set the position of the text, relative to the icon
		lblSunset.setVerticalTextPosition( JLabel.BOTTOM );
		lblSunset.setHorizontalTextPosition( JLabel.CENTER );
		lblSunset.setForeground( Color.WHITE );
		lblSunset.setToolTipText( "Sunset Time" );
		lblSunset.setIconTextGap( 0 );
		lblSunset.setFont( font16 );
		lblSunset.setBounds( 232, 140, 100, 36 );
		lblWidget.add( lblSunset );
				
		lblClock = new JLabel( "", SwingConstants.CENTER );
		lblClock.setName( "lblClock" );
		lblClock .setForeground( new Color( 244, 150, 48, 0 ) );
		lblClock.setFont( samsungsansRegular.deriveFont( 44f ) );
		lblClock .setToolTipText( "Current Time" );
		lblClock.setBounds( 94, 140, 150, 40 );
		lblWidget.add( lblClock );
		
		// Five Day Forecast Area
		lblDay1Day = new JLabel( MONDAY, SwingConstants.CENTER );
		lblDay1Day.setName( "lblDay1Day");
		lblDay1Day.setFont( font14 );
		lblDay1Day.setForeground( Color.WHITE );
		
		lblDay1Image = new JLabel( "", SwingConstants.CENTER );
		lblDay1Image.setName( "lblDay1Image" );
		lblDay1Image.setIcon( UtilityMethod.scaleImageIcon( DEFAULT_WEATHER_IMAGE, 40, 40 ) );
		
		lblDay1Temps = new JLabel( NO_READING_FORECAST, SwingConstants.CENTER );
		lblDay1Temps.setName( "lblDay1Temps" );
		lblDay1Temps.setFont( font14 );
		lblDay1Temps.setForeground( Color.WHITE );		
		lblDay1Temps.setVerticalAlignment( JLabel.CENTER );
		
		JPanel pnlDay1 = new JPanel();
		pnlDay1.setName( "pnlDay1" );
		pnlDay1.setBackground( new Color( 0, 255, 0, 0 ) );
		pnlDay1.setBounds( 9, 184, 60, 80 );
		pnlDay1.setLayout( new BorderLayout() );		
		pnlDay1.add( lblDay1Day, "North" );
		pnlDay1.add( lblDay1Image, "Center" );
		pnlDay1.add( lblDay1Temps, "South" );		
		
		lblWidget.add( pnlDay1 );
		
		lblDay2Day = new JLabel( TUESDAY, SwingConstants.CENTER );
		lblDay2Day.setName( "lblDay2Day" );
		lblDay2Day.setFont( font14 );
		lblDay2Day.setForeground( Color.WHITE );
		
		lblDay2Image = new JLabel( "", SwingConstants.CENTER );
		lblDay2Image.setName( "lblDay2Image" );
		lblDay2Image.setIcon( UtilityMethod.scaleImageIcon(DEFAULT_WEATHER_IMAGE, 40, 40 ) );
		
		lblDay2Temps = new JLabel( NO_READING_FORECAST, SwingConstants.CENTER );
		lblDay2Temps.setName( "lblDay2Temps" );
		lblDay2Temps.setFont( font14 );
		lblDay2Temps.setForeground( Color.WHITE );		
		lblDay2Temps.setVerticalAlignment( JLabel.CENTER );
		
		JPanel pnlDay2 = new JPanel();
		pnlDay2.setName( "pnlDay2" );
		pnlDay2.setBackground(  new Color( 0, 255, 0, 0 ) );
		pnlDay2.setBounds( 75, 184, 60, 80 );
		pnlDay2.setLayout( new BorderLayout() );		
		pnlDay2.add( lblDay2Day, "North" );
		pnlDay2.add( lblDay2Image, "Center" );
		pnlDay2.add( lblDay2Temps, "South" );		
		
		lblWidget.add( pnlDay2 );
		
		lblDay3Day = new JLabel( WEDNESDAY, SwingConstants.CENTER );
		lblDay3Day.setName( "lblDay3Day" );
		lblDay3Day.setFont( font14 );
		lblDay3Day.setForeground( Color.WHITE );
		
		lblDay3Image = new JLabel( "", SwingConstants.CENTER );
		lblDay3Image.setName( "lblDay3Image" );
		lblDay3Image.setIcon( UtilityMethod.scaleImageIcon( DEFAULT_WEATHER_IMAGE, 40, 40 ) );
		
		lblDay3Temps = new JLabel( NO_READING_FORECAST, SwingConstants.CENTER );
		lblDay3Temps.setName( "lblDay3Temps" );
		lblDay3Temps.setFont( font14 );
		lblDay3Temps.setForeground( Color.WHITE );		
		lblDay3Temps.setVerticalAlignment( JLabel.CENTER );
		
		JPanel pnlDay3 = new JPanel();
		pnlDay3.setName( "pnlDay3" );
		pnlDay3.setBackground(  new Color( 0, 255, 0, 0 ) );
		pnlDay3.setBounds( 141, 184, 60, 80 );
		pnlDay3.setLayout( new BorderLayout() );		
		pnlDay3.add( lblDay3Day, "North" );
		pnlDay3.add( lblDay3Image, "Center" );
		pnlDay3.add( lblDay3Temps, "South" );		
		
		lblWidget.add( pnlDay3 );
		
		lblDay4Day = new JLabel( THURSDAY, SwingConstants.CENTER );
		lblDay4Day.setName( "lblDay4Day" );
		lblDay4Day.setFont( font14 );
		lblDay4Day.setForeground( Color.WHITE );
		
		lblDay4Image = new JLabel( "", SwingConstants.CENTER );
		lblDay4Image.setName( "lblDay4Image" );
		lblDay4Image.setIcon( UtilityMethod.scaleImageIcon( DEFAULT_WEATHER_IMAGE, 40, 40 ) );
		
		lblDay4Temps = new JLabel( NO_READING_FORECAST, SwingConstants.CENTER );
		lblDay4Temps.setName( "lblDay4Temps" );
		lblDay4Temps.setFont( font14 );
		lblDay4Temps.setForeground( Color.WHITE );		
		lblDay4Temps.setVerticalAlignment( JLabel.CENTER );
		
		JPanel pnlDay4 = new JPanel();
		pnlDay4.setName( "pnlDay4" );
		pnlDay4.setBackground( new Color( 0, 255, 0, 0 ) );
		pnlDay4.setBounds( 207, 184, 60, 80 );
		pnlDay4.setLayout( new BorderLayout() );		
		pnlDay4.add( lblDay4Day, "North" );
		pnlDay4.add( lblDay4Image, "Center" );
		pnlDay4.add( lblDay4Temps, "South" );		
		
		lblWidget.add( pnlDay4 );		
		
		lblDay5Day = new JLabel( FRIDAY, SwingConstants.CENTER );
		lblDay5Day.setName( "lblDay5Day" );
		lblDay5Day.setFont( font14 );
		lblDay5Day.setForeground( Color.WHITE );
		
		lblDay5Image = new JLabel( "", SwingConstants.CENTER );
		lblDay5Image.setName( "lblDay5Image" );
		lblDay5Image.setIcon( UtilityMethod.scaleImageIcon( DEFAULT_WEATHER_IMAGE, 40, 40 ) );
		
		lblDay5Temps = new JLabel( NO_READING_FORECAST, SwingConstants.CENTER );
		lblDay5Temps.setName( "lblDay5Temps" );
		lblDay5Temps.setFont( font14 );
		lblDay5Temps.setForeground( Color.WHITE );		
		lblDay5Temps.setVerticalAlignment( JLabel.CENTER );
		
		JPanel pnlDay5 = new JPanel();
		pnlDay5.setName( "pnlDay5" );
		pnlDay5.setBackground(  new Color( 0, 255, 0, 0 ) );
		pnlDay5.setBounds( 273, 184, 60, 80 );
		pnlDay5.setLayout( new BorderLayout() );
		pnlDay5.add( lblDay5Day, "North" );
		pnlDay5.add( lblDay5Image, "Center" );
		pnlDay5.add( lblDay5Temps, "South" );		
		
		lblWidget.add( pnlDay5 );			
		
		lblWeatherProvider = new JLabel( PROVIDER, null, SwingConstants.CENTER );
		lblWeatherProvider.setName( "lblWeatherProvider" );
		lblWeatherProvider.setFont( font14 );
		lblWeatherProvider.setForeground( Color.WHITE );
		lblWeatherProvider.setToolTipText( "Weather Provider" );
		lblWeatherProvider.setBounds( 0, 266, 340, 20 );
		lblWidget.add( lblWeatherProvider );
		
		frmWeatherWidget.getContentPane().add( lblWidget );	
		
		// call the method which will assign the pop-up menu to all components on the widget
		addRightClickPopup( frmWeatherWidget.getContentPane() );
		
		WindowDragListener windowDrag = new WindowDragListener( frmWeatherWidget );
		
		frmWeatherWidget.addMouseListener( windowDrag );	
		frmWeatherWidget.addMouseMotionListener( windowDrag );
	}// end of method createUserForm
	
	/**
	 * Display a pop-up menu on any component that is right-clicked
	 * 
	 * @param parent The component that own's the pop-up menu
	 */
	private void addRightClickPopup( Container parent )
	{
		PopupMenuHandler pmh = new PopupMenuHandler();
		WindowDragListener windowDrag = new WindowDragListener( frmWeatherWidget );
		
		frmWeatherWidget.addMouseListener( windowDrag );	
		frmWeatherWidget.addMouseMotionListener( windowDrag );
		
		for ( Component c : parent.getComponents() )
	    {
			c.addMouseListener( pmh );
			c.addMouseListener( windowDrag );	
			c.addMouseMotionListener( windowDrag );
			
			// use recursion an add a pop-up handler to each component
			// on the JDialog
			if ( c instanceof Container )
	        {
	        	addRightClickPopup( ( Container ) c );
	        }// end of if block
	    }
	}// end of method addRightClickPopup
	
	/**
	 * Changes the transparency of a {@ Color} using it's alpha channel
	 * 
	 * @param source  The original {@code Color} to be made transparent
	 * @param alpha   The level of transparency to be present i.e. % of transparency.
	 * @return        The updated color with transparency added
	 */
	private static Color makeTransparent( Color source, int alpha )
	{
	    return new Color( source.getRed(), source.getGreen(), source.getBlue(), alpha );
	}// end of method makeTransparent
	
	/**
	 * Save the current on screen coordinates of the widget. 
	 */
	private static void saveScreenPosition()
	{
		Point currCoords = frmWeatherWidget.getLocationOnScreen();
    	xPos = currCoords.x;
    	yPos = currCoords.y;
    	
		Preference.setPropValues( Preference.CONFIG_FILE, "xy", xPos + "," + yPos );
	}// end of method saveScreenPosition	

	/***
	 * Terminates the program.
	 */
	private static void terminateProgram() 
	{
		running = false;
		System.exit( 0 );	// terminate the program		
	}// end of method terminateProgram
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() 
	{
		// update the widget on a separate thread
		while( running )
		{	
			 if ( applyPreferenceUpdates )
			 {

				 if( preferenceUpdated.size() > 0 ) 
				 {
					 for ( String preference : preferenceUpdated.keySet() )
					 {
						 switch ( preference )
						 {
						 	case WeatherLionMain.CURRENT_LOCATION_PREFERENCE:
						 		// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference, preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setLocation( 
									preferenceUpdated.get( preference ) );
								
								// reset the re-attempted flag before running the service
								reAttempted = false;
								
								// run the weather service
								ws = new WidgetUpdateService( false );
								ws.execute();
								
								break;
							case WeatherLionMain.WEATHER_SOURCE_PREFERENCE:
								// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference, preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setProvider( 
									preferenceUpdated.get( preference ) );
								
								// reset the re-attempted flag before running the service
								reAttempted = false;
								
								// run the weather service
								ws = new WidgetUpdateService( false );
								ws.execute();
								
								break;
							case WeatherLionMain.USE_SYSTEM_LOCATION_PREFERENCE:
								// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference, preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setUseSystemLocation( 
									Boolean.parseBoolean( preferenceUpdated.get( preference ) ) );
																
								// reset the re-attempted flag before running the service
								reAttempted = false;
								
								// run the weather service
								ws = new WidgetUpdateService( false );
								ws.execute();
								
								break;
							case WeatherLionMain.UPDATE_INTERVAL:
								// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference, preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setInterval( 
									Integer.parseInt( preferenceUpdated.get( preference ) ) );
								
								break;
							case WeatherLionMain.USE_METRIC_PREFERENCE:
								// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference, preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setUseMetric( 
									Boolean.parseBoolean( preferenceUpdated.get( preference ) ) );
								
								// update the units displayed on the widget
								ws = new WidgetUpdateService( true );
								ws.execute();
								
								break;
							case WeatherLionMain.ICON_SET_PREFERENCE:
								// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference, preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setIconSet( 
									preferenceUpdated.get( preference ) );
								
								WeatherLionMain.iconSet = WeatherLionMain.storedPreferences.getIconSet();
								updateIconSet();
								break;		
							case WeatherLionMain.WIDGET_BACKGROUND_PREFERENCE:
								// update the local preference file
								Preference.setPropValues( Preference.PREFERENCE_FILE,
									preference,	preferenceUpdated.get( preference ) );
								
								// update running data
								WeatherLionMain.storedPreferences.setWidgetBackground(
									preferenceUpdated.get( preference ) );
								
								switch ( WeatherLionMain.storedPreferences.getWidgetBackground() )
								{
									case "default": default:
										lblWidget.setIcon( WeatherLionMain.DEFAULT_BACKGROUND_IMAGE );
										frmWeatherWidget.repaint();
										break;
									case "android":
										lblWidget.setIcon( WeatherLionMain.ANDROID_BACKGROUND_IMAGE );
										frmWeatherWidget.repaint();
										break;	
									case "rabalac":
										lblWidget.setIcon( WeatherLionMain.RABALAC_BACKGROUND_IMAGE );
										frmWeatherWidget.repaint();
										break;							
								}// end of switch block
								
								break;						
							default:
								break;
						}// end of switch block
					}// end of for each loop
					 
					// remove all items from the object
	                preferenceUpdated.clear();
				}// end of if block
			 }// end of if block			 
			
			try
			{
				Thread.sleep( WeatherLionMain.storedPreferences.getInterval() );			
			
				ws = new WidgetUpdateService( false );
				ws.execute();
				
			}// end of try block
			catch( InterruptedException e )
			{
				// A thread interrupt will be thrown when a preference is updated.
				running = false;
				resumeThread();
			}// end of catch block
		}// end of while loop					
	}// end of method run	
	
	/**
	 * Change the icon set used to display weather conditions
	 */
	private void updateIconSet()
	{
		String ico = Preference.getSavedPreferences().getIconSet();
		
		WeatherLionMain.iconSet = ico.equals( "default" ) ?
				WeatherLionMain.DEFAULT_ICON_SET : ico;
			
		// indicate that the icons are being changed
		iconSetSwtich = true;
    	
    	// update based on time of day
    	checkAstronomy(); 		
		
		StringBuilder iconFile = null;
		
		// Update five day weather icons
		for( int i = 1; i < 6; i++ ) 
		{
			iconFile = new StringBuilder( 
				WEATHER_IMAGE_PATH_PREFIX + WeatherLionMain.iconSet + "/weather_" + 
					UtilityMethod.weatherImages.get( 
						( (JLabel) getComponentByName( "lblDay" + i + "Image" ) ).getToolTipText().toLowerCase() ) );
			
			WidgetUpdateService.loadWeatherIcon( (JLabel) getComponentByName( "lblDay" + i + "Image"),
					iconFile.toString(), 40, 40 );
		}// end of for loop
		
		frmWeatherWidget.repaint();
	}// end of method updateIconSet

	/**
	 * Resume the thread after it was interrupted.
	 */
	public static void resumeThread() 
	{
		running = true;		
	}// end of method resumeThread	
	
	/**
	 * Private inner class which will handle all the pop-up menu actions
	 * 
	 * @author Paul O. Patterson
	 * @version     1.0
	 * @since       1.0
	 */
	private class PopupMenuHandler extends MouseAdapter implements ActionListener 
	{
		public void actionPerformed( ActionEvent e )
		{
			JMenuItem source = ( JMenuItem )( e.getSource() );
				
			switch ( source.getText() )
			{
				case "Add/Delete Keys":
					LionSecurityManager lm = new LionSecurityManager( frmWeatherWidget );
					lm.showForm();
					
					break;
				case "Preferences":
					PreferenceForm pf = new PreferenceForm( frmWeatherWidget );
					pf.showForm();
					
					break;		
				case "Refresh":
					frmWeatherWidget.repaint();

					break;				
				case "Exit":
					terminateProgram();									
					break;
				default:
					break;
			}// end of switch		
		}// end of method actionPerformed
		
		public void mousePressed( MouseEvent me )
       	{
			showPopup( me );						
        }// end of method mousePressed

        public void mouseReleased( MouseEvent me )
        {
        	showPopup( me );
        }// end of method mouseReleased

        private void showPopup( MouseEvent me )
        {        	
        	if ( me.isPopupTrigger() )
            {
    			popRightClick.show( me.getComponent(), me.getX(), me.getY() );
            }// end of if block					     	
        }// end of method showPopup
    }// end of inner class PopupMenuListener
		
	/**
	 * Private inner class that allow for the window to be move as it is not decorated.
	 * 
	 * @author Paul O. Patterson
	 * @version     1.0
	 * @since       1.0
	 */
	private static class WindowDragListener extends MouseAdapter
	{
        private final JDialog window;
        private Point mouseDownCompCoords = null;

        public WindowDragListener( JDialog window )
        {
            this.window = window;
        }

        public void mouseReleased( MouseEvent e )
        {
            mouseDownCompCoords = null;
            saveScreenPosition();
        }
        
        public void mousePressed( MouseEvent e )
        {
            mouseDownCompCoords = e.getPoint();        
        }
        
        public void mouseDragged( MouseEvent e ) 
        {
        	Point currCoords = e.getLocationOnScreen();
        	
        	if( e != null )
        	{
        		// some elements being dragged might throw an error
        		try 
        		{
					xPos = currCoords.x - mouseDownCompCoords.x;
					yPos = currCoords.y - mouseDownCompCoords.y;
					
					window.setLocation( xPos, yPos );
				}// end of try block
        		catch ( NullPointerException npe )
        		{
				}// end of catch block
        	}// end of if block
        	
        }
    }// end of class WindowDragListener	
	
	/**
	 * Calls updateUI on all sub-components of the JFrame
	 */
	private static void updateUI()
    {
        SwingUtilities.updateComponentTreeUI( frmWeatherWidget );
    }// end of method updateUI
	
	/**
	 * @author Paul O. Patterson
	 * <br />
	 * <b style="margin-left:-40px">Date Created:</b>
	 * <br />
	 * 11/21/17
	 * <br />
	 * <b style="margin-left:-40px">Description:</b>
	 * <br />
	 * <span>Inner class which performs the widget updates</span>
	 * <br />
	 * <b style="margin-left:-40px">Updates:</b><br />
	 * <ul>
	 * 		<li>01/08/19 - Handling for broken weather sources added</li>
	 * 		<li>01/21/19 - Switched from using {@code retrieveYahooGeoLocationUsingAddress} to {@code retrieveHereGeoLocationUsingAddress}
	 *  	until Yahoo! Weather sorts out their new weather API data delivery to developers.</li>
	 *  	<li>
	 *  		01/22/19
	 *  		<ol>
	 *  			<li>Updated weather and temps algorithms</li>
	 *  			<li>Updated weather icon handling</li>
	 *  		</ol>
	 *  	</li>
	 *  	<li>
	 *  		03/23/19
	 *  		<ol>
	 *  			<li>Updated service with addition weather provider, Here Maps Weather</li>
	 *  			<li>Added method {@link #loadHereMapsWeather} as new data provider</li>
	 *  			<li>Added method {@link #astronomyCheck} for code refactoring</li>
	 *  			<li>Modified {@code class WidgetUpdateService} access to public</li>
	 *  		</ol>
	 *  	</li>
	 *  	<li>
	 *  		Updated the Yahoo app id to be sent through "X-Yahoo-App-Id" instead of the 
	 *  		previous "Yahoo-App-Id" due to changes made by Yahoo to their weather API.
	 *  	</li>
	 *  <li>05/07/19 - Weather fields updated to utilize {@code StringBuilder}s instead of {@code String}s<li>
	 *  <li>
     *  	05/11/19
     *  	<ol>
     *  		<li>Added check to see if Yahoo! Weather returns correct JSON data where expected</li>
     *  		<li>Added exception throwing to a weather loading methods</li>
     *  	</ol>
     *  </li>
	 * </ul>
	 */
	public static class WidgetUpdateService extends SwingWorker<ArrayList<String>, Object>
	{
		private static DarkSkyWeatherDataItem darkSky;
		private static HereMapsWeatherDataItem.WeatherData hereWeatherWx;
		private static HereMapsWeatherDataItem.ForecastData hereWeatherFx;
		private static HereMapsWeatherDataItem.AstronomyData hereWeatherAx;
		private static OpenWeatherMapWeatherDataItem.WeatherData openWeatherWx;
		private static OpenWeatherMapWeatherDataItem.ForecastData openWeatherFx;
		private static WeatherBitWeatherDataItem.WeatherData weatherBitWx;
		private static WeatherBitWeatherDataItem.SixteenDayForecastData weatherBitFx;
		private static WeatherUndergroundDataItem underground;
	    private static YahooWeatherDataItem yahoo; // Deprecated code that Yahoo! replaced in 2019	
	    private static YahooWeatherYdnDataItem yahoo19;	
	    private static YrWeatherDataItem yr;	

	    private StringBuilder wxUrl = new StringBuilder();
	    private StringBuilder fxUrl = new StringBuilder();
	    private StringBuilder axUrl = new StringBuilder();
	    private ArrayList<String> strJSON;
	    
	    private final String CELSIUS = "\u00B0C";
	    private final String DEGREES = "\u00B0";
	    private final String FAHRENHEIT = "\u00B0F";

	    private static StringBuilder currentCity = new StringBuilder();
	    private static StringBuilder currentCountry = new StringBuilder();
	    private static StringBuilder currentTemp = new StringBuilder();
	    private static StringBuilder currentFeelsLikeTemp = new StringBuilder();
	    private static StringBuilder currentWindSpeed = new StringBuilder();
	    private static StringBuilder currentWindDirection = new StringBuilder();
	    private static StringBuilder currentHumidity = new StringBuilder();
	    private static StringBuilder currentLocation = new StringBuilder();
	    public  static StringBuilder currentCondition = new StringBuilder();
	    private static StringBuilder currentHigh = new StringBuilder();
	    private static StringBuilder currentLow = new StringBuilder();
	    private static List< FiveDayForecast > currentFiveDayForecast = 
	    	new ArrayList< FiveDayForecast >();
	    private static int[][] hl;
	    	    
	    private boolean unitChange;
	    
	    private WeatherDataXMLService wXML;
	    private Dictionary< String, float[][] > dailyReading;
	    
	    private String tempUnits;
	    
	    private Document weatherXML;
	    private Element rootNode;
	    private Element xmlAtmosphere;
	    private Element xmlCurrent;		    		
	    private Element xmlWind;
    	private Element xmlForecast;
	    private List< Element > xmlForecastList = null;
	    
	    private static LinkedHashMap<String, String> hereMapsWeatherProductKeys;
	    static
	    {
	    	hereMapsWeatherProductKeys = new LinkedHashMap<String, String>();
	    	hereMapsWeatherProductKeys.put( "conditions", "observation" );
	    	hereMapsWeatherProductKeys.put( "forecast", "forecast_7days_simple" );
	    	hereMapsWeatherProductKeys.put( "astronomy", "forecast_astronomy" );
	    };
	    
	    // The name of this class
		private static Class<?> thisClass = new Object(){}.getClass();
		
		// public variables
		public static String darkSkyApiKey = null;
	    public static String hereAppId = null;
	    public static String hereAppCode = null;
	    public static String yahooConsumerKey = null;
	    public static String yahooConsumerSecret = null;
	    public static String yahooAppId = null;
	    public static String openWeatherMapApiKey = null;
	    public static String weatherBitApiKey = null;
	    public static String weatherUndergroundApiKey = null;
	    public static String geoNameAccount = null;
	    
	    public static StringBuilder sunriseTime = new StringBuilder();
	    public static StringBuilder sunsetTime = new StringBuilder();

	    public WidgetUpdateService( boolean unitChange )
		{		
			this.unitChange = unitChange;
			// store the current provider in case of failure
			previousWeatherProvider.setLength( 0 );
			previousWeatherProvider.append( Preference.getSavedPreferences().getProvider() );
		}// end of default constructor		
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ArrayList<String> doInBackground() throws Exception 
		{
			tempUnits = WeatherLionMain.storedPreferences.getUseMetric() ? CELSIUS : FAHRENHEIT;
			currentCity.setLength( 0 );
			currentCity.append( WeatherLionMain.storedPreferences.getLocation() );
			String json = null;
			float lat;
			float lng;
			strJSON = new ArrayList<String>();
			String wxDataPrivider = null;
			
			if( WeatherLionMain.noAccessToStoredProvider ) 
			{
				wxDataPrivider = LionSecurityManager.webAccessGranted.get( 0 );
			}// end of if block
			else
			{
				wxDataPrivider = WeatherLionMain.storedPreferences.getProvider();
			}// end of else block
					
			if( !unitChange )
			{
				// Check the Internet connection availability
		        if( UtilityMethod.hasInternetConnection() )
		        {
		        	wxUrl.setLength( 0 );
		        	fxUrl.setLength( 0 );
		        	axUrl.setLength( 0 );
		        	
		        	switch( wxDataPrivider )
		            {
		            	case WeatherLionMain.DARK_SKY:
		            		json =
    							UtilityMethod.retrieveGeoNamesGeoLocationUsingAddress( currentCity.toString() );
        				
		            		CityData.currentCityData = UtilityMethod.createGeoNamesCityData( json );
		            		lat = CityData.currentCityData.getLatitude();
		            		lng = CityData.currentCityData.getLongitude();
		            		
		            		wxUrl.setLength( 0 );
		            		wxUrl.append( "https://api.darksky.net/forecast/" +
		            					darkSkyApiKey + "/" + lat + "," + lng );
		            		
		            		break;
		            	case WeatherLionMain.OPEN_WEATHER:
		            		json =
    							UtilityMethod.retrieveGeoNamesGeoLocationUsingAddress( currentCity.toString() );
        				
		            		CityData.currentCityData = UtilityMethod.createGeoNamesCityData( json );
		            		lat = CityData.currentCityData.getLatitude();
		            		lng = CityData.currentCityData.getLongitude();
		            		
		            		wxUrl.setLength( 0 );
		            		wxUrl.append( "https://api.openweathermap.org/data/2.5/weather?"+
		            				"lat=" + lat + "&lon=" + lng + "&appid=" + openWeatherMapApiKey +
		            					"&units=imperial" );
		            		
		            		fxUrl.setLength( 0 );
		            		fxUrl.append(
		                            "https://api.openweathermap.org/data/2.5/forecast/daily?" + 
	                            		"lat=" + lat + "&lon=" + lng + "&appid=" + openWeatherMapApiKey +
		            					"&units=imperial" );
	            		
		            		break;
		            	case WeatherLionMain.HERE_MAPS:
		            		json =
        						UtilityMethod.retrieveHereGeoLocationUsingAddress( currentCity.toString() );
	            		
	            			CityData.currentCityData = UtilityMethod.createHereCityData( json );
		            		
	            			wxUrl.setLength( 0 );
		            		wxUrl.append(
		            				"https://weather.api.here.com/weather/1.0/report.json?" +
		            				"app_id=" +	hereAppId +
		            				"&app_code=" + hereAppCode +
		            				"&product=" + hereMapsWeatherProductKeys.get( "conditions" ) +
		            				"&name=" + UtilityMethod.escapeUriString( currentCity.toString() ) +
		            				"&metric=false" );
		            		
		            		fxUrl.setLength( 0 );
		            		fxUrl.append(
		            				"https://weather.api.here.com/weather/1.0/report.json?" +
		            				"app_id=" +	hereAppId +
		            				"&app_code=" + hereAppCode +
		            				"&product=" + hereMapsWeatherProductKeys.get( "forecast" ) +
		            				"&name=" + UtilityMethod.escapeUriString( currentCity.toString() ) +
		            				"&metric=false" );
		            		
		            		axUrl.setLength( 0 );
		            		axUrl.append(
		            				"https://weather.api.here.com/weather/1.0/report.json?" +
		            				"app_id=" +	hereAppId +
		            				"&app_code=" + hereAppCode +
		            				"&product=" + hereMapsWeatherProductKeys.get( "astronomy" ) +
		            				"&name=" + UtilityMethod.escapeUriString( currentCity.toString() ) +
		            				"&metric=false" );
		            		break;
		            	case WeatherLionMain.WEATHER_BIT:
		            		json =
    							UtilityMethod.retrieveGeoNamesGeoLocationUsingAddress( currentCity.toString() );
        				
		            		CityData.currentCityData = UtilityMethod.createGeoNamesCityData( json );
		            		lat = CityData.currentCityData.getLatitude();
		            		lng = CityData.currentCityData.getLongitude();
		            		
		            		wxUrl.setLength( 0 );
		            		wxUrl.append( 
		            				"https://api.weatherbit.io/v2.0/current?city="+ 
	            						UtilityMethod.escapeUriString( currentCity.toString() ) +
	            							"&units=I&key=" + weatherBitApiKey );

		            		// Sixteen day forecast will be used as it contains more relevant data
		            		fxUrl.setLength( 0 );
		            		fxUrl.append(
		            				"https://api.weatherbit.io/v2.0/forecast/daily?city="+ 
	            						UtilityMethod.escapeUriString( currentCity.toString() ) +
	            							"&units=I&key=" + weatherBitApiKey );
		            		break;
		            	case WeatherLionMain.YAHOO_WEATHER:
		            		try 
				        	{
				        		strJSON.add( getYahooWeatherData( WeatherLionMain.storedPreferences.getLocation().toLowerCase() ) );
							}// end of try block
				        	catch ( Exception e )
				        	{
				        		strJSON = null;
							}// end of catch block
		            		
		            		break;
		                case WeatherLionMain.YR_WEATHER:
		                	json =
        						UtilityMethod.retrieveGeoNamesGeoLocationUsingAddress( currentCity.toString() );
            				
            				CityData.currentCityData = UtilityMethod.createGeoNamesCityData( json );
            				
			            	String cityName = 
			            			CityData.currentCityData.getCityName().contains( " " ) ?
			            					CityData.currentCityData.getCityName().replace( " ", "_" ) :
			            						CityData.currentCityData.getCityName();
			            	String countryName = 
			            			CityData.currentCityData.getCountryName().contains( " " ) ?
			            					CityData.currentCityData.getCountryName().replace( " ", "_" ) :
			            						CityData.currentCityData.getCountryName();
			            	String regionName = cityName.equalsIgnoreCase("Kingston") ? "Kingston" :
			            			CityData.currentCityData.getRegionName().contains( " " ) ?
			            				CityData.currentCityData.getRegionName().replace( " ", "_" ) :
			            					CityData.currentCityData.getRegionName();	// Yr data mistakes Kingston as being in St. Andrew
            				
            				wxUrl.setLength( 0 );
	    					wxUrl.append( "https://www.yr.no/place/" +
	    		    				 countryName + "/" + regionName + "/" + cityName + "/forecast.xml" );	            			               
		                	
		                    break;
		            }// end of switch block
		        }// end of if block	
		        
		        if( !wxDataPrivider.equals( WeatherLionMain.YAHOO_WEATHER ) )
		        {
		        	if( wxUrl.length() != 0 && fxUrl.length() != 0 && axUrl.length() != 0 ) 
			        {
			        	strJSON.add( UtilityMethod.retrieveWeatherData( wxUrl.toString() ) );	        			
			        	strJSON.add( UtilityMethod.retrieveWeatherData( fxUrl.toString() ) );	        			
			        	strJSON.add( UtilityMethod.retrieveWeatherData( axUrl.toString() ) );	        			
			        }// end of if block
		        	else if( wxUrl.length() != 0 && fxUrl.length() != 0 && axUrl.length() == 0 ) 
			        {
			        	strJSON.add( UtilityMethod.retrieveWeatherData( wxUrl.toString() ) );	        			
			        	strJSON.add( UtilityMethod.retrieveWeatherData( fxUrl.toString() ) );	        			
			        }// end of if block
			        else if( wxUrl.length() != 0 && fxUrl.length() == 0  && axUrl.length() == 0 ) 
			        {
			        	strJSON.add( UtilityMethod.retrieveWeatherData( wxUrl.toString() ) );	        			
			        }// end of else if block
			        else if( wxUrl.length() == 0 && fxUrl.length() != 0  && axUrl.length() == 0 ) 
			        {
			        	strJSON.add( UtilityMethod.retrieveWeatherData( fxUrl.toString() ) );	        			
			        }// end of else if block
			        else if( wxUrl.length() == 0 && fxUrl.length() == 0  && axUrl.length() != 0 ) 
			        {
			        	strJSON.add( UtilityMethod.retrieveWeatherData( axUrl.toString() ) );	        			
			        }// end of else if block
		        }// end of if block		        
			}// end of if block				
			
			return strJSON;
					
		}// end of message doInBackground
		
		/**
		 * {@inheritDoc}
		 */
		 @Override
	     protected void done()
		 {				 
			 updateUI();
			 
			 if( !unitChange ) 
			 {
				 try 
				 {
					strJSON = get();
				 }// end of try block
				 catch ( InterruptedException e )
				 {
					 UtilityMethod.logMessage( "severe", e.getMessage(),
							 TAG + "::done" );
				 }// end of catch block
				 catch ( ExecutionException e )
				 {
					 UtilityMethod.logMessage( "severe", e.getMessage(),
							 TAG + "::done" );
				 }// end of catch block		
				 
				 // check that the ArrayList is not empty and the the first element is not null 
				 if( strJSON != null && !strJSON.isEmpty() && strJSON.get( 0 ) != null )
				 {
					 lblOffline.setVisible( false ); // we are connected to the Internet if JSON data is returned
					 
					 try 
					 {
						 switch( WeatherLionMain.storedPreferences.getProvider() )
						 {
			            	case WeatherLionMain.DARK_SKY:
			            		darkSky = new Gson().fromJson( strJSON.get( 0 ), DarkSkyWeatherDataItem.class );
			            		loadDarkSkyWeather();
			            		
		                    	break;
			            	case WeatherLionMain.HERE_MAPS:
			            		hereWeatherWx = new Gson().fromJson( strJSON.get( 0 ), HereMapsWeatherDataItem.WeatherData.class );
			            		hereWeatherFx = new Gson().fromJson( strJSON.get( 1 ), HereMapsWeatherDataItem.ForecastData.class );
			            		hereWeatherAx = new Gson().fromJson( strJSON.get( 2 ), HereMapsWeatherDataItem.AstronomyData.class );
			            		loadHereMapsWeather();
			            		
		                    	break;
			            	case WeatherLionMain.OPEN_WEATHER:
			            		openWeatherWx = new Gson().fromJson( strJSON.get( 0 ), OpenWeatherMapWeatherDataItem.WeatherData.class );
			            		openWeatherFx = new Gson().fromJson( strJSON.get( 1 ), OpenWeatherMapWeatherDataItem.ForecastData.class );
			            		loadOpenWeather();

		                    	break;
			            	case WeatherLionMain.WEATHER_BIT:
			            		weatherBitWx = new Gson().fromJson( strJSON.get( 0 ), WeatherBitWeatherDataItem.WeatherData.class );
			            		weatherBitFx = new Gson().fromJson( strJSON.get( 1 ), WeatherBitWeatherDataItem.SixteenDayForecastData.class );
			            		loadWeatherBitWeather();

		                    	break;
			                case WeatherLionMain.YAHOO_WEATHER:
			                	
			                	// Temporary fix for Yahoo! Weather bad JSON data
//								if( !strJSON.toString().startsWith( "{" )  ) 
//								{
//									String temp = strJSON.toString();
//									strJSON.clear();
//									strJSON.add( temp.substring(temp.indexOf( "{" ), temp.lastIndexOf( "}" ) + 1 ) );
//								}// end of if block
			                	
			                	// Yahoo is constantly messing around with their API
			                	String jsonWeatherObj = null;
			            		
			        			// Check if a JSON was returned from the web service
			        			for ( String wxD : strJSON)
			        			{
			        				Object json = null;
									
			        				try 
			        				{
										json = new JSONTokener( wxD ).nextValue();
									}// end of try block
			        				catch ( JSONException e )
			        				{
										UtilityMethod.logMessage( "severe", "Bad Yahoo data: " + e.getMessage(),
												TAG + "::done" );
									}// end of catch block
			        				
			        				if ( json instanceof JSONObject )
				        			{
			        					jsonWeatherObj = wxD;
				        			}// end of if block
								}// end of for each loop
			                	
			                	yahoo19 = new Gson().fromJson( jsonWeatherObj, YahooWeatherYdnDataItem.class);
			                    loadYahooYdnWeather();

			                    break;
			                case WeatherLionMain.YR_WEATHER:
			                    YrWeatherDataItem.deserializeYrXML( strJSON.get( 0 ) );
			                    yr = YrWeatherDataItem.yrWeatherDataItem;		                    
			                    loadYrWeather();

			                    break;
			                default:
			                    break;
						 }// end of switch block
						 
						 UtilityMethod.lastUpdated = new Date();
							
						 SimpleDateFormat dt = new SimpleDateFormat( "E h:mm a" );
						 String timeUpdated = dt.format( UtilityMethod.lastUpdated );
						 currentLocation.setLength( 0 );
						 currentLocation.append( WeatherLionMain.storedPreferences.getLocation() );
			
						 // Update the current location and update time stamp
						 lblLocation.setText( currentLocation.substring( 0, currentLocation.indexOf( "," ) ) +
			                    ", " + timeUpdated );
			
						 // Update the weather provider label
						 lblWeatherProvider.setText( WeatherLionMain.storedPreferences.getProvider() );
						 lblWeatherProvider.setIcon( 
							new ImageIcon(
								"res/assets/img/icons/" +
									WeatherLionMain.storedPreferences.getProvider().toLowerCase() + ".png" ) );
						 
						 if( UtilityMethod.refreshRequested )
						 {
							 UtilityMethod.refreshRequested = false;
						 }// end of if block
						 
						 if( !frmWeatherWidget.isVisible() )
						 {
							 frmWeatherWidget.setVisible( true );				 
						 }// end of if block
						 
						 WeatherLionMain.weatherLoadedFromProvider = true;
						 usingPreviousData = false;
					 }// end of try block
					 catch( Exception e )
					 {
						 WeatherLionWidget.dataLoadedSuccessfully = false;
						 
						 // Undo changes made
						 WeatherLionMain.storedPreferences.setProvider( 
							WeatherLionWidget.previousWeatherProvider.toString() );
						 
						 Preference.setPropValues( Preference.PREFERENCE_FILE,
									WeatherLionMain.WEATHER_SOURCE_PREFERENCE,
									WeatherLionWidget.previousWeatherProvider.toString() );
						 
						 if( PreferenceForm.frmPreference != null )
						 {
							 if( PreferenceForm.frmPreference.isVisible() )
							 {
								 PreferenceForm.cboWeatherProviders.setSelectedItem( 
										 WeatherLionWidget.previousWeatherProvider.toString() );
							 }// end of if block
						 }// end of if block						 
					 }// end of catch block		
				 }// end of inner if block
				 else // no json data was returned so check for Internet connectivity
				 {
					// Check the Internet connection availability
			        if( !UtilityMethod.hasInternetConnection() )
			        {
			        	File previousWeatherData = new File( WeatherLionMain.DATA_DIRECTORY_PATH  +
			        			WeatherLionMain.WEATHER_DATA_XML );
			        	
			        	// check for previous weather data stored locally
			        	if( previousWeatherData.exists() ) 
			        	{
			        		loadPreviousWeatherData();
			        		WeatherLionMain.weatherLoadedFromProvider = false;
			        					        		
			        		if( !lblOffline.isVisible() )
			        		{
			        			JOptionPane.showMessageDialog( frmWeatherWidget, "No internet connection was detected so "
			        				+ "previous weather\ndata will be used until connection to the internet is restored.",
			        				"Internet Connection Error", JOptionPane.ERROR_MESSAGE );
			        			
			        			lblOffline.setVisible( true ); // display the offline icon on the widget
			        		}// end of if block
			        	}// end of if block
			        }// end of if block	
			        else // we are connected to the Internet so that means the issue lies with the weather source
			        {
//			        	int scheduleTime = 5000; // Sleep for five seconds
//			    		
//			    		// wait for five seconds and try the provider once more
//			    		while( WeatherLionWidget.running && !reAttempted )
//			    		{
//			    	 		new java.util.Timer().schedule( 
//			    		        new java.util.TimerTask()
//			    		        {
//			    		            @Override
//			    		            public void run()
//			    		            {
//			    		            	UtilityMethod.logMessage( "info", "Waiting to retry service provider...",
//					    						thisClass.getEnclosingClass().getSimpleName()+ "::run" );	
//					    				UtilityMethod.logMessage( "info", "Retrying service provider...",
//					    						thisClass.getEnclosingClass().getSimpleName() + "::run" );
//						    				
//					    				reAttempted = true;
//			    		            	
//			    		            	// run the weather service
//			    						widgetThread = new Thread( this ); 
//			    						widgetThread.start();
//			    		            }
//			    		        }, scheduleTime 
//			    			);
//			    		}// end of while block
			    		
//			    		if( usingPreviousData && reAttempted  )
			    		if( usingPreviousData )
			        	{			        		
			        		UtilityMethod.logMessage( "severe", "Data service responded with: " + 
			        			strJSON.toString(),	"Get Data Retry" );
			        		
			        		// return to the previous data service
			        		Preference.getSavedPreferences().setProvider( previousWeatherProvider.toString() );
			        		
			        		JOptionPane.showMessageDialog( frmWeatherWidget, WeatherLionMain.storedPreferences.getProvider()
			        				+ " seems to be non-responsive at the moment.\nRight click the widget and select"
			        				+ " another provider from the\npreferences dialog box.",
			        				"Weather Data Error", JOptionPane.WARNING_MESSAGE );
			        		
			        		PreferenceForm pf = new PreferenceForm( frmWeatherWidget  );
			        		pf.showForm();
			        	}// end of else block	
			        }// end of else block
				 }// end of inner else block
			 }// end of if block
			else 
			{
				updateTemps( true );
			}// end of else block
			 
			// start the icon thread after the first launch of this thread.
			if( !iconThread.isAlive() )
			{
				iconThread.start();
			}// end of if block
			 
	     }// end of overridden method done
		 
		 /***
		 * Yahoo! Developers Network 2019 documentation
		 * url: https://developer.yahoo.com/weather/documentation.html#java
		 * 
		 * @return A {@code String} representation of JSON data
		 * @throws Exception 
		 */
		 public static String getYahooWeatherData( String wxCity ) throws Exception
		 {
			 	wxCity = wxCity.replace( " ", "%2B" ).replace( ",", "%2C" ); // add URL Encoding for two characters 
		        final String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss";

		        long timestamp = new Date().getTime() / 1000;
		        byte[] nonce = new byte[ 32 ];
		        Random rand = new Random();
		        rand.nextBytes( nonce );
		        String oauthNonce = new String( nonce ).replaceAll( "\\W", "" );
		        
		        List<String> parameters = new ArrayList<>();
		        parameters.add( "oauth_consumer_key=" + yahooConsumerKey );
		        parameters.add( "oauth_nonce=" + oauthNonce );
		        parameters.add( "oauth_signature_method=HMAC-SHA1" );
		        parameters.add( "oauth_timestamp=" + timestamp );
		        parameters.add( "oauth_version=1.0" );
		        // Make sure value is encoded
		        parameters.add( "location=" + wxCity );
		        parameters.add( "format=json" );
		        Collections.sort( parameters );

		        StringBuffer parametersList = new StringBuffer();
		        
		        for ( int i = 0; i < parameters.size(); i++ )
		        {
		            parametersList.append( ( ( i > 0 ) ? "&" : "" ) + parameters.get( i ) );
		        }// end of for loop

		        String signatureString = "GET&" +
		            URLEncoder.encode( url, "UTF-8" ) + "&" +
		            URLEncoder.encode( parametersList.toString(), "UTF-8" );

		        String signature = null;
		        
		        try 
		        {
		            SecretKeySpec signingKey =
		            		new SecretKeySpec( ( yahooConsumerSecret + "&" ).getBytes(), "HmacSHA1" );
		            Mac mac = Mac.getInstance( "HmacSHA1" );
		            mac.init( signingKey );
		            byte[] rawHMAC = mac.doFinal( signatureString.getBytes() );
		            Encoder encoder = Base64.getEncoder();
		            signature = encoder.encodeToString( rawHMAC );
		        }// end of try block 
		        catch ( Exception e )
		        {
		            System.err.println( "Unable to append signature" );
		            return null;
		        }// end of catch block

		        String authorizationLine = "OAuth " +
		            "oauth_consumer_key=\"" + yahooConsumerKey + "\", " +
		            "oauth_nonce=\"" + oauthNonce + "\", " +
		            "oauth_timestamp=\"" + timestamp + "\", " +
		            "oauth_signature_method=\"HMAC-SHA1\", " +
		            "oauth_signature=\"" + signature + "\", " +
		            "oauth_version=\"1.0\"";

		        HttpClient client = HttpClient.newHttpClient();
		        
		        // The app id header of "Yahoo-App-Id" has been deprecated to "X-Yahoo-App-Id" 
		        HttpRequest request = HttpRequest.newBuilder()
		            .uri( URI.create( url + "?location=" + wxCity + "&format=json" ) )
		            .header( "Authorization", authorizationLine )
		            .header( "X-Yahoo-App-Id", yahooAppId )
		            .header( "Content-Type", "application/json" )
		            .build();
		        
		        HttpResponse<String> response = client.send( request, BodyHandlers.ofString() );
			 
		        return response.body();
		 }// end of method getYahooWeatherData
		 
		 /**
	     * Loads the applicable weather icon image an sizes it accordingly
	     * 
	     * @param component The {@code JLabel} in which the image will be displayed
	     * @param iconFile  The file name for the icon
	     * @param width  The new width of the image
	     * @param height  The new height of the image
	     */
	    public static void loadWeatherIcon( JLabel component, String iconFile, int width, int height )
	    {
	    	Icon img = null;
	    	
			try 
			{
				img = new ImageIcon( iconFile );
				component.setIcon( UtilityMethod.scaleImageIcon( img, width, height ) );	        
			}// end of try block 
			catch ( NullPointerException e )
			{
				UtilityMethod.logMessage( "severe","Weather icon " + 
					iconFile + " could not be loaded!",
					thisClass.getEnclosingClass().getSimpleName() + "::loadWeatherIcon" );
			}// end of catch block
	    	
	    }// end of method loadWeatherIcon
		 
	    private void loadDarkSkyWeather() throws Exception
	    {
	    	currentCity.setLength( 0 );
	    	currentCity.append( CityData.currentCityData.getCityName() );
	    	
	    	currentCountry.setLength( 0 );
	    	currentCountry.append( CityData.currentCityData.getCountryName() );
	    	
	    	currentCondition.setLength( 0 ); // reset
	    	currentCondition.append( UtilityMethod.toProperCase( darkSky.getCurrently().getSummary() ) );
	    	
	    	currentWindDirection.setLength( 0 );
	    	currentWindDirection.append( UtilityMethod.compassDirection(darkSky.getCurrently().getWindBearing() ) );
	    	
	    	currentHumidity.setLength( 0 );
	    	currentHumidity.append( String.valueOf( Math.round( darkSky.getCurrently().getHumidity() * 100 ) ) );
	    	
	    	sunriseTime.setLength( 0 );
	    	sunriseTime.append( new SimpleDateFormat( "h:mm a" ).format(
	    			UtilityMethod.getDateTime( darkSky.getDaily().getData().get( 0 ).getSunriseTime() ) )
	    				.toUpperCase() );
	    	
	    	sunsetTime.setLength( 0 );
	    	sunsetTime.append( new SimpleDateFormat( "h:mm a" ).format(
	    			UtilityMethod.getDateTime( darkSky.getDaily().getData().get( 0 ).getSunsetTime() ) ).
	    				toUpperCase() ); 
	    	
	        updateTemps( true ); // call update temps here
	        formatWeatherCondition();
	
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	
	        lblWindReading.setText( currentWindDirection +
                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ?
            		" km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity.toString() + "%" );
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );
	
	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( 
					sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
	        	UtilityMethod.logMessage( "severe" , e.getMessage(),
	        	    TAG + "::loadDarkSkyWeather [line: "
	        	    + e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get(currentCondition.toString().toLowerCase() + " (night)");
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey().startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block
	      
	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
	        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );
	
	        lblCurrentConditionImage.setToolTipText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	        	    		    	
	    	 // Five Day Forecast
            int i = 1;
            currentFiveDayForecast.clear(); // ensure that this list is clean
            
            for ( DarkSkyWeatherDataItem.Daily.Data wxForecast : darkSky.getDaily().getData() )
            {
				Date fxDate = UtilityMethod.getDateTime( wxForecast.getTime() );
				String fCondition = wxForecast.getSummary().toLowerCase();
				
				if ( fCondition.contains( "until" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "until" ) - 1 ).trim();
                }// end of if block

                if ( fCondition.contains( "starting" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "starting" ) - 1 ).trim();
                }// end of if block

                if ( fCondition.contains( "overnight" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "overnight" ) - 1 ).trim();
                }// end of if block
                
                if ( fCondition.contains( "throughout" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "throughout" ) - 1 ).trim();
                }// end of if block
                
                if ( fCondition.contains( " in " ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( " in " ) - 1 ).trim();
                }// end of if block
                
                if( fCondition.toLowerCase().contains( "and" ) )
    	        {
    	            String[] conditions = fCondition.toLowerCase().split( "and" );
    	                 	            
    	            fCondition = conditions[ 0 ].trim();
    	        }// end of if block

                fCondition = UtilityMethod.toProperCase( fCondition );
                
                ( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
            			new SimpleDateFormat( "E d" ).format( fxDate ) );
	
	            // Load current forecast condition weather image
	            if( fCondition.toLowerCase().contains( "(day)" ) )
	            {
	                fCondition = fCondition.replace( "(day)", "").trim();
	            }// end of if block
	            else if( fCondition.toLowerCase().contains( "(night)" ) )
	            {
	                fCondition = fCondition.replace( "(night)", "" ).trim();
	            }// end of if block
	
	            String fConditionIcon = null;
	           	            
	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
	            	    {
	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	fCondition = e.getKey();
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( fConditionIcon == null )
	            	{
	            		fConditionIcon = "na.png";
	            	}// end of if block	            	
	            }// end of if block 
	            else
	            {
	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
	            }// end of if block	        

	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ), WEATHER_IMAGE_PATH_PREFIX + 
            			WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
            	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( UtilityMethod.toProperCase( fCondition ) );
	            
	            currentFiveDayForecast.add(
	            		new FiveDayForecast( fxDate, String.valueOf( hl[ i - 1 ][ 0 ] ),
	            				String.valueOf( hl[ i - 1 ][ 1 ] ), fCondition ) );
                
                if ( i == 5 )
                {
                    break;
                }// end of if block

                i++; // increment sentinel
                
			}// end of for each loop
            
            // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
            
            wXML = new WeatherDataXMLService( WeatherLionMain.DARK_SKY, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(),
	        		currentTemp.toString().substring( 0, currentTemp.toString().indexOf( DEGREES ) ).trim(),
	        		currentFeelsLikeTemp.toString(), currentHigh.toString(), currentLow.toString(),
	        		currentWindSpeed.toString(), currentWindDirection.toString(), currentHumidity.toString(),
	        		sunriseTime.toString(),	sunsetTime.toString(), currentFiveDayForecast );	
	        
	        wXML.execute();
	    	
	    }// end of method loadDarkSkyWeather
	    
	    private void loadHereMapsWeather() throws Exception 
	    {
	    	HereMapsWeatherDataItem.WeatherData.Observations.Location.Observation obs = hereWeatherWx.getObservations().getLocation().get( 0 )
	    			.getObservation().get( 0 );
	    	HereMapsWeatherDataItem.AstronomyData.Astronomic.Astronomy ast = hereWeatherAx.getAstronomy().getAstronomy().get( 0 );
	    	
	    	currentCity.setLength( 0 );
	    	currentCity.append( CityData.currentCityData.getCityName() );
	    	
	    	currentCountry.setLength( 0 );
	    	currentCountry.append( CityData.currentCityData.getCountryName() );
	    	
	    	currentCondition.setLength( 0 );
	    	currentCondition.append( obs.getIconName().contains( "_" ) ?
	    			UtilityMethod.toProperCase( obs.getIconName().replaceAll( "_", " " ) ) :
	    				UtilityMethod.toProperCase( obs.getIconName().replaceAll( "_", " " ) ) );
	    			
	    	currentWindDirection.setLength( 0 );
	    	currentWindDirection.append( obs.getWindDescShort() );
	    	
	    	currentWindSpeed.setLength( 0 );
	    	currentWindSpeed.append( String.valueOf( obs.getWindSpeed() ) );
	    	
	    	currentHumidity.setLength( 0 );
	    	currentHumidity.append( String.valueOf( Math.round( obs.getHumidity() ) ) );
	    	
	    	sunriseTime.setLength( 0 );
	    	sunriseTime.append( ast.getSunrise().toUpperCase() );
	    	
	    	sunsetTime.setLength( 0 );
	    	sunsetTime.append( ast.getSunset().toUpperCase() );
	    	List< HereMapsWeatherDataItem.ForecastData.DailyForecasts.ForecastLocation.Forecast > fdf =
	    			hereWeatherFx.getDailyForecasts().getForecastLocation().getForecast();
	    	
	    	updateTemps( true ); // call update temps here
	        formatWeatherCondition();
	        
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	
	        lblWindReading.setText( currentWindDirection +
                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ? 
            		" km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity.toString() + "%" );
	
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString());
	
	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
    			rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
	        	UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadHereMapsWeather [line: " 
	        	    + e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = 
                		UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( 
                		currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon =
                    		UtilityMethod.weatherImages.get( 
                				currentCondition.toString().toLowerCase() + " (night)" );
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( 
            		currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey().startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get( 
        				currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block

	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
	        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );
	
	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        // Five Day Forecast
	        int i = 1;
	        Date lastDate = new Date();
	        SimpleDateFormat df = new SimpleDateFormat( "MMM dd, yyyy" );
	        currentFiveDayForecast.clear(); // ensure that this list is clean
	        
	        // loop through the forecast data. only 5 days are needed
            for ( HereMapsWeatherDataItem.ForecastData.DailyForecasts.ForecastLocation.Forecast wxForecast : fdf )
            {
            	df = new SimpleDateFormat( "yyyy-MM-dd" );
            	Date fxDate = null;
				
            	try
            	{
					fxDate = df.parse( wxForecast.getUtcTime().substring( 0, 10 ) );
				}// end of try block
            	catch ( ParseException pe )
            	{
					UtilityMethod.logMessage( "severe" , pe.getMessage(),
						TAG + "::loadHereMapsWeather [line: " +
						pe.getStackTrace()[ 1 ].getLineNumber() + "]" );
				}// end of catch block            	            	
            	
            	if ( !df.format( fxDate ).equals( df.format( lastDate ) ) )
                {
            		lastDate = fxDate;
            		            		
            		String fCondition =wxForecast.getIconName().contains( "_" ) ?
    	    			UtilityMethod.toProperCase( wxForecast.getIconName().replaceAll( "_", " " ) ) :
    	    				UtilityMethod.toProperCase( wxForecast.getIconName().replaceAll( "_", " " ) );
            		String fDay =  new SimpleDateFormat( "E d" ).format( fxDate );
            		
            		if ( fCondition.contains( "until" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "until" ) - 1 ).trim();
                    }// end of if block

                    if ( fCondition.contains( "starting" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "starting" ) - 1 ).trim();
                    }// end of if block

                    if ( fCondition.contains( "overnight" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "overnight" ) - 1 ).trim();
                    }// end of if block
                    
                    if ( fCondition.contains( "throughout" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "throughout" ) - 1 ).trim();
                    }// end of if block
                    
                    if ( fCondition.contains( "in " ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "in " ) - 1 ).trim();
                    }// end of if block
                    
                    if ( fCondition.contains( "is " ) )
                    {
                        fCondition = fCondition.substring( fCondition.indexOf( "is " ) + 3,
                    		fCondition.length() ).trim();
                    }// end of if block
                    
                    if( fCondition.toLowerCase().contains( "and" ) )
        	        {
        	            String[] conditions = fCondition.toLowerCase().split( "and" );
        	                 	            
        	            fCondition = conditions[ 0 ].trim();
        	        }// end of if block
                    
                    fCondition = UtilityMethod.toProperCase( fCondition );
                	
                    ( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
                			new SimpleDateFormat( "E d" ).format( fDay ) );
    	
    	            // Load current forecast condition weather image
    	            if( fCondition.toLowerCase().contains( "(day)") )
    	            {
    	                fCondition = fCondition.replace( "(day)", "").trim();
    	            }// end of if block
    	            else if( fCondition.toLowerCase().contains( "(night)" ) )
    	            {
    	                fCondition = fCondition.replace( "(night)", "" ).trim();
    	            }// end of if block
    	
    	            String fConditionIcon = null;
       	            
    	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
    	            {
    	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
    	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
    	            	{
    	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
    	            	    {
    	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
    	            	    	fCondition = e.getKey();
    	            	    	break; // exit the loop
    	            	    }// end of if block
    	            	}// end of for block
    	            	
    	            	// if a match still could not be found, use the not available icon
    	            	if( fConditionIcon == null )
    	            	{
    	            		fConditionIcon = "na.png";
    	            	}// end of if block	            	
    	            }// end of if block 
    	            else
    	            {
    	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
    	            }// end of if block
    	            
    	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ), WEATHER_IMAGE_PATH_PREFIX + 
                			WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
                	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( UtilityMethod.toProperCase( fCondition ) );
    	            
    	            currentFiveDayForecast.add(
    	            		new FiveDayForecast( fxDate, String.valueOf( hl[ i - 1 ][ 0 ] ),
    	            				String.valueOf( hl[ i - 1 ][ 1 ] ), fCondition ) );
            		
            		if ( i == 5 )
                    {
                        break;
                    }// end of if block

                    i++;
                }// end of if block
            }// end of for each loop
            
            // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
            
            wXML = new WeatherDataXMLService( WeatherLionMain.OPEN_WEATHER, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
	        		currentTemp.toString().substring( 0, currentTemp.toString().indexOf( DEGREES ) ).trim(),
	        		currentFeelsLikeTemp.toString(), currentHigh.toString(), currentLow.toString(),
	        		currentWindSpeed.toString(), currentWindDirection.toString(), currentHumidity.toString(),
	        		sunriseTime.toString(), sunsetTime.toString(), currentFiveDayForecast );	
	        
	        wXML.execute();
	    }// end of method loadHereMapsWeather
	    
	    private void loadOpenWeather() throws Exception
	    {
	    	currentCity.setLength( 0 );
	    	currentCity.append( CityData.currentCityData.getCityName() );
	    	
	    	currentCountry.setLength( 0 );
	    	currentCountry.append( CityData.currentCityData.getCountryName() );
	    	
	    	currentCondition.setLength( 0 ); // reset
	    	currentCondition.append( openWeatherWx.getWeather().get( 0 ).getDescription() );
	    	
	    	currentWindDirection.setLength( 0 ); // reset
	    	currentWindDirection.append( UtilityMethod.compassDirection( openWeatherWx.getWind().getDeg() ) );
	    	
	    	currentHumidity.setLength( 0 );
	    	currentHumidity.append( String.valueOf( Math.round( openWeatherWx.getMain().getHumidity() ) ) );
	    	
	    	sunriseTime.setLength( 0 );
	    	sunriseTime.append( new SimpleDateFormat( "h:mm a" ).format(
	    			UtilityMethod.getDateTime( openWeatherWx.getSys().getSunrise() ) ).toUpperCase() );
	    	
	    	sunsetTime.setLength( 0 );
	    	sunsetTime.append( new SimpleDateFormat( "h:mm a" ).format(
	    			UtilityMethod.getDateTime( openWeatherWx.getSys().getSunset() ) ).toUpperCase() );
	    	List< OpenWeatherMapWeatherDataItem.ForecastData.Data > fdf = openWeatherFx.getList();
	    	
	        updateTemps( true ); // call update temps here
	        formatWeatherCondition();
	        
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	
	        lblWindReading.setText( currentWindDirection +
	                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ? " km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity.toString() + "%" );
	
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );
	
	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
	        	UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadOpenWeather [line: " +
					e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey(
                		currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() + " (night)");
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get( 
    					currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block

	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
	        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );
	
	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        // Five Day Forecast
	        int i = 1;
	        Date lastDate = new Date();
	        SimpleDateFormat df = new SimpleDateFormat( "MMM dd, yyyy" );
	        currentFiveDayForecast.clear(); // ensure that this list is clean
	        
	        // loop through the forecast data. only 5 days are needed
            for ( OpenWeatherMapWeatherDataItem.ForecastData.Data wxForecast : fdf )
            {
            	Date fxDate = UtilityMethod.getDateTime( wxForecast.getDt() );            	            	
            	
            	if ( !df.format( fxDate ).equals( df.format( lastDate ) ) )
                {
            		lastDate = UtilityMethod.getDateTime(wxForecast.getDt() );
            		String fCondition = wxForecast.getWeather().get( 0 ).getDescription();
            		String fDay =  new SimpleDateFormat( "E d" ).format( fxDate );
            		
            		if ( fCondition.contains( "until" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "until" ) - 1 ).trim();
                    }// end of if block

                    if ( fCondition.contains( "starting" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "starting" ) - 1 ).trim();
                    }// end of if block

                    if ( fCondition.contains( "overnight" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "overnight" ) - 1 ).trim();
                    }// end of if block
                    
                    if ( fCondition.contains( "throughout" ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "throughout" ) - 1 ).trim();
                    }// end of if block
                    
                    if ( fCondition.contains( "in " ) )
                    {
                        fCondition = fCondition.substring( 0, fCondition.indexOf( "in " ) - 1 ).trim();
                    }// end of if block
                    
                    if ( fCondition.contains( "is " ) )
                    {
                        fCondition = fCondition.substring( fCondition.indexOf( "is " ) + 3,
                    		fCondition.length() ).trim();
                    }// end of if block
                    
                    if( fCondition.toLowerCase().contains( "and" ) )
        	        {
        	            String[] conditions = fCondition.toLowerCase().split( "and" );
        	                 	            
        	            fCondition = conditions[ 0 ].trim();
        	        }// end of if block
                    
                    fCondition = UtilityMethod.toProperCase( fCondition );
                	
                    ( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
                			new SimpleDateFormat( "E d" ).format( fDay ) );
    	
    	            // Load current forecast condition weather image
    	            if( fCondition.toLowerCase().contains( "(day)") )
    	            {
    	                fCondition = fCondition.replace( "(day)", "").trim();
    	            }// end of if block
    	            else if( fCondition.toLowerCase().contains( "(night)" ) )
    	            {
    	                fCondition = fCondition.replace( "(night)", "" ).trim();
    	            }// end of if block
    	
    	            String fConditionIcon = null;
       	            
    	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
    	            {
    	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
    	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
    	            	{
    	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
    	            	    {
    	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
    	            	    	fCondition = e.getKey();
    	            	    	break; // exit the loop
    	            	    }// end of if block
    	            	}// end of for block
    	            	
    	            	// if a match still could not be found, use the not available icon
    	            	if( fConditionIcon == null )
    	            	{
    	            		fConditionIcon = "na.png";
    	            	}// end of if block	            	
    	            }// end of if block 
    	            else
    	            {
    	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
    	            }// end of if block
    	            
    	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ), WEATHER_IMAGE_PATH_PREFIX + 
                			WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
                	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( UtilityMethod.toProperCase( fCondition ) );
    	            
    	            currentFiveDayForecast.add(
    	            		new FiveDayForecast( fxDate, String.valueOf( hl[ i - 1 ][ 0 ] ),
    	            				String.valueOf( hl[ i - 1 ][ 1 ] ), fCondition ) );
            		
            		if ( i == 5 )
                    {
                        break;
                    }// end of if block

                    i++;
                }// end of if block
            }// end of for each loop
            
            // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
            
            wXML = new WeatherDataXMLService( WeatherLionMain.OPEN_WEATHER, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
	        		currentTemp.toString().substring( 0, currentTemp.toString().indexOf( DEGREES ) ).trim(),
	        		currentFeelsLikeTemp.toString(), currentHigh.toString(), currentLow.toString(),
	        		currentWindSpeed.toString(), currentWindDirection.toString(), currentHumidity.toString(),
	        		sunriseTime.toString(), sunsetTime.toString(), currentFiveDayForecast );	
	        
	        wXML.execute();
	    }// end of method loadOpenWeather
	    
	    /**
	     * Load Weather previously received from provider
	     */
	    private void loadPreviousWeatherData()
	    {
	    	File previousWeatherData = new File( 
    			WeatherLionMain.DATA_DIRECTORY_PATH + WeatherLionMain.WEATHER_DATA_XML );
	    	Element xmlProvider = null;
	    	Element xmlAstronomy;
	    	Element xmlLocation;
	    	Element xmlForecastData = null;
	    				
			// check for previous weather data stored locally
			if( previousWeatherData.exists() ) 
			{
				SAXBuilder builder = new SAXBuilder();
								
				try 
		    	{
		    		// just in case the document contains unnecessary white spaces
		    		builder.setIgnoringElementContentWhitespace( true );
		    		
		    		// download the document from the URL and build it
		    		weatherXML = builder.build( previousWeatherData );
		    		
		    		// get the root node of the XML document
		    		rootNode = weatherXML.getRootElement();
		    		
		    		// get the root node of the XML document
		    		xmlProvider = rootNode.getChild( "Provider" );
		    		xmlLocation = rootNode.getChild( "Location" );
		    		xmlAtmosphere = rootNode.getChild( "Atmosphere" );
		    		xmlWind = rootNode.getChild( "Wind" );
		    		xmlAstronomy = rootNode.getChild( "Astronomy" );
		    		xmlCurrent = rootNode.getChild( "Current" );		    		
		    		xmlForecast = rootNode.getChild( "DailyForecast" );
		    		xmlForecastList = rootNode.getChild( "DailyForecast" ).getChildren( "DayForecast" );
		    			
		            currentCity.setLength( 0 );
		            currentCity.append( xmlLocation.getChildText( "City" ) );
		            
			        currentCountry.setLength( 0 );
			        currentCountry.append( xmlLocation.getChildText( "Country" ) );
			        
			        currentCondition.setLength( 0 ); // reset
			        currentCondition.append( xmlCurrent.getChildText( "Condition" ) ); 
			        
			        currentWindDirection.setLength( 0 ); // reset
			        currentWindDirection.append( xmlWind.getChildText( "WindDirection" ) );
			        
			        currentWindSpeed.setLength( 0 );
			        currentWindSpeed.append( xmlWind.getChildText( "WindSpeed" ) );
			        
			        currentHumidity.setLength( 0 );
			        currentHumidity.append( xmlAtmosphere.getChildText( "Humidity" ) );
			        
			        currentLocation = currentCity;
			        
			        sunriseTime.setLength( 0 );
			        sunriseTime.append( xmlAstronomy.getChildText( "Sunrise" ).toUpperCase() );
			        
			        sunsetTime.setLength( 0 );
			        sunsetTime.append( xmlAstronomy.getChildText( "Sunset" ).toUpperCase() );
		    	}// end of try block 
		    	catch ( IOException io )
		    	{
					UtilityMethod.logMessage( "severe", io.getMessage(), 
						thisClass.getEnclosingClass().getSimpleName() + "::loadPreviousWeather" );
		    	}// end of catch block 
		    	catch ( JDOMException jdomex )
		    	{
					UtilityMethod.logMessage( "severe", jdomex.getMessage(), 
						thisClass.getEnclosingClass().getSimpleName() + "::loadPreviousWeather" );
		    	}// end of catch block
				
			}// end of if block    	
	        
			updateTemps( false ); // call update temps here
	        formatWeatherCondition();
	        	       
	        // Some providers like Yahoo! loves to omit a zero on the hour mark example: 7:0 am
	        if( sunriseTime.length() == 6 )
	        {
	            String[] ft = sunriseTime.toString().split( ":" );
	            sunriseTime.setLength( 0 );
	            sunriseTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end of if block
	        else if( sunsetTime.length() == 6 )
	        {
	            String[] ft= sunsetTime.toString().split( ":" );
	            sunsetTime.setLength( 0 );
	            sunsetTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end if else if block

	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	        lblWindReading.setText( currentWindDirection +
                " " + Math.round( Float.parseFloat( currentWindSpeed.toString() ) ) +
                ( WeatherLionMain.storedPreferences.getUseMetric() ? " km/h" : " mph" ) );
	        
	        // Yr's Weather Service does not track humidity
	        if( currentHumidity.toString().length() == 0 ) currentHumidity.append( "0" );
	        	        
	        currentHumidity = currentHumidity.toString().contains( "%" )
	        		? new StringBuilder( currentHumidity.toString().replaceAll( "%", "" ) )
	        		: currentHumidity; // remove before parsing
	        lblHumidity.setText( Math.round( Float.parseFloat( currentHumidity.toString() ) )
	        		+ ( !currentHumidity.toString().contains( "%" ) ? "%" : "" ) );
	        
	        Date timeUpdated = null;
        	
			try
			{
				timeUpdated = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z yyyy" ).parse(
					xmlProvider.getChildText( "Date" ) );
			}// end of try block
			catch ( ParseException e )
			{
				UtilityMethod.logMessage( "severe" , e.getMessage(),
						TAG + "::loadPreviousWeather [line: " + e.getStackTrace()[1].getLineNumber()+ "]" );
			}// end of catch block
			
	        // Update the current location and update time stamp
			String ts = currentLocation.toString().contains( "," ) 
					? currentLocation.substring( 0, currentLocation.indexOf( "," ) ) 
						+ ", " + new SimpleDateFormat( "E h:mm a" ).format( timeUpdated )
					: currentLocation + ", " 
						+ new SimpleDateFormat( "E h:mm a" ).format( timeUpdated );
						
			lblLocation.setText( ts );
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );      
	       
	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        String twenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( twenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( twenty4HourTime.split( ":" )[ 1 ].trim() ) );
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
				UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadPreviousWeather [line: " + e.getStackTrace()[1].getLineNumber()+ "]" );
	        }// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() + " (night)" );
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	        }// end of if block
	        else
	        {
	            currentConditionIcon = UtilityMethod.weatherImages.get( 
            		currentCondition.toString().toLowerCase() );
	        }// end of else block
	        
	        currentConditionIcon =  UtilityMethod.weatherImages.get( 
        		currentCondition.toString().toLowerCase() ) == null ? 
            		"na.png" :
            		currentConditionIcon;
	        
	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
	        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );

	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        SimpleDateFormat df = new SimpleDateFormat( "MMMM dd, yyyy" );
	        	        
	        int x = 0;
	        	        
	        for ( int i = 0; i < xmlForecastList.size(); i++ )
			{
	            x++;				
				Element wxDailyForecast = xmlForecastList.get( i );                	
            	Date forecastDate = null;
            	
				try
				{
					forecastDate = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z yyyy" ).parse( wxDailyForecast.getChildText( "Date" ) );
				}// end of try block
				catch ( ParseException e )
				{
					UtilityMethod.logMessage( "severe", "Couldn't parse the forcecast date!",
						thisClass.getEnclosingClass().getSimpleName() + "::loadPreviousWeather"  );
				}// end of catch block
				
				( (JLabel) getComponentByName( String.format("lblDay%dDay", i + 1 ) ) ).setText(
            			new SimpleDateFormat( "E d" ).format( forecastDate ) );
            	
            	// Load current forecast condition weather image
            	String fCondition =   wxDailyForecast.getChildText( "Condition" );
            	
            	if( fCondition.toLowerCase().contains( "(day)" ) )
            	{
            		fCondition = fCondition.replace( "(day)", "" ).trim();
            	}// end of if block
            	else if( fCondition.toLowerCase().contains( "(night)" ) )
            	{
            		fCondition = fCondition.replace( "(night)", "" ).trim();
            	}// end of if block
            	
            	String fConditionIcon 
            		= UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null 
            			? "na.png" : UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
            	
            	loadWeatherIcon( ( (JLabel) getComponentByName( String.format("lblDay%dImage", i + 1 ) ) ),
        			WEATHER_IMAGE_PATH_PREFIX + WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
            	( (JLabel) getComponentByName( String.format("lblDay%dImage", i + 1 ) ) ).setToolTipText( 
            			UtilityMethod.toProperCase( fCondition ) );
            	            	
            	currentFiveDayForecast.add(
            			new FiveDayForecast( forecastDate,
            					wxDailyForecast.getChildText( "HighTemperature" ),
            					wxDailyForecast.getChildText( "Low" ),
            					fCondition ) );            	
            	                	
            	if( i == 4 )
            	{
            		break;
            	}// end of if block            	
			}// end of for loop

			 // Update the weather provider
			 lblWeatherProvider.setText( xmlProvider.getChildText( "Name" ) );
			 String providerIcon = "res/assets/img/icons/" + xmlProvider.getChildText( "Name" ).toLowerCase() + ".png";
			 
			 lblWeatherProvider.setIcon( 
				new ImageIcon( providerIcon ) );
			 
			 if( UtilityMethod.refreshRequested )
			 {
				 UtilityMethod.refreshRequested = false;
			 }// end of if block
			 
			 if( !frmWeatherWidget.isVisible() )
			 {
				 frmWeatherWidget.setVisible( true );				 
			 }// end of if block
	       
			 usingPreviousData = true; // indicate that old weather data is being used
	    }// end of method loadPreviousWeatherData
	    
	    private void loadWeatherBitWeather() throws Exception
	    {
	    	currentCity.setLength( 0 );
	    	currentCity.append( CityData.currentCityData.getCityName() );
	    	
	    	currentCountry.setLength( 0 );
	    	currentCountry.append( CityData.currentCityData.getCountryName() );
	    	
	    	currentCondition.setLength( 0 ); // reset
	    	currentCondition.append( UtilityMethod.toProperCase( 
	    			weatherBitWx.getData().get( 0 ).getWeather().getDescription() ) );
	    	
	    	currentWindDirection.setLength( 0 );
	    	currentWindDirection.append( weatherBitWx.getData().get( 0 ).getWind_cdir() );
	    	
	    	currentHumidity.setLength( 0 );
	    	currentHumidity.append( String.valueOf( Math.round( weatherBitWx.getData().get( 0 ).getRh() ) ) );
	    	
	    	// Weather seems to be in a time-zone that is four hours ahead of Eastern Standard Time
            // They do not supply that information though.
            int tzOffset = 5;
            
            sunriseTime.setLength( 0 );
	    	sunriseTime.append( UtilityMethod.get12HourTime( 
    			Integer.parseInt( weatherBitWx.getData().get( 0 ).getSunrise().split( ":" )[ 0 ] )
    				- tzOffset, Integer.parseInt( 
						weatherBitWx.getData().get( 0 ).getSunrise().split( ":" )[ 1 ] ) ) );
	    	
	    	sunsetTime.setLength( 0 );
	    	sunsetTime.append( UtilityMethod.get12HourTime( 
    			Integer.parseInt( weatherBitWx.getData().get( 0 ).getSunset().split( ":" )[ 0 ] )
    				- tzOffset, Integer.parseInt( 
						weatherBitWx.getData().get( 0 ).getSunset().split( ":" )[ 1 ] ) ) );
	    	
	    	List< WeatherBitWeatherDataItem.SixteenDayForecastData.Data > fdf = weatherBitFx.getData();
	    	
	    	// call update temps here
	        updateTemps( true );
	        
	        formatWeatherCondition();
	        
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	        lblWindReading.setText( currentWindDirection +
                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() 
            		? " km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity.toString() + "%" );
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );
	
	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
	        	UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadWeatherBitWeather [line: " +
					e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get( 
                		currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() + " (night)" );
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block
	        
	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
	        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );
	
	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        // Five Day Forecast
	        int currentHour = Integer.parseInt( new SimpleDateFormat( "h" ).format( new Date() ) );
            int i = 1;
            currentFiveDayForecast.clear(); // ensure that this list is clean
	        
	        // loop through the 16 day forecast data. only 5 days are needed
            for ( WeatherBitWeatherDataItem.SixteenDayForecastData.Data wxForecast : fdf )
            {
            	Date fxDate = null;
	        	SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
	        	String dt = wxForecast.getDatetime();
	        	
	        	try 
	        	{
	        		fxDate = df.parse( dt );
				}// end of try block
	        	catch ( ParseException e )
	        	{
	        		UtilityMethod.logMessage( "severe", "Unable to parse date string!", 
        				thisClass.getEnclosingClass().getSimpleName() + "::loadWeatherBitWeather" );
	        		UtilityMethod.logMessage( "severe", e.getMessage(),
        				thisClass.getEnclosingClass().getSimpleName() + "::loadWeatherBitWeather");
				}// end of catch block          	
	            
	            if ( fxDate.after( new Date() ) )
                {
                    String fCondition = wxForecast.getWeather().getDescription();
        	            
                	if ( fxDate.after( new Date() ) )
                    {
                		if ( fCondition.contains( "until" ) )
                        {
                            fCondition = fCondition.substring( 0, fCondition.indexOf( "until" ) - 1 ).trim();
                        }// end of if block

                        if ( fCondition.contains( "starting" ) )
                        {
                            fCondition = fCondition.substring( 0, fCondition.indexOf( "starting" ) - 1 ).trim();
                        }// end of if block

                        if ( fCondition.contains( "overnight" ) )
                        {
                            fCondition = fCondition.substring( 0, fCondition.indexOf( "overnight" ) - 1 ).trim();
                        }// end of if block
                        
                        if ( fCondition.contains( "throughout" ) )
                        {
                            fCondition = fCondition.substring( 0, fCondition.indexOf( "throughout" ) - 1 ).trim();
                        }// end of if block
                        
                        if ( fCondition.contains( "in " ) )
                        {
                            fCondition = fCondition.substring( 0, fCondition.indexOf( "in " ) - 1 ).trim();
                        }// end of if block
                        
                        if ( fCondition.contains( "is " ) )
                        {
                            fCondition = fCondition.substring( fCondition.indexOf( "is " ) + 3, fCondition.length() ).trim();
                        }// end of if block
                        
                        if( fCondition.toLowerCase().contains( "and" ) )
            	        {
            	            String[] conditions = fCondition.toLowerCase().split( "and" );
            	                 	            
            	            fCondition = conditions[ 0 ].trim();
            	        }// end of if block
                        
                        fCondition = UtilityMethod.toProperCase( fCondition );
                    	
                        ( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
                    			new SimpleDateFormat( "E d" ).format( fxDate ) );
        	
        	            // Load current forecast condition weather image
        	            if(fCondition.toLowerCase().contains( "(day)" ) )
        	            {
        	                fCondition = fCondition.replace( "(day)", "").trim();
        	            }// end of if block
        	            else if(fCondition.toLowerCase().contains( "(night)" ) )
        	            {
        	                fCondition = fCondition.replace( "(night)", "" ).trim();
        	            }// end of if block
        	
        	            String fConditionIcon = null;
           	            
        	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
        	            {
        	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
        	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
        	            	{
        	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
        	            	    {
        	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
        	            	    	fCondition = e.getKey();
        	            	    	break; // exit the loop
        	            	    }// end of if block
        	            	}// end of for block
        	            	
        	            	// if a match still could not be found, use the not available icon
        	            	if( fConditionIcon == null )
        	            	{
        	            		fConditionIcon = "na.png";
        	            	}// end of if block	            	
        	            }// end of if block 
        	            else
        	            {
        	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
        	            }// end of if block
        	            
        	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ), 
    	            		WEATHER_IMAGE_PATH_PREFIX +	WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
                    	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( 
                    		UtilityMethod.toProperCase( fCondition ) );
        	            
        	            currentFiveDayForecast.add(
        	            		new FiveDayForecast( fxDate, String.valueOf( hl[ i - 1 ][ 0 ] ),
        	            				String.valueOf( hl[ i - 1 ][ 1 ] ), fCondition ) );
		            
        	            i++; // increment sentinel
                    	
                    	if( i == 6 ) 
                    	{
                    		break;
                    	}// end of if block
                    }// end of if block
                }// end of if block
            }// end of for each loop
            
            String ct = null;
            
            // sometimes weather bit includes symbols in their data
            if( currentTemp.toString().contains( DEGREES )  )
            {
            	ct = currentTemp.substring( 0, currentTemp.indexOf( DEGREES ) ).trim();
            }// end of if block
            else
            {
            	ct = currentTemp.toString();
            }// end of else block
            
            // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
            
            wXML = new WeatherDataXMLService( WeatherLionMain.WEATHER_BIT, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
	        		ct, currentFeelsLikeTemp.toString(), currentHigh.toString(),
	        		currentLow.toString(), currentWindSpeed.toString(), currentWindDirection.toString(),
	        		currentHumidity.toString(), sunriseTime.toString(), sunsetTime.toString(),
	        		currentFiveDayForecast );	
	        
	        wXML.execute();
	    }// end of method loadWeatherBitWeather
	    
		 /**
	     * Load Weather Underground Weather data
	     */
	    @Deprecated
	    private void loadWeatherUndergroundWeather() throws Exception
	    {
	        currentCity.setLength( 0 );
	        currentCity.append( underground.getCurrent_observation().getDisplay_location().getFull() );
	        
	        currentCountry.setLength( 0 );
	        currentCountry.append( underground.getCurrent_observation().getDisplay_location().getCountry() );
	        
	        currentCondition.setLength( 0 ); // reset
	        currentCondition.append( UtilityMethod.toProperCase( underground.getCurrent_observation().getWeather() ) );
	        
	        currentWindDirection.setLength( 0 );
	        currentWindDirection.append( underground.getCurrent_observation().getWind_dir() );
	        
	        currentHumidity.setLength( 0 );
	        currentHumidity.append( underground.getCurrent_observation().getRelative_humidity() );
	        
	        currentLocation = currentCity;
	        
	        sunriseTime.setLength( 0 );
	        sunriseTime.append( UtilityMethod.get12HourTime( underground.getSun_phase().getSunrise().getHour(),
	                underground.getSun_phase().getSunrise().getMinute() ) );
	        
	        sunsetTime.setLength( 0 );
	        sunsetTime.append( UtilityMethod.get12HourTime( underground.getSun_phase().getSunset().getHour(),
	                underground.getSun_phase().getSunset().getMinute() ) );
	
	        List< WeatherUndergroundDataItem.Forecast.SimpleForecast.ForecastDay > fdf =
	                underground.getForecast().getSimpleforecast().getForecastday();
		
	        updateTemps( true ); // call update temps here
	        formatWeatherCondition();
	        
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	
	        switch ( currentWindDirection.toString().toLowerCase() )
	        {
				case "north":
					currentWindDirection.setLength( 0 );
					currentWindDirection.append( "N" );
					break;
				case "south":
					currentWindDirection.setLength( 0 );
					currentWindDirection.append( "S" );
					break;
				case "east":
					currentWindDirection.setLength( 0 );
					currentWindDirection.append( "E" );
					break;
				case "west":
					currentWindDirection.setLength( 0 );
					currentWindDirection.append( "W" );
					break;
			default:
				break;
			}// end of switch case	        
	        
	        lblWindReading.setText( currentWindDirection +
                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() 
            		? " km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity.toString() + "%" );
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );
	
	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
				UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadWeatherUndergroundWeather [line: " +
					e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get(
                		currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( 
                		currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() + " (night)" );
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get( 
        				currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block

	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );
	
	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        // Five Day Forecast
	        int i = 1;
	        Calendar c = Calendar.getInstance();
	        currentFiveDayForecast.clear(); // ensure that this list is clean
	
	        // loop through the forecast data. only 5 days are needed
	        for ( WeatherUndergroundDataItem.Forecast.SimpleForecast.ForecastDay wxForecast : fdf )
	        {
	        	int period = wxForecast.getPeriod();
	
	            c.set( wxForecast.getDate().getYear(),
	                    wxForecast.getDate().getMonth() - 1,
	                    wxForecast.getDate().getDay() );
	
	            Date fxDate = c.getTime();
	
	            String fCondition = wxForecast.getConditions().toLowerCase();
	
	            if ( fCondition.contains( "until" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "until" ) - 1 ).trim();
                }// end of if block

                if ( fCondition.contains( "starting" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "starting" ) - 1 ).trim();
                }// end of if block

                if ( fCondition.contains( "overnight" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "overnight" ) - 1 ).trim();
                }// end of if block
                
                if ( fCondition.contains( "throughout" ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "throughout" ) - 1 ).trim();
                }// end of if block
                
                if ( fCondition.contains( "in " ) )
                {
                    fCondition = fCondition.substring( 0, fCondition.indexOf( "in " ) - 1 ).trim();
                }// end of if block
	
                if( fCondition.toLowerCase().contains( "and" ) )
    	        {
    	            String[] conditions = fCondition.toLowerCase().split( "and" );
    	                 	            
    	            fCondition = conditions[ 0 ].trim();
    	        }// end of if block
                
	            fCondition = UtilityMethod.toProperCase( fCondition );
	
	            ( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
            			new SimpleDateFormat( "E d" ).format( fxDate ) );
	
	            // Load current forecast condition weather image
	            if( fCondition.toLowerCase().contains( "(day)" ) )
	            {
	                fCondition = fCondition.replace( "(day)", "" ).trim();
	            }// end of if block
	            else if( fCondition.toLowerCase().contains( "(night)" ) )
	            {
	                fCondition = fCondition.replace( "(night)", "" ).trim();
	            }// end of if block
	
	            String fConditionIcon = null;
   	            
	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
	            	    {
	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	fCondition = e.getKey();
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( fConditionIcon == null )
	            	{
	            		fConditionIcon = "na.png";
	            	}// end of if block	            	
	            }// end of if block 
	            else
	            {
	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
	            }// end of if block
	            
	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ), 
            		WEATHER_IMAGE_PATH_PREFIX +	WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
            	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( UtilityMethod.toProperCase( fCondition ) );
	            
	            currentFiveDayForecast.add(
	            		new FiveDayForecast( fxDate, String.valueOf( hl[ i - 1 ][ 0 ] ),
	            				String.valueOf( hl[ i - 1 ][ 1 ] ), fCondition ) );
	
	            if ( period == 5 )
	            {
	                break;
	            }// end of if block
	
	            i++; // increment sentinel
	        }// end of for each loop
	        
	        // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
	        
	        wXML = new WeatherDataXMLService( WeatherLionMain.WEATHER_UNDERGROUND, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
	        		currentTemp.toString().substring( 0, currentTemp.toString().indexOf( DEGREES ) ).trim(),
	        		currentFeelsLikeTemp.toString(), currentHigh.toString(), currentLow.toString(),
	        		currentWindSpeed.toString(), currentWindDirection.toString(), currentHumidity.toString(),
	        		sunriseTime.toString(), sunsetTime.toString(), currentFiveDayForecast );	
	        
	        wXML.execute();
	    }// end of method loadWeatherUndergroundWeather
	    
	    /**
	     * Load Yahoo! Weather data
	     */
	    private void loadYahooYdnWeather() throws Exception
	    {
	    	currentCity.setLength( 0 );
	        currentCity.append( yahoo19.getLocation().getCity() +
	                ", " + yahoo19.getLocation().getRegion() );
	        
	        currentCountry.setLength( 0 ); // reset
	        currentCountry.append( yahoo19.getLocation().getCountry() );

	        currentCondition.setLength( 0 ); // reset
	        currentCondition.append( 
	                yahoo19.getCurrentObservation().getCondition().getText() );
	        
	        currentHumidity.setLength( 0 );
	        currentHumidity.append( String.valueOf(
	                Math.round( yahoo19.getCurrentObservation().getAtmosphere().getHumidity() ) ) );
	        
	        currentLocation = currentCity;
	        
	        sunriseTime.setLength( 0 ); // reset
	        sunriseTime.append( yahoo19.getCurrentObservation().getAstronomy().getSunrise().toUpperCase() );
	        
	        sunsetTime.setLength( 0 ); // reset
	        sunsetTime.append(  yahoo19.getCurrentObservation().getAstronomy().getSunset().toUpperCase() );
	        
	        updateTemps( true ); // call update temps here
	        formatWeatherCondition();
	        
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        lblWindReading.setText( currentWindDirection +
                " " + Math.round( Double.parseDouble( currentWindSpeed.toString() ) ) +
                	( WeatherLionMain.storedPreferences.getUseMetric() ? " km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity + "%" );
	
	        // Yahoo loves to omit a zero on the hour mark ex: 7:0 am
	        if( sunriseTime.length() == 6 )
	        {
	            String[] ft= sunriseTime.toString().split( ":" );
	            sunriseTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end of if block
	        else if( sunsetTime.length() == 6 )
	        {
	            String[] ft= sunsetTime.toString().split( ":" );
	            sunsetTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end if else if block

	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );

	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( 
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format(
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
				UtilityMethod.logMessage( "severe", e.getMessage(), 
					TAG + "::loadYahooYdnWeather" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get(
                		currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( 
                		currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() + " (night)");
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get(
        				currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block
	        
	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
    		 		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );

	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        List< YahooWeatherYdnDataItem.Forecast > fdf = yahoo19.getForecast();
	        currentFiveDayForecast.clear(); // ensure that this list is clean
	        
	        for ( int i = 0; i <= fdf.size(); i++ )
	        {
				Date fDate = UtilityMethod.getDateTime( fdf.get( i ).getDate() );
				( (JLabel) getComponentByName( "lblDay"+ (i + 1) +"Day" ) ).setText(
            			new SimpleDateFormat( "E d" ).format( fDate ) );

	            // Load current forecast condition weather image
	            String fCondition =   UtilityMethod.yahooWeatherCodes[
	                    fdf.get( i ).getCode() ];

	            if( fCondition.toLowerCase().contains( "(day)" ) )
	            {
	                fCondition = fCondition.replace( "(day)", "" ).trim();
	            }// end of if block
	            else if( fCondition.toLowerCase().contains( "(night)" ) )
	            {
	                fCondition = fCondition.replace( "(night)", "" ).trim();
	            }// end of if block
	            
	            if( fCondition.toLowerCase().contains( "and" ) )
    	        {
    	            String[] conditions = fCondition.toLowerCase().split( "and" );
    	                 	            
    	            fCondition = conditions[ 0 ].trim();
    	        }// end of if block

	            String fConditionIcon = null;
   	            
	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey().startsWith( fCondition.toLowerCase() ) ) 
	            	    {
	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	fCondition = e.getKey();
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( fConditionIcon == null )
	            	{
	            		fConditionIcon = "na.png";
	            	}// end of if block	            	
	            }// end of if block 
	            else
	            {
	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
	            }// end of if block
	            
	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ (i + 1) +"Image" ) ), WEATHER_IMAGE_PATH_PREFIX + 
            			WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
            	( (JLabel) getComponentByName( "lblDay"+ (i + 1) +"Image" ) ).setToolTipText( UtilityMethod.toProperCase( fCondition ) );
	            
	            Date forecastDate = UtilityMethod.getDateTime( fdf.get( i ).getDate() );
	            
	            currentFiveDayForecast.add(
	            		new FiveDayForecast(forecastDate, String.valueOf( hl[i][0] ),
	            				String.valueOf( hl[i][1] ), fCondition ) );
	            if( i == 4 )
	            {
	                break;
	            }// end of if block
	        }// end of for loop
	        
	        // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
	       
			wXML = new WeatherDataXMLService( WeatherLionMain.YAHOO_WEATHER, new Date(), 
					currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
					currentTemp.toString(),	currentFeelsLikeTemp.toString(), currentHigh.toString(),
					currentLow.toString(),	currentWindSpeed.toString(), currentWindDirection.toString(),
					currentHumidity.toString(),	sunriseTime.toString(),
					sunsetTime.toString(), currentFiveDayForecast );
			
			wXML.execute();
			
	    }// end of method loadYahooYdnWeather
	    
	    @Deprecated
	    /**
	     * Load Yahoo! Weather data
	     */
	    private void loadYahooWeather() throws Exception
	    {
	    	currentCity.setLength( 0 ); // reset
	    	currentCity.append( yahoo.getQuery().getResults().getChannel().getLocation().getCity() +
	                ", " + yahoo.getQuery().getResults().getChannel().getLocation().getRegion() );
	        
	        currentCountry.setLength( 0 ); // reset
	        currentCountry.append( yahoo.getQuery().getResults().getChannel().getLocation().getCountry() );

	        currentCondition.setLength( 0 ); // reset
	        currentCondition.append( 
	                UtilityMethod.yahooWeatherCodes[
	                        Integer.parseInt( yahoo.getQuery().getResults().getChannel().getItem().getCondition().getCode())
	                        ] );
	        
	        currentHumidity.setLength( 0 ); // reset
	        currentHumidity.append( String.valueOf(
	                Math.round( Double.parseDouble( yahoo.getQuery().getResults().getChannel().getAtmosphere().getHumidity() ) ) ) );
	        
	        currentLocation = currentCity;
	        
	        sunriseTime.setLength( 0 ); 
	        sunriseTime.append( yahoo.getQuery().getResults().getChannel().getAstronomy().getSunrise().toUpperCase() );
	        
	        sunsetTime.setLength( 0 );
	        sunsetTime.append( yahoo.getQuery().getResults().getChannel().getAstronomy().getSunset().toUpperCase() );
	        
	        updateTemps( true ); // call update temps here
	        formatWeatherCondition();
	        
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	        lblWindReading.setText( currentWindDirection +
                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric()
            		? " km/h" : " mph" ) );
	        lblHumidity.setText( currentHumidity + "%" );
	
	        // Yahoo loves to omit a zero on the hour mark ex: 7:0 am
	        if( sunriseTime.length() == 6 )
	        {
	            String[] ft= sunriseTime.toString().split( ":" );
	            sunriseTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end of if block
	        else if( sunsetTime.length() == 6 )
	        {
	            String[] ft= sunsetTime.toString().split( ":" );
	            sunsetTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end if else if block

	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );

	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format(
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format(
        		rightNow.getTime() ) + " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
	        	UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadYahooWeather [line: " +
					e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get(
                		currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( 
                		currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() + " (night)" );
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( 
                    		currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get( 
        				currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block
	        
	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
	        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );

	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        List< YahooWeatherDataItem.Query.Results.Channel.Item.Forecast > fdf =
	                yahoo.getQuery().getResults().getChannel().getItem().getForecast();
	        currentFiveDayForecast.clear(); // ensure that this list is clean
	        
	        for ( int i = 0; i <= fdf.size(); i++ )
	        {
	        	( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
            			new SimpleDateFormat( "E d" ).format( fdf.get( i ).getDay() ) );
	        	
	            // Load current forecast condition weather image
	            String fCondition =   UtilityMethod.yahooWeatherCodes[
	                    Integer.parseInt( fdf.get( i ).getCode() ) ];

	            if( fCondition.toLowerCase().contains( "(day)" ) )
	            {
	                fCondition = fCondition.replace( "(day)", "" ).trim();
	            }// end of if block
	            else if( fCondition.toLowerCase().contains( "(night)" ) )
	            {
	                fCondition = fCondition.replace( "(night)", "" ).trim();
	            }// end of if block
	            
	            if( fCondition.toLowerCase().contains( "and" ) )
    	        {
    	            String[] conditions = fCondition.toLowerCase().split( "and" );
    	                 	            
    	            fCondition = conditions[ 0 ].trim();
    	        }// end of if block

	            String fConditionIcon = null;
   	            
	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
	            	    {
	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	fCondition = e.getKey();
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( fConditionIcon == null )
	            	{
	            		fConditionIcon = "na.png";
	            	}// end of if block	            	
	            }// end of if block 
	            else
	            {
	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
	            }// end of if block
	            
	            loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ),
	            	WEATHER_IMAGE_PATH_PREFIX +	WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
            	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( 
            		UtilityMethod.toProperCase( fCondition ) );
	            
	            Date forecastDate = null;
	            
				try 
				{
					forecastDate = new SimpleDateFormat( "dd MMM yyyy" ).parse( fdf.get( i ).getDate() );
				}// end of try block
				catch (ParseException e)
				{
					UtilityMethod.logMessage( "severe" , e.getMessage(),
						TAG + "::loadYahooWeather [line: " +
						e.getStackTrace()[ 1 ].getLineNumber() + "]" );
				}// end of catch block
	            
	            currentFiveDayForecast.add(
	            		new FiveDayForecast(forecastDate, String.valueOf( hl[i][0] ),
	            				String.valueOf(hl[i][1] ), fCondition ) );
	            if( i == 4 )
	            {
	                break;
	            }// end of if block
	        }// end of for loop
	        
	        // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
            
	        wXML = new WeatherDataXMLService( WeatherLionMain.YAHOO_WEATHER, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
	        		currentTemp.toString().substring( 0, currentTemp.toString().indexOf( DEGREES ) ).trim(),
	        		currentFeelsLikeTemp.toString(), currentHigh.toString(), currentLow.toString(),
	        		currentWindSpeed.toString(), currentWindDirection.toString(), currentHumidity.toString(),
	        		sunriseTime.toString(), sunsetTime.toString(), currentFiveDayForecast );
	        
	        wXML.execute();
	    }// end of method loadYahooWeather
	    
	    /**
	     * Load Yr Weather data
	     */
	    private void loadYrWeather() throws Exception
	    {
	        currentCity.setLength( 0 );
	        currentCity.append( yr.getName() );
	        	        
	        currentCountry.setLength( 0 );
	        currentCountry.append( yr.getCountry() );

	        currentCondition.setLength( 0 ); // reset
	        currentCondition.append( UtilityMethod.toProperCase( yr.getForecast().get( 0 ).getSymbolName() ) );
	        
	        currentHumidity.setLength( 0 );
	        currentHumidity.append( currentHumidity != null ? currentHumidity : String.valueOf( 0 ) ); // use the humidity reading from previous providers
	        
	        // append a zero if there is no humidity
	        if( currentHumidity.length() == 0 ) currentHumidity.append( "0" );
	        
	        currentLocation = currentCity;
	        
	        sunriseTime.setLength( 0 );
	        sunriseTime.append( new SimpleDateFormat( "h:mm a" ).format( yr.getSunrise() ) );

	        sunsetTime.setLength( 0 );
	        sunsetTime.append( new SimpleDateFormat( "h:mm a" ).format( yr.getSunset() ) );
	        
	        // call update temps here
	        updateTemps( true );
	        
	        formatWeatherCondition();
	
	        lblWeatherCondition.setText( UtilityMethod.toProperCase( currentCondition.toString() ) );
	        lblHumidity.setText( !currentHumidity.toString().contains( "%" ) ?  currentHumidity.toString() + "%" : currentHumidity.toString() );
	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );

	        // Some providers like Yahoo love to omit a zero on the hour mark example: 7:0 am
	        if( sunriseTime.length() == 6 )
	        {
	            String[] ft = sunriseTime.toString().split( ":" );
	            sunriseTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end of if block
	        else if( sunsetTime.length() == 6 )
	        {
	            String[] ft= sunsetTime.toString().split( ":" );
	            sunsetTime.append( ft[ 0 ] + ":0" + ft[ 1 ] );
	        }// end if else if block

	        lblSunrise.setText( sunriseTime.toString() );
	        lblSunset.setText( sunsetTime.toString() );

	        // Load current condition weather image
	        Calendar rightNow = Calendar.getInstance();
	        Calendar nightFall = Calendar.getInstance();
	        Calendar sunUp = Calendar.getInstance();
	        String sunsetTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunsetTime.toString() );
	        String sunriseTwenty4HourTime = new SimpleDateFormat( "yyyy-MM-dd" ).format( rightNow.getTime() )
	        		+ " " + UtilityMethod.get24HourTime( sunriseTime.toString() );
	        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
	        Date rn = null;
			Date nf = null;
			Date su = null;
			
	        try 
	        {
				rn = sdf.parse( sdf.format( rightNow.getTime() ) );
				nightFall.setTime( sdf.parse( sunsetTwenty4HourTime ) );
				nightFall.set( Calendar.MINUTE, Integer.parseInt( sunsetTwenty4HourTime.split( ":" )[ 1 ].trim() ) );
				sunUp.setTime( sdf.parse( sunriseTwenty4HourTime ) );
				
				nf = sdf.parse( sdf.format( nightFall.getTime() ) );
				su = sdf.parse( sdf.format( sunUp.getTime() ) );
			} // end of try block
	        catch ( ParseException e )
	        {
	        	UtilityMethod.logMessage( "severe" , e.getMessage(),
					TAG + "::loadHereMapsWeather [line: " +
					e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
	        	        
	        String currentConditionIcon = null;
	        
	        if ( rn.equals( nf ) || rn.after( nf ) || rn.before( su ) )
	        {
	            if ( currentCondition.toString().toLowerCase().contains( "(night)" ) )
	            {
	                currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	            }// end of if block
	            else
	            {
	                if( UtilityMethod.weatherImages.containsKey( currentCondition.toString().toLowerCase() + " (night)" ) )
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get(currentCondition.toString().toLowerCase() + " (night)");
	                }// end of if block
	                else
	                {
	                    currentConditionIcon = UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() );
	                }// end of else block
	            }// end of else block
	            
	            if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	            
	            sunsetIconsInUse = true;
	            sunriseIconsInUse = false;
	        }// end of if block
	        else
	        {
	        	if( UtilityMethod.weatherImages.get( currentCondition.toString().toLowerCase() ) == null )
	            {
	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
	            	{
	            	    if ( e.getKey() .startsWith( currentCondition.toString().toLowerCase() ) ) 
	            	    {
	            	    	currentConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
	            	    	currentCondition.setLength( 0 ); // reset
	            	    	currentCondition.append( e.getKey() );
	            	    	break; // exit the loop
	            	    }// end of if block
	            	}// end of for block
	            	
	            	// if a match still could not be found, use the not available icon
	            	if( currentConditionIcon == null )
	            	{
	            		currentConditionIcon = "na.png";
	            	}// end of if block
	            }// end of if block 
	        	else
	        	{
	        		currentConditionIcon = UtilityMethod.weatherImages.get(
        				currentCondition.toString().toLowerCase() );
	        	}// end of else block
	        	
	            sunriseIconsInUse = true;
        		sunsetIconsInUse = false;
	        }// end of else block
	        
	        loadWeatherIcon( lblCurrentConditionImage, WEATHER_IMAGE_PATH_PREFIX +
        		WeatherLionMain.iconSet + "/weather_" + currentConditionIcon, 140, 140 );

	        lblCurrentConditionImage.setToolTipText( 
        		UtilityMethod.toProperCase( currentCondition.toString() ) );
	        
	        List< YrWeatherDataItem.Forecast > fdf = yr.getForecast();
	        SimpleDateFormat df = new SimpleDateFormat( "MMMM dd, yyyy" );
	        
	        int i = 1;
	        int x = 0;
	        currentFiveDayForecast.clear(); // ensure that this list is clean
	        
	        for ( Forecast wxDailyForecast : fdf )
			{
	            x++;
				
				// the first time period is one that will be stored
                if ( x == 1 )
                {                	
                	Date forecastDate = wxDailyForecast.getTimeFrom();
                	
                	( (JLabel) getComponentByName( "lblDay"+ i +"Day" ) ).setText(
                			new SimpleDateFormat( "E d" ).format( forecastDate ) );
                	
                	// Load current forecast condition weather image
                	String fCondition =   wxDailyForecast.getSymbolName();
                	
                	if( fCondition.toLowerCase().contains( "(day)" ) )
                	{
                		fCondition = fCondition.replace( "(day)", "" ).trim();
                	}// end of if block
                	else if( fCondition.toLowerCase().contains( "(night)" ) )
                	{
                		fCondition = fCondition.replace( "(night)", "" ).trim();
                	}// end of if block
                	
                	 if( fCondition.toLowerCase().contains( "and" ) )
         	        {
         	            String[] conditions = fCondition.toLowerCase().split( "and" );
         	                 	            
         	            fCondition = conditions[ 0 ].trim();
         	        }// end of if block
                	 
                	String fConditionIcon = null;
        	            
     	            if( UtilityMethod.weatherImages.get( fCondition.toLowerCase() ) == null )
     	            {
     	            	// sometimes the JSON data received is incomplete so this has to be taken into account 
     	            	for ( Entry<String, String> e : UtilityMethod.weatherImages.entrySet() )
     	            	{
     	            	    if ( e.getKey() .startsWith( fCondition.toLowerCase() ) ) 
     	            	    {
     	            	    	fConditionIcon =  UtilityMethod.weatherImages.get( e.getKey() ); // use the closest match
     	            	    	fCondition = e.getKey();
     	            	    	break; // exit the loop
     	            	    }// end of if block
     	            	}// end of for block
     	            	
     	            	// if a match still could not be found, use the not available icon
     	            	if( fConditionIcon == null )
     	            	{
     	            		fConditionIcon = "na.png";
     	            	}// end of if block	            	
     	            }// end of if block 
     	            else
     	            {
     	            	fConditionIcon = UtilityMethod.weatherImages.get( fCondition.toLowerCase() );
     	            }// end of if block               	
     	                	           
                	loadWeatherIcon( ( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ), WEATHER_IMAGE_PATH_PREFIX + 
                			WeatherLionMain.iconSet + "/weather_" + fConditionIcon, 40, 40 );
                	( (JLabel) getComponentByName( "lblDay"+ i +"Image" ) ).setToolTipText( UtilityMethod.toProperCase( fCondition ) );
                	
                	currentFiveDayForecast.add(
                			new FiveDayForecast( forecastDate,
            					String.valueOf( dailyReading.get( df.format( wxDailyForecast.getTimeFrom() ) ) [ 0 ][ 0 ] ),
            					String.valueOf( dailyReading.get( df.format( wxDailyForecast.getTimeFrom() ) ) [ 0 ][ 1 ] ),
            					fCondition ) );
                	if( i == 5 )
                	{
                		break;
                	}// end of if block
                	
                	i++; // increment sentinel
                }// end of if block
	            
	            if ( wxDailyForecast.getTimePeriod() == 3 )
                {
                    x = 0;
                }// end of if block
	        }// end of for loop
	        
	        // if the code gets to here then all was loaded successfully
            WeatherLionWidget.dataLoadedSuccessfully = true;
            
	        wXML = new WeatherDataXMLService( WeatherLionMain.YR_WEATHER, new Date(), 
	        		currentCity.toString(), currentCountry.toString(), currentCondition.toString(), 
	        		currentTemp.toString(),	currentFeelsLikeTemp.toString(), currentHigh.toString(),
	        		currentLow.toString(), currentWindSpeed.toString(), currentWindDirection.toString(), currentHumidity.toString(),
	        		sunriseTime.toString(), sunsetTime.toString(), currentFiveDayForecast );
	        
	        wXML.execute();
	    }// end of method loadYrWeather

	    /***
	     * Check the current time of day
	     */
		private void formatWeatherCondition()
		{
			String tc = currentCondition.toString();
			
			if( currentCondition.toString().toLowerCase().startsWith( "day" ) )
	    	{
	        	currentCondition.setLength( 0 ); // reset
	        	currentCondition.append( tc.toLowerCase().replace( "day", "" ).trim() );
	    	}// end of if block
	        else if( currentCondition.toString().toLowerCase().startsWith( "night" ) )
	    	{
	        	currentCondition.setLength( 0 ); // reset
	        	currentCondition.append( tc.toLowerCase().replace( "night", "" ).trim() );
	    	}// end of else if block
	        
	        if( currentCondition.toString().toLowerCase().contains( "(day)" ) )
	        {
	        	currentCondition.setLength( 0 ); // reset
	        	currentCondition.append( 
	                    UtilityMethod.toProperCase( tc.toString().replace( "(day)", "" ).trim() ) );
	        }// end of if block
	        else if( currentCondition.toString().toLowerCase().contains("(night)" ) )
	        {
	        	currentCondition.setLength( 0 ); // reset
	        	currentCondition.append( 
	                    UtilityMethod.toProperCase( tc.toString().replace( "(night)", "" ).trim() ) );
	        }// end of else if block
	        
	        if( currentCondition.toString().toLowerCase().contains( "thunderstorms" ) && 
	    			currentCondition.toString().toLowerCase().indexOf( "thunderstorms" ) > 0 )
	        {
	        	currentCondition.setLength( 0 ); // reset
	        	currentCondition.append( tc.toString().toLowerCase().replace( "thunderstorms", "t-storms" ) );
	        }// end of if block
	    	else if( currentCondition.toString().toLowerCase().contains( "thundershowers" ) && 
	    			currentCondition.toString().toLowerCase().indexOf( "thundershowers" ) > 0 )
	        {
	    		currentCondition.setLength( 0 ); // reset
	    		currentCondition.append( tc.toString().toLowerCase().replace( "thundershowers", "t-showers" ) );
	        }// end of else if block
	    	else if( currentCondition.toString().toLowerCase().contains( "and" ) )
	        {
	            String[] conditions = currentCondition.toString().toLowerCase().split( "and" );
	            
	            currentCondition.setLength( 0 ); // reset
	    		currentCondition.append( 
	                    UtilityMethod.toProperCase( conditions[ 0 ].trim() ) );
	        }// end of if block
		}// end of method astronomyCheck()
	    
	    /***
	     * Update the numerical values displayed on the widget
	     * 
	     * @param hasConnection A {@code boolean} value representing Internet Connectivity.
	     */
		private void updateTemps( boolean hasConnection ) 
	    {
	    	String today = null;
	    	int i;
	    	
	    	if( hasConnection )
	    	{
	    		switch( WeatherLionMain.storedPreferences.getProvider() )
				{
					case WeatherLionMain.DARK_SKY:
	                    if( WeatherLionMain.storedPreferences.getUseMetric() )
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round(
	                                UtilityMethod.fahrenheitToCelsius( darkSky.getCurrently().getTemperature() ) ) );
	
	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round(
	                                UtilityMethod.fahrenheitToCelsius( darkSky.getCurrently().getApparentTemperature() ) ) );
	
	                        currentHigh.setLength( 0 );
	                        currentHigh.append(
	                            Math.round( UtilityMethod.fahrenheitToCelsius(
	                                darkSky.getDaily().getData().get( 0 ).getTemperatureMax() ) ) );
	
	                        currentHigh.setLength( 0 );
	                        currentHigh.append(
	                                Math.round(
	                                        UtilityMethod.celsiusToFahrenheit(
	                                                darkSky.getDaily().getData().get( 0 ).getTemperatureMin() ) ) );
	
	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append( Math.round(
	                            UtilityMethod.mphToKmh( darkSky.getCurrently().getWindSpeed() ) ) );
	                    }// end of if block
	                    else
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round( darkSky.getCurrently().getTemperature() ) );
	
	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round( darkSky.getCurrently().getApparentTemperature() ) );
	
	                        currentHigh.setLength( 0 );
	                        currentHigh.append(
	                            Math.round( darkSky.getDaily().getData().get( 0 ).getTemperatureMax() ) );
	
	                        currentLow.setLength( 0 );
	                        currentLow.append(
	                            Math.round( darkSky.getDaily().getData().get( 0 ).getTemperatureMin() ) );
	
	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append(
	                                Math.round( darkSky.getCurrently().getWindSpeed() ) );
	                    }// end of else block
				
				        // Display weather data on widget
				        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );
				        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp.toString() + DEGREES );
				        lblDayHigh.setText( currentHigh + DEGREES );
				        lblDayHigh.setToolTipText( "Current High Temp " + currentHigh + DEGREES + "F" );
				        lblDayLow.setText( currentLow + DEGREES );
				        lblDayLow.setToolTipText( "Current Low Temp " + currentLow + DEGREES + "F" );
				
				        lblWindReading.setText( currentWindDirection +
			                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric()
		                		? " km/h" : " mph" ) );
				        lblHumidity.setText( currentHumidity.toString() );
				        
				        // Five Day Forecast
				        i = 1;
				        hl = new int[ 5 ][ 2 ];
				        
				        for ( DarkSkyWeatherDataItem.Daily.Data wxForecast : darkSky.getDaily().getData()  )
				        {
				            String fHigh;
				            String fLow;
				
				            if( WeatherLionMain.storedPreferences.getUseMetric() )
				            {
				            	fHigh = String.valueOf( Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( wxForecast.getTemperatureMax() ) ) );
				                fLow = String.valueOf( Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( wxForecast.getTemperatureMin() ) ) );
				            }// end of if block
				            else
				            {
				            	fHigh = String.valueOf( Math.round( wxForecast.getTemperatureMax() ) );
				                fLow = String.valueOf( Math.round( wxForecast.getTemperatureMin() ) );
				            }// end of else block
				
				            String temps = String.format( "%s° %s°", fLow, fHigh );
				            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i) + "Temps" );
				            
				            hl[ i - 1 ][ 0 ] = Integer.parseInt( fHigh );
				            hl[ i - 1 ][ 1 ] = Integer.parseInt( fLow );
				            
				            dayTemps.setText( temps );
				            
				            if ( i == 5 )
				            {
				                break;
				            }// end of if block
				            
				            i++; // increment sentinel
				        }// end of for each loop
				        
						break;
					case WeatherLionMain.HERE_MAPS:
	                    double fl =
	                            hereWeatherWx
                                    .getObservations()
                                    .getLocation()
                                    .get( 0 )
                                    .getObservation()
                                    .get( 0 )
                                    .getComfort();

	                    if( WeatherLionMain.storedPreferences.getUseMetric() )
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append(
	                            Math.round(
                                    UtilityMethod.fahrenheitToCelsius(
                                        hereWeatherWx
                                            .getObservations()
                                            .getLocation()
                                            .get( 0 )
                                            .getObservation()
                                            .get( 0 )
                                            .getTemperature()
                                    ) ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append(
	                            Math.round( UtilityMethod.fahrenheitToCelsius( (float) fl ) ) );

	                        currentHigh.setLength( 0 );
	                        currentHigh.append(
	                            Math.round(
	                                UtilityMethod.fahrenheitToCelsius(
                                        hereWeatherWx
                                            .getObservations()
                                            .getLocation()
                                            .get( 0 )
                                            .getObservation()
                                            .get( 0 )
                                            .getHighTemperature()
	                                ) ) );

	                        currentLow.setLength( 0 );
	                        currentLow.append(
	                            Math.round(
	                                UtilityMethod.fahrenheitToCelsius(
	                                    hereWeatherWx
	                                        .getObservations()
	                                        .getLocation()
	                                        .get( 0 )
	                                        .getObservation()
	                                        .get( 0 )
	                                        .getLowTemperature()
	                                ) ) );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append(
	                            Math.round(
	                                UtilityMethod.fahrenheitToCelsius(
	                                    hereWeatherWx
	                                        .getObservations()
	                                        .getLocation()
	                                        .get( 0 )
	                                        .getObservation()
	                                        .get( 0 )
	                                        .getWindSpeed()
	                                ) ) );
	                    }// end of if block
	                    else
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append(
	                            Math.round(
	                                hereWeatherWx
	                                    .getObservations()
	                                    .getLocation()
	                                    .get( 0 )
	                                    .getObservation()
	                                    .get( 0 )
	                                    .getTemperature()
	                            ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round( (float) fl ) );

	                        currentHigh.setLength( 0 );
	                        currentHigh.append(
	                            Math.round(
	                                hereWeatherWx
	                                    .getObservations()
	                                    .getLocation()
	                                    .get( 0 )
	                                    .getObservation()
	                                    .get( 0 )
	                                    .getHighTemperature()
	                            ) );

	                        currentLow.setLength( 0 );
	                        currentLow.append(
	                            Math.round(
	                                hereWeatherWx
	                                    .getObservations()
	                                    .getLocation()
	                                    .get( 0 )
	                                    .getObservation()
	                                    .get( 0 )
	                                    .getLowTemperature()
	                            ) );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append(
	                            Math.round(
	                                hereWeatherWx
	                                    .getObservations()
	                                    .getLocation()
	                                    .get( 0 )
	                                    .getObservation()
	                                    .get( 0 )
	                                    .getWindSpeed()
	                            ) );
	                    }// end of else block
				
				        // Display weather data on widget
				        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );
				        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp + DEGREES );
				        lblDayHigh.setText( currentHigh + DEGREES );
				        lblDayHigh.setToolTipText( "Current High Temp " + currentLow + DEGREES + "F" );
				        lblDayLow.setText( currentLow + DEGREES );
				        lblDayLow.setToolTipText( "Current Low Temp " + currentLow + DEGREES + "F" );
				
				        lblWindReading.setText( currentWindDirection +
				                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ?
				                		" km/h" : " mph" ) );
				        lblHumidity.setText( currentHumidity.toString() );
				        
				        // Five Day Forecast
				        List< HereMapsWeatherDataItem.ForecastData.DailyForecasts.ForecastLocation.Forecast > hFdf = 
				        		hereWeatherFx.getDailyForecasts().getForecastLocation().getForecast();
				        i = 1;
				        hl = new int[ 5 ][ 2 ];
				        
				        for ( HereMapsWeatherDataItem.ForecastData.DailyForecasts.ForecastLocation.Forecast wxForecast : hFdf )
				        {
				            String fHigh;
				            String fLow;
				
				            if( WeatherLionMain.storedPreferences.getUseMetric() )
				            {
				            	fHigh = String.valueOf( Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( wxForecast.getHighTemperature() ) ) );
				                fLow = String.valueOf( Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( wxForecast.getLowTemperature() ) ) );
				            }// end of if block
				            else
				            {
				            	fHigh = String.valueOf( Math.round( wxForecast.getHighTemperature() ) );
				                fLow = String.valueOf( Math.round( wxForecast.getLowTemperature() ) );
				            }// end of else block
				
				            String temps = String.format( "%s° %s°", fLow, fHigh );
				            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i) + "Temps" );
				            
				            hl[ i - 1 ][ 0 ] = Integer.parseInt( fHigh );
				            hl[ i - 1 ][ 1 ] = Integer.parseInt( fLow );
				            			            
				            dayTemps.setText( temps );		
				
				            if ( i == 5 )
				            {
				                break;
				            }// end of if block
				            
				            i++; // increment sentinel
				        }// end of for each loop
				        
						break;
					case WeatherLionMain.OPEN_WEATHER:
	                    fl = UtilityMethod.heatIndex( openWeatherWx.getMain().getTemp(),
	                            openWeatherWx.getMain().getHumidity() );

	                    if( WeatherLionMain.storedPreferences.getUseMetric() )
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round(
	                            UtilityMethod.fahrenheitToCelsius( openWeatherWx.getMain().getTemp() ) ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round(
	                            UtilityMethod.fahrenheitToCelsius( (float) fl ) ) );

	                        currentHigh.setLength( 0 );
	                        currentHigh.append(
	                            Math.round(
	                                UtilityMethod.fahrenheitToCelsius(
	                                    openWeatherFx.getList().get( 0 ).getTemp().getMax() ) ) );

	                        currentHigh.setLength( 0 );
	                        currentLow.append(
	                            Math.round(
	                                UtilityMethod.celsiusToFahrenheit(
	                                    openWeatherFx.getList().get( 0 ).getTemp().getMin() ) ) );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append(
	                            Math.round( UtilityMethod.mphToKmh( openWeatherWx.getWind().getSpeed() ) ) );
	                    }// end of if block
	                    else
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round( openWeatherWx.getMain().getTemp() ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round( (float) fl ) );

	                        currentHigh.setLength( 0 );
	                        currentHigh.append( Math.round( openWeatherFx.getList().get( 0 ).getTemp().getMax() ) );

	                        currentLow.setLength( 0 );
	                        currentLow.append( Math.round( openWeatherFx.getList().get( 0 ).getTemp().getMin() ) );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append( Math.round( openWeatherWx.getWind().getSpeed() ) );
	                    }// end of else block
				
				        // Display weather data on widget
				        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );
				        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp + DEGREES );
				        lblDayHigh.setText( currentHigh + DEGREES );
				        lblDayHigh.setToolTipText( "Current High Temp " + currentLow + DEGREES + "F");
				        lblDayLow.setText( currentLow + DEGREES );
				        lblDayLow.setToolTipText( "Current Low Temp " + currentLow + DEGREES + "F");
				
				        lblWindReading.setText( currentWindDirection +
				                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ?
				                		" km/h" : " mph" ) );
				        lblHumidity.setText( currentHumidity.toString() );
				        
				        // Five Day Forecast
				        List< OpenWeatherMapWeatherDataItem.ForecastData.Data > oFdf = openWeatherFx.getList();
				        i = 1;
				        hl = new int[ 5 ][ 2 ];
				        
				        for ( OpenWeatherMapWeatherDataItem.ForecastData.Data wxForecast : oFdf )
				        {
				            String fHigh;
				            String fLow;
				
				            if( WeatherLionMain.storedPreferences.getUseMetric() )
				            {
				            	fHigh = String.valueOf( Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( wxForecast.getTemp().getMax() ) ) );
				                fLow = String.valueOf( Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( wxForecast.getTemp().getMin() ) ) );
				            }// end of if block
				            else
				            {
				            	fHigh = String.valueOf( Math.round( wxForecast.getTemp().getMax() ) );
				                fLow = String.valueOf( Math.round( wxForecast.getTemp().getMin() ) );
				            }// end of else block
				
				            String temps = String.format( "%s° %s°", fLow, fHigh );
				            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i) + "Temps" );
				            
				            hl[ i - 1 ][ 0 ] = Integer.parseInt( fHigh );
				            hl[ i - 1 ][ 1 ] = Integer.parseInt( fLow );
				            			            
				            dayTemps.setText( temps );		
				
				            if ( i == 5 )
				            {
				                break;
				            }// end of if block
				            
				            i++; // increment sentinel
				        }// end of for each loop
				        
						break;
					case WeatherLionMain.WEATHER_BIT:
	                    fl = weatherBitWx.getData().get( 0 ).getAppTemp() == 0
	                            ?  weatherBitWx.getData().get( 0 ).getTemp()
	                            : weatherBitWx.getData().get( 0 ).getAppTemp();

	                    if( WeatherLionMain.storedPreferences.getUseMetric() )
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius( (float) fl ) ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round(
	                            UtilityMethod.fahrenheitToCelsius( (float) weatherBitWx.getData().get( 0 ).getAppTemp() ) ) );

	                        // not supplied by provider
	                        currentHigh.setLength( 0 );
	                        currentHigh.append( 0  + DEGREES );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append(
	                            Math.round( UtilityMethod.mphToKmh( weatherBitWx.getData().get( 0 ).getWindSpeed() ) ) );
	                    }// end of if block
	                    else
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round( (float) weatherBitWx.getData().get( 0 ).getTemp() ) );

	                        currentHigh.setLength( 0 );
	                        currentHigh.append( Math.round( 0 ) ); // not supplied by provider

	                        currentLow.setLength( 0 );
	                        currentLow.append( 0 ); // not supplied by provider

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append( Math.round( weatherBitWx.getData().get( 0 ).getWindSpeed() ) );
	                    }// end of else block
				
						// Display weather data on widget
				        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );
				        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp + DEGREES );
				        lblDayHigh.setText( currentHigh + DEGREES );
				        lblDayHigh.setToolTipText( "Current High Temp " + currentLow + DEGREES + "F");
				        lblDayLow.setText( currentLow + DEGREES );
				        lblDayLow.setToolTipText( "Current Low Temp " + currentLow + DEGREES + "F");
				
				        lblWindReading.setText( currentWindDirection +
				                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ?
				                		" km/h" : " mph" ) );
				        lblHumidity.setText( currentHumidity.toString() );
				        
				        // Five Day Forecast
				        List< WeatherBitWeatherDataItem.SixteenDayForecastData.Data > wFdf = weatherBitFx.getData();
			            int count = wFdf.size(); // number of items in the array
			            double lowTemp = 0;
		                double highTemp = 0;
				        i = 1;
				        hl = new int[ 5 ][ 2 ];
				        
				        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
				        today = df.format( new Date() );
				        
				        for ( WeatherBitWeatherDataItem.SixteenDayForecastData.Data wxForecast : wFdf )
				        {
				        	String fxDate = null;
				        	df = new SimpleDateFormat( "yyyy-MM-dd" );
				        	String dt = wxForecast.getDatetime();
				        	
				        	try 
				        	{
				        		fxDate = df.format( df.parse( dt ) );
							}// end of try block
				        	catch ( ParseException e )
				        	{
				        		UtilityMethod.logMessage( "severe" , e.getMessage(),
									TAG + "::updateTemps [line: " +
									e.getStackTrace()[ 1 ].getLineNumber() + "]" );
							}// end of catch block
				        	
				        	if( fxDate.equals( today ) )
			                {
			                	currentHigh.setLength( 0 );
			                	currentHigh.append( Math.round( wFdf.get( i ).getMaxTemp() ) );
			                	
			                	currentLow.setLength( 0 );
			                	currentLow.append( Math.round( wFdf.get( i ).getMinTemp() ) );
			                	
			                	lblDayHigh.setText( 
			                			( Integer.parseInt( currentHigh.toString() ) > Integer.parseInt( currentTemp.toString().replace( "°F" , "" ) ) 
			                					? currentHigh.toString() : Integer.parseInt( currentTemp.toString().replace( "°F" , "" ) ) + DEGREES ) );
			                    lblDayLow.setText( currentLow + DEGREES );
			                    
			                    highTemp = wFdf.get( i ).getMaxTemp() > Double.parseDouble( currentTemp.toString().replace( "°F" , "" ) ) 
			                    		? wFdf.get( i ).getMaxTemp() : Double.parseDouble( currentTemp.toString().replace( "°F" , "" ) );
			                }// end of if block
				        	else 
				        	{
				        		highTemp = wFdf.get( i ).getMaxTemp();
				        	}// end of else block
		                    
				        	// this data that they provide is inaccurate but it will be used
				        	lowTemp = wFdf.get( i ).getMinTemp();
				        	
	                    	if( WeatherLionMain.storedPreferences.getUseMetric() )
				            {
				            	highTemp = Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( (float) highTemp ) );
				            	lowTemp = Math.round( 
				            			 UtilityMethod.fahrenheitToCelsius( (float) lowTemp ) );
				            }// end of if block
				            else
				            {
				            	highTemp = Math.round( highTemp );
				            	lowTemp = Math.round( lowTemp );
				            }// end of else block

	                    	hl[ i - 1 ][ 0 ] = (int) highTemp;
				            hl[ i - 1 ][ 1 ] = (int) lowTemp;
					            
				            String fHigh = String.valueOf(  (int) highTemp );
				            String fLow = String.valueOf(  (int) lowTemp );				
				            String temps = String.format( "%s° %s°", fLow, fHigh );
				            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i) + "Temps" );
					            			            
				            dayTemps.setText( temps );
					            
		                    if( i == 5 ) 
		                    {
		                    	break;
		                    }// end of if block
		                    
		                    i++; // increment sentinel
				        }// end of for each loop
				        
						break;
				    case WeatherLionMain.WEATHER_UNDERGROUND:
				    	List< WeatherUndergroundDataItem.Forecast.SimpleForecast.ForecastDay > wuFdf =
		                underground.getForecast().getSimpleforecast().getForecastday();
				    	
				    	Float ct = Float.parseFloat( underground.getCurrent_observation().getTemp_f() );
				    	Float ch = Float.parseFloat( currentHumidity.toString().replace( "%", "" ) );
				    	
				        if( WeatherLionMain.storedPreferences.getUseMetric() )
				        {
				        	currentTemp.setLength( 0 );
				        	currentTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius( ct ) ) );
				            
				            if( underground.getCurrent_observation().getHeat_index_f().equalsIgnoreCase( "NA" )
				            		|| underground.getCurrent_observation().getHeat_index_f() == null )
				            {
				            	currentFeelsLikeTemp.setLength( 0 );
				            	currentFeelsLikeTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius( 
			            			(float) UtilityMethod.heatIndex( Double.parseDouble( currentTemp.toString() ),
		            					ch ) ) ) );
				            }// end of if block
				            else
				            {
				            	currentFeelsLikeTemp.setLength( 0 );
				            	currentFeelsLikeTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius( ct ) ) );
				            }// end of else block
				            
				            currentHigh.setLength( 0 );
				            currentHigh.append(
			            		Math.round( UtilityMethod.fahrenheitToCelsius( wuFdf.get(0).getHigh().getFahrenheit() ) ) );
				            
				            currentLow.setLength( 0 );
				            currentLow.append( 
			                    Math.round( UtilityMethod.fahrenheitToCelsius( wuFdf.get(0).getLow().getFahrenheit() ) ) );
				
				            currentWindSpeed.setLength( 0 );
				            currentWindSpeed.append( Math.round( UtilityMethod.mphToKmh( underground.getCurrent_observation().getWind_mph() ) ) );
				        }// end of if block
				        else
				        {
				        	currentTemp.setLength( 0 );
				            currentTemp.append( Math.round( ct ) );
				            
				            if( underground.getCurrent_observation().getHeat_index_f().equalsIgnoreCase( "NA" )
				            		|| underground.getCurrent_observation().getHeat_index_f() == null )
				            {
				            	currentFeelsLikeTemp.setLength( 0 );
				            	currentFeelsLikeTemp.append( Math.round( UtilityMethod.heatIndex(
			            			 ct, ch ) ) );
				            }// end of if block
				            else
				            {
				            	currentFeelsLikeTemp.setLength( 0 );
				            	currentFeelsLikeTemp.append( Math.round( ( Float.parseFloat(
				                    underground.getCurrent_observation().getHeat_index_f() ) ) ) );
				            }// end of else block
				            
				            currentHigh.setLength( 0 );
				            currentHigh.append( wuFdf.get( 0 ).getHigh().getFahrenheit() );

				            currentLow.setLength( 0 );
				            currentLow.append( wuFdf.get( 0 ).getLow().getFahrenheit() );
				            
				            currentWindSpeed.setLength( 0 );
				            currentWindSpeed.append( Math.round( 
			            		underground.getCurrent_observation().getWind_mph() ) );
				        }// end of else block
				
				        // Display weather data on widget
				        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );
				        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp.toString() + DEGREES );
				        lblDayHigh.setText( currentHigh + DEGREES );
				        lblDayHigh.setToolTipText( "Current High Temp " + currentLow.toString() + DEGREES + "F" );
				        lblDayLow.setText( currentLow + DEGREES );
				        lblDayLow.setToolTipText( "Current Low Temp " + currentLow.toString() + DEGREES + "F" );
				
				        lblWindReading.setText( currentWindDirection +
			                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric()
		                		? " km/h" : " mph" ) );
				        lblHumidity.setText( currentHumidity.toString() );
				        
				        // Five Day Forecast
				        Calendar c = Calendar.getInstance();
				        hl = new int[ 5 ][ 2 ];
				        
				        // loop through the forecast data. only 5 days are needed
				        for ( WeatherUndergroundDataItem.Forecast.SimpleForecast.ForecastDay wxForecast : wuFdf )
				        {
				            int period = wxForecast.getPeriod();
				
				            c.set( wxForecast.getDate().getYear(),
				                    wxForecast.getDate().getMonth() - 1,
				                    wxForecast.getDate().getDay() );
				
				            String fh;
				            String fLow;
				
				            if( WeatherLionMain.storedPreferences.getUseMetric() )
				            {
				                fh = String.valueOf(
				                        Math.round(UtilityMethod.fahrenheitToCelsius(
				                                wxForecast.getHigh().getFahrenheit() ) )
				                );
				                fLow =  String.valueOf(
				                        Math.round( UtilityMethod.fahrenheitToCelsius( 
				                                wxForecast.getLow().getFahrenheit() ) )
				                );
				            }// end of if block
				            else
				            {
				                fh = String.valueOf( Math.round( wxForecast.getHigh().getFahrenheit() ) );
				                fLow = String.valueOf( Math.round( wxForecast.getLow().getFahrenheit() ) );
				            }// end of else block
				
				            String temps = String.format("%s° %s°", fLow, fh);
				            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (period) + "Temps" );
				            
				            hl[ period - 1 ][ 0 ] = Integer.parseInt( fh );
				            hl[ period - 1 ][ 1 ] = Integer.parseInt( fLow );
				            			            
				            dayTemps.setText( temps );			
				
				            if ( period == 5 )
				            {
				                break;
				            }// end of if block
				        }// end of for each loop
					
				    	break;
				    case WeatherLionMain.YAHOO_WEATHER:
	                    currentWindSpeed.setLength( 0 );
	                    currentWindSpeed.append( yahoo19.getCurrentObservation().getWind().getSpeed() );

	                    currentWindDirection.setLength( 0 );
	                    currentWindDirection.append( UtilityMethod.compassDirection(
	                            yahoo19.getCurrentObservation().getWind().getDirection() ) );

	                    fl = UtilityMethod.heatIndex(
	                            yahoo19.getCurrentObservation().getCondition().getTemperature(),
	                            yahoo19.getCurrentObservation().getAtmosphere().getHumidity() );

	                    if( WeatherLionMain.storedPreferences.getUseMetric() )
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius(
	                                (float) yahoo19.getCurrentObservation().getCondition().getTemperature() ) ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round(
	                            UtilityMethod.fahrenheitToCelsius( (float) fl ) ) );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append( 
	                            Math.round(UtilityMethod.mphToKmh( yahoo19.getCurrentObservation().getWind().getSpeed() ) ) );
	                    }// end of if block
	                    else
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round(
	                                yahoo19.getCurrentObservation().getCondition().getTemperature() ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round( fl ) );

	                        currentWindSpeed.setLength( 0 ); // reset
	                        currentWindSpeed.append( yahoo19.getCurrentObservation().getWind().getSpeed() );
	                    }// end of else block

				        // Display weather data on widget
				        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );			        
				        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp + DEGREES );
				        lblWindReading.setText( currentWindDirection +
			                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ? " km/h" : " mph" ) );
				        			        
				        List< YahooWeatherYdnDataItem.Forecast > yFdf = yahoo19.getForecast();
				        			        
				        hl = new int[ 5 ][ 2 ];
				        
				        for ( i = 0; i <= yFdf.size() - 1; i++ )
				        {
				        	df = new SimpleDateFormat( "dd MMM yyyy" );
				            String fDate = df.format( UtilityMethod.getDateTime( yFdf.get( i ).getDate() ) );
				            today = df.format( new Date() );

				            String temps;
				            String fh;
				            String fLow;

				            if( WeatherLionMain.storedPreferences.getUseMetric() )
				            {
				                lblDayHigh.setText( Math.round( UtilityMethod.fahrenheitToCelsius(
	                                (float) yFdf.get( i ).getHigh() ) ) + DEGREES );
				                lblDayLow.setText( Math.round( UtilityMethod.fahrenheitToCelsius(
	                                (float) yFdf.get( i ).getLow() ) ) + DEGREES );

				                fh = String.valueOf( Math.round( UtilityMethod.fahrenheitToCelsius( (float) yFdf.get( i ).getHigh() ) ) );
				                fLow = String.valueOf( Math.round( UtilityMethod.fahrenheitToCelsius( (float) yFdf.get( i ).getLow() ) ) );
				                temps = String.format( "%s° %s°", fLow, fh );

				            }// end of if block
				            else
				            {
				                if( fDate.equals( today ) )
				                {
				                	currentHigh.setLength( 0 );
				                	currentHigh.append( (int) yFdf.get( i ).getHigh() );
				                	
				                	currentLow.setLength( 0 );
				                	currentLow.append( (int) yFdf.get( i ).getLow() );
				                	
				                	lblDayHigh.setText( currentHigh + DEGREES );
				                    lblDayLow.setText( currentLow + DEGREES );
				                }// end of if block
				                
				                fh = String.valueOf( Math.round( yFdf.get( i ).getHigh() ) );
				                fLow = String.valueOf( Math.round( yFdf.get( i ).getLow() ) );

				                temps = String.format( "%s° %s°", fLow, fh );
				            }// end of else block
				            
				            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i + 1) + "Temps" );
				            			            
				            hl[i][0] = Integer.parseInt( fh );
				            hl[i][1] = Integer.parseInt( fLow );

				            dayTemps.setText( temps );	

				            if( i == 4 )
				            {
				                break;
				            }// end of if block
				        }// end of for loop
				
				        break;
				    case WeatherLionMain.YR_WEATHER:
	                    currentWindDirection.setLength( 0 );
	                    currentWindDirection.append(
	                            yr.getForecast().get( 0 ).getWindDirCode() );

	                    if( WeatherLionMain.storedPreferences.getUseMetric() )
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( yr.getForecast().get( 0 ).getTemperatureValue() );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( yr.getForecast().get( 0 ).getTemperatureValue() );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append( 
	                            Math.round(
	                                UtilityMethod.mpsToKmh( yr.getForecast().get( 0 ).getWindSpeedMps() ) ) );
	                    }// end of if block
	                    else
	                    {
	                        currentTemp.setLength( 0 );
	                        currentTemp.append( Math.round(
	                                UtilityMethod.celsiusToFahrenheit(
	                                        yr.getForecast().get( 0 ).getTemperatureValue() ) ) );

	                        currentFeelsLikeTemp.setLength( 0 );
	                        currentFeelsLikeTemp.append( Math.round(
	                                UtilityMethod.celsiusToFahrenheit(
	                                        yr.getForecast().get( 0 ).getTemperatureValue() ) ) );

	                        currentWindSpeed.setLength( 0 );
	                        currentWindSpeed.append(
	                            Math.round( UtilityMethod.mpsToMph( yr.getForecast().get( 0 ).getWindSpeedMps() ) ) );
	                    }// end of else block
						
						// Display weather data on widget
						lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );			        
						lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp + DEGREES );
						lblWindReading.setText( currentWindDirection +
						        " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ? " km/h" : " mph" ) );
									        
						List< YrWeatherDataItem.Forecast > fdf = yr.getForecast();
						
						// Five Day Forecast
						i = 1;
						float fHigh = 0;    // forecasted high
						float fLow = 0;     // forecasted low
						Date currentDate = new Date();
						dailyReading = new Hashtable< String, float[][] >();
						int x = 0;
						df = new SimpleDateFormat( "MMMM dd, yyyy" );
						String temps = null;
						
						// get the highs and lows from the forecast first
						for ( Forecast wxTempReading : fdf )
						{
							x++;
							
							if ( x == 1 )
						    {
						        currentDate = wxTempReading.getTimeFrom();
						        fHigh = (float) Math.round( UtilityMethod.celsiusToFahrenheit( wxTempReading.getTemperatureValue() ) );
						        fLow = (float) Math.round( UtilityMethod.celsiusToFahrenheit( wxTempReading.getTemperatureValue() ) );
						    }// end of if block
						
							// monitor date change
							if ( df.format( wxTempReading.getTimeFrom() ).equals( df.format( currentDate ) ) )
							{
							    float cr = (float) Math.round( UtilityMethod.celsiusToFahrenheit( wxTempReading.getTemperatureValue() ) );
							
							    if ( cr > fHigh )
							    {
							        fHigh = cr;
							    }// end of if block
							
								if (cr < fLow)
								{
								    fLow = cr;
								}// end of if block                     
							}// end of if block
							
							if ( wxTempReading.getTimePeriod() == 3 )
							{
							    x = 0;
							    float[][] hl = { { fHigh, fLow } };
							    dailyReading.put( df.format( wxTempReading.getTimeFrom() ), hl );
							}// end of if block
						}// end of first for each loop 
						
						x = 0;
						
						// repeat the loop and store the five day forecast
						for ( Forecast wxForecast : fdf )
						{
							x++;
							
							String fDate = df.format( wxForecast.getTimeFrom() );
							
							// the first time period is always the current reading for this moment
							if ( x == 1 )
							{
								fHigh = dailyReading.get( df.format( wxForecast.getTimeFrom() ) ) [ 0 ][ 0 ];
								fLow = dailyReading.get( df.format( wxForecast.getTimeFrom() ) ) [ 0 ][ 1 ];
								
								if( WeatherLionMain.storedPreferences.getUseMetric() )
								{
								    lblDayHigh.setText( fHigh + DEGREES );
								    lblDayLow.setText( fLow + DEGREES );
								    
								    fHigh = Math.round( 
											UtilityMethod.celsiusToFahrenheit( 
													dailyReading.get( df.format( wxForecast.getTimeFrom() ) )[ 0 ][ 0 ] ) );
									fLow = Math.round( 
											UtilityMethod.celsiusToFahrenheit( 
													dailyReading.get( df.format( wxForecast.getTimeFrom() ) )[ 0 ][ 1 ] ) );
								    
								    temps = String.format( "%s° %s°", (int) fLow, (int) fHigh );
								
								}// end of if block
								else
								{
								    if( fDate.equals( df.format( new Date() ) ) )
								    {
								    	currentHigh.setLength( 0 );
								    	currentHigh.append( (int) fHigh );
								    	
								    	currentLow.setLength( 0 );
								    	currentLow.append( (int) fLow );
								    	
								    	lblDayHigh.setText( currentHigh + DEGREES );
								        lblDayLow.setText( currentLow + DEGREES );
								    }// end of if block
									
								    temps = String.format( "%s° %s°", (int) fLow, (int) fHigh );
								}// end of else block
								
								JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i) + "Temps" );
								dayTemps.setText( temps );								
								
								if ( i == 5 )
								{
								    break;
								}// end of if block                   
							
								i++; // increment sentinel
							}// end of if block
						
							if ( wxForecast.getTimePeriod() == 3 )
							{
							    x = 0;
							}// end of if block  
						
						 }// end of second for each loop
						
						break;
				    default:
				        break;
				}// end of switch block
	    	}// end of if block
	    	else // if there is no Internet connection
	    	{
	    		tempUnits = WeatherLionMain.storedPreferences.getUseMetric() ? CELSIUS : FAHRENHEIT;

	            // get the root node of the XML document
	            xmlCurrent = rootNode.getChild( "Current" );
	            xmlForecast = rootNode.getChild( "DailyForecast" );
	            xmlForecastList = rootNode.getChild( "DailyForecast" ).getChildren( "DayForecast" );

	            // populate the global variables
	            currentWindDirection.setLength( 0 );
	            currentWindDirection.append( xmlWind.getChildText( "WindDirection" ) );

	            currentWindSpeed.setLength( 0 );
	            currentWindSpeed.append( xmlWind.getChildText( "WindSpeed" ) );

	            currentHumidity.setLength( 0 );
	            currentHumidity.append( xmlAtmosphere.getChildText( "Humidity" ) );

	            if( WeatherLionMain.storedPreferences.getUseMetric() )
	            {
	                currentTemp.setLength( 0 );
	                currentTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius(
	                        Float.parseFloat( xmlCurrent.getChildText( "Temperature" ) ) ) ) );

	                currentFeelsLikeTemp.setLength( 0 );
	                currentFeelsLikeTemp.append( Math.round( UtilityMethod.fahrenheitToCelsius(
	                        Float.parseFloat( xmlCurrent.getChildText( "FeelsLike" ) ) ) ) );

	                currentHigh.setLength( 0 );
	                currentHigh.append( Math.round( UtilityMethod.fahrenheitToCelsius(
	                    Float.parseFloat( xmlCurrent.getChildText( "HighTemperature" ) ) ) ) );

	                currentLow.setLength( 0 );
	                currentLow.append( Math.round( UtilityMethod.fahrenheitToCelsius(
	                    Float.parseFloat( xmlCurrent.getChildText( "LowTemperature" ) ) ) ) );

	                currentWindSpeed.setLength( 0 );
	                currentWindSpeed.append( 
	                    Math.round( UtilityMethod.mphToKmh( Float.parseFloat( xmlWind.getChildText( "WindSpeed" ) ) ) ) );
	            }// end of if block
	            else
	            {
	                currentTemp.setLength( 0 );
	                currentTemp.append( Math.round(
	                        Float.parseFloat( xmlCurrent.getChildText( "Temperature" ) ) ) );

	                currentFeelsLikeTemp.setLength( 0 );
	                currentFeelsLikeTemp.append( Math.round(
	                        Float.parseFloat( xmlCurrent.getChildText( "FeelsLike" ) ) ) );

	                currentHigh.setLength( 0 );
	                currentHigh.append( Math.round(
	                        Float.parseFloat( xmlCurrent.getChildText( "HighTemperature" ) ) ) );

	                currentLow.setLength( 0 );
	                currentLow.append( Math.round(
	                        Float.parseFloat( xmlCurrent.getChildText( "LowTemperature" ) ) ) );

	                currentWindSpeed.setLength( 0 );
	                currentWindSpeed.append( Math.round(
	                        Float.parseFloat( xmlWind.getChildText( "WindSpeed" ) ) ) );
	            }// end of else block

		        // Display weather data on widget
		        lblCurrentTemperature.setText( currentTemp.toString() + tempUnits );		        
		        lblFeelsLike.setText( FEELS_LIKE + " " + currentFeelsLikeTemp + DEGREES );		        
		        lblDayHigh.setText( currentHigh.toString() + DEGREES );
                lblDayLow.setText( currentLow.toString() + DEGREES );
		        lblWindReading.setText( currentWindDirection +
		                " " + currentWindSpeed + ( WeatherLionMain.storedPreferences.getUseMetric() ? " km/h" : " mph" ) );
		        			        
		        hl = new int[ 5 ][ 2 ];
			        
		        for ( i = 0; i <= xmlForecastList.size(); i++ )
		        {
		        	Element wxDailyForecast = xmlForecastList.get( i );
		        	Date forecastDate = null;
		        	
		        	try
		        	{
		        		forecastDate = new SimpleDateFormat( "EEE MMM dd HH:mm:ss z yyyy" ).parse( wxDailyForecast.getChildText( "Date" ) );
		        	}// end of try block
		        	catch ( ParseException e )
		        	{
		        		UtilityMethod.logMessage( "severe", "Couldn't parse the forcecast date!",
	        				thisClass.getEnclosingClass().getSimpleName() + "::updateTemps");
		        	}// end of catch block
		        	
		        	DateFormat df = new SimpleDateFormat( "dd MMM yyyy" );
		        	String fDate = df.format( forecastDate );
        			today = df.format( new Date() );

		            String temps;
		            String fh;
		            String fLow;

		            if( WeatherLionMain.storedPreferences.getUseMetric() )
		            {
		                lblDayHigh.setText( Math.round( UtilityMethod.fahrenheitToCelsius(
		                                Float.parseFloat( wxDailyForecast.getChildText( "HighTemperature" ) ) ) ) + DEGREES );
		                lblDayLow.setText( Math.round( UtilityMethod.fahrenheitToCelsius(
		                                Float.parseFloat( wxDailyForecast.getChildText( "LowTemperature" ) ) ) ) + DEGREES );

		                fh = String.valueOf( Math.round( UtilityMethod.fahrenheitToCelsius( Float.parseFloat( wxDailyForecast.getChildText( "HighTemperature" ) ) ) ) );
		                fLow = String.valueOf( Math.round( UtilityMethod.fahrenheitToCelsius( Float.parseFloat( wxDailyForecast.getChildText( "LowTemperature" ) ) ) ) );
		                temps = String.format( "%s° %s°", ( int ) Float.parseFloat( fLow ), ( int ) Float.parseFloat( fh ) );

		            }// end of if block
		            else
		            {
		                fh = wxDailyForecast.getChildText( "HighTemperature" );
		                fLow = wxDailyForecast.getChildText( "LowTemperature" );
		                
		                if( fh.equals( "" ) )
		                {
		                	fh = "0";
		                }// end of if block
		                else if( fLow.equals( "" )  )
		                {
		                	fLow = "0";
		                }// end of if block
		                
		                temps = String.format( "%s° %s°", ( int ) Float.parseFloat( fLow ), ( int ) Float.parseFloat( fh ) );
		            }// end of else block
			            			            
		            hl[i][0] = ( int ) Float.parseFloat( fh );
		            hl[i][1] = ( int ) Float.parseFloat( fLow );
		            
		            JLabel dayTemps = (JLabel) getComponentByName( "lblDay" + (i + 1) + "Temps" );
		            dayTemps.setText( temps );	            

		            if( i == 4 )
		            {
		                break;
		            }// end of if block
	 			}// end of for loop      
	    	}// end of else block
	    	
	    	// Update the color of the temperature label
	    	lblCurrentTemperature.setForeground(
	    			UtilityMethod.temperatureColor( Integer.parseInt(
	    					currentTemp.toString().replaceAll( "\\D+","" ) ) ) );
	    }// end of method updateTemps	    
	}// end of class WidgetUpdateService	
	
}// end of class WeatherLionWidget
