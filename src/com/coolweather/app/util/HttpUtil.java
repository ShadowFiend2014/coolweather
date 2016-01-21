package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	public static void sendHttpRequest(final String addr, final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpURLConnection conn = null;
				try{
					URL url = new URL(addr);
					conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					InputStream in = conn.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					StringBuilder result = new StringBuilder();
					String line = null;
					while((line = br.readLine()) != null){
						result.append(line);
					}
					if(listener != null){
						L.i(result.toString());
						listener.onFinish(result.toString());
					} else {
						L.e("listener null");
					}
				} catch (Exception e) {
					if(listener != null){
						listener.onError(e);
					} else {
						L.e("listener null");
					}
				} finally {
					if(conn != null){
						conn.disconnect();
					}
				}
			}
			
		}).start();
	}
	
}
