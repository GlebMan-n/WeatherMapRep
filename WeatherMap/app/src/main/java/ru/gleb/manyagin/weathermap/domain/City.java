package ru.gleb.manyagin.weathermap.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.sql.Time;

/**
 * Created by gleb.manyagin on 23.04.2015.
 */
public class City
{

    //region Fields
    private int id;
    private String cityName;
    private double lat;
    private double lon;
    private String sys_country;

    //endregion

    //region Keys
    //endregion

    //region Constructors
    public City() { }

    //endregion

    //region Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return cityName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getCountry() {
        return sys_country;
    }

    //endregion

    //region Setter
    public void  setId(Integer _id) {
        id = _id;
    }

    public void setName(String _name) {
        cityName = _name;
    }

    public void  setLat(Double _lat) {
        lat = _lat;
    }

    public void setLon(Double _lon) {
        lon = _lon;
    }

    public void setCountry(String _country) {
        sys_country = _country;
    }
    //endregion

    //region Methods

    //endregion
}
