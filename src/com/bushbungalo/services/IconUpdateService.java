package com.bushbungalo.services;

import com.bushbungalo.WeatherLionWidget;

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
				WeatherLionWidget.checkAstronomy();								
			}// end of try block 
			catch ( InterruptedException e ) 
			{
				// do nothing
			}// end of catch block
		}// end of while block
	}

}// end of class IconUpdateService
