package ru.gleb.manyagin.weathermap.domain;

import java.util.ArrayList;


/**
 * Created by user on 30.04.2015.
 */
public class WeatherData {
    //region Fields
    private City city;
    //private ArrayList<Weather> weatherArrayList = new ArrayList<Weather>();
    private Weather weather;
    private Integer dt;
    private Integer zoom_lvl;
    //endregion

    //region Keys
    //endregion

    //region Constructors
    public WeatherData() {
        city = new City();
    }

    //endregion

    //region Getters
    public City getCity()
    {
        return city;
    }

    public Weather getWeather()
    {
        return weather;
    }

    public Integer getDt()
    {
        return dt;
    }

    public Integer getZoomLevel()
    {
        return zoom_lvl;
    }

    //endregion

    //region Setters
    public void setWeather(Weather _weather)
    {
        weather = _weather;
    }

    public void setCity(City _city )
    {
        city = _city;
    }

    public void setDt(Integer _dt)
    {
        dt = _dt;
    }

    public void setZoomLevel (Integer _zoom_lvl){zoom_lvl = _zoom_lvl; }
    //endregion

    //region Methods
    //endregion

}
