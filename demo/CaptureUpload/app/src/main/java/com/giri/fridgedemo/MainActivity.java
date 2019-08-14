package com.giri.fridgedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    // 拍照功能的RequestCode
    private static int REQ_1 = 1;
    // 相片存放路径
    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 拍照上传功能
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        // 获取相片完整路径
        imageFilePath = Environment.getExternalStorageDirectory().getPath();
        imageFilePath = imageFilePath + "/" +"temp.png";
    }


    /**
     *
     *   拍照上传功能
     * */
    // 调用相机
    public void imageCapture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 自定义相片存放路径
        Uri imageUri = Uri.fromFile(new File(imageFilePath));
        // 将系统路径更改为自定义路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,REQ_1);
    }
    // 图片上传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_1) {
                FileInputStream fileInputStream = null;
                try {
                    // 获取文件流
                    fileInputStream = new FileInputStream(imageFilePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    /**
                     *  上传功能
                     * */

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }





}
