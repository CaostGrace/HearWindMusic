package cn.onegroup.mobile1603.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.onegroup.mobile1603.R;

/**
 * Created by CaostGrace on 2017/12/18 0018.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class RotateCircleImageView extends ImageView {

    private Context context;
    public RotateCircleImageView(Context context) {
        this(context, null);
    }

    public RotateCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RotateCircleImageView, defStyleAttr, 0);//用这个类获得自定义的属性




        paint.setColor(typedArray.getColor(R.styleable.RotateCircleImageView_circle_back_color,
                Color.BLACK));
        tempImage = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(
                R.styleable.RotateCircleImageView_image, R.mipmap.ic_launcher));
        bkWidth = typedArray.getDimensionPixelSize(R.styleable.
                        RotateCircleImageView_circle_back_width,
                dip2px(context, 0));//黑色边框的宽度,DensityUtils是我的一个工具类，将dp转换成px的


        rotateSD = typedArray.getFloat(R.styleable.RotateCircleImageView_rotate_sd, 0.8f);
        rotate_fx = typedArray.getInt(R.styleable.RotateCircleImageView_rotate_fx, 0);
        isRotate = typedArray.getBoolean(R.styleable.RotateCircleImageView_isRotate, true);
    }


    private Bitmap image;
    private Bitmap tempImage;
    private Paint paint;
    private int bkWidth;//黑色圆边框的宽度
    private int rotate_fx=0;//旋转方向  0=顺时针 1=逆时针
    private float rotateSD = 0.8f;//每次旋转的角度--建议范围0.1f-1，否则会抖动
    private boolean isRotate = false;//控制是否旋转


    public void setTempImage(Bitmap tempImage) {
        this.image = tempImage;
        if (tempImage.getHeight() < tempImage.getWidth()) {
            //这里用Bitmap类提供的缩放方法把图片缩放成指定大小,如果图片高度比宽度小，则强制拉伸
            image = Bitmap.createScaledBitmap(tempImage, yy_width - bkWidth,
                    yy_width - bkWidth, false);
        } else {

            //这里用Bitmap类提供的缩放方法把图片缩放成指定大小（宽度等于预测的宽度，高度按比例缩放）
            //该方法根据参数的宽高强制缩放图片，所以这里根据宽度算出缩放后的高度
            image = Bitmap.createScaledBitmap(tempImage, yy_width - bkWidth,(int) (tempImage.getHeight() /
                    (((float) tempImage.getWidth()) / yy_width) - bkWidth), false);
        }

        invalidate();
    }

    int yy_width;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);//分别拿到宽高的大小和测量模式
        int mWidth;//最终宽度
        int mHeight;//最终高度
        yy_width = widthSize;//预测宽度，先假设它等于指定大小或填充窗体
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;//如果是指定大小或填充窗体（以后直接说成指定大小）,直接设置最终宽度

        } else {
            yy_width=tempImage.getWidth();//如果是包裹内容，则预测宽度等于图片宽度
            mWidth = yy_width + getPaddingLeft() + getPaddingRight();//最终宽度等于预测宽度加 左右Padding宽度
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;//同上
        } else {
            mHeight = getPaddingTop() + getPaddingBottom() + yy_width;//最终高度等于预测宽度加 上下Padding宽度
            //目的是让控件的宽高相等，但Padding是可以由用户自由指定的，所以再加上padding
        }
        if (tempImage.getHeight() < tempImage.getWidth()) {
            //这里用Bitmap类提供的缩放方法把图片缩放成指定大小,如果图片高度比宽度小，则强制拉伸
            image = Bitmap.createScaledBitmap(tempImage, yy_width - bkWidth,
                    yy_width - bkWidth, false);
        } else {

            //这里用Bitmap类提供的缩放方法把图片缩放成指定大小（宽度等于预测的宽度，高度按比例缩放）
            //该方法根据参数的宽高强制缩放图片，所以这里根据宽度算出缩放后的高度
            image = Bitmap.createScaledBitmap(tempImage, yy_width - bkWidth,(int) (tempImage.getHeight() /
                    (((float) tempImage.getWidth()) / yy_width) - bkWidth), false);
        }
        setMeasuredDimension(mWidth, mHeight);//设置View的宽高，测量结束
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(getWidth() / 2, getWidth() / 2 , getWidth() / 2, paint);//绘制黑色圆
        canvas.drawBitmap(getCircleBitmap(image, image.getWidth(), rotateSD),
                getWidth() / 2 - image.getWidth() / 2,
                getHeight() / 2 - image.getWidth() / 2, paint);//绘制圆形图片
        if (isRotate) {
            handler.postDelayed(runnable, 16);//16毫秒后启动子线程
        }
    }


    private Bitmap bitmap;
    private boolean isCreateBitmap = false;
    private Canvas canvas;
    private PorterDuffXfermode pdf;
    private Paint bitmapPaint;

    private Bitmap getCircleBitmap(Bitmap image, int width, float rotate) {
        if (!isCreateBitmap) {//节约资源所以这些代码只需要执行一次
            bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(true);//抗锯齿
            bitmapPaint.setDither(true);//忘了是啥....反正效果好点
            bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);//创建一个指定宽高的空白bitmap
            isCreateBitmap = true;
            canvas = new Canvas(bitmap);//用那个空白bitmap创建一个画布
            canvas.drawCircle(width / 2, width / 2, width / 2, bitmapPaint);//在画布上画个圆
            pdf = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);//创建一个混合模式为保留后者相交的部分
        }
        bitmapPaint.setXfermode(pdf);//设置混合模式
        if (rotate_fx==0) {
            canvas.rotate(rotate, width / 2, width / 2);//顺时针
        } else {//旋转画布：意思是下一次绘制的内容会被旋转这么多个角度

            canvas.rotate(-rotate, width / 2, width / 2);//逆时针
        }
        canvas.drawBitmap(image, 0, 0, bitmapPaint);//绘制图片，（图片会被旋转）
        bitmapPaint.setXfermode(null);
        return bitmap;//这个bitmap在画布中被旋转，画圆，返回后就是一个圆形的bitmap
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();//刷新界面
        }
    };

    public void startRotate() {//开始旋转
        if (!isRotate) {
            this.isRotate = true;
            invalidate();
        }
    }

    public void stopRotate() {//暂停旋转
        isRotate = false;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
