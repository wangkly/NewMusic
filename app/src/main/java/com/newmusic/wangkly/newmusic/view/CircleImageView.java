package com.newmusic.wangkly.newmusic.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class CircleImageView extends AppCompatImageView {

    private float width;

    private float height;

    private float radius;

    private Paint paint;

    private Matrix matrix;



    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true); //抗锯齿
        matrix = new Matrix();//初始化缩放矩阵

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = Math.min(width,height)/2;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setShader(initBitmapShader());//将着色器设置给画笔
        drawCircleBorder(canvas,0, Color.BLACK,radius);
        canvas.save();
        canvas.drawCircle(width / 2, height / 2, radius-150, paint);//使用画笔在画布上画圆
    }



    public void drawCircleBorder(Canvas canvas, int borderWidth, int borderColor, float radius){

        Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setColor(borderColor);
        canvas.drawColor(Color.WHITE);

//        Path path = new Path();
//        path.addCircle(width / 2, height / 2, radius,Path.Direction.CCW);
//        canvas.drawPath(path,mPaint);

        canvas.drawCircle(width / 2, height / 2, radius,mPaint);

    }

    /**
     * 获取ImageView中资源图片的Bitmap，利用Bitmap初始化图片着色器,通过缩放矩阵将原资源图片缩放到铺满整个绘制区域，避免边界填充
     */
    private BitmapShader initBitmapShader() {
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max((width-150) / bitmap.getWidth(), (height-150) / bitmap.getHeight());
        matrix.setScale(scale, scale);//将图片宽高等比例缩放，避免拉伸
        bitmapShader.setLocalMatrix(matrix);
        return bitmapShader;
    }



}
