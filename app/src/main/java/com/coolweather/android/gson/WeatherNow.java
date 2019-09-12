package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherNow {

    public String status;
    public Basic basic;
    public Now now;
    public Update update;


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

}
