package ru.byters.bcwind;

import org.json.JSONArray;
import org.json.JSONObject;

import ry.byters.bcwind.R;
import utils.CityInfo;
import utils.DownloadDataTask;
import utils.ForecastInfo;
import utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActivityDetails extends Activity 
{
	int pos;
	View[] forecast;
	static DownloadDataTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.activity_details);
		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) 
		    pos = extras.getInt(getString(R.string.id_listitem));
		else pos=0;
		   
		if (Utils.Cityes!=null)
			if (Utils.Cityes.size()>pos)
			{
				setTitle(Utils.Cityes.get(pos).name);
						
				TextView tvDate = 		(TextView)findViewById(R.id.tvDate);
				TextView tvTemp1 = 		(TextView)findViewById(R.id.tvTemp1);
				TextView tvTempMinMax = (TextView)findViewById(R.id.tvTempMinMax);
				TextView tvWeather = 	(TextView)findViewById(R.id.tvWeather);
				TextView tvSunDay = 	(TextView)findViewById(R.id.tvSunDay);
				TextView tvWind = 		(TextView)findViewById(R.id.tvWind);
				TextView tvPressure = 	(TextView)findViewById(R.id.tvPressure);
				TextView tvHumidity = 	(TextView)findViewById(R.id.tvHumidity);
							
				tvDate.			setText(Utils.Cityes.get(pos).date);
				tvTemp1.		setText(Utils.Cityes.get(pos).temp);
				tvTempMinMax.	setText(Utils.Cityes.get(pos).tempMinMax);
				tvWeather.		setText(Utils.Cityes.get(pos).weather);
				tvSunDay.		setText(Utils.Cityes.get(pos).sunDay);
				tvWind.			setText(Utils.Cityes.get(pos).windspeed);
				tvPressure.		setText(Utils.Cityes.get(pos).pressure);
				tvHumidity.		setText(Utils.Cityes.get(pos).humidity);
				
				if (task==null)
					UpdateData();
				setForecast();
			}
	}
	
	
	/**update local data with data from server*/
	void UpdateData()
	{
		task = new DownloadDataTask()
		{
			@Override
			protected void onPostExecute(String result) 
	        {	
				if (!result.isEmpty()) 
					{
						convertData(result);
						setForecast();
					}
	        }				
		};
		
		task.execute("http://api.openweathermap.org/data/2.5/forecast/daily?id="+Utils.Cityes.get(pos).id+"&units=metric");
			
	}

	/**parse JSON object with forecast*/
	void convertData(String data)
    {			
		try
    	{
    		JSONObject o = new JSONObject(data);
    		
    		JSONArray a= o.getJSONArray("list");
    		for (int i=1;i<a.length();++i)
    		{
    			ForecastInfo info = new ForecastInfo();
    			
				info.temp=		a.getJSONObject(i).getJSONObject("temp").getString("day")+"°";        				
    			info.date=		Utils.calcDate("dd MMMM",a.getJSONObject(i).getString("dt"));
    			info.weather = 	a.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main");
    			info.misc=		Utils.calcPressure(a.getJSONObject(i).getString("pressure"))+", "+
    							a.getJSONObject(i).getString("humidity")+"%, "+
    							a.getJSONObject(i).getString("speed")+"ì/ñ";
    			
				Utils.Cityes.get(pos).forecast[i-1]=info;    
				Utils.SaveListToFile(this);
    		}
    	}
    	catch (Exception e)
    	{
    		return;
    	}
    }
	
	/**put forecast info into activity*/
	void setForecast()
	{
		CityInfo c= Utils.Cityes.get(pos);
		{
			if (forecast==null)
			{
				forecast = new View[6];
				forecast[0]=findViewById(R.id.forecast1);
				forecast[1]=findViewById(R.id.forecast2);
				forecast[2]=findViewById(R.id.forecast3);
				forecast[3]=findViewById(R.id.forecast4);
				forecast[4]=findViewById(R.id.forecast5);
				forecast[5]=findViewById(R.id.forecast6);
			}
			
			for (int i=0;i<c.forecast.length;++i)
			{
				TextView forecastTemp 	= (TextView)forecast[i].findViewById(R.id.tvTemp);
				TextView tvDate 		= (TextView)forecast[i].findViewById(R.id.tvDate);
				TextView tvWeather 		= (TextView)forecast[i].findViewById(R.id.tvWeather);
				TextView tvMisc 		= (TextView)forecast[i].findViewById(R.id.tvMisc);
				
				forecastTemp.	setText(c.forecast[i].temp);
				tvDate.			setText(c.forecast[i].date);
				tvWeather.		setText(c.forecast[i].weather);
				tvMisc.			setText(c.forecast[i].misc);				
			}			
		}
	}
}
