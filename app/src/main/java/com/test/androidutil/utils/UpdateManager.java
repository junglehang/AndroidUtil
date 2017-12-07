package com.test.androidutil.utils;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yifan.shufa.R;
import com.yifan.shufa.activity.HomeActivity;
import com.yifan.shufa.widget.BaseDialog;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import static android.content.Context.NOTIFICATION_SERVICE;

;

/**
 * Created by Administrator on 2017/11/11 0011.
 * 版本更新
 */

public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
    private int type = 2;//更新方式，0：引导更新，1：安装更新，2：强制更新
    private String url = "";//apk下载地址
    private String updateMessage = "";//更新内容
    private String fileName = null;//文件名
    private boolean isDownload = false;//是否下载
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private BaseDialog dialog;
    private ProgressDialog progressDialog;

    public static UpdateManager updateManager;
    private AlertDialog.Builder alertDialog;

    public static UpdateManager getInstance() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        return updateManager;
    }

    private UpdateManager() {

    }

    /**
     * 弹出版本更新提示框
     */
    public void showDialog(final Context context) {
        String title = "";
        String left = "";
        boolean cancelable = false;
        if (type == 1 | isDownload) {
            title = "安装新版本";
            left = "立即安装";
        } else {
            title = "发现新版本";
            left = "立即更新";
        }
        if (type == 2) {
            cancelable = false;
        }
        //    dialog.dismiss();
        alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(updateMessage).setCancelable(cancelable)

                .setPositiveButton(left, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.create().dismiss();
                        if (type == 1 | isDownload) {
                            installApk(context, new File(downLoadPath, fileName));
                        } else {
                            if (url != null && !TextUtils.isEmpty(url)) {
                                if (type == 2) {
                                    createProgress(context);
                                } else {
                                    createNotification(context);
                                //    createProgress(context);
                                }
                                downloadFile(context);
                            } else {
                                Toast.makeText(context, "下载地址错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.create().dismiss();
                        Intent intent = new Intent(context, HomeActivity.class);
                        context.startActivity(intent);
                       /* if (type == 2) {
                            System.exit(0);
                        }*/
                    }
                });

        alertDialog.create().show();

      /*  dialog = new BaseDialog.Builder(context).setTitle(title).setMessage(updateMessage).setCancelable(cancelable)
                .setLeftClick(left, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (type == 1 | isDownload) {
                            installApk(context, new File(downLoadPath, fileName));
                        } else {
                            if (url != null && !TextUtils.isEmpty(url)) {
                                if (type == 2) {
                                    createProgress(context);
                                } else {
                                    createNotification(context);
                                }
                                downloadFile(context);
                            } else {
                                Toast.makeText(context, "下载地址错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })

                .setRightClick("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (type == 2) {
                            System.exit(0);
                        }
                    }
                })
                .create();
        dialog.show();*/
    }


    /**
     * 下载apk
     *
     */
    public void downloadFile(final Context context) {
        Log.d(TAG, "downloadFile: ");
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(downLoadPath + fileName);
        x.http().request(HttpMethod.GET, params, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //实时更新通知栏进度条
                if (type == 0) {
                    notifyNotification(current, total);
                } else if (type == 2) {
                    progressDialog.setProgress((int) (current * 100 / total));
                }
                if (total == current) {
                    Log.d(TAG, "onLoading: ");
                    if (type == 0) {
                        mBuilder.setContentText("下载完成");
                        mNotifyManager.notify(10086, mBuilder.build());
                    } else if (type == 2) {
                        progressDialog.setMessage("下载完成");
                    }
                    if (type == 1) {
                        showDialog(context);
                    } else {
                        installApk(context, new File(downLoadPath, fileName));
                    }
                }
            }
        });
    }

    /**
     * 强制更新时显示在屏幕的进度条
     *
     */
    private void createProgress(Context context) {
        Log.d(TAG, "createProgress: ");
        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在下载...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    /**
     * 创建通知栏进度条
     *
     */
    private void createNotification(Context context) {
        Log.d(TAG, "createNotification: ");
        mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.app_log);
        mBuilder.setContentTitle("版本更新");
        mBuilder.setContentText("正在下载...");
        mBuilder.setProgress(0, 0, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(10086, notification);
    }

    /**
     * 更新通知栏进度条
     *
     */
    private void notifyNotification(long percent, long length) {
        Log.d(TAG, "notifyNotification: ");
        mBuilder.setProgress((int) length, (int) percent, false);
        mNotifyManager.notify(10086, mBuilder.build());
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件
     */
    private void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * @return 当前应用的版本号
     */
    public String getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionCode+"";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断当前网络是否wifi
     */
    public boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public UpdateManager setUrl(String url) {
        this.url = url;
        return this;
    }

    public UpdateManager setType(int type) {
        this.type = type;
        return this;
    }

    public UpdateManager setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
        return this;
    }

    public UpdateManager setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public UpdateManager setIsDownload(boolean download) {
        isDownload = download;
        return this;
    }
}
