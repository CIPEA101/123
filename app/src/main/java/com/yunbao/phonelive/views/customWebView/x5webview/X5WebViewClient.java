package com.yunbao.phonelive.views.customWebView.x5webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.WebStorage;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @desc 主要处理解析，渲染网页等浏览器做的事情。帮助WebView处理各种通知、请求事件：比如页面加载的开始、结束、失败时的对话框提示
 */
public class X5WebViewClient extends WebViewClient {
	/**
	 * 依赖的窗口
	 */
	private Context context;
	/**
	 * webView
	 */
	private X5WebView mX5WebView;

	/**
	 * 是否需要清除历史记录
	 */
	private boolean needClearHistory = false;
	/**
	 * 当前url
	 */
	private String mCurUrl;
	/**
	 * 是否为首次加载
	 */
	private boolean mIsWxFirstLoad = true;

	public X5WebViewClient(Context context, X5WebView x5WebView) {
		this.context = context;
		this.mX5WebView = x5WebView;
	}



	/**
	 * 重写此方法表明点击网页内的链接由自己处理，而不是新开Android的系统browser中响应该链接。
	 */
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		return true;
	}


	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		return shouldOverrideUrlLoading(view, request.getUrl().toString());
	}

	/**
	 * 网页加载开始时调用，显示加载提示旋转进度条
	 */
	@Override
	public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
		super.onPageStarted(webView, url, bitmap);
		showProgressDialog();
		mCurUrl = url;
		if (null != mClientInterface) {
			mClientInterface.onPageStarted(mCurUrl);
		}
	}

	public interface OnWebViewClientInterface {
		void onPageStarted(String curUrl);
		void onPageFinished();
		void onReceivedError();
	}

	private OnWebViewClientInterface mClientInterface;

	public void setWebViewClientListener(OnWebViewClientInterface clientListener) {
		mClientInterface = clientListener;
	}

	/**
	 * 网页加载完成时调用，比如：隐藏加载提示旋转进度条*/
	@Override
	public void onPageFinished(WebView webView, String url) {
		super.onPageFinished(webView, url);
		dismissProgressDialog();
		if (null != mClientInterface) {
			mClientInterface.onPageFinished();
		}
	}

	/**
	 * 网页加载失败时调用，隐藏加载提示旋转进度条
	 * 捕获的是 文件找不到，网络连不上，服务器找不到等问题
	 */
	@Override
	public void onReceivedError(WebView webView, int errorCode,
                                String description, String failingUrl) {
		super.onReceivedError(webView, errorCode, description, failingUrl);

		dismissProgressDialog();
		webView.stopLoading();
		webView.clearView();
		if (null != mClientInterface) {
			mClientInterface.onReceivedError();
		}
	}

	/**
	 * 这个不能拦截, 目前h5 好多资源404...会走到这个异常里面
	 */
	@Override
	public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
		super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
//		String url = "";
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			url = webResourceRequest.getUrl().toString();
//		} else {
//			url = webResourceRequest.toString();
//		}
//		LogUtils.d("onReceivedHttpError url = " + url);
//		//显示404页面
//		if (null != mClientInterface) {
//			mClientInterface.onReceivedError();
//		}
	}

	@Override
	public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
		sslErrorHandler.proceed();
	}

	/**
	 * 显示进度加载对话框
	 * param msg 显示内容
	 */
	public void showProgressDialog() {
//		try {
//			if (mProgressDialog == null) {
//				mProgressDialog = new CustomProgressDialog(context, "");
//			}
//			mProgressDialog.show();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	/**
	 * 隐藏进度加载对话框
	 */
	public void dismissProgressDialog() {
//		try {
//			if (mProgressDialog != null && mProgressDialog.isShowing()) {
//				mProgressDialog.cancel();
//				mProgressDialog.dismiss();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/*=================================根据需要清除历史记录=================================*/
	@Override
	public void doUpdateVisitedHistory(WebView webView, String url, boolean isReload) {
		super.doUpdateVisitedHistory(webView, url, isReload);
		if(needClearHistory){
			//清除历史记录
			webView.clearHistory();
			needClearHistory = false;
		}
	}

	/**
	 * 设置是否清楚历史记录
	 * @param needClearHistory
	 */
	public void setNeedClearHistory(boolean needClearHistory) {
		this.needClearHistory = needClearHistory;
	}

	/**
	 * 扩充数据库的容量
	 * @param url
	 * @param databaseIdentifier
	 * @param currentQuota
	 * @param estimatedSize
	 * @param totalUsedQuota
	 * @param quotaUpdater
	 */
	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
		quotaUpdater.updateQuota(estimatedSize * 2);
	}
}
