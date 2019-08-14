package top.gardel.httputils;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

/**
 * HTTP访问类
 * 可以POST,GET,Download,Upload
 *
 * @author Gardel
 * @version 1.1.1
 * @package top.gardel.httputils
 * @copyright 迷雾工作室
 * @link http://www.mium.studio
 */
public class HttpRequest {
    private static int TIMEOUT = 10000;
    private static JSONCookieStore cookieStore = null;
    private static int inputBufSize = 4096;
    private static int outputBufSize = 4096;

    /**
     * 将Cookie保存到Json
     *
     * @param path 储存路径
     */
    public static void setCookieSavePath(String path) {
        cookieStore = new JSONCookieStore(path);
        CookieHandler.setDefault(new CookieManager(cookieStore, new CookiePolicy() {
            @Override
            public boolean shouldAccept(URI uri, HttpCookie httpCookie) {
                return true;
            }
        }));
    }

    public static JSONCookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 设置跟踪重定向
     * 默认: true
     */
    public static void setFollowRedirects(boolean isFollow) {
        HttpURLConnection.setFollowRedirects(isFollow);
    }

    public static int getInputBufSize() {
        return inputBufSize;
    }

    public static void setInputBufSize(int inputBufSize) {
        HttpRequest.inputBufSize = inputBufSize;
    }

    public static int getOutputBufSize() {
        return outputBufSize;
    }

    public static void setOutputBufSize(int outputBufSize) {
        HttpRequest.outputBufSize = outputBufSize;
    }

    public static void setTIMEOUT(int TIMEOUT) {
        HttpRequest.TIMEOUT = TIMEOUT;
    }

    public static String Get(String urlpath) throws MalformedURLException, ProtocolException, HttpException, IOException {
        return Get(urlpath, null);
    }

    /**
     * GET方法
     *
     * @param urlpath 请求地址
     * @param query   查询表单
     * @return String 结果
     * @throws MalformedURLException,ProtocolException,IOException 异常
     */
    public static String Get(String urlpath, Form query) throws MalformedURLException, ProtocolException, HttpException, IOException {
        String realurl = urlpath;
        if (null != query && query.hasParamPart()) {
            realurl += "?" + query.buildQueryString();
        }
        HttpURLConnection HttpConn = (HttpURLConnection) new URL(realurl).openConnection();
        //设置参数
        //HttpConn.setDoOutput(true);
        HttpConn.setConnectTimeout(TIMEOUT);
        HttpConn.setReadTimeout(TIMEOUT);
        HttpConn.setRequestMethod("GET");

        //设置请求属性
        HttpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        HttpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        HttpConn.setRequestProperty("Charset", "UTF-8");
        //HttpConn.setRequestProperty("User-Agent", "HttpRequest by Gardel(MiumStudio)");
        HttpConn.connect();

        switch (HttpConn.getResponseCode()) {
            case HttpURLConnection.HTTP_OK: {
                StringBuilder sb = new StringBuilder(inputBufSize);
                byte[] buf = new byte[inputBufSize];
                InputStream is = HttpConn.getInputStream();
                int len;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                is.close();
                if (null != cookieStore) cookieStore.write();
                return sb.toString();
            }
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new HttpException("404页面不存在");
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new HttpException("服务器内部错误");
            case HttpURLConnection.HTTP_RESET:
                throw new HttpException("交易被网关重置");
            case HttpURLConnection.HTTP_FORBIDDEN:
            case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
            case HttpURLConnection.HTTP_UNAVAILABLE:
                throw new HttpException("服务器拒绝交易");
            default:
                throw new HttpException(HttpConn.getResponseCode(), HttpConn.getResponseMessage());
        }

    }

    public static File Download(String urlpath, Form query, String filepath) throws MalformedURLException, ProtocolException, HttpException, IOException {
        return Download(urlpath, query, filepath, true, null);
    }

    public static File Download(String urlpath, String filepath) throws MalformedURLException, ProtocolException, HttpException, IOException {
        return Download(urlpath, null, filepath, true, null);
    }

    public static File Download(String urlpath, String filepath, Callback callback) throws MalformedURLException, ProtocolException, HttpException, IOException {
        return Download(urlpath, null, filepath, true, callback);
    }

