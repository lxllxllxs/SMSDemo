package com.lxl.smsdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
	private  Notification.Builder builder;
	private SharedPreferences sp;

	@Override
	public void onCreate() {
		super.onCreate();

		builder=new Notification.Builder(this);
		builder.setContentTitle("发送信息");
		builder.setContentText("正在发送...");
		builder.setAutoCancel(false);
		builder.setTicker("发送短信");
		builder.setSmallIcon(R.drawable.icon_lauch);
		PendingIntent pendingIntent=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);
		builder.setContentIntent(pendingIntent);
		sp=getSharedPreferences("smsDemo",MODE_PRIVATE);
		startForeground(1,builder.build());
		Log.d(TAG, "onCreate: ");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String number=sp.getString("number","0");
		String content=sp.getString("content","0");
		Log.d(TAG, "onStartCommand: number"+number+"CONTENt"+content);

		sendMessage(number,content);
		return  START_NOT_STICKY ;
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	/*
	* 发送短信
	* */
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

	@Override
	public void onDestroy() {
		unregisterReceiver(sendR);
		unregisterReceiver(deliverR);
		super.onDestroy();
	}

	/*
	*
	* */
	private BroadcastReceiver sendR=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()){
				case  Activity.RESULT_OK:
					Toast.makeText(context,"发送成功",Toast.LENGTH_SHORT).show();
					Log.d(TAG, "onReceive:\"发送成功\" ");
					getSP();
					stopSelf();
					break;

				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				case SmsManager.RESULT_ERROR_NO_SERVICE:
				case SmsManager.RESULT_ERROR_NULL_PDU:
				case SmsManager.RESULT_ERROR_RADIO_OFF:
				//发送失败
					Toast.makeText(context,"send failure",Toast.LENGTH_SHORT).show();
					Log.d(TAG, "onReceive 发送失败");
					getSP();
					stopSelf();
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


	public  void getSP(){
		SharedPreferences.Editor ed=getSharedPreferences("smsDemo",Context.MODE_PRIVATE).edit();
		ed.putBoolean("task",false);
		ed.commit();
		Button btn=(Button)View.inflate(this,R.layout.activity_main,null).findViewById(R.id.custom);
		btn.setText("定时");
	}

}
