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
 * @class �Զ��������ͼ
 */
public class LoadingView extends View{
	// ��ʵ��Բ�Ļ���  
	       private Paint mCirclePaint;  
	        // ��Բ���Ļ���  
	       private Paint mRingPaint;  
	       // ������Ļ���  
	        private Paint mTextPaint;  
	        
	        //��ͼƬ�Ļ���
	        private Paint mBmpPaint;
	        
	        //bitmap
	        private Bitmap bmp ;
	       // Բ����ɫ  
	        private int mCircleColor;  
	        // Բ����ɫ  
	        private int mRingColor;  
	       // �뾶  
	       private float mRadius;  
	        // Բ���뾶  
	        private float mRingRadius;  
	       // Բ�����  
	        private float mStrokeWidth;  
	       // Բ��x����  
	        private int mXCenter;  
	        // Բ��y����  
	        private int mYCenter;  
	        // �ֵĳ���  
	        private float mTxtWidth;  
	         // �ֵĸ߶�  
	         private float mTxtHeight;  
	         
	         private float mBmpWidth;
	         
	         private float mBmpHeight;
	         // �ܽ���  
	         private int mTotalProgress = 100;  
	         private int mProgress; 
	         /****
	          * 
	          * @param context ������ָ��
	          * @param attrs �Զ�����Դ�ļ�
	          */
	         public LoadingView(Context context,AttributeSet attrs)
	         {
	        	 super(context,attrs);
	        	 initAttrs(context,attrs);
	        	 initVariable();
	         }

	         /*****
	          * ���ر����Զ�����Դ�ļ���������ֵ
	          * @param context �����Ķ���
	          * @param attrs  ��Դ�ļ�
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
			 * ��ʼ���ʶ���
			 * ��Ȧ����
			 * ��ɫ��仭��
			 * λͼ����
			 */
			private void initVariable() {
				// TODO Auto-generated method stub
				//�������λ���
				mCirclePaint =new Paint();
				mCirclePaint.setAntiAlias(true);//���û��ʾ��Ч��
				mCirclePaint.setColor(mCircleColor);//���û�����ɫ
				mCirclePaint.setStyle(Paint.Style.FILL);//���û��ʷ�� ������|ʵ�ģ�
				
				//������仭��
				mRingPaint = new Paint();
				mRingPaint.setAntiAlias(true);
				mRingPaint.setColor(mRingColor);
				mRingPaint.setStyle(Paint.Style.STROKE);
				mRingPaint.setStrokeWidth(mStrokeWidth);
				
				//����paint��������
//				mTextPaint = new Paint();
//				mTextPaint.setAntiAlias(true);
//				mTextPaint.setStyle(Paint.Style.FILL);
//				mTextPaint.setARGB(255,255,255,255);
//				mTextPaint.setTextSize(mRadius/2);
//				
//				FontMetrics fm =mTextPaint.getFontMetrics();
//				mTxtHeight = (int)Math.ceil(fm.descent - fm.ascent);
				
				//����λͼ����
				mBmpPaint = new Paint();
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.kd1);
				mBmpWidth = bmp.getWidth();
				mBmpHeight = bmp.getHeight();
	
			}
			/*******
			 * @param canvas ��������
			 */
			protected void onDraw(Canvas canvas)
			{
				mXCenter = getWidth() /2 ;
				mYCenter = getHeight() /2 ;
				 //���ⲿԲ
				canvas.drawCircle(mXCenter, mYCenter,mRadius , mCirclePaint);
				//���Զ���ͼ�����м�
				canvas.drawBitmap(bmp, mXCenter - mBmpWidth/2, mYCenter - mBmpHeight/2,mBmpPaint);
				
				if(mProgress > 0)
				{
					RectF oval = new RectF();
					oval.left = (mXCenter - mRingRadius);
					oval.top = (mYCenter - mRingRadius);
					oval.right = mRingRadius  + mXCenter;
					oval.bottom = mRingRadius + mYCenter;
					//drawArc ����Բ����״
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
			 * ����view��progressֵ
			 * @param Progress ����ֵ
			 */
			public void setProgress(int Progress) {
				mProgress= Progress;
				postInvalidate();
			}


}