package com.liran.lenovo.circleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * TODO: document your custom view class.
 */
public class RoundImageView extends ImageView {

    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Bitmap mMaskBitmap;

    private Bitmap bitmap;

    private WeakReference<Bitmap> mWeakBitmap;


    /**
     * 图片的类型，圆形or圆角
     */
    private int type;
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;
    /**
     * 圆角大小的默认值
     */
    private static final int BODER_RADIUS_DEFAULT = 10;
    /**
     * 圆角的大小
     */
    private float mBorderRadius;
    private Drawable drawable;

    public RoundImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RoundImageView, defStyle, 0);

        type = a.getInt(R.styleable.RoundImageView_Type, TYPE_CIRCLE);
        mBorderRadius = a.getDimension(R.styleable.RoundImageView_borderRadius, BODER_RADIUS_DEFAULT);

        a.recycle();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (type == TYPE_CIRCLE) {
            int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(width, width);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();

        if (bitmap == null || bitmap.isRecycled()) {

            //拿到drawable
            drawable = getDrawable();
            //获取drawable的宽和高
            int dwidth = drawable.getIntrinsicWidth();
            int dheight = drawable.getIntrinsicHeight();

            if (drawable != null) {

                bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                float scale = 1.0f;

                //创建画布
                Canvas drawCanvas = new Canvas(bitmap);

                if (type == TYPE_ROUND) {
                    scale = Math.max(getWidth() * 1.0f / dwidth, getHeight()
                            * 1.0f / dheight);

                } else {

                    scale = getWidth() * 1.0f / Math.min(dwidth, dheight);
                }
                drawable.setBounds(0, 0, (int) (scale * dwidth), (int) (scale * dheight));
                drawable.draw(drawCanvas);

                if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                    mMaskBitmap = getBitmap();
                }

                // Draw Bitmap.
                mPaint.reset();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(mXfermode);
                //绘制形状
                drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);
                mPaint.setXfermode(null);
                //将准备好的bitmap绘制出来
                canvas.drawBitmap(bitmap, 0, 0, null);
                //bitmap缓存起来，避免每次调用onDraw，分配内存
                mWeakBitmap = new WeakReference<Bitmap>(bitmap);

            }

        }

        //如果bitmap还存在，则直接绘制即可
        if (bitmap != null)
        {
            mPaint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
            return;
        }


    }


    public Bitmap getBitmap() {

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        if (type == TYPE_ROUND)
        {
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),
                    mBorderRadius, mBorderRadius, paint);
        } else
        {
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2,
                    paint);
        }

        return bitmap;

    }


    @Override
    public void invalidate()
    {
        mWeakBitmap = null;
        if (mMaskBitmap != null)
        {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        super.invalidate();
    }
}
