package ru.byters.bcwind;

import org.json.JSONArray;
import org.json.JSONObject;

import ry.byters.bcwind.R;
import utils.CityInfo;
import utils.DownloadDataTask;
import utils.ForecastInfo;
import utils.Utils;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActivityDetails extends Activity 
{
	static int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.activity_details);
		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) 
		    pos = extras.getInt(getString(R.string.id_listitem));
		
		setTitle(Utils.Cityes.get(pos).name);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment {
		View[] forecast;
		Activity act;
		
		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_activity_details, container, false);
			
			TextView tvDate = 		(TextView)rootView.findViewById(R.id.tvDate);
			TextView tvTemp1 = 		(TextView)rootView.findViewById(R.id.tvTemp1);
			TextView tvTempMinMax = (TextView)rootView.findViewById(R.id.tvTempMinMax);
			TextView tvWeather = 	(TextView)rootView.findViewById(R.id.tvWeather);
			TextView tvSunDay = 	(TextView)rootView.findViewById(R.id.tvSunDay);
			TextView tvWind = 		(TextView)rootView.findViewById(R.id.tvWind);
			TextView tvPressure = 	(TextView)rootView.findViewById(R.id.tvPressure);
			TextView tvHumidity = 	(TextView)rootView.findViewById(R.id.tvHumidity);
						
			tvDate.			setText(Utils.Cityes.get(pos).date);
			tvTemp1.		setText(Utils.Cityes.get(pos).temp);
			tvTempMinMax.	setText(Utils.Cityes.get(pos).tempMinMax);
			tvWeather.		setText(Utils.Cityes.get(pos).weather);
			tvSunDay.		setText(Utils.Cityes.get(pos).sunDay);
			tvWind.			setText(Utils.Cityes.get(pos).windspeed);
			tvPressure.		setText(Utils.Cityes.get(pos).pressure);
			tvHumidity.		setText(Utils.Cityes.get(pos).humidity);
			
			forecast = new View[6];
			forecast[0]=rootView.findViewById(R.id.forecast1);
			forecast[1]=rootView.findViewById(R.id.forecast2);
			forecast[2]=rootView.findViewById(R.id.forecast3);
			forecast[3]=rootView.findViewById(R.id.forecast4);
			forecast[4]=rootView.findViewById(R.id.forecast5);
			forecast[5]=rootView.findViewById(R.id.forecast6);
						
			new DownloadDataTask()
			{
				@Override
				protected void onPostExecute(String result) 
		        {	
					if (!result.equals("")) convertData(result);
					setForecast();
		        }				
			}.execute("http://api.openweathermap.org/data/2.5/forecast/daily?id="+Utils.Cityes.get(pos).id+"&units=metric");
			
			return rootView;
		}
		
		@Override
		public void onAttach(Activity activity) {
			act = activity;
			super.onAttach(activity);
		}
		
		void convertData(String data)
        {			
			if (data!="")
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
	        							a.getJSONObject(i).getString("speed")+"ì/ñ"
	        							;
	        			
        				Utils.Cityes.get(pos).forecast[i-1]=info;    
        				if (act!=null) Utils.SaveListToFile(act);
	        		}
	        	}
	        	catch (Exception e)
	        	{
	        		return;
	        	}
			else return ;
        }
		
		void setForecast()
		{
			CityInfo c= Utils.Cityes.get(pos);
			if (c!=null)
			{
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
}
