package top.gardel.httputils;

import java.io.IOException;
import android.content.Context;
import android.os.Handler;

/**
 * 对HttpRequest用于Android的封装
 */
public class HttpUtils {
	private static Handler mainHandler;

	/**
	 * @param context 调用者的Context
	 * @param urlpath 访问地址
	 * @param form 请求参数，传入null则无参数
	 * @param event 回调函数
	 */
	public static void Get(final Context context,
						   final String urlpath,
						   final Form form,
						   final EventHander event) {
		if (mainHandler == null) mainHandler = new Handler(context.getMainLooper());
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final String res = HttpRequest.Get(urlpath, form);
						mainHandler.post(new Runnable() {
								@Override
								public void run() {
									event.onSuccess(res);
								}
							});
					} catch (final IOException e) {
						mainHandler.post(new Runnable() {
							@Override
							public void run() {
								event.onFailed(e);
							}
						});
					}
				}
		}).start();
	}

	/**
	 * @param context 调用者的Context
	 * @param urlpath 访问地址
	 * @param formdata 请求参数，传入null则无参数
	 * @param filepath 文件保存路径
	 * @param event 回调函数
	 */
	public static void Download(final Context context,
								final String urlpath,
								final Form formdata,
								final String filepath,
								final boolean userange,
								final EventHander event) {
		if (mainHandler == null) mainHandler = new Handler(context.getMainLooper());
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final java.io.File res = HttpRequest.Download(urlpath, formdata, filepath, userange, new Callback() {
							@Override
							public void onProgress(final long downloaded, final long total) {
								mainHandler.post(new Runnable() {
									@Override
									public void run() {
										event.onProgress(downloaded, total == -1 ? Integer.MAX_VALUE : total);
									}
								});
							}
						});
						mainHandler.post(new Runnable() {
								@Override
								public void run() {
									event.onSuccess(res);
								}
							});
					} catch (final IOException e) {
						mainHandler.post(new Runnable() {
							@Override
							public void run() {
								event.onFailed(e);
							}
						});
					}
				}
			}).start();
	}

	/**
	 * @param context 调用者的Context
	 * @param urlpath 访问地址
	 * @param formdata 请求参数（这里必须要有参数）
	 * @param event 回调函数
	 */
	public static void Post(final Context context,
							final String urlpath,
							final Form formdata,
							final EventHander event) {
		if (mainHandler == null) mainHandler = new Handler(context.getMainLooper());
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final String res = HttpRequest.Post(urlpath, formdata);
						mainHandler.post(new Runnable() {
								@Override
								public void run() {
									event.onSuccess(res);
								}
							});
					} catch (final IOException e) {
						mainHandler.post(new Runnable() {
							@Override
							public void run() {
								event.onFailed(e);
							}
						});
					}
				}
			}).start();
	}

	/**
	 * @param context 调用者的Context
	 * @param urlpath 访问地址
	 * @param formdata 请求参数（这里必须要有参数）
	 * @param event 回调函数
	 */
	public static void Upload(final Context context,
							final String urlpath,
							final Form formdata,
							final EventHander event) {
		if (mainHandler == null) mainHandler = new Handler(context.getMainLooper());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final String res = HttpRequest.Post(urlpath, formdata, true, new Callback(){
						@Override
						public void onProgress(final long passeded, final long total) {
							mainHandler.post(new Runnable() {
								@Override
								public void run() {
									event.onProgress(passeded, total == -1 ? Integer.MAX_VALUE : total);
								}
							});
						}
					});
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							event.onSuccess(res);
						}
					});
				} catch (final IOException e) {
					mainHandler.post(new Runnable() {
						@Override
						public void run() {
							event.onFailed(e);
						}
					});
				}
			}
		}).start();
	}
}
