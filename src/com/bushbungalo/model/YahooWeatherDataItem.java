package com.bushbungalo.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * @author Paul O. Patterson
 * <br />
 * <b style="margin-left:-40px">Date Created:</b>
 * <br />
 * 11/16/17
 */
@Deprecated
public class YahooWeatherDataItem
{
    private static YahooWeatherDataItem yahooWeatherData;

    private Query query;

    public Query getQuery()
    {
        return query;
    }

    public void setQuery(Query query)
    {
        this.query = query;
    }

    public class Query
    {
        private String count;
        private String created;
        private Results results;

        public String getCount()
        {
            return count;
        }

        public void setCount(String count)
        {
            this.count = count;
        }

        public String getCreated()
        {
            return created;
        }

        public void setCreated(String created)
        {
            this.created = created;
        }

        public Results getResults()
        {
            return results;
        }

        public void setResults(Results results)
        {
            this.results = results;
        }


        public class Results
        {
            private Channel channel;

            public Channel getChannel()
            {
                return channel;
            }

            public void setChannel(Channel channel)
            {
                this.channel = channel;
            }

            public class Channel
            {
                private Units units;
                private Location location;
                private Wind wind;

                public Units getUnits()
                {
                    return units;
                }

                public void setUnits(Units units)
                {
                    this.units = units;
                }

                public Location getLocation()
                {
                    return location;
                }

                public void setLocation(Location location)
                {
                    this.location = location;
                }

                public Wind getWind() {
                    return wind;
                }

                public void setWind(Wind wind)
                {
                    this.wind = wind;
                }

                private Atmosphere atmosphere;
                private Astronomy astronomy;
                private Item item;

                public Atmosphere getAtmosphere()
                {
                    return atmosphere;
                }

                public void setAtmosphere(Atmosphere atmosphere)
                {
                    this.atmosphere = atmosphere;
                }

                public Astronomy getAstronomy()
                {
                    return astronomy;
                }

                public void setAstronomy(Astronomy astronomy)
                {
                    this.astronomy = astronomy;
                }

                public Item getItem()
                {
                    return item;
                }

                public void setItem(Item item)
                {
                    this.item = item;
                }


                public class Units
                {
                    private String distance;
                    private String pressure;
                    private String speed;
                    private String temperature;

                    public String getDistance()
                    {
                        return distance;
                    }

                    public void setDistance(String distance)
                    {
                        this.distance = distance;
                    }

                    public String getPressure()
                    {
                        return pressure;
                    }

                    public void setPressure(String pressure)
                    {
                        this.pressure = pressure;
                    }

                    public String getSpeed()
                    {
                        return speed;
                    }

                    public void setSpeed(String speed)
                    {
                        this.speed = speed;
                    }

                    public String getTemperature()
                    {
                        return temperature;
                    }

                    public void setTemperature(String temperature)
                    {
                        this.temperature = temperature;
                    }
                }// end of class Units

                public class Location
                {
                    private String city;
                    private String country;
                    private String region;

                    public String getCity()
                    {
                        return city;
                    }

                    public void setCity(String city)
                    {
                        this.city = city;
                    }

                    public String getCountry()
                    {
                        return country;
                    }

                    public void setCountry(String country)
                    {
                        this.country = country;
                    }

                    public String getRegion()
                    {
                        return region;
                    }

                    public void setRegion(String region)
                    {
                        this.region = region;
                    }

                }// end of class Location

                public class Wind
                {
                    private String chill;
                    private String direction;
                    private String speed;

                    public String getChill()
                    {
                        return chill;
                    }

                    public void setChill(String chill)
                    {
                        this.chill = chill;
                    }

                    public String getDirection()
                    {
                        return direction;
                    }

                    public void setDirection(String direction)
                    {
                        this.direction = direction;
                    }

                    public String getSpeed()
                    {
                        return speed;
                    }

                    public void setSpeed(String speed)
                    {
                        this.speed = speed;
                    }

                }// end of class Wind

                public class Atmosphere
                {
                    private String humidity;
                    private String pressure;
                    private String rising;
                    private String visibility;

                    public String getHumidity()
                    {
                        return humidity;
                    }

                    public void setHumidity(String humidity)
                    {
                        this.humidity = humidity;
                    }

                    public String getPressure()
                    {
                        return pressure;
                    }

                    public void setPressure(String pressure)
                    {
                        this.pressure = pressure;
                    }

                    public String getRising()
                    {
                        return rising;
                    }

                    public void setRising(String rising)
                    {
                        this.rising = rising;
                    }

