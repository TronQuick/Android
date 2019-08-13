package com.giri.fridgedemo2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.internal.http.multipart.Part;

import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *      拍照并上传到服务器接口
 * */

public class CameraActivity extends AppCompatActivity {

    // 拍照功能的RequestCode
    private static int REQ_1 = 1;

    // 预览窗口
    private ImageView cameraView;

    // 生成时间戳
    public static String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        /**
         *     调用相机
         * */
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraView = findViewById(R.id.cameraView);
        startActivityForResult(captureIntent,REQ_1);
    }


    /**
     *     处理拍摄图片
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_1) {
                Boolean uploadResult =false;
                // 接收拍摄数据
                Bundle bundle = data.getExtras();
                Bitmap imageBmp = (Bitmap)bundle.get("data");

                // 图片预览
                cameraView.setImageBitmap(imageBmp);

                // 引用时间戳命名，进行本地储存，返回路径
                String imageFilePath = saveImage(generateTimeStamp(),imageBmp);

                // 传入图片路径，进行上传
                if (imageFilePath != null)
                  uploadImage(imageFilePath);


            }
        }
        else finish();
    }


    /**
     *    本地储存方法,返回文件路径
     *    路径：/sdcard/fridge/
     * */
    public String saveImage(String name, Bitmap bmp) {

        File appDir = new File(Environment.getExternalStorageDirectory(),"fridge");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    public void uploadImage(String targetUrl) {

    }


    /**
     *  返回按钮
     */
    public void backHome(View view) {
        finish();
    }
}