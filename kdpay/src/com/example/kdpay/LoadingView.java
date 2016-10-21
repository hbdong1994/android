package com.example.kdpay;

import java.io.InputStream;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.jar.Attributes;

import com.example.kdpay.R;
import com.example.kdpay.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
/****
 * 
 * @author hbdong
 * @class 自定义进度试图
 */
public class LoadingView extends View{
	// 画实心圆的画笔  
	       private Paint mCirclePaint;  
	        // 画圆环的画笔  
	       private Paint mRingPaint;  
	       // 画字体的画笔  
	        private Paint mTextPaint;  
	        
	        //画图片的画笔
	        private Paint mBmpPaint;
	        
	        //bitmap
	        private Bitmap bmp ;
	       // 圆形颜色  
	        private int mCircleColor;  
	        // 圆环颜色  
	        private int mRingColor;  
	       // 半径  
	       private float mRadius;  
	        // 圆环半径  
	        private float mRingRadius;  
	       // 圆环宽度  
	        private float mStrokeWidth;  
	       // 圆心x坐标  
	        private int mXCenter;  
	        // 圆心y坐标  
	        private int mYCenter;  
	        // 字的长度  
	        private float mTxtWidth;  
	         // 字的高度  
	         private float mTxtHeight;  
	         
	         private float mBmpWidth;
	         
	         private float mBmpHeight;
	         // 总进度  
	         private int mTotalProgress = 100;  
	         private int mProgress; 
	         /****
	          * 
	          * @param context 上下文指针
	          * @param attrs 自定义资源文件
	          */
	         public LoadingView(Context context,AttributeSet attrs)
	         {
	        	 super(context,attrs);
	        	 initAttrs(context,attrs);
	        	 initVariable();
	         }

	         /*****
	          * 加载本地自定义资源文件，参数赋值
	          * @param context 上下文对象
	          * @param attrs  资源文件
	          */
			private void initAttrs(Context context, AttributeSet attrs) {
				// TODO Auto-generated method stub
				TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,  
							R.styleable.LoadingView,0, 0);
				mRadius = typedArray.getDimension(R.styleable.LoadingView_radius,80);
				mStrokeWidth = typedArray.getDimension(R.styleable.LoadingView_strokeWidth, 10);
				mCircleColor = typedArray.getColor(R.styleable.LoadingView_circleColor ,0xFFFFFFFF);
				mRingColor = typedArray.getColor(R.styleable.LoadingView_ringColor, 0xFFFFFFFF);
	
				
				mRingRadius = mRadius + mStrokeWidth / 2 ;
			}

			/****
			 * 初始画笔对象
			 * 外圈画笔
			 * 颜色填充画笔
			 * 位图画笔
			 */
			private void initVariable() {
				// TODO Auto-generated method stub
				//设置外形画笔
				mCirclePaint =new Paint();
				mCirclePaint.setAntiAlias(true);//设置画笔锯齿效果
				mCirclePaint.setColor(mCircleColor);//设置画笔颜色
				mCirclePaint.setStyle(Paint.Style.FILL);//设置画笔风格 （空心|实心）
				
				//设置填充画笔
				mRingPaint = new Paint();
				mRingPaint.setAntiAlias(true);
				mRingPaint.setColor(mRingColor);
				mRingPaint.setStyle(Paint.Style.STROKE);
				mRingPaint.setStrokeWidth(mStrokeWidth);
				
				//画笔paint对象设置
//				mTextPaint = new Paint();
//				mTextPaint.setAntiAlias(true);
//				mTextPaint.setStyle(Paint.Style.FILL);
//				mTextPaint.setARGB(255,255,255,255);
//				mTextPaint.setTextSize(mRadius/2);
//				
//				FontMetrics fm =mTextPaint.getFontMetrics();
//				mTxtHeight = (int)Math.ceil(fm.descent - fm.ascent);
				
				//设置位图画笔
				mBmpPaint = new Paint();
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.kd1);
				mBmpWidth = bmp.getWidth();
				mBmpHeight = bmp.getHeight();
	
			}
			/*******
			 * @param canvas 画布对象
			 */
			protected void onDraw(Canvas canvas)
			{
				mXCenter = getWidth() /2 ;
				mYCenter = getHeight() /2 ;
				 //画外部圆
				canvas.drawCircle(mXCenter, mYCenter,mRadius , mCirclePaint);
				//将自定义图像画在中间
				canvas.drawBitmap(bmp, mXCenter - mBmpWidth/2, mYCenter - mBmpHeight/2,mBmpPaint);
				
				if(mProgress > 0)
				{
					RectF oval = new RectF();
					oval.left = (mXCenter - mRingRadius);
					oval.top = (mYCenter - mRingRadius);
					oval.right = mRingRadius  + mXCenter;
					oval.bottom = mRingRadius + mYCenter;
					//drawArc 绘制圆弧形状
					canvas.drawArc(oval, -90, ((float)mProgress/mTotalProgress)*360, false, mRingPaint);
					
//					String txt = mProgress+"%";
//					mTxtWidth = mTextPaint.measureText(txt,0,txt.length());
//					canvas.drawText(txt,mXCenter - mTxtWidth /2, mYCenter+mTxtHeight /4,mTextPaint);
					
					Log.i("x", " "+mXCenter+"");
					Log.i("y", " "+mYCenter +"");
					Log.i("bmpx"," "+ mBmpWidth+" ");
					Log.i("bmpy", " "+mBmpHeight+"");
					
					
					
				}
			}
			
			/***
			 * 设置view的progress值
			 * @param Progress 进度值
			 */
			public void setProgress(int Progress) {
				mProgress= Progress;
				postInvalidate();
			}


}