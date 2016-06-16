package com.lxl.smsdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/6/14.
 */
public class sendService extends Service {
	@Nullable
	private AlarmManager alarmManager;
	private  static  final String TAG="sendService";
	private  static final String ACTION_SEND="com.lxl.smsdemo.send";
	private static  final String ACTION_DELIVER="com.lxl.smsdemo.deliver";
	@Override
	public void onCreate() {
		super.onCreate();
/*
		Notification.Builder builder=new Notification.Builder(this);
		builder.setContentTitle("SMSDemo");
		builder.setContentText("running");
		builder.setAutoCancel(false);
		builder.setTicker("Foreground Service Start");
		builder.setSmallIcon(R.drawable.icon_lauch);
		PendingIntent pendingIntent=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
		builder.setContentIntent(pendingIntent);
		startForeground(1,builder.build());
		Log.d("sendService","finsh");*/

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String number=intent.getStringExtra("number");
		String content=intent.getStringExtra("content");
		Log.d(TAG, "onStartCommand: number"+number+"CONTENt"+content);
		sendMessage(number,content);
		return  START_NOT_STICKY ;
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	private void sendMessage(String phoneNumber,String message) {
		//判断输入的phoneNumber是否为合法电话号码
		if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
			PendingIntent pintent=PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(ACTION_SEND),0);
			PendingIntent dintent=PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(ACTION_DELIVER),0);

			registerReceiver(sendR,new IntentFilter(ACTION_SEND));
			registerReceiver(deliverR,new IntentFilter(ACTION_DELIVER));

			SmsManager smsmanger=SmsManager.getDefault();
			smsmanger.sendTextMessage(phoneNumber,null,message,pintent,dintent);
		}
	}



	private BroadcastReceiver sendR=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()){
				case  Activity.RESULT_OK:
					Toast.makeText(context,"发送成功",Toast.LENGTH_SHORT).show();
					Log.d(TAG, "onReceive:\"发送成功\" ");
					stopSelf();
					break;

				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				case SmsManager.RESULT_ERROR_NO_SERVICE:
				case SmsManager.RESULT_ERROR_NULL_PDU:
				case SmsManager.RESULT_ERROR_RADIO_OFF:
				//发送失败
					Toast.makeText(context,"send failure",Toast.LENGTH_SHORT).show();
					Log.d(TAG, "onReceive 发送失败");
					break;
			}


		}
	};


	private BroadcastReceiver deliverR=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()){

				case Activity.RESULT_OK:
					Toast.makeText(context,"对方已接受",Toast.LENGTH_SHORT).show();
					Log.d(TAG, "onReceive 已接受");
					break;

				case Activity.RESULT_CANCELED:
					Toast.makeText(context,"接受失败",Toast.LENGTH_SHORT).show();
					Log.d(TAG, "onReceive 接受失败");
					break;
			}
		}
	};

}