    /**
     * 下载方法
     *
     * @param urlpath  请求地址
     * @param query    查询表单
     * @param filepath 文件路径
     * @param callback 下载进度回调
     * @return File
     * @throws MalformedURLException,ProtocolException,IOException 请求出错
     */
    @SuppressLint("DefaultLocale")
    public static File Download(String urlpath, Form query, String filepath, boolean userange, Callback callback) throws MalformedURLException, ProtocolException, HttpException, IOException {
        String realurl = urlpath;
        File file = new File(filepath);
        if (null != query && query.hasParamPart()) {
            realurl += "?" + query.buildQueryString();
        }
        HttpURLConnection HttpConn = (HttpURLConnection) new URL(realurl).openConnection();
        //设置参数
        //HttpConn.setDoOutput(true);
        HttpConn.setConnectTimeout(TIMEOUT);
        HttpConn.setReadTimeout(TIMEOUT);
        HttpConn.setRequestMethod("GET");

        //设置请求属性
        HttpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        HttpConn.setRequestProperty("Connection", "Keep-Alive"); // 维持长连接
        HttpConn.setRequestProperty("Charset", "UTF-8");
        //HttpConn.setRequestProperty("User-Agent", "HttpRequest by Gardel(MiumStudio)");
        if (userange && file.exists() && file.length() > 0)
            HttpConn.setRequestProperty("Range", String.format("bytes=%d-", file.length()));
        HttpConn.connect();

        switch (HttpConn.getResponseCode()) {
            case HttpURLConnection.HTTP_OK: {
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = HttpConn.getInputStream();
                long totalLen = -1, readLen = 0;
                try {
                    totalLen = Long.parseLong(HttpConn.getHeaderField("Content-Length"));
                } catch (Exception ignored) {
                }
                byte[] b = new byte[inputBufSize];
                int len;
                while ((len = is.read(b)) != -1) {
                    readLen += len;
                    fos.write(b, 0, len);
                    if (callback != null) callback.onProgress(readLen, totalLen);
                }
                fos.flush();
                fos.close();
                is.close();
                if (null != cookieStore) cookieStore.write();
                return file;
            }
            case HttpURLConnection.HTTP_PARTIAL: {
                FileOutputStream fos = new FileOutputStream(file, true);
                InputStream is = HttpConn.getInputStream();
                long totalLen = -1, readLen = 0;
                try {
                    totalLen = file.length() + Long.parseLong(HttpConn.getHeaderField("Content-Length"));
                    readLen = file.length();
                } catch (Exception ignored) {
                }
                byte[] b = new byte[inputBufSize];
                int len;
                while ((len = is.read(b)) != -1) {
                    readLen += len;
                    fos.write(b, 0, len);
                    if (callback != null) callback.onProgress(readLen, totalLen);
                }
                fos.flush();
                fos.close();
                is.close();
                if (null != cookieStore) cookieStore.write();
                return file;
            }
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new HttpException("404页面不存在");
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new HttpException("服务器内部错误");
            case HttpURLConnection.HTTP_RESET:
                throw new HttpException("交易被网关重置");
            case HttpURLConnection.HTTP_FORBIDDEN:
            case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
            case HttpURLConnection.HTTP_UNAVAILABLE:
                throw new HttpException("服务器拒绝交易");
            default:
                throw new HttpException(HttpConn.getResponseCode(), HttpConn.getResponseMessage());
        }
    }

    public static String Post(String urlpath, Form formdata) throws MalformedURLException, ProtocolException, HttpException, IOException {
        return Post(urlpath, formdata, false, null);
    }

    /**
     * POST方法
     *
     * @param urlpath     请求地址
     * @param formdata    POST数据
     * @param isMultipart 是否为multipart/form-data
     * @param callback    上传进度回调
     * @return String
     * @throws MalformedURLException,ProtocolException,IOException 请求出错
     */
    public static String Post(String urlpath, Form formdata, boolean isMultipart, Callback callback) throws MalformedURLException, ProtocolException, HttpException, IOException {
        if (formdata == null) throw new IOException("postdata for POST method is required");
        if (!isMultipart && formdata.hasFilePart())
            isMultipart = true; // 如果表单有文件则必须用multipart/form-data提交
        HttpURLConnection HttpConn = (HttpURLConnection) new URL(urlpath).openConnection();
        //设置参数
        HttpConn.setDoOutput(true);
        HttpConn.setDoInput(true);   //需要输入
        HttpConn.setUseCaches(false);  //不允许缓存
        HttpConn.setConnectTimeout(TIMEOUT);
        HttpConn.setReadTimeout(TIMEOUT);
        HttpConn.setRequestMethod("POST");   //设置POST方式连接

        //设置请求属性
        HttpConn.setRequestProperty("Content-Type", (isMultipart ? "multipart/form-data; boundary=" + Form.getRandomSting() : "application/x-www-form-urlencoded"));
        HttpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        HttpConn.setRequestProperty("Charset", "UTF-8");
        //HttpConn.setRequestProperty("User-Agent", "HttpRequest by Gardel(MiumStudio)");
        HttpConn.connect();
        if (isMultipart) {
            formdata.submit(HttpConn.getOutputStream(), callback);
        } else {
            HttpConn.getOutputStream().write(formdata.buildQueryString().getBytes());
        }
        switch (HttpConn.getResponseCode()) {
            case HttpURLConnection.HTTP_OK: {
                StringBuilder sb = new StringBuilder(inputBufSize);
                byte[] buf = new byte[inputBufSize];
                InputStream is = HttpConn.getInputStream();
                int len;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    sb.append(new String(buf, 0, len));
                }
                is.close();
                if (null != cookieStore) cookieStore.write();
                return sb.toString();
            }
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new HttpException("404页面不存在");
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new HttpException("服务器内部错误");
            case HttpURLConnection.HTTP_RESET:
                throw new HttpException("交易被网关重置");
            case HttpURLConnection.HTTP_FORBIDDEN:
            case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
            case HttpURLConnection.HTTP_UNAVAILABLE:
                throw new HttpException("服务器拒绝交易");
            default:
                throw new HttpException(HttpConn.getResponseCode(), HttpConn.getResponseMessage());
        }

    }
}
