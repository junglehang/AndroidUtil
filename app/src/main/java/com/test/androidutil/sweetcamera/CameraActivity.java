package com.test.androidutil.sweetcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yifan.shufa.R;
import com.yifan.shufa.ShufaApplication;
import com.yifan.shufa.sweetcamera.ui.MaskView;
import com.yifan.shufa.sweetcamera.util.FileUtil;
import com.yifan.shufa.sweetcamera.widget.SquareCameraContainer;
import com.yifan.shufa.utils.DisplayUtil;

/**
 * 自定义相机的activity
 *
 * @author jerry
 * @date 2015-09-01
 */
public class CameraActivity extends Activity {
    public static final String TAG = "CameraActivity";

    private CameraManager mCameraManager;

    private TextView m_tvFlashLight, m_tvCameraDireation;
    private SquareCameraContainer mCameraContainer;

    /**
     * 这里的宽高是控制中心可见正方形框的大小的
     * 这里的宽高与SquareCameraContainer类中centerSquareScaleBitmap()方法中需要传递的宽高是一致的
     * 由于测试的时候拍照片截图不完全，所以这里设置的值需要设置的比centerSquareScaleBitmap()（值为90）小点
     */
    int DST_CENTER_RECT_WIDTH = 80; // 单位是dip
    int DST_CENTER_RECT_HEIGHT = 80;// 单位是dip

    private int mFinishCount = 2;   //finish计数   当动画和异步任务都结束的时候  再调用finish方法

    private Handler handler = new Handler();
    private MaskView maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraManager = CameraManager.getInstance(this);
        FileUtil.initFolder();//创建文件夹存储图片
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void initView() {
        m_tvFlashLight = (TextView) findViewById(R.id.tv_flashlight);
        m_tvCameraDireation = (TextView) findViewById(R.id.tv_camera_direction);
        mCameraContainer = (SquareCameraContainer) findViewById(R.id.cameraContainer);
        maskView = (MaskView) findViewById(R.id.view_mask);
    }

    void initData() {
        mCameraManager.bindOptionMenuView(m_tvFlashLight, m_tvCameraDireation);
//        mCameraContainer.setImagePath(getIntent().getStringExtra(PATH_OUTIMG));
        mCameraContainer.bindActivity(this);

        if (maskView != null) {
            //适配预览视图的宽高为屏幕的宽度
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ShufaApplication.mScreenWidth, ShufaApplication.mScreenWidth);
            maskView.setLayoutParams(params);
            Rect screenCenterRect = createCenterScreenRect(DisplayUtil.dip2px(this, DST_CENTER_RECT_WIDTH),
                    DisplayUtil.dip2px(this, DST_CENTER_RECT_HEIGHT));
            maskView.setCenterRect(screenCenterRect);
        }
    }

    void initListener() {
        if (mCameraManager.canSwitch()) {
            m_tvCameraDireation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_tvCameraDireation.setClickable(false);
                    mCameraContainer.switchCamera();

                    //500ms后才能再次点击
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            m_tvCameraDireation.setClickable(true);
                        }
                    }, 500);
                }
            });
        }

        m_tvFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraContainer.switchFlashMode();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraContainer != null) {
            mCameraContainer.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraContainer != null) {
            mCameraContainer.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraManager.unbinding();
        mCameraManager.releaseActivityCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //在创建前  释放相机
    }

    /**
     * 对一些参数重置
     */
    public void rest() {
        mFinishCount = 2;
    }

    /**
     * 退出按钮点击
     */
    public void onExitClicked(View view) {
//        onBackPressed();
        finish();
    }

    /**
     * 照相按钮点击
     */
    public void onTakePhotoClicked(View view) {
        mCameraContainer.takePicture();
    }

    /**
     * 提交finish任务  进行计数  都在main Thread
     */

    /**
     * 照完照片 提交
     */
    public void postTakePhoto() {
        mCameraManager.releaseActivityCamera();//释放相机资源

        /**
         * 这里提交照片需要跳转到下一界面
         */
        Intent intent = new Intent(CameraActivity.this, ShengZiActivity.class);
        startActivity(intent);


    }

    /**
     * 生成屏幕中间的矩形
     *
     * @param w 目标矩形的宽度,单位px
     * @param h 目标矩形的高度,单位px
     * @return
     */
    private Rect createCenterScreenRect(int w, int h) {
        int x1 = DisplayUtil.getScreenMetrics(this).x / 2 - w / 2;
        int y1 = DisplayUtil.getScreenMetrics(this).y / 2 - h / 2;
        int x2 = x1 + w;
        int y2 = y1 + h;
        return new Rect(x1, y1, x2, y2);
    }
}
