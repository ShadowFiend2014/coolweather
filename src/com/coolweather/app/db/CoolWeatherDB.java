package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.L;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	private final String TAG = "CoolWeatherDB";
	
	public static final String DB_NAME = "cool_weather";
	
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(context, 
				DB_NAME, null, VERSION);
		db = coolWeatherOpenHelper.getWritableDatabase();
	}
	
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	public void saveProvince(Province province){
		if(province != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			long result = db.insert("Province", null, contentValues);
			L.i(TAG, "saveProvince SQLiteDatabase.insert = " + result);
		} else {
			L.e(TAG, "province is null");
		}
	}
	
	public List<Province> getProvinces(){
		List<Province> pList = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				pList.add(province);
			}while(cursor.moveToNext());
		} else {
			L.e(TAG, "Province table is null");
		}
		return pList;
	}
	
	public void saveCity(City city){
		if(city != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			long result = db.insert("City", null, contentValues);
			L.i(TAG, "saveCity SQLiteDatabase.insert = " + result);
		} else {
			L.e(TAG, "city is null");
		}
	}
	
	public List<City> getCities(int provinceId){
		List<City> cList = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, 
				null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				cList.add(city);
			}while(cursor.moveToNext());
		} else {
			L.e(TAG, "City table is null");
		}
		return cList;
	}
	
	public void saveCounty(County county){
		if(county != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("county_name", county.getCountyName());
			contentValues.put("county_code", county.getCountyCode());
			contentValues.put("city_id", county.getCityId());
			long result = db.insert("County", null, contentValues);
			L.i(TAG, "saveCounty SQLiteDatabase.insert = " + result);
		} else {
			L.e(TAG, "county is null");
		}
	}
	
	public List<County> getCounties(int cityId){
		List<County> cList = new ArrayList<County>();
		Cursor cursor = db.query("City", null, "city_id = ?", new String[]{String.valueOf(cityId)}, 
				null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				cList.add(county);
			}while(cursor.moveToNext());
		} else {
			L.e(TAG, "County table is null");
		}
		return cList;
	}
	
}