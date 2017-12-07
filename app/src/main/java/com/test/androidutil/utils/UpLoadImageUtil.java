package com.test.androidutil.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 604406650 on 2016/10/14.
 * 压缩原理讲解：压缩一张图片。我们需要知道这张图片的原始大小，然后根据我们设定的压缩比例进行压缩。
 * 这样我们就需要做3件事：
 * 1.获取原始图片的长和宽
 * BitmapFactory.Options options = new BitmapFactory.Options();
 * options.inJustDecodeBounds = true;
 * BitmapFactory.decodeFile(filePath, options);
 * int height = options.outHeight;
 * int width = options.outWidth;
 * 以上代码是对图片进行解码，inJustDecodeBounds设置为true，可以不把图片读到内存中,但依然可以计算出图片的大小，
 * 这正好可以满足我们第一步的需要。
 * <p>
 * 2.计算压缩比例
 * int height = options.outHeight;
 * int width = options.outWidth;
 * int inSampleSize = 1;
 * int reqHeight=800;
 * int reqWidth=480;
 * if (height > reqHeight || width > reqWidth) {
 * final int heightRatio = Math.round((float) height/ (float) reqHeight);
 * final int widthRatio = Math.round((float) width / (float) reqWidth);
 * inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
 * 一般手机的分辨率为 480*800 ，所以我们压缩后图片期望的宽带定为480，高度设为800，这2个值只是期望的宽度与高度，
 * 实际上压缩后的实际宽度也高度会比期望的要大。如果图片的原始高度或者宽带大约我们期望的宽带和高度，我们需要计算出缩放比例的数值。
 * 否则就不缩放。heightRatio是图片原始高度与压缩后高度的倍数，widthRatio是图片原始宽度与压缩后宽度的倍数。
 * inSampleSize为heightRatio与widthRatio中最小的那个，inSampleSize就是缩放值。
 * inSampleSize为1表示宽度和高度不缩放，为2表示压缩后的宽度与高度为原来的1/2
 * <p>
 * 3.缩放并压缩图片
 * //在内存中创建bitmap对象，这个对象按照缩放大小创建的
 * options.inSampleSize = calculateInSampleSize(options, 480, 800);
 * options.inJustDecodeBounds = false;
 * Bitmap bitmap= BitmapFactory.decodeFile(filePath, options);
 * <p>
 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
 * bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
 * byte[] b = baos.toByteArray();
 * 前3行的代码其实已经得到了一个缩放的bitmap对象，如果你在应用中显示图片，就可以使用这个bitmap对象了。
 * 由于考虑到网络流量的问题。我们好需要牺牲图片的质量来换取一部分空间，这里调用bm.compress()方法进行压缩，
 * 这个方法的第二个参数，如果是100，表示不压缩，我这里设置的是60，你也可以更加你的需要进行设置，
 * 在实验的过程中我设置为30，图片都不会失真。
 * }
 */

public class UpLoadImageUtil {
    private static UpLoadImageUtil sUpLoadImageUtil;
    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
    // 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static int readTimeOut = 30 * 1000; // 读取超时
    private static int connectTimeout = 10 * 1000; // 连接超时时间
    /***
     * 请求使用多长时间
     */
    private static int requestTime = 0;

    private static final String CHARSET = "utf-8"; // 设置编码

    private UpLoadImageUtil() {

    }

    /**
     * 单例模式获取上传工具
     *
     * @return
     */
    public static UpLoadImageUtil getInstance() {
        if (null == sUpLoadImageUtil) {
            sUpLoadImageUtil = new UpLoadImageUtil();
        }
        return sUpLoadImageUtil;
    }

