package com.coolweather.android.util;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public static Response sendHttpRequest(String address) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
