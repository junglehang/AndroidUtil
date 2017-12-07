package com.test.androidutil.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

/**
 *  需要动态申请读写权限
 * Created by Administrator on 2017/12/6 0006.
 */

public class ScreenshotUtils {
    private String filepath;
    private RelativeLayout workbook;

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage() {
        /**     //1.构建Bitmap
         WindowManager windowManager = getWindowManager();
         Display display = windowManager.getDefaultDisplay();
         int w = display.getWidth();
         int h = display.getHeight();
         Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
         //2.获取屏幕
         View decorview = this.getWindow().getDecorView();
         decorview.setDrawingCacheEnabled(true);
         Bmp = decorview.getDrawingCache();
         */
        Log.d("veve", "GetandSaveCurrentImage: "+1);
        int height = workbook.getHeight();
        int width = workbook.getWidth();
        Bitmap Bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        workbook.setDrawingCacheEnabled(true);
        Bmp = workbook.getDrawingCache();

        String SavePath = Environment.getExternalStorageDirectory() + "/klxz_imgs/lx";
        //3.保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            filepath = SavePath + "/" + System.currentTimeMillis() + ".png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                if (true){

                //    Toast.makeText(this, "截屏文件已保存至" + filepath + "下", Toast.LENGTH_LONG).show();
                }else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        workbook.setDrawingCacheEnabled(false);
    }

    /**
     * 获取SDCard的目录路径功能   * @return
     */
    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }
}
