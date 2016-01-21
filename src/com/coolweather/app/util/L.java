package com.coolweather.app.util;

import android.util.Log;

public class L {
	
	public static final String TAG = "MyLogInfo";
	
	public static final int VERBOSE = 1;
	
	public static final int DEBUG = 2;
	
	public static final int INFO = 3;
	
	public static final int WARN = 4;
	
	public static final int ERROR = 5;
	
	public static final int NO = 6;
	
	public static final int LEVEL = VERBOSE;
	
	public static void v(String tag, String msg){
		if(LEVEL <= VERBOSE){
			Log.i(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if(LEVEL <= DEBUG){
			Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, String msg){
		if(LEVEL <= INFO){
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg){
		if(LEVEL <= WARN){
			Log.i(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if(LEVEL <= ERROR){
			Log.i(tag, msg);
		}
	}
	
	public static void v(String msg){
		if(LEVEL <= VERBOSE){
			Log.i(TAG, msg);
		}
	}
	
	public static void d(String msg){
		if(LEVEL <= DEBUG){
			Log.i(TAG, msg);
		}
	}
	
	public static void i(String msg){
		if(LEVEL <= INFO){
			Log.i(TAG, msg);
		}
	}
	
	public static void w(String msg){
		if(LEVEL <= WARN){
			Log.i(TAG, msg);
		}
	}
	
	public static void e(String msg){
		if(LEVEL <= ERROR){
			Log.i(TAG, msg);
		}
	}
	
}
