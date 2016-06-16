package com.lxl.smsdemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {


	private EditText numbertv;
	private  EditText contenttv;

	private AlarmManager alarmManager;
	private PendingIntent pintent;
	private static  final String TAG="MainActivity";
	private static  final  int PICK_CONTACT=0x123;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}


	public  void init(){
		numbertv=(EditText)findViewById(R.id.number);
		contenttv=(EditText)findViewById(R.id.content);
		alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i=new Intent(MainActivity.this,second.class);
		pintent=PendingIntent.getActivity(MainActivity.this,0,i,0);
		(findViewById(R.id.send)).setOnClickListener(onclicklistener);
		(findViewById(R.id.custom)).setOnClickListener(onclicklistener);

	}






	private View.OnClickListener onclicklistener=new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.send:
					Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent,PICK_CONTACT);
//					queryContacts();
					break;
				case R.id.custom:
					timer();

					break;
			}
		}
	};

	private  void timer(){
		final Calendar cl=Calendar.getInstance();
		new TimerPicker(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar c=Calendar.getInstance();
				c.setTimeInMillis(System.currentTimeMillis());
				Log.d("Timer1",c.getTimeInMillis()+"");
				c.set(Calendar.HOUR_OF_DAY,hourOfDay);
				c.set(Calendar.MINUTE,minute);
				long time=c.getTimeInMillis();
				Log.d("Timer2",time+"");
				Intent intent=new Intent(MainActivity.this,sendService.class);

				intent.putExtra("number",numbertv.getText().toString());
				intent.putExtra("content",contenttv.getText().toString());
				PendingIntent pendingIntent=PendingIntent.getService(MainActivity.this,0,intent,0);
				alarmManager.set(AlarmManager.RTC,c.getTimeInMillis(),pendingIntent);
				Log.d("Timer3","complete");

//				Toast.makeText(getApplicationContext(),"设置完毕"+cl.getTimeInMillis(),Toast.LENGTH_SHORT).show();
			}
		},cl.get(Calendar.HOUR_OF_DAY),cl.get(Calendar.MINUTE),false).show();


	}


	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		String numb="";
		switch (reqCode) {
			case (PICK_CONTACT) :
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					Log.d(TAG, "onActivityResult: "+contactData.getPath()+"==="+contactData.toString()+"=="+data.toString()+"==");
					queryContacts(contactData);

				}
				break;
		}
	}




	public void queryContacts(Uri data){
		Uri uri=data;
		Cursor cursor=getContentResolver().query(uri,null,null,null,null);
		String id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
		Cursor c=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
				ContactsContract.Contacts._ID+"="+id,
				null,
				null);
		while (c.moveToNext()){
			String number=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Log.d(TAG, "queryContacts: "+number);
			numbertv.setText(number);
		}


	}





}
