HttpUtils for Android
=========================
Android HttpUrlConnection utilities
-------------------------
一个用于Android的java原生HttpUrlConnection的封装，目前实现了Get,Post,Download的异步回调封装
### 使用方法:
``` Java
import top.gardel.httputils.*;
	
//不带参数的GET:
HttpUtils.Get(getApplicationContext()/* 当前应用的Context */, "http://192.168.0.101/test.php", null/* 参数 */, new top.gardel.httputils.EventHander() {
    @Override
    public void onSuccess(String result) {
        tv.setText(result);  // 可以直接更新UI
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
	
//带参数的Get
HttpUtils.Get(getApplicationContext(), "http://192.168.0.101/test.php", new top.gardel.httputils.Form()
        .addParam("key", "value"), new top.gardel.httputils.EventHander() {
    @Override
    public void onSuccess(String result) {
        tv.setText(result);  // 可以直接更新UI
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
	
//Post
HttpUtils.Post(getApplicationContext()/* 当前应用的Context */, "http://192.168.0.101/test.php", new top.gardel.httputils.Form()
        .addParam("key", "value") // 还可以添加更多
        .addParam("foo", "bar"), new top.gardel.httputils.EventHander() {
    @Override
    public void onSuccess(String result) {
        tv.setText(result); // 可以直接更新UI
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
	
//文件下载(注意Android M以后需要动态请求权限
HttpUtils.Download(getApplicationContext(), "http://192.168.0.101/test.php", null/* Get参数 */, android.os.Environment.getExternalStorageDirectory().getPath() + "/example"， true/* 断点续传 */, new top.gardel.httputils.Form()
        .addParam("key", "value"), new top.gardel.httputils.EventHander() {
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
	
//文件上传
HttpUtils.Upload(getApplicationContext(), "http://192.168.0.101/test.php", new top.gardel.httputils.Form()
        .addFile("file", new java.io.File(android.os.Environment.getExternalStorageDirectory().getPath() + "/example"))
        //还可以添加更多
        .addParam("key", "value"), new top.gardel.httputils.EventHander() {
    @Override
    public void onSuccess(String result) {
        tv.setText(result);  // 可以直接更新UI
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
``` 
### 更多操作:

``` Java
// 以下可以放到Activity或Application的onCreate里执行
HttpRequest.setCookieSavePath(getFilesDir().getAbsolutePath() + "/cookies.json"); //设置Cookie持久化
HttpRequest.setFollowRedirects(false); //禁止跟踪重定向
HttpRequest.setTIMEOUT(12000); //设置读写超时
HttpRequest.setInputBufSize(40960); //设置输入缓冲的大小
HttpRequest.setOutputBufSize(40960); //设置输出缓冲的大小
```
