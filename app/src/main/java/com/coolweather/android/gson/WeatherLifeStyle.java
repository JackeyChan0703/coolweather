package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherLifeStyle {

    public String status;
    public Basic basic;
    public Update update;

    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;

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

    public List<LifeStyle> getLifeStyleList() {
        return lifeStyleList;
    }

    public void setLifeStyleList(List<LifeStyle> lifeStyleList) {
        this.lifeStyleList = lifeStyleList;
    }

}
