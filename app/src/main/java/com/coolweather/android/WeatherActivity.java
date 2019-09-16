package com.coolweather.android;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Basic;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.LifeStyle;
import com.coolweather.android.gson.Now;
import com.coolweather.android.gson.Update;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.gson.WeatherForecast;
import com.coolweather.android.gson.WeatherLifeStyle;
import com.coolweather.android.gson.WeatherNow;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView humText;
    private TextView pcpnText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.froecast_layout);
        humText = findViewById(R.id.hum_text);
        pcpnText = findViewById(R.id.pcpn_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener((view -> drawerLayout.openDrawer(GravityCompat.START)));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pref.getString("weather", null);
        String bingPic = pref.getString("bing_pic",null);
        if (bingPic!=null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
        if (weatherString != null) {
            //当前有缓存，直接解析
            Weather weather = Utility.handleWeatherReponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //当前没有缓存，去服务器查询
            String weatherId = getIntent().getStringExtra("weatherId");
            weatherLayout.setVisibility(View.INVISIBLE);
            try {
                requestWeather(weatherId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //刷新
        swipeRefresh.setOnRefreshListener(()->{
            try {
                requestWeather(mWeatherId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 显示天气信息
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime;
        String degree = weather.now.Temperature + "℃";
        String weatherInfo = weather.now.cond;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.froecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.cond);
            maxText.setText(forecast.maxTemperature);
            minText.setText(forecast.minTemperature);
            forecastLayout.addView(view);
        }
        if (weather.now.hum != null && weather.now.pcpn != null) {
            humText.setText(weather.now.hum);
            pcpnText.setText(weather.now.pcpn);
            for (LifeStyle lifeStyle : weather.lifeStyleList) {
                String type = lifeStyle.getLifeType();
                if (type.equals("comf")) {
                    String comfort = "舒适度：" + lifeStyle.lifeLevel + "," + lifeStyle.lifeText;
                    comfortText.setText(comfort);
                } else if (type .equals( "cw")) {
                    String carWash = "洗车指数：" + lifeStyle.lifeLevel + "," + lifeStyle.lifeText;
                    carWashText.setText(carWash);
                } else if (type .equals("sport")) {
                    String sport = "运动建议：" + lifeStyle.lifeLevel + "," + lifeStyle.lifeText;
                    sportText.setText(sport);
                }
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 根据天气id请求城市的天气信息
     *
     * @param weatherId 城市或地区id
     */
    public void requestWeather(final String weatherId) throws IOException {
        Weather weather = new Weather();
        String weatherNowUrl = "https://free-api.heweather.net/s6/weather/now?location=" + weatherId + "&key=c5fefbe84ab54b939bbc4fbec75d0c24";
        String weatherForecastUrl = "https://free-api.heweather.net/s6/weather/forecast?location=" + weatherId + "&key=c5fefbe84ab54b939bbc4fbec75d0c24";
        String weatherLifestyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?location=" + weatherId + "&key=c5fefbe84ab54b939bbc4fbec75d0c24";


        new Thread(()->{
            try {
                String responseText = HttpUtil.sendHttpRequest(weatherNowUrl).body().string();
                WeatherNow weatherNow = Utility.handleWeatherNowReponse(responseText);
                if (weatherNow != null && weatherNow.status.equals("ok")) {
                    weather.setBasic(weatherNow.basic);
                    weather.setUpdate(weatherNow.update);
                    weather.setStatus(weatherNow.status);
                    weather.setNow(weatherNow.now);
                } else {
                    Toast.makeText(WeatherActivity.this, "获取天气现在信息失败", Toast.LENGTH_SHORT).show();
                }
                String response = HttpUtil.sendHttpRequest(weatherForecastUrl).body().string();
                WeatherForecast weatherForecast = Utility.handleWeatherForecastReponse(response);
                if (weatherForecast != null && weatherForecast.status.equals("ok")) {
                    if (weatherForecast != null && weatherForecast.status.equals("ok")) {
                        weather.setForecastList(weatherForecast.forecastList);
                        mWeatherId = weather.basic.weatherId;
                    }
                } else {
                    Toast.makeText(WeatherActivity.this, "获取天气现在信息失败", Toast.LENGTH_SHORT).show();
                }

                String string = HttpUtil.sendHttpRequest(weatherLifestyleUrl).body().string();
                WeatherLifeStyle weatherLifeStyle = Utility.handleWeatherLifeStyleReponse(string);
                if (weatherLifeStyle != null && weatherLifeStyle.status.equals("ok")) {
                    weather.setLifeStyleList(weatherLifeStyle.lifeStyleList);
                } else {
                    Toast.makeText(WeatherActivity.this, "获取天气现在信息失败", Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(()->{
                    showWeatherInfo(weather);
                    swipeRefresh.setRefreshing(false);
                } );
                loadBingPic();

                //最终的数据
                /*final String weatherToString = weather.toString();
                runOnUiThread(() -> {
                    if (weather != null && weather.status.equals("ok")) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", weatherToString);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(()->{
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                });
            }
        });
    }
}


   /*     HttpUtil.sendOkHttpRequest(weatherForecastUrl, new Callback() {
@Override
public void onResponse(Call call, Response response) throws IOException {
final String responseText = response.body().string();
        WeatherForecast weatherForecast = Utility.handleWeatherForecastReponse(responseText);
        runOnUiThread(() -> {
        if (weatherForecast != null && weatherForecast.status.equals("ok")) {
        if (weatherForecast != null && weatherForecast.status.equals("ok")) {
        weather.setForecastList(weatherForecast.forecastList);
        }
        } else {
        Toast.makeText(WeatherActivity.this, "获取天气现在信息失败", Toast.LENGTH_SHORT).show();
        }
        });
        }

@Override
public void onFailure(Call call, IOException e) {
        runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "获取天气预报信息失败", Toast.LENGTH_SHORT).show());
        }
        });
        HttpUtil.sendOkHttpRequest(weatherLifestyleUrl, new Callback() {
@Override
public void onResponse(Call call, Response response) throws IOException {
final String responseText = response.body().string();
        WeatherLifeStyle weatherLifeStyle = Utility.handleWeatherLifeStyleReponse(responseText);
        runOnUiThread(() -> {
        if (weatherLifeStyle != null && weatherLifeStyle.status.equals("ok")) {
        weather.setLifeStyleList(weatherLifeStyle.lifeStyleList);
        } else {
        Toast.makeText(WeatherActivity.this, "获取天气现在信息失败", Toast.LENGTH_SHORT).show();
        }
        });
        }

@Override
public void onFailure(Call call, IOException e) {
        runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "获取天气生活指数信息失败", Toast.LENGTH_SHORT).show());
        }
        });*/