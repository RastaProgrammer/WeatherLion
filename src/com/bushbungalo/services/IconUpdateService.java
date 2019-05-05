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
 * </ul>
 */
public class IconUpdateService implements Runnable
{
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
				WeatherLionMain.connectedToInternet = UtilityMethod.hasInternetConnection();
				
				// If the not connected to the Internet, for a connection
				if( !WeatherLionMain.connectedToInternet )
				{
					// if there was no previous Internet connection, check for a return in connectivity
					// and refresh the widget
					if( WeatherLionWidget.usingPreviousData && WeatherLionMain.connectedToInternet 
							&& UtilityMethod.updatedRequired() )
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
				UtilityMethod.logMessage( "severe", e.getMessage(), "IconUpdateService::run" );				
			}// end of catch block
		}// end of while block
	}// end of method run

}// end of class IconUpdateService
