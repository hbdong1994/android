package com.example.kdpay;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.example.kdpay.R.drawable;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private WebView webview;
	private ImageView image;
	private LoadingView loading;
	
	int dialog_count = 0;
	//实现固定时间内点击两次按钮退出程序
	private  long exit_time = 0;
	
	private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//启用窗口特征消除标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	
		setContentView(R.layout.activity_main);
		SharedPreferences setting = getSharedPreferences("first", 0);
		Boolean user_first = setting.getBoolean("FIRST", true);
		
		webview = (WebView) findViewById(R.id.webView1);
		
		image = (ImageView) findViewById(R.id.imageView1);
		image.setBackgroundResource(R.drawable.ic_welcome);
		
		
		
	    //设置自定义进度条
		loading = (LoadingView) findViewById(R.id.loading);
		loading.getBackground().setAlpha(40);
		loading.setVisibility(View.INVISIBLE);
		//欢迎界面
		new Handler().postDelayed(new Runnable(){
			
            @Override
            public void run() {
            			image.setVisibility(View.VISIBLE);
            			webview.loadUrl("http://m.kdpay.com/#page1");
            }    
        }, 2000);
		//设置webview属性
		webview.getSettings().setJavaScriptEnabled(true);//允许JS
		webview.getSettings().setSupportZoom(true);
		webview.requestFocus();//使页面获得焦点
		webview.getSettings().setUseWideViewPort(true);// 这个很关键 ，任意比例缩放
		webview.getSettings().setAllowFileAccess(true);   //获取本地文件访问权限  登录cookie时使用，免登录
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadsImagesAutomatically(true);//支持图片自动加载
		webview.getSettings().setAppCacheEnabled(true);

		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);// 支持内容重新布局
		webview.getSettings().setLoadWithOverviewMode(true); //支持缩放到屏幕大小
		
		//判断用户是否第一次启动，不启用缓存
		if(user_first)
		{
			setting.edit().putBoolean("FIRST", false).commit();  
			webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//			Toast.makeText(getApplicationContext(), "第一次启动", Toast.LENGTH_SHORT).show();
		}
		else 
		{
			webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//设置缓存
//			Toast.makeText(getApplicationContext(), "不是第一次启动", Toast.LENGTH_SHORT).show();
		}
		
