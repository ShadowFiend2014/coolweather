package com.coolweather.app;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	
	private LinearLayout weatherInfoLayout;
	
	private TextView cityName;
	
	private TextView publishTime;
	
	private TextView weatherDesp;
	
	private TextView temp1;
	
	private TextView temp2;
	
	private TextView currentDate;
	
	private Button switchCity;
	
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityName = (TextView) findViewById(R.id.city_name);
		publishTime = (TextView) findViewById(R.id.publish_text);
		weatherDesp = (TextView) findViewById(R.id.weather_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		currentDate = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			publishTime.setText("同步中......");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	private void showWeather() {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(preference.getString("city_name", ""));
		publishTime.setText(preference.getString("publish_time", ""));
		weatherDesp.setText(preference.getString("weather_desp", ""));
		temp1.setText(preference.getString("temp1", ""));
		temp2.setText(preference.getString("temp2", ""));
		currentDate.setText(preference.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
	}

	private void queryWeatherCode(String countyCode) {
		String addr = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(addr, "countyCode");
	}

	private void queryFromServer(final String addr, final String type) {
		HttpUtil.sendHttpRequest(addr, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String result) {
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(result)){
						String[] arr = result.split("\\|");
						String weatherCode = arr[1];
						queryWeatherInfo(weatherCode);
					}
				} else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, result);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishTime.setText("同步失败");
					}
					
				});
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.switch_city){
			Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			return;
		}
		if(v.getId() == R.id.refresh_weather){
			publishTime.setText("同步中......");
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = preference.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
		}
	}

	private void queryWeatherInfo(String weatherCode) {
		String addr = "http://www.weather.com.cn/data/cityinfo" + weatherCode + ".html";
		queryFromServer(addr, "weatherCode");
	}
	
}
