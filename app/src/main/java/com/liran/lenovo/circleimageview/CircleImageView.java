package com.liran.lenovo.circleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * 圆形图片的Imageview
 * Created by LiRan on 2016-02-10.
 */
public class CircleImageView extends ImageView {


    private int type;
    private static final int TYPE_CIRCLE = 0;
    private static final int TYPE_ROUND = 1;

    /**
     * 默认圆角大小
     */
    private static final int DEFAULT_RADIUS = 10;

    /**
     * 圆角大小
     */
    private int mBorderRadius;

    /**
     * 绘制的paint
     */
    private Paint mBitmapPaint;

    /**
     * 圆角半径
     */
    private int mRadius;

    /**
     * 主要用于缩放
     */
    private Matrix mMatrix;

    /**
     * 渲染图像
     */
    private BitmapShader mBitmapShader;

    /**
     * view的宽度
     */
    private int mWidth;

    private RectF mRoundRect;

    public CircleImageView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMatrix = new Matrix();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);

        mBorderRadius = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_raidus, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));//默认10dp
        type = typedArray.getInt(R.styleable.CircleImageView_type, TYPE_CIRCLE);//默认是circle

        typedArray.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }
}