    //计算图片的缩放值
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        /**
         * 这里需要的宽度和高度reqWidth，reqHeight最好设置为480,800，
         * 因为为设置为720,1280的时候就会出错，状态码为-2，表示图片太小
         */
        options.inSampleSize = calculateInSampleSize(options, 720, 1080);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //把bitmap转换成String
    public String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    //Bitmap对象保存为图片文件
    public File saveBitmapToFile(String picPath) {
//        File file=new File("/mnt/sdcard/pic/01.jpg");//将要保存图片的路径
        Bitmap smallBitmap = getSmallBitmap(picPath);// 根据路径获得图片并压缩，返回bitmap用于显示

        File file = new File(picPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            smallBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);

            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    //把bitmap转为文件保存
    public boolean bitmapToFileName(String filename, int type) {
        Bitmap smallBitmap = getSmallBitmap(filename);// 根据路径获得图片并压缩，返回bitmap用于显示
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 80;

        if (type == 1) {
            int degree = readPictureDegree(filename);
            Log.d(TAG, "bitmapToFileName: ");
            smallBitmap = rotateBitmap(smallBitmap, degree);
        }

//        if (degree != 0) {//旋转照片角度
//
//            smallBitmap = rotateBitmap(smallBitmap, degree);
//        }
        OutputStream stream = null;
        try {
//            stream = new FileOutputStream("/sdcard/" + filename);
            stream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean compress = false;
        try {
            compress = smallBitmap.compress(format, quality, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return compress;
    }

    /**
     * 压缩图片，处理某些手机拍照角度旋转的问题
     */
    public String compressImage(Context context, String filePath, String fileName) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filePath);

        int degree = readPictureDegree(filePath);

        if (degree != 0) {//旋转照片角度
            bm = rotateBitmap(bm, degree);
        }

//        File imageDir = SDCardUtils.getImageDir(context);
        File imageDir = new File(filePath);

        File outputFile = new File(imageDir, fileName);

        FileOutputStream out = new FileOutputStream(outputFile);

        bm.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return outputFile.getPath();
    }

    /**
     * 判断照片角度
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 旋转照片
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int degress) {
//        if (bitmap != null) {
//            Matrix m = new Matrix();
//            m.postRotate(degress);
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//                    bitmap.getHeight(), m, true);
//            return bitmap;
//        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        if (width < height) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,width, height);
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, true);
        }

        return bitmap;
    }

    /**
     * 上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param requestURL 请求的rul
     * @return 返回响应的内容
     */
    public int uploadFile(File file, String PIC_KEY, String requestURL, Map<String, String> params) {
        System.out.println("File=====" + file.getName());
        int mResponseCode = 0;
        String result = null;
        long requestTime = System.currentTimeMillis();
        long responseTime = 0;
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeOut);//读取超时
            conn.setConnectTimeout(connectTimeout);//连接超时
            conn.setDoInput(true);//允许输入
            conn.setDoOutput(true);//允许输出
            conn.setUseCaches(false);//不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            /**
             * 当文件不为空，把文件包装并且上传
             * 以下是用于上传参数
             */
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = null;
            String param = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
            String imagefiledate = dateFormat.format(new Date());
            params.put("upfile", imagefiledate);

            /***
             * 以下是用于上传参数
             */
            if (param != null && params.size() > 0) {
                Iterator<String> it = params.keySet().iterator();
                while (it.hasNext()) {
                    sb = null;
                    sb = new StringBuffer();
                    String key = it.next();
                    String value = params.get(key);
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END)
                            .append(LINE_END);
                    sb.append(value).append(LINE_END);
                    param = sb.toString();
                    Log.i(TAG, key + "=" + params + "##");
                    dos.write(param.getBytes());
                    // dos.flush();
                }
            }
            sb = null;
            param = null;
            sb = new StringBuffer();
            /**
             * 这里重点注意 name里面的为服务器端要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后名的
             * 比如:abc.png
             */
            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition:form-data; name=\"" + PIC_KEY + "\"; filename=\"" + imagefiledate
                    + file.getName() + "\"" + LINE_END);
            sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的,用于服务器端辨别文件的类型
            sb.append(LINE_END);
            param = sb.toString();
            sb = null;
            Log.i(TAG, file.getName() + "=" + param + "##");
            dos.write(param.getBytes());
            //开始上传文件
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[10240];
            int len = 0;
            int curLen = 0;
            while ((len = is.read(bytes)) != -1) {
                curLen += len;
                dos.write(bytes, 0, len);
            }
            is.close();

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200 = 成功，当响应成功，获取响应的流
             */
            mResponseCode = conn.getResponseCode();

            responseTime = System.currentTimeMillis();
            this.requestTime = (int) ((responseTime - requestTime) / 1000);
            Log.e(TAG, "response code:" + mResponseCode);
            if (mResponseCode == 200) {
                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss = 0;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e(TAG, "result : " + result);
                //解析result字符串
                JSONObject jsonObject = new JSONObject(result);
                //获取上传图片成功与否的状态码
                /**
                 * 头像保存状态
                 参数说明：Statenum＝1保存成功；
                 Statenum＝0  头像上传失败；
                 Statenum＝－1 头像图片太大；
                 Statenum＝－2 头像图片太小；
                 Statenum＝－3 头像内容不合法；
                 */
                int state = (int) jsonObject.get("state");
                Log.e(TAG, "state返回状态码 : " + state);
                return state;
            } else {
                Log.e(TAG, "request error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
