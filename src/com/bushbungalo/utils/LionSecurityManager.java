package com.bushbungalo.utils;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.bushbungalo.WeatherLionMain;
import com.bushbungalo.WeatherLionWidget;
import com.bushbungalo.WeatherLionWidget.WidgetUpdateService;


/**
 * @author Paul O. Patterson
 * @version     1.1
 * @since       1.1
 * 
 * <p>
 * This class is responsible for encryption and decryption functionality thus allowing 
 * the program to hide sensitive access within a local database storage. There are more 
 * robust ways of carrying out these security functionality so, this is just a simple 
 * approach for this home application.
 * </p>
 * 
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 04/04/19
 * <br />
 */

@SuppressWarnings("unused")
public class LionSecurityManager 
{
	public static Connection conn = ConnectionManager.getInstance().getConnection();;
	public static String databasePath = "res/storage/";
	public static File databaseDirectory = new File( databasePath );
	public static File databaseFile = new File( databasePath + WeatherLionMain.WAK_DATABASE_NAME );
	public static String announcement = "<html>Weather Lion can consume data from webservices such as:<br />"
			+ "Dark Sky Weather, Here Maps, Yahoo! Weather, Open Weather Map,<br />Weather Bit, Weather Underground,"
			+ " and Geonames.<br />In order for the program to us any of these providers, you must aquire key from each<br />"
			+ "services provider.<br /><br />The following URLs can be used to obtain access to the websites:<br />"
			+ "<ol><li><b>Dark Sky:</b> <a href=\"https://darksky.net/dev/\">https://darksky.net/dev</a></li>" 
			+ "<li>Geonames: <a href=\"http://www.geonames.org/\">http://www.geonames.org/</a><br /></li>"
			+ "<li>Here Maps: <a href=\"https://developer.here.com/\">https://developer.here.com/</a></li>"
			+ "<li>Open Weather Map: <a href=\"https://openweathermap.org/api\">https://openweathermap.org/api</a></li>"
			+ "<li>Weather Bit: <a href=\"https://www.weatherbit.io/api\">https://www.weatherbit.io/api</a></li>"
			+ "<li>Yahoo Weather: <a href=\"https://developer.yahoo.com/weather/\">https://developer.yahoo.com/weather/</a></li></ol>"
			+ "<br />The program will be able to display weather from Yr.no (Norwegian Metrological Institute)<br /> as they don't require a key.<br />"
			+ "<br /><p style='color: red;'><b>**Access must be supplied for any the specified weather providers and a username to use "
			+ "<br />the geonames website (<a href=\\\"http://www.geonames.org/\\\">http://www.geonames.org/</a>) for city search.</b></p></html>";
	
	private static Cipher ecipher;
	private static Cipher dcipher;
	
	private static JDialog frmKeys;
	private static JLabel lblAccessProvider;
	private static JLabel lblKeyName;
	private static JLabel lblKeyValue;
	private static JButton btnAdd;
	private static JButton btnDeleteKey;
	private static JButton btnFinish;
	private static JComboBox< String > cboAccessProvider;
	private static JTextField txtKeyName;
	private static JPasswordField pwdKeyValue;
	private static JCheckBox chkShowHidePwd;
	
	// right click pop-up
	private static JPopupMenu popRightClick;
	
	// right click menu items
	private static JMenuItem cutItem;
	private static JMenuItem copyItem;
	private static JMenuItem pasteItem;
	private static JMenuItem deleteItem;
	private static JMenuItem selectAllItem;
	
	private static JComponent caller;
	private static JDialog owner;
	
	private static String[] darkSkyRequiredKeys = new String[] { "api_key" };
	private static String[] geoNamesRequiredKeys = new String[] { "username" };
	private static String[] hereMapsRequiredKeys = new String[] { "app_id", "app_code" };
	private static String[] openWeatherMapRequiredKeys = new String[] { "api_key" };
	private static String[] weatherUndergroundRequiredKeys = new String[] { "api_key" };
	private static String[] yahooRequiredKeys = new String[] { "app_id", "consumer_key", "consumer_secret" };
	@Deprecated
	private static String[] weatherBitRequiredKeys = new String[] { "api_key" };
	
	private static DefaultComboBoxModel< String > weatherProviders;
	private static List< String > keysMissing;
	private static int answer;
	
	private static Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private static StringSelection data;
	private static boolean selectAll = false;
	
	public static ArrayList< String > webAccessGranted;
	
	// this flag will inform the program that valid access keys have been loaded
	public static boolean accessGranted = false;
	
	// the program utilizes the services of 
	private static boolean geoNamesAccountLoaded; 
	
	/**
	 * No argument, default constructor
	 */
	public LionSecurityManager() 
	{
	}// end of default constructor
	
	/**
	 * One-argument constructor
	 * 
	 * @param parent The parent component of any dialog boxes display from the class
	 */
	public LionSecurityManager( JDialog parent )
	{
		LionSecurityManager.owner = parent;
				
		init(); 
	}// end of one-argument constructor
	
	/**
	 * Initialize all the components of this class
	 */
	public static void init() 
	{
		createUserForm();
		
		// create the right-click pop-up menu
		createUserFormPopupMenu();
		
		// create all necessary files if they are not present
		if( !databaseDirectory.exists() ) 
		{
			databaseDirectory.mkdirs();
			
			try
			{
				databaseFile.createNewFile();				
			}// end of try black 
			catch ( IOException e )
			{
				e.printStackTrace();
			}// end of catch block
		}// end of if block
		
		if( !databaseFile.exists() )
		{
			try
			{
				databaseFile.createNewFile();
				UtilityMethod.createWSADatabase();
				frmKeys.setVisible( true );
			}// end of try block
			catch ( IOException e )
			{
				UtilityMethod.logMessage( "severe" , e.getMessage(), "LionSecurityManager::init" );
			}// end of catch block
		}// end of if block
		else if( UtilityMethod.checkIfTableExists( "wak", "access_keys" ) == 0 )
		{
			UtilityMethod.createWSADatabase();
			UtilityMethod.msgBox( announcement, 
				WeatherLionMain.PROGRAM_NAME + " - IMPORTANT",
					JOptionPane.INFORMATION_MESSAGE, null );
		}// end of else if block
		
		loadAccessProviders();
	}// end of method init
	
