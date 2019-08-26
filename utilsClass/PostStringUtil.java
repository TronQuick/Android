package com.giri.fridge.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostStringUtil {
    /**
     * 基于 OkHttp 的 postString 方法
     */
    public static void postString(String RequestContent, String url) {
        // 获取okHttp实体
        OkHttpClient client = new OkHttpClient();

        // RequestBody中的MediaType指定为纯文本，编码方式是utf-8
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"),
                RequestContent);

        // 发送请求
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 处理响应
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("POST", "POST request fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                Log.d("POST", "responseStr：" + responseStr);
            }
        });
    }
}
