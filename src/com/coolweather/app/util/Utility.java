package com.coolweather.app.util;

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
	
}
