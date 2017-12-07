package com.test.androidutil.utils.appUploadUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.yifan.shufa.global.Constant;
import com.yifan.shufa.utils.SPUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 更新版本的service
 */
public class UpdateInfoService {
    ProgressDialog progressDialog;
    Handler handler;
    Context context;
    UpdateInfo updateInfo;
    private String TAG = "UpdateInfoService";

    public UpdateInfoService(Context context) {
        this.context = context;
    }

    public UpdateInfo getUpDateInfo() throws Exception {
        UpdateInfo updateInfo = new UpdateInfo();
        String android_url = SPUtil.getString(context, "android_url", null);
        String path = android_url;
        Log.d(TAG, "getUpDateInfo: android_url"+ Constant.ANDROID_URL+android_url);

        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            // 创建一个url对象
            URL url = new URL(path);
            // 通過url对象，创建一个HttpURLConnection对象（连接）
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            //	设置连接超时
//			urlConnection.setDoOutput(true);
//			urlConnection.setDoInput(true);
//			urlConnection.setConnectTimeout(10000);  //设置连接超时为10s
//			urlConnection.setReadTimeout(10000);     //读取数据超时也是10s
            // 通过HttpURLConnection对象，得到InputStream
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "GB2312"));
            // 使用io流读取文件
            String line1 = reader.readLine();
            Log.d(TAG, "getUpDateInfo: line1");
            while ((line = reader.readLine()) != null) {
                sb.append(line);
        }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            //	设置访问升级文件出问题标志位直接登陆
            updateInfo.setVersion("false");
            return updateInfo;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        String info = sb.toString();

        updateInfo.setVersion(info.split("&")[1]);
        Log.d(TAG, "getUpDateInfo: "+info);
        updateInfo.setDescription(info.split("&")[2]);
        updateInfo.setUrl(info.split("&")[3]);

        this.updateInfo = updateInfo;
        return updateInfo;
    }

    public void downLoadFile(final String url, final ProgressDialog pDialog, Handler h) {
        progressDialog = pDialog;
        handler = h;
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) (entity.getContentLength());   //获取文件大小
//					float length =  (entity.getContentLength());   //获取文件大小
                    progressDialog.setMax(length / 1024);         //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "Education_Android.apk");
                        fileOutputStream = new FileOutputStream(file);
                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        //看不出progressbar的效果。
                        byte[] buf = new byte[10 * 1024];
                        int ch = -1;
                        float process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch / 1024;
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setProgressNumberFormat("%1d KB/%2d KB");
                            progressDialog.setProgress((int) process);       //这里就是关键的实时更新进度了！
//							progressDialog.setProgressNumberFormat(String.format("%.2fM/%.2fM", process,length/1024/1024));
                        }
                    //    String version = updateInfo.getVersion();
                     //   Log.d(TAG, "run: "+version);

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        pDialog.dismiss();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }


    void down() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
                update();
            }
        });
    }

    void update() {
        //	把服务器上的版本号更新到SharePreference
    //    SPUtil.putString(context, "Version", updateInfo.getVersion());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "Education_Android.apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
