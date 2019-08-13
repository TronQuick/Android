package com.giri.fridgedemo2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 *      获取限位开关的输出值，上传到服务器后台，触发拍照
 * */

public class ioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);

        // 获取限位开关的输出值


        // 上传限位开关的输出值


        // 触发拍照
        startAutoCamera();
    }

    public void startAutoCamera() {

    }
}
