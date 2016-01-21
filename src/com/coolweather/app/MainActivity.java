package com.coolweather.app;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.L;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	
	public static final int LEVEL_CITY = 1;
	
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog mProgress;
	
	private TextView title;
	
	private ListView listview;
	
	private ArrayAdapter<String> adapter;
	
	private CoolWeatherDB db;
	
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;
	
	private List<City> cityList;
	
	private List<County> countyList;
	
	private Province selectedProvince;
	
	private City selectedCity;
	
	private int currentLevel;
	
	private boolean isFromWeather;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeather = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(this);
        if(prf.getBoolean("city_selected", false) && !isFromWeather){
        	Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        title = (TextView) findViewById(R.id.title_text);
        listview = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					queryCity();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounty();
				} else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					L.i("MainAct " + countyCode);
					startActivity(intent);
					finish();
				}
			}
			
		});
        db = CoolWeatherDB.getInstance(this);
        queryProvince();
    }

    private void queryProvince() {
		provinceList = db.getProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryServer(null, "province");
		}
	}
    
	private void queryServer(final String code, final String type) {
		String addr;
		if(!TextUtils.isEmpty(code)){
			addr = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			addr = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(addr, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String result) {
				boolean r = false;
				if("province".equals(type)){
					r = Utility.handleProvinceResponse(db, result);
				} else if ("city".equals(type)) {
					r = Utility.handleCityResponse(db, result, selectedProvince.getId());
				} else if ("county".equals(type)) {
					r = Utility.handleCountyResponse(db, result, selectedCity.getId());
				}
				if(r){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							} else if ("city".equals(type)) {
								queryCity();
							} else if ("county".equals(type)) {
								queryCounty();
							}
						}
						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						L.e("加载失败");
					}
					
				});
			}
			
		});
	}

	private void showProgressDialog() {
		if(mProgress == null){
			mProgress = new ProgressDialog(this);
			mProgress.setMessage("is loading ......");
			mProgress.setCanceledOnTouchOutside(false);
		}
		mProgress.show();
	}
	
	private void closeProgressDialog(){
		if(mProgress != null){
			mProgress.dismiss();
		}
	}

	private void queryCity() {
		cityList = db.getCities(selectedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City c : cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			title.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	private void queryCounty() {
		countyList = db.getCounties(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County c : countyList){
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			title.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryServer(selectedCity.getCityCode(), "county");
		}
	}

	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCity();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			if(isFromWeather){
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
