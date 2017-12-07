package com.test.androidutil.sweetcamera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hanvon.HWCloudManager;
import com.yifan.shufa.R;
import com.yifan.shufa.activity.SynchroVideoPlay;
import com.yifan.shufa.domain.SZBean;
import com.yifan.shufa.global.GlobalContants;
import com.yifan.shufa.sweetcamera.album.ResultBean;
import com.yifan.shufa.sweetcamera.util.BitmapUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * Created by 604406650 on 2016/10/27.
 */
public class ShengZiActivity extends Activity implements View.OnClickListener {
    private HWCloudManager hwCloudManagerTable;
    private DiscernHandler discernHandler;
    String result = null;
    private String imagePath;
    private ProgressDialog pd;
    private TextView testView;
    private Button button1, button2;
    private EditText editText;
    private ImageView btnImage;
    private LinearLayout ll;
    private TextView testView2;
    private LinearLayout result_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initView();
        /**
         * your_android_key 是您在开发者中心申请的android_key 并 申请了云文本识别服务
         * 开发者中心：http://developer.hanvon.com/
         */
        //不打包的key值:22001326-105e-4fd2-af9e-298a6f198d18
//        hwCloudManagerTable = new HWCloudManager(this, "22001326-105e-4fd2-af9e-298a6f198d18");
        // 打包的key值:4fe5d250-bbf5-44d8-9c83-f9254ce3110d
        hwCloudManagerTable = new HWCloudManager(this, "338ba242-e126-4ce3-9b33-42676c7129b0");

        discernHandler = new DiscernHandler();
        // 识别
        testView.setText("");
        pd = ProgressDialog.show(ShengZiActivity.this, "", "正在识别请稍后......");
        DiscernThread discernThread = new DiscernThread();
        new Thread(discernThread).start();
    }

    private void initView() {
        //顶部返回按钮
        btnImage = (ImageView) findViewById(R.id.back);
        button1 = (Button) findViewById(R.id.shengzi_button11);
        button2 = (Button) findViewById(R.id.shengzi_button2);
        // 识别结果
        result_ll = (LinearLayout) findViewById(R.id.shengzishibie_result);
        testView = (TextView) findViewById(R.id.shengzi_result1);
        // 手动输入布局
        ll = (LinearLayout) findViewById(R.id.shengzishibie_ll);
        // 手动输入
        editText = (EditText) findViewById(R.id.shengzi_result11);
        // 提示语句
        testView2 = (TextView) findViewById(R.id.shengzi_result2);
        ImageView resultImage = (ImageView) findViewById(R.id.resultimage);
        imagePath = BitmapUtils.getImagePath();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            resultImage.setImageBitmap(bitmap);
        }

        btnImage.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back://顶部返回按钮
                finish();
                break;
            case R.id.shengzi_button11://重新拍照按钮
                finish();
                break;
            case R.id.shengzi_button2://开始识别按钮
                String tv1 = testView.getText().toString();
                if (tv1 == null || tv1.length() == 0) {
                    result_ll.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);//显示手动输入布局
                    String tv2 = editText.getText().toString();//获取编辑框的文本
                    if (tv2 == null || tv2.length() == 0) {
                        Toast.makeText(ShengZiActivity.this, "请输入汉子", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        getDataFromServer(tv2);
                    }

                } else {
//                    String tv11 = tv1.substring(0, 1);
                    getDataFromServer(tv1);
                }
                break;
            default:
                break;

        }
    }

    public class DiscernThread implements Runnable {

        @Override
        public void run() {
            try {
                /**
                 * 调用汉王云文本识别方法
                 */
                result = hwCloudManagerTable.textLanguage("text", imagePath);// http请求的纯文本
                // activity_result = hwCloudManagerTable.tableLanguage("json",
                // picPath);//json
                // activity_result = hwCloudManagerTable.tableLanguage("superjson",
                // picPath);//增强版json
                // activity_result =
                // hwCloudManagerTable.tableLanguage4Https("text",picPath);//https请求的纯文本
            } catch (Exception e) {
                // TODO: handle exception
            }
            Bundle mBundle = new Bundle();
            mBundle.putString("responce", result);
            Message msg = new Message();
            msg.setData(mBundle);
            discernHandler.sendMessage(msg);
        }
    }

    public class DiscernHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.dismiss();
            Bundle bundle = msg.getData();
            String responce = bundle.getString("responce");
            /**
             * responce: {"code":"434","result":"haven't apply service or service has expired or no times left"}
             * 表示识别次数已用完
             */
            Log.i("responce", responce);
            parsedata(responce);
        }

        private void parsedata(String responce) {
            Gson gson = new Gson();
            ResultBean result = gson.fromJson(responce, ResultBean.class);
            if (result != null) {
                String textResult = result.getTextResult();
                if (textResult != null) {
                    /**
                     * 识别的结果是：例如 "天\r\n" 因为识别的时候，如果出现识别错误，可能会出现多个字，所以需要截取第一个字
                     */
                    String result1 = textResult.substring(0, 1);
                    System.out.println("result1==" + result1);
                    // 显示识别的结果
                    if (result1.equals("{")) {
                        result_ll.setVisibility(View.GONE);
                        testView2.setVisibility(View.VISIBLE);
                        ll.setVisibility(View.VISIBLE);
                    } else {
                        testView.setText(result1);
                    }
                    // getDataFromServer(testView.getText().toString());
                } else {
                    result_ll.setVisibility(View.GONE);
                    testView2.setVisibility(View.VISIBLE);
                    testView2.setText("    今日识别汉子的次数已用完,明日再识别吧！");
                    ll.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 从服务器获取数据
     */
    public void getDataFromServer(String result) {


        try {
            RequestParams params = new RequestParams(GlobalContants.SERVER_URL + "/app/works.php?ac=getvediobyword&username="
                    + GlobalContants.username + "&sz=" + result);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //解析result
                    if (result == null) {
                        return;
                    }
                    parseData2(result);
                }

                //请求异常后的回调方法
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    testView2.setVisibility(View.VISIBLE);
                    testView2.setText("连接服务器失败！");
                }

                //主动调用取消请求的回调方法
                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        } catch (Exception e) {
            // 显示提示语句
            testView2.setVisibility(View.VISIBLE);
            testView2.setText("此生字不在服务器中！");
            ll.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    protected void parseData2(String result2) {
        Gson gson = new Gson();
        SZBean szBean = gson.fromJson(result2, SZBean.class);
        if (szBean != null) {
            String error = szBean.getError();
            if (error == null) {
                String path = szBean.getPath();// 生子的视频路径
                String sz = szBean.getSz();// 生子
                String file_id = szBean.getFile_id();// 生字id

                Intent intent = new Intent(ShengZiActivity.this, SynchroVideoPlay.class);
                Bundle boundle = new Bundle();
                boundle.putString("path", path);
                boundle.putString("sz", sz);
                boundle.putString("file_id", file_id);
                boundle.putInt("boundle", 2);
                intent.putExtras(boundle);
                startActivity(intent);
                finish();
            } else {
                testView2.setVisibility(View.VISIBLE);
                testView2.setText("此生字不在服务器中！");
                ll.setVisibility(View.VISIBLE);
            }
        }
    }


}
