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
import android.widget.ImageView;
import android.widget.Toast;

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

    // 预览窗口
    private ImageView cameraView;

    // 生成时间戳
    public static String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp;
    }

    // ++
    // 使用时间戳命名图片
    String imgFileName = generateTimeStamp() + ".jpg";
    // ++
    // 定义储存路径
    String imgRootPath = Environment.getExternalStorageDirectory().getPath() + "/" + imgFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        /**
         *     调用相机
         * */
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        // ++
        Uri imgUri = Uri.fromFile(new File(imgRootPath));
        // 更改相片路径
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);


        cameraView = findViewById(R.id.cameraView);
        startActivityForResult(captureIntent, REQ_1);
    }


    /**
     * 处理拍摄图片
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_1) {

                try {
                    // 接收拍摄数据
                    FileInputStream imageFis = new FileInputStream(imgRootPath);
                    Bitmap imageBmp = BitmapFactory.decodeStream(imageFis);
                    // 图片预览
                    cameraView.setImageBitmap(imageBmp);

                    // 提示开始上传
                    Toast.makeText(this, "正在上传图片...", Toast.LENGTH_LONG).show();

                    // 引用时间戳命名，进行备份储存，返回路径
                    String imgBackupPath = saveImage(imgFileName, imageBmp);

                    // 传入图片路径，进行上传
                    File imgFile = new File(imgBackupPath);
//                String imgUploadResult = UploadUtil.uploadFile(imgFile,);

                    // 通过imgUploadResult判断上传结果，200为上传成功,弹出提示
                    if (1 != 0) {
                        Toast.makeText(this, "图片上传成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            finish();
        }

    }


    /**
     * 本地储存方法,返回文件路径
     * 路径：/sdcard/fridge/
     */
    public String saveImage(String name, Bitmap bmp) {

        File appDir = new File(Environment.getExternalStorageDirectory(), "CameraBackUp");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name ;
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


    /**
     * 返回按钮
     */
    public void backHome(View view) {
        finish();
    }

}
