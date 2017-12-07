package com.test.androidutil.utils;


import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;
import java.util.Set;


/**
 * author：Lynn on 2016/9/7 16:58
 * <p/>
 * E-mail：lynn_47253@sina.com
 */

public class HttpRequestToServer {
    private String result = "HttpRequestFailed";
    /**
     * 第三步：创建一个变量映射调用者传过来的接口
     */
    private HttpRequest_ServerfeedbackResult mylistener;

    /**
     * 第一步定义接口
     */
    public interface HttpRequest_ServerfeedbackResult {
        public void onFeedbackResult(String result);
    }

    /**
     * 第二步暴露方法给调用者
     *
     * @param listener
     */
    public void setonFeedbackResultListener(HttpRequest_ServerfeedbackResult listener) {
        mylistener = listener;
    }

    public void getDataFromServer_Get(String url) {
        HttpUtils utils = new HttpUtils();
        utils.configCurrentHttpCacheExpiry(0);
        utils.configTimeout(15 * 1000);// 连接超时  //指的是连接一个url的连接等待时间。
        // 使用xutils发送请求
        utils.send(HttpMethod.GET, url , new RequestCallBack<String>() {

            // 访问成功, 在主线程运行
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                result = (String) responseInfo.result;
                if (mylistener != null) {
                    mylistener.onFeedbackResult(result);
                }
            }
            @Override
            public void onFailure(
                    HttpException arg0,
                    String arg1) {
                if (mylistener != null) {
                    mylistener.onFeedbackResult(result);
                }
            }
        });
    }

    /**
     * 从服务器获取数据
     */

    public void getDataFromServer_Post(String url, Map<String, String> param) {

        RequestParams params = new RequestParams(url);
        if (param.size() == 0) {
            return;
        }
        Set<Map.Entry<String, String>> sets = param.entrySet();

        for (Map.Entry<String, String> set : sets) {
            params.addBodyParameter(set.getKey(), set.getValue());
        }
        params.setConnectTimeout(15*1000);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (mylistener != null) {
                    mylistener.onFeedbackResult(result);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mylistener != null) {
                    mylistener.onFeedbackResult(result);
                }
            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void getDataFromServer_Post2(RequestParams params) {


        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (mylistener != null) {
                    mylistener.onFeedbackResult(result);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mylistener != null) {
                    mylistener.onFeedbackResult(result);
                }
            }

            @Override
            public void onFinished() {

            }
        });
    }


}
