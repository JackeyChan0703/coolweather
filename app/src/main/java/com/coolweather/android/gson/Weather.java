package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Weather {

    public String status;
    public Basic basic;
    public Now now;
    public Update update;

    @SerializedName("lifestyle")
    public List<LifeStyle>  lifeStyleList;

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

    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public List<LifeStyle> getLifeStyleList() {
        return lifeStyleList;
    }

    public void setLifeStyleList(List<LifeStyle> lifeStyleList) {
        this.lifeStyleList = lifeStyleList;
    }

    public List<Forecast> getForecastList() {
        return forecastList;
    }

    public void setForecastList(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    @Override
    public String toString() {
        return "{\"HeWeather6\":[{" +
                "\"status\":" + "\""+status + "\"," +
                "\"basic\":" + "\""+basic +"\","+
                "\"now\":" + "\""+now +"\","+
                "\"update\":" + "\""+update +"\","+
                "\"lifestyle\":" + lifeStyleList+"," +
                "\"daily_forecast\":" + forecastList +
                "}]}";
    }
}
