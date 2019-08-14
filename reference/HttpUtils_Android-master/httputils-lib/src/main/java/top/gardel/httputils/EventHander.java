package top.gardel.httputils;

public interface EventHander {
	void onSuccess(String result);
	void onSuccess(java.io.File file);
	void onFailed(Exception e);
	void onProgress(long passeded, long total);
}
