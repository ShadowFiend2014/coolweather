package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB db, String res){
		if(!TextUtils.isEmpty(res)){
			L.i(res);
			String[] provincies = res.split(",");
			if(provincies != null && provincies.length > 0){
				for(String p : provincies){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					db.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCityResponse(CoolWeatherDB db, String res, int pid){
		if(!TextUtils.isEmpty(res)){
			L.i(res);
			String[] cities = res.split(",");
			if(cities != null && cities.length > 0){
				for(String c : cities){
					City city = new City();
					String[] arr = c.split("\\|");
					city.setCityCode(arr[0]);
					city.setCityName(arr[1]);
					city.setProvinceId(pid);
					db.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCountyResponse(CoolWeatherDB db, String res, int cid){
		if(!TextUtils.isEmpty(res)){
			L.i(res);
			String[] counties = res.split(",");
			if(counties != null && counties.length > 0){
				for(String c : counties){
					County county = new County();
					String[] arr = c.split("\\|");
					county.setCountyCode(arr[0]);
					county.setCountyName(arr[1]);
					county.setCityId(cid);
					db.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject object = new JSONObject(response);
			JSONObject weather = object.getJSONObject("weatherinfo");
			String cityName = weather.getString("city");
			String weatherCode = weather.getString("cityid");
			String temp1 = weather.getString("temp1");
			String temp2 = weather.getString("temp2");
			String desp = weather.getString("weather");
			String publishTime = weather.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, desp, publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context, String cityName, String weatherCode,
			String temp1, String temp2, String desp, String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", desp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}
