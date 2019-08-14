package top.gardel.httputils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * 由Gardel在2018/8/7使用Android Studio创建.
 */
public class Form {
    private Map<String, String> params;
    private Map<String, File> files;
    private static String randomSting;//随机字符串（表单的分割符）
    private long formLength = 0;

    static {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*_";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 15; i++) {
            sb.append(base.charAt(new java.util.Random().nextInt(base.length())));
        }
        randomSting = sb.toString();
    }

    public Form() {
        this.params = new TreeMap<>();
        this.files = new TreeMap<>();
    }

    public Form(Map<String, String> params, Map<String, File> files) {
        if (this.params == null) this.params = new TreeMap<>();
        if (this.files == null) this.files = new TreeMap<>();
        // 添加表单元素并计算总长度，49和100是submit方法里每次循环中字符串常量的长度
        if (params != null) for (Map.Entry<String, String> entry : params.entrySet()) {
            formLength += 49 + entry.getKey().length() + entry.getValue().length() + randomSting.length();
            this.params.put(entry.getKey(), entry.getValue());
        }
        if (files != null) for (Map.Entry<String, File> entry : files.entrySet()) {
            formLength += 100 + entry.getKey().length() + entry.getValue().getName().length() + entry.getValue().length() + randomSting.length();
            this.files.put(entry.getKey(), entry.getValue());
        }
    }

    public Form clear() {
        this.params.clear();
        this.files.clear();
        this.formLength = 0;
        return this;
    }

    public static String getRandomSting() {
        return randomSting;
    }

    // 添加请求参数
    public Form addParam(String key, String value) {
        this.params.put(key, value);
        formLength += 49 + key.length() + value.length() + randomSting.length();
        return this;
    }

    // 添加上传文件
    public Form addFile(String name, File file) {
        formLength += 100 + name.length() + file.getName().length() + file.length() + randomSting.length();
        this.files.put(name, file);
        return this;
    }

    public boolean hasParamPart() {
        return this.params.size() > 0;
    }

    public boolean hasFilePart() {
        return this.files.size() > 0;
    }

    // 构建查询字符串
    public String buildQueryString() {
        if (!hasParamPart()) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            try {
                sb.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
                sb.append(entry.getKey())
                        .append("=")
                        .append("&");
            }
        }
        sb.delete(sb.lastIndexOf("&"), sb.length());
        return sb.toString();
    }

    // 提交表单（不能手动调用）
    public void submit(OutputStream os, Callback callback) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        // 添加键值对表单
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            dos.writeBytes("--" + randomSting + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");//每个参数都用随机字符串分割，键值后有两个回车换行
            dos.writeBytes(entry.getValue());
            dos.writeBytes("\r\n");
            if (callback != null) callback.onProgress(dos.size(), formLength);
        }
        // 添加文件表单
        for (Map.Entry<String, File> entry : this.files.entrySet()) {
            dos.writeBytes("--" + randomSting + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + entry.getValue().getName() + "\"\r\n");
            dos.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            try {
                FileInputStream fis = new FileInputStream(entry.getValue());
                byte[] buf = new byte[HttpRequest.getInputBufSize()];
                int len;
                while ((len = fis.read(buf)) != -1) {
                    dos.write(buf, 0, len);
                    if (dos.size() > formLength) formLength = dos.size();
                    if (callback != null) callback.onProgress(dos.size(), formLength);
                }
                fis.close();
                dos.flush();
            } catch (FileNotFoundException ignored) {
            }
            dos.writeBytes("\r\n");
        }
        dos.writeBytes("--");
        dos.writeBytes(randomSting);
        dos.writeBytes("--\r\n");//结尾仍以随机字符串结尾，也可以不要
        if (callback != null) callback.onProgress(formLength, formLength);
        dos.flush();
    }
}
