package com.giri.fridge.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostStringUtil {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static OkHttpClient client = new OkHttpClient();

    /**
     * 基于 OkHttp 的 postString 方法
     * @param url
     * @param json
     */
    public static void post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("POST","response:"+response.body().string());
        }
    }
}