                    public String getVisibility()
                    {
                        return visibility;
                    }

                    public void setVisibility(String visibility)
                    {
                        this.visibility = visibility;
                    }
                }// end of class Atmosphere

                public class Astronomy
                {
                    private String sunrise;
                    private String sunset;

                    public String getSunrise()
                    {
                        return sunrise;
                    }

                    public void setSunrise(String sunrise)
                    {
                        this.sunrise = sunrise;
                    }

                    public String getSunset()
                    {
                        return sunset;
                    }

                    public void setSunset(String sunset)
                    {
                        this.sunset = sunset;
                    }
                }// end of class Astronomy

                public class Item
                {
                    private String title;
                    private String lat;
                    private String longi;
                    private String link;
                    private String pubDate;
                    private Condition condition;
                    private List<Forecast> forecast;
                    private String description;
                    private GUID guid;

                    public String getTitle()
                    {
                        return title;
                    }

                    public void setTitle(String title)
                    {
                        this.title = title;
                    }

                    public String getLat()
                    {
                        return lat;
                    }

                    public void setLat(String lat)
                    {
                        this.lat = lat;
                    }

                    public String getLongi()
                    {
                        return longi;
                    }

                    public void setLongi(String longi)
                    {
                        this.longi = longi;
                    }

                    public String getLink()
                    {
                        return link;
                    }

                    public void setLink(String link)
                    {
                        this.link = link;
                    }

                    public String getPubDate()
                    {
                        return pubDate;
                    }

                    public void setPubDate(String pubDate)
                    {
                        this.pubDate = pubDate;
                    }

                    public Condition getCondition()
                    {
                        return condition;
                    }

                    public void setCondition(Condition condition)
                    {
                        this.condition = condition;
                    }

                    public List<Forecast> getForecast()
                    {
                        return forecast;
                    }

                    public void setForecast(List<Forecast> forecast)
                    {
                        this.forecast = forecast;
                    }

                    public String getDescription()
                    {
                        return description;
                    }

                    public void setDescription(String description)
                    {
                        this.description = description;
                    }

                    public GUID getGuid()
                    {
                        return guid;
                    }

                    public void setGuid(GUID guid)
                    {
                        this.guid = guid;
                    }

                    public class Condition
                    {
                        private String code;
                        private String date;
                        private String temp;
                        private String text;

                        public String getCode()
                        {
                            return code;
                        }

                        public void setCode(String code)
                        {
                            this.code = code;
                        }

                        public String getDate()
                        {
                            return date;
                        }

                        public void setDate(String date)
                        {
                            this.date = date;
                        }

                        public String getTemp()
                        {
                            return temp;
                        }

                        public void setTemp(String temp)
                        {
                            this.temp = temp;
                        }

                        public String getText()
                        {
                            return text;
                        }

                        public void setText(String text)
                        {
                            this.text = text;
                        }


                    }// end of class Condition

                    public class Forecast
                    {
                        private String code;
                        private String date;
                        private String day;
                        private String high;
                        private String low;
                        private String text;

                        public String getCode()
                        {
                            return code;
                        }

                        public void setCode(String code)
                        {
                            this.code = code;
                        }

                        public String getDate()
                        {
                            return date;
                        }

                        public void setDate(String date)
                        {
                            this.date = date;
                        }

                        public String getDay()
                        {
                            return day;
                        }

                        public void setDay(String day)
                        {
                            this.day = day;
                        }

                        public String getHigh()
                        {
                            return high;
                        }

                        public void setHigh(String high)
                        {
                            this.high = high;
                        }

                        public String getLow()
                        {
                            return low;
                        }

                        public void setLow(String low)
                        {
                            this.low = low;
                        }

                        public String getText()
                        {
                            return text;
                        }

                        public void setText(String text)
                        {
                            this.text = text;
                        }

                    }// end of class Forecast

                    public class GUID
                    {
                        private boolean guid;

                        public boolean isGuid()
                        {
                            return guid;
                        }

                        public void setGuid(boolean guid)
                        {
                            this.guid = guid;
                        }
                    }// end of class GUID
                }// end of class Item
            }// end of class Results
        }// end of class Channel
    }// end of class Query

    public static boolean DeserializeYahooJSON( String strJSON )
    {
        Gson gson = new Gson();
        yahooWeatherData = gson.fromJson( strJSON, YahooWeatherDataItem.class );

        if (yahooWeatherData == null)
        {
            return false;
        }// end of if block
        else
        {
            return true;
        }// end of else block

    }// end of method DeserializeJSON
}// end of class YahooWeatherDataItem
