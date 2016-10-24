package com.example.kdpay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

public class LoadingSwipeRefreshLayout extends SwipeRefreshLayout {
	 WebView webview;

	public LoadingSwipeRefreshLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public LoadingSwipeRefreshLayout(Context context,AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
	}
	
	public ViewGroup getViewGroup() {
		return webview;
	}
	
	public void setViewGroup(WebView viewGroup) {
		this.webview = viewGroup;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Pattern goIndex = Pattern.compile("/(#page[2-9])");
		Matcher  m = goIndex.matcher(this.webview.getUrl());
		if(m.find())
		{
			return false;
		}else{
		return super.onTouchEvent(arg0);
		}

	}
	
	

}
