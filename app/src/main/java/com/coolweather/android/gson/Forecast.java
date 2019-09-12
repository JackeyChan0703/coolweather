package com.coolweather.android.gson;


import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;//日期
    @SerializedName("tmp_max")
    public String maxTemperature;//最大温度
    @SerializedName("tmp_min")
    public String minTemperature;//最小温度
    @SerializedName("cond_txt_d")
    public String cond;//白天天气情况

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(String maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(String minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    @Override
    public String toString() {
        return "{" +
                "\"date\":" + "\""+date + "\"," +
                "\"tmp_max\":" + "\""+maxTemperature + "\"," +
                "\"tmp_min\":" + "\""+minTemperature + "\"," +
                "\"cond_txt_d\":" + "\""+cond + "\"" +
                "}";
    }
}