	/**
 	 * Saves a city to a local SQLite 3 database.
 	 * 
 	 * @param keyProvider  The name of the web service that supplies the key
 	 * @param keyName      The name of the key
 	 * @param keyValue     The value of the key
 	 * @param hex          A value used in the encryption process
 	 * @return             An {@code int} value indicating success or failure.<br /> 1 for success and 0 for failure.
 	 */
 	public static int addSiteKeyToDatabase( String keyProvider, String keyName, String keyValue,
 			String hex ) 
	{
 		// Redundancy check
 		if( UtilityMethod.checkIfTableExists( "wak", "access_keys" ) == 0 )
		{
			String tableSQL = 
					"CREATE table IF NOT EXISTS wak.access_keys "
					+ "(KeyProvider TEXT, KeyName TEXT, KeyValue TEXT(64), Hex TEXT)";
			
			try
			(
				Statement stmt = conn.createStatement();
			)
			{
				stmt.executeUpdate( tableSQL );    
			}// end of try block
			catch( SQLException e )
			{
			}// end of catch block
		}// end of if block
 	 		
 		int affected_rows = 0;
		String SQL = "INSERT INTO access_keys ( KeyProvider, KeyName, KeyValue, Hex ) VALUES ( ?, ?, ?, ? )";
				
		try(
	            PreparedStatement stmt = conn.prepareStatement( 
	            		SQL,
	            		ResultSet.TYPE_FORWARD_ONLY,
	            		ResultSet.CONCUR_READ_ONLY );
	    )
        {
			stmt.setString( 1, keyProvider );
			stmt.setString( 2, keyName );
			stmt.setString( 3, keyValue );
			stmt.setString( 4, hex );
			affected_rows = stmt.executeUpdate();
			
			return affected_rows;
        }// end of try block
		catch ( SQLException e )
		{
			return 0;
		}// end of catch block
	}// end of method addSiteKeyToDatabase
 	 	
 	/**
 	 * Checks to see if any provider stored in the database is missing a key 
 	 * that is required.
 	 */
 	private static int checkForMissingKeys() 
	{
		String mks = keysMissing.toString().replaceAll( "[\\[\\](){}]", "" );
		String fMks = null;
		
		if( UtilityMethod.numberOfCharacterOccurences( ',', mks ) > 1 )
		{
			fMks = UtilityMethod.replaceLast( ",", ", and", mks );
		}// end of if block
		else if( UtilityMethod.numberOfCharacterOccurences( ',', mks ) == 1 )
		{
			fMks = mks.replace( ",", " and" );
		}// end of else block
		else
		{
			fMks = mks;
		}// end of else block
		
		String prompt = "Yahoo! Weather requires the following missing " +
						( keysMissing.size() > 1 ? "keys" : "key" ) + ":\n"
						+ fMks + "\nDo you wish to add " + 
						( keysMissing.size() > 1 ? "them" : "it" ) + " now?";
		int result = UtilityMethod.responseBox( prompt, WeatherLionMain.PROGRAM_NAME + " - Add Missing Key", 
				new String[] { "Yes", "No" }, JOptionPane.QUESTION_MESSAGE, null );
		
		return result;
			
	}// end of message checkForMissingKeys
 	
 	/**
 	 * Close the {@code JDialog} window
 	 */
	private static void closeWindow() 
	{
		if( WeatherLionWidget.frmWeatherWidget != null )
		{
			if ( WeatherLionWidget.frmWeatherWidget.isVisible() )
			{
				frmKeys.dispose();
			}// end of if block
			else
			{
				System.exit( 0 );	// terminate the program
			}// end of else block
		}// end of if block
		else
		{
			frmKeys.dispose();
		}// end of else block
		
	}// end of method closeWindow
	
	/***
	 * Creates a right-click menu for the text fields.
	 */
	private static void createUserFormPopupMenu()
	{
		PopupMenuHandler pmh = new PopupMenuHandler();
		
		//
		// txaPopup
		//
		popRightClick = new JPopupMenu();
		
		cutItem = new JMenuItem( "Cut" );
	    cutItem.addActionListener( pmh );
	    cutItem.setIcon( new ImageIcon( "res/assets/img/icons/cut.png" ) );
	    popRightClick.add( cutItem );	    
	        
	    copyItem = new JMenuItem( "Copy" );
	    copyItem.addActionListener( pmh );
	    copyItem.setIcon( new ImageIcon( "res/assets/img/icons/copy.png" ) );
	    popRightClick.add( copyItem );	
	    
	    pasteItem = new JMenuItem( "Paste" );
	    pasteItem.addActionListener( pmh );
	    pasteItem.setIcon( new ImageIcon( "res/assets/img/icons/paste.png" ) );
	    popRightClick.add( pasteItem );
	    
	    deleteItem = new JMenuItem( "Delete" );
	    deleteItem.addActionListener( pmh );
	    deleteItem.setIcon( new ImageIcon( "res/assets/img/icons/delete.png" ) );
	    popRightClick.add( deleteItem );
	    
	    selectAllItem = new JMenuItem( "Select All" );
	    selectAllItem.addActionListener( pmh );
	    selectAllItem.setIcon( new ImageIcon( "res/assets/img/icons/select_all.png" ) );
	    popRightClick.add( selectAllItem );
	    
	    txtKeyName.addMouseListener( pmh );
	    pwdKeyValue.addMouseListener( pmh );
	}// end of method createUserFormPopupMenu
	
