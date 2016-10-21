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
	//ʵ�̶ֹ�ʱ���ڵ�����ΰ�ť�˳�����
	private  long exit_time = 0;
	
	private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//���ô�����������������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	
		setContentView(R.layout.activity_main);
		SharedPreferences setting = getSharedPreferences("first", 0);
		Boolean user_first = setting.getBoolean("FIRST", true);
		
		webview = (WebView) findViewById(R.id.webView1);
		
		image = (ImageView) findViewById(R.id.imageView1);
		image.setBackgroundResource(R.drawable.ic_welcome);
		
		
		
	    //�����Զ��������
		loading = (LoadingView) findViewById(R.id.loading);
		loading.getBackground().setAlpha(40);
		loading.setVisibility(View.INVISIBLE);
		//��ӭ����
		new Handler().postDelayed(new Runnable(){
			
            @Override
            public void run() {
            			image.setVisibility(View.VISIBLE);
            			webview.loadUrl("http://m.kdpay.com/#page1");
            }    
        }, 2000);
		//����webview����
		webview.getSettings().setJavaScriptEnabled(true);//����JS
		webview.getSettings().setSupportZoom(true);
		webview.requestFocus();//ʹҳ���ý���
		webview.getSettings().setUseWideViewPort(true);// ����ܹؼ� �������������
		webview.getSettings().setAllowFileAccess(true);   //��ȡ�����ļ�����Ȩ��  ��¼cookieʱʹ�ã����¼
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setLoadsImagesAutomatically(true);//֧��ͼƬ�Զ�����
		webview.getSettings().setAppCacheEnabled(true);

		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);// ֧���������²���
		webview.getSettings().setLoadWithOverviewMode(true); //֧�����ŵ���Ļ��С
		
		//�ж��û��Ƿ��һ�������������û���
		if(user_first)
		{
			setting.edit().putBoolean("FIRST", false).commit();  
			webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//			Toast.makeText(getApplicationContext(), "��һ������", Toast.LENGTH_SHORT).show();
		}
		else 
		{
			webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//���û���
//			Toast.makeText(getApplicationContext(), "���ǵ�һ������", Toast.LENGTH_SHORT).show();
		}
		
//		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.setWebViewClient(new WebViewClient(){
		  @Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			Toast toast =Toast.makeText(getApplicationContext(), "��~�Ҳ��������ˡ���", Toast.LENGTH_SHORT);
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
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {
				// TODO Auto-generated method stub
				Pattern protocol = Pattern.compile("/protocol");
				Matcher m_protocol  = protocol.matcher(url);
				if(url.startsWith("http:") || url.startsWith("https:"))
				{
					 
					  return false;	
				}
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
				startActivity(intent);
			    return true;
			}
		});	
		
		
		
		webview.setWebChromeClient(new WebChromeClient(){
			  //�ؼ����룬���º�����û��API�ĵ��ģ�������Eclipse�лᱨ����������@Override�ؼ���������Ļ���

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
            		              startActivityForResult(Intent.createChooser(i, "�ļ�ѡ��"),  
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
					//�������

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
	//�жϵ���¼�
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if(keyCode == KeyEvent.KEYCODE_BACK)
	{
		
		//��ȡ��ǰwebview��url
	    String url = webview.getUrl();
	    url = url.toLowerCase();
	    //��ҳ�ж�
   	    Pattern goIndex = Pattern.compile("/#page[2-9]");
   	    Pattern exit = Pattern.compile("/#page1");
   	    Pattern info = Pattern.compile("/userinfo");
		
   	    Matcher m  = goIndex.matcher(url);
   	    Matcher m_exit = exit.matcher(url);
   	    Matcher m_info = info.matcher(url);
   	   
//		Toast.makeText(getApplicationContext(), webview.copyBackForwardList().getCurrentIndex()+"", Toast.LENGTH_SHORT).show();
		//��ȡwebview��ʷҳ������ 
   	    int size = webview.copyBackForwardList().getSize();
		     if(webview.canGoBack())
		     {	 
		    	 //�������ҳ��������κ��˼��˳�����
		    	  if(m_exit.find())
				     {
				    	 sureExit();
				    	 return false;
				     }
		    
			    	 if(m.find())
			    	 {
			    		 //���Է��� ��һ�򿪵�ҳ����-1
	//		    		 Toast.makeText(getApplicationContext(), webview.copyBackForwardList().getCurrentIndex()+"--->"+webview.copyBackForwardList().getCurrentItem().getUrl(), Toast.LENGTH_SHORT).show();		
	//		    		 webview.go(size-2); 
			    		 webview.goBackOrForward(-size+1);
			    		 return true;
			    	 }
		    	 //����Ǹ�����Ϣ���棬���˷��ص���ҳ
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


//������ΰ�ť�˳�����
public void sureExit(){
	if((System.currentTimeMillis() - exit_time) > 2000)
	{
//		Toast.makeText(getApplicationContext(), exit_time+"", Toast.LENGTH_SHORT).show();
		Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����~~", Toast.LENGTH_SHORT).show();
		exit_time = System.currentTimeMillis();
	}
	else 
	{
		finish();
		System.exit(0);
	}
}




	
}
