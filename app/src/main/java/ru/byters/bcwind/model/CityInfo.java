package ru.byters.bcwind.model;

import java.io.Serializable;

public class CityInfo implements Serializable {
    public String date;
    public String name;
    public String country;
    public String temp;
    public String windspeed;
    public String weather;
    public String id;
    public String tempMin;
    public String tempMax;
    public String sunrise;
    public String sunset;
    public String pressure;
    public String humidity;
    public ForecastInfo[] forecast;

}