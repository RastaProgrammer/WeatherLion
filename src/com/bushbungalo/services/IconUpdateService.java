package com.bushbungalo.services;

import com.bushbungalo.WeatherLionMain;
import com.bushbungalo.WeatherLionWidget;
import com.bushbungalo.WeatherLionWidget.WidgetUpdateService;
import com.bushbungalo.utils.UtilityMethod;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Class Description:</b>
 * <br />
 * 		<span>Service that calls method <b><i>checkAstronomy</i></b> in the <b>WeatherLionWidget</b> class which checks the current time of day</span>
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 10/12/18
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>05/04/19 - Service now monitors Internet connectivity and updates widget accordingly.</li>
 * 		<li>05/05/19 - Service now only performs the check 1 minute before an update is due.</li>
 * </ul>
 */
public class IconUpdateService implements Runnable
{
	private static final String TAG = "IconUpdateService";
	
	@Override
	public void run() 
	{
		int sleepTime = 500;
		
		// update the date and time in a separate thread
		while( WeatherLionWidget.running )
		{
			try
			{				
				Thread.sleep( sleepTime );
				
				// keep track of Internet connectivity
				if( UtilityMethod.timeForConnectivityCheck() ) 
				{
					WeatherLionMain.connectedToInternet = UtilityMethod.hasInternetConnection();
				}// end of if block
				
				// If the program is not connected to the Internet, wait for a connection
				if( WeatherLionMain.connectedToInternet )
				{
					// if there was no previous Internet connection, check for a return in connectivity
					// and refresh the widget
					if( WeatherLionWidget.usingPreviousData && UtilityMethod.updateRequired() )
					{    			
						// run the weather service
						WeatherLionWidget.ws = new WidgetUpdateService( false );
						WeatherLionWidget.ws.execute();
					}// end of if block
				}// end of if block
				
				if( WeatherLionMain.connectedToInternet )
				{
					WeatherLionWidget.lblOffline.setVisible( false );
				}// end of if block
				else 
				{
					WeatherLionWidget.lblOffline.setVisible( true );
				}// end of else block
				
				WeatherLionWidget.checkAstronomy();								
			}// end of try block 
			catch ( InterruptedException e ) 
			{
				 UtilityMethod.logMessage( "severe", e.getMessage(),
			        TAG + "::run [line: " +
			        UtilityMethod.getExceptionLineNumber( e )  + "]" );		
			}// end of catch block
		}// end of while block
	}// end of method run
}// end of class IconUpdateService
