package top.gardel.httputils;

import java.io.IOException;

public class HttpException extends IOException
{
    private int errorCode = 10000;
    
    HttpException (int code, String msg) {
        super("交易失败! 服务器返回代码:" + code + " 错误原因:" + msg);
        errorCode = code;
    }
    
    HttpException (String msg) {
        super(msg);
    }

    public int getCode() {
        return errorCode;
    }
}
