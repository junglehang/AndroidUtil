package com.test.androidutil.utils.bitmap;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 内存缓存
 * Created by 604406650 on 2016/10/11.
 */
public class MemoryCacheUtils {

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheUtils(){
        //获取本机总内存。并截取内存的1/8；模拟器默认内存是16M
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;
        //获取图片占用内存大小
        mMemoryCache = new LruCache<String,Bitmap>((int) maxMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getRowBytes() * value.getHeight();//获取图片占用内存大小
                return byteCount;
            }
        };
    }

    /**
     * 从内存读
     */
    public Bitmap getBitmapFromMemory(String url){
        return mMemoryCache.get(url);
    }
    /**
     * 写内存
     */
    public void setBitmapToMemory(String url, Bitmap bitmap){
        mMemoryCache.put(url, bitmap);
    }

}
