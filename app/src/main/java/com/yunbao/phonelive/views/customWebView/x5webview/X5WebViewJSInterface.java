package com.yunbao.phonelive.views.customWebView.x5webview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.JavascriptInterface;

import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.Utils;
import com.yunbao.phonelive.views.customWebView.utils.WebviewGlobals;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @desc Java和Java script交互的工具类
 */
public class X5WebViewJSInterface {

    private static X5WebViewJSInterface instance;
    private OnWebViewJsInterface mOnWebViewJsInterface;
    private WeakReference<Context> mWeakContext;

    public static String mCurrentPhotoPath = null;//拍照存储的路径,例如：/storage/emulated/0/Pictures/20170608104809.jpg

    public static X5WebViewJSInterface getInstance(Context context) {
        if (instance == null) {
            instance = new X5WebViewJSInterface();
        }
        instance.mWeakContext = new WeakReference<>(context);
        return instance;
    }

    /**
     * 设置监听
     * @param onWebViewJsInterface
     */
    public void setJsListener(OnWebViewJsInterface onWebViewJsInterface) {
        mOnWebViewJsInterface = onWebViewJsInterface;
    }

    private boolean checkJSauth() {
        return true;
    }

    /**
     * 判断弱引用context是否不可用
     * @return
     */
    private boolean isContextUnAvailable() {
        return null == mWeakContext || null == mWeakContext.get();
    }

    /**
     * 保存图片到系统相册
     * @param imageUrl
     */
    @JavascriptInterface
    public void saveFile(String imageUrl) {
        if (isContextUnAvailable()) {
            return;
        }
        String cameraPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator;

    }


    /**
     * 打开pdf
     *
     * @param url
     */
    @JavascriptInterface
    public void openpdf(String url, String title) {
        if (isContextUnAvailable()) {
            return;
        }
//        PDFReviewActivity.start(url,title);
    }

    @JavascriptInterface
    public void refreshWebView() {
        if (null != mOnWebViewJsInterface) {
            mOnWebViewJsInterface.refreshWebView();
        }
    }

    @JavascriptInterface
    public void setWebReadingInfo(String webmsg) {
        //产品消息
        if (null != mOnWebViewJsInterface) {
            mOnWebViewJsInterface.setWebReadingInfo(webmsg);
        }
    }

    @JavascriptInterface
    public void setTitle(final String title) {
        if (null != mOnWebViewJsInterface) {
            mOnWebViewJsInterface.setTitle(title);
        }
    }


    public interface OnWebViewJsInterface {
        void refreshWebView();
        void setWebReadingInfo(String webmsg);
        void setTitle(final String title);
    }
    /**-------------上面是业务功能----------------*/

    /**
     * 打开相机拍照的Intent【js调用的方法必须添加@JavascriptInterface】
     */
    @JavascriptInterface
    public void takePicture() {
        if (isContextUnAvailable()) {
            return;
        }
        Activity activity = Utils.getActivityFromContext(mWeakContext.get());
        if (null == activity || activity.isFinishing()) {
            return;
        }
        //调用系统拍照
        //调用系统拍照，保存到系统图库
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            //解决buildsdk>=24,调用Uri.fromFile时报错的问题
            // https://blog.csdn.net/qq_34709056/article/details/77968456
            //https://blog.csdn.net/qq_34709056/article/details/78528507
            mCurrentPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "JPEG_" + System.currentTimeMillis() +".jpg";
            File file = new File(mCurrentPhotoPath);
            Uri photoFile = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = activity.getApplicationInfo().packageName + ".provider";
                photoFile = FileProvider.getUriForFile(activity.getApplicationContext(), authority, file);
            } else {
                photoFile = Uri.fromFile(file);
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
            //启动拍照的窗体。并注册 回调处理
            activity.startActivityForResult(takePictureIntent, WebviewGlobals.CAMERA_REQUEST_CODE);
        }

    }

    /**
     * 打开本地相册选择图片的Intent
     */
    @JavascriptInterface
    public void choosePic() {
        if (isContextUnAvailable()) {
            return;
        }
        Activity activity = Utils.getActivityFromContext(mWeakContext.get());
        if (null == activity || activity.isFinishing()) {
            return;
        }
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        String IMAGE_UNSPECIFIED = "image/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
        Intent wrapperIntent = Intent.createChooser(innerIntent, "Image Browser");
        activity.startActivityForResult(wrapperIntent, WebviewGlobals.CHOOSE_FILE_REQUEST_CODE);
    }

    /**
     * 打开文件管理器选择文件的Intent
     */
    @JavascriptInterface
    public void chooseFile() {
        if (isContextUnAvailable()) {
            return;
        }
        Activity activity = Utils.getActivityFromContext(mWeakContext.get());
        if (null == activity || activity.isFinishing()) {
            return;
        }
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        String IMAGE_UNSPECIFIED = "*/*";
        innerIntent.setType(IMAGE_UNSPECIFIED).addCategory(Intent.CATEGORY_OPENABLE); // 查看类型
        try {
            activity.startActivityForResult(Intent.createChooser(innerIntent, "选取文件"),
                    11);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show("亲，木有文件管理器啊-_-!!");
        }
    }

    private final String MimeType_Img = "image/*";

    @JavascriptInterface
    public void chooseFile(String acceptType) {
        if (MimeType_Img.equals(acceptType)) {
            takePhoto();
        } else {
            chooseFile();
        }
    }

    private void takePhoto() {
        if (isContextUnAvailable()) {
            return;
        }
        Activity activity = Utils.getActivityFromContext(mWeakContext.get());
        if (null == activity || activity.isFinishing()) {
            return;
        }
//        PictureSelector.create(activity)
//                .openGallery(PictureMimeType.ofImage())
//                .imageSpanCount(4)// 每行显示个数 int
//                .selectionMode(PictureConfig.MULTIPLE)
//                .maxSelectNum(9)
//                .previewImage(true)// 是否可预览图片 true or false
//                .isCamera(true)// 是否显示拍照按钮 true or false
//                .compress(true)
//                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     * 打开录音机录音的Intent【js调用的方法必须添加@JavascriptInterface】
     */
    @JavascriptInterface
    public void openRecord() {
        if (isContextUnAvailable()) {
            return;
        }
        Activity activity = Utils.getActivityFromContext(mWeakContext.get());
        if (null == activity || activity.isFinishing()) {
            return;
        }
        Intent openRecordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        activity.startActivityForResult(openRecordIntent, WebviewGlobals.RECORD_REQUEST_CODE);
    }

}
