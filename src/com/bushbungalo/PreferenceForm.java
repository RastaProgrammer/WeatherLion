package com.bushbungalo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import com.bushbungalo.model.CityData;
import com.bushbungalo.services.CityStorageService;
import com.bushbungalo.utils.JSONHelper;
import com.bushbungalo.utils.UtilityMethod;

/**
 * @author Paul O. Patterson
 * @version     1.0
 * @since       1.0
 * <p>
 * This class is responsible for displaying a {@code JFrame} window 
 * allowing the user to modify various settings for the program.
 * </p> 
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>01/21/19 - Removed Yahoo! Weather as an option</li>
 * 		<li>01/13/19 - Added method {@link #calculateWindChill}</li>
 * 		<li>01/24/19 - Reinstated Yahoo! Weather as an option</li>
 * 		<li>01/28/19 - Icon set panel added</li>
 * 		<li>
 * 			01/29/19 
 * 				<ol>
 * 					<li>Added event handlers for icon tab radio buttons.</li>
 * 					<li>Updated state change listeners for all radio buttons to {@code addItemListener}
 * 					from {@code addChangeListener} due to unwanted and inefficient behaviour.</li>
 * 				</ol>
 * 		</li>
 * 		<li>
 * 			02/04/19 - Added methods {@link #loadInstalledIconPacks} and {@link #getInstalledIconPacks} to
 * 					   support dynamic loading of available icon packs.
 * 		</li>
 * 		<li>02/05/19 - Moved assets out of the jar file to eliminate path headaches.</li>
 *      <li>
 * 			05/11/19
 * 			<ol>
 * 				<li>05/11/19 - Changed {@code cboWeatherProviders}'s accessor to public so that it can be updated externally.</li>
 * 				<li>05/11/19 - Removed printing stack trace errors to console for logging</li>
 * 			</ol>
 * 		</li>
 * 		<li>06/28/19 - Added method {@link #storeNewLocationLocally}</li>
 *      <li>07/03/19 - Updated preference saving method.</li>
 * </ul>
 */

public class PreferenceForm
{
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 5004285498268440865L;
	
	private static enum Mode 
	{
	    INSERT,
	    COMPLETION
	};
	
	// Singleton instance of the user form
	private static PreferenceForm m_instance = null;	
	
	// form objects
	public static JDialog frmPreference;
	
	// layered pane
	public static JLayeredPane preferenceLayer;
	
	// buttons	
	public static JButton btnSearch; // the city data service needs access to this button
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnApply;
	
	// radio buttons
	private JRadioButton radDefault;
	private JRadioButton radAndroid;
	private JRadioButton radRabalac;
	
	 // labels
	private JLabel lblLocation;
	private JLabel lblLocationDescription;
	private JLabel lblData;
	private JLabel lblDataDescription;
	private JLabel lblProvider;
	private JLabel lblRefreshInterval;
	private JLabel lblRefreshIntervalDescription;
	private JLabel lblGeoLocation;	
	private JLabel lblTempUnits;	
	private JLabel lblInterval;	
	private JLabel lblAbout;	

	// text fields
	public static JTextField txtLocation;
	
	public static JList<String> jlMatches;
	public static JScrollPane matchesScrollPane;
	
	public static DefaultComboBoxModel< String > cityNames;
	
	// check boxes
	JCheckBox chkUseSystemLocation;
	JCheckBox chkUseMetric;
	
	// combo boxes
    public static JComboBox< String > cboWeatherProviders;	// expose this object to other classes
    private JComboBox< String > cboRefreshInterval;
    
    // default combo box models    
    private DefaultComboBoxModel< String > weatherProviders;
    private DefaultComboBoxModel< String > refreshIntervals;
    
    // tabbed pane
 	private JTabbedPane tpPreferences;
 	
 	// panels
 	private JPanel pnlWeather;
 	private JPanel pnlBackground;
 	private JPanel pnlIconSet;
 	private JPanel pnlAbout;

	protected int m_current_tab;	
 	
 	// constants
 	private static final String LOCATION_DATA_TEXT = "Type the city name for which the weather is required. Example Pine Hills or Kingston. After entering the city name, press the search button to locate the city then select the city from the drop down list when displayed.";
 	private static final String WEATHER_DATA_TEXT = "Weather providers get weather data from different internet sources. They can differ by number of locations and provide different data.";
 	
 	private static final String ABOUT_PROGRAM = "Weather Lion is an ongoing effort to create a desktop weather widget"
 			+ " using the Java programming language as well as others languages as I continue to grow as"
 			+ " a computer programmer.<br/><br/><br/><br/>Praise Ye JAH!!!!"; 
 	
 	private static final String SEARCH = "Search";
	private static final String  OK = "OK";
	private static final String CANCEL = "Cancel";
	private static final String APPLY = "Apply";
	
	private String[] updateIntervals = new String[] { "15", "30", "60" };
	
	private static final String COMMIT_ACTION = "commit";
	private JDialog owner;
	
	private JPanel pnlIconSelectionCtn;
	
	private ButtonGroup iconGroup;
	
	public static Border textBoxLine = BorderFactory.createMatteBorder( 1, 1, 1, 1,
			new Color( 171, 173, 179  ) );
	
	public static Border listBoxLine = BorderFactory.createMatteBorder( 0, 1, 1 , 1,
			new Color( 171, 173, 179  ) );
	
	public static Border focusTextBoxLine = BorderFactory.createMatteBorder( 1, 1, 1, 1,
			new Color( 0, 84, 153  ) );
	
	public static Border focusListBoxLine = BorderFactory.createMatteBorder( 0, 1, 1 , 1,
			new Color( 0, 84, 153  ) );
	
	public static Border bottom_border = BorderFactory.createMatteBorder( 0, 0, 1 , 0,
			new Color( 160, 160, 160 ) );
	
	// border used as padding
	public static Border paddingBorder = BorderFactory.createEmptyBorder( 2, 2, 2, 2 );
	public static Border margin = new EmptyBorder( 0, 2, 0, 0 ); // left padding
	public static String currentCity;
	public static StringBuilder searchCity = new StringBuilder();
	
	private static Color defaultSelectionColor;
	private static boolean clickAction;
	private static boolean hoverAction;
	private static boolean outsideListArea;
	private static int cityIndex;
	
