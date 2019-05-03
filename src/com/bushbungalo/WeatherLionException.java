package com.bushbungalo;

/**
 * @author Paul O. Patterson
 * @version     1.0
 * @since       1.0
 * 
 * <p>
 * This class is a custom exception class specific to the WeatherLion program. 
 * </p>
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 02/08/19
 * <br />
 * <b style="margin-left:-40px">Description:</b><br />
 * <p>
 * 		{@code WeatherWidget} Application {@code Exception} class.
 * </p>
 */

public class WeatherLionException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public WeatherLionException()
	{
	}

	/**
	 * Extends the base Exception class constructor
	 * @param description Error description
	 */
	public WeatherLionException(String description)
	{
		super(description);
	}

	/**
	 * Extends the base Exception class constructor
	 * @param ex The root exception causing the error
	 */
	public WeatherLionException(Throwable ex)
	{
		super(ex);
	}

	/**
	 * Extends the base Exception class constructor
	 * 
	 * @param description Error description
	 * @param ex The root exception causing the error
	 */
	public WeatherLionException(String description, Throwable ex)
	{
		super(description, ex);
	}

	/**
	 * Extends the base Exception class constructor
	 * 
	 * @param description Error description
	 * @param ex The root exception causing the error
	 * @param suppression enable suppression
	 * @param writable writable stack trace
	 */
	public WeatherLionException(String description, Throwable ex, boolean suppression, boolean writable)
	{
		super(description, ex, suppression, writable);
	}
}// end of class WeatherLionException
