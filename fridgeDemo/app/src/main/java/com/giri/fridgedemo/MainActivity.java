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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainActivity extends AppCompatActivity {
    // 拍照功能的RequestCode
    private static int REQ_1 = 1;
    // 相片存放路径
    private String imageFilePath;
    //文件名
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 拍照上传功能
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        // 生成相片文件名
//        imageName = generateTimeStamp()+".jpg";
        // 设置相片路径
//        imageFilePath = Environment.getExternalStorageDirectory().getPath();
//        imageFilePath = imageFilePath + "/" + imageName;
    }

    //生成时间戳
    public static String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp;
    }


    /**
     *
     *   拍照上传功能
     * */
    // 调用相机
    public void imageCapture(View view) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        自定义相片存放路径
//        Uri imageUri = Uri.fromFile(new File(imageFilePath));
//        将系统路径更改为自定义路径
//        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(captureIntent,REQ_1);
    }

    // 图片上传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_1) {
                Bundle bundle = data.getExtras();
                Bitmap imageBmp = (Bitmap)bundle.get("data");

                    /**
                     *  上传
                     * */
                    String imageFilePath = saveImage(generateTimeStamp(),imageBmp);


            }
        }
    }

    // 上传方法,返回路径
    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
