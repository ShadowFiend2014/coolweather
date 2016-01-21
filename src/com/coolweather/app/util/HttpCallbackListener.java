package com.coolweather.app.util;

public interface HttpCallbackListener {
	void onFinish(String result);
	void onError(Exception e);
}
