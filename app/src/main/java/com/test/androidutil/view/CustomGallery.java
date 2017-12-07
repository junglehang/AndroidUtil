package com.test.androidutil.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class CustomGallery extends Gallery {
    /**
     * Gallery的中心点
     */
    private int galleryCenterPoint = 0;
    /**
     * 摄像机对象
     */
    private Camera mCamera;
    private Matrix mMatrix ;

    public CustomGallery(Context context) {
        super(context);
        // 启动getChildStaticTransformation
        setStaticTransformationsEnabled(true);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

    public CustomGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 启动getChildStaticTransformation
        setStaticTransformationsEnabled(true);
        mCamera = new Camera();
        mMatrix = new Matrix();
    }


    /**
     * 当Gallery的宽和高改变时回调此方法，第一次计算gallery的宽和高时，也会调用此方法
     */
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        // TODO Auto-generated method stub
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        galleryCenterPoint = getCenterOfCoverflow();
//
//    }

    /**
     * 返回gallery的item的子图形的变换效果
     *
     * @param t 指定当前item的变换效果
     */
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        int viewCenterPoint = getCenterOfView(child); // view的中心点
        int rotateAngle = 0; // 旋转角度，默认为0

        /*
         *如果view的中心点等于gallery中心，表示当前选中的图片，使其放大
         *否侧其他的图片都保持原大小不变
         * 这里是选中图片放大关键代码，也就这一句。
         */

        // 设置变换效果前，需要把Transformation中的上一个item的变换效果清除
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX); // 设置变换效果的类型为矩阵类型
        //设置选中的item的宽高
//        if(viewCenterPoint == galleryCenterPoint) {
//            child.setLayoutParams(new LayoutParams(80, 50));
//        }
        final float offset = calculateOffsetOfCenter(child);
        //transformViewRoom(child, t, offset);
        //startTransformationItem((ImageView) child, rotateAngle, t);
        return true;
    }

    //获取父控件中心点 X 的位置
    protected int getCenterOfCoverflow() {
        return ((getWidth() - getPaddingLeft() - getPaddingRight()) >> 1) + getPaddingLeft();
    }
    //获取 child 中心点 X 的位置
    protected int getCenterOfView(View view) {
        return view.getLeft() + (view.getWidth() >> 1);
    }

    //计算 child 偏离 父控件中心的 offset 值， -1 <= offset <= 1
    protected float calculateOffsetOfCenter(View view) {
        final int pCenter = getCenterOfCoverflow();
        final int cCenter = getCenterOfView(view);

        float offset = (cCenter - pCenter) / (pCenter * 1.0f);
        offset = Math.min(offset, 1.0f);
        offset = Math.max(offset, -1.0f);

        return offset;
    }

    //transformViewRoom(child, t, offset); 根据 offset 值设置 t 的不同效果值，比如 alpha 效果， 平移效果（立体效果），旋转效果，代码如下。
    private void transformViewRoom(View child, Transformation t, float race) {
//        Camera mCamera = new Camera();
        mCamera.save();
        final Matrix matrix = t.getMatrix();
        final int halfHeight = child.getMeasuredHeight() >> 1;
        final int halfWidth = child.getMeasuredWidth() >> 1;

        // 平移 X、Y、Z 轴已达到立体效果
        mCamera.translate(-race * 50, 0.0f, Math.abs(race) * 200);
        //也可设置旋转效果
        mCamera.getMatrix(matrix);
        //以 child 的中心点变换
        matrix.preTranslate(-halfWidth, -halfHeight);
        matrix.postTranslate(halfWidth, halfHeight);
        mCamera.restore();
        //设置 alpha 变换
//        t.setAlpha(1 - Math.abs(race));
    }

    /*
    通过在画 child 之前对 Canvas 进行必要的变换，如上 Transformation 变换类似，
    重载 drawChild 方法，相关代码如下。（注：此对Android4.1及以上版本，
    对于4.1以下版本还是用老方法比较OK的）*/
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        // TODO Auto-generated method stub
        boolean ret;
        //Android SDK 4.1
        if (android.os.Build.VERSION.SDK_INT > 15) {
            final float offset = calculateOffsetOfCenter(child);
            getTransformationMatrix(child, offset);

//            child.setAlpha(1 - Math.abs(offset));

            final int saveCount = canvas.save();
            canvas.concat(mMatrix);
            ret = super.drawChild(canvas, child, drawingTime);
            canvas.restoreToCount(saveCount);
        } else {
            ret = super.drawChild(canvas, child, drawingTime);
        }
        return ret;
    }

    void getTransformationMatrix(View child, float offset) {
        final int halfWidth = child.getLeft() + (child.getMeasuredWidth() >> 1);
        final int halfHeight = child.getMeasuredHeight() >> 1;

        mCamera.save();
        mCamera.translate(-offset * 50, 0.0f, Math.abs(offset) * 200);

        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-halfWidth, -halfHeight);
        mMatrix.postTranslate(halfWidth, halfHeight);
    }

}