//		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.setWebViewClient(new WebViewClient(){
		  @Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			Toast toast =Toast.makeText(getApplicationContext(), "哭~找不到网络了··", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		    Intent intent = new Intent(MainActivity.this,FailNotFound.class);
		    intent.putExtra("fail_url", failingUrl);
		    startActivity(intent);
		    if(webview.canGoBack())
		    {
		    	webview.goBack();
		    }
		    else 
		    {
		    	finish();
		    }
//		    
//		    finish();
//			view.loadUrl("file:///android_asset/err.html");
			 
		    
		}
		  /*****
		   * 1.通过sdk版本判断 是否是4.4以上，若不是，则通过游览器打开
		   * 
		   * 
		   */
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {
				// TODO Auto-generated method stub
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
				{
					Toast toast  = Toast.makeText(getApplicationContext(),"您的安卓版本过低，正在跳转游览器打开。。", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					toast.show();
					Intent i =new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
					return false;	
				}
				else {
				if(url.startsWith("http:") || url.startsWith("https:"))
				{
					 view.loadUrl(url);
					 return true;	
				}
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				startActivity(intent);
			    return true;
			}
				
			}
		});	
		
		
		
		webview.setWebChromeClient(new WebChromeClient(){
			  //关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。

			// For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
//                i.setType("/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @Override
            public boolean onShowFileChooser(WebView webView,  
            	          ValueCallback<Uri[]> filePathCallback,  
            		                   FileChooserParams fileChooserParams) {  
            		                if (uploadMessage != null)  
            	                 return false;  
            		                uploadMessage = filePathCallback;  
            		              Intent i = new Intent(Intent.ACTION_GET_CONTENT);  
            		              i.addCategory(Intent.CATEGORY_OPENABLE);  
            		              i.setType("*/*");  
            		              startActivityForResult(Intent.createChooser(i, "文件选择"),  
            		            		  FILECHOOSER_RESULTCODE);  
            		               return true;  
            		           }  


            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
//                i.setType("file/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if(newProgress == 100)
				{
					//加载完成

					loading.setVisibility(View.INVISIBLE);
					image.setVisibility(View.INVISIBLE);

					if(webview.copyBackForwardList().getCurrentIndex() != -1 )
					{
						image.setBackgroundResource(0);
					}
//					Toast.makeText(getApplicationContext(), webview.copyBackForwardList().getCurrentIndex()+"", Toast.LENGTH_SHORT).show();
					
				}
				else 
				{
					loading.setVisibility(View.VISIBLE);
					loading.setProgress(newProgress);
					image.setVisibility(View.VISIBLE);

				}
				super.onProgressChanged(view, newProgress);
			}
			
		});		
		
	
		
		
		
	}
	//判断点击事件
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode == KeyEvent.KEYCODE_BACK)
	{
		
		//获取当前webview的url
	    String url = webview.getUrl();
	    url = url.toLowerCase();
	    //首页判断
   	    Pattern goIndex = Pattern.compile("/#page[2-9]");
   	    Pattern exit = Pattern.compile("/#page1");
   	    Pattern info = Pattern.compile("/userinfo");
		
   	    Matcher m  = goIndex.matcher(url);
   	    Matcher m_exit = exit.matcher(url);
   	    Matcher m_info = info.matcher(url);
   	   
//		Toast.makeText(getApplicationContext(), webview.copyBackForwardList().getCurrentIndex()+"", Toast.LENGTH_SHORT).show();
		//获取webview历史页面总数 
   	    int size = webview.copyBackForwardList().getSize();
		     if(webview.canGoBack())
		     {	 
		    	 //如果是首页，点击两次后退键退出程序
		    	  if(m_exit.find())
				     {
				    	 sureExit();
				    	 return false;
				     }
		    
			    	 if(m.find())
			    	 {
			    		 //测试发现 第一打开的页面是-1
	//		    		 Toast.makeText(getApplicationContext(), webview.copyBackForwardList().getCurrentIndex()+"--->"+webview.copyBackForwardList().getCurrentItem().getUrl(), Toast.LENGTH_SHORT).show();		
	//		    		 webview.go(size-2); 
			    		 webview.goBackOrForward(-size+1);
			    		 return true;
			    	 }
		    	 //如果是个人信息界面，后退返回到首页
			    	 if(m_info.find())
				     {
//				    	 Toast.makeText(getApplicationContext(), "can goback-->"+webview.copyBackForwardList().getCurrentIndex(), Toast.LENGTH_SHORT).show();
//				    	 if(webview.copyBackForwardList().getCurrentIndex() == 1 && webview.canGoBack())
//				    	 {
//				    		  webview.goBack();
//				    		  return true;
//				    	 }
//				    	 else {
//				    		 webview.goBackOrForward(-size+1);
//				    		 return true;
//				    	 }
				    	 webview.loadUrl("http://m.kdpay.com/#page1");
				    	 return true;
				    	 
				     }
			    	 webview.goBack();
			    	 return true;
		     }
		     if(m_exit.find())
		     {	 
		    	 sureExit();
		    	 return false;
		     }
		     if(m_info.find())
		     {
//		 		Toast.makeText(getApplicationContext(), "can not  goback", Toast.LENGTH_SHORT).show();
		 		 webview.loadUrl("http://m.kdpay.com/#page1");
		    	 return true;
		     } 
	
	}
  	 return super.onKeyDown(keyCode, event);
	
}


@Override
public void onActivityResult(int requestCode, int resultCode, Intent intent)
{

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
        if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (uploadMessage == null)
                return;
            uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            uploadMessage = null;
        }
    }
    else if (requestCode == FILECHOOSER_RESULTCODE)
    {
        if (null == mUploadMessage)
            return;
        // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
        // Use RESULT_OK only if you're implementing WebView inside an Activity
        Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
    }
    else
        Toast.makeText(getBaseContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
}


//点击两次按钮退出程序
public void sureExit(){
	if((System.currentTimeMillis() - exit_time) > 2000)
	{
//		Toast.makeText(getApplicationContext(), exit_time+"", Toast.LENGTH_SHORT).show();
		Toast.makeText(getApplicationContext(), "再按一次退出程序~~", Toast.LENGTH_SHORT).show();
		exit_time = System.currentTimeMillis();
	}
	else 
	{
		finish();
		System.exit(0);
	}
}




	
}