	/**
	 * Create the main {@code JDialog} window
	 */
 	private static void createUserForm()
	{
		//
		// frmKeys
		//
		frmKeys = new JDialog( owner );
		frmKeys.setIconImage( UtilityMethod.createImage( "res/assets/img/icons/icon.png" ).getImage() );
		frmKeys.setResizable( false );
		frmKeys.setSize( 408, 210 );
		frmKeys.getContentPane().setFont( new Font( "Arial", Font.PLAIN, 18 ) );
		frmKeys.getContentPane().setEnabled( true );
		frmKeys.getContentPane().setLayout( null );
		frmKeys.setModal( true );
		frmKeys.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		frmKeys.setTitle( "Data Access Key Entry" );
		frmKeys.setLocationRelativeTo( owner );
		
		frmKeys.addWindowListener( new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing( WindowEvent windowEvent )
		    {
		    	closeWindow();
		    }
		});
		
		//
		// lblAccessProvider
		//
		lblAccessProvider = new JLabel( "Access Provider:" );
		lblAccessProvider.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		lblAccessProvider.setBounds( 16, 22, 100, 14 );
		
		//
		// lblKeyName
		//
		lblKeyName = new JLabel( "Key Name:" );
		lblKeyName.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		lblKeyName.setBounds( 16, 44, 70, 32 );
		
		//
		// lblKey Value
		//
		lblKeyValue = new JLabel( "Key Value:" );
		lblKeyValue.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		lblKeyValue.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
		lblKeyValue.setBounds( 16, 72, 70, 36 );
		
		//
		// txtKeyName
		//
		txtKeyName = new JTextField();
		txtKeyName.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		txtKeyName.setBounds( 118, 48, 260, 26 );
				
		//
		// pwdKeyValue
		//
		pwdKeyValue = new JPasswordField();
		pwdKeyValue.setBounds( 118, 78, 260, 26 );
		pwdKeyValue.setEchoChar( '*' );
		
		//
		//  chkShowHidePwd
		//
		 chkShowHidePwd = new JCheckBox( "Show Key" );
		 chkShowHidePwd.setBounds( 298, 104, 120, 26 );
		 chkShowHidePwd.setSelected( false );
		 