	public static boolean locationSelected;
	
	/**
	 * One-Argument/Default constructor
	 * 
	 * @param parent The parent window for this {@code JDialog}
	 */
	public PreferenceForm( JDialog parent )
	{
		this.owner = parent;
		
		// build all the components that the user form will contain as well as the form itself
		InitializeComponents(); 
	}// end of default constructor
	
	/***
	 * Get the only instance of the user form.
	 * 
	 * @return An instance of the user form.
	 */
	public static PreferenceForm getInstance( JDialog parent )
	{
		if ( m_instance == null )
		{
			m_instance = new PreferenceForm( parent );
		}// end of if block
		
		return m_instance;
	}// end of method getInstance
	
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
            	loadKnownPlaces();
            	
            	// show the form
        		frmPreference.setVisible( true );
        		
            }// end of method run
        });
				
	}// end of method showForm()
	
	/**
	 * Handle the search button click
	 */
	private void btnSearch_Click()
	{
        btnSearch.setIcon( WeatherLionMain.LOADING_IMAGE );
        btnSearch.setText( "Searching..." );
        btnSearch.setEnabled( false );
		
		searchCity.setLength( 0 ); // clear any previous searches
		searchCity.append( txtLocation.getText() );
		
		if( searchCity.toString().trim().length() > 0 &&
				!UtilityMethod.isKnownCity( searchCity.toString() ) )
		{
			// ignore anything that comes after a comma
			if (searchCity.toString().contains(","))
            {                       
				searchCity.delete(searchCity.toString().indexOf(","), 
                    searchCity.length());
            }// end of if block
			
			UtilityMethod.findGeoNamesCity( searchCity.toString() );
		}// end of if block
	}// end of method btnSearch_Click
	
	/**
	 * Handle the Ok button click
	 */
	private void btnOk_Click()
	{
		// apply the location setting
		saveLocationPreference();
		
		// get rid of the form
		frmPreference.dispose();
	}// end of method btnOk_Click
	
	/**
	 * Handle the cancel button click
	 */
	private void btnCancel_Click()
	{
		WeatherLionWidget.applyPreferenceUpdates = false;
		frmPreference.dispose();
	}// end of method btnCancel_Click
	
	/**
	 * Handle the apply button click
	 */
	private void btnApply_Click()
	{
		// this flag will be updated after loading success
		WeatherLionWidget.dataLoadedSuccessfully = false;
		
		// stop the thread if it is currently running
		if( WeatherLionWidget.widgetThread != null ) 
		{
			WeatherLionWidget.widgetThread.interrupt();			
		}// end of if block
		
		// just for esthetics as the form will be disposed anyway
		if( matchesScrollPane.isVisible() )
		{
			matchesScrollPane.setVisible( false );
		}// end of if block
		
		WeatherLionWidget.applyPreferenceUpdates = true;
		saveLocationPreference();
	}// end of method btnApply_Click
	
	/**
	 * Load a list of previous place that were searched for
	 */
	private void loadKnownPlaces()
    {
		List< CityData > previousSearches = JSONHelper.importFromJSON();
		List< String > searchList = new ArrayList< String >();
		
		// when the program is first runs there will be no previous searches so
		// this function does nothing on the first run
		if( previousSearches != null )
		{
			for ( CityData city : previousSearches )
			{
				if( city.getRegionCode() != null && !UtilityMethod.isNumeric( city.getRegionCode() ) )
				{
					searchList.add( city.getCityName() + ", " + city.getRegionCode() );
				}// end of if block
				else
				{
					searchList.add( city.getCityName() + ", " + city.getCountryName() );
				}// end of else block
			}// end of for each loop
			
			Collections.sort( searchList );
			
			// Set the JTextfield as an auto complete object
			JTextfieldAutoComplete autoComplete = new JTextfieldAutoComplete( txtLocation, searchList );
			txtLocation.getDocument().addDocumentListener( autoComplete );
			
			// Maps the tab key to the commit action, which finishes the auto complete
			// when given a suggestion
			txtLocation.getInputMap().put( KeyStroke.getKeyStroke( "TAB" ), COMMIT_ACTION );
			txtLocation.getActionMap().put( COMMIT_ACTION, autoComplete.new CommitAction() );		
			
			String[] s = searchList.toArray ( new String[ searchList.size() ] );
			cityNames = new DefaultComboBoxModel< String >( s );
			
			jlMatches.setModel( cityNames );
			jlMatches.setVisibleRowCount( searchList.size() );
			jlMatches.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			matchesScrollPane.setBounds( txtLocation.getX(), txtLocation.getY() + txtLocation.getHeight(),
				txtLocation.getWidth(), jlMatches.getPreferredSize().height );
			
			jlMatches.addListSelectionListener( new ListSelectionListener()
			{
				@Override
				public void valueChanged( ListSelectionEvent e ) 
				{		
					// do nothing if there is no selection or the mouse hovered over an item selecting it
					if( hoverAction || jlMatches.isSelectionEmpty() ) return;
					
					txtLocation.setText( jlMatches.getSelectedValue() );
					cityIndex = jlMatches.getSelectedIndex();
					matchesScrollPane.setVisible( false );
				}
			});
			
			// select the first of the previous weather locations if one
			// was'nt stored in the preferences		
			if( WeatherLionMain.storedPreferences.getLocation() == null )
			{
				txtLocation.setText( jlMatches.getModel().getElementAt( 0 ) );		
			}// end of if block
			else
			{
				txtLocation.setText( WeatherLionMain.storedPreferences.getLocation() );	
			}// end of else block
		}// end of if block
    }// end of method loadKnownPlaces
	
	/**
	 * Save a location to the database
	 */
	private void saveLocationPreference() 
	{
		String currentLocation = null;
		
		// the location setting is the only one that does not get written
		// to the preferences files automatically so the user has to do this
		// explicitly but clicking OK or Apply.		
		if( !txtLocation.getText().equals( WeatherLionMain.storedPreferences.getLocation() ) &&
				txtLocation.getText().trim().length() > 0 ) 
		{
			// combine the city and the region as the current location
            final String[] location = txtLocation.getText().toString().split( "," );
            
            if( location.length > 0 )
            {
                // countries who have a region such as a state or municipality
                if( location.length > 2 )
                {
                    currentLocation = location[ 0 ].trim() + ", " + location[ 1 ].trim();
                }// end of if block
                else
                {
                    currentLocation = txtLocation.getText().toString().trim();
                }// end of else block			
                               
                // notify the widget of this update
				if( !WeatherLionWidget.preferenceUpdated.containsKey(
						WeatherLionMain.CURRENT_LOCATION_PREFERENCE ) ) 
				{
					WeatherLionWidget.preferenceUpdated.put( 
						WeatherLionMain.CURRENT_LOCATION_PREFERENCE, currentLocation );
					locationSelected = true;
				}// end of if block							
				
				if( WeatherLionWidget.frmWeatherWidget != null )
				{
					if( !WeatherLionWidget.frmWeatherWidget.isVisible() ) 
					{
						WeatherLionWidget.getInstance();					
					}// end of if block
				}// end of if block
			}// end of if block
		}// end of if block
		 else
         {
             currentLocation = txtLocation.getText();
         }// end of else block

         if (!UtilityMethod.isFoundInJSONStorage( currentLocation ) )
         {
        	 cityIndex = 0; // pick the first city found in the search by default
        	 
        	 if( jlMatches.getSelectedIndex() != -1 )
        	 {
        		 cityIndex = jlMatches.getSelectedIndex();
        	 }// end of if block
        	 
        	//run an background service
         	CityStorageService cs = new CityStorageService( cityIndex,
     			txtLocation.getText() );
         	cs.execute();
         }// end of if block
	}// end of method saveLocationPreference	
	
	/**
	 * Initialize the contents of the frmPreference.
	 */
	private void InitializeComponents()
	{
		// create the user form ( JFrame )
		createUserForm();
		
		// create all the components to be used on the user form ( JFrame )
		createUserFormComponents();		
		
		// setup button handling events
		setButtonHandlers();
		
		// add the created components to the user form
		addUserFormComponents();
		
		txtLocation.setText( WeatherLionMain.storedPreferences.getLocation() );
		cboWeatherProviders.setSelectedItem( WeatherLionMain.storedPreferences.getProvider() );
		
		// position the window center screen
		frmPreference.setLocationRelativeTo( null );
	}// end of method 
	
	/**
	 * Add all the components to the JFrame
	 */
	private void addUserFormComponents()
	{
		// add all JComponents to the JFrame
		frmPreference.getContentPane().add( btnOk );
		frmPreference.getContentPane().add( btnCancel );
		frmPreference.getContentPane().add( btnApply );
		frmPreference.getContentPane().add( tpPreferences );		
	}// end of method addFormComponents
	
	/***
	 * Creates the main user form that interfaces with the user.
	 */
	private void createUserForm()
	{
		//
		// frmPreference
		//
		frmPreference = new JDialog( owner );
		frmPreference.setIconImage( UtilityMethod.createImage( "res/assets/img/icons/icon.png" ).getImage() );
		frmPreference.setResizable( false );
		frmPreference.getContentPane().setFont( new Font( "Arial", Font.PLAIN, 18 ) );
		frmPreference.getContentPane().setEnabled( true );
		frmPreference.getContentPane().setLayout( null );
		frmPreference.setModal( true );
		frmPreference.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		frmPreference.setTitle( "Preferences" );
		
		frmPreference.addWindowListener( new WindowAdapter() 
		{
		    @Override
		    public void windowClosing( WindowEvent windowEvent )
		    {
		    	frmPreference.dispose();
		    }// end of method windowClosing
		});
		
		// scale the user form ( JFrame )
		frmPreference.setSize( 682, 726 );
			
	}// end of method createUserForm	
	
	/**
	 * Create all the buttons to be placed on the {@code JFrame}
	 */
	private void createUserFormButtons() 
	{
		btnSearch = new JButton( SEARCH );
		btnSearch.setFont( new Font( "Arial", Font.PLAIN, 13 ) );
		btnSearch.setToolTipText( "Find a matching city." );
		btnSearch.setBounds( 500, 54, 130, 28 );
		btnSearch.setMnemonic( KeyEvent.VK_S);
				
		btnOk = new JButton( OK );
		btnOk.setFont( new Font( "Arial", Font.PLAIN, 13 ) );
		btnOk.setToolTipText( "Complete transaction." );
		btnOk.setBounds( 302, 656, 116, 28 );
		btnOk.setMnemonic( KeyEvent.VK_K );		
		
		btnCancel = new JButton( CANCEL );
		btnCancel.setFont( new Font( "Arial", Font.PLAIN, 13 ) );
		btnCancel.setToolTipText( "Ignore any changes made." );
		btnCancel.setBounds( 424, 656, 116, 28 );
		btnCancel.setMnemonic( KeyEvent.VK_C );		
		
		btnApply = new JButton( APPLY );
		btnApply.setFont( new Font( "Arial", Font.PLAIN, 13 ) );
		btnApply.setToolTipText( "Apply all changes made." );
		btnApply.setBounds( 546, 656, 116, 28 );
		btnApply.setMnemonic( KeyEvent.VK_A );		
	}// end of method createFormButtons
	
	/**
	 * Create all the labels to be placed on the {@code JFrame}
	 */
	private void createUserFormLabels()
	{
		Color hotTrack = new Color( 0, 102, 204 );

		preferenceLayer = new JLayeredPane();
		preferenceLayer.setBounds( 0, 4, 672, 640 );
		
		lblLocation = new JLabel( "Location" );
		lblLocation.setFont( new Font( "Arial", Font.PLAIN, 24 ) );
		lblLocation.setForeground( hotTrack );
		lblLocation.setBounds( 8, 4 , 622, 34 );		
		lblLocation.setBorder( bottom_border );	
		preferenceLayer.add( lblLocation, 0 );
		
		txtLocation = new JTextField();
		txtLocation.setBounds( 8, 55, 485, 26 );
		txtLocation.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		txtLocation.setEditable( true );
		txtLocation.setFocusTraversalKeysEnabled( false );	
		txtLocation.setEnabled( !WeatherLionMain.storedPreferences.getUseSystemLocation() );	
		preferenceLayer.add( txtLocation, 0 );
		
		txtLocation.addFocusListener( new FocusAdapter() 
		{
			@Override
			public void focusGained( FocusEvent e )
			{
				if( matchesScrollPane.isVisible() ) matchesScrollPane.setVisible( false ); 
			}
		});
		
		preferenceLayer.add( btnSearch, 0 );
		
		jlMatches = new JList< String >( new String[] { "No match found..." } );		
		jlMatches.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		jlMatches.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		jlMatches.setOpaque( true );
		defaultSelectionColor = jlMatches.getSelectionBackground(); // keep the original color for later
		
		jlMatches.addMouseMotionListener(new MouseAdapter()
		{
			@SuppressWarnings("serial")
			public void mouseMoved(MouseEvent me)
			{
			    jlMatches.setCellRenderer( new DefaultListCellRenderer()
			    {
			    	Point p = new Point( me.getX(), me.getY() );
				    int ind = jlMatches.locationToIndex( p );
				    
			    	@Override
                    @SuppressWarnings("rawtypes")
                    public Component getListCellRendererComponent( JList list,
                            Object value, int index, boolean isSelected,
                            boolean cellHasFocus ) 
			    	{
			    		Component c = super.getListCellRendererComponent(list, value, index,
                                isSelected, cellHasFocus );
			    		
						StringBuilder listItemText = new StringBuilder();
						listItemText.setLength( 0 );
						
						
						if( outsideListArea ) ind = -1;
						
						if ( ind != -1 && !clickAction )
						{
							jlMatches.setSelectionBackground( new Color( 204, 228, 247 ) );
							jlMatches.setSelectionForeground( Color.BLACK );
							listItemText.append( jlMatches.getModel().getElementAt( ind ) );
							list.setSelectedIndex( ind );
						}// end of if block
			    		
                        return c;
                    }
                });
			  }
			});
		
		jlMatches.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				hoverAction = false;
				clickAction = true;
				jlMatches.setSelectionForeground( Color.WHITE );
				jlMatches.setSelectionBackground( defaultSelectionColor );
				
				// check if the left mouse button is double clicked
				if ( e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton( e ) )
				{
					txtLocation.setText( jlMatches.getSelectedValue() );
					matchesScrollPane.setVisible( false );
				}// end of if block
			}
			
			@Override
			public void mouseExited( MouseEvent e )
			{
				hoverAction = true;
				clickAction = false;
				outsideListArea = true;
				jlMatches.clearSelection();
				txtLocation.setBackground( Color.WHITE );
				txtLocation.setBorder( new CompoundBorder( textBoxLine, margin ) );
				matchesScrollPane.setBorder( new CompoundBorder( listBoxLine, margin ) );
			}
			
			@Override
			public void mouseEntered( MouseEvent e )
			{
				hoverAction = true;
				clickAction = false;
				outsideListArea = false;
				txtLocation.setBackground( new Color( 204, 228, 247 ) );
				txtLocation.setBorder( new CompoundBorder( focusTextBoxLine, margin ) );
				matchesScrollPane.setBorder( new CompoundBorder( focusListBoxLine, margin ) );
			}
		});
		
		matchesScrollPane = new JScrollPane( jlMatches );
		matchesScrollPane.setBorder( listBoxLine );
		matchesScrollPane.setOpaque( true );
		matchesScrollPane.setBackground( Color.WHITE );
		matchesScrollPane.setVisible( false );
		preferenceLayer.add( matchesScrollPane, Integer.valueOf( 2 ), 0 );

		lblLocationDescription = new JLabel( "<html><p>" + LOCATION_DATA_TEXT + "</p></html>" );
		lblLocationDescription.setFont(new Font( "Arial", Font.PLAIN, 14 ) );
		lblLocationDescription.setForeground( new Color( 105, 105, 105 ) );
		lblLocationDescription.setBounds( 8, 95, 600, 60 );
		preferenceLayer.add( lblLocationDescription, 0 );
		
		lblData = new JLabel( "Data" );
		lblData.setFont( new Font( "Arial", Font.PLAIN, 24 ) );
		lblData.setForeground( hotTrack );
		lblData.setBounds( 8, 160 , 622, 34 );		
		lblData.setBorder( bottom_border );	
		preferenceLayer.add( lblData, 0 );
		
		lblProvider = new JLabel( "Weather Provider:" );
		lblProvider.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		lblProvider.setBounds( 8, 200 , 620, 34 );
		preferenceLayer.add( lblProvider, 0 );
		
		weatherProviders = new DefaultComboBoxModel< String >( WeatherLionMain.authorizedProviders );
		cboWeatherProviders = new JComboBox< String >();
		cboWeatherProviders.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		cboWeatherProviders.setBounds( 8, 240, 622, 26 );
		cboWeatherProviders.setEditable( false );
		cboWeatherProviders.setModel( weatherProviders );
		preferenceLayer.add( cboWeatherProviders, 0 );
		
		cboWeatherProviders.addActionListener( new ActionListener() 
		{			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( e.getActionCommand().equals( "comboBoxChanged" ) )
				{
					String selectedProvider = cboWeatherProviders.getSelectedItem().toString();
					
					if( !selectedProvider.equals( WeatherLionMain.storedPreferences.getProvider() ) )
					{
						// notify the widget of this update
						if( !WeatherLionWidget.preferenceUpdated.containsKey(
								WeatherLionMain.WEATHER_SOURCE_PREFERENCE ) ) 
						{
							WeatherLionWidget.preferenceUpdated.put( 
								WeatherLionMain.WEATHER_SOURCE_PREFERENCE, selectedProvider );
						}// end of if block
						
						WeatherLionWidget.previousWeatherProvider.setLength( 0 ); // clear the string
						WeatherLionWidget.previousWeatherProvider.append(
							WeatherLionMain.storedPreferences.getProvider() );					
					}// end of if block					
				}// end of if block
			}
		});
		
		lblDataDescription = new JLabel("<html><p>" + WEATHER_DATA_TEXT + "</p></html>");
		lblDataDescription.setFont(new Font( "Arial", Font.PLAIN, 14 ) );
		lblDataDescription.setForeground( new Color(105, 105, 105 ) );
		lblDataDescription.setBounds( 8, 270, 600, 60 );
		preferenceLayer.add( lblDataDescription, 0 );
		
		lblRefreshInterval = new JLabel( "Refresh Interval" );
		lblRefreshInterval.setFont( new Font( "Arial", Font.PLAIN, 24 ) );
		lblRefreshInterval.setForeground( hotTrack );
		lblRefreshInterval.setBounds( 8, 330 , 622, 34 );		
		lblRefreshInterval.setBorder( bottom_border );
		preferenceLayer.add( lblRefreshInterval, 0 );
		
		lblRefreshIntervalDescription = new JLabel( "Weather refresh interval:" );
		lblRefreshIntervalDescription.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		lblRefreshIntervalDescription.setForeground( new Color( 105, 105, 105 ) );
		lblRefreshIntervalDescription.setBounds( 8, 368 , 622, 34 );
		preferenceLayer.add( lblRefreshIntervalDescription, 0 );
		
		lblInterval =  new JLabel( UtilityMethod.millisecondsToMinutes( 
				WeatherLionMain.storedPreferences.getInterval() ) + " min." );
		lblInterval.setFont( new Font( "Arial", Font.BOLD, 14 ) );
		lblInterval.setBounds( 172, 368 , 622, 34 );
		preferenceLayer.add( lblInterval, 0 );
		
		refreshIntervals = new DefaultComboBoxModel< String >( updateIntervals );
		cboRefreshInterval = new JComboBox< String >();
		cboRefreshInterval.setBounds( 8, 400, 622, 26 );
		cboRefreshInterval.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		cboRefreshInterval.setEditable( false );
		cboRefreshInterval.setModel( refreshIntervals );
		preferenceLayer.add( cboRefreshInterval, 0 );
		
		String selectInterval = String.valueOf(  UtilityMethod.millisecondsToMinutes( 
				WeatherLionMain.storedPreferences.getInterval() ) );
				
		cboRefreshInterval.setSelectedItem( selectInterval );	
		
		cboRefreshInterval.addActionListener( new ActionListener() 
		{			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( e.getActionCommand().equals( "comboBoxChanged" ) )
				{
					int selectedInterval = 
							UtilityMethod.minutesToMilliseconds(
									Integer.parseInt( cboRefreshInterval.getSelectedItem().toString() ) );
					
					if( selectedInterval != WeatherLionMain.storedPreferences.getInterval() )
					{
						// notify the widget of this update
						if( !WeatherLionWidget.preferenceUpdated.containsKey(
								WeatherLionMain.UPDATE_INTERVAL ) ) 
						{
							WeatherLionWidget.preferenceUpdated.put( WeatherLionMain.UPDATE_INTERVAL,
								String.valueOf( 
									UtilityMethod.minutesToMilliseconds(
										Integer.parseInt( 
											cboRefreshInterval.getSelectedItem().toString() ) ) ) );
						}// end of if block
						
						lblInterval.setText( cboRefreshInterval.getSelectedItem() + " min." );					
					}// end of if block
				}// end of if block	actionPerformed		
			}// end of method 
		});
	
		lblGeoLocation = new JLabel( "Use Geolocation" );
		lblGeoLocation.setFont( new Font( "Arial", Font.PLAIN, 24 ) );
		lblGeoLocation.setForeground( hotTrack );
		lblGeoLocation.setBounds( 8, 440 , 622, 34 );		
		lblGeoLocation.setBorder( bottom_border );
		preferenceLayer.add( lblGeoLocation, 0 );
		
		chkUseSystemLocation = new JCheckBox( "Use the system's IP location" );
		chkUseSystemLocation.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		chkUseSystemLocation.setBounds( 8, 480, 622, 30 );
		chkUseSystemLocation.setBackground( Color.WHITE );
		chkUseSystemLocation.setSelected( WeatherLionMain.storedPreferences.getUseSystemLocation() );
		preferenceLayer.add( chkUseSystemLocation, 0 );
		
		chkUseSystemLocation.addItemListener( new ItemListener()
		{			
			@Override
			public void itemStateChanged( ItemEvent e ) 
			{
				txtLocation.setEnabled( !chkUseSystemLocation.isSelected() );
				
				// notify the widget of this update
				if( !WeatherLionWidget.preferenceUpdated.containsKey(
						WeatherLionMain.USE_SYSTEM_LOCATION_PREFERENCE ) ) 
				{
					WeatherLionWidget.preferenceUpdated.put(
						WeatherLionMain.USE_SYSTEM_LOCATION_PREFERENCE, 
						String.valueOf( chkUseSystemLocation.isSelected() ) );
				}// end of if block				
			}
		});
		
		lblTempUnits = new JLabel( "Units" );
		lblTempUnits.setFont( new Font( "Arial", Font.PLAIN, 24 ) );
		lblTempUnits.setForeground( hotTrack );
		lblTempUnits.setBounds( 8, 520 , 622, 34 );		
		lblTempUnits.setBorder( bottom_border );
		preferenceLayer.add( lblTempUnits, 0 );
		
		chkUseMetric = new JCheckBox( "Use Metric ( \u00B0C )" );
		chkUseMetric .setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		chkUseMetric .setBounds( 8, 560, 622, 30 );
		chkUseMetric .setBackground( Color.WHITE );
		chkUseMetric.setSelected( WeatherLionMain.storedPreferences.getUseMetric() );
		preferenceLayer.add( chkUseMetric, 0 );
		
		chkUseMetric.addItemListener( new ItemListener()
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				// notify the widget of this update
				if( !WeatherLionWidget.preferenceUpdated.containsKey(
						WeatherLionMain.USE_METRIC_PREFERENCE ) ) 
				{
					WeatherLionWidget.preferenceUpdated.put(
						WeatherLionMain.USE_METRIC_PREFERENCE, 
						String.valueOf( chkUseMetric.isSelected() ) );
				}// end of if block				
			}
		});
		
		pnlWeather.add( preferenceLayer  ); // add the layered panel to the jpanel
		
		// Icon Set tab
		
		pnlIconSelectionCtn = new JPanel( new FlowLayout( FlowLayout.LEADING ) );
		pnlIconSelectionCtn.setOpaque( false );  // make transparent background;
