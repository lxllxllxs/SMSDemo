package com.lxl.smsdemo;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Created by Administrator on 2016/6/15.
 */
public class TimerPicker extends TimePickerDialog   {

	public TimerPicker(Context context, OnTimeSetListener callBack,
							  int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
		// TODO Auto-generated constructor stub
	}

	public TimerPicker(Context context, int theme,
							  OnTimeSetListener callBack, int hourOfDay, int minute,
							  boolean is24HourView) {
		super(context, theme, callBack, hourOfDay, minute, is24HourView);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onStop() {
		//将此处的super.stop()删除就能够解决问题了
	}

}