package com.liran.lenovo.circleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

    }

    /**
     * 初始化bitmapshader
     */
    private void setUpShader() {

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
    }

    /**
     * drawable幢bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitamp(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        creatBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Bitmapcanvas = new Canvas(creatBitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(Bitmapcanvas);
        
        return creatBitmap;
    }

}