//		pnlIconSelectionCtn.setBorder( BorderFactory.createEtchedBorder() );
		pnlIconSelectionCtn.setBounds( 8, 10 , 652, 594 );
		
		// load all icon packs found
		loadInstalledIconPacks();
		
		pnlIconSet.add( pnlIconSelectionCtn );
		
		( (JRadioButton) 
				WeatherLionMain.iconSetComponents.get(
						WeatherLionMain.storedPreferences.getIconSet() ).get( 2 )
		).setSelected( true );
				
		// Background tab
		radDefault = new JRadioButton( "" );
		radDefault.setBackground( Color.white );
		
		JLabel lblDefaultImage = new JLabel();
		lblDefaultImage.setIcon( UtilityMethod.scaleImageIcon( 
			WeatherLionMain.DEFAULT_BACKGROUND_IMAGE, 136, 115 ) );
		
		JLabel defaultTitle = new JLabel( "Default" );
		defaultTitle.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		
		JPanel pnlDefaultBackground = new JPanel();
		pnlDefaultBackground.setBackground( Color.white );
		pnlDefaultBackground.setBounds( 8, 10 , 220, 115 );
		pnlDefaultBackground.setLayout( new BorderLayout() );
		pnlDefaultBackground.add( radDefault, "West" );
		pnlDefaultBackground.add( lblDefaultImage, "Center" );
		pnlDefaultBackground.add( defaultTitle, "East" );
		
		radAndroid = new JRadioButton( "" );
		radAndroid.setBackground( Color.white );
		
		JLabel lblAndroidImage = new JLabel();
		lblAndroidImage.setIcon( UtilityMethod.scaleImageIcon( 
				WeatherLionMain.ANDROID_BACKGROUND_IMAGE, 136, 115 ) );
		
		JLabel androidTitle = new JLabel( "Android" );
		androidTitle.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		
		JPanel pnlAndroidBackground = new JPanel();
		pnlAndroidBackground.setBackground( Color.white );
		pnlAndroidBackground.setBounds( 8, 210 , 220, 115 );
		pnlAndroidBackground.setLayout( new BorderLayout() );
		pnlAndroidBackground.add( radAndroid, "West" );
		pnlAndroidBackground.add( lblAndroidImage, "Center" );
		pnlAndroidBackground.add( androidTitle, "East" );
		
		radRabalac = new JRadioButton( "" );
		radRabalac.setBackground( Color.white );
		
		JLabel lblRabalacImage = new JLabel();
		lblRabalacImage.setIcon( UtilityMethod.scaleImageIcon( 
				WeatherLionMain.RABALAC_BACKGROUND_IMAGE, 136, 115 ) );
		
		JLabel rabalacTitle = new JLabel( "Rabalac" );
		rabalacTitle.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		
		JPanel pnlRabalacBackground = new JPanel();
		pnlRabalacBackground.setBackground( Color.white );
		pnlRabalacBackground.setBounds( 8, 410 , 220, 115 );
		pnlRabalacBackground.setLayout( new BorderLayout() );
		pnlRabalacBackground.add( radRabalac, "West" );
		pnlRabalacBackground.add( lblRabalacImage, "Center" );
		pnlRabalacBackground.add( rabalacTitle, "East" );
		
		ButtonGroup backgroundGroup = new ButtonGroup(); // button group for the radio buttons
		backgroundGroup.add( radDefault );
		backgroundGroup.add( radAndroid );
		backgroundGroup.add( radRabalac );
		
		pnlBackground.add( pnlDefaultBackground );
		pnlBackground.add( pnlAndroidBackground );
		pnlBackground.add( pnlRabalacBackground );
		
		switch ( WeatherLionMain.storedPreferences.getWidgetBackground() )
		{
			case "default":
				radDefault.setSelected( true );
				break;
			case "android":
				radAndroid.setSelected( true );
				break;	
			case "rabalac":
				radRabalac.setSelected( true );
				break;	
			default:
				break;
		}// end of switch block
		
		radDefault.addItemListener( new ItemListener() 
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if ( e.getStateChange() == ItemEvent.SELECTED )
			    {
					if( !WeatherLionMain.storedPreferences.getWidgetBackground().equals( "default" ) &&
							radDefault.isSelected() )
					{
						WeatherLionWidget.preferenceUpdated.put(
							WeatherLionMain.WIDGET_BACKGROUND_PREFERENCE, "default" );
					}// end of if block
				}// end of if block				
			    else if ( e.getStateChange() == ItemEvent.DESELECTED )
			    {			    	
			    }// end of else block
			}
		});
		
		radAndroid.addItemListener( new ItemListener() 
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if ( e.getStateChange() == ItemEvent.SELECTED )
			    {
					if( !WeatherLionMain.storedPreferences.getWidgetBackground().equals( "android" ) &&
							radAndroid.isSelected() )
					{
						WeatherLionWidget.preferenceUpdated.put( 
							WeatherLionMain.WIDGET_BACKGROUND_PREFERENCE, "android" );
					}// end of if block
				}// end of if block				
			    else if ( e.getStateChange() == ItemEvent.DESELECTED )
			    {			    	
			    }// end of else block
			}
		});
		
		radRabalac.addItemListener( new ItemListener() 
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if ( e.getStateChange() == ItemEvent.SELECTED )
			    {
					if( !WeatherLionMain.storedPreferences.getWidgetBackground().equals( "rabalac" ) &&
							radRabalac.isSelected() )
					{
						WeatherLionWidget.preferenceUpdated.put( 
							WeatherLionMain.WIDGET_BACKGROUND_PREFERENCE, "rabalac" );
					}// end of if block
				}// end of if block				
			    else if ( e.getStateChange() == ItemEvent.DESELECTED )
			    {			    	
			    }// end of else block
			}
		});
		
		// About tab
		int thisYear = Calendar.getInstance().get( Calendar.YEAR );
		String message = "<html><center><b>Weather Lion</b>"
				+ "<br />Author: Paul O. Patterson<br />"
				+ "BushBungalo Productions™ 2005 - " + thisYear + "<br />"
				+ "Version: 1.0<br />"
				+ "&copy All rights reserved</center>"
				+ "<br /><br />"+ ABOUT_PROGRAM  + "</html>";
						
		lblAbout = new JLabel( message );	// Original year of creation
		lblAbout.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		lblAbout.setBounds( 8, 4 , 680, 300 );
		pnlAbout.add( lblAbout );
		
	}// end of method createUserFormLabels
	
	/***
	 * Load all available icon packs
	 */
	private void loadInstalledIconPacks()
	{
		if( WeatherLionMain.iconPackList.size() > 0 )
		{
			WeatherLionMain.iconPackList.clear();
			WeatherLionMain.iconSetComponents.clear();
			WeatherLionMain.iconPacksLoaded = false;
		}// end of if block
				
		File[] files =
				new File( "res/assets/img/weather_images" ).listFiles();
					
		getInstalledIconPacks( files );
		
		WeatherLionMain.iconSetComponents = new HashMap< String, ArrayList< Component > >();
		iconGroup = new ButtonGroup();
		
		for ( String packName : WeatherLionMain.iconPackList )
		{
			JLabel packTitle = new JLabel( UtilityMethod.toProperCase( packName ),
					SwingConstants.CENTER );
			packTitle.setFont( new Font( "Arial", Font.BOLD, 14 ) );
			
			JLabel packDefaultImage = new JLabel( "", SwingConstants.CENTER );
			
			// An alternate file to be used as a preview image for the icon pack
			File previewImage = new File( "res/assets/img/weather_images/" + packName + "/preview_image.png" );
									
			Icon wxIcon = new ImageIcon( "res/assets/img/weather_images/" + packName +
				( previewImage.exists() ? "/preview_image.png" : "/weather_10.png" ) );
					
			packDefaultImage.setIcon( UtilityMethod.scaleImageIcon( wxIcon, 100, 100 ) );
						
			JRadioButton iconSelector = new JRadioButton( "" );
			iconSelector.setOpaque( false );
			iconSelector.setHorizontalAlignment( SwingConstants.CENTER );
			
			JPanel iconSelectionContainer = new JPanel();
			iconSelectionContainer.setOpaque( false );
			iconSelectionContainer.setPreferredSize( new Dimension( 140,158 ) );
			iconSelectionContainer.setLayout( new BorderLayout() );
			iconSelectionContainer.add( packTitle, "North" );
			iconSelectionContainer.add( packDefaultImage, "Center" );
			iconSelectionContainer.add( iconSelector, "South" );
			
			iconSelectionContainer.setBorder( new EmptyBorder( 2, 2, 10, 2 ) );
			
			packDefaultImage.addMouseListener( new MouseAdapter() 
			{
				@Override
				public void mouseClicked( MouseEvent e ) 
				{
					iconSelector.setSelected( true );
				}
			});
			
			iconSelector.addItemListener( new ItemListener()
			{			
				@Override
				public void itemStateChanged( ItemEvent e )
				{
				    if ( e.getStateChange() == ItemEvent.SELECTED )
				    {
				    	if( !WeatherLionMain.storedPreferences.getIconSet().equals( packName ) )
				    	{
				    		WeatherLionWidget.preferenceUpdated.put( 
								WeatherLionMain.ICON_SET_PREFERENCE, packName );
						}// end of if block
				    }// end of if block
				    else if ( e.getStateChange() == ItemEvent.DESELECTED )
				    {			    	
				    }// end of else block
				}		
			});
			
			// Add icon selections to JPanel
			pnlIconSelectionCtn.add( iconSelectionContainer );
			
			iconGroup.add( iconSelector );
			
			ArrayList< Component > components = new ArrayList< Component >();
			components.add( packTitle );	// Add the component that displays the icon pack title
			components.add( packDefaultImage ); // Add the component that displays the icon pack default image
			components.add( iconSelector ); // Add the component that displays the radio button to select the pack
			
			WeatherLionMain.iconSetComponents.put( packName, components );
		}// end of for each loop
		
		WeatherLionMain.iconPacksLoaded = true;
		UtilityMethod.logMessage( "info", "Icon Packs Installed: " + WeatherLionMain.iconPackList,
				"PreferenceForm::loadInstalledIconPacks" );
		
	}// end of method  loadInstalledIconPacks
	
	/***
	 * Read the weather images directory and load the names of all sub-directories
	 * which contain optional items to be used with the widget
	 * 
	 * @param file	A file path representing the location of each icon pack
	 */
	public static void getInstalledIconPacks( File[] files )
	{
	    for ( File file : files ) 
	    {
	        if ( file.isDirectory() )
	        {
	        	WeatherLionMain.iconPackList.add( file.getName() );
	            getInstalledIconPacks( file.listFiles() ); // recursive call.
	        }// end of if block 
	    }// end of for loop
	}// end of method getInstalledIconPacks
	
	/***
	 * Creates the components for the user form.
	 */
	private void createUserFormComponents()
	{
		createTabbedPaneAndPanels();
		
		createUserFormButtons();
		
		createUserFormLabels();
	}// end of method createUserFormComponents
	
	/**
	 * Create all the tabbed panes to be placed on the {@code JFrame}
	 */
	private void createTabbedPaneAndPanels() 
	{
		tpPreferences = new JTabbedPane();
		tpPreferences.setBackground( Color.GRAY );
		tpPreferences.setBounds( 5, 10, 658, 640 );		
		
		// track which tab is currently in focus
		tpPreferences.addChangeListener( new ChangeListener()
		{
			@Override
			public void stateChanged( ChangeEvent e )
			{
				m_current_tab = tpPreferences.getSelectedIndex();					
			}// end of method stateChanged
		});				
		
		pnlWeather = new JPanel();
		pnlWeather.setLayout( null );
		pnlWeather.setBackground( Color.WHITE );
		tpPreferences.addTab( "Weather", null, pnlWeather, "Weather Preferences" );
		tpPreferences.setMnemonicAt( 0, KeyEvent.VK_1 );
		
		tpPreferences.addMouseListener( new MouseAdapter() 
		{			
			@Override
			public void mousePressed( MouseEvent e ) 
			{
				if( matchesScrollPane.isVisible() ) matchesScrollPane.setVisible( false ); 
			}
			
			@Override
			public void mouseClicked( MouseEvent e )
			{
				if( matchesScrollPane.isVisible() ) matchesScrollPane.setVisible( false ); 
			}
		});
				
		pnlIconSet = new JPanel();
		pnlIconSet.setLayout( null );
		pnlIconSet.setBackground( Color.WHITE );
		tpPreferences.addTab( "Icon Set", null, pnlIconSet, "Weather Icons" );
		tpPreferences.setMnemonicAt( 1, KeyEvent.VK_2 );
		
		pnlBackground = new JPanel();
		pnlBackground.setLayout( null );
		pnlBackground.setBackground( Color.WHITE );
		tpPreferences.addTab( "Background", null, pnlBackground, "Widget Background" );
		tpPreferences.setMnemonicAt( 2, KeyEvent.VK_3 );

		pnlAbout = new JPanel();
		pnlAbout.setLayout( null );
		pnlAbout.setBackground( Color.WHITE );
		tpPreferences.addTab( "About", null, pnlAbout, "About Program" );
		tpPreferences.setMnemonicAt( 3, KeyEvent.VK_4 );	
	}// end of method createTabbedPaneAndPanels
	
	/***
	 * Initializes all the handler that will process all button presses. 
	 */
	private void setButtonHandlers()
	{
		// create new ButtonHandler for button event handling
		ButtonHandler handler = new ButtonHandler();
		
		btnSearch.addActionListener( handler );
		btnOk.addActionListener( handler );
		btnCancel.addActionListener( handler );
		btnApply.addActionListener( handler );		
	}// end of method setButtonHandlers()	
	
	/**
	 * Inner class for button event handling
	 * 
	 * @author Paul O. Patterson
	 * @version     1.0
	 * @since       1.0
	 */
	private class ButtonHandler implements ActionListener
	{
		// handle button event
		public void actionPerformed( ActionEvent event )
		{
			// evaluate with key the user pressed
			switch ( event.getActionCommand() )
			{
				case SEARCH:
					btnSearch_Click();
					break;
				case OK:
					btnOk_Click();
					break;
				case CANCEL:
					btnCancel_Click();
					break;
				case APPLY:
					btnApply_Click();
					break;					
				default:					
					break;					
			}// end of switch				
		} // end method actionPerformed
	} // end private inner class ButtonHandler
	
	/**
	 * Inner class for JTextfield auto complete functionality
	 * 
	 * @author Paul O. Patterson
	 * @version     1.0
	 * @since       1.0
	 */
	private class JTextfieldAutoComplete implements DocumentListener
	{		
		private JTextField textField;
		private final List< String > keywords;
		private Mode mode = Mode.INSERT;
			  
		public JTextfieldAutoComplete( JTextField textField, List<String> keywords ) 
		{
			 this.textField = textField;
			 this.keywords = keywords;
		}
			
		/**
		 * {@inheritDoc}
		 */					
		@Override
		public void changedUpdate(DocumentEvent ev) { }
		
		/**
		 * {@inheritDoc}
		 */	
		@Override
		public void removeUpdate(DocumentEvent ev) { }
		
		/**
		 * {@inheritDoc}
		 */	
		@Override
		public void insertUpdate(DocumentEvent ev) 
		{		  
			if ( ev.getLength() != 1 )
			{
				return;
			}// end of if block
			
			int pos = ev.getOffset();
			String content = null;
			
			try
			{
			  content = textField.getText( 0, pos + 1 );
			}// end of try block
			catch ( BadLocationException e )
			{
				UtilityMethod.logMessage( "severe" , e.getMessage(),
					"PreferenceForm::insertUpdate [line: " 
				    + e.getStackTrace()[ 1 ].getLineNumber() + "]" );
			}// end of catch block
				
			// Find where the word starts
			int w;
			
			for ( w = pos; w >= 0; w-- )
			{
				if ( !Character.isLetter( content.charAt( w ) ) )
				{
					break;
				}// end of if block
			}// end of for loop

			// Too few chars
			if ( pos - w < 2 )
			{
				return;
			}// end of if block
			
			String prefix = content.substring( w + 1 );
			Collections.sort( keywords, String::compareToIgnoreCase );
			int n = Collections.binarySearch( keywords, prefix, String::compareToIgnoreCase );
			
			if ( n < 0 && -n <= keywords.size() ) 
			{
				String match = keywords.get( -n - 1 );
			  
				if ( startsWith( match, prefix, true ) ) 
				{
					// A completion is found
					String completion = match.substring( pos - w );
					// We cannot modify Document from within notification,
					// so we submit a task that does the change later
					SwingUtilities.invokeLater( new CompletionTask( completion, pos + 1 ) );
				}// end of if block
			}// end of if block
			else 
			{
				// Nothing found
				mode = Mode.INSERT;		
			}// end of if block
		}// end of method insertUpdate
		
		private boolean startsWith( String str, String prefix, boolean ignoreCase ) 
		{
		      if ( str == null || prefix == null )
		      {
		          return ( str == null && prefix == null );
		      }// end of if block
		      
		      if ( prefix.length() > str.length() ) 
		      {
		          return false;
		      }// end of if block
		      
		      return str.regionMatches( ignoreCase, 0, prefix, 0, prefix.length() );
		}// end of method startsWith

		@SuppressWarnings("serial")
		public class CommitAction extends AbstractAction
		{	   
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void actionPerformed( ActionEvent ev ) 
			{
				if ( mode == Mode.COMPLETION )  
				{
					int pos = textField.getSelectionEnd();
					StringBuffer sb = new StringBuffer( textField.getText() );
					sb.insert( pos, " " );
					textField.setText( sb.toString() );
					textField.setCaretPosition( pos + 1 );
					mode = Mode.INSERT;
				}// end of if block
				else 
				{
					textField.replaceSelection( "\t" );
				}// end of else block
		    }// end of method actionPerformed
		}// end of class CommitAction

		private class CompletionTask implements Runnable
		{
		    private String completion;
		    private int position;
		
		    CompletionTask( String completion, int position )
		    {
		    	this.completion = completion;
		    	this.position = position;
		    }// end of two-argument constructor

		    public void run()
		    {
			    StringBuffer sb = new StringBuffer( textField.getText() );
			    sb.insert( position, completion );
			    textField.setText( sb.toString() );
			    textField.setCaretPosition( position + completion.length() );
			    textField.moveCaretPosition( position );
			    mode = Mode.COMPLETION;
		    }// end of method run
		}// end of class CompletionTask
	 }// end of class JTextfieldAutoComplete
}// end of class PreferenceForm