		 chkShowHidePwd.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged( ItemEvent e ) 
				{
					if( e.getStateChange() == ItemEvent.SELECTED )
					{
						pwdKeyValue.setEchoChar( (char) 0 );
					}// end of if block
					else 
					{
					    pwdKeyValue.setEchoChar( '*' );
					}// end of else block
				}			
			});
		
		//
		// btnAddKey
		//
		btnAdd = new JButton( "Add Key" );
		btnAdd.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		btnAdd.setBounds( 69, 130, 100, 30 );
		btnAdd.setMnemonic( KeyEvent.VK_A );
		
		btnAdd.addMouseListener( new MouseAdapter()
		{			
			@Override
			public void mousePressed( MouseEvent e )
			{
				if( txtKeyName == null || txtKeyName.getText().length() == 0
					|| txtKeyName.getText().equals( "" ) )
				{
					UtilityMethod.msgBox( "Please enter a valid key name as given by the provider!",
						WeatherLionMain.PROGRAM_NAME + " - No Key Name", JOptionPane.ERROR_MESSAGE, null );
					txtKeyName.requestFocus();
				}// end of if block
				else if( pwdKeyValue == null || pwdKeyValue.getPassword().length == 0
						|| pwdKeyValue.getPassword().toString().equals( "" ) )
				{
					UtilityMethod.msgBox( "Please enter a valid key value as given by the provider!",
						WeatherLionMain.PROGRAM_NAME + " - No Key Value", JOptionPane.ERROR_MESSAGE, null );
					pwdKeyValue.requestFocus();
				}// end of else if block
				else
				{
					char[] rawKey = pwdKeyValue.getPassword();	// password text fields are usually character arrays
	                String keyString = new String( rawKey );	 // convert the character array to a string
					String[] encryptedKey = encrypt( keyString );
					
					answer = -1; // reset the field because it must be reused
					
					String selectedProvider = cboAccessProvider.getSelectedItem().toString();
					
					switch ( selectedProvider )
					{
						case "Here Maps Weather":
							if( !Arrays.asList( hereMapsRequiredKeys ).contains( txtKeyName.getText().toLowerCase() ) )
							{
								UtilityMethod.msgBox( "The " + selectedProvider +
										" does not require a key \"" + txtKeyName.getText() + "\"!",
										"Invalid Key Type",
										JOptionPane.ERROR_MESSAGE,
										null );
								txtKeyName.requestFocusInWindow();
								
								return;
							}// end of if block 
							
							break;
						case "Yahoo! Weather":
							if( !Arrays.asList( yahooRequiredKeys ).contains( txtKeyName.getText().toLowerCase() ) )
							{
								UtilityMethod.msgBox( "The " + selectedProvider +
										" does not require a key \"" + txtKeyName.getText() + "\"!",
										"Invalid Key Type",
										JOptionPane.ERROR_MESSAGE,
										null );
								txtKeyName.requestFocusInWindow();
								
								return;
							}// end of if block
							
							break;
						default:
							break;
					}// end of switch block
					
					if( addSiteKeyToDatabase( cboAccessProvider.getSelectedItem().toString(), 
							txtKeyName.getText(), encryptedKey[ 0 ], encryptedKey[ 1 ] ) == 1 )
					{
						if( txtKeyName.isEnabled() ) txtKeyName.setText( "" );
						pwdKeyValue.setText( "" );
						
						UtilityMethod.msgBox( "The key was successfully added to the database.",
								WeatherLionMain.PROGRAM_NAME + " - Success", JOptionPane.INFORMATION_MESSAGE, null );
						txtKeyName.requestFocus();
					}// end of if block
					else
					{
						UtilityMethod.msgBox( "The key could not be added to the database!"
							+ "\nPlease recheck the key and try again.",
								WeatherLionMain.PROGRAM_NAME + " - Error", JOptionPane.ERROR_MESSAGE, null );
						txtKeyName.requestFocus();
					}// end of else block
				}// end of else block
			}
		});
				
		//
		// btnDeleteKey
		//
		btnDeleteKey = new JButton( "Delete Key" );
		btnDeleteKey.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		btnDeleteKey.setBounds( 174, 130, 100, 30 );
		btnDeleteKey.setMnemonic( KeyEvent.VK_D );
		
		btnDeleteKey.addMouseListener(new MouseAdapter()
		{			
			@Override
			public void mousePressed( MouseEvent e )
			{
				if( txtKeyName == null || txtKeyName.getText().length() == 0
						|| txtKeyName.getText().equals( "" ) )
				{
					UtilityMethod.msgBox( "Please enter a valid key name as given by the provider!",
							WeatherLionMain.PROGRAM_NAME + " - No Key Name", JOptionPane.ERROR_MESSAGE, null );
					txtKeyName.requestFocus();
				}// end of if block
				else
				{
					 // confirm that user really wishes to delete the key
					String prompt = "Are you sure that you wish to delete the " +
									txtKeyName.getText() + "\nkey assinged by " + 
									cboAccessProvider.getSelectedItem() + "?\n" +
									"This cannot be undone!";
					int result = UtilityMethod.responseBox( prompt, WeatherLionMain.PROGRAM_NAME + " Delete Key", 
							 new String[] { "Yes", "No" }, JOptionPane.QUESTION_MESSAGE, null );
					
					if( result == JOptionPane.YES_OPTION )
					{
						if( deleteSiteKeyFromDatabase( cboAccessProvider.getSelectedItem().toString(),
								txtKeyName.getText() ) == 1 )
						{
							UtilityMethod.msgBox( "The " + cboAccessProvider.getSelectedItem() + 
									" " + txtKeyName.getText() + " has been removed from the database.",
									WeatherLionMain.PROGRAM_NAME + " - Success", JOptionPane.INFORMATION_MESSAGE, null );
							txtKeyName.requestFocus();
						}// end of if block
						else 
						{
							UtilityMethod.msgBox( "An error occured while removing the " + cboAccessProvider.getSelectedItem() + 
									" " + txtKeyName.getText() + " from the database!"
									+ "\nPlease check the Key Provider and Key Name specified and try again.",
									WeatherLionMain.PROGRAM_NAME + " - Deletion Failed", JOptionPane.ERROR_MESSAGE, null );
							txtKeyName.requestFocus();
						}// end of else block
						
					}// end of if block
					else 
					{
						UtilityMethod.msgBox( "Key deletion aborted!",
							WeatherLionMain.PROGRAM_NAME + " - Deletion Aborted", JOptionPane.INFORMATION_MESSAGE, null );
						txtKeyName.requestFocus();
					}// end of else block
					
				}// end of else block
			}
		});
		
		//
		// btnFinish
		//
		btnFinish = new JButton( "Finish" );
		btnFinish.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		btnFinish.setBounds( 278, 130, 100, 30 );
		btnFinish.setMnemonic( KeyEvent.VK_F );
		
		btnFinish.addMouseListener( new MouseAdapter()
		{			
			@Override
			public void mousePressed( MouseEvent e )
			{
				loadAccessProviders();
				
				if( keysMissing != null )
				{
					if( keysMissing.size() > 0 )
					{
						answer = checkForMissingKeys();
						accessGranted = false;

						if( answer == JOptionPane.YES_OPTION ) 
						{
							if( !frmKeys.isVisible() )
							{
								frmKeys.setVisible( true );	
							}// end of if block
							else
							{
								txtKeyName.requestFocusInWindow();
							}// end of else block
						}
						else
						{
							String wp = null;
							String[] keys = null;
							
							if( keysMissing.size() > 1 )
							{
								ArrayList< String > missingHereKeys = new ArrayList<>();
								ArrayList< String > missingYahooKeys = new ArrayList<>();
								
								for ( String keyName : keysMissing )
								{
									if( Arrays.asList( hereMapsRequiredKeys ).contains( keyName ) )
									{
										missingHereKeys.add( keyName );
									}// end of if block
									else if( Arrays.asList( hereMapsRequiredKeys ).contains( keyName ) )
									{
										missingYahooKeys.add( keyName );
									}// end of else if block
								}// end of for each loop
								
								if( missingHereKeys.size() > 0 )
								{
									String hKeys = missingHereKeys.toString().replaceAll( "[\\[\\](){}]", "" );
									String fs = null;
									
									if( UtilityMethod.numberOfCharacterOccurences( ',', hKeys ) > 1 )
									{
										fs = UtilityMethod.replaceLast( ",", ", and", hKeys );
									}// end of if block
									else if( UtilityMethod.numberOfCharacterOccurences( ',', hKeys ) == 1 )
									{
										fs = hKeys.replace( ",", " and" );
									}// end of else block
									else
									{
										fs = hKeys;
									}// end of else block
									
									wp = WeatherLionMain.HERE_MAPS;
									
									UtilityMethod.msgBox(  wp + " cannot be used as a weather source without\n"
											+ "first adding the missing " + ( missingHereKeys.size() > 1 ? "keys" : "key" )
											+ fs + ".", WeatherLionMain.PROGRAM_NAME + " - Missing Key",
											JOptionPane.INFORMATION_MESSAGE, null );
									
								}// end of if block
								
								if( missingYahooKeys.size() > 0 )
								{
									String hKeys = missingYahooKeys.toString().replaceAll( "[\\[\\](){}]", "" );
									String fs = null;
									
									if( UtilityMethod.numberOfCharacterOccurences( ',', hKeys ) > 1 )
									{
										fs = UtilityMethod.replaceLast( ",", ", and", hKeys );
									}// end of if block
									else if( UtilityMethod.numberOfCharacterOccurences( ',', hKeys ) == 1 )
									{
										fs = hKeys.replace( ",", " and" );
									}// end of else block
									else
									{
										fs = hKeys;
									}// end of else block
									
									wp = WeatherLionMain.YAHOO_WEATHER;
									
									UtilityMethod.msgBox(  wp + " cannot be used as a weather source without\n"
											+ "first adding the missing " + ( missingYahooKeys.size() > 1 ? "keys" : "key" )
											+ fs + ".", WeatherLionMain.PROGRAM_NAME + " - Missing Key",
											JOptionPane.INFORMATION_MESSAGE, null );
									
								}// end of if block
							}// end of if block
						}// end of else block
					}// end of if block
				}// end of if block
				
				if( webAccessGranted.size() >= 1 &&  !webAccessGranted.contains( "GeoNames" ) ) 
				{
					UtilityMethod.msgBox( "This program requires a geonames username"
							+ " which was not stored in the database.\nIT IS FREE!",
							"Missing Key", JOptionPane.ERROR_MESSAGE, null );
				}// end of else if block
				else if( webAccessGranted.size() == 2 && webAccessGranted.contains( "GeoNames" ) && 
						webAccessGranted.contains( "Yr.no (Norwegian Metrological Institute)" ) )
				{
					UtilityMethod.msgBox( "The program will only display weather data from"
							+ " Yr.no (Norwegian Metrological Institute).\nObtain access keys for"
							+ " the others if you wish to use them.",
							"Single Weather Provider", JOptionPane.INFORMATION_MESSAGE, null );
					accessGranted = true;
					closeWindow();
				}// end of else if block
				else 
				{
					accessGranted = true;
					closeWindow();
				}// end of else block
			}
		});
		
		//
		// cboAccessProvider
		//
		
		// Load only the providers who require access keys
		ArrayList< String > wxOnly =
				new ArrayList< String >( Arrays.asList( WeatherLionMain.providerNames ) );
		
		Collections.sort( wxOnly );	// sort the list
		
		// GeoNames is not a weather provider so it cannot be select here
		if( wxOnly.contains( "Yr.no (Norwegian Metrological Institute)" ) )
			wxOnly.remove( "Yr.no (Norwegian Metrological Institute)" );
		
		String[] accessNeededProviders = wxOnly.toArray( new String[ 0 ] );
		weatherProviders = new DefaultComboBoxModel< String >( accessNeededProviders );
		cboAccessProvider = new JComboBox< String >();
		cboAccessProvider.setFont( new Font( "Tahoma", Font.PLAIN, 13 ) );
		cboAccessProvider.setEditable( false );
		cboAccessProvider.setModel( weatherProviders );
		cboAccessProvider.setBounds( 118, 18, 260, 26 );
		
		cboAccessProvider.addActionListener( new ActionListener() 
		{			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( e.getActionCommand().equals( "comboBoxChanged" ) )
				{
					selectedProvider();
				}// end of if block
			}
		});
								
		// add components
		frmKeys.getContentPane().add( lblAccessProvider );
		frmKeys.getContentPane().add( lblKeyName );
		frmKeys.getContentPane().add( lblKeyValue );
		frmKeys.getContentPane().add( cboAccessProvider );
		frmKeys.getContentPane().add( txtKeyName );
		frmKeys.getContentPane().add( pwdKeyValue );
		frmKeys.getContentPane().add( chkShowHidePwd );
		frmKeys.getContentPane().add( btnAdd );
		frmKeys.getContentPane().add( btnDeleteKey );
		frmKeys.getContentPane().add( btnFinish );
		
		// check the selected provider and add the default API key where necessary
		selectedProvider();
	}// end of method createUserForm
 	
 	/***
 	 * Remove a key that is stored in the local database
 	 * 
 	 * @param keyProvider  The name of the web service that supplies the key
 	 * @param keyName The name of the key
 	 * @return An {@code int} value indicating success or failure.<br /> 1 for success and 0 for failure.
 	 */
 	public static int deleteSiteKeyFromDatabase( String keyProvider, String keyName ) 
 	{
 		int affected_rows = 0;
		String SQL = "DELETE FROM access_keys WHERE KeyProvider = ? AND keyName = ?";
				
		try(
	            PreparedStatement stmt = conn.prepareStatement( 
	            		SQL,
	            		ResultSet.TYPE_FORWARD_ONLY,
	            		ResultSet.CONCUR_READ_ONLY );
	    )
        {
			stmt.setString( 1, keyProvider );
			stmt.setString( 2, keyName );
			affected_rows = stmt.executeUpdate();
			
			return affected_rows;
        }// end of try block
		catch ( SQLException e )
		{
			return 0;
		}// end of catch block
 	}// end of method deleteSiteKeyFromDatabase
 	
 	/***
 	 * Encrypt a {@code String} using DES Encryption
 	 *  
 	 * @param userKey A {@code String} value that the user wishes to encrypt 
 	 * @return A {@code String} array object containing the encrypted key and hex
 	 */
 	public static String[] encrypt( String userKey )
	{
		String[] strData = new String[ 2 ];
		
		try
		{
			String encKey = null;
			String res = null;
		    SecretKey secretKey;

		    secretKey = KeyGenerator.getInstance( "DES" ).generateKey();
		    ecipher = Cipher.getInstance( "DES" );
		    ecipher.init( Cipher.ENCRYPT_MODE, secretKey );
		    byte[] utf8 = userKey.getBytes( "UTF8" );
		    byte[] enc = ecipher.doFinal( utf8 );

		    enc = Base64.getEncoder().encode( enc );
		    res = new String( enc );

		    // Returning values 0 = Encrypted String 1 = Key For Storage
		    strData[ 0 ] = res;
		    byte[] keyBytes = secretKey.getEncoded();
		    encKey = new String( Base64.getEncoder().encode( keyBytes ), "UTF8" );
		    strData[ 1 ] = encKey;
		}// end of try block
		catch( Exception e )
		{
		    e.printStackTrace();
		}// end of catch block
		 
		return strData;
	}// end of method encrypt
	
 	/***
 	 * Decrypt a {@code String} the was encrypted using DES Encryption
 	 *  
 	 * @param encryptedKey An encrypted {@code String} value  
 	 * @param hKey A hex value used during the encryption  
 	 * @return A {@code String} value containing the decrypted {@code String}
 	 */
 	public static String decrypt( String encryptedKey, String hKey )
	{
		String strData = null;

		try 
		{
			SecretKey secretKey = new SecretKeySpec( Base64.getDecoder().decode(
					hKey.getBytes( "UTF8" ) ), "DES" );
		    dcipher = Cipher.getInstance( "DES" );
		    dcipher.init( Cipher.DECRYPT_MODE, secretKey );
		    byte[] dec = Base64.getDecoder().decode( encryptedKey.getBytes() );
		    byte[] utf8 = dcipher.doFinal( dec );
		    strData = new String( utf8, "UTF8" );
		}// end of try block
	    catch( Exception e )
		{
		    e.printStackTrace();
		}// end of catch block
		 
		return strData;
	}// end of method decrypt
 	
 	/**
 	 * Retrieves an encrypted access key from a local SQLite 3 database
 	 * 
 	 * @param keyProvider  The name of the web service that supplies the key
 	 * @return				An {@code Object} of the {@code CityData} custom class
 	 */
	public static ArrayList< String > getSiteKeyFromDatabase( String keyProvider )
	{
		String SQL = null;
		ArrayList< String > ak = new ArrayList< String >();
		
		if( keyProvider != null )
		{
			SQL = "SELECT KeyName, KeyValue, Hex FROM access_keys WHERE KeyProvider = ?";
		}// end of if block
		
		ResultSet rs = null;
		
		try(
				PreparedStatement stmt = conn.prepareStatement( 
							SQL,
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY );
		)
		{
			if( keyProvider != null )
			{
				stmt.setString( 1, keyProvider );
			}// end of if block
			
			rs = stmt.executeQuery();			
			int found = 0;
			
			while ( rs.next() )
			{
				ak.add( rs.getString( "KeyName" )  + ":" + rs.getString( "KeyValue" ) +
						":" + rs.getString( "Hex" ) );
				found++;
			}// end of while loop
			
			if( found > 0 )
			{
				return ak;
			}// end of if block
			else
			{
				return null;
			}// end of else block
	    }// end of try block
		catch( SQLException e )
		{
			return null;
		}// end of catch block
	}// end of method getCityData
	
	/**
	 * Load all access providers stored in the database
	 */
	private static void loadAccessProviders() 
	{
		ArrayList<String> appKeys = new ArrayList< String >();
		webAccessGranted = new ArrayList< String >();
		
		for ( String provider : WeatherLionMain.providerNames )
		{
			 appKeys = getSiteKeyFromDatabase( provider );
				
			if( appKeys != null )
			{
				switch ( provider )
				{
					case "Dark Sky Weather":
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( darkSkyRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								WidgetUpdateService.darkSkyApiKey = decrypt( kv[ 1 ], kv[ 2 ] );
							}// end of if block
						}// end of for each loop
						
						if( WidgetUpdateService.darkSkyApiKey != null )
						{
							webAccessGranted.add( "Dark Sky Weather" );
							UtilityMethod.logMessage( "info", 
									"Dark Sky API key loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						
						break;
					
					case "GeoNames":
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( geoNamesRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								WidgetUpdateService.geoNameAccount = decrypt( kv[ 1 ], kv[ 2 ] );
							}// end of if block
						}// end of for each loop
						
						if( WidgetUpdateService.geoNameAccount != null )
						{
							webAccessGranted.add( "GeoNames" );
							geoNamesAccountLoaded = true;
							UtilityMethod.logMessage( "info", 
								"GeoNames user account loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						
						break;
					case "Open Weather Map":
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( openWeatherMapRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								WidgetUpdateService.openWeatherMapApiKey = decrypt( kv[ 1 ], kv[ 2 ] );
							}// end of if block
						}// end of for each loop

						if( WidgetUpdateService.openWeatherMapApiKey != null )
						{
							webAccessGranted.add( "Open Weather Map" );
							UtilityMethod.logMessage( "info", 
								"Open Weather Map key loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						
						break;
					case "Weather Bit":
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( weatherBitRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								WidgetUpdateService.weatherBitApiKey = decrypt( kv[ 1 ], kv[ 2 ] );
							}// end of if block
						}// end of for each loop
						
						if( WidgetUpdateService.weatherBitApiKey != null )
						{
							webAccessGranted.add( "Weather Bit" );
							UtilityMethod.logMessage( "info", 
								"Weather Bit key loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						
						break;
					case "Weather Underground":
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( weatherUndergroundRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								WidgetUpdateService.weatherUndergroundApiKey = decrypt( kv[ 1 ], kv[ 2 ] );
							}// end of if block
							
						}// end of for each loop
						
						if( WidgetUpdateService.weatherUndergroundApiKey != null )
						{
							webAccessGranted.add( "Weather Underground" );
							UtilityMethod.logMessage( "info", 
								"Weather Underground key loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						
						break;
					case "Here Maps Weather":
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( hereMapsRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								switch(  kv[ 0 ].toLowerCase() )
								{
									case "app_id":
										WidgetUpdateService.hereAppId = decrypt( kv[ 1 ], kv[ 2 ] );
										break;
									case "app_code":
										WidgetUpdateService.hereAppCode = decrypt( kv[ 1 ], kv[ 2 ] );
										break;
									default:
										break;
								}// end of switch block
							}// end of if block
						}// end of for each loop
						
						if( WidgetUpdateService.hereAppId != null && WidgetUpdateService.hereAppCode != null )
						{
							webAccessGranted.add( "Here Maps Weather" );
							UtilityMethod.logMessage( "info", 
								"Here Maps Weather keys loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						else if( WidgetUpdateService.hereAppId != null && WidgetUpdateService.hereAppCode == null )
						{
							UtilityMethod.msgBox( "Here Maps Weather requires an app_code which is"
								+ " not stored in the database.", 
									WeatherLionMain.PROGRAM_NAME + " - Missing Key", JOptionPane.ERROR_MESSAGE, null );
						}// end of if block
						else if( WidgetUpdateService.hereAppId == null && WidgetUpdateService.hereAppCode != null )
						{
							UtilityMethod.msgBox( "Here Maps Weather requires an app_id which is"
								+ " not stored in the database.", 
									WeatherLionMain.PROGRAM_NAME + " - Missing Key", JOptionPane.ERROR_MESSAGE, null );
						}// end of if block
						break;
					case "Yahoo! Weather":
						ArrayList<String> keysFound = new ArrayList<>();
						
						for ( String key : appKeys )
						{	
							String[] kv = key.split( ":" );
							
							if( Arrays.asList( yahooRequiredKeys ).contains( kv[ 0 ].toLowerCase() ) )
							{
								switch(  kv[ 0 ].toLowerCase() )
								{
									case "app_id":
										WidgetUpdateService.yahooAppId = decrypt( kv[ 1 ], kv[ 2 ] );
										keysFound.add( "app_id" );
										break;
									case "consumer_key":
										WidgetUpdateService.yahooConsumerKey = decrypt( kv[ 1 ], kv[ 2 ] );
										keysFound.add( "consumer_key" );
										break;
									case "consumer_secret":
										WidgetUpdateService.yahooConsumerSecret = decrypt( kv[ 1 ], kv[ 2 ] );
										keysFound.add( "consumer_secret" );
										break;
									default:
										break;
								}// end of switch block
							}// end of if block
						}// end of for each loop
						
						keysMissing = new LinkedList<String>( Arrays.asList( yahooRequiredKeys ) );
						keysMissing.removeAll( keysFound ); // remove all the keys found
						
						if( keysMissing.size() == 0 )
						{
							webAccessGranted.add( "Yahoo! Weather" );
							UtilityMethod.logMessage( "info", 
								"Yahoo! Weather keys loaded!", "SecurityManager::loadAccessProviders" );
						}// end of if block
						else 
						{
							// do not check for missing keys if the form is already displayed
							if( !frmKeys.isVisible() )
							{
								if( checkForMissingKeys() == JOptionPane.YES_OPTION )
								{
									cboAccessProvider.setSelectedItem( "Yahoo! Weather" );	
									frmKeys.setVisible( true );	
										txtKeyName.requestFocusInWindow();
								}// end of if block
								else
								{
									UtilityMethod.msgBox( "Yahoo! Weather cannot be used as a weather source without "
										+ "first adding the missing " + ( keysMissing.size() > 1 ? "keys" : "key" ) + ".",
											WeatherLionMain.PROGRAM_NAME + " - Missing Key", JOptionPane.INFORMATION_MESSAGE, null );
								}// end of else block
							}// end of if block
							else
							{
								txtKeyName.requestFocusInWindow();
							}// end of else block
						}// end of else block
						
						break;
					default:
						break;
				}// end of switch block
			}// end of if block
		}// end of outer for each loop
		
		// add the only weather provider that does not require a key
		webAccessGranted.add( WeatherLionMain.YR_WEATHER );
				
		if( webAccessGranted.size() > 0 ) 
		{
			String s = webAccessGranted.toString().replaceAll( "[\\[\\](){}]", "" );
			String fs = null;
			
			if( UtilityMethod.numberOfCharacterOccurences( ',', s ) > 1 )
			{
				fs = UtilityMethod.replaceLast( ",", ", and", s );
			}// end of if block
			else if( UtilityMethod.numberOfCharacterOccurences( ',', s ) == 1 )
			{
				fs = s.replace( ",", " and" );
			}// end of else block
			else
			{
				fs = s;
			}// end of else block
			
			UtilityMethod.logMessage( "info", 
				"The following access providers were loaded:\n" + fs + ".", "SecurityManager::loadAccessProviders" );
			
		}// end of if block
		else 
		{
			UtilityMethod.logMessage( "info", 
				"No valid access privelages were stored in the database!", "SecurityManager::loadAccessProviders" );
		}// end of else block
		
		if( webAccessGranted.size() == 0 )
		{
			if( noAccessPrivialgesStored() == JOptionPane.YES_OPTION )
			{
				if( !frmKeys.isVisible() )
				{
					frmKeys.setVisible( true );	
					txtKeyName.requestFocusInWindow();
				}// end of if block
				else
				{
					txtKeyName.requestFocusInWindow();
				}// end of else block
			}
			else
			{
				UtilityMethod.missingRequirementsPrompt( "Insufficient Access Privilages" );
			}// end of if block
		}// end of if block
		
		if( webAccessGranted.size() >= 1 &&  !geoNamesAccountLoaded ) 
		{
			UtilityMethod.logMessage(
				"severe", "GeoNames user name not found!", "LionSecurityManager::loadAccessProviders" );
			
			// confirm that user has a GeoNames account and want's to store it
			String prompt = "This program requires a geonames username\n" +
							"which was not stored in the database. IT IS FREE!" +
							"\nDo you wish to add it now?";
			int result = UtilityMethod.responseBox( prompt, WeatherLionMain.PROGRAM_NAME + " - Add Missing Key", 
						new String[] { "Yes", "No" }, JOptionPane.QUESTION_MESSAGE, null );
			
			if( result == JOptionPane.YES_OPTION )
			{
				if( !frmKeys.isVisible() )
				{
					cboAccessProvider.setSelectedItem( "GeoNames" );
					frmKeys.setVisible( true );
					pwdKeyValue.requestFocusInWindow();
				}// end of if block
				else
				{
					cboAccessProvider.setSelectedItem( "GeoNames" );
					pwdKeyValue.requestFocusInWindow();
				}// end of else block
			}// end of if block
			else 
			{
				UtilityMethod.missingRequirementsPrompt( "Insufficient Access Privilages" );
			}// end of else block
		}// end of else if block
		
		// if valid access was not loaded for the provider previously used, take it into account
		if( WeatherLionMain.storedPreferences != null )
		{
			if( !webAccessGranted.contains( WeatherLionMain.storedPreferences.getProvider() ) )
			{
				WeatherLionMain.noAccessToStoredProvider = true;
			}// end of if block
			else
			{
				WeatherLionMain.noAccessToStoredProvider = false;
			}// end of else block
		}// end of if block
		
	}// end of method loadAccessProcviders
	
	/**
	 * This confirmation dialog gives the user an opportunity to proved 
	 * access keys to the weather providers that they intend to use.
	 * 
	 * @return		An {@code int} value representing the user's response
	 */
	private static int noAccessPrivialgesStored() 
	{
		// check if the user wishes to provide some accounts for access
		// to weather services.
		int result = JOptionPane.showConfirmDialog( null,
					"The program will not run without access privialges!" +
					"\nDo you wish to add some now?", 
					"Add Access Privialges", JOptionPane.YES_NO_OPTION , 
					JOptionPane.QUESTION_MESSAGE );
		
		return result;
			
	}// end of message noAccessPrivialgesStored()
	
	/**
	 * Determine which weather access provider the user selected form the
	 * list of options.
	 */
	private static void selectedProvider()
	{
		String  selectedProvider = cboAccessProvider.getSelectedItem().toString();
		
		if( selectedProvider.equals( "GeoNames" ) )
		{
			txtKeyName.setEnabled( false );
			txtKeyName.setText( "username" );
			pwdKeyValue.requestFocus();
		}// end of if block
		else if( selectedProvider.equals( "Dark Sky Weather" ) ||
				 selectedProvider.equals( "Open Weather Map" ) ||
			     selectedProvider.equals( "Weather Bit" ) ||
			     selectedProvider.equals( "Weather Underground" ) )
		{
			txtKeyName.setEnabled( false );
			txtKeyName.setText( "api_key" );
			pwdKeyValue.requestFocus();
		}// end of if block	
		else 
		{
			txtKeyName.setEnabled( true );
			txtKeyName.setText( "" );
			txtKeyName.requestFocus();
		}// end of else block
	}// end of method selectedProvider
	
	/**
	 * 	Display user form after it has been loaded
	 */
	public void showForm()
	{
		// Create and display the form
        java.awt.EventQueue.invokeLater( new Runnable()
        {
            public void run()
            {
            	// show the form
        		frmKeys.setVisible( true );
        		
            }// end of method run
        });
				
	}// end of method showForm()
	
	/***
	 * Performs a copy operation.
	 */
	private static void copyText()
	{
		if( ( ( JTextComponent ) caller ).getSelectedText() != null )
		{
			data = new StringSelection( ( ( JTextComponent ) caller ).getSelectedText() );		
			clipboard.setContents( data, null );			
		}// end of if block		
	}// end of method copyText
	
	/***
	 * Performs a cut operation.
	 */
	private static void cutText()
	{
		selectAll = false;
			copyText();
				( ( JTextComponent ) caller ).setText( "" );			
	}// end of method cutText
	
	/***
	 * Performs a delete operation.
	 */
	private static void deleteText()
	{
		selectAll = false;
		
		if( ( ( JTextComponent ) caller ).getSelectedText() != null )
		{
			( ( JTextComponent ) caller ).replaceSelection( "" );			
		}// end of if block
	}// end of method copyText
	
	/***
	 * Performs a paste operation.
	 */
	private static void pasteText()
	{
		selectAll = false;
		String value = "";
		
		try 
		{
			value = 
					( String)  Toolkit.getDefaultToolkit().getSystemClipboard().getData( DataFlavor.stringFlavor );
			
			if( ( ( JTextComponent ) caller ).getText() == "" ) 
			{
				( ( JTextComponent ) caller ).setText( value );
			}// end of if block
			else if( ( ( JTextComponent ) caller ).getSelectedText() != null )
			{
				( ( JTextComponent ) caller ).replaceSelection( value );
			}// end of else if block
			else
			{
				( ( JTextComponent ) caller ).setText( ( ( JTextComponent ) caller ).getText() + value );
			}// end of else block
		}// end of try block
		catch ( HeadlessException he )
		{
			// ignore
		}// end of method HeadlessException catch block
		catch ( UnsupportedFlavorException ufe )
		{
			// ignore
		}// end of UnsupportedFlavorException catch block
		catch ( IOException ioe ) 
		{
			// ignore
		}// end of IOException catch block	
	}// end of method pasteText
	
	/***
	 * Select all the text in the text area.
	 */
	private static void selectAllText()
	{
		selectAll = true;
		( ( JTextComponent ) caller ).selectAll();
	}// end of method selectAllText
	
	/**
	 * Private inner class which will handle all the pop-up menu actions
	 * 
	 * @author Paul O. Patterson
	 * @version     1.1
	 * @since       1.1
	 */
	private static class PopupMenuHandler extends MouseAdapter implements ActionListener 
	{
		public void actionPerformed( ActionEvent e )
		{
			JMenuItem source = ( JMenuItem )( e.getSource() );
			
			switch ( source.getText() )
			{
				case "Cut":
					cutText();
				break;		
				case "Copy":
					copyText();
					break;
				case "Paste":
					pasteText();									
					break;
				case "Delete":
 					deleteText();
 					break;
				case "Select All":
					selectAllText();									
					break;
				default:
					break;
			}// end of switch		
		}// end of method actionPerformed
		
		public void mousePressed( MouseEvent me )
       	{
			caller = ( JComponent ) me.getSource();			
						
			if( caller.isEnabled() )
       		{
            	showPopup( me );
       		}// end of if block
        }// end of method mousePressed

        public void mouseReleased( MouseEvent me )
        {
        	caller = ( JComponent ) me.getSource();			
			
			if( caller.isEnabled() )
       		{
            	showPopup( me );
       		}// end of if block
        }// end of method mouseReleased

        private void showPopup( MouseEvent me )
        {
        	caller = ( JComponent ) me.getSource();
        	
        	if ( me.isPopupTrigger() )
            {
            	if( ( ( JTextComponent ) caller).getSelectedText() != null )
        		{
        			cutItem.setEnabled( true );			
        			copyItem.setEnabled( true );			
        			deleteItem.setEnabled( true );			
        		}// end of if block
            	else
            	{
            		cutItem.setEnabled( false );			
            		copyItem.setEnabled( false );			
            		deleteItem.setEnabled( false );
            	}// end of else block
            	
            	popRightClick.show( me.getComponent(),
			               me.getX(), me.getY()
			              );
            }// end of if block
        }// end of method showPopup
    }// end of inner class PopupMenuListener
}// end of class SecurityManager
