package com.coolweather.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoUpdateService extends Service {

    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingpPic();
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingpPic() {

    }

    private void updateWeather() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
