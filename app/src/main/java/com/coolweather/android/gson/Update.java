package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Update {
    @SerializedName("loc")
    public String localTime;//当前时间
    @SerializedName("utc")
    public String updateTime;//更新时间

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "{" +
                "\"loc\"" + "\""+localTime + "\"" +
                ",\"utc\":" + "\""+updateTime + "\"" +
                "}";
    }
}
