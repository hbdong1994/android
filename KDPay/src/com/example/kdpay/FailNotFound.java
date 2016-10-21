package com.example.kdpay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.nsd.NsdManager.RegistrationListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

public class FailNotFound extends Activity {
	private ImageView fail;
	private Integer netstatus;
	private Context mContext;
//	private BroadcastReceiver network;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.failtofound);
		mContext = this;
		Intent getUrl = ((Activity) mContext).getIntent();
		Bundle bundle = getUrl.getExtras();
		String url = bundle.getString("fail_url");
//		Toast.makeText(mContext, url, Toast.LENGTH_SHORT).show();
		fail= (ImageView) findViewById(R.id.imageView1);
		
		registerReciver();
		
		
		}

public static int GetNetype(Context context)
{ 
    int netType = -1;  
    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
    if(networkInfo==null)
    { 
        return netType; 
    } 
    int nType = networkInfo.getType(); 
    if(nType==ConnectivityManager.TYPE_MOBILE)
    { 
        if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
        { 
            netType = 3; 
        } 
        else
        { 
            netType = 2; 
        } 
    } 
    else if(nType==ConnectivityManager.TYPE_WIFI)
    { 
        netType = 1; 
    } 
    return netType; 
}

public void registerReciver(){
	BroadcastReceiver network = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			netstatus = GetNetype(FailNotFound.this);
//			Toast.makeText(mContext, " "+netstatus+" ", Toast.LENGTH_SHORT).show();
			if(netstatus != -1)
			{
				Toast toast = Toast.makeText(getApplicationContext(), "加载网络中···跳转首页中···", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
				 Intent intents = new Intent(FailNotFound.this,MainActivity.class);
				 startActivity(intents);
				 finish();		 
			}
			
		}
	};
	IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	this.registerReceiver(network, filter);
}
}
