package com.test.androidutil.utils.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.yifan.shufa.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 自定义图片加载工具
 * Created by 604406650 on 2016/10/11.
 */

public class MyBitmapUtils {

    private final MemoryCacheUtils mMemoryCacheUtils;
    private final LocalCacheUtils mLocalCacheUtils;
    private final NetCacheUtils mNetCacheUtils;

    public MyBitmapUtils(){
        //内存缓存
        mMemoryCacheUtils = new MemoryCacheUtils();
        //本地缓存
        mLocalCacheUtils = new LocalCacheUtils();
        //网络缓存
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);

    }
    public void display(ImageView ivPic , String url, int flag){
        ivPic.setImageResource(R.mipmap.default_error);//设置默认加载图片

        Bitmap bitmap = null;
        // 从内存读
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            System.out.println("从内存读取图片啦...");
            return;
        }

        // 从本地读
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);

        if (bitmap != null) {
            Bitmap ratio = ratio(bitmap, bitmap.getWidth(), bitmap.getHeight());
            Log.d("vivi", "displayxxxxx: "+ratio.getByteCount());

            ivPic.setImageBitmap(ratio);
            System.out.println("从本地读取图片啦...");
            mMemoryCacheUtils.setBitmapToMemory(url, ratio);// 将图片保存在内存
            return;
        }

        // 从网络读
        mNetCacheUtils.getBitmapFromNet(ivPic, url,flag);
    }

    /**
     * 此方法的重载主要是为了在 我的  界面里面编辑用户头像的
     * 时候可以及时更新
     * @param ivPic
     * @param url
     * @param flag
     */
    public void display1(ImageView ivPic , String url, int flag){
        ivPic.setImageResource(R.mipmap.default_error);//设置默认加载图片
        Bitmap bitmap = null;
        //从内存读
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if(bitmap != null){
            ivPic.setImageBitmap(bitmap);
            //从网络读,主要是为了更新
            mNetCacheUtils.getBitmapFromNet(ivPic, url,flag);
            return;
        }else{
            //从本地读
            bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
            if(bitmap != null){
                ivPic.setImageBitmap(bitmap);
                mMemoryCacheUtils.setBitmapToMemory(url,bitmap);//将图片保存在内存
                //从网络读,主要是为了更新
                mNetCacheUtils.getBitmapFromNet(ivPic, url,flag);
                return;
            }else{
                //从网络读,主要是为了更新
                mNetCacheUtils.getBitmapFromNet(ivPic, url,flag);
            }
        }

//        //从网络读
//        mNetCacheUtils.getBitmapFromNet(ivPic, url);
    }


    public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);
    }
}
