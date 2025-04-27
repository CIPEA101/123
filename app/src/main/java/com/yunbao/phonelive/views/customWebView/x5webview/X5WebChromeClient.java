package com.yunbao.phonelive.views.customWebView.x5webview;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;

/**
 * @desc 处理解析，渲染网页等浏览器做的事情。辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
 */
public class X5WebChromeClient extends WebChromeClient {
	/**
	 * 依赖的窗口
	 * */
	private Context mContext;
	/**
	 * Chrome client listener
	 */
	private OnChromeClientInterface mClientInterface;

	public X5WebChromeClient(Context context) {
		this.mContext = context;
	}

	public void setClientInterface(OnChromeClientInterface clientInterface) {
		this.mClientInterface = clientInterface;
	}

	//更改加载进度值
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		if (newProgress < 5) {
			newProgress = 5;
		}
		if (null != mClientInterface) {
			mClientInterface.onProgressChanged(newProgress);
		}
	}

	@Override
	public boolean onJsAlert(WebView webView, String url, String message, JsResult jsResult) {
		jsResult.cancel();
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

		return true;
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		if (TextUtils.isEmpty(title) || null == mClientInterface) {
			return;
		}
		if (title.length() > 11) {
			String str = title.substring(0, 10) + "...";
			mClientInterface.onReceivedTitle(str);
		} else {
			mClientInterface.onReceivedTitle(title);
		}
	}

	@Override
	public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
		quotaUpdater.updateQuota(spaceNeeded * 2);
	}

	public interface OnChromeClientInterface {
		void onProgressChanged(int newProgress);
		void onReceivedTitle(String title);
	}

	/*=========================================实现webview打开文件管理器功能==============================================*/
	/**
	 * HTML界面：
	 * <input accept="image/*" capture="camera" id="imgFile" name="imgFile" type="file">
	 * <input type="file" capture="camera" accept="* /*" name="image">
	 *  */

	/**
	 * 重写WebChromeClient 的openFileChooser方法
	 * 这里有个漏洞，4.4.x的由于系统内核发生了改变，没法调用以上方法，现在仍然找不到解决办法，唯一的方法就是4.4直接使用手机浏览器打开，这个是可以的。
	 * */

	private android.webkit.ValueCallback<Uri> mUploadMessage;//5.0--版本用到的
	private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;//5.0++版本用到的

	//3.0--
	public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg) {
		openFileChooserImpl(uploadMsg, null);
	}
	//3.0++
	public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType) {
		openFileChooserImpl(uploadMsg, acceptType);
	}
	//4.4--(4.4.2特殊，不执行该方法)
	public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
		openFileChooserImpl(uploadMsg, acceptType);
	}

	//5.0++
	@Override
	public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
		String acceptType = null;
		if (null != fileChooserParams && null != fileChooserParams.getAcceptTypes() &&
				fileChooserParams.getAcceptTypes().length > 0) {
			acceptType = fileChooserParams.getAcceptTypes()[0];
		}
		openFileChooserImplForAndroid5(filePathCallback, acceptType);
		return true;
	}
	//5.0--的调用
	private void openFileChooserImpl(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType) {
		mUploadMessage = uploadMsg;
		dispatchTakePictureIntent(acceptType);
	}
	//5.0++的调用
	private void openFileChooserImplForAndroid5(android.webkit.ValueCallback<Uri[]> uploadMsg,
												String acceptType) {
		mUploadCallbackAboveL = uploadMsg;
		dispatchTakePictureIntent(acceptType);
	}

	//拍照或者打开文件管理器
	private void dispatchTakePictureIntent(String acceptType) {
		if(mUploadMessage!=null){

		}
		if(mUploadCallbackAboveL!=null) {

		}
		X5WebViewJSInterface.getInstance(mContext).chooseFile(acceptType);
	}

	public android.webkit.ValueCallback<Uri> getUploadMessage() {
		return mUploadMessage;
	}

	public android.webkit.ValueCallback<Uri[]> getUploadCallbackAboveL() {
		return mUploadCallbackAboveL;
	}

	public void setUploadMessage(android.webkit.ValueCallback<Uri> uploadMessage) {
		this.mUploadMessage = uploadMessage;
	}

	public void setUploadCallbackAboveL(android.webkit.ValueCallback<Uri[]> uploadCallbackAboveL) {
		this.mUploadCallbackAboveL = uploadCallbackAboveL;
	}


}
