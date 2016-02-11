package com.liran.lenovo.circleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * 圆形图片的Imageview
 * Created by LiRan on 2016-02-10.
 */
public class CircleImageView extends ImageView {


    private static final String TAG = "CircleImageView";
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
    private Canvas Bitmapcanvas;
    private Bitmap creatBitmap;
    private float scale;

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
        scale = 0;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);

        mBorderRadius = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_raidus, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS, getResources().getDisplayMetrics()));//默认10dp
        type = typedArray.getInt(R.styleable.CircleImageView_type, TYPE_CIRCLE);//默认是circle

        typedArray.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("TAG", "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 如果类型是圆形，则强制改变view的宽高一致，以小值为准
         */
        if (type == TYPE_CIRCLE)
        {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
            Log.d(TAG, "onMeasure: radius= "+mRadius+" mWidth= "+mWidth);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        Paint p = new Paint();
        //清屏
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        canvas.drawColor(Color.WHITE);

        if (type == TYPE_ROUND) {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius,
                    mBitmapPaint);
            Log.d(TAG, "onDraw: 绘制圆角");
        } else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
            Log.d(TAG, "onDraw: 绘制圆~~");
            // drawSomeThing(canvas);
        }
        Log.d(TAG, "onDraw: 绘制结束");

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 圆角图片的范围
        if (type == TYPE_ROUND) {
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 初始化bitmapshader
     */
    /*private void setUpShader() {

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitamp(drawable);
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (type == TYPE_CIRCLE) {
            scale = mWidth * 1.0f / Math.min(bmp.getWidth(), bmp.getHeight());
        } else if (type == TYPE_ROUND) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight()
                    * 1.0f / bmp.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        //设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);

        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }*/
    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitamp(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的小值
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;

        } else if (type == TYPE_ROUND) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight()
                    * 1.0f / bmp.getHeight());
        }

        Log.d(TAG, "setUpShader: bmp.width= "+bmp.getWidth()+"  bpm.height= "+bmp.getHeight());

        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    /**
     * drawable幢bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitamp(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    public void setBorderRadius(int borderRadius) {
        int pxVal = dp2px(borderRadius);
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE) {
                this.type = TYPE_CIRCLE;
            }
            requestLayout();
        }

    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }


    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state)
                    .getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }

    }

}
