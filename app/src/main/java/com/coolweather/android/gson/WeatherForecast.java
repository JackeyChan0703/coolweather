package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecast {

    public String status;
    public Basic basic;
    public Update update;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }



    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public List<Forecast> getForecastList() {
        return forecastList;
    }

    public void setForecastList(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }
}
