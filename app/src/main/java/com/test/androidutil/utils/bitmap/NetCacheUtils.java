package com.test.androidutil.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存
 * Created by 604406650 on 2016/10/11.
 */
public class NetCacheUtils {

    private LocalCacheUtils mLocalCacheUtils;//本地缓存工具
    private MemoryCacheUtils mMemoryCacheUtils;//内存缓存工具
    private Bitmap bitmap ;
    public NetCacheUtils(){};
    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 从网络下载图片
     */
    public void getBitmapFromNet(ImageView ivPic, String url, int flag) {
        //启动AsyncTask,参数会在doInbackground中获取
        new BitmapTask().execute(ivPic, url,flag);
    }

    /**
     * Handler和线程池的封装
     * <p>
     * 第一个泛型:参数类型  第二个泛型 : 更新进度的泛型  第三个泛型: doInBackground的返回值
     * onPostExecute的参数就是doInBackground的返回值
     */
    private class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private ImageView mIvPic;
        private String mUrl;
        private int flag;

        /**
         * 后台耗时方法在此执行, 子线程
         */
        @Override
        protected Bitmap doInBackground(Object... params) {
            mIvPic = (ImageView) params[0];
            mUrl = (String) params[1];
            flag = (int) params[2];
//            mIvPic.setTag(mUrl);//将url和imageview绑定（要在主线程中操作）
            bitmap = downloadBitmap(mUrl, flag);//下载图片
            return bitmap;//下载图片
        }

        /**
         * 耗时方法结束后，执行该方法，主线程
         *
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mIvPic.setTag(mUrl);//将url和imageview绑定（要在主线程中操作）
            if (bitmap != null) {
                String bindUrl = (String) mIvPic.getTag();
                if (mUrl.equals(bindUrl)) {//确保图片设定给了正确的imageview
                    mIvPic.setImageBitmap(bitmap);
                    mLocalCacheUtils.setBitmapToLocal(mUrl, bitmap);//将图片保存在本地
                    mMemoryCacheUtils.setBitmapToMemory(mUrl, bitmap);//将图片保存在内存中
                }
            }
        }
    }

    /**
     * 下载图片
     *
     * @param url
     * @param flag
     * @return
     */
    private Bitmap downloadBitmap(String url, int flag) {
        HttpURLConnection conn = null;
        Bitmap bitmap = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = conn.getInputStream();
                if(flag == 1){//书法课堂里面的图片加载需要压缩
                    //图片压缩
//                    BitmapFactory.Options option = new BitmapFactory.Options();
//                    option.inSampleSize = 1;//宽高都压缩为原来的二分之一，此参数需要根据图片要展示的大小来确定
//                    option.inPreferredConfig = Bitmap.Config.RGB_565;//设置图片格式
//                    bitmap = BitmapFactory.decodeStream(inputStream, null, option);
                    //图片本身以及处理之后放在服务器上的，已经比较小了 ，不需要压缩了
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }else if(flag == 2){//我的  界面里面的用户图像上传到服务器的时候已经将图片压缩了所以在加载的时候不需要再次压缩了，否则会失真
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }


}

