package ru.gleb.manyagin.weathermap.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gleb.manyagin on 23.04.2015.
 */
public class Weather {

    //region Fields
    private double main_tempInKelvin;
    private int main_humidity;
    private double main_temp_min;
    private double main_temp_max;
    private double main_pressure;
    private double main_pressure_sea_lvl;
    private double main_pressure_grnd_lvl;
    private double wind_speed;
    private double wind_deg;
    private double wind_gust;
    private String cloudiness;
    private int weather_id;
    private String weather_main;
    private String weather_description;
    private String weather_icon;
    private int place_id;
    //endregion

    //region Keys
    //endregion

    //region Constructors
    public Weather() { }

    //endregion

    //region Getters
    public Double getTemp() {
        return main_tempInKelvin;
    }

    public Integer getHumidity() {
        return main_humidity;
    }

    public Double getTempMin() {
        return main_temp_min;
    }

    public Double getTempMax() {
        return main_temp_max;
    }

    public Double getPressure() {
        return main_pressure * 0.75006375541921;
    }

    public Double getPressureSeaLvl() {
        return main_pressure_sea_lvl;
    }

    public Double getPressureGrndLvl() {
        return main_pressure_grnd_lvl;
    }

    public Double getWndSpeed() {
        return wind_speed;
    }

    public Double getWndDeg() {
        return wind_deg;
    }

    public Double getWndGust() {
        return wind_gust;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public Integer getWetherId() {
        return weather_id;
    }

    public String getMain() {
        return weather_main;
    }

    public String getDescription() {
        return weather_description;
    }

    public String getIcon() {
        return weather_icon;
    }

    public int getPlaceId() {return place_id;}
    //endregion

    //region Setter
    public void setTemp(Double _value) {
         main_tempInKelvin = _value;
    }

    public void setHumidity(Integer _value) {
        main_humidity = _value;
    }

    public void setTempMin(Double _value) {
         main_temp_min = _value;
    }

    public void setTempMax(Double _value) {
         main_temp_max = _value;
    }

    public void setPressure(Double  _value){
         main_pressure = _value;
    }

    public void setPressureSeaLvl(Double _value) {
         main_pressure_sea_lvl = _value;
    }

    public void setPressureGrndLvl(Double _value) {
        main_pressure_grnd_lvl = _value;
    }

    public void setWndSpeed(Double _value) {
        wind_speed = _value;
    }

    public void setWndDeg(Double _value) {
        wind_deg = _value;
    }

    public void setWndGust(Double _value) {
        wind_gust = _value;
    }

    public void setCloudiness(String _value) {
        cloudiness = _value;
    }

    public void setWeatherId(Integer _value) {
        weather_id = _value;
    }

    public void setMain(String _value) {
        weather_main = _value;
    }

    public void setDescription(String _value) {
        weather_description = _value;
    }

    public void setIcon(String _value) {weather_icon = _value; }

    public void setPlaceId(int _value) {
        place_id = _value;
    }
    //endregion

    //region Methods
    //endregion
}
