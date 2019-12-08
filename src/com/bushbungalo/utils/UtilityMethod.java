package com.bushbungalo.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bushbungalo.WeatherLionMain;
import com.bushbungalo.WeatherLionWidget.WidgetUpdateService;
import com.bushbungalo.model.CityData;
import com.bushbungalo.model.HereGeoLocation;
import com.bushbungalo.model.YahooGeoLocation;
import com.bushbungalo.services.CityDataService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author Paul O. Patterson
 * @version     1.0
 * @since       1.0
 * 
 * <p>
 * This class provides all the utility functions that the program may 
 * need to perform simple tasks and basic calculations.
 * </p>
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/21/17
 * <br />
 * <b style="margin-left:-40px">Updates:</b><br />
 * <ul>
 * 		<li>10/18/18 - Added method {@link #temperatureColor}</li>
 * 		<li>01/13/19 - Added method {@link #calculateWindChill}</li>
 * 		<li>01/21/19 - Added {@link #LinkedHashMap} weatherCountryCodes</li>
 * 		<li>01/22/19 - Updated {@link #LinkedHashMap} weatherImages</li>
 * 		<li>02/06/19 - Added methods {@link #getFileCount} and {@link #getSubdirectories}</li>
 * 		<li>02/08/19 - Added method {@link #logMessage}</li>
 *      <li>02/18/19 - Added methods {@link #imageToBufferedImage} and {@link #makeColorTransparent}</li>
 *      <li>03/02/19
 *      	<ol>
 *      		<li>
 *      			API from {@link http://api.ipstack.com} not working so {@link http://ip-api.com/xml} will 
 *      			be used to obtain location using IP Address
 *      		</li>
 *      		<li>Added method {@link #getSystemIpAddress}</li>
 *      		<li>Updated method {@link #getSystemLocation}</li>
 *      	</ol>
 *      </li>
 *      <li>03/12/19
 *      	<ol>
 *      		<li>
 *      			API from {@link http://api.ipstack.com} is now working so code will be reverted from {@link http://ip-api.com/xml} 
 *      			to obtain location using IP Address
 *      		</li>		
 *      		<li>Added methods {@link #isWindows}, {@link #isLinux}, and {@link #isMac}</li>
 *      		<li>Updated method {@link #getSystemIpAddress}</li>
 *      	</ol>
 *      </li>
 *      <li>03/20/19 - Added method {@link #createHereCityData}</li>
 *      <li>03/22/19
 *      	<ol>
 *      		<li>Added method {@link #isNumeric}</li>		
 *      		<li>Updated method {@link #get24HourTime} to inspect for a space within the {@code String} passed</li>
 *      	</ol>
 *      </li>
 *      <li>04/11/19 - Added methods {@link #replaceLast}, {@link #numberOfCharacterOccurences}, {@link #msgBox}, and {@link #messagePrompt}</li>
 *      <li>05/02/19 - Added methods {@link #confirmBox} and {@link #size}</li>
 *      <li>05/06/19 - Added method {@link #timeForConnectivityCheck}</li>
 *      <li>05/11/19 - Removed printing stack trace errors to console for logging</li>
 *      <li>05/21/19 - Renamed method {@link #makeColorTransparent} to {@link #setImageOpacity}.</li>
 *      <li>06/13/19 - Added method {@link #containsWholeWord}</li>
 *      <li>06/28/19 - Updated methods {@link #isFoundInJSONStorage} and {@link #isFoundInXMLStorage}</li>
 *      <li>07/04/19 - Added method {@link #getExceptionLineNumber}</li>
 *      <li>07/25/19 - Added method {@link #retrieveGeoNamesGeoLocationUsingCoordinates}</li>
 * </ul>
 */

@SuppressWarnings("unused")
public abstract class UtilityMethod
{
	private static final String TAG = "UtilityMethod";
	
	// obtain OS information
	public static String OS = System.getProperty( "os.name" ).toLowerCase();
		
	public static String[] yahooWeatherCodes =
	{
	   "tornado", "tropical storm", "hurricane", "severe thunderstorms",
	   "thunderstorms", "mixed rain and snow", "mixed rain and sleet",
	   "mixed snow and sleet", "freezing drizzle", "drizzle", "freezing rain",
	   "showers", "showers", "snow flurries", "light snow showers", "blowing snow",
	   "snow", "hail", "sleet", "dust", "foggy", "haze", "smoky", "blustery", "windy",
	   "cold", "cloudy", "mostly cloudy (night)", "mostly cloudy (day)",
	   "partly cloudy (night)", "partly cloudy (day)", "clear (night)", "sunny",
	   "fair (night)", "fair (day)", "mixed rain and hail", "hot",
	   "isolated thunderstorms", "scattered thunderstorms", "scattered thunderstorms",
	   "scattered showers", "heavy snow", "scattered snow showers", "heavy snow",
	   "partly cloudy", "thundershowers", "snow showers", "isolated thundershowers"
	};

    // Array of compass sectors
    public static String[] compassSectors = 
    {
    	"N", "NNE", "NE", "ENE", "E", "ESE",
        "SE", "SSE", "S", "SSW", "SW", "WSW",
        "W", "WNW", "NW", "NNW" 
    };

    // Maps a cardinal point to its name
    public static LinkedHashMap<String, String> cardinalPoints;
    static
    {
        cardinalPoints = new LinkedHashMap<String, String>();
        cardinalPoints.put( "E", "East" );
        cardinalPoints.put( "N", "North" );
        cardinalPoints.put( "S", "South" );
        cardinalPoints.put( "W", "West" );
    };

    // Maps a US state's two-letter code to its full name
    public static LinkedHashMap<String, String> usStatesByCode;
    static
    {
        usStatesByCode = new LinkedHashMap<String, String>();

        usStatesByCode.put("AL", "Alabama");
        usStatesByCode.put("AK", "Alaska");
        usStatesByCode.put("AZ", "Arizona");
        usStatesByCode.put("AR", "Arkansas");
        usStatesByCode.put("CA", "California");
        usStatesByCode.put("CO", "Colorado");
        usStatesByCode.put("CT", "Connecticut");
        usStatesByCode.put("DE", "Delaware");
        usStatesByCode.put("FL", "Florida");
        usStatesByCode.put("GA", "Georgia");
        usStatesByCode.put("HI", "Hawaii");
        usStatesByCode.put("ID", "Idaho");
        usStatesByCode.put("IL", "Illinois");
        usStatesByCode.put("IN", "Indiana");
        usStatesByCode.put("IA", "Iowa");
        usStatesByCode.put("KS", "Kansas");
        usStatesByCode.put("KY", "Kentucky");
        usStatesByCode.put("LA", "Louisiana");
        usStatesByCode.put("ME", "Maine");
        usStatesByCode.put("MD", "Maryland");
        usStatesByCode.put("MA", "Massachusetts");
        usStatesByCode.put("MI", "Michigan");
        usStatesByCode.put("MN", "Minnesota");
        usStatesByCode.put("MS", "Mississippi");
        usStatesByCode.put("MO", "Missouri");
        usStatesByCode.put("MT", "Montana");
        usStatesByCode.put("NE", "Nebraska");
        usStatesByCode.put("NV", "Nevada");
        usStatesByCode.put("NH", "New Hampshire");
        usStatesByCode.put("NJ", "New Jersey");
        usStatesByCode.put("NM", "New Mexico");
        usStatesByCode.put("NY", "New York");
        usStatesByCode.put("NC", "North Carolina");
        usStatesByCode.put("ND", "North Dakota");
        usStatesByCode.put("OH", "Ohio");
        usStatesByCode.put("OK", "Oklahoma");
        usStatesByCode.put("OR", "Oregon");
        usStatesByCode.put("PA", "Pennsylvania");
        usStatesByCode.put("RI", "Rhode Island");
        usStatesByCode.put("SC", "South Carolina");
        usStatesByCode.put("SD", "South Dakota");
        usStatesByCode.put("TN", "Tennessee");
        usStatesByCode.put("TX", "Texas");
        usStatesByCode.put("UT", "Utah");
        usStatesByCode.put("VT", "Vermont");
        usStatesByCode.put("VA", "Virginia");
        usStatesByCode.put("WA", "Washington");
        usStatesByCode.put("WV", "West Virginia");
        usStatesByCode.put("WI", "Wisconsin");
        usStatesByCode.put("WY", "Wyoming");
    }

    // Maps a US state's full name to its two-letter code
    public static LinkedHashMap<String, String> usStatesByName;
    static
    {
        usStatesByName = new LinkedHashMap<String, String>();

        usStatesByName.put("Alabama", "AL");
        usStatesByName.put("Alaska", "AK");
        usStatesByName.put("Arizona", "AZ");
        usStatesByName.put("Arkansas", "AR");
        usStatesByName.put("California", "CA");
        usStatesByName.put("Colorado", "CO");
        usStatesByName.put("Connecticut", "CT");
        usStatesByName.put("Delaware", "DE");
        usStatesByName.put("Florida", "FL");
        usStatesByName.put("Georgia", "GA");
        usStatesByName.put("Hawaii", "HI");
        usStatesByName.put("Idaho", "ID");
        usStatesByName.put("Illinois", "IL");
        usStatesByName.put("Indiana", "IN");
        usStatesByName.put("Iowa", "IA");
        usStatesByName.put("Kansas", "KS");
        usStatesByName.put("Kentucky", "KY");
        usStatesByName.put("Louisiana", "LA");
        usStatesByName.put("Maine", "ME");
        usStatesByName.put("Maryland", "MD");
        usStatesByName.put("Massachusetts", "MA");
        usStatesByName.put("Michigan", "MI");
        usStatesByName.put("Minnesota", "MN");
        usStatesByName.put("Mississippi", "MS");
        usStatesByName.put("Missouri", "MO");
        usStatesByName.put("Montana", "MT");
        usStatesByName.put("Nebraska", "NE");
        usStatesByName.put("Nevada", "NV");
        usStatesByName.put("New Hampshire", "NH");
        usStatesByName.put("New Jersey", "NJ");
        usStatesByName.put("New Mexico", "NM");
        usStatesByName.put("New York", "NY");
        usStatesByName.put("North Carolina", "NC");
        usStatesByName.put("North Dakota", "ND");
        usStatesByName.put("Ohio", "OH");
        usStatesByName.put("Oklahoma", "OK");
        usStatesByName.put("Oregon", "OR");
        usStatesByName.put("Pennsylvania", "PA");
        usStatesByName.put("Rhode Island", "RI");
        usStatesByName.put("South Carolina", "SC");
        usStatesByName.put("South Dakota", "SD");
        usStatesByName.put("Tennessee", "TN");
        usStatesByName.put("Texas", "TX");
        usStatesByName.put("Utah", "UT");
        usStatesByName.put("Vermont", "VT");
        usStatesByName.put("Virginia", "VA");
        usStatesByName.put("Washington", "WA");
        usStatesByName.put("West Virginia", "WV");
        usStatesByName.put("Wisconsin", "WI");
        usStatesByName.put("Wyoming", "WY");
    }

    // Maps a country's two-letter code to its full name
    public static LinkedHashMap<String, String> worldCountries;
    static
    {
        worldCountries = new LinkedHashMap<String, String>();

        worldCountries.put("AF", "Afghanistan");
        worldCountries.put("AX", "Aland Islands");
        worldCountries.put("AL", "Albania");
        worldCountries.put("DZ", "Algeria");
        worldCountries.put("AS", "American Samoa");
        worldCountries.put("AD", "Andorra");
        worldCountries.put("AO", "Angola");
        worldCountries.put("AI", "Anguilla");
        worldCountries.put("AQ", "Antarctica");
        worldCountries.put("AG", "Antigua and Barbuda");
        worldCountries.put("AR", "Argentina");
        worldCountries.put("AM", "Armenia");
        worldCountries.put("AW", "Aruba");
        worldCountries.put("AU", "Australia");
        worldCountries.put("AT", "Austria");
        worldCountries.put("AZ", "Azerbaijan");
        worldCountries.put("BS", "Bahamas");
        worldCountries.put("BH", "Bahrain");
        worldCountries.put("BD", "Bangladesh");
        worldCountries.put("BB", "Barbados");
        worldCountries.put("BY", "Belarus");
        worldCountries.put("BE", "Belgium");
        worldCountries.put("BZ", "Belize");
        worldCountries.put("BJ", "Benin");
        worldCountries.put("BM", "Bermuda");
        worldCountries.put("BT", "Bhutan");
        worldCountries.put("BO", "Bolivia");
        worldCountries.put("BA", "Bosnia and Herzegovina");
        worldCountries.put("BW", "Botswana");
        worldCountries.put("BV", "Bouvet Island");
        worldCountries.put("BR", "Brazil");
        worldCountries.put("VG", "British Virgin Islands");
        worldCountries.put("IO", "British Indian Ocean Territory");
        worldCountries.put("BN", "Brunei Darussalam");
        worldCountries.put("BG", "Bulgaria");
        worldCountries.put("BF", "Burkina Faso");
        worldCountries.put("BI", "Burundi");
        worldCountries.put("KH", "Cambodia");
        worldCountries.put("CM", "Cameroon");
        worldCountries.put("CA", "Canada");
        worldCountries.put("CV", "Cape Verde");
        worldCountries.put("KY", "Cayman Islands");
        worldCountries.put("CF", "Central African Republic");
        worldCountries.put("TD", "Chad");
        worldCountries.put("CL", "Chile");
        worldCountries.put("CN", "China");
        worldCountries.put("HK", "Hong Kong, SAR China");
        worldCountries.put("MO", "Macao, SAR China");
        worldCountries.put("CX", "Christmas Island");
        worldCountries.put("CC", "Cocos (Keeling) Islands");
        worldCountries.put("CO", "Colombia");
        worldCountries.put("KM", "Comoros");
        worldCountries.put("CG", "Congo (Brazzaville)");
        worldCountries.put("CD", "Congo, (Kinshasa)");
        worldCountries.put("CK", "Cook Islands");
        worldCountries.put("CR", "Costa Rica");
        worldCountries.put("CI", "Côte d'Ivoire");
        worldCountries.put("HR", "Croatia");
        worldCountries.put("CU", "Cuba");
        worldCountries.put("CY", "Cyprus");
        worldCountries.put("CZ", "Czech Republic");
        worldCountries.put("DK", "Denmark");
        worldCountries.put("DJ", "Djibouti");
        worldCountries.put("DM", "Dominica");
        worldCountries.put("DO", "Dominican Republic");
        worldCountries.put("EC", "Ecuador");
        worldCountries.put("EG", "Egypt");
        worldCountries.put("SV", "El Salvador");
        worldCountries.put("GQ", "Equatorial Guinea");
        worldCountries.put("ER", "Eritrea");
        worldCountries.put("EE", "Estonia");
        worldCountries.put("ET", "Ethiopia");
        worldCountries.put("FK", "Falkland Islands (Malvinas)");
        worldCountries.put("FO", "Faroe Islands");
        worldCountries.put("FJ", "Fiji");
        worldCountries.put("FI", "Finland");
        worldCountries.put("FR", "France");
        worldCountries.put("GF", "French Guiana");
        worldCountries.put("PF", "French Polynesia");
        worldCountries.put("TF", "French Southern Territories");
        worldCountries.put("GA", "Gabon");
        worldCountries.put("GM", "Gambia");
        worldCountries.put("GE", "Georgia");
        worldCountries.put("DE", "Germany");
        worldCountries.put("GH", "Ghana");
        worldCountries.put("GI", "Gibraltar");
        worldCountries.put("GR", "Greece");
        worldCountries.put("GL", "Greenland");
        worldCountries.put("GD", "Grenada");
        worldCountries.put("GP", "Guadeloupe");
        worldCountries.put("GU", "Guam");
        worldCountries.put("GT", "Guatemala");
        worldCountries.put("GG", "Guernsey");
        worldCountries.put("GN", "Guinea");
        worldCountries.put("GW", "Guinea-Bissau");
        worldCountries.put("GY", "Guyana");
        worldCountries.put("HT", "Haiti");
        worldCountries.put("HM", "Heard and Mcdonald Islands");
        worldCountries.put("VA", "Holy SeeÂ (Vatican City State)");
        worldCountries.put("HN", "Honduras");
        worldCountries.put("HU", "Hungary");
        worldCountries.put("IS", "Iceland");
        worldCountries.put("IN", "India");
        worldCountries.put("ID", "Indonesia");
        worldCountries.put("IR", "Iran, Islamic Republic of");
        worldCountries.put("IQ", "Iraq");
        worldCountries.put("IE", "Ireland");
        worldCountries.put("IM", "Isle of Man");
        worldCountries.put("IL", "Israel");
        worldCountries.put("IT", "Italy");
        worldCountries.put("JM", "Jamaica");
        worldCountries.put("JP", "Japan");
        worldCountries.put("JE", "Jersey");
        worldCountries.put("JO", "Jordan");
        worldCountries.put("KZ", "Kazakhstan");
        worldCountries.put("KE", "Kenya");
        worldCountries.put("KI", "Kiribati");
        worldCountries.put("KP", "KoreaÂ (North)");
        worldCountries.put("KR", "KoreaÂ (South)");
        worldCountries.put("KW", "Kuwait");
        worldCountries.put("KG", "Kyrgyzstan");
        worldCountries.put("LA", "Lao PDR");
        worldCountries.put("LV", "Latvia");
        worldCountries.put("LB", "Lebanon");
        worldCountries.put("LS", "Lesotho");
        worldCountries.put("LR", "Liberia");
        worldCountries.put("LY", "Libya");
        worldCountries.put("LI", "Liechtenstein");
        worldCountries.put("LT", "Lithuania");
        worldCountries.put("LU", "Luxembourg");
        worldCountries.put("MK", "Macedonia, Republic of");
        worldCountries.put("MG", "Madagascar");
        worldCountries.put("MW", "Malawi");
        worldCountries.put("MY", "Malaysia");
        worldCountries.put("MV", "Maldives");
        worldCountries.put("ML", "Mali");
        worldCountries.put("MT", "Malta");
        worldCountries.put("MH", "Marshall Islands");
        worldCountries.put("MQ", "Martinique");
        worldCountries.put("MR", "Mauritania");
        worldCountries.put("MU", "Mauritius");
        worldCountries.put("YT", "Mayotte");
        worldCountries.put("MX", "Mexico");
        worldCountries.put("FM", "Micronesia, Federated States of");
        worldCountries.put("MD", "Moldova");
        worldCountries.put("MC", "Monaco");
        worldCountries.put("MN", "Mongolia");
        worldCountries.put("ME", "Montenegro");
        worldCountries.put("MS", "Montserrat");
        worldCountries.put("MA", "Morocco");
        worldCountries.put("MZ", "Mozambique");
        worldCountries.put("MM", "Myanmar");
        worldCountries.put("NA", "Namibia");
        worldCountries.put("NR", "Nauru");
        worldCountries.put("NP", "Nepal");
        worldCountries.put("NL", "Netherlands");
        worldCountries.put("AN", "Netherlands Antilles");
        worldCountries.put("NC", "New Caledonia");
        worldCountries.put("NZ", "New Zealand");
        worldCountries.put("NI", "Nicaragua");
        worldCountries.put("NE", "Niger");
        worldCountries.put("NG", "Nigeria");
        worldCountries.put("NU", "Niue");
        worldCountries.put("NF", "Norfolk Island");
        worldCountries.put("MP", "Northern Mariana Islands");
        worldCountries.put("NO", "Norway");
        worldCountries.put("OM", "Oman");
        worldCountries.put("PK", "Pakistan");
        worldCountries.put("PW", "Palau");
        worldCountries.put("PS", "Palestinian Territory");
        worldCountries.put("PA", "Panama");
        worldCountries.put("PG", "Papua New Guinea");
        worldCountries.put("PY", "Paraguay");
        worldCountries.put("PE", "Peru");
        worldCountries.put("PH", "Philippines");
        worldCountries.put("PN", "Pitcairn");
        worldCountries.put("PL", "Poland");
        worldCountries.put("PT", "Portugal");
        worldCountries.put("PR", "Puerto Rico");
        worldCountries.put("QA", "Qatar");
        worldCountries.put("RE", "Reunion");
        worldCountries.put("RO", "Romania");
        worldCountries.put("RU", "Russian Federation");
        worldCountries.put("RW", "Rwanda");
        worldCountries.put("BL", "Saint-BarthÃ©lemy");
        worldCountries.put("SH", "Saint Helena");
        worldCountries.put("KN", "Saint Kitts and Nevis");
        worldCountries.put("LC", "Saint Lucia");
        worldCountries.put("MF", "Saint-Martin (French part)");
        worldCountries.put("PM", "Saint Pierre and Miquelon");
        worldCountries.put("VC", "Saint Vincent and Grenadines");
        worldCountries.put("WS", "Samoa");
        worldCountries.put("SM", "San Marino");
        worldCountries.put("ST", "Sao Tome and Principe");
        worldCountries.put("SA", "Saudi Arabia");
        worldCountries.put("SN", "Senegal");
        worldCountries.put("RS", "Serbia");
        worldCountries.put("SC", "Seychelles");
        worldCountries.put("SL", "Sierra Leone");
        worldCountries.put("SG", "Singapore");
        worldCountries.put("SK", "Slovakia");
        worldCountries.put("SI", "Slovenia");
        worldCountries.put("SB", "Solomon Islands");
        worldCountries.put("SO", "Somalia");
        worldCountries.put("ZA", "South Africa");
        worldCountries.put("GS", "South Georgia and the South Sandwich Islands");
        worldCountries.put("SS", "South Sudan");
        worldCountries.put("ES", "Spain");
        worldCountries.put("LK", "Sri Lanka");
        worldCountries.put("SD", "Sudan");
        worldCountries.put("SR", "Suriname");
        worldCountries.put("SJ", "Svalbard and Jan Mayen Islands");
        worldCountries.put("SZ", "Swaziland");
        worldCountries.put("SE", "Sweden");
        worldCountries.put("CH", "Switzerland");
        worldCountries.put("SY", "Syrian Arab RepublicÂ (Syria)");
        worldCountries.put("TW", "Taiwan, Republic of China");
        worldCountries.put("TJ", "Tajikistan");
        worldCountries.put("TZ", "Tanzania, United Republic of");
        worldCountries.put("TH", "Thailand");
        worldCountries.put("TL", "Timor-Leste");
        worldCountries.put("TG", "Togo");
        worldCountries.put("TK", "Tokelau");
        worldCountries.put("TO", "Tonga");
        worldCountries.put("TT", "Trinidad and Tobago");
        worldCountries.put("TN", "Tunisia");
        worldCountries.put("TR", "Turkey");
        worldCountries.put("TM", "Turkmenistan");
        worldCountries.put("TC", "Turks and Caicos Islands");
        worldCountries.put("TV", "Tuvalu");
        worldCountries.put("UG", "Uganda");
        worldCountries.put("UA", "Ukraine");
        worldCountries.put("AE", "United Arab Emirates");
        worldCountries.put("GB", "United Kingdom");
        worldCountries.put("US", "United States");
        worldCountries.put("UM", "US Minor Outlying Islands");
        worldCountries.put("UY", "Uruguay");
        worldCountries.put("UZ", "Uzbekistan");
        worldCountries.put("VU", "Vanuatu");
        worldCountries.put("VE", "VenezuelaÂ (Bolidouble ian Republic)");
        worldCountries.put("VN", "Viet Nam");
        worldCountries.put("VI", "Virgin Islands, US");
        worldCountries.put("WF", "Wallis and Futuna Islands");
        worldCountries.put("EH", "Western Sahara");
        worldCountries.put("YE", "Yemen");
        worldCountries.put("ZM", "Zambia");
        worldCountries.put("ZW", "Zimbabwe");
    };
    
    // Maps a country's full name to its two-letter code
    public static LinkedHashMap<String, String> worldCountryCodes;
    static
    {
        worldCountryCodes = new LinkedHashMap<String, String>();

        worldCountryCodes.put( "Afghanistan", "AF" );
        worldCountryCodes.put( "Aland Islands", "AX" );
        worldCountryCodes.put( "Albania", "AL" );
        worldCountryCodes.put( "Algeria", "DZ" );
        worldCountryCodes.put( "American Samoa", "AS" );
        worldCountryCodes.put( "Andorra", "AD" );
        worldCountryCodes.put( "Angola", "AO" );
        worldCountryCodes.put( "Anguilla", "AI" );
        worldCountryCodes.put( "Antarctica", "AQ" );
        worldCountryCodes.put( "Antigua and Barbuda", "AG" );
        worldCountryCodes.put( "Antigua & Barbuda", "AG" );
        worldCountryCodes.put( "Argentina", "AR" );
        worldCountryCodes.put( "Armenia", "AM" );
        worldCountryCodes.put( "Aruba", "AW" );
        worldCountryCodes.put( "Australia", "AU" );
        worldCountryCodes.put( "Austria", "AT" );
        worldCountryCodes.put( "Azerbaijan", "AZ" );
        worldCountryCodes.put( "Bahamas", "BS" );
        worldCountryCodes.put( "Bahrain", "BH" );
        worldCountryCodes.put( "Bangladesh", "BD" );
        worldCountryCodes.put( "Barbados", "BB" );
        worldCountryCodes.put( "Belarus", "BY" );
        worldCountryCodes.put( "Belgium", "BE" );
        worldCountryCodes.put( "Belize", "BZ" );
        worldCountryCodes.put( "Benin", "BJ" );
        worldCountryCodes.put( "Bermuda", "BM" );
        worldCountryCodes.put( "Bhutan", "BT" );
        worldCountryCodes.put( "Bolivia", "BO" );
        worldCountryCodes.put( "Bosnia and Herzegovina", "BA" );
        worldCountryCodes.put( "Bosnia & Herzegovina", "BA" );
        worldCountryCodes.put( "Botswana", "BW" );
        worldCountryCodes.put( "Bouvet Island", "BV" );
        worldCountryCodes.put( "Brazil", "BR" );
        worldCountryCodes.put( "British Virgin Islands", "VG" );
        worldCountryCodes.put( "British Indian Ocean Territory", "IO" );
        worldCountryCodes.put( "Brunei Darussalam", "BN" );
        worldCountryCodes.put( "Bulgaria", "BG" );
        worldCountryCodes.put( "Burkina Faso", "BF" );
        worldCountryCodes.put( "Burundi", "BI" );
        worldCountryCodes.put( "Cambodia", "KH" );
        worldCountryCodes.put( "Cameroon", "CM" );
        worldCountryCodes.put( "Canada", "CA" );
        worldCountryCodes.put( "Cape Verde", "CV" );
        worldCountryCodes.put( "Cayman Islands", "KY" );
        worldCountryCodes.put( "Central African Republic", "CF" );
        worldCountryCodes.put( "Chad", "TD" );
        worldCountryCodes.put( "Chile", "CL" );
        worldCountryCodes.put( "China", "CN" );
        worldCountryCodes.put( "Hong Kong, SAR China", "HK" );
        worldCountryCodes.put( "Hong Kong", "HK" );
        worldCountryCodes.put( "Macao, SAR China", "MO" );
        worldCountryCodes.put( "Macao", "MO" );
        worldCountryCodes.put( "Christmas Island", "CX" );
        worldCountryCodes.put( "Cocos (Keeling) Islands", "CC" );
        worldCountryCodes.put( "Cocos Islands", "CC" );
        worldCountryCodes.put( "Colombia", "CO" );
        worldCountryCodes.put( "Comoros", "KM" );
        worldCountryCodes.put( "Congo (Brazzaville)", "CG" );
        worldCountryCodes.put( "Congo (Kinshasa)", "CD" );
        worldCountryCodes.put( "Cook Islands", "CK" );
        worldCountryCodes.put( "Costa Rica", "CR" );
        worldCountryCodes.put( "Côte d'Ivoire", "CI" );
        worldCountryCodes.put( "Croatia", "HR" );
        worldCountryCodes.put( "Cuba", "CU" );
        worldCountryCodes.put( "Cyprus", "CY" );
        worldCountryCodes.put( "Czech Republic", "CZ" );
        worldCountryCodes.put( "Denmark", "DK" );
        worldCountryCodes.put( "Djibouti", "DJ" );
        worldCountryCodes.put( "Dominica", "DM" );
        worldCountryCodes.put( "Dominican Republic", "DO" );
        worldCountryCodes.put( "Ecuador", "EC" );
        worldCountryCodes.put( "Egypt", "EG" );
        worldCountryCodes.put( "El Salvador", "SV" );
        worldCountryCodes.put( "Equatorial Guinea", "GQ" );
        worldCountryCodes.put( "Eritrea", "ER" );
        worldCountryCodes.put( "Estonia", "EE" );
        worldCountryCodes.put( "Ethiopia", "ET" );
        worldCountryCodes.put( "Falkland Islands (Malvinas)", "FK" );
        worldCountryCodes.put( "Faroe Islands", "FO" );
        worldCountryCodes.put( "Fiji", "FJ" );
        worldCountryCodes.put( "Finland", "FI" );
        worldCountryCodes.put( "France", "FR" );
        worldCountryCodes.put( "French Guiana", "GF" );
        worldCountryCodes.put( "French Polynesia", "PF" );
        worldCountryCodes.put( "French Southern Territories", "TF" );
        worldCountryCodes.put( "Gabon", "GA" );
        worldCountryCodes.put( "Gambia", "GM" );
        worldCountryCodes.put( "Georgia", "GE" );
        worldCountryCodes.put( "Germany", "DE" );
        worldCountryCodes.put( "Ghana", "GH" );
        worldCountryCodes.put( "Gibraltar", "GI" );
        worldCountryCodes.put( "Greece", "GR" );
        worldCountryCodes.put( "Greenland", "GL" );
        worldCountryCodes.put( "Grenada", "GD" );
        worldCountryCodes.put( "Guadeloupe", "GP" );
        worldCountryCodes.put( "Guam", "GU" );
        worldCountryCodes.put( "Guatemala", "GT" );
        worldCountryCodes.put( "Guernsey", "GG" );
        worldCountryCodes.put( "Guinea", "GN" );
        worldCountryCodes.put( "Guinea-Bissau", "GW" );
        worldCountryCodes.put( "Guyana", "GY" );
        worldCountryCodes.put( "Haiti", "HT" );
        worldCountryCodes.put( "Heard and Mcdonald Islands", "HM" );
        worldCountryCodes.put( "Heard & Mcdonald Islands", "HM" );
        worldCountryCodes.put( "Vatican City", "VA" );
        worldCountryCodes.put( "Vatican", "VA" );
        worldCountryCodes.put( "Honduras", "HN" );
        worldCountryCodes.put( "Hungary", "HU" );
        worldCountryCodes.put( "Iceland", "IS" );
        worldCountryCodes.put( "India", "IN" );
        worldCountryCodes.put( "Indonesia", "ID" );
        worldCountryCodes.put( "Iran, Islamic Republic of", "IR" );
        worldCountryCodes.put( "Iran", "IR" );
        worldCountryCodes.put( "Iraq", "IQ" );
        worldCountryCodes.put( "Ireland", "IE" );
        worldCountryCodes.put( "Isle of Man", "IM" );
        worldCountryCodes.put( "Israel", "IL" );
        worldCountryCodes.put( "Italy", "IT" );
        worldCountryCodes.put( "Jamaica", "JM" );
        worldCountryCodes.put( "Japan", "JP" );
        worldCountryCodes.put( "Jersey", "JE" );
        worldCountryCodes.put( "Jordan", "JO" );
        worldCountryCodes.put( "Kazakhstan", "KZ" );
        worldCountryCodes.put( "Kenya", "KE" );
        worldCountryCodes.put( "Kiribati", "KI" );
        worldCountryCodes.put( "Korea (North)", "KP" );
        worldCountryCodes.put( "North Korea", "KP" );
        worldCountryCodes.put( "Korea (South)", "KR" );
        worldCountryCodes.put( "South Korea", "KR" );
        worldCountryCodes.put( "Kuwait", "KW" );
        worldCountryCodes.put( "Kyrgyzstan", "KG" );
        worldCountryCodes.put( "Lao PDR", "LA" );
        worldCountryCodes.put( "Lao", "LA" );
        worldCountryCodes.put( "Latvia", "LV" );
        worldCountryCodes.put( "Lebanon", "LB" );
        worldCountryCodes.put( "Lesotho", "LS" );
        worldCountryCodes.put( "Liberia", "LR" );
        worldCountryCodes.put( "Libya", "LY" );
        worldCountryCodes.put( "Liechtenstein", "LI" );
        worldCountryCodes.put( "Lithuania", "LT" );
        worldCountryCodes.put( "Luxembourg", "LU" );
        worldCountryCodes.put( "Macedonia, Republic of", "MK" );
        worldCountryCodes.put( "Macedonia", "MK" );
        worldCountryCodes.put( "Madagascar", "MG" );
        worldCountryCodes.put( "Malawi", "MW" );
        worldCountryCodes.put( "Malaysia", "MY" );
        worldCountryCodes.put( "Maldives", "MV" );
        worldCountryCodes.put( "Mali", "ML" );
        worldCountryCodes.put( "Malta", "MT" );
        worldCountryCodes.put( "Marshall Islands", "MH" );
        worldCountryCodes.put( "Martinique", "MQ" );
        worldCountryCodes.put( "Mauritania", "MR" );
        worldCountryCodes.put( "Mauritius", "MU" );
        worldCountryCodes.put( "Mayotte", "YT" );
        worldCountryCodes.put( "Mexico", "MX" );
        worldCountryCodes.put( "Micronesia, Federated States of", "FM" );
        worldCountryCodes.put( "Micronesia", "FM" );
        worldCountryCodes.put( "Moldova", "MD" );
        worldCountryCodes.put( "Monaco", "MC" );
        worldCountryCodes.put( "Mongolia", "MN" );
        worldCountryCodes.put( "Montenegro", "ME" );
        worldCountryCodes.put( "Montserrat", "MS" );
        worldCountryCodes.put( "Morocco", "MA" );
        worldCountryCodes.put( "Mozambique", "MZ" );
        worldCountryCodes.put( "Myanmar", "MM" );
        worldCountryCodes.put( "Namibia", "NA" );
        worldCountryCodes.put( "Nauru", "NR" );
        worldCountryCodes.put( "Nepal", "NP" );
        worldCountryCodes.put( "Netherlands", "NL" );
        worldCountryCodes.put( "Netherlands Antilles", "AN" );
        worldCountryCodes.put( "New Caledonia", "NC" );
        worldCountryCodes.put( "New Zealand", "NZ" );
        worldCountryCodes.put( "Nicaragua", "NI" );
        worldCountryCodes.put( "Niger", "NE" );
        worldCountryCodes.put( "Nigeria", "NG" );
        worldCountryCodes.put( "Niue", "NU" );
        worldCountryCodes.put( "Norfolk Island", "NF" );
        worldCountryCodes.put( "Northern Mariana Islands", "MP" );
        worldCountryCodes.put( "Norway", "NO" );
        worldCountryCodes.put( "Oman", "OM" );
        worldCountryCodes.put( "Pakistan", "PK" );
        worldCountryCodes.put( "Palau", "PW" );
        worldCountryCodes.put( "Palestinian Territory", "PS" );
        worldCountryCodes.put( "Panama", "PA" );
        worldCountryCodes.put( "Papua New Guinea", "PG" );
        worldCountryCodes.put( "Paraguay", "PY" );
        worldCountryCodes.put( "Peru", "PE" );
        worldCountryCodes.put( "Philippines", "PH" );
        worldCountryCodes.put( "Pitcairn", "PN" );
        worldCountryCodes.put( "Poland", "PL" );
        worldCountryCodes.put( "Portugal", "PT" );
        worldCountryCodes.put( "Puerto Rico", "PR" );
        worldCountryCodes.put( "Qatar", "QA" );
        worldCountryCodes.put( "Reunion", "RE" );
        worldCountryCodes.put( "Romania", "RO" );
        worldCountryCodes.put( "Russian Federation", "RU" );
        worldCountryCodes.put( "Rwanda", "RW" );
        worldCountryCodes.put( "Saint Barthélemy", "BL" );
        worldCountryCodes.put( "Saint Helena", "SH" );
        worldCountryCodes.put( "Saint Kitts and Nevis", "KN" );
        worldCountryCodes.put( "Saint Kitts & Nevis", "KN" );
        worldCountryCodes.put( "Saint Lucia", "LC" );
        worldCountryCodes.put( "Saint-Martin (French part)", "MF" );
        worldCountryCodes.put( "Saint-Martin", "MF" );
        worldCountryCodes.put( "Saint Pierre and Miquelon", "PM" );
        worldCountryCodes.put( "Saint Pierre & Miquelon", "PM" );
        worldCountryCodes.put( "Saint Vincent and Grenadines", "VC" );
        worldCountryCodes.put( "Saint Vincent & Grenadines", "VC" );
        worldCountryCodes.put( "Samoa", "WS" );
        worldCountryCodes.put( "San Marino", "SM" );
        worldCountryCodes.put( "Sao Tome and Principe", "ST" );
        worldCountryCodes.put( "Sao Tome & Principe", "ST" );
        worldCountryCodes.put( "Saudi Arabia", "SA" );
        worldCountryCodes.put( "Senegal", "SN" );
        worldCountryCodes.put( "Serbia", "RS" );
        worldCountryCodes.put( "Seychelles", "SC" );
        worldCountryCodes.put( "Sierra Leone", "SL" );
        worldCountryCodes.put( "Singapore", "SG" );
        worldCountryCodes.put( "Slovakia", "SK" );
        worldCountryCodes.put( "Slovenia", "SI" );
        worldCountryCodes.put( "Solomon Islands", "SB" );
        worldCountryCodes.put( "Somalia", "SO" );
        worldCountryCodes.put( "South Africa", "ZA" );
        worldCountryCodes.put( "South Georgia and the South Sandwich Islands", "GS" );
        worldCountryCodes.put( "South Georgia & the South Sandwich Islands", "GS" );
        worldCountryCodes.put( "South Sudan", "SS" );
        worldCountryCodes.put( "Spain", "ES" );
        worldCountryCodes.put( "Sri Lanka", "LK" );
        worldCountryCodes.put( "Sudan", "SD" );
        worldCountryCodes.put( "Suriname", "SR" );
        worldCountryCodes.put( "Svalbard and Jan Mayen Islands", "SJ" );
        worldCountryCodes.put( "Svalbard & Jan Mayen Islands", "SJ" );
        worldCountryCodes.put( "Swaziland", "SZ" );
        worldCountryCodes.put( "Sweden", "SE" );
        worldCountryCodes.put( "Switzerland", "CH" );
        worldCountryCodes.put( "Syrian Arab Republic (Syria)", "SY" );
        worldCountryCodes.put( "Syria", "SY" );
        worldCountryCodes.put( "Taiwan, Republic of China", "TW" );
        worldCountryCodes.put( "Taiwan", "TW" );
        worldCountryCodes.put( "Tajikistan", "TJ" );
        worldCountryCodes.put( "Tanzania, United Republic of", "TZ" );
        worldCountryCodes.put( "Tanzania", "TZ" );
        worldCountryCodes.put( "Thailand", "TH" );
        worldCountryCodes.put( "Timor-Leste", "TL" );
        worldCountryCodes.put( "Togo", "TG" );
        worldCountryCodes.put( "Tokelau", "TK" );
        worldCountryCodes.put( "Tonga", "TO" );
        worldCountryCodes.put( "Trinidad and Tobago", "TT" );
        worldCountryCodes.put( "Trinidad & Tobago", "TT" );
        worldCountryCodes.put( "Tunisia", "TN" );
        worldCountryCodes.put( "Turkey", "TR" );
        worldCountryCodes.put( "Turkmenistan", "TM" );
        worldCountryCodes.put( "Turks and Caicos Islands", "TC" );
        worldCountryCodes.put( "Turks & Caicos Islands", "TC" );
        worldCountryCodes.put( "Tuvalu", "TV" );
        worldCountryCodes.put( "Uganda", "UG" );
        worldCountryCodes.put( "Ukraine", "UA" );
        worldCountryCodes.put( "United Arab Emirates", "AE" );
        worldCountryCodes.put( "United Kingdom", "GB" );
        worldCountryCodes.put( "United States", "US" );
        worldCountryCodes.put( "US Minor Outlying Islands", "UM" );
        worldCountryCodes.put( "Uruguay", "UY" );
        worldCountryCodes.put( "Uzbekistan", "UZ" );
        worldCountryCodes.put( "Vanuatu", "VU" );
        worldCountryCodes.put( "Venezuela (Bolidouble ian Republic)", "VE" );
        worldCountryCodes.put( "Venezuela", "VE" );
        worldCountryCodes.put( "Viet Nam", "VN" );
        worldCountryCodes.put( "Virgin Islands, US", "VI" );
        worldCountryCodes.put( "Wallis and Futuna Islands", "WF" );
        worldCountryCodes.put( "Western Sahara", "EH" );
        worldCountryCodes.put( "Yemen", "YE" );
        worldCountryCodes.put( "Zambia", "ZM" );
        worldCountryCodes.put( "Zimbabwe", "ZW" );
    }
    
    public static LinkedHashMap<String, String> worldCountriesByName;
    static
    {
			worldCountriesByName = new LinkedHashMap<String, String>();
			
			worldCountriesByName.put( "Afghanistan", "AF" );
            worldCountriesByName.put( "Aland Islands", "AX" );
            worldCountriesByName.put( "Albania", "AL" );
            worldCountriesByName.put( "Algeria", "DZ" );
            worldCountriesByName.put( "American Samoa", "AS" );
            worldCountriesByName.put( "Andorra", "AD" );
            worldCountriesByName.put( "Angola", "AO" );
            worldCountriesByName.put( "Anguilla", "AI" );
            worldCountriesByName.put( "Antarctica", "AQ" );
            worldCountriesByName.put( "Antigua and Barbuda", "AG" );
            worldCountriesByName.put( "Argentina", "AR" );
            worldCountriesByName.put( "Armenia", "AM" );
            worldCountriesByName.put( "Aruba", "AW" );
            worldCountriesByName.put( "Australia", "AU" );
            worldCountriesByName.put( "Austria", "AT" );
            worldCountriesByName.put( "Azerbaijan", "AZ" );
            worldCountriesByName.put( "Bahamas", "BS" );
            worldCountriesByName.put( "Bahrain", "BH" );
            worldCountriesByName.put( "Bangladesh", "BD" );
            worldCountriesByName.put( "Barbados", "BB" );
            worldCountriesByName.put( "Belarus", "BY" );
            worldCountriesByName.put( "Belgium", "BE" );
            worldCountriesByName.put( "Belize", "BZ" );
            worldCountriesByName.put( "Benin", "BJ" );
            worldCountriesByName.put( "Bermuda", "BM" );
            worldCountriesByName.put( "Bhutan", "BT" );
            worldCountriesByName.put( "Bolivia", "BO" );
            worldCountriesByName.put( "Bosnia and Herzegovina", "BA" );
            worldCountriesByName.put( "Botswana", "BW" );
            worldCountriesByName.put( "Bouvet Island", "BV" );
            worldCountriesByName.put( "Brazil", "BR" );
            worldCountriesByName.put( "British Virgin Islands", "VG" );
            worldCountriesByName.put( "British Indian Ocean Territory", "IO" );
            worldCountriesByName.put( "Brunei Darussalam", "BN" );
            worldCountriesByName.put( "Bulgaria", "BG" );
            worldCountriesByName.put( "Burkina Faso", "BF" );
            worldCountriesByName.put( "Burundi", "BI" );
            worldCountriesByName.put( "Cambodia", "KH" );
            worldCountriesByName.put( "Cameroon", "CM" );
            worldCountriesByName.put( "Canada", "CA" );
            worldCountriesByName.put( "Cape Verde", "CV" );
            worldCountriesByName.put( "Cayman Islands", "KY" );
            worldCountriesByName.put( "Central African Republic", "CF" );
            worldCountriesByName.put( "Chad", "TD" );
            worldCountriesByName.put( "Chile", "CL" );
            worldCountriesByName.put( "China", "CN" );
            worldCountriesByName.put( "Hong Kong", "HK" );
            worldCountriesByName.put( "Macao", "MO" );
            worldCountriesByName.put( "Christmas Island", "CX" );
            worldCountriesByName.put( "Cocos (Keeling) Islands", "CC" );
            worldCountriesByName.put( "Colombia", "CO" );
            worldCountriesByName.put( "Comoros", "KM" );
            worldCountriesByName.put( "Congo (Brazzaville)", "CG" );
            worldCountriesByName.put( "Congo", "CD" );
            worldCountriesByName.put( "Cook Islands", "CK" );
            worldCountriesByName.put( "Costa Rica", "CR" );
            worldCountriesByName.put( "Côte d'Ivoire", "CI" );
            worldCountriesByName.put( "Croatia", "HR" );
            worldCountriesByName.put( "Cuba", "CU" );
            worldCountriesByName.put( "Cyprus", "CY" );
            worldCountriesByName.put( "Czech Republic", "CZ" );
            worldCountriesByName.put( "Denmark", "DK" );
            worldCountriesByName.put( "Djibouti", "DJ" );
            worldCountriesByName.put( "Dominica", "DM" );
            worldCountriesByName.put( "Dominican Republic", "DO" );
            worldCountriesByName.put( "Ecuador", "EC" );
            worldCountriesByName.put( "Egypt", "EG" );
            worldCountriesByName.put( "El Salvador", "SV" );
            worldCountriesByName.put( "Equatorial Guinea", "GQ" );
            worldCountriesByName.put( "Eritrea", "ER" );
            worldCountriesByName.put( "Estonia", "EE" );
            worldCountriesByName.put( "Ethiopia", "ET" );
            worldCountriesByName.put( "Falkland Islands (Malvinas)", "FK" );
            worldCountriesByName.put( "Faroe Islands", "FO" );
            worldCountriesByName.put( "Fiji", "FJ" );
            worldCountriesByName.put( "Finland", "FI" );
            worldCountriesByName.put( "France", "FR" );
            worldCountriesByName.put( "French Guiana", "GF" );
            worldCountriesByName.put( "French Polynesia", "PF" );
            worldCountriesByName.put( "French Southern Territories", "TF" );
            worldCountriesByName.put( "Gabon", "GA" );
            worldCountriesByName.put( "Gambia", "GM" );
            worldCountriesByName.put( "Georgia", "GE" );
            worldCountriesByName.put( "Germany", "DE" );
            worldCountriesByName.put( "Ghana", "GH" );
            worldCountriesByName.put( "Gibraltar", "GI" );
            worldCountriesByName.put( "Greece", "GR" );
            worldCountriesByName.put( "Greenland", "GL" );
            worldCountriesByName.put( "Grenada", "GD" );
            worldCountriesByName.put( "Guadeloupe", "GP" );
            worldCountriesByName.put( "Guam", "GU" );
            worldCountriesByName.put( "Guatemala", "GT" );
            worldCountriesByName.put( "Guernsey", "GG" );
            worldCountriesByName.put( "Guinea", "GN" );
            worldCountriesByName.put( "Guinea-Bissau", "GW" );
            worldCountriesByName.put( "Guyana", "GY" );
            worldCountriesByName.put( "Haiti", "HT" );
            worldCountriesByName.put( "Heard and Mcdonald Islands", "HM" );
            worldCountriesByName.put( "Holy See (Vatican City State)", "VA" );
            worldCountriesByName.put( "Honduras", "HN" );
            worldCountriesByName.put( "Hungary", "HU" );
            worldCountriesByName.put( "Iceland", "IS" );
            worldCountriesByName.put( "India", "IN" );
            worldCountriesByName.put( "Indonesia", "ID" );
            worldCountriesByName.put( "Iran", "IR" );
            worldCountriesByName.put( "Iraq", "IQ" );
            worldCountriesByName.put( "Ireland", "IE" );
            worldCountriesByName.put( "Isle of Man", "IM" );
            worldCountriesByName.put( "Israel", "IL" );
            worldCountriesByName.put( "Italy", "IT" );
            worldCountriesByName.put( "Jamaica", "JM" );
            worldCountriesByName.put( "Japan", "JP" );
            worldCountriesByName.put( "Jersey", "JE" );
            worldCountriesByName.put( "Jordan", "JO" );
            worldCountriesByName.put( "Kazakhstan", "KZ" );
            worldCountriesByName.put( "Kenya", "KE" );
            worldCountriesByName.put( "Kiribati", "KI" );
            worldCountriesByName.put( "Korea (North)", "KP" );
            worldCountriesByName.put( "Korea (South)", "KR" );
            worldCountriesByName.put( "Kuwait", "KW" );
            worldCountriesByName.put( "Kyrgyzstan", "KG" );
            worldCountriesByName.put( "Lao PDR", "LA" );
            worldCountriesByName.put( "Latvia", "LV" );
            worldCountriesByName.put( "Lebanon", "LB" );
            worldCountriesByName.put( "Lesotho", "LS" );
            worldCountriesByName.put( "Liberia", "LR" );
            worldCountriesByName.put( "Libya", "LY" );
            worldCountriesByName.put( "Liechtenstein", "LI" );
            worldCountriesByName.put( "Lithuania", "LT" );
            worldCountriesByName.put( "Luxembourg", "LU" );
            worldCountriesByName.put( "Macedonia", "MK" );
            worldCountriesByName.put( "Madagascar", "MG" );
            worldCountriesByName.put( "Malawi", "MW" );
            worldCountriesByName.put( "Malaysia", "MY" );
            worldCountriesByName.put( "Maldives", "MV" );
            worldCountriesByName.put( "Mali", "ML" );
            worldCountriesByName.put( "Malta", "MT" );
            worldCountriesByName.put( "Marshall Islands", "MH" );
            worldCountriesByName.put( "Martinique", "MQ" );
            worldCountriesByName.put( "Mauritania", "MR" );
            worldCountriesByName.put( "Mauritius", "MU" );
            worldCountriesByName.put( "Mayotte", "YT" );
            worldCountriesByName.put( "Mexico", "MX" );
            worldCountriesByName.put( "Micronesia", "FM" );
            worldCountriesByName.put( "Moldova", "MD" );
            worldCountriesByName.put( "Monaco", "MC" );
            worldCountriesByName.put( "Mongolia", "MN" );
            worldCountriesByName.put( "Montenegro", "ME" );
            worldCountriesByName.put( "Montserrat", "MS" );
            worldCountriesByName.put( "Morocco", "MA" );
            worldCountriesByName.put( "Mozambique", "MZ" );
            worldCountriesByName.put( "Myanmar", "MM" );
            worldCountriesByName.put( "Namibia", "NA" );
            worldCountriesByName.put( "Nauru", "NR" );
            worldCountriesByName.put( "Nepal", "NP" );
            worldCountriesByName.put( "Netherlands", "NL" );
            worldCountriesByName.put( "Netherlands Antilles", "AN" );
            worldCountriesByName.put( "New Caledonia", "NC" );
            worldCountriesByName.put( "New Zealand", "NZ" );
            worldCountriesByName.put( "Nicaragua", "NI" );
            worldCountriesByName.put( "Niger", "NE" );
            worldCountriesByName.put( "Nigeria", "NG" );
            worldCountriesByName.put( "Niue", "NU" );
            worldCountriesByName.put( "Norfolk Island", "NF" );
            worldCountriesByName.put( "Northern Mariana Islands", "MP" );
            worldCountriesByName.put( "Norway", "NO" );
            worldCountriesByName.put( "Oman", "OM" );
            worldCountriesByName.put( "Pakistan", "PK" );
            worldCountriesByName.put( "Palau", "PW" );
            worldCountriesByName.put( "Palestinian Territory", "PS" );
            worldCountriesByName.put( "Panama", "PA" );
            worldCountriesByName.put( "Papua New Guinea", "PG" );
            worldCountriesByName.put( "Paraguay", "PY" );
            worldCountriesByName.put( "Peru", "PE" );
            worldCountriesByName.put( "Philippines", "PH" );
            worldCountriesByName.put( "Pitcairn", "PN" );
            worldCountriesByName.put( "Poland", "PL" );
            worldCountriesByName.put( "Portugal", "PT" );
            worldCountriesByName.put( "Puerto Rico", "PR" );
            worldCountriesByName.put( "Qatar", "QA" );
            worldCountriesByName.put( "Réunion", "RE" );
            worldCountriesByName.put( "Romania", "RO" );
            worldCountriesByName.put( "Russian Federation", "RU" );
            worldCountriesByName.put( "Rwanda", "RW" );
            worldCountriesByName.put( "Saint-Barthélemy", "BL" );
            worldCountriesByName.put( "Saint Helena", "SH" );
            worldCountriesByName.put( "Saint Kitts and Nevis", "KN" );
            worldCountriesByName.put( "Saint Lucia", "LC" );
            worldCountriesByName.put( "Saint-Martin (French part)", "MF" );
            worldCountriesByName.put( "Saint Pierre and Miquelon", "PM" );
            worldCountriesByName.put( "Saint Vincent and Grenadines", "VC" );
            worldCountriesByName.put( "Samoa", "WS" );
            worldCountriesByName.put( "San Marino", "SM" );
            worldCountriesByName.put( "Sao Tome and Principe", "ST" );
            worldCountriesByName.put( "Saudi Arabia", "SA" );
            worldCountriesByName.put( "Senegal", "SN" );
            worldCountriesByName.put( "Serbia", "RS" );
            worldCountriesByName.put( "Seychelles", "SC" );
            worldCountriesByName.put( "Sierra Leone", "SL" );
            worldCountriesByName.put( "Singapore", "SG" );
            worldCountriesByName.put( "Slovakia", "SK" );
            worldCountriesByName.put( "Slovenia", "SI" );
            worldCountriesByName.put( "Solomon Islands", "SB" );
            worldCountriesByName.put( "Somalia", "SO" );
            worldCountriesByName.put( "South Africa", "ZA" );
            worldCountriesByName.put( "South Georgia and the South Sandwich Islands", "GS" );
            worldCountriesByName.put( "South Sudan", "SS" );
            worldCountriesByName.put( "Spain", "ES" );
            worldCountriesByName.put( "Sri Lanka", "LK" );
            worldCountriesByName.put( "Sudan", "SD" );
            worldCountriesByName.put( "Suriname", "SR" );
            worldCountriesByName.put( "Svalbard and Jan Mayen Islands", "SJ" );
            worldCountriesByName.put( "Swaziland", "SZ" );
            worldCountriesByName.put( "Sweden", "SE" );
            worldCountriesByName.put( "Switzerland", "CH" );
            worldCountriesByName.put( "Syrian Arab Republic (Syria)", "SY" );
            worldCountriesByName.put( "Taiwan", "TW" );
            worldCountriesByName.put( "Tajikistan", "TJ" );
            worldCountriesByName.put( "Tanzania", "TZ" );
            worldCountriesByName.put( "Thailand", "TH" );
            worldCountriesByName.put( "Timor-Leste", "TL" );
            worldCountriesByName.put( "Togo", "TG" );
            worldCountriesByName.put( "Tokelau", "TK" );
            worldCountriesByName.put( "Tonga", "TO" );
            worldCountriesByName.put( "Trinidad and Tobago", "TT" );
            worldCountriesByName.put( "Tunisia", "TN" );
            worldCountriesByName.put( "Turkey", "TR" );
            worldCountriesByName.put( "Turkmenistan", "TM" );
            worldCountriesByName.put( "Turks and Caicos Islands", "TC" );
            worldCountriesByName.put( "Tuvalu", "TV" );
            worldCountriesByName.put( "Uganda", "UG" );
            worldCountriesByName.put( "Ukraine", "UA" );
            worldCountriesByName.put( "United Arab Emirates", "AE" );
            worldCountriesByName.put( "United Kingdom", "GB" );
            worldCountriesByName.put( "USA", "US" );
            worldCountriesByName.put( "United States", "US" );
            worldCountriesByName.put( "United States of America", "US" );
            worldCountriesByName.put( "US Minor Outlying Islands", "UM" );
            worldCountriesByName.put( "Uruguay", "UY" );
            worldCountriesByName.put( "Uzbekistan", "UZ" );
            worldCountriesByName.put( "Vanuatu", "VU" );
            worldCountriesByName.put( "Venezuela (Bolivarian Republic)", "VE" );
            worldCountriesByName.put( "Viet Nam", "VN" );
            worldCountriesByName.put( "Virgin Islands", "VI" );
            worldCountriesByName.put( "Wallis and Futuna Islands", "WF" );
            worldCountriesByName.put( "Western Sahara", "EH" );
            worldCountriesByName.put( "Yemen", "YE" );
            worldCountriesByName.put( "Zambia", "ZM" );
            worldCountriesByName.put( "Zimbabwe", "ZW" );
	}
	
    // Maps a weather reading to a specific asset icon file
    public static LinkedHashMap<String, String> weatherImages;
    static
    {
        weatherImages = new LinkedHashMap<>();

        weatherImages.put("tornado", "0.png");
        weatherImages.put("tropical storm", "1.png");
        weatherImages.put("hurricane", "1.png");
        weatherImages.put("severe thunderstorm", "1.png");
        weatherImages.put("severe thunderstorms", "1.png");
        weatherImages.put("thunderstorm", "1.png");
        weatherImages.put("thunderstorms", "1.png");
        weatherImages.put("tstorms", "1.png");
        weatherImages.put("t-storms", "1.png");        
        weatherImages.put("freezing rain", "2.png");
        weatherImages.put("mixed rain and hail", "2.png");
        weatherImages.put("mixed rain and snow", "2.png");
        weatherImages.put("mixed rain and sleet", "2.png");
        weatherImages.put("sleet", "2.png");
        weatherImages.put("light snow showers", "2.png");
        weatherImages.put("freezing drizzle", "2.png");
        weatherImages.put("blowing snow", "3.png");
        weatherImages.put("heavy snow", "3.png");
        weatherImages.put("mixed snow and sleet", "3.png");
        weatherImages.put("scattered snow showers", "3.png");
        weatherImages.put("snow", "3.png");
        weatherImages.put("snow showers", "3.png");
        weatherImages.put("drizzle", "4.png");
        weatherImages.put("light rain", "4.png");
        weatherImages.put("moderate rain", "4.png");
        weatherImages.put("sprinkles", "4.png");
        weatherImages.put("heavy intensity rain", "5.png");
        weatherImages.put("heavy rain", "5.png");
        weatherImages.put("heavy rain showers", "5.png");
        weatherImages.put("rain", "5.png");
        weatherImages.put("rain showers", "5.png");
        weatherImages.put("showers", "5.png");
        weatherImages.put("snow flurries", "5.png");
        weatherImages.put("hail", "5.png");
        weatherImages.put("dust", "6.png");
        weatherImages.put("smoky", "6.png");
        weatherImages.put("fog", "7.png");
        weatherImages.put("foggy", "7.png");
        weatherImages.put("haze", "7.png");
        weatherImages.put("mist", "7.png");
        weatherImages.put("misty", "7.png");
        weatherImages.put("clouds", "8.png");
        weatherImages.put("cloudy", "8.png");
        weatherImages.put("overcast", "8.png");
        weatherImages.put("broken clouds", "8.png");
        weatherImages.put("overcast clouds", "8.png");
        weatherImages.put("scattered clouds", "8.png");
        weatherImages.put("cold", "9.png");
        weatherImages.put("mostly cloudy", "10.png");
        weatherImages.put("mostly cloudy (night)", "11.png");
        weatherImages.put("few clouds", "12.png");
        weatherImages.put("humid", "12.png");
        weatherImages.put("humid and partly cloudy", "12.png");
        weatherImages.put("mostly clear", "12.png");
        weatherImages.put("partly cloudy", "12.png");
        weatherImages.put("mostly sunny", "12.png");
        weatherImages.put("few clouds (night)", "13.png");
        weatherImages.put("humid (night)", "13.png");
        weatherImages.put("mostly sunny (night)", "13.png");
        weatherImages.put("mostly clear (night)", "13.png");
        weatherImages.put("partly cloudy (night)", "13.png");
        weatherImages.put("clear (night)", "14.png");
        weatherImages.put("clear sky (night)", "14.png");
        weatherImages.put("fair (night)", "14.png");
        weatherImages.put("sunny (night)", "14.png");
        weatherImages.put("clear", "15.png");
        weatherImages.put("clear sky", "15.png");
        weatherImages.put("fair", "15.png");
        weatherImages.put("sunny", "15.png");
        weatherImages.put("hot", "15.png");
        weatherImages.put("sky is clear", "15.png");
        weatherImages.put("chance of a thunderstorm", "16.png");
        weatherImages.put("isolated t-storms", "16.png");
        weatherImages.put("isolated thunderstorms", "16.png");
        weatherImages.put("isolated thundershowers", "16.png");
        weatherImages.put("isolated t-showers", "16.png");
        weatherImages.put("scattered thunderstorms", "16.png");
        weatherImages.put("scattered t-storms", "16.png");
        weatherImages.put("scattered tstorms", "16.png");
        weatherImages.put("thundershowers", "16.png");        
        weatherImages.put("thunderstorm with rain", "16.png");
        weatherImages.put("scattered showers", "17.png");
        weatherImages.put("isolated showers", "17.png");
        weatherImages.put("chance of rain", "17.png");        
        weatherImages.put("light rain showers", "17.png");
        weatherImages.put("light shower rain", "17.png");
        weatherImages.put("chance of a thunderstorm (night)", "18.png");
        weatherImages.put("isolated thunderstorms (night)", "18.png");
        weatherImages.put("isolated t-storms (night)", "18.png");
        weatherImages.put("isolated thundershowers (night)", "18.png");
        weatherImages.put("isolated t-showers (night)", "18.png");
        weatherImages.put("scattered thunderstorms (night)", "18.png");
        weatherImages.put("scattered t-storms (night)", "18.png");
        weatherImages.put("thundershowers (night)", "18.png");
        weatherImages.put("t-showers (night)", "18.png");
        weatherImages.put("drizzle (night)", "19.png");
        weatherImages.put("isolated showers (night)", "19.png");
        weatherImages.put("light rain (night)", "19.png");
        weatherImages.put("scattered showers (night)", "19.png");
        weatherImages.put("dust (night)", "20.png");
        weatherImages.put("smoky (night)", "20.png");
        weatherImages.put("blustery (night)", "20.png");
        weatherImages.put("breezy (night)", "20.png");
        weatherImages.put("blustery", "20.png");
        weatherImages.put("windy (night)", "21.png");
        weatherImages.put("breez", "21.png");
        weatherImages.put("breeze", "21.png");
        weatherImages.put("breezy", "21.png");
        weatherImages.put("wind", "21.png");
        weatherImages.put("windy", "21.png");
        weatherImages.put("not available", "na.png");
    }

    public enum LogLevel
    {
        SEVERE,
        INFO,
        WARNING
    }
    
    public static Date lastUpdated;
    public static boolean refreshRequested;
    public static boolean weatherWidgetEnabled = true;   

	public static ArrayList<String> subDirectoriesFound = new ArrayList<>();
	private static File[] files;

	/** Weather Calculations **/
	
    /***
     * Calculate the wind chill for temperatures at or below 50° F and wind speeds above 3 mph
     * 
     * @param fTemp A temperature measured in Fahrenheit
     * @param mphWind A wind speed measure in miles per hour (mph)
     * @return A integer value representing the calculated wind chill.
     */
    public static int calculateWindChill( int fTemp, int mphWind ) 
    {
    	// The wind chill calculator only works for temperatures at or below 50 ° F
    	// and wind speeds above 3 mph.
    	if( fTemp > 50 || mphWind < 3 )
    	{
    		return fTemp;
    	}// end of if block
    	else
    	{
    		return (int) ( 35.74 + ( 0.6215 * fTemp ) - ( 35.75 * Math.pow( mphWind, 0.16 ) )
    				+ (0.4275 * fTemp * Math.pow( mphWind, 0.16 ) ) );
    	}// end of else block    	
    }// end of method calculateWindChill
    
    /***
     * Heat index computed using air temperature F and relative humidity
     * 
     * @param airTemp	The current air temperature reading
     * @param relativeHumidity The current relative humidity reading
     * @return A {@code double} representing the heat index value 
     * @author Kevin Sharp and Mark Klein
     * @see <a href='https://www.wpc.ncep.noaa.gov/html/heatindex.shtml' target='_top'>Noaa.gov</a>
     */
    public static double heatIndex( double airTemp, double relativeHumidity )
    {
    	double hi = 0;
   	 	double vaporPressure = 0;
        double satVaporPressure = 0;
        double airTempInFahrenheit  = 0;
        double hiTemp = 0;
        double fpTemp = 0;
        double hiFinal = 0;
        double adj1 = 0;
        double adj2 = 0;
        double adj = 0;
        
    	if( relativeHumidity > 100 )
        {
            return 0;
        }// end of if block
        else if( relativeHumidity < 0 )
        {
            return 0;
        }//end of else if block
        else if( airTemp <= 40.0 )
        {
            hi = airTemp;
        }//end of else if block
        else 
        {
            hiTemp = 61.0 + ( ( airTemp - 68.0 ) * 1.2 ) + ( relativeHumidity * 0.094 );
            hiFinal = 0.5* ( airTemp + hiTemp );

            if( hiFinal > 79.0 )
            {
                hi = -42.379 + 2.04901523
                	* airTemp + 10.14333127
                	* relativeHumidity - 0.22475541 
                	* airTemp * relativeHumidity
                	- 6.83783 * ( Math.pow( 10, -3 ) ) 
                	* ( Math.pow( airTemp, 2 ) ) - 5.481717 
                	* ( Math.pow( 10, -2 ) ) * ( Math.pow( relativeHumidity, 2 ) ) 
                	+ 1.22874 * ( Math.pow( 10, -3 ) ) * ( Math.pow( airTemp, 2 ) )
                	* relativeHumidity + 8.5282 * ( Math.pow( 10, -4 ) )
                	* airTemp * ( Math.pow( relativeHumidity, 2 ) ) 
                	- 1.99 * ( Math.pow( 10, -6 ) ) * ( Math.pow( airTemp, 2 ) )
                	* ( Math.pow( relativeHumidity, 2 ) );
                
                if( ( relativeHumidity <= 13 ) && ( airTemp >= 80.0 ) 
                		&& ( airTemp <= 112.0 ) )
                {
                    adj1 = ( 13.0 - relativeHumidity ) / 4.0;
                    adj2 = Math.sqrt( ( 17.0 - Math.abs( airTemp - 95.0 ) ) / 17.0 );
                    adj = adj1 * adj2;
                    hi = hi - adj;
                }// end of if block
                else if( ( relativeHumidity > 85.0 ) && ( airTemp >= 80.0 )
                	&& ( airTemp <= 87.0 ) )
                {
                    adj1 = ( relativeHumidity - 85.0 ) / 10.0;
                    adj2 = ( 87.0 - airTemp ) / 5.0;
                    adj = adj1 * adj2;
                    hi = hi + adj;
                }// end of else if block
            }// end of if block
            else
            {
                hi = hiFinal;
            }// end of else block
        }// end of else block
            	
        double  tempc2 = ( airTemp - 32 ) * .556;
        double  rh2 = 1 - relativeHumidity/100;
        double  tdpc2 = tempc2 - ( ( ( 14.55 + .114* tempc2 ) * rh2 ) 
        		+ ( Math.pow( ( ( 2.5 + .007 * tempc2 ) * rh2 ), 3 ) )
        		+ ( ( 15.9 + .117 * tempc2 ) ) * ( Math.pow( rh2, 14 ) ) );
       
        return Math.round( hi );
    }// end of method heatIndex
    
    /***
     * Heat index computed using air temperature and dew point temperature. Degrees F
     * <br />
     * Steps to calculate:
     * <ol>
     *		<li>Convert T and Td to degrees C</li>
     * 		<li>Using T and Td, calculate the vapor pressure and saturation vapor pressure.</li>
     * 		<li>Calculate RH = (E/Es) * 100</li>
     * </ol>
     * 
     * @param airTemp	The current air temperature reading
     * @param dewPoint The current dew point reading
     * @return A {@code double} representing the heat index value 
     * @author Kevin Sharp and Mark Klein
     * @see <a href='https://www.wpc.ncep.noaa.gov/html/heatindex.shtml' target='_top'>Noaa.gov</a>
     */    
    public static double heatIndexDew( double airTemp, double dewPoint )
    {
    	double hi = 0;
   	 	double vaporPressure = 0;
        double satVaporPressure = 0;
        double airTempInFahrenheit  = 0;
        double relativeHumidity = 0;
        double hiTemp = 0;
        double fpTemp = 0;
        double hiFinal = 0;
        double adj1 = 0;
        double adj2 = 0;
        double adj = 0;
    	
    	double tc2 = ( airTemp - 32) * .556;
    	double tdc2 = ( dewPoint -32 )* .556;
     
    	if ( tc2 < tdc2 )
    	{
    		return 0;
    	}// end of if block
	    else if( airTemp <= 40.0 )
	    {
	         hi = airTemp;
	    }// end of else if block
	    else
	    {
	         vaporPressure = 6.11 * ( Math.pow( 10, 7.5 * ( tdc2 / ( 237.7 + tdc2 ) ) ) );
	         satVaporPressure = 6.11 * ( Math.pow( 10, 7.5 *( tc2 / ( 237.7 + tc2 ) ) ) );
	         relativeHumidity = Math.round( 100.0 * ( vaporPressure / satVaporPressure ) );
	         hiTemp = 61.0 + ( ( airTemp - 68.0 ) * 1.2) + ( relativeHumidity *0.094 );
	         hiFinal = 0.5 * ( airTemp + hiTemp );  
	  
	         if( hiFinal > 79.0 )
	         {
	             hi = -42.379 + 2.04901523 * airTemp 
	            	 + 10.14333127 * relativeHumidity 
	            	 - 0.22475541 * airTemp
	            	 * relativeHumidity - 6.83783
	            	 * ( Math.pow( 10, -3 ) ) * ( Math.pow( airTemp, 2 ) )
	            	 - 5.481717 * ( Math.pow( 10, -2 ) )
	            	 * ( Math.pow( relativeHumidity, 2 ) ) 
	            	 + 1.22874 * ( Math.pow( 10, -3 ) )
	            	 * ( Math.pow( airTemp, 2 ) ) * relativeHumidity
	            	 + 8.5282 * ( Math.pow( 10, -4 ) )
	            	 * airTemp * ( Math.pow( relativeHumidity, 2 ) )
	            	 - 1.99 * ( Math.pow( 10, -6 ) )
	            	 * ( Math.pow( airTemp, 2 ) ) * ( Math.pow( relativeHumidity, 2 ) );
	
	             	if( ( relativeHumidity <= 13.0 ) && ( airTemp >= 80.0 )
	             		&& ( airTemp <= 112.0 ) )
	             	{
	             		adj1 = ( 13.0 - relativeHumidity ) / 4.0;
	             		adj2 = Math.sqrt( ( 17.0 - Math.abs( airTemp - 95.0 ) ) / 17.0 );
	             		adj = adj1 * adj2;
	             		hi = hi - adj;
	             	}// end of if block
	             	else if( ( relativeHumidity > 85.0 ) && ( airTemp >= 80.0 )
	             		&& ( airTemp <= 87.0 ) )
	             	{
	             		adj1 = ( relativeHumidity - 85.0 ) /10.0;
	             		adj2 = ( 87.0 - airTemp ) / 5.0;
	             		adj = adj1 * adj2;
	             		hi = hi + adj; 
	             	}// end of else if block 
	         }// end of if block
	         else
	         {
	        	 hi = hiFinal;
	         }// end of else block
	     }// end of else block
    	
	     String answer = Math.round( hi ) + " F" +  " / " 
	    		 + Math.round( ( hi - 32 ) * .556 ) + " C";
	     String relativeHumidityS = relativeHumidity + "%";
	     
	     return  Math.round( hi );
    }// end of method heatIndexDew
    
    /***
     * Heat index computed using air temperature C and relative humidity
     * 
     * @param airTempCelsius	The current air temperature reading
     * @param relativeHumidity The current relative humidity reading
     * @return A {@code double} representing the heat index value 
     * @author Kevin Sharp and Mark Klein
     * @see <a href='https://www.wpc.ncep.noaa.gov/html/heatindex.shtml' target='_top'>Noaa.gov</a>
     */
    public static double heatIndexCelsius( double airTempCelsius, double relativeHumidity )
    {
    	double hi = 0;
    	double tempAirInFahrenheit = 0;
        double hiTemp = 0;
        double fpTemp = 0;
        double hiFinal = 0;
        double adj1 = 0;
        double adj2 = 0;
        double adj = adj1 * adj2;
    	
        if( relativeHumidity > 100 )
        {
        	return 0;
        }// end of if block
        else if ( relativeHumidity < 0 )
        {
        	return 0;
        }// end of else if block
        else if ( airTempCelsius <= 4.44 )
        {
            hi = airTempCelsius;
        }// end of else if block
        else
        {
            tempAirInFahrenheit = 1.80 * airTempCelsius+ 32.0;
            hiTemp = 61.0 + ( ( tempAirInFahrenheit - 68.0) * 1.2 ) + ( relativeHumidity * 0.094 );
            fpTemp = airTempCelsius;
            hiFinal = 0.5 * ( fpTemp + hiTemp );

            if( hiFinal > 79.0 )
            {
                hi = -42.379 + 2.04901523 
            		* tempAirInFahrenheit + 10.14333127 
            		* relativeHumidity - 0.22475541 
            		* tempAirInFahrenheit * relativeHumidity 
            		- 6.83783 * ( Math.pow( 10, -3 ) )
            		* ( Math.pow( tempAirInFahrenheit, 2 ) ) 
            		- 5.481717 * ( Math.pow( 10, -2 ) ) 
            		* ( Math.pow( relativeHumidity, 2 ) )
            		+ 1.22874 * ( Math.pow( 10, -3 ) ) 
            		* ( Math.pow( tempAirInFahrenheit, 2 ) )
            		* relativeHumidity + 8.5282 
            		* ( Math.pow( 10, -4 ) ) * tempAirInFahrenheit 
            		* ( Math.pow( relativeHumidity, 2 ) )
            		- 1.99 * ( Math.pow( 10, -6 ) ) 
            		* ( Math.pow( tempAirInFahrenheit, 2 ) )
            		* ( Math.pow( relativeHumidity, 2 ) );

                if( ( relativeHumidity <= 13 ) && ( tempAirInFahrenheit >= 80.0 )
                		&& ( tempAirInFahrenheit <= 112.0 ) )
                {
                    adj1 = ( 13.0 - relativeHumidity ) / 4.0;
                    adj2 = Math.sqrt( ( 17.0 - Math.abs( tempAirInFahrenheit - 95.0 ) ) /17.0 );
                    adj = adj1 * adj2;
                    hi = hi - adj;
                }// end of if block
                else if( ( relativeHumidity > 85.0 ) && ( tempAirInFahrenheit >= 80.0 )
                		&& ( tempAirInFahrenheit <= 87.0 ) )
                {
                    adj1 = ( relativeHumidity - 85.0 ) / 10.0;
                    adj2 = ( 87.0 - tempAirInFahrenheit ) / 5.0;
                    adj = adj1 * adj2;
                    hi = hi + adj;
                }// end of else if block
            }// end of if block
            else
            {
            	hi = hiFinal;
            }// end of else block
        }
        
        String heatIndexS = Math.round( hi ) + " F"  
        + " / " + Math.round( ( hi - 32 ) * .556 ) + " C";
        double rh3 = 1 - relativeHumidity / 100;
        double tdpc3 = airTempCelsius - ( ( ( 14.55 + .114 * airTempCelsius ) * rh3 )
        		+ ( Math.pow( ( ( 2.5 + .007 * airTempCelsius ) * rh3 ), 3 ) )
        		+ ( (15.9 + .117 * airTempCelsius ) ) * ( Math.pow( rh3, 14 ) ) );
        String dewpt = Math.round( 1.80 * tdpc3 + 32.0 ) 
        		+ " F" + " / " + Math.round( tdpc3 ) + " C";
        
        return Math.round( ( hi - 32 ) * .556 );
    }// end of method heatIndexCelsius
    
    /***
     * Heat index computed using air temperature and dew point temperature. Degrees C
     * 
     * @param airTempCelsius	The current air temperature reading
     * @param dewPointCelsius The current dew point reading
     * @return A {@code double} representing the heat index value 
     * @author Kevin Sharp and Mark Klein
     * @see <a href='https://www.wpc.ncep.noaa.gov/html/heatindex.shtml' target='_top'>Noaa.gov</a>
     */    
    public static double heatIndexDewCelsius( double airTempCelsius, double dewPointCelsius )
    {
    	 double hi = 0;
    	 double vaporPressure = 0;
         double satVaporPressure = 0;
         double relativeHumidity  = 0;
         double airTempInFahrenheit  = 0;
         double hiTemp = 0;
         double fpTemp = 0;
         double hiFinal = 0;
         double adj1 = 0;
         double adj2 = 0;
         double adj = 0;
    	
    	if ( airTempCelsius <  dewPointCelsius )
        {
            return 0;
        }// end of if block
        else if( airTempCelsius <= 4.44 )
        {
            hi = airTempCelsius;
        }// end of else if block
        else
        {
           vaporPressure = 6.11 * ( Math.pow( 10, 7.5 * 
        		          ( dewPointCelsius / ( 237.7 + dewPointCelsius ) ) ) );
           satVaporPressure = 6.11 * ( Math.pow( 10, 7.5 * 
        		         ( airTempCelsius / ( 237.7 + airTempCelsius ) ) ) );
           relativeHumidity = Math.round( 100.0 * ( vaporPressure / satVaporPressure ) );
           airTempInFahrenheit = 1.80 * airTempCelsius + 32.0;
           hiTemp = 61.0 + ( ( airTempInFahrenheit - 68.0 ) * 1.2 ) 
        		   + ( relativeHumidity * 0.094 );
           fpTemp = airTempInFahrenheit;
           hiFinal = 0.5 * ( fpTemp + hiTemp );

           if( hiFinal > 79.0 )
           {
        	   hi = -42.379 + 2.04901523 * airTempInFahrenheit
        			+ 10.14333127 * relativeHumidity - 0.22475541 
        			* airTempInFahrenheit * relativeHumidity 
        			- 6.83783 * ( Math.pow( 10, -3 ) ) 
        			* ( Math.pow( airTempInFahrenheit, 2 ) ) 
        			- 5.481717 * ( Math.pow( 10, -2 ) ) 
        			* ( Math.pow( relativeHumidity, 2 ) ) 
        			+ 1.22874 * ( Math.pow( 10, -3 ) ) 
        			* ( Math.pow( airTempInFahrenheit, 2 ) )
        			* relativeHumidity + 8.5282 
        			* ( Math.pow( 10, -4 ) )
        			* airTempInFahrenheit * ( Math.pow( relativeHumidity, 2) ) 
        			- 1.99 * ( Math.pow( 10, -6 ) ) * ( Math.pow( airTempInFahrenheit, 2 ) )
        			* ( Math.pow( relativeHumidity, 2 ) );

                if( ( relativeHumidity <= 13.0 ) && ( airTempInFahrenheit >= 80.0 )
                	&& ( airTempInFahrenheit <= 112.0 ) )
                {
                    adj1 = ( 13.0 - relativeHumidity ) / 4.0;
                    adj2 = Math.sqrt( ( 17.0 - Math.abs( airTempInFahrenheit -95.0 ) ) /17.0 );
                    adj = adj1 * adj2;
                    hi = hi - adj;
                }// end of if block
                else if( ( relativeHumidity > 85.0 ) && ( airTempInFahrenheit >= 80.0 )
                	&& ( airTempInFahrenheit <= 87.0 ) )
                {
                    adj1 = ( relativeHumidity - 85.0 ) / 10.0;
                    adj2 = ( 87.0 - airTempInFahrenheit ) / 5.0;
                    adj = adj1 * adj2;
                    hi = hi + adj;
                }// end of else if block
            }// end of if block
            else 
            {
                hi = hiFinal;
            }// end of else block
        }// end of else block
    	
    	String heatDewCelsius =
    		   Math.round( hi ) + " F" +  " / " + Math.round( ( hi - 32 ) * .556 ) + " C";
        String heatDewCelsiusRelativeHumidity = relativeHumidity + "%";
        
        return Math.round( ( hi - 32 ) * .556 );
    }// end of method heatIndexDewPointCelsius
 
    /** Unit Conversions **/
    
    /**
     * Accepts a numeric value of type float that represents
     * a temperature in Celsius and converts it to Fahrenheit.
     *
     * @param celsius   The temperature in Celsius
     * @return  The converted value in Fahrenheit.
     */
    public static float celsiusToFahrenheit( float celsius )
    {
        return (float)( celsius * 1.8 + 32 );
    }// end of method celsiusToFahrenheit

    /**
     * Accepts a numeric value of type float that represents
     * a temperature in Celsius and converts it to Kelvin.
     *
     * @param celsius  The temperature in Celsius
     * @return  The converted value in Kelvin.
     */
    public static double celsiusToKelvin( float celsius )
    {
        return celsius + 273.15;
    }// end of method celsiusToKelvin

    /**
     * Accepts a numeric value of type float that represents
     * a temperature in Fahrenheit and converts it to Celsius.
     *
     * @param fahrenheit The temperature in Fahrenheit.
     * @return  The converted value in Celsius.
     */
    public static float fahrenheitToCelsius( float fahrenheit )
    {
        float celsius = Math.round( ( fahrenheit - 32 ) / 1.8 );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( celsius ) );
    }// end of method fahrenheitToCelsius

    /**
     * Accepts a numeric value of type float that represents
     * a temperature in Fahrenheit and converts it to Kelvin.
     *
     * @param fahrenheit  The temperature in Fahrenheit.
     * @return  The converted value in Kelvin.
     */
    public static float fahrenheitToKelvin( float fahrenheit )
    {
        float kelvin = Math.round( ( fahrenheit + 459.67 ) * 0.5555555555555556 );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( kelvin ) );
    }// end of method fahrenheitToKelvin

    /**
     * Accepts a numeric value of type float that represents
     * a temperature in Kelvin and converts it to Celsius.
     *
     * @param kelvin  The temperature in Fahrenheit.
     * @return  The converted value in Celsius.
     */
    public static float kelvinToCelsius( float kelvin )
    {
        float celsius = Math.round( kelvin - 273.15 );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( celsius ) );
    }// end of method kelvinToCelsius

    /**
     * Accepts a numeric value of type float that represents
     * a temperature in Kelvin and converts it to Fahrenheit.
     *
     * @param kelvin  The temperature in Fahrenheit.
     * @return  The converted value in Fahrenheit.
     */
    public static float kelvinToFahrenheit( float kelvin )
    {
        float fahrenheit = Math.round( kelvin * 1.8 - 459.67 );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( fahrenheit ) );
    }// end of method kelvinToFahrenheit

    /**
     * Converts milliseconds to minutes.
     *
     * @param milliseconds  The number of milliseconds to be converted.
     * @return  The converted time value.
     */
    public static int millisecondsToMinutes( int milliseconds )
    {
        return milliseconds / 60000;
    }// end of method millisecondsToMinutes

    /**
     * Converts a minute time value to milliseconds.
     *
     * @param minutes  The number of minutes to be converted.
     * @return  The converted time value.
     */
    public static int minutesToMilliseconds( int minutes )
    {
        return minutes * 60000;
    }// end of method minutesToMillisesonds
    
    /**
     * Converts a double value into Km/h unit measurement
     * 
     * @param mps	The value to be converted in mps
     * @return	The value after the conversion.
     */
    public static double mpsToKmh( double mps )
    {
        return mps * 3.6;
    }// end of method mpsToKmh

    /**
     * Accepts a numeric value of type double that represents
     * a rate of speed in mph (Miles per hour) and converts it to km/h (Kilometers per hour).
     *
     * @param mph  The rate of speed in mph (Miles per hour).
     * @return  The converted rate of speed value in km/h (Kilometers per hour).
     */
    public static double mphToKmh( double mph )
    {
        return mph * 1.60934;
    }// end of method mphToKmh

    /**
     *  Accepts a numeric value of type double that represents
     *  a rate of speed in kmh (Kilometers per hour) and converts it to mph (Miles per hour).
     *
     * @param kmh The rate of speed in kmh (Kilometers per hour).
     * @return  The converted rate of speed value in mph (Miles per hour).
     */
    public static double kmhToMph( double kmh )
    {
        return kmh * 0.621371;
    }// end of method kmhToMph
    
    /**
     * Converts a value in kilometers per hour to meters per second
     *  
     * @param kmh The value in kilometers per hour
     * @return The converted value in meters per second
     */
    public static double kmhToMps( double kmh )
    {
        return kmh * 0.277778;
    }// end of method kmhToMps

    /**
     * Accepts a numeric value of type float that represents
     * a rate of speed in Mps (Meters per second) and converts it to Mph (Miles per hour).
     *
     * @param mps  The rate of speed in Mps (Meters per second).
     * @return  The converted rate of speed value in Mph (Miles per hour).
     */
    public static float mpsToMph( float mps )
    {
        float mph = Math.round( mps * 2.23694 );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( mph ) );
    }// end of method mpsToMph
    
    /**
     * Accepts a numeric value of type float that represents
     * a rate of speed in Mph (Miles per hour) and converts it to Mph (Meters per seconds).
     *
     * @param mph  The rate of speed in Mph (Miles per hour).
     * @return  The converted rate of speed value in Mph (Meters per seconds).
     */
    public static float mphToMps( float mph )
    {
        float mps = Math.round( mph * 0.44704 );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( mps ) );
    }// end of method mphToMps
    

    /** Miscellaneous Methods **/
    
    /**
     * Round a value to 2 decimal places
     *
     * @param value The value to be rounded
     * @return  The rounded value to 2 decimal places
     */
    public static float roundValue( double value )
    {
        float rounded = Math.round( value );

        return Float.parseFloat( new DecimalFormat( "##.00" ).format( rounded ) );
    }// end of method roundValue

    /**
     * Accepts a numeric value of type float that represents
     * an angle of degree representing a compass direction.
     *
     * @param degrees  The angle of the direction
     * @return  The converted value
     */
    public static String compassDirection( float degrees )
    {
        int index = (int)( ( degrees / 22.5 ) + 0.5 );

        return compassSectors[ index % 16 ];
    }// end of method compassDirection

    /**
     * Accepts a numeric value of type long that represents
     * a Unix time value.
     *
     * @param unixTimeValue  The numerical time value
     * @return  The {@code Date} object.
     */
    public static Date getDateTime( long unixTimeValue )
    {
        return new Date( unixTimeValue * 1000L ); // *1000 is to convert seconds to milliseconds
    }// end of method getDateTime

    /**
     * Returns a {@code Date} object from a {@code String} representation of a date
     * 
     * @param date	A {@code String} representation of a date
     * @return	A {@code Date} object
     */
    public static Date getDate(String date)
    {
        Date startDate = null;        

        try
        {
            startDate = new SimpleDateFormat( "MM/dd/yyyy" ).parse( date );            
        }// end of try block
        catch (ParseException e)
        {
        	JOptionPane.showMessageDialog( null, e.getMessage(),
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );

        }// end of catch block

        return startDate;
    }// end of method getDateTime

    /**
     * Accepts a 24hr time and converts it to a 12hr time.
     *
     * @param hour  The 24hr clock hour time value
     * @param minute  The minute time value
     * @return  Formatted 12hr time. Example  00:00 file return 12:00 AM.
     */
    public static String get12HourTime( int hour, int minute )
    {
    	// 24 hour times might return a negative if the time-zone
    	// offset is subtracted from 00 or 24hrs
        if( hour < 0 ) hour = 24 + hour;
    	
        return String.format( "%d:%s %s",
                ( hour > 12 ? hour - 12 : ( hour == 0 ? 12 : hour ) ),
                ( minute < 10 ? minute == 0 ? "00" :( "0" + minute )
                        : String.valueOf( minute ) ),
                (hour > 12 ? "PM" : "AM") );
    }// end of method get12HourTime
    
    /**
     * Converts a {@code String} representation of a time in 12hr format
     * to a time in 24hr format.
     * 
     * @param time	A {@code String} representation of a time in 12hr format
     * @return		A {@code String} representation of a time in 24hr format
     */
    public static String get24HourTime( String time )
    {
    	StringBuilder realTime = new StringBuilder( time );
    	
    	if( !realTime.toString().contains( " " ) )
    	{
    		int insertionPoint = time.indexOf( ":" ) + 2;
    		
    		realTime = new StringBuilder( time ).insert( time.length() - 2, " " );
    	}// end of if block
    	
    	int hour = Integer.parseInt( realTime.toString().split( ":" )[ 0 ].trim() );
    	int minute = Integer.parseInt( realTime.toString().split( ":" )[ 1 ].trim().split( " " )[ 0 ].trim() );
    	String meridian = realTime.toString().split( " " )[ 1 ].trim();
    	String t = null;
    	
        if( meridian.equalsIgnoreCase( "am" ) ) 
        {
        	t = String.format("%s:%d", hour < 10 ? "0" + hour : hour == 12 ? "00" : hour, minute );
        }// end of if block
        else if( meridian.equalsIgnoreCase( "pm" ) ) 
        {
        	t = String.format("%s:%d", hour < 12 ? 12 + hour : hour, minute );
        }// end of else if block
        
        return t;
    }// end of method get24HourTime

    /**
     * Ensures that the city entered by the user is correctly formatted.
     *
     * @param cityName A {@code String} representing the of a city.
     * @return  A boolean value of True/False dependent on the result of the test.
     */
    public static boolean isValidCityName( String cityName )
    {
    	return cityName.contains( "," );
    }// end of method isValidCityName
    
    /***
     * Returns the number of files found in a specific path
     * 
     * @param path The location to search for files
     * @return The number of files found which may include directories
     */
    public static int getFileCount( String path )
	{
		File[] files = new File( path ).listFiles();
				
		return files.length;
	}// end of method getFileCount
    
    /**
     * Compute the distance between two strings.
     */    
    public static int getLevenshteinDistance( String firstString, String secondString )
    {
        int n = firstString.length();
        int m = secondString.length();
        int[][] distance = new int[ n + 1 ][ m + 1 ];

        // Step 1
        if( n == 0 )
        {
            return m;
        }// end of if block

        if( m == 0 )
        {
            return n;
        }// end of if block

        // Step 2
        for( int i = 0; i <= n; distance[i][0] = i++ );

        for( int j = 0; j <= m; distance[0][j] = j++ );

        // Step 3
        for( int i = 1; i <= n; i++ )
        {
            //Step 4
            for (int j = 1; j <= m; j++ )
            {
                // Step 5
                int cost = ( secondString.charAt( j - 1 ) == firstString.charAt( i - 1 ) ) ? 0 : 1;

                // Step 6
                distance[ i ][ j ] = Math.min(
                    Math.min( distance[ i - 1 ][ j ] + 1, distance[ i ][ j - 1 ] + 1 ),
                distance[ i - 1 ][ j - 1] + cost );
            }// end of inner for loop
        }// end of outer for loop

        // Step 7
        return distance[ n ][ m ];
    }// end of method getLevenshteinDistance
    
    /***
     * Locate any subdirectories found in a specific directory
     * 
     * @param path A directory path which may contain subdirectories
     * @return An {@code ArrayList} containing the names of all subdirectories found in the given path
     */
    public static ArrayList<String> getSubdirectories( String path )
	{
		files = files == null ? new File( path ).listFiles() : files;
	    
		if( files == null ) return null;
		
		for ( File file : files ) 
	    {
	        if ( file.isDirectory() )
	        {
	        	subDirectoriesFound.add( file.getName() );
	        	files = file.listFiles();
	        	getSubdirectories( path ); // recursive call.
	        }// end of if block 
	    }// end of for loop
		
		return subDirectoriesFound;
	}// end of method getSubdirectories
    
    /***
     * Converts an image to a {@code BufferedImage}
     * 
     * @param image The image to be converted
     * @return A {@code BufferedImage} of the original image     
     * @author corgrath
     * @see <a href='https://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png#665428' target='_top'>Stack Overflow</a>
     */
    public static BufferedImage imageToBufferedImage( Image image ) 
    {
        BufferedImage bufferedImage =
        		new BufferedImage( image.getWidth( null ),
        				image.getHeight(null), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage( image, 0, 0, null );
        g2.dispose();

        return bufferedImage;
    }// end of method imageToBufferedImage
    
    /***
     * Add transparency to a {@code BufferedImage}
     * 
     * @param im The image to be made transparent
     * @param color The color to be made transparent
     * @return A {@code Image} with transparency added
     * @author corgrath
     * @see <a href='https://stackoverflow.com/questions/665406/how-to-make-a-color-transparent-in-a-bufferedimage-and-save-as-png#665428' target='_top'>Stack Overflow</a>
     */
     public static Image setImageOpacity( BufferedImage im, final Color color ) 
     {
        ImageFilter filter = new RGBImageFilter()
        {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB( int x, int y, int rgb )
            {
                if ( ( rgb | 0xFF000000 ) == markerRGB )
                {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }// end of if block
                else
                {
                    // nothing to do
                    return rgb;
                }// end of else block
            }// end of method filterRGB
        };

        ImageProducer ip = new FilteredImageSource( im.getSource(), filter );
        return Toolkit.getDefaultToolkit().createImage( ip );
    }// end of method setImageOpacity
    
    /***
     * Scales an image to specified dimensions
     * 
     * @param img	The image to be resized
     * @param width The width of the new image
     * @param height The height of the new image
     * @return A copy of the image scaled to the specified dimensions
     */
    public static Icon scaleImageIcon( Icon img, int width, int height )
	{
		Image newImg = null;
		
		try 
		{
			Image image = ( ( ImageIcon ) img ).getImage(); // transform it 
			newImg = image.getScaledInstance( width, height,
					java.awt.Image.SCALE_SMOOTH ); // scale it the smooth way  
			
		}// end of try block
		catch( NullPointerException e )
		{
			logMessage( LogLevel.SEVERE, "Could not scale icon " + img + " " + e.getMessage(),
 		        TAG + "::scaleImageIcon [line: " +
        	    getExceptionLineNumber( e ) + "]" );
		}// end of catch block
		
		return new ImageIcon( newImg );  // transform it back
	}// end of method scaleImageIcon
    
    /**
     * Converts a sequence to alphabetic characters to sentence case.
     *
     * @param text The {@code String} value to be converted.
     * @return		A {@code String} representation of text in a sentence case format.
     */
    public static String toProperCase( String text )
    {
        String[] sep = { " ", "-", "/", "'" };
        int cycle = text.length();
        StringBuilder sequence = new StringBuilder( text.toLowerCase() );

        for ( int i = 0; i <= cycle; i++ )
        {
            if ( i == 0 && Character.isAlphabetic( sequence.charAt( i ) ) )
            {
                sequence.replace( sequence.indexOf( Character.toString( sequence.charAt( i ) ) ),
                        sequence.indexOf( Character.toString( sequence.charAt( i ) ) ) + 1,
                        Character.toString( Character.toUpperCase( sequence.charAt( i ) ) ) );
            }// end of if block
            else if ( ( i < cycle ) && Character.isAlphabetic( sequence.charAt( i ) ) &&
                    ( sequence.charAt( i - 1 ) == 'c' && sequence.charAt( i - 2 ) == 'M' )  )
            {
                sequence.replace( sequence.indexOf( Character.toString( sequence.charAt( i ) ) ),
                        sequence.indexOf( Character.toString( sequence.charAt( i ) ) ) + 1,
                        Character.toString( sequence.charAt( i ) ).toUpperCase() );
            }// end of else if block
            else if ( ( i < cycle ) && Character.isAlphabetic( sequence.charAt( i ) ) &&
                    ( Character.toString( sequence.charAt( i - 1 ) ).equals( sep[ 0 ] ) ||
                            Character.toString( sequence.charAt( i - 1 ) ).equals( sep [ 1 ] ) ) )
            {
                sequence.replace( i, i + 1, Character.toString( sequence.charAt( i ) ).toUpperCase() );
            }// end of else if block
        }// end of for loop

        return sequence.toString();

    }// end of method properCase
    
    /**
     * Checks if a string contains a whole word on its own.
     * 
     * @param input	A search {@code String}
     * @param word	A {@code String} being searched for
     * @return	True if the whole word is found, otherwise False.
     */
    public static boolean containsWholeWord( String input, String word )
    {  	        
        return Pattern.compile( String.format( "\b%s\b", word ) ).matcher( input ).find();
    }// end of method containsWholeWord

    /**
     * Returns an {@code ImageIcon} from a specified file path.
     * 
     * @param path	A {@code String} representation of a file system path.
     * @return	An {@code ImageIcon}.
     */
  	public static ImageIcon createImage( String path )
  	{
  		return new ImageIcon( Toolkit.getDefaultToolkit().getImage( path ) );
  	}// end of method ImageIcon createImage
  	
    /**
     * Format a Uri so that is is compatible with a valid standard {@code Uri} {@code String}.
     * 
     * @param uri The {@code Uri} {@code String} to be formatted
     * @return  A valid formatted {@code Uri} {@code String}
     */
    public static String escapeUriString(String uri)
    {
        String encodedString = null;

        try
        {
            encodedString = URLEncoder.encode( uri, "UTF-8" );
        }// end of try block
        catch ( UnsupportedEncodingException e )
        {
        	logMessage( LogLevel.SEVERE, e.getMessage(),
		        TAG + "::escaprUriString [line: " + getExceptionLineNumber( e ) + "]" );
        }// end of catch block

        return encodedString;
    }// end of method escapeUriString

    /**
     * Determine if the device is connected to the Internet.
     * 
     * @param context The calling context.
     * @return True/False depending on the connection state.
     */
    public static boolean hasInternetConnection()
    {
        return NetworkHelper.hasNetworkAccess();
    }// end of method hasInternetConnection

    /**
     * Determines if a city was previously stored to the local storage.
     * 
     * @param cityName	The name of the city.
     * @return	True/False dependent on the outcome of the check.
     */
    public static boolean isKnownCity( String cityName )
    {
    	if( cityName.contains( "," ) ) 
    	{
    		return isFoundInDatabase(cityName);		
//    		isFoundInJSONStorage(cityName);        
//          isFoundInXMLStorage(cityName);	
    	}// end of if block   		
		
		return false;
    }// end of method isKnownCity

    /**
     * Determines if a city has been previously stored a a local XML file.
     * 
     * @param cityName	The name of the city
     * @return	True/False dependent on the outcome of the check.
     */
	public static boolean isFoundInXMLStorage( String cityName )
	{
		boolean found = false;
		
		if( new File( XMLHelper.PREVIOUSLY_FOUND_CITIES_XML ).exists() ) 
		{
			//XML file search
	        SAXBuilder builder = new SAXBuilder();
	    	
	    	try 
	    	{
	    		// just in case the document contains unnecessary white spaces
	    		builder.setIgnoringElementContentWhitespace( true );
	    		
	    		// download the document from the URL and build it
	    		Document document = builder.build( XMLHelper.PREVIOUSLY_FOUND_CITIES_XML );
	    		
	    		// get the root node of the XML document
	    		Element rootNode = document.getRootElement();
	    		
	    		List< Element > list = rootNode.getChildren( "City" );
	    		
	    		for ( int i = 0; i < list.size(); i++ )
	    		{
	    			Element node = list.get( i );
					String cCityName = node.getChildText( "CityName" );
					String cRegionName = node.getChildText( "RegionName" );
					String cRegionCode = node.getChildText( "RegionCode" );
					String cCountryName = node.getChildText( "CountryName" );
					boolean containsNumber = isNumeric( cRegionCode );
					
					if( cityName.equalsIgnoreCase( cCityName + ", " + cCountryName ) ||
				        !containsNumber && cityName.equalsIgnoreCase( cCityName + ", " + cRegionCode ) )
					{
					    found = true;
					    logMessage( LogLevel.INFO,  cityName + " was found in the XML storage.",
					            TAG + "::isFoundInXMLStorage" );
					}// end of if block
	    		}// end of for loop    		 		
	    		
	    	}// end of try block 
	    	catch ( IOException io )
	    	{
	    		 logMessage( LogLevel.SEVERE, io.getMessage(),
			        TAG + "::isFoundInXMLStorage [line: " + getExceptionLineNumber( io ) + "]" );
	    	}// end of catch block 
	    	catch ( JDOMException jdomex )
	    	{
	    		logMessage( LogLevel.SEVERE, jdomex.getMessage(),
			        TAG + "::isFoundInXMLStorage [line: " + 
		        		getExceptionLineNumber( jdomex ) + "]" );
	    	}// end of catch block
		}// end of if block	
    	
    	return found;
	}// end of method isFoundInXMLStorage

	/**
     * Determines if a city has been previously stored a a local JSON file.
     * 
     * @param cityName	The name of the city
     * @return	True/False dependent on the outcome of the check.
     */
	public static boolean isFoundInJSONStorage( String cityName )
	{
		String[] city = cityName.split( "," );
		boolean found = false;
		
		//JSON File Search		
		if( new File( JSONHelper.PREVIOUSLY_FOUND_CITIES_JSON ).exists() )
        {
			Gson gson = new Gson();
			//JSONHelper.cityDataList = JSONHelper.importFromJSON();
			// convert the list to a JSON string
	        //String jsonString = gson.toJson(JSONHelper.cityDataList);
	        
			String jsonString = null;
			
	        try 
	        {
				jsonString = 
						new String( Files.readAllBytes( Paths.get( JSONHelper.PREVIOUSLY_FOUND_CITIES_JSON ) ) );
			}// end of try block
	        catch ( IOException e )
	        {
	        	logMessage( LogLevel.SEVERE, e.getMessage(),
    		        TAG + "::isFoundInJSONStorage [line: " + getExceptionLineNumber( e ) + "]" );
			}// end of catch block 
	        
	        if (jsonString != null)
            {
	        	// convert the file JSON into a list of objects
		        List< CityData > cityDataList = gson.fromJson( jsonString, new TypeToken< List<CityData > >() {}.getType() );
		        
		        for ( CityData c : cityDataList )
                {
                    String cCityName = c.getCityName();
                    String cRegionName = c.getRegionName();
                    String cRegionCode = c.getRegionCode();
                    String cCountryName = c.getCountryName();
                    boolean containsNumber = isNumeric( cRegionCode );

                    if( cityName.equalsIgnoreCase( cCityName + ", " + cCountryName ) ||
                        !containsNumber && cityName.equalsIgnoreCase( cCityName + ", " + cRegionCode ) )
                    {
                        found = true;
                        logMessage( LogLevel.INFO,  cityName + " was found in the JSON storage.",
                                TAG + "::isFoundInJSONStorage" );
                    }// end of if block
                }// end of for each loop
            }// end of if block
        }// end of if block       
        
        return found;
	}// end of method isFoundInJSONStorage

	/**
     * Determines if a city has been previously stored a a local SQLite 3 database.
     * 
     * @param cityName	The name of the city
     * @return	True/False dependent on the outcome of the check.
     */
	public static boolean isFoundInDatabase( String cityName )
	{
		// Check SQLite Database
    	String SQL;
    	String[] city = cityName.split( "," ); 

    	if( city[ 1 ].trim().length() == 2 ) 
    	{
    		SQL = "SELECT CityName, CountryName, CountryCode, RegionCode, "
    				+ "Latitude, Longitude FROM WorldCities.world_cities WHERE CityName = ? AND RegionCode = ? " +
    				" AND typeof(RegionCode) = 'integer'";    		
    	}// end of if block
    	else
    	{
    		SQL = "SELECT CityName, CountryName, CountryCode, RegionCode, "
    				+ "Latitude, Longitude FROM WorldCities.world_cities WHERE CityName = ? AND CountryName = ?";    			
    	}// end of else block

    	ResultSet rs = null;
    			
		try(
				PreparedStatement stmt = WeatherLionMain.conn.prepareStatement( 
							SQL,
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY );
		)
		{
			stmt.setString( 1, city[ 0 ].trim() );
			stmt.setString( 2, city[ 1 ].trim() );				
			
			rs = stmt.executeQuery();			
			int found = 0;
			
			while ( rs.next() )
			{
				found++;
			}// end of while loop
			
			if( found > 0 )
			{
				logMessage( LogLevel.INFO,  cityName + cityName + " was found in the Database storage.",
					TAG  + "::isFoundInJSONStorage" );
				return true;
			}// end of if block			
		}// end of try block
		catch( SQLException e ) 
		{
			 logMessage( LogLevel.SEVERE, e.getMessage(),
		        TAG + "::isFoundInDatabase [line: " + getExceptionLineNumber( e ) + "]" );
		}// end of catch block
		
		return false;
	}// end of method isFoundInDatabase

	/***
	 * Test if a {@code String} value is a number
	 * 
	 * @param value A {@code String} value to be tested
	 * @return A {@code boolean} true/false depending on the result of the test
	 */
	public static boolean isNumeric( String value ) 
	{
		return value != null && value.matches( "[-+]?\\d*\\.?\\d+" );
	}// end of method isNumeric
	
	/***
	 * Method that determines if host OS is windows
	 * 
	 * @return A {@code boolean} true/false depending on the result of the check
	 */
	public static boolean isWindows()
	{
		return ( OS.indexOf( "win" ) >= 0 );
 	}// end of method isWindows
	
	/***
	 * Method that determines if host OS is Linux
	 * 
	 * @return A {@code boolean} true/false depending on the result of the check
	 */
	public static boolean isLinux()
	{
 		return ( OS.indexOf( "linux" ) >= 0 );
 	}// end of method isLinux
 
	/***
	 * Method that determines if host OS is MacIntosh
	 * 
	 * @return A {@code boolean} true/false depending on the result of the check
	 */
	public static boolean isMac()
	{
		return ( OS.indexOf( "mac" ) >= 0);
 	}// end of method isMac
	
	public static String findClosestWordMatch( String[] phraseList, String searchPhrase )
    {
        StringBuilder closestMatch = new StringBuilder();
        int closest = searchPhrase.length();

        for( String phrase : phraseList )
        {
            int cost = UtilityMethod.getLevenshteinDistance( searchPhrase , phrase );

            if( cost < closest )
            {
                closest = cost;
                closestMatch.setLength( 0 );
                closestMatch.append( phrase );
            }// end of if block
        }// end of for loop

        return closestMatch.toString();
    }// end of method findClosestWordMatch
		
	/**
     * Uses the Geo Names web service to determine if a city actually exists.
     *
     * @param cityName The name of the city to be checked
     * @param cityData  The object that the JSON data returned from the web service will be mapped to.
     * @param context   The calling context.
     */
    public static void findGeoNamesCity( String cityName )
    {        
        int maxRows = 100;
        
    	// All spaces must be replaced with the + symbols for the HERE Maps web service
        if( cityName.contains( " " ) )
        {
        	cityName = cityName.replace( " ", "+" );
        }// end of if block
        
        // All commas must be replaced with the + symbols for the HERE Maps web service
        if( cityName.contains( "," ) )
        {
        	cityName = cityName.replace( ",", "+" );
        }// end of if block
        
        String cityUrl =
        		"http://api.geonames.org/searchJSON?q=" + 
        			escapeUriString( cityName.toLowerCase() ) +
        				"&maxRows=" + maxRows +
        				"&username=" + WidgetUpdateService.geoNameAccount;

        if ( hasInternetConnection() )
        {
            //run an background service
        	CityDataService cd = new CityDataService( cityUrl, "geo" );
        	cd.execute();
        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );
        }// end of else block
    }// end of method findGeoNamesCity
    
	/**
     * Uses the Here Maps web service to determine if a city actually exists.
     *
     * @param cityName The name of the city to be checked
     * @param cityData  The object that the JSON data returned from the web service will be mapped to.
     * @param context   The calling context.
     */
    public static void findHereCity( String cityName )
    {        
        // All spaces must be replaced with the + symbols for the HERE Maps web service
        if( cityName.contains( " " ) )
        {
        	cityName = cityName.replace( " ", "+" );
        }// end of if block
        
        // All commas must be replaced with the + symbols for the HERE Maps web service
        if( cityName.contains( "," ) )
        {
        	cityName = cityName.replace( ",", "+" );
        }// end of if block
    	
        String cityUrl =
                "https://geocoder.api.here.com/6.2/geocode.json?"
                + "app_id=" + WidgetUpdateService.hereAppId
                + "&app_code=" + WidgetUpdateService.hereAppCode
                + "&searchtext=" + escapeUriString( cityName.toLowerCase() );

        if ( hasInternetConnection() )
        {
            //run an background service
        	CityDataService cd = new CityDataService( cityUrl, "here" );
        	cd.execute();
        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );
        }// end of else block
    }// end of method findHereCity
    
    /**
     * Uses the Yahoo! web service to determine if a city actually exists.
     *
     * @param cityName The name of the city to be checked
     * @param cityData  The object that the JSON data returned from the web service will be mapped to.
     * @param context   The calling context.
     */
    @Deprecated
    public static void findYahooCity( String cityName )
    {        

        String cityUrl =
                "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text%3D%22" +
                        escapeUriString(cityName.toLowerCase()) +
                        "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        if ( hasInternetConnection() )
        {
           //run an background service
        	CityDataService cd = new CityDataService( cityUrl, "yahoo" );
        	cd.execute();
        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );
        }// end of else block
    }// end of method findYahooCity
    
    /**
     * Uses the GeoNames web service to return the geographical location of a city using it's name.
     *
     * @param wxLocation The location of the city to be found.
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */    
    public static String retrieveGeoNamesGeoLocationUsingAddress( String wxLocation )
    {
    	 int maxRows = 10;
         String strJSON = null;
         String ps;
         StringBuilder fileData = new StringBuilder();
         
         if ( wxLocation != null )
         {
        	 wxLocation = wxLocation.contains( "," ) ?
                     wxLocation.substring( 0, wxLocation.indexOf( "," ) ).toLowerCase() :
                     wxLocation;
             ps = String.format( "%s%s%s", "gn_sd_", wxLocation.replaceAll( " ", "_" ), ".json" );
             WeatherLionMain.previousCitySearchFile = new File(
        		 WeatherLionMain.previousSearchesPath.getPath() + "/" +  ps );
             
             if( WeatherLionMain.previousCitySearchFile.exists() )
             {
                 try(
                         FileReader fr = new FileReader( WeatherLionMain.previousCitySearchFile );	// declare and initialize the file reader object
                         BufferedReader br = new BufferedReader( fr ) 	// declare and initialize the buffered reader object
                 )
                 {
                     String line;

                     while( ( line = br.readLine() ) != null )
                     {
                         fileData.append( line );
                     }// end of while loop

                     strJSON = fileData.toString();
                 }// end of try block
                 catch ( IOException e )
                 {
                     logMessage( LogLevel.SEVERE, e.getMessage(),
                             TAG + "::retrieveGeoNamesGeoLocationUsingAddress [line: " +
                                     getExceptionLineNumber( e )  + "]" );
                 }// end of catch block
             }// end of if block
             else
             {
                 String geoUrl =
                         "http://api.geonames.org/searchJSON?" +
                                 "name_equals=" + wxLocation.toLowerCase() +
                                 "&maxRows=" + maxRows +
                                 "&username=" + WidgetUpdateService.geoNameAccount;

                 if ( hasInternetConnection() )
                 {
                     try
                     {
                         strJSON = HttpHelper.downloadUrl( geoUrl );
                     }// end of try block
                     catch ( IOException e )
                     {
                         logMessage( LogLevel.SEVERE, e.getMessage(),
                                 TAG + "::retrieveGeoNamesGeoLocationUsingAddress [line: " +
                                         getExceptionLineNumber(e) + "]" );
                     }// end of catch block

                 }// end of if block
                 else
                 {
                	 JOptionPane.showMessageDialog( null, "No Internet Connection.",
             				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );
                 }// end of else block
             }// end of else block
             
         }// end of if block    	

        // Return the data from specified url
        return strJSON;

    }// end of method retrieveGeoNamesGeoLocationUsingAddress

    /**
     * Uses the Google web service to return the geographical location of a city using it's name.
     *
     * @param wxLocation The location of the city to be found.
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */
    public static String retrieveGoogleGeoLocationUsingAddress( String wxLocation )
    {
        String strJSON = null;
        String geoUrl =
                "http://maps.googleapis.com/maps/api/geocode/json?address="+
                        escapeUriString( wxLocation.toLowerCase() ) + "&sensor=false";

        if ( hasInternetConnection() )
        {
            try
            {
                strJSON = HttpHelper.downloadUrl( geoUrl );
            }// end of try block
            catch (IOException e)
            {
            	logMessage( LogLevel.SEVERE, e.getMessage(),
    		        TAG + "::retrieveGoogleGeoLocationUsingAddress [line: " +
    		        getExceptionLineNumber( e ) + "]" ); 
            }// end of catch block

        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );

        }// end of else block

        // Return the data from specified url
        return strJSON;

    }// end of method retrieveGoogleGeoLocationUsingAddress
    
    /**
     * Uses the Here Maps web service to return the geographical location of a city using it's name.
     *
     * @param wxLocation The location of the city to be found.
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */
    public static String retrieveHereGeoLocationUsingAddress( String wxLocation )
    {
    	// All spaces must be replaced with the + symbols for the HERE Maps web service
        if( wxLocation.contains( " " ) )
        {
        	wxLocation = wxLocation.replace( " ", "+" );
        }// end of if block
        
        // All commas must be replaced with the + symbols for the HERE Maps web service
        if( wxLocation.contains( "," ) )
        {
        	wxLocation = wxLocation.replace( ",", "+" );
        }// end of if block
    	
    	String strJSON = null;
        String geoUrl =
                "https://geocoder.api.here.com/6.2/geocode.json?"
                + "app_id=" + WidgetUpdateService.hereAppId
                + "&app_code=" + WidgetUpdateService.hereAppCode
                + "&searchtext=" + escapeUriString( wxLocation.toLowerCase() );

        if ( hasInternetConnection() )
        {
            try
            {
                strJSON = HttpHelper.downloadUrl( geoUrl );
            }// end of try block
            catch ( IOException e )
            {
            	logMessage( LogLevel.SEVERE, e.getMessage(),
        		        TAG + "::retrieveHereGeoLocationUsingAddress [line: " +
        		        getExceptionLineNumber( e ) + "]" );
            }// end of catch block

        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );

        }// end of else block

        // Return the data from specified url
        return strJSON;

    }// end of method retrieveHereGeoLocationUsingAddress

    /**
     * Uses the Yahoo web service to return the geographical location of a city using it's name.
     *
     * @param wxLocation The location of the city to be found.
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */
    @Deprecated
    public static String retrieveYahooGeoLocationUsingAddress( String wxLocation )
    {
        String strJSON = null;
        String geoUrl =
                "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.places%20where%20text%3D%22"+
                        escapeUriString( wxLocation.toLowerCase() ) +
                        "%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        if ( UtilityMethod.hasInternetConnection() )
        {
            try
            {
                strJSON = HttpHelper.downloadUrl( geoUrl );
            }// end of try block
            catch ( IOException e )
            {
            	 logMessage( LogLevel.SEVERE, e.getMessage(),
     		        TAG + "::retrieveYahooGeoLocationUsingAddress [line: " +
            	    getExceptionLineNumber( e ) + "]" );
            }// end of catch block

        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );

        }// end of else block

        // Return the data from specified url
        return strJSON;

    }// end of method retrieveGoogleGeoLocationUsingAddress
    
    /**
     * Uses the Geonames web service to return the geographical location of a city using it's coordinates.
     *
     * @param lat   The line of latitude that the city is located
     * @param lng   The line of longitude that the city is located
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */
    public static String retrieveGeoNamesGeoLocationUsingCoordinates( float lat, float lng )
    {
        String strJSON = null;
        String geoUrl =
                "http://api.geonames.org/findNearbyPlaceNameJSON?" +
                "lat=" + lat +
                "&lng=" + lng +
                "&username=" + WidgetUpdateService.geoNameAccount;

        if ( UtilityMethod.hasInternetConnection() )
        {
            try
            {
                strJSON = HttpHelper.downloadUrl( geoUrl );
            }// end of try block
            catch (IOException e)
            {
            	logMessage( LogLevel.SEVERE, e.getMessage(),
    		        TAG + "::RetrieveGeoNamesGeoLocationUsingCoordinates [line: " +
    		        getExceptionLineNumber( e ) + "]" );            	
            }// end of catch block

        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );

        }// end of else block

        // Return the data from specified URL
        return strJSON;

    }// end of method RetrieveGeoNamesGeoLocationUsingCoordinates
    
    /**
     * Uses the Google web service to return the geographical location of a city using its coordinates.
     *
     * @param lat   The line of latitude that the city is located
     * @param lng   The line of longitude that the city is located
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */
    public static String retrieveGoogleGeoLocationUsingCoordinates( double lat, double lng )
    {
        String strJSON = null;
        String geoUrl =
                "http://maps.googleapis.com/maps/api/geocode/json?"
                + "latlng="+ lat + "," + lng 
                + "&sensor=false";

        if ( UtilityMethod.hasInternetConnection() )
        {
            try
            {
                strJSON = HttpHelper.downloadUrl( geoUrl );
            }// end of try block
            catch (IOException e)
            {
            	logMessage( LogLevel.SEVERE, e.getMessage(),
    		        TAG + "::retrieveGoogleGeoLocationUsingCoordinates [line: " +
    		        getExceptionLineNumber( e ) + "]" );            	
            }// end of catch block

        }// end of if block
        else
        {
        	JOptionPane.showMessageDialog( null, "No Internet Connection.",
    				WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );

        }// end of else block

        // Return the data from specified URL
        return strJSON;

    }// end of method retrieveGoogleGeoLocationUsingCoordinates

    /**
     * Retrieves weather information from a specific weather provider's web service URL.
     *
     * @param wxUrl The providers web service {@code URL}
     * @return  A {@code String} representation of a JSON {@code Object} returned from the web service.
     */
    public static String retrieveWeatherData( String wxUrl )
    {
        String strJSON = null;
        
        try
        {
            strJSON = HttpHelper.downloadUrl( wxUrl );
        }// end of try block
        catch ( IOException e )
        {
        	logMessage( LogLevel.SEVERE, e.getMessage(),
		        TAG + "::retrieveWeatherData [line: " +
		        getExceptionLineNumber( e ) + "]" ); 
        }// end of catch block
        
        // Return the data from specified URL
        return strJSON;

    }// end of method retrieveWeatherData
    
    /**
     * Subtracts one time value from another.
     * 
     * @param firstPeriod	The first time value
     * @param secondPeriod	The second time value
     * @return	The result of the subtraction
     */
    public static long subtractTime( Date firstPeriod, Date secondPeriod ) 
    {
    	//milliseconds
        long difference = firstPeriod.getTime() - secondPeriod.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long elapsedMinutes = difference / minutesInMilli;
    	
    	return elapsedMinutes;
    }// end of method subtractTime
    
    /**
     * Returns an RGB Color which corresponds with a temperature
     * 
     * @param t The temperature to be tested
     * @return A new {@code Color}
     */
    public static Color temperatureColor( int t )
    {
    	Color c;
    	t = WeatherLionMain.storedPreferences.getUseMetric()
                ? (int) celsiusToFahrenheit( t )
                : t;
    	
    	if( t <= 40 )
    	{
    		c = new Color( 45, 99, 252 ); // Cold
    	}// end of if block
    	else if( Math.abs( t - 60 ) + Math.abs( 41 - t ) == Math.abs( 41 - 60 ) )
    	{
    		c = new Color( 151, 205, 251 ); // Chilly
    	}// end of else if block	
    	else if( Math.abs( t - 70 ) + Math.abs( 84 - t ) == Math.abs( 84 - 70 ) )
    	{
    		c = new Color( 152, 211, 0 ); // Warm
    	}// end of else if block		
    	else if( t > 85 )
    	{
    		c = new Color( 253, 0, 3 ); // Hot
    	}// end of else if block
    	else
    	{
    		c = new Color( 0, 172, 74 ); // Normal
    	}// end of else block
    	       
    	return c;
    }// end of method temperatureColor
    
    /**
     * Get the duration of time that has elapsed since a certain date.
     *
     * @param pastDate The date in the past to be compared to.
     * @return  A {@code String} representing the time frame that has passed.
     */
    public static String getTimeSince( Date pastDate )
    {
        //milliseconds
        long difference = new Date().getTime() - pastDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;

        long elapsedMinutes = difference / minutesInMilli;
        String timeElapsed;

        if( elapsedMinutes >= 60 )
        {
            // an hour or more
            int hours = (int) elapsedMinutes / 60;
            timeElapsed = hours > 1 ? hours + " hours ago" : hours + " hour ago";
        }// end of if block
        else if( elapsedMinutes >= 1440 )
        {
            // a day or more
            int days = (int) elapsedMinutes / 1440;
            timeElapsed = days > 1 ? days + " days ago" : days + " day ago";
        }// end of if block
        else if( elapsedMinutes >= 10080 )
        {
            // a week or more
            int weeks = (int) elapsedMinutes / 10080;
            timeElapsed = weeks > 1 ? weeks + " weeks ago" : weeks + " week ago";
        }// end of if block
        else if( elapsedMinutes >= 43830 )
        {
            // a month or more
            int months = (int) elapsedMinutes / 1440;
            timeElapsed = months > 1 ? months + " months ago" : months + " month ago";
        }// end of if block
        else if( elapsedMinutes >= 525960 )
        {
            // a year or more
            int years = (int) elapsedMinutes / 525960;
            timeElapsed = years > 1 ? years + " years ago" : years + " year ago";
        }// end of if block
        else
        {
            int seconds = (int) elapsedMinutes / 60;

            if( elapsedMinutes < 1 )
            {
                // time in seconds
                timeElapsed = seconds > 1 ? seconds + " seconds ago" : seconds + " second ago";
            }// end of if block
            else
            {
                // time in minutes
                timeElapsed = elapsedMinutes > 1 ? elapsedMinutes + " minutes ago" : elapsedMinutes + " minute ago";
            }// end of else block
        }// end of else block

        return timeElapsed;
    }// end of method getTimeSince
    
    /***
     * Determines whether or not a connectivity check needs to be performed
     * 
     * @return	A {@code boolean} value of true/false dependent on the outcome of the test.
     */
    public static boolean timeForConnectivityCheck() 
	{
    	int interval = WeatherLionMain.storedPreferences.getInterval();
    	long minutesToGo = millisecondsToMinutes( interval );
    	boolean ready = false;
    	
    	if( lastUpdated != null && !updateRequired() )
    	{
    		Calendar cal = Calendar.getInstance();
    		cal.setTime( lastUpdated );
    		cal.add( Calendar.MINUTE, millisecondsToMinutes( interval ) );
    		Date nextUpdateDue = cal.getTime();
    		
    		 //milliseconds
            long difference = nextUpdateDue.getTime() - new Date().getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            minutesToGo = difference / minutesInMilli;
            
            ready = minutesToGo <= 1;
         }// end of if block
    	else if( updateRequired() || lastUpdated != null )
    	{
    		ready = true;
    	}// end of else if block
    	
    	return ready;
    }// end of method timeForConnectivityCheck
    
    /**
     * Determine if the widget needs to be refreshed based on the specified refresh period.
     *
     * @return  True/False depending on the result of the check.
     */
    public static boolean updateRequired()
    {
        if( lastUpdated == null )
        {
            return true;
        }// end of if block

        if( refreshRequested )
        {
            return true;
        }// end of if block

        int interval = WeatherLionMain.storedPreferences.getInterval();

        //milliseconds
        long difference = Math.abs( new Date().getTime() - lastUpdated.getTime() );

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        if( elapsedMinutes >= interval )
        {
            return true;
        }// end of if block
        else
        {
            return false;
        }// end of else block
    }// end of method updatedRequired  
    
    /**
     * Returns a valid weather condition that is relevant to the application
     *
     * @param condition The weather condition to be validated
     * @return  A {@code String} value representing a valid weather condition
     */
    public static String validateCondition( String condition )
    {
        condition = condition.toLowerCase();

        if ( condition.contains( "until" ) )
        {
            condition = condition.substring( 0, condition.indexOf( "until" ) - 1 ).trim();
        }// end of if block

        if ( condition.contains( "starting" ) )
        {
            condition = condition.substring( 0, condition.indexOf( "starting" ) - 1 ).trim();
        }// end of if block

        if ( condition.contains( "overnight" ) )
        {
            condition = condition.substring( 0, condition.indexOf( "overnight" ) - 1 ).trim();
        }// end of if block

        if ( condition.contains( "night" ) )
        {
            condition = condition.replaceAll( "night", "" ).trim();
        }// end of if block

        if ( condition.contains( "possible" ) )
        {
            condition = condition.replaceAll( "possible", "" ).trim();
        }// end of if block

        if ( condition.contains( "throughout" ) )
        {
            condition = condition.substring( 0, condition.indexOf( "throughout" ) - 1 ).trim();
        }// end of if block

        if ( condition.contains( " in " ) )
        {
            condition = condition.substring( 0, condition.indexOf( " in " ) - 1 ).trim();
        }// end of if block

        if( condition.toLowerCase().contains( "and" ) )
        {
            String[] conditions = condition.toLowerCase().split( "and" );

            condition = conditions[ 0 ].trim();
        }// end of if block

        if( condition.toLowerCase().contains( "(day)") )
        {
            condition = condition.replace( "(day)", "").trim();
        }// end of if block
        else if( condition.toLowerCase().contains( "(night)" ) )
        {
            condition = condition.replace( "(night)", "" ).trim();
        }// end of if block

        // create a new method for locating the nearest match
        condition = UtilityMethod.findClosestWordMatch(
                        UtilityMethod.weatherImages.keySet().toArray( new String[0]),
                            condition );

        return toProperCase( condition );
    }// end of method validateCondition
    
    /**
     * Returns the line number were the exception occurred in the code.
     * 
     * @param e		The exception that was thrown by the compiler.
     * @return		The line number at which the exception was thrown.
     */
    public static int getExceptionLineNumber( Exception e )
    {
    	return e.getStackTrace()[ 1 ].getLineNumber();
    }// end of method getExceptionLineNumber
    
    /**
     * Uses the computers Internet connection to determine the current city location of the connection.
     * 
     * @return An {@code Object} of the {@code CityData} custom class
     */
    public static CityData getSystemLocation() 
    {
    	// region Client Machine Information
    	
    	SAXBuilder builder = new SAXBuilder();
    	CityData cd = null;
    	
    	try 
    	{
    		// just in case the document contains unnecessary white spaces
    		builder.setIgnoringElementContentWhitespace( true );
    		
    		String ipStackUrl = "http://api.ipstack.com/" + getSystemIpAddress() 
    							+ "?access_key=1bb227db8f2dca1b9a3917fb403e2e99&output=xml&legacy=1"; // TO BE DEPRECATED
//    		String ipStackUrl = "http://ip-api.com/xml";
    		
    		// download the document from the URL and build it
    		Document document = builder.build( ipStackUrl );
    		    		    		
    		// get the root node of the XML document
    		Element rootNode = document.getRootElement();
    		
    		// get the text from the root's children nodes
    		// ipstack.com implementation
			String ip = rootNode.getChildText( "ip" );
    		String countryCode = rootNode.getChildText( "country_code" );
    		String countryName = rootNode.getChildText( "country_name" );
    		String regionName = rootNode.getChildText( "region_name" );
    		String regionCode = rootNode.getChildText( "region_code" );
    		String city = rootNode.getChildText( "city" );
    		String zipCode = rootNode.getChildText( "zip_code" );
    		String timeZone = rootNode.getChildText( "time_zone" );
    		String latitude = rootNode.getChildText( "latitude" );
    		String longitude = rootNode.getChildText( "longitude" );
    		String metroCode = rootNode.getChildText( "metro_code" );
    		
    		// ip-api.com implementation
//    		String ip = rootNode.getChildText( "query" );
//    		String countryCode = rootNode.getChildText( "countryCode" );
//    		String countryName = rootNode.getChildText( "country" );
//    		String regionName = rootNode.getChildText( "regionName" );
//    		String regionCode = rootNode.getChildText( "region" );
//    		String city = rootNode.getChildText( "city" );
//    		String zipCode = rootNode.getChildText( "zip" );
//    		String timeZone = rootNode.getChildText( "timezone" );
//    		String latitude = rootNode.getChildText( "lat" );
//    		String longitude = rootNode.getChildText( "lon" );
//    		String serviceProvider = rootNode.getChildText( "org" );
    		
    		// create a new CityData object
    		cd = new CityData( city, countryName, countryCode, regionName,
    				regionCode, Float.parseFloat( latitude ), Float.parseFloat( longitude ) ); 		
    		
    	}// end of try block 
    	catch ( IOException io )
    	{
    		logMessage( LogLevel.SEVERE, io.getMessage(),
		        TAG + "::cleanLockFiles [line: " + getExceptionLineNumber( io ) + "]" );
    		
    		// Use backup data from https://ipapi.co instead
    		String ip = getSystemIpAddress();
    		String url = "https://ipapi.co/" + ip + "/json";
    		String strJSON = null;
    		
    		try 
     		{
                strJSON = HttpHelper.downloadUrl( url );
    			Object json = new JSONTokener( strJSON ).nextValue();
         		
     			// Check if a JSON was returned from the web service
     			if ( json instanceof JSONObject )
     			{
     				// Get the full HTTP Data as JSONObject
    				JSONObject reader = new JSONObject( strJSON );
    				
     				// ipapi.co implementation
     	    		String city = reader.getString( "city" );
     	    		String regionName = reader.getString( "region" );
     	    		String regionCode = reader.getString( "region_code" );
     	    		String countryCode = reader.getString( "country" );
     	    		String countryName = reader.getString( "country_name" );
     	    		String continent_code = reader.getString( "continent_code" );
     	    		Boolean inEu = reader.getBoolean( "in_eu" );
     	    		String zipCode = reader.getString( "postal" );
     	    		String latitude = reader.getString( "latitude" );
     	    		String longitude = reader.getString( "longitude" );
     	    		String timeZone = reader.getString( "timezone" );
     	    		String utcOffset = reader.getString( "utc_offset" );
     	    		String countryCallingCode = reader.getString( "country_calling_code" );
     	    		String currency = reader.getString( "currency" );
     	    		String languages = reader.getString( "languages" );
     	    		String asn = reader.getString( "asn" );
     	    		String serviceProvider = reader.getString( "org" );
     	    		
     	    		// create a new CityData object
     	    		cd = new CityData( city, countryName, countryCode, regionName,
     	    				regionCode, Float.parseFloat( latitude ), Float.parseFloat( longitude ) );
     			}// end of if block			
     		}// end of try block
    		catch( IOException e ) 
    		{
    			logMessage( LogLevel.SEVERE, e.getMessage(),
    				TAG + "::getSystemLocation [line: " + getExceptionLineNumber( e ) + "]" );
    		}// end of catch block
     		catch ( JSONException e )
     		{
     			logMessage( LogLevel.SEVERE, e.getMessage(),
    				TAG + "::getSystemLocation [line: " + getExceptionLineNumber( e ) + "]" );
     		}// end of catch block
    	}// end of catch block 
    	catch ( JDOMException jdomex )
    	{
    		 logMessage( LogLevel.SEVERE, jdomex.getMessage(),
		        TAG + "::getSystemLocation [line: " + getExceptionLineNumber( jdomex ) + "]" );
    	}// end of catch block
    	
    	return cd;
    }// end of method getSystemLocation
    
    /***
     * Retrieves the system's IP Address
     * 
     * @return A {@code String} representation of the system's IP address
     */
    public static String getSystemIpAddress()
    {
    	String ip = null;
    	
    	// check if the underlying OS is Windows
    	if( isWindows() )
    	{
    		Process uptimeProc = null;
    		StringBuffer ipAddress = new StringBuffer();
    		
    		try 
    		{
    			uptimeProc = Runtime.getRuntime().exec("ipconfig");
    			BufferedReader br = new BufferedReader(
    					  				new InputStreamReader( uptimeProc.getInputStream() ) );
    			String line = null;
    			
    			// If find that the temporary ip address on windows reveals a more precise location
    			// when using the IP address to determine location
    			while(( line = br.readLine() ) != null )
    			{
    				String tempAdd = "Temporary IPv6 Address. . . . . . :";
    				
    				if( line.contains( tempAdd ) ) 
    				{
    					ipAddress.append( line.substring( tempAdd.length() + 4 ) );
    					break;
    				}// end of if block
    			}// end of while loop
    			
    			ip = ipAddress.toString().trim();
    		}// end of try block
    		catch ( IOException e )
    		{
    			// fallback in case the command encountered an error
    			String[][] jsonUrls = { { "https://www.trackip.net/ip?json", "IP" }, 
		                { "https://api.ipify.org?format=json", "ip" }
                      };
				String strJSON = null;
				String[] urlUsed = null;
				
				if ( hasInternetConnection() )
				{
				    while( strJSON == null ) 
				    {
				    	for ( String[] url : jsonUrls ) 
				    	{
				    		try
				            {
				                strJSON = HttpHelper.downloadUrl( url[ 0 ] );
				                urlUsed = url;
				            }// end of try block
				            catch ( IOException io )
				            {
				            	strJSON = null;
				            }// end of catch block
						}// end of for each loop
				    }// end of while loop
					
				    try 
					{
						Object json = new JSONTokener( strJSON ).nextValue();
						
						// Check if a JSON was returned from the web service
						if ( json instanceof JSONObject )
						{
							// Get the full HTTP Data as JSONObject
							JSONObject reader = new JSONObject( strJSON );
							
							// Get the String returned from the object
							ip = reader.getString( urlUsed[ 1 ] );
						}// end of if block			
					}// end of try block
					catch ( JSONException je )
					{
						 logMessage( LogLevel.WARNING, je.getMessage(),
					        TAG + "::getSystemIpAddress [line: " + getExceptionLineNumber( je ) + "]" );
					}// end of catch block
				
				}// end of if block
				else
				{
					JOptionPane.showMessageDialog( null, "No Internet Connection.",
						WeatherLionMain.PROGRAM_NAME, JOptionPane.ERROR_MESSAGE );
				
				}// end of else block
    		}// end of catch block
    	}// end of if block
    	
        // Return the data from specified url
        return ip;

    }// end of method getSystemIpAddress
    
    /***
     * Use the logger to log messages locally
     * 
     * @param level	Level of the log
     * @param message	Message to be logged
     * @param inMethod	The method in which the data required logging
     */
    public static void logMessage( LogLevel level, String message, String inMethod )
    {
    	// use the class name for the logger, this way you can refactor
    	Logger logger = Logger.getLogger( inMethod );
    	
    	try
		{
			WidgetLogger.setup();
			WidgetLogger.addWidgetLoggerHandlers( logger );	// Logger class

			// set the LogLevel to All so that all messages will be written
			logger.setLevel( Level.ALL );
			
			// log based on the specified log level
			switch ( level )
			{
				case INFO:
					logger.info( message );
					break;
				case SEVERE:
					logger.severe( message );
					break;
				case WARNING:
					logger.warning( message );
					break;				
				default:
					logger.finest( message );
					break;
			}// end of switch block
		}// end of try block
		catch( Exception ex )
		{
		}// end of catch block
    	
    	WidgetLogger.closeFileHandlers();
    }// end of method logMessage
    
    /**
     * Saves a file to a specified location on disc.
     * 
     * @param fileName The name of the {@code File} to be stored.
     * @param path	The path to the specified file.
     * @param content	The content to be stored in the specified file.
     */
    public static void saveToFile( String fileName, String path, String content )
    {
    	File fn = new File( path + fileName );
    	
    	try ( FileOutputStream fop = new FileOutputStream( fn ) )
    	{
    		// if file doesn't exists, then create it
    		if ( !fn.exists() )
    		{
    			fn.createNewFile();
    		}// end of if block
    		
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write( contentInBytes );
			fop.flush();						
		}// end of try block 
    	catch ( IOException e )
    	{
    		logMessage( LogLevel.SEVERE, e.getMessage(),
 		        TAG + "::saveToFile [line: " +
        	    getExceptionLineNumber( e ) + "]" );
		}// end of catch block    	
    }// end of method saveToFile    

    /**
     * Uses city information contained in a {@code String} JSON format
     * and converts it to a {@code CityData} {@code Object}.
     * 
     * @param cityJSON The city data in a JSON formatted {@code String}
     * @return	An {@code Object} of the {@code CityData} custom class
     */
    public static CityData createCityData( String cityJSON )
    {
    	CityData currentCityData = new CityData();
    	
    	try 
		{
	    	if( cityJSON != null )
	    	{
	    		Object json = new JSONTokener( cityJSON ).nextValue();
	    		String cityName = null;
				String countryName = null;
				String countryCode = null;
				String regionName = null;
				String regionCode = null;				
				String Latitude = null;
				String Longitude = null;
	
				// Check if a JSON was returned from the web service
				if ( json instanceof JSONObject )
				{
					// Get the full HTTP Data as JSONObject
					JSONObject reader = new JSONObject( cityJSON );
					// Get the JSONObject "query"
					JSONObject query = reader.getJSONObject( "query" );
					JSONObject results = query.getJSONObject( "results" );
					JSONArray places = results.getJSONArray( "place" );
					int matchCount = places.length();
	
					// if the place array only contains one object, then only one
					// match was found
					if (matchCount == 1) 
					{
						JSONObject place = places.getJSONObject( 0 );
						JSONObject centroid = place.getJSONObject( "centroid" );
						JSONObject admin1 = place.getJSONObject( "admin1" );
						JSONObject country = place.getJSONObject( "country" );
						
						cityName = UtilityMethod.toProperCase(place.getString( "name" ) );
						countryName = UtilityMethod.toProperCase( country
								.getString( "content" ) );
						countryCode = country.getString( "code" ).toUpperCase();
						regionName = UtilityMethod.toProperCase( admin1.getString( "content" ) );
						regionCode = admin1.getString( "code" ).toUpperCase();					
						Latitude = centroid.getString( "latitude" );
						Longitude = centroid.getString( "longitude" );
						
						currentCityData.setCityName( cityName );
						currentCityData.setCountryName( countryName );
						currentCityData.setCountryCode( countryCode );
						currentCityData.setRegionName( regionName );
						currentCityData.setRegionCode( regionCode );
						currentCityData.setLatitude( Float.parseFloat( Latitude ) );
						currentCityData.setLongitude( Float.parseFloat( Longitude ) );
	
					}// end of if block
					else
					{					
						// Multiple matches were found
						// Store the data in local storage
						Gson gson = new GsonBuilder()
								.registerTypeAdapter(
										YahooGeoLocation.class,
										new YahooGeoLocation.YahooGeoLocationDeserializer() )
								.create();
						YahooGeoLocation.cityGeographicalData = gson.fromJson(
								cityJSON, YahooGeoLocation.class );	
						
						YahooGeoLocation.Query.Results.Place place = YahooGeoLocation.cityGeographicalData
														.getQuery()
														.getResults()
														.getPlace().get( 0 );
												
						cityName = UtilityMethod.toProperCase( place.getName() );
						countryName = UtilityMethod.toProperCase( place.getCountry().getContent() );
						countryCode = place.getCountry().getCode().toUpperCase();
						regionName = UtilityMethod.toProperCase( place.getAdmin1().getContent() );
						regionCode = place.getAdmin1().getCode().toUpperCase();					
						Latitude = place.getCentroid().getLatitude();
						Longitude = place.getCentroid().getLongitude();
						
						currentCityData.setCityName( cityName );
						currentCityData.setCountryName( countryName );
						currentCityData.setCountryCode( countryCode );
						currentCityData.setRegionName( regionName );
						currentCityData.setRegionCode( regionCode );
						currentCityData.setLatitude( Float.parseFloat( Latitude ) );
						currentCityData.setLongitude( Float.parseFloat( Longitude ) );
	
					}// end of else block
				}// end of if block
	    	}// end of if block
		}// end of try block
		catch ( JSONException e )
		{
			logMessage( LogLevel.WARNING, e.getMessage(),
		        TAG + "::createCityData [line: " + getExceptionLineNumber( e ) + "]" );
		}// end of catch block
    	
    	return currentCityData;
    	
    }// end of method createCityData
    
    /**
     * Uses data received from the GeoNames web service in a {@code String} JSON format
     * and converts it to a {@code CityData} {@code Object}.
     * 
     * @param cityJSON The city data in a JSON formatted {@code String}
     * @return	An {@code Object} of the {@code CityData} custom class
     */
    public static CityData createGeoNamesCityData( String cityJSON )
    {
    	CityData currentCityData = new CityData();
    	
    	try 
		{
	    	if( cityJSON != null )
	    	{
	    		Object json = new JSONTokener( cityJSON ).nextValue();
	    		String cityName = null;
	    		String localCityName = null;
	    		String countyCityName = null;
				String countryName = null;
				String countryCode = null;
				String regionCode = null;
				String regionName = null;
				Float Latitude = null;
				Float Longitude = null;
	
				// Check if a JSON was returned from the web service
				if ( json instanceof JSONObject )
				{
					// Get the full HTTP Data as JSONObject
					JSONObject geoNamesJSON = new JSONObject( cityJSON );
					// Get the JSONObject "geonames"
					JSONArray geoNames = geoNamesJSON.optJSONArray( "geonames" );
					int matchCount = geoNamesJSON.getInt( "totalResultsCount" );
	
					// if the place array only contains one object, then only one
					// match was found
					if ( matchCount > 0 ) 
					{
						JSONObject place = geoNames.getJSONObject( 0 );
						
						cityName = place.getString( "name" );
						countryName =  place.getString( "countryName" );
						countryCode = place.getString( "countryCode" );
						localCityName = place.getString( "toponymName" );
						regionCode = place.getString( "adminCode1" );					
						regionName = countryCode.equalsIgnoreCase( "US" ) ?
								     UtilityMethod.usStatesByCode.get( regionCode ) :
								     null;
						Latitude = Float.parseFloat( place.getString( "lat" ) );
						Longitude = Float.parseFloat( place.getString( "lng" ) );
						
						currentCityData.setCityName( cityName );
						currentCityData.setCountryName( countryName );
						currentCityData.setCountryCode( countryCode );
						currentCityData.setRegionCode( regionCode );
						currentCityData.setRegionName( regionName );
						currentCityData.setLatitude( Latitude );
						currentCityData.setLongitude( Longitude );
	
					}// end of if block
				}// end of if block
	    	}// end of if block
		}// end of try block
		catch ( JSONException e )
		{
			  logMessage( LogLevel.WARNING, e.getMessage(),
				  TAG + "::createGeoNamesCityData [line: " + getExceptionLineNumber( e ) + "]" );
		}// end of catch block
    	
    	return currentCityData;
    	
    }// end of method createGeoNamesCityData
    
    /**
     * Uses data received from the Here Maps web service in a {@code String} JSON format
     * and converts it to a {@code CityData} {@code Object}.
     * 
     * @param cityJSON The city data in a JSON formatted {@code String}
     * @return	An {@code Object} of the {@code CityData} custom class
     */
    public static CityData createHereCityData( String cityJSON )
    {
    	CityData currentCityData = new CityData();
    	
    	try 
		{
	    	if( cityJSON != null )
	    	{
	    		Object json = new JSONTokener( cityJSON ).nextValue();
	    		String localCityName = null;
				String countryName = null;
				String countryCode = null;
				String regionName = null;
				Float Latitude = null;
				Float Longitude = null;
	
				// Check if a JSON was returned from the web service
				if ( json instanceof JSONObject )
				{
					// Get the full HTTP Data as JSONObject
					JSONObject reader = new JSONObject( cityJSON );
					// Get the JSONObject "query"
					JSONObject response = reader.getJSONObject( "Response" );
					JSONObject view = response.getJSONArray( "View" ).getJSONObject( 0 );
					JSONArray places = view.optJSONArray( "Result" );
					int matchCount = places.length();
	
					// if the place array only contains one object, then only one
					// match was found
					if ( matchCount == 1 ) 
					{
						JSONObject place = places.getJSONObject( 0 );
						JSONObject location = place.getJSONObject( "Location" );
						JSONObject displayPosition = location.getJSONObject( "DisplayPosition" );
						JSONObject navigationPosition = location.getJSONArray( "NavigationPosition" ).getJSONObject( 0 );
						JSONObject address = location.getJSONObject( "Address" );
						JSONArray additionalData = address.getJSONArray( "AdditionalData" );
						
						countryName = UtilityMethod.toProperCase( 
								additionalData.getJSONObject( 0 ).getString( "value" ) );
						countryCode = worldCountryCodes.get( countryName ).toUpperCase();
						regionName = additionalData.getJSONObject( 1 ).getString( "value" ); // "key": "StateName"
						
						localCityName = countryName.equalsIgnoreCase( "USA" ) ?             
										UtilityMethod.toProperCase( address.getString( "District" ) ) :
										UtilityMethod.toProperCase( address.getString( "City" ) );
						Latitude = (float) navigationPosition.getDouble( "Latitude" );
						Longitude = (float) navigationPosition.getDouble( "Longitude" );
						
						currentCityData.setCityName( localCityName );
						currentCityData.setCountryName( countryName );
						currentCityData.setCountryCode( countryCode );
						currentCityData.setRegionName( regionName );
						currentCityData.setLatitude( Latitude );
						currentCityData.setLongitude( Longitude );
	
					}// end of if block
					else
					{					
						// Multiple matches were found
						// Store the data in local storage
						Gson gson = new GsonBuilder()
								.registerTypeAdapter(
										HereGeoLocation.class,
										new HereGeoLocation.HereGeoLocationDeserializer() )
								.create();
						HereGeoLocation.cityGeographicalData = gson.fromJson(
								cityJSON, HereGeoLocation.class );	
						
						HereGeoLocation.Response.View.Result place = HereGeoLocation.cityGeographicalData
														.getResponse()
														.getView()
														.getResult().get( 0 );
						
						localCityName = UtilityMethod.toProperCase( place.getLocation().getAddress().getDistrict() );
						countryName = UtilityMethod.toProperCase( place.getLocation().getAddress().getCountry() );
						countryCode = worldCountryCodes.get( place.getLocation().getAddress().getCountry() ).toUpperCase();
						regionName = place.getLocation().getAddress().getAdditionalData().get( 1 ).getValue(); // "key": "StateName"
						Latitude = place.getLocation().getNavigationPosition().getLatitude();
						Longitude = place.getLocation().getNavigationPosition().getLongitude();
						
						currentCityData.setCityName( localCityName );
						currentCityData.setCountryName( countryName );
						currentCityData.setCountryCode( countryCode );
						currentCityData.setRegionName( regionName );
						currentCityData.setLatitude( Latitude );
						currentCityData.setLongitude( Longitude );
	
					}// end of else block
				}// end of if block
	    	}// end of if block
		}// end of try block
		catch ( JSONException e )
		{
			logMessage( LogLevel.SEVERE, e.getMessage(),
		        TAG + "::createHereCityData [line: " + getExceptionLineNumber( e ) + "]" );
		}// end of catch block
    	
    	return currentCityData;
    	
    }// end of method createHereCityData
    
    /**
 	 * Determines weather or not a specific database contains a specific table
 	 * 
 	 * @param tableName The table that is in question
 	 * 
     * @return        	 The {@code int} value of 0 if not found of 1 if the table is found
 	 */
 	public static int checkIfTableExists( String dbName, String tableName ) 
 	{
 		int affected_rows = 0;
 		String fullyQualifiedDBName = dbName + ".sqlite_master";
 		String SQL = "SELECT name FROM " + fullyQualifiedDBName + " WHERE type='table' AND name=?";
		
 		// connection to a database cannot be null
		if( WeatherLionMain.conn != null )
		{
			try(
					PreparedStatement stmt = WeatherLionMain.conn.prepareStatement( 
		            		SQL,
		            		ResultSet.TYPE_FORWARD_ONLY,
		            		ResultSet.CONCUR_READ_ONLY );
		    )
	        {
				stmt.setString( 1, tableName );
				affected_rows = stmt.executeUpdate();
	        }// end of try block
			catch ( SQLException e )
			{
				logMessage( LogLevel.SEVERE, e.getMessage(),
				        TAG + "::checkIfTableExists [line: " + getExceptionLineNumber( e ) + "]" );
				
				// sometimes the execute update statement returns a result set so it will be handled here
				if( e.getMessage().toLowerCase().equals( "query returns results" ) )
				{
					try(
							PreparedStatement stmt = WeatherLionMain.conn.prepareStatement( 
				            		SQL,
				            		ResultSet.TYPE_FORWARD_ONLY,
				            		ResultSet.CONCUR_READ_ONLY );
				    )
			        {
						stmt.setString( 1, tableName );
						ResultSet rs = stmt.executeQuery();
						
						while ( rs.next() ) 
						{
							affected_rows++;
						}// end of while
			        }// end of try block
					catch ( SQLException ee )
					{
						affected_rows = 0;
						logMessage( LogLevel.SEVERE, ee.getMessage(),
						        TAG + "::checkIfTableExists [line: " + getExceptionLineNumber( ee ) + "]" );
					}// end of catch block
				}// end of 
			}// end of catch block
		}// end of if block
			
		return affected_rows;
 	}// end of method checkIfTableExists
    
    /**
	 * Creates an SQLite 3 database if one does not exist
	 * 
	 * @return An {@code int} value indicating success or failure.<br /> 1 for success and 0 for failure.
	 */
	public static int createWSADatabase()
	{
		int ac = checkIfTableExists( "wak", "access_keys" );
		int success = 1; // assume success unless otherwise
		
		if( ac == 0 )
		{
			String siteAccessTable =
					"CREATE TABLE wak.access_keys (KeyProvider TEXT, KeyName TEXT, KeyValue TEXT(64), Hex TEXT)";
			
			try
			(
				Statement stmt = WeatherLionMain.conn.createStatement();
			)
			{
				stmt.executeUpdate( siteAccessTable );    
			}// end of try block
			catch( SQLException e )
			{
				success = 0;
				logMessage( LogLevel.SEVERE, e.getMessage(),
				        TAG + "::createWSADatabase [line: " + getExceptionLineNumber( e ) + "]" );
			}// end of catch block
		}// end of if block
		
		return success;
	}// end of method createWSADatabase
    
    /**
     * Creates an SQLite 3 database if one does not exist
     * 
     * @return An {@code int} value indicating success or failure.<br /> 1 for success and 0 for failure.
     */
 	public static int createWorldCitiesDatabase()
 	{
 		int ac = checkIfTableExists( "WorldCities", "world_cities" );
		int success = 1; // assume success unless otherwise
		
		if( ac == 0 )
		{
			String worldCitiesTable =
	 				"CREATE TABLE WorldCities.world_cities (CityName TEXT, CountryName TEXT, CountryCode TEXT (2), "
	 						+  "RegionName TEXT, RegionCode TEXT (2), Latitude REAL, Longitude REAL, DateAdded TEXT)";
	 		
	 		try
	 		(
	 			Statement stmt = WeatherLionMain.conn.createStatement();
	 		)
	 		{
	             stmt.executeUpdate( worldCitiesTable );    
	 		}// end of try block
	 		catch( SQLException e )
	 		{
	 			success = 0;
	 			logMessage( LogLevel.SEVERE, e.getMessage(),
				        TAG + "::createWorldCitiesDatabase [line: " + getExceptionLineNumber( e ) + "]" );
	 		}// end of catch block
		}// end of if block
		
		return success;
 	}// end of method createWorldCitiesDatabase
 	
 	/**
 	 * Saves a city to a local SQLite 3 database.
 	 * 
 	 * @param cityName		The name of the city
 	 * @param countryName	The name of the country
 	 * @param countryCode	The country's corresponding two-letter country code
 	 * @param regionName	The name of the region in which the city is located
 	 * @param regionCode	The region's corresponding two-letter region code
 	 * @param latitude		The line of latitude value
 	 * @param longitude		The line of longitude value
 	 * @return				An {@code int} value indicating success or failure.<br /> 1 for success and 0 for failure.
 	 */
 	public static int addCityToDatabase( String cityName, String countryName, String countryCode,
 			String regionName, String regionCode, float latitude, float longitude ) 
	{
 		int affected_rows = 0;
		String SQL = "INSERT INTO WorldCities.world_cities ( CityName, CountryName, CountryCode,"
				+ " RegionName, RegionCode, Latitude, Longitude, DateAdded  ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
				
		try(
	            PreparedStatement stmt = WeatherLionMain.conn.prepareStatement( 
	            		SQL,
	            		ResultSet.TYPE_FORWARD_ONLY,
	            		ResultSet.CONCUR_READ_ONLY );
	    )
        {
			stmt.setString( 1, cityName );
			stmt.setString( 2, countryName );
			stmt.setString( 3, countryCode );
			stmt.setString( 4, regionName );
			stmt.setString( 5, regionCode );
			stmt.setFloat( 6, latitude );
			stmt.setFloat( 7, longitude );
			stmt.setString( 8, new SimpleDateFormat( "E, MMM, dd, yyyy h:mm a" ).format( new Date() ) );
			affected_rows = stmt.executeUpdate();
			
			return affected_rows;
        }// end of try block
		catch ( SQLException e )
		{
			logMessage( LogLevel.SEVERE, e.getMessage(),
			        TAG + "::addCityToDatabase [line: " + getExceptionLineNumber( e ) + "]" );
			return 0;
		}// end of catch block
	}// end of method addCityToDatabase
 	
 	/**
 	 * Retrieves a city from a local SQLite 3 database
 	 * 
 	 * @param cityName		The name of the city
 	 * @param regionCode	The region's corresponding two-letter region code
 	 * @param countryName	The name of the country
 	 * @return				An {@code Object} of the {@code CityData} custom class
 	 */
	public static CityData getCityDataFromDatabase( String cityName, String regionCode, String countryName )
	{
		String SQL;
		
		if( regionCode != null && countryName != null )
		{
			SQL = "SELECT CityName, CountryName, CountryCode, RegionCode, Latitude,"
					+ " Longitude FROM WorldCities.world_cities WHERE CityName = ? AND RegionCode = ? AND CountryName = ?";
		}// end of if block
		else if( regionCode != null && countryName == null )
		{
			SQL = "SELECT CityName, CountryName, CountryCode, RegionCode, Latitude,"
					+ " Longitude FROM WorldCities.world_cities WHERE CityName = ? AND RegionCode = ?";			
		}// end of else if block
		else if( regionCode == null && countryName != null )
		{
			SQL = "SELECT CityName, CountryName, CountryCode, RegionCode, Latitude"
					+ ", Longitude FROM WorldCities.world_cities WHERE CityName = ? AND CountryName = ?";			
		}// end of else if block
		else
		{
			SQL = "SELECT CityName, CountryName, CountryCode, RegionCode, Latitude,"
					+ " Longitude FROM WorldCities.world_cities WHERE CityName = ?";
		}// end of else block
		
		ResultSet rs = null;
		
		try(
				PreparedStatement stmt = WeatherLionMain.conn.prepareStatement( 
							SQL,
							ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY );
		)
		{
			if( regionCode != null && countryName != null )
			{
				stmt.setString( 1, cityName );
				stmt.setString( 2, regionCode );
				stmt.setString( 3, countryName );
			}// end of if block
			else if( regionCode != null && countryName == null )
			{
				stmt.setString( 1, cityName );
				stmt.setString( 2, regionCode );						
			}// end of else if block
			else if( regionCode == null && countryName != null )
			{
				stmt.setString( 1, cityName );
				stmt.setString( 3, countryName );					
			}// end of else if block
			else
			{
				stmt.setString( 1, cityName );					
			}// end of else block			
			
			rs = stmt.executeQuery();			
			int found = 0;
			
			while ( rs.next() )
			{
				found++;
			}// end of while loop
			
			if( found > 0 )
			{
				return new CityData(rs.getString( "CityName" ), rs.getString( "CountryName" ), rs.getString( "CountryCode" ),
						rs.getString( "RegionCode" ), rs.getFloat( "Latitude" ), rs.getFloat( "Longitude" ) );
			}// end of if block
			else
			{
				return null;
			}// end of else block
	    }// end of try block
		catch( SQLException e )
		{
			logMessage( LogLevel.SEVERE, e.getMessage(),
			        TAG + "::getCityDataFromDatabase [line: " + getExceptionLineNumber( e ) + "]" );
			return null;
		}// end of catch block
	}// end of method getCityDataFromDatabase
	
	/**
	 * Custom response dialog box.
	 * 
	 * @param message Message to be displayed to the user.
	 * @param title		Title of the message dialog.
	 * @param messageType	The type of message to be displayed.
	 * @return 	An {@code int} value representing the position in the array in
	 * which the button appears zero based.
	 */
	public static int responseBox( String message, String title, String[] buttons, int messageType, JFrame parent )
	{
		// create a custom dialog box with program icon on the title bar
        JOptionPane prompt = new JOptionPane( message, messageType, 
        		JOptionPane.DEFAULT_OPTION, null, buttons, null );
        JDialog dialog = prompt.createDialog( parent, title );
        dialog.setIconImage( WeatherLionMain.PROGRAM_ICON );
        dialog.pack();
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.setLocationRelativeTo( parent );
        dialog.setVisible( true );
        
        return Arrays.asList( buttons ).indexOf( prompt.getValue().toString() );
	}// end of method confirmBox
	
	/**
	 * Custom message dialog box.
	 * 
	 * @param message Message to be displayed to the user.
	 * @param title		Title of the message dialog.
	 * @param messageType	The type of message to be displayed.
	 */
	public static void msgBox( String message, String title, int messageType, JFrame parent )
	{
		// create a custom dialog box with program icon on the title bar
        JOptionPane prompt = new JOptionPane( message, messageType, 
        		JOptionPane.DEFAULT_OPTION, null, new Object[]{ "OK" }, null );
        JDialog dialog = prompt.createDialog( parent, title );
        dialog.setIconImage( WeatherLionMain.PROGRAM_ICON );
        dialog.pack();
        dialog.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        dialog.setLocationRelativeTo( parent );
        dialog.setVisible( true);
	}// end of method msgBox
	
	/***
	 * Displays a message box prompt to the user
	 * 
	 * @param message message A {@code String} representing a message to be displayed to the user
	 * @param title A {@code String} representing the title for the dialog box
	 * @param type A {@code Integer} value representing the type of message icon displayed in the dialog
	 */
	private static void messagePrompt( String message, String title, int type ) 
	{
		// prompt the user
		JOptionPane.showMessageDialog( null, message , title , type  );
	}// end of method openingPrompt
	
	/***
	 * Replace the last occurrence of a {@code String} contained in another {@code String}
	 * 
	 * @param find		The {@code String} to look for in another {@code String}
	 * @param replace	The replacement {@code String}
	 * @param string	The {@code String} that contains another {@code String} 
	 * @return			A modified {@code String} reflecting the requested change
	 */
	public static String replaceLast( String find, String replace, String string )
	{
        int lastIndex = string.lastIndexOf( find );
        
        if ( lastIndex == -1 )
        {
            return string;
        }// end of if block
        
        String beginString = string.substring( 0, lastIndex );
        String endString = string.substring( lastIndex + find.length() );
        
        return beginString + replace + endString;
	}// end of method replaceLast
	
	/***
	 * Searches for the number of times that a {@code char} is found in a {@code String}
	 * 
	 * @param c			   	The {@code char} to look for in a {@code String}
	 * @param checkString 	The {@code String} that contains the specified {@code char}
	 * @return				An {@code int} representing the number of occurrences of the search character
	 */
	public static int numberOfCharacterOccurrences( char c, String checkString )
	{
		int cn = 0;
		
        for ( int i = 0; i < checkString.length(); i++ )
        {
            if ( c == checkString.charAt( i ) )
            {
                cn++;
            }// end of if block
        }// end of for each loop
        
        return cn;
	}// end of method numberOfCharacterOccurrences
	
	/**
	 * Return the name of the SQLite database file currently in use.
	 * 
	 * @return		A {@code String} value representing the name of the SQLite database file currently in use  
	 */
	public static String getCurrentDatabaseFileName()
	{
		String siteAccessTable = "PRAGMA database_list";
		String cc = null;

		try
		(
			Statement stmt = WeatherLionMain.conn.createStatement();
		)
		{
			ResultSet rs = stmt.executeQuery( siteAccessTable );
			
			// there should only be one database attached to a connection in theory
			while( rs.next() )
			{
				cc = rs.getString( 3 );
			}// end of while loop
			
			return cc.substring( cc.lastIndexOf( "\\" ) + 1, cc.length() );
		}// end of try block
		catch( SQLException e )
		{
			logMessage( LogLevel.SEVERE, e.getMessage(),
		        TAG + "::getCurrentDatabaseFileName [line: " + getExceptionLineNumber( e ) + "]" );
			return null;
		}// end of catch block
	}// end of method getCurrentDatabaseFileName
	
	/***
	 * Remove unwanted lock files and others from the log directories
	 */
	public static void cleanLockFiles() 
	{
		int badHTMLFileCount = 0;
		int badTXTFileCount = 0;
		
		// remove unwanted HTML files
		try ( DirectoryStream< Path > files = Files.newDirectoryStream( Paths.get( "res/log/html/" ) ) )
		{
	        for ( Path path : files )
	        {
	        	File file = new File( path.toString() );
	        	String fileExtension = file.getName().substring( file.getName().lastIndexOf( "." ) + 1 );
	        	
	            if( path.toString().contains( ".lck" ) || !fileExtension.equals( "html" ) )
	            {
	            	if( file.delete() )
	            	{
	            		badHTMLFileCount++;
	        		}// end of if block
	            }// end of if block
	        }// end of for each loop
	    }// end of try block 
		catch ( IOException e )
		{
			logMessage( LogLevel.WARNING, e.getMessage(),
		        TAG + "::cleanLockFiles [line: " + getExceptionLineNumber( e ) + "]" );
		}// end of catch block
		
		// remove unwanted TXT files
		try ( DirectoryStream< Path > files = Files.newDirectoryStream( Paths.get( "res/log/text/" ) ) )
		{
	        for ( Path path : files )
	        {
	        	File file = new File( path.toString() );
	        	String fileExtension = file.getName().substring( file.getName().lastIndexOf( "." ) + 1 );
	        	
	            if( path.toString().contains( ".lck" ) || !fileExtension.equals( "txt" ) )
	            {
	            	if( file.delete() )
	            	{
	            		badTXTFileCount++;
	        		}// end of if block
	            }// end of if block
	        }// end of for each loop
	    }// end of try block 
		catch ( IOException e )
		{
			logMessage( LogLevel.WARNING, e.getMessage(),
		        TAG + "::cleanLockFiles [line: " + getExceptionLineNumber( e ) + "]" );
		}// end of catch block
		
		if( badHTMLFileCount > 0 )
		{
			logMessage( LogLevel.WARNING, 
				"Removed " + badHTMLFileCount +
				( badHTMLFileCount == 1 ?
					" bad file " :
					" bad files " ) + "from html log directory.",
		        TAG + "::cleanLockFiles" );
		}// end of if block
		
		if( badTXTFileCount > 0 )
		{
			logMessage( LogLevel.WARNING, 
					"Removed " + badTXTFileCount +
					( badTXTFileCount == 1 ?
						" bad file " :
						" bad files " ) + "from txt log directory.",
		        TAG + "::cleanLockFiles" );
		}// end of if block
	}// end of method cleanLockFiles
	
	/***
	 * Displays a message box prompt to the user
	 * 
	 * @param asset Additional {@code String} representing the missing asset
	 */
	public static void missingRequirementsPrompt( String asset ) 
	{
		// prompt the user
		JOptionPane.showMessageDialog( null, "There are missing files or information that are neccessary for"
				+ " the program to run and\ntherefore renders the program corrupt and unable to launch!",
    			WeatherLionMain.PROGRAM_NAME + " (" + asset + ")", JOptionPane.ERROR_MESSAGE  );
		
		// log message
		logMessage( LogLevel.SEVERE, "Missing: " + asset, TAG + "::missingAssetPrompt" );
		
		System.exit( 0 );	// terminate the program
	}// end of method missingAssetPrompt
	
	/**
	 * Attempts to calculate the size of a file or directory.
	 * 
	 * <p>
	 * Since the operation is non-atomic, the returned value may be inaccurate.
	 * However, this method is quick and does its best.
	 * </p>		 
	 * @return	A {@code long} representing the size of the file in bytes
	 * @author <a href="https://stackoverflow.com/users/1418643/aksel-willgert" target="_top">Aksel Willgert</a>
	 * @see <a href='https://stackoverflow.com/questions/2149785/get-size-of-folder-or-file' target="_top">Stack Overflow</a>
	 */
	public static long size( Path path )
	{
	    final AtomicLong size = new AtomicLong(0);

	    try 
	    {
	        Files.walkFileTree( path, new SimpleFileVisitor<Path>() 
	        {
	            @Override
	            public FileVisitResult visitFile( Path file, BasicFileAttributes attrs )
	            {

	                size.addAndGet(attrs.size());
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult visitFileFailed(Path file, IOException exc) {

	                System.out.println("skipped: " + file + " (" + exc + ")");
	                // Skip folders that can't be traversed
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

	                if (exc != null)
	                    System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
	                // Ignore errors traversing a folder
	                return FileVisitResult.CONTINUE;
	            }
	        });
	    }// end of try block 
	    catch ( IOException e )
	    {
	    	logMessage( LogLevel.SEVERE, e.getMessage(),
			        TAG + "::size [line: " + getExceptionLineNumber( e ) + "]" );
	    	
	        throw new AssertionError( "walkFileTree will not throw IOException if the FileVisitor does not" );
	    }// end of catch block

	    return size.get();
	}// end of method size
}// end of class UtilityMethod
