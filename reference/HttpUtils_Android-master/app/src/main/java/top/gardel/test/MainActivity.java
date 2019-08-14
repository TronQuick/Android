package top.gardel.test;

public class MainActivity extends android.app.Activity {
    android.widget.TextView tv;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = findViewById(R.id.mainTextView1);
        top.gardel.httputils.HttpRequest.setCookieSavePath(getFilesDir().getAbsolutePath() + "/cookies.json"); //设置Cookie持久化
        top.gardel.httputils.HttpRequest.setFollowRedirects(false); //禁止跟踪重定向
        top.gardel.httputils.HttpRequest.setTIMEOUT(12000); //设置读写超时
        top.gardel.httputils.HttpRequest.setInputBufSize(4096 * 1024); //设置输入缓冲的大小
        top.gardel.httputils.HttpRequest.setOutputBufSize(4096 * 1024); //设置输出缓冲的大小
    }

    public void GetBT(android.view.View v) {
        top.gardel.httputils.HttpUtils.Get(this, "http://192.168.0.101/api/cookie.php", null, new top.gardel.httputils.EventHander() {
            @Override
            public void onSuccess(String result) {
                tv.setText(result);
            }

            @Override
            public void onSuccess(java.io.File file) {
                // 这个函数不会被执行
            }

            @Override
            public void onProgress(long downloaded, long total) {
                // 这个函数不会被执行
            }

            @Override
            public void onFailed(Exception e) {
                android.widget.Toast.makeText(getApplicationContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GetQBT(android.view.View v) {
        top.gardel.httputils.HttpUtils.Get(this, "http://192.168.0.101/api/post.php", new top.gardel.httputils.Form()
                .addParam("key", "value"), new top.gardel.httputils.EventHander() {
            @Override
            public void onSuccess(String result) {
                tv.setText(result);
            }

            @Override
            public void onSuccess(java.io.File file) {
                // 这个函数不会被执行
            }

            @Override
            public void onProgress(long passeded, long total) {
                // 这个函数不会被执行
            }

            @Override
            public void onFailed(Exception e) {
                android.widget.Toast.makeText(getApplicationContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void PostBT(android.view.View v) {
        top.gardel.httputils.HttpUtils.Post(this, "http://192.168.0.101/api/post.php", new top.gardel.httputils.Form()
                .addParam("key", "value"), new top.gardel.httputils.EventHander() {
            @Override
            public void onSuccess(String result) {
                tv.setText(result);
            }

            @Override
            public void onSuccess(java.io.File file) {
                // 这个函数不会被执行
            }

            @Override
            public void onProgress(long passeded, long total) {
                // 这个函数不会被执行
            }

            @Override
            public void onFailed(Exception e) {
                android.widget.Toast.makeText(getApplicationContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void DownloadBT(android.view.View v) {
        top.gardel.httputils.HttpUtils.Download(this,
                "https://gardel.miumstudio.cn/test.3gp", // md5: f3dda820316ff8706d5904a55c792716
                null,
                android.os.Environment.getExternalStorageDirectory().getPath() + "/example",
                true,
                new top.gardel.httputils.EventHander() {
            @Override
            public void onSuccess(String result) {
                // 这个函数不会被执行
            }

            @Override
            public void onSuccess(java.io.File file) {
                tv.setText(String.format(getString(R.string.str_file_save_to), file.getPath()));
            }

            @Override
            public void onProgress(long passeded, long total) {
                android.util.Log.d("Progress", "已下载: " + passeded + "/" + total);
            }

            @Override
            public void onFailed(Exception e) {
                android.widget.Toast.makeText(getApplicationContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void UploadBT(android.view.View v) {
        top.gardel.httputils.HttpUtils.Upload(this, "http://192.168.0.101/api/post.php", new top.gardel.httputils.Form()
                .addFile("file", new java.io.File(android.os.Environment.getExternalStorageDirectory().getPath() + "/example"))
                .addParam("key", "value"), new top.gardel.httputils.EventHander() {
            @Override
            public void onSuccess(String result) {
                tv.setText(result);
            }

            @Override
            public void onSuccess(java.io.File file) {
                // 这个函数不会被执行
            }

            @Override
            public void onProgress(long passeded, long total) {
                android.util.Log.d("Progress", "已上传: " + passeded + "/" + total);
            }

            @Override
            public void onFailed(Exception e) {
                android.widget.Toast.makeText(getApplicationContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
