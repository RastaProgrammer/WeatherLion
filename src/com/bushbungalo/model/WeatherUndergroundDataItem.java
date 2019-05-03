package com.bushbungalo.model;

import java.util.List;

/**
 * @author Paul O. Patterson
* <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/16/17
 */
@Deprecated
public class WeatherUndergroundDataItem
{
    private Response response;
    private Location location;
    private CurrentObservation current_observation;
    private Forecast forecast;
    private SunPhase sun_phase;

    public Response getResponse()
    {
        return response;
    }

    public void setResponse(Response response)
    {
        this.response = response;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public CurrentObservation getCurrent_observation()
    {
        return current_observation;
    }

    public void setCurrent_observation(CurrentObservation current_observation)
    {
        this.current_observation = current_observation;
    }

    public Forecast getForecast()
    {
        return forecast;
    }

    public void setForecast(Forecast forecast)
    {
        this.forecast = forecast;
    }

    public SunPhase getSun_phase()
    {
        return sun_phase;
    }

    public void setSun_phase(SunPhase sun_phase)
    {
        this.sun_phase = sun_phase;
    }

    public class Response
    {
        private float version;
        private String termsofService;
        private Feature features;

        public float getVersion()
        {
            return version;
        }

        public void setVersion(float version)
        {
            this.version = version;
        }

        public String getTermsofService()
        {
            return termsofService;
        }

        public void setTermsofService(String termsofService)
        {
            this.termsofService = termsofService;
        }

        public Feature getFeatures()
        {
            return features;
        }

        public void setFeatures(Feature features)
        {
            this.features = features;
        }

        public class Feature
        {
            private int geolookup;
            private int conditions;
            private int forecast;

            public int getGeolookup()
            {
                return geolookup;
            }

            public void setGeolookup(int geolookup)
            {
                this.geolookup = geolookup;
            }

            public int getConditions()
            {
                return conditions;
            }

            public void setConditions(int conditions)
            {
                this.conditions = conditions;
            }

            public int getForecast()
            {
                return forecast;
            }

            public void setForecast(int forecast)
            {
                this.forecast = forecast;
            }
        }// end of class Feature
    }// end of class Response

    public class Location
    {
        private String type;
        private String country;
        private String country_iso3166;
        private String state;
        private String city;
        private String tz_short;
        private String tz_long;
        private double lat;
        private double lon;
        private int zip;
        private int magic;
        private int wmo;
        private String l;
        private String requesturl;
        private String wuiurl;

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getCountry()
        {
            return country;
        }

        public void setCountry(String country)
        {
            this.country = country;
        }

        public String getCountry_iso3166()
        {
            return country_iso3166;
        }

        public void setCountry_iso3166(String country_iso3166)
        {
            this.country_iso3166 = country_iso3166;
        }

        public String getState()
        {
            return state;
        }

        public void setState(String state)
        {
            this.state = state;
        }

        public String getCity()
        {
            return city;
        }

        public void setCity(String city)
        {
            this.city = city;
        }

        public String getTz_short()
        {
            return tz_short;
        }

        public void setTz_short(String tz_short)
        {
            this.tz_short = tz_short;
        }

        public String getTz_long()
        {
            return tz_long;
        }

        public void setTz_long(String tz_long)
        {
            this.tz_long = tz_long;
        }

        public double getLat()
        {
            return lat;
        }

        public void setLat(double lat)
        {
            this.lat = lat;
        }

        public double getLon()
        {
            return lon;
        }

        public void setLon(double lon)
        {
            this.lon = lon;
        }

        public int getZip()
        {
            return zip;
        }

        public void setZip(int zip)
        {
            this.zip = zip;
        }

        public int getMagic()
        {
            return magic;
        }

        public void setMagic(int magic)
        {
            this.magic = magic;
        }

        public int getWmo()
        {
            return wmo;
        }

        public void setWmo(int wmo)
        {
            this.wmo = wmo;
        }

        public String getL()
        {
            return l;
        }

        public void setL(String l)
        {
            this.l = l;
        }

        public String getRequesturl()
        {
            return requesturl;
        }

        public void setRequesturl(String requesturl)
        {
            this.requesturl = requesturl;
        }

        public String getWuiurl()
        {
            return wuiurl;
        }

        public void setWuiurl(String wuiurl)
        {
            this.wuiurl = wuiurl;
        }

        public class NearbyWeatherStations
        {
            public class Airport
            {
                List<Station> station;

                public List<Station> getStation()
                {
                    return station;
                }

                public void setStation(List<Station> station)
                {
                    this.station = station;
                }

                public class Station
                {
                    private String city;
                    private String state;
                    private String country;
                    private String icao;
                    private double lat;
                    private double lon;

                    public String getCity()
                    {
                        return city;
                    }

                    public void setCity(String city)
                    {
                        this.city = city;
                    }

                    public String getState()
                    {
                        return state;
                    }

                    public void setState(String state)
                    {
                        this.state = state;
                    }

                    public String getCountry()
                    {
                        return country;
                    }

                    public void setCountry(String country)
                    {
                        this.country = country;
                    }

                    public String getIcao()
                    {
                        return icao;
                    }

                    public void setIcao(String icao)
                    {
                        this.icao = icao;
                    }

                    public double getLat()
                    {
                        return lat;
                    }

                    public void setLat(double lat)
                    {
                        this.lat = lat;
                    }

                    public double getLon()
                    {
                        return lon;
                    }

                    public void setLon(double lon)
                    {
                        this.lon = lon;
                    }
                }// end of class Station
            }// end of class Airport

            public class PWS
            {
                List<Station> station;

                public List<Station> getStation()
                {
                    return station;
                }

                public void setStation(List<Station> station)
                {
                    this.station = station;
                }

                public class Station
                {
                    private String city;
                    private String state;
                    private String country;
                    private String id;
                    private double lat;
                    private double lon;
                    private int distance_km;
                    private int distance_mi;

                    public String getCity()
                    {
                        return city;
                    }

                    public void setCity(String city)
                    {
                        this.city = city;
                    }

                    public String getState()
                    {
                        return state;
                    }

                    public void setState(String state)
                    {
                        this.state = state;
                    }

                    public String getCountry()
                    {
                        return country;
                    }

                    public void setCountry(String country)
                    {
                        this.country = country;
                    }

                    public String getId()
                    {
                        return id;
                    }

                    public void setId(String id)
                    {
                        this.id = id;
                    }

                    public double getLat()
                    {
                        return lat;
                    }

                    public void setLat(double lat)
                    {
                        this.lat = lat;
                    }

                    public double getLon()
                    {
                        return lon;
                    }

                    public void setLon(double lon)
                    {
                        this.lon = lon;
                    }

                    public int getDistance_km()
                    {
                        return distance_km;
                    }

                    public void setDistance_km(int distance_km)
                    {
                        this.distance_km = distance_km;
                    }

                    public int getDistance_mi()
                    {
                        return distance_mi;
                    }

                    public void setDistance_mi(int distance_mi)
                    {
                        this.distance_mi = distance_mi;
                    }
                }// end of class Station
            }// end of class PWS
        }// end of method NearbyWeatherStations
    }// end of class Location

    public class CurrentObservation
    {
        private Image image;
        private DisplayLocation display_location;
        private ObservationLocation observation_location;
        private Estimated estimated;
        private String station_id;
        private String observation_time;
        private String observation_time_rfc822;
        private String observation_epoch;
        private String local_time_rfc822;
        private String local_epoch;
        private String local_tz_short;
        private String local_tz_long;
        private String local_tz_offset;
        private String weather;
        private String temperature_String;
        private String temp_f;
        private String temp_c;
        private String relative_humidity;
        private String wind_String;
        private String wind_dir;
        private String wind_degrees;
        private float wind_mph;
        private float wind_gust_mph;
        private float wind_kph;
        private float wind_gust_kph;
        private float pressure_mb;
        private float pressure_in;
        private String pressure_trend;
        private String dewpoint_String;
        private float dewpoint_f;
        private float dewpoint_c;
        private String heat_index_String;
        private String heat_index_f;
        private String heat_index_c;
        private String windchill_String;
        private String windchill_f;
        private String windchill_c;
        private String feelslike_String;
        private float feelslike_f;
        private float feelslike_c;
        private String visibility_mi;
        private String visibility_km;
        private String solarradiation;
        private String UV;
        private String precip_1hr_in;
        private String precip_1hr_metric;
        private String precip_today_String;
        private String precip_today_in;
        private String precip_today_metric;
        private String icon;
        private String icon_url;
        private String forecast_url;
        private String history_url;
        private String ob_url;
        private String nowcast;

        public class Image
        {
            private String url;
            private String title;
            private String link;

            public String getUrl()
            {
                return url;
            }

            public void setUrl(String url)
            {
                this.url = url;
            }

            public String getTitle()
            {
                return title;
            }

            public void setTitle(String title)
            {
                this.title = title;
            }

            public String getLink()
            {
                return link;
            }

            public void setLink(String link)
            {
                this.link = link;
            }
        }// end of class Image

        public class DisplayLocation
        {
            private String full;
            private String city;
            private String state;
            private String state_name;
            private String country;
            private String country_iso3166;
            private int zip;
            private int magic;
            private int wmo;
            private double latitude;
            private double longitude;
            private String elevation;

            public String getFull()
            {
                return full;
            }

            public void setFull(String full)
            {
                this.full = full;
            }

            public String getCity()
            {
                return city;
            }

            public void setCity(String city)
            {
                this.city = city;
            }

            public String getState()
            {
                return state;
            }

            public void setState(String state)
            {
                this.state = state;
            }

            public String getState_name()
            {
                return state_name;
            }

            public void setState_name(String state_name)
            {
                this.state_name = state_name;
            }

            public String getCountry()
            {
                return country;
            }

            public void setCountry(String country)
            {
                this.country = country;
            }

            public String getCountry_iso3166()
            {
                return country_iso3166;
            }

            public void setCountry_iso3166(String country_iso3166)
            {
                this.country_iso3166 = country_iso3166;
            }

            public int getZip()
            {
                return zip;
            }

            public void setZip(int zip)
            {
                this.zip = zip;
            }

            public int getMagic()
            {
                return magic;
            }

            public void setMagic(int magic)
            {
                this.magic = magic;
            }

            public int getWmo()
            {
                return wmo;
            }

            public void setWmo(int wmo)
            {
                this.wmo = wmo;
            }

            public double getLatitude()
            {
                return latitude;
            }

            public void setLatitude(double latitude)
            {
                this.latitude = latitude;
            }

            public double getLongitude()
            {
                return longitude;
            }

            public void setLongitude(double longitude)
            {
                this.longitude = longitude;
            }

            public String getElevation()
            {
                return elevation;
            }

            public void setElevation(String elevation)
            {
                this.elevation = elevation;
            }
        }// end of method DisplayLocation

        public class ObservationLocation
        {
            private String full;
            private String city;
            private String state;
            private String country;
            private String country_iso3166;
            private double latitude;
            private double longitude;
            private String elevation;

            public String getFull()
            {
                return full;
            }

            public void setFull(String full)
            {
                this.full = full;
            }

            public String getCity()
            {
                return city;
            }

            public void setCity(String city)
            {
                this.city = city;
            }

            public String getState()
            {
                return state;
            }

            public void setState(String state)
            {
                this.state = state;
            }

            public String getCountry()
            {
                return country;
            }

            public void setCountry(String country)
            {
                this.country = country;
            }

            public String getCountry_iso3166()
            {
                return country_iso3166;
            }

            public void setCountry_iso3166(String country_iso3166)
            {
                this.country_iso3166 = country_iso3166;
            }

            public double getLatitude()
            {
                return latitude;
            }

            public void setLatitude(double latitude)
            {
                this.latitude = latitude;
            }

            public double getLongitude()
            {
                return longitude;
            }

            public void setLongitude(double longitude)
            {
                this.longitude = longitude;
            }

            public String getElevation()
            {
                return elevation;
            }

            public void setElevation(String elevation)
            {
                this.elevation = elevation;
            }

        }// end of method ObservationLocation

        public class Estimated
        {

        }// end of method Estimated

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public DisplayLocation getDisplay_location() {
            return display_location;
        }

        public void setDisplay_location(DisplayLocation display_location) {
            this.display_location = display_location;
        }

        public ObservationLocation getObservation_location() {
            return observation_location;
        }

        public void setObservation_location(ObservationLocation observation_location) {
            this.observation_location = observation_location;
        }

        public Estimated getEstimated() {
            return estimated;
        }

        public void setEstimated(Estimated estimated) {
            this.estimated = estimated;
        }

        public String getStation_id()
        {
            return station_id;
        }

        public void setStation_id(String station_id)
        {
            this.station_id = station_id;
        }

        public String getObservation_time()
        {
            return observation_time;
        }

        public void setObservation_time(String observation_time)
        {
            this.observation_time = observation_time;
        }

        public String getObservation_time_rfc822()
        {
            return observation_time_rfc822;
        }

        public void setObservation_time_rfc822(String observation_time_rfc822)
        {
            this.observation_time_rfc822 = observation_time_rfc822;
        }

        public String getObservation_epoch()
        {
            return observation_epoch;
        }

        public void setObservation_epoch(String observation_epoch)
        {
            this.observation_epoch = observation_epoch;
        }

        public String getLocal_time_rfc822()
        {
            return local_time_rfc822;
        }

        public void setLocal_time_rfc822(String local_time_rfc822)
        {
            this.local_time_rfc822 = local_time_rfc822;
        }

        public String getLocal_epoch()
        {
            return local_epoch;
        }

        public void setLocal_epoch(String local_epoch)
        {
            this.local_epoch = local_epoch;
        }

        public String getLocal_tz_short()
        {
            return local_tz_short;
        }

        public void setLocal_tz_short(String local_tz_short)
        {
            this.local_tz_short = local_tz_short;
        }

        public String getLocal_tz_long()
        {
            return local_tz_long;
        }

        public void setLocal_tz_long(String local_tz_long)
        {
            this.local_tz_long = local_tz_long;
        }

        public String getLocal_tz_offset()
        {
            return local_tz_offset;
        }

        public void setLocal_tz_offset(String local_tz_offset)
        {
            this.local_tz_offset = local_tz_offset;
        }

        public String getWeather()
        {
            return weather;
        }

        public void setWeather(String weather)
        {
            this.weather = weather;
        }

        public String getTemperature_String()
        {
            return temperature_String;
        }

        public void setTemperature_String(String temperature_String)
        {
            this.temperature_String = temperature_String;
        }

        public String getTemp_f()
        {
            return temp_f;
        }

        public void setTemp_f(String temp_f)
        {
            this.temp_f = temp_f;
        }

        public String getTemp_c()
        {
            return temp_c;
        }

        public void setTemp_c(String temp_c)
        {
            this.temp_c = temp_c;
        }

        public String getRelative_humidity()
        {
            return relative_humidity;
        }

        public void setRelative_humidity(String relative_humidity)
        {
            this.relative_humidity = relative_humidity;
        }

        public String getWind_String()
        {
            return wind_String;
        }

        public void setWind_String(String wind_String)
        {
            this.wind_String = wind_String;
        }

        public String getWind_dir()
        {
            return wind_dir;
        }

        public void setWind_dir(String wind_dir)
        {
            this.wind_dir = wind_dir;
        }

        public String getWind_degrees()
        {
            return wind_degrees;
        }

        public void setWind_degrees(String wind_degrees)
        {
            this.wind_degrees = wind_degrees;
        }

        public float getWind_mph()
        {
            return wind_mph;
        }

        public void setWind_mph(float wind_mph)
        {
            this.wind_mph = wind_mph;
        }

        public float getWind_gust_mph()
        {
            return wind_gust_mph;
        }

        public void setWind_gust_mph(float wind_gust_mph)
        {
            this.wind_gust_mph = wind_gust_mph;
        }

        public float getWind_kph()
        {
            return wind_kph;
        }

        public void setWind_kph(float wind_kph)
        {
            this.wind_kph = wind_kph;
        }

        public float getWind_gust_kph()
        {
            return wind_gust_kph;
        }

        public void setWind_gust_kph(float wind_gust_kph)
        {
            this.wind_gust_kph = wind_gust_kph;
        }

        public float getPressure_mb()
        {
            return pressure_mb;
        }

        public void setPressure_mb(int pressure_mb)
        {
            this.pressure_mb = pressure_mb;
        }

        public float getPressure_in()
        {
            return pressure_in;
        }

        public void setPressure_in(float pressure_in)
        {
            this.pressure_in = pressure_in;
        }

        public String getPressure_trend()
        {
            return pressure_trend;
        }

        public void setPressure_trend(String pressure_trend)
        {
            this.pressure_trend = pressure_trend;
        }

        public String getDewpoint_String()
        {
            return dewpoint_String;
        }

        public void setDewpoint_String(String dewpoint_String)
        {
            this.dewpoint_String = dewpoint_String;
        }

        public float getDewpoint_f()
        {
            return dewpoint_f;
        }

        public void setDewpoint_f(int dewpoint_f)
        {
            this.dewpoint_f = dewpoint_f;
        }

        public float getDewpoint_c()
        {
            return dewpoint_c;
        }

        public void setDewpoint_c(int dewpoint_c)
        {
            this.dewpoint_c = dewpoint_c;
        }

        public String getHeat_index_String()
        {
            return heat_index_String;
        }

        public void setHeat_index_String(String heat_index_String)
        {
            this.heat_index_String = heat_index_String;
        }

        public String getHeat_index_f()
        {
            return heat_index_f;
        }

        public void setHeat_index_f(String heat_index_f)
        {
            this.heat_index_f = heat_index_f;
        }

        public String getHeat_index_c()
        {
            return heat_index_c;
        }

        public void setHeat_index_c(String heat_index_c)
        {
            this.heat_index_c = heat_index_c;
        }

        public String getWindchill_String()
        {
            return windchill_String;
        }

        public void setWindchill_String(String windchill_String)
        {
            this.windchill_String = windchill_String;
        }

        public String getWindchill_f()
        {
            return windchill_f;
        }

        public void setWindchill_f(String windchill_f)
        {
            this.windchill_f = windchill_f;
        }

        public String getWindchill_c()
        {
            return windchill_c;
        }

        public void setWindchill_c(String windchill_c)
        {
            this.windchill_c = windchill_c;
        }

        public String getFeelslike_String()
        {
            return feelslike_String;
        }

        public void setFeelslike_String(String feelslike_String)
        {
            this.feelslike_String = feelslike_String;
        }

        public float getFeelslike_f()
        {
            return feelslike_f;
        }

        public void setFeelslike_f(float feelslike_f)
        {
            this.feelslike_f = feelslike_f;
        }

        public float getFeelslike_c()
        {
            return feelslike_c;
        }

        public void setFeelslike_c(float feelslike_c)
        {
            this.feelslike_c = feelslike_c;
        }

        public String getVisibility_mi()
        {
            return visibility_mi;
        }

        public void setVisibility_mi(String visibility_mi)
        {
            this.visibility_mi = visibility_mi;
        }

        public String getVisibility_km()
        {
            return visibility_km;
        }

        public void setVisibility_km(String visibility_km)
        {
            this.visibility_km = visibility_km;
        }

        public String getSolarradiation()
        {
            return solarradiation;
        }

        public void setSolarradiation(String solarradiation)
        {
            this.solarradiation = solarradiation;
        }

        public String getUV()
        {
            return UV;
        }

        public void setUV(String UV)
        {
            this.UV = UV;
        }

        public String getPrecip_1hr_in()
        {
            return precip_1hr_in;
        }

        public void setPrecip_1hr_in(String precip_1hr_in)
        {
            this.precip_1hr_in = precip_1hr_in;
        }

        public String getPrecip_1hr_metric()
        {
            return precip_1hr_metric;
        }

        public void setPrecip_1hr_metric(String precip_1hr_metric)
        {
            this.precip_1hr_metric = precip_1hr_metric;
        }

        public String getPrecip_today_String()
        {
            return precip_today_String;
        }

        public void setPrecip_today_String(String precip_today_String)
        {
            this.precip_today_String = precip_today_String;
        }

        public String getPrecip_today_in()
        {
            return precip_today_in;
        }

        public void setPrecip_today_in(String precip_today_in)
        {
            this.precip_today_in = precip_today_in;
        }

        public String getPrecip_today_metric()
        {
            return precip_today_metric;
        }

        public void setPrecip_today_metric(String precip_today_metric)
        {
            this.precip_today_metric = precip_today_metric;
        }

        public String getIcon()
        {
            return icon;
        }

        public void setIcon(String icon)
        {
            this.icon = icon;
        }

        public String getIcon_url()
        {
            return icon_url;
        }

        public void setIcon_url(String icon_url)
        {
            this.icon_url = icon_url;
        }

        public String getForecast_url()
        {
            return forecast_url;
        }

        public void setForecast_url(String forecast_url)
        {
            this.forecast_url = forecast_url;
        }

        public String getHistory_url()
        {
            return history_url;
        }

        public void setHistory_url(String history_url)
        {
            this.history_url = history_url;
        }

        public String getOb_url()
        {
            return ob_url;
        }

        public void setOb_url(String ob_url)
        {
            this.ob_url = ob_url;
        }

        public String getNowcast()
        {
            return nowcast;
        }

        public void setNowcast(String nowcast)
        {
            this.nowcast = nowcast;
        }
    }// end of class CurrentObservation

    public class Forecast
    {
        private TxtForecast txt_forecast;
        private SimpleForecast simpleforecast;

        public TxtForecast getTxt_forecast() {
            return txt_forecast;
        }

        public void setTxt_forecast(TxtForecast txt_forecast)
        {
            this.txt_forecast = txt_forecast;
        }

        public SimpleForecast getSimpleforecast()
        {
            return simpleforecast;
        }

        public void setSimpleforecast(SimpleForecast simpleforecast)
        {
            this.simpleforecast = simpleforecast;
        }

        public class TxtForecast
        {
            private String date;
            private List<ForeCastDay> forecastday;

            public String getDate()
            {
                return date;
            }

            public void setDate(String date)
            {
                this.date = date;
            }

            public List<ForeCastDay> getForecastday()
            {
                return forecastday;
            }

            public void setForecastday(List<ForeCastDay> forecastday)
            {
                this.forecastday = forecastday;
            }

            public class ForeCastDay
            {
                private int period;
                private String icon;
                private String icon_url;
                private String title;
                private String fcttext;
                private String fcttext_metric;
                private int pop;

                public int getPeriod()
                {
                    return period;
                }

                public void setPeriod(int period)
                {
                    this.period = period;
                }

                public String getIcon()
                {
                    return icon;
                }

                public void setIcon(String icon)
                {
                    this.icon = icon;
                }

                public String getIcon_url()
                {
                    return icon_url;
                }

                public void setIcon_url(String icon_url)
                {
                    this.icon_url = icon_url;
                }

                public String getTitle()
                {
                    return title;
                }

                public void setTitle(String title)
                {
                    this.title = title;
                }

                public String getFcttext()
                {
                    return fcttext;
                }

                public void setFcttext(String fcttext)
                {
                    this.fcttext = fcttext;
                }

                public String getFcttext_metric()
                {
                    return fcttext_metric;
                }

                public void setFcttext_metric(String fcttext_metric)
                {
                    this.fcttext_metric = fcttext_metric;
                }

                public int getPop()
                {
                    return pop;
                }

                public void setPop(int pop)
                {
                    this.pop = pop;
                }
            }// end of class ForecastDay
        }// end of class TxtForecast

        public class SimpleForecast
        {
            private List<ForecastDay> forecastday;

            public List<ForecastDay> getForecastday()
            {
                return forecastday;
            }

            public void setForecastday(List<ForecastDay> forecastday)
            {
                this.forecastday = forecastday;
            }

            public class ForecastDay
            {
                private ForecastDate date;
                private High high;
                private Low low;

                public class ForecastDate
                {
                    private String epoch;
                    private String pretty;
                    private int day;
                    private int month;
                    private int year;
                    private int yday;
                    private int hour;
                    private int min;
                    private int sec;
                    private int isdst;
                    private String monthname;
                    private String monthname_short;
                    private String weekday_short;
                    private String weekday;
                    private String ampm;
                    private String tz_short;
                    private String tz_long;

                    public String getEpoch()
                    {
                        return epoch;
                    }

                    public void setEpoch(String epoch)
                    {
                        this.epoch = epoch;
                    }

                    public String getPretty()
                    {
                        return pretty;
                    }

                    public void setPretty(String pretty)
                    {
                        this.pretty = pretty;
                    }

                    public int getDay()
                    {
                        return day;
                    }

                    public void setDay(int day)
                    {
                        this.day = day;
                    }

                    public int getMonth()
                    {
                        return month;
                    }

                    public void setMonth(int month)
                    {
                        this.month = month;
                    }

                    public int getYear()
                    {
                        return year;
                    }

                    public void setYear(int year)
                    {
                        this.year = year;
                    }

                    public int getYday()
                    {
                        return yday;
                    }

                    public void setYday(int yday)
                    {
                        this.yday = yday;
                    }

                    public int getHour()
                    {
                        return hour;
                    }

                    public void setHour(int hour)
                    {
                        this.hour = hour;
                    }

                    public int getMin()
                    {
                        return min;
                    }

                    public void setMin(int min)
                    {
                        this.min = min;
                    }

                    public int getSec()
                    {
                        return sec;
                    }

                    public void setSec(int sec)
                    {
                        this.sec = sec;
                    }

                    public int getIsdst()
                    {
                        return isdst;
                    }

                    public void setIsdst(int isdst)
                    {
                        this.isdst = isdst;
                    }

                    public String getMonthname()
                    {
                        return monthname;
                    }

                    public void setMonthname(String monthname)
                    {
                        this.monthname = monthname;
                    }

                    public String getMonthname_short()
                    {
                        return monthname_short;
                    }

                    public void setMonthname_short(String monthname_short)
                    {
                        this.monthname_short = monthname_short;
                    }

                    public String getWeekday_short()
                    {
                        return weekday_short;
                    }

                    public void setWeekday_short(String weekday_short)
                    {
                        this.weekday_short = weekday_short;
                    }

                    public String getWeekday()
                    {
                        return weekday;
                    }

                    public void setWeekday(String weekday)
                    {
                        this.weekday = weekday;
                    }

                    public String getAmpm()
                    {
                        return ampm;
                    }

                    public void setAmpm(String ampm)
                    {
                        this.ampm = ampm;
                    }

                    public String getTz_short()
                    {
                        return tz_short;
                    }

                    public void setTz_short(String tz_short)
                    {
                        this.tz_short = tz_short;
                    }

                    public String getTz_long()
                    {
                        return tz_long;
                    }

                    public void setTz_long(String tz_long)
                    {
                        this.tz_long = tz_long;
                    }
                }// end of class ForcastDate

                private int period;

                public int getPeriod()
                {
                    return period;
                }

                public void setPeriod(int period)
                {
                    this.period = period;
                }


                public class High
                {
                    private int fahrenheit;
                    private int celsius;

                    public int getFahrenheit()
                    {
                        return fahrenheit;
                    }

                    public void setFahrenheit(int fahrenheit)
                    {
                        this.fahrenheit = fahrenheit;
                    }

                    public int getCelsius()
                    {
                        return celsius;
                    }

                    public void setCelsius(int celsius)
                    {
                        this.celsius = celsius;
                    }
                }// end of class High

                public class Low
                {
                    private int fahrenheit;
                    private int celsius;

                    public int getFahrenheit() {
                        return fahrenheit;
                    }

                    public void setFahrenheit(int fahrenheit)
                    {
                        this.fahrenheit = fahrenheit;
                    }

                    public int getCelsius()
                    {
                        return celsius;
                    }

                    public void setCelsius(int celsius)
                    {
                        this.celsius = celsius;
                    }
                }// end of class Low

                private String conditions;
                private String icon;
                private String icon_url;
                private String skyicon;
                private int pop;
                private QpfAllDay qpf_allday;
                private QpfDay qpf_day;
                private QpfNight qpf_night;
                private SnowAllDay snow_allday;
                private SnowDay snow_day;
                private SnowNight snow_night;
                private MaxWind maxwind;
                private AveWind avewind;
                private int avehumidity;
                private int maxhumidity;
                private int minhumidity;

                public ForecastDate getDate()
                {
                    return date;
                }

                public void setDate(ForecastDate date)
                {
                    this.date = date;
                }

                public High getHigh()
                {
                    return high;
                }

                public void setHigh(High high)
                {
                    this.high = high;
                }

                public Low getLow()
                {
                    return low;
                }

                public void setLow(Low low)
                {
                    this.low = low;
                }

                public String getConditions()
                {
                    return conditions;
                }

                public void setConditions(String conditions)
                {
                    this.conditions = conditions;
                }

                public String getIcon()
                {
                    return icon;
                }

                public void setIcon(String icon)
                {
                    this.icon = icon;
                }

                public String getIcon_url()
                {
                    return icon_url;
                }

                public void setIcon_url(String icon_url)
                {
                    this.icon_url = icon_url;
                }

                public String getSkyicon()
                {
                    return skyicon;
                }

                public void setSkyicon(String skyicon)
                {
                    this.skyicon = skyicon;
                }

                public int getPop()
                {
                    return pop;
                }

                public void setPop(int pop)
                {
                    this.pop = pop;
                }

                public QpfAllDay getQpf_allday()
                {
                    return qpf_allday;
                }

                public void setQpf_allday(QpfAllDay qpf_allday)
                {
                    this.qpf_allday = qpf_allday;
                }

                public QpfDay getQpf_day()
                {
                    return qpf_day;
                }

                public void setQpf_day(QpfDay qpf_day)
                {
                    this.qpf_day = qpf_day;
                }

                public QpfNight getQpf_night()
                {
                    return qpf_night;
                }

                public void setQpf_night(QpfNight qpf_night)
                {
                    this.qpf_night = qpf_night;
                }

                public SnowAllDay getSnow_allday()
                {
                    return snow_allday;
                }

                public void setSnow_allday(SnowAllDay snow_allday)
                {
                    this.snow_allday = snow_allday;
                }

                public SnowDay getSnow_day()
                {
                    return snow_day;
                }

                public void setSnow_day(SnowDay snow_day)
                {
                    this.snow_day = snow_day;
                }

                public SnowNight getSnow_night()
                {
                    return snow_night;
                }

                public void setSnow_night(SnowNight snow_night)
                {
                    this.snow_night = snow_night;
                }

                public MaxWind getMaxwind()
                {
                    return maxwind;
                }

                public void setMaxwind(MaxWind maxwind)
                {
                    this.maxwind = maxwind;
                }

                public AveWind getAvewind()
                {
                    return avewind;
                }

                public void setAvewind(AveWind avewind)
                {
                    this.avewind = avewind;
                }

                public int getAvehumidity()
                {
                    return avehumidity;
                }

                public void setAvehumidity(int avehumidity)
                {
                    this.avehumidity = avehumidity;
                }

                public int getMaxhumidity()
                {
                    return maxhumidity;
                }

                public void setMaxhumidity(int maxhumidity)
                {
                    this.maxhumidity = maxhumidity;
                }

                public int getMinhumidity()
                {
                    return minhumidity;
                }

                public void setMinhumidity(int minhumidity)
                {
                    this.minhumidity = minhumidity;
                }

                public class QpfAllDay
                {
                    private String inch;
                    private String mm;

                    public String getInch()
                    {
                        return inch;
                    }

                    public void setInch(String inch)
                    {
                        this.inch = inch;
                    }

                    public String getMm()
                    {
                        return mm;
                    }

                    public void setMm(String mm)
                    {
                        this.mm = mm;
                    }
                }// end of class QpfAllDay

                public class QpfDay
                {
                    private String inch;
                    private String mm;

                    public String getInch()
                    {
                        return inch;
                    }

                    public void setInch(String inch)
                    {
                        this.inch = inch;
                    }

                    public String getMm()
                    {
                        return mm;
                    }

                    public void setMm(String mm)
                    {
                        this.mm = mm;
                    }
                }// end of class QpfDay

                public class QpfNight
                {
                    private String inch;
                    private String mm;

                    public String getInch()
                    {
                        return inch;
                    }

                    public void setInch(String inch)
                    {
                        this.inch = inch;
                    }

                    public String getMm()
                    {
                        return mm;
                    }

                    public void setMm(String mm)
                    {
                        this.mm = mm;
                    }
                }// end of class QpfNight

                public class SnowAllDay
                {
                    private String inch;
                    private String cm;

                    public String getInch()
                    {
                        return inch;
                    }

                    public void setInch(String inch)
                    {
                        this.inch = inch;
                    }

                    public String getCm()
                    {
                        return cm;
                    }

                    public void setCm(String cm)
                    {
                        this.cm = cm;
                    }
                }// end of class SnowAllDay

                public class SnowDay
                {
                    private String inch;
                    private String cm;

                    public String getInch()
                    {
                        return inch;
                    }

                    public void setInch(String inch)
                    {
                        this.inch = inch;
                    }

                    public String getCm()
                    {
                        return cm;
                    }

                    public void setCm(String cm)
                    {
                        this.cm = cm;
                    }
                }// end of class SnowDay

                public class SnowNight
                {
                    private String inch;
                    private String cm;

                    public String getInch()
                    {
                        return inch;
                    }

                    public void setInch(String inch)
                    {
                        this.inch = inch;
                    }

                    public String getCm()
                    {
                        return cm;
                    }

                    public void setCm(String cm)
                    {
                        this.cm = cm;
                    }
                }// end of class SnowNight

                public class MaxWind
                {
                    private int mph;
                    private int kph;
                    private String dir;
                    private int degrees;

                    public int getMph()
                    {
                        return mph;
                    }

                    public void setMph(int mph)
                    {
                        this.mph = mph;
                    }

                    public int getKph()
                    {
                        return kph;
                    }

                    public void setKph(int kph)
                    {
                        this.kph = kph;
                    }

                    public String getDir()
                    {
                        return dir;
                    }

                    public void setDir(String dir)
                    {
                        this.dir = dir;
                    }

                    public int getDegrees()
                    {
                        return degrees;
                    }

                    public void setDegrees(int degrees)
                    {
                        this.degrees = degrees;
                    }
                }// end of class MaxWind

                public class AveWind
                {
                    private int mph;
                    private int kph;
                    private String dir;
                    private int degrees;

                    public int getMph()
                    {
                        return mph;
                    }

                    public void setMph(int mph)
                    {
                        this.mph = mph;
                    }

                    public int getKph()
                    {
                        return kph;
                    }

                    public void setKph(int kph)
                    {
                        this.kph = kph;
                    }

                    public String getDir()
                    {
                        return dir;
                    }

                    public void setDir(String dir)
                    {
                        this.dir = dir;
                    }

                    public int getDegrees()
                    {
                        return degrees;
                    }

                    public void setDegrees(int degrees)
                    {
                        this.degrees = degrees;
                    }
                }// end of class AveWind
            }// end of method ForecastDay
        }// end of class SimpleForecast                          
    }// end of class Forecast

    public class SunPhase
    {
        private SunRise sunrise;
        private SunSet sunset;

        public SunRise getSunrise()
        {
            return sunrise;
        }

        public void setSunrise(SunRise sunrise)
        {
            this.sunrise = sunrise;
        }

        public SunSet getSunset()
        {
            return sunset;
        }

        public void setSunset(SunSet sunset)
        {
            this.sunset = sunset;
        }

        public class SunRise
        {
            private int hour;
            private int minute;

            public int getHour()
            {
                return hour;
            }

            public void setHour(int hour)
            {
                this.hour = hour;
            }

            public int getMinute()
            {
                return minute;
            }

            public void setMinute(int minute)
            {
                this.minute = minute;
            }
        }// end of class SunRise

        public class SunSet
        {
            private int hour;
            private int minute;

            public int getHour()
            {
                return hour;
            }

            public void setHour(int hour)
            {
                this.hour = hour;
            }

            public int getMinute()
            {
                return minute;
            }

            public void setMinute(int minute)
            {
                this.minute = minute;
            }
        }// end of class SunSet
    }// end of class SunPhase
}// end of class WeatherUndergroundDataItem