package com.giri.fridgedemo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 拍照上传
    public void startCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    // 定位并上传
    public void startLocation(View view) {
        Intent intent = new Intent(this, gpsActivity.class);
        startActivity(intent);
    }

    // IO采集上传
    public void startIO(View view) {
        Intent intent = new Intent(this, ioActivity.class);
        startActivity(intent);
    }

    // 温度采集上传
    public void startTemperature(View view) {

    }
}
