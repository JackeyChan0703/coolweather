package com.coolweather.android.gson;


import com.google.gson.annotations.SerializedName;

public class LifeStyle {
    @SerializedName("type")
    public String lifeType;//生活活动类型
    @SerializedName("brf")
    public String lifeLevel;//等级
    @SerializedName("txt")
    public String lifeText;//描述

    public String getLifeType() {
        return lifeType;
    }

    public void setLifeType(String lifeType) {
        this.lifeType = lifeType;
    }

    public String getLifeLevel() {
        return lifeLevel;
    }

    public void setLifeLevel(String lifeLevel) {
        this.lifeLevel = lifeLevel;
    }

    public String getLifeText() {
        return lifeText;
    }

    public void setLifeText(String lifeText) {
        this.lifeText = lifeText;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":'" + "\""+lifeType + "\"," +
                "\"brf\":" + "\""+lifeLevel + "\"," +
                "\"txt\":" + "\""+lifeText + "\"" +
                "}";
    }
}
