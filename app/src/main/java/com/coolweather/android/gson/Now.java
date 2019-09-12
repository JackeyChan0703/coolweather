package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("tmp")
    public String Temperature;//温度

    @SerializedName("cond_txt")
    public String cond; //概况

    @SerializedName("hum")
    public String hum;//相对湿度

    @SerializedName("pcpn")
    public String pcpn;//降水量

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getPcpn() {
        return pcpn;
    }

    public void setPcpn(String pcpn) {
        this.pcpn = pcpn;
    }

    @Override
    public String toString() {
        return "Now{" +
                "Temperature='" + Temperature + '\'' +
                ", cond='" + cond + '\'' +
                ", hum='" + hum + '\'' +
                ", pcpn='" + pcpn + '\'' +
                '}';
    }
}
