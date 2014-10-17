package ry.byters.bcwind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ActivityDetails extends Activity 
{
	static String CityID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.activity_details);
		
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) 
		    CityID = extras.getString("id");
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment {
		TextView tvCityName;
		TextView[] tvTemp;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_activity_details, container, false);
			
			tvCityName = (TextView)rootView.findViewById(R.id.tvCityNameDetails);
			tvTemp = new TextView[7];
			
			tvTemp[0]  = (TextView)rootView.findViewById(R.id.tvTemp1);
			tvTemp[1]  = (TextView)rootView.findViewById(R.id.tvTemp2);
			tvTemp[2]  = (TextView)rootView.findViewById(R.id.tvTemp3);
			tvTemp[3]  = (TextView)rootView.findViewById(R.id.tvTemp4);
			tvTemp[4]  = (TextView)rootView.findViewById(R.id.tvTemp5);
			tvTemp[5]  = (TextView)rootView.findViewById(R.id.tvTemp6);
			tvTemp[6]  = (TextView)rootView.findViewById(R.id.tvTemp7);
			
			new DownloadDataTask().execute();
			
			return rootView;
		}
		
		void ConvertData(String data)
        {
			CityInfo currentCity=null;
			for (CityInfo c:Utils.Cityes)
				if (c.id.equals(CityID)) currentCity=c;
			
			if (data!="")
	        	try
	        	{
	        		JSONObject o = new JSONObject(data);
	        		tvCityName.setText(o.getJSONObject("city").getString("name"));
	        		
	        		JSONArray a= o.getJSONArray("list");
	        		for (int i=0;i<a.length();++i)
	        		{
	        			tvTemp[i].setText(a.getJSONObject(i).getJSONObject("temp").getString("day"));
	        			if (currentCity!=null)
	        				currentCity.forecast[i]=tvTemp[i].getText().toString();
	        		}
	        	}
	        	catch (Exception e)
	        	{
	        		setLocalForecast(currentCity);
	        	}
			else setLocalForecast(currentCity);
        }
		
		void setLocalForecast(CityInfo cCity)
		{
			if (cCity!=null)
			{
				if (tvCityName!=null)
					tvCityName.setText(cCity.name);
			
    			for (int i=0;i<cCity.forecast.length;++i)
    				tvTemp[i].setText(cCity.forecast[i]);	
			}
		}
		
		private class DownloadDataTask extends AsyncTask<Void, Void, String> 
		{			
	        protected String doInBackground(Void... params) 
	        {
	        	StringBuilder builder = new StringBuilder();
	        	HttpClient client = new DefaultHttpClient();
	        	HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/forecast/daily?id="+CityID+"&units=metric");
	            
	        	try 
	        	{
	              HttpResponse response = client.execute(httpGet);
	              StatusLine statusLine = response.getStatusLine();
	              int statusCode = statusLine.getStatusCode();
	              if (statusCode == 200) 
	              {
	                HttpEntity entity = response.getEntity();
	                InputStream content = entity.getContent();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	                String line;
	                while ((line = reader.readLine()) != null) 
	                {
	                  builder.append(line);
	                }
	              } else 
	            	 return ""; 
	            } catch (ClientProtocolException e) 
	            {
	            	return "";
	            }
	             catch (IOException e) 
	             {
	            	return "";
	             }
            	
	            return builder.toString();
	        }
	
	        protected void onPostExecute(String result) 
	        {	
	        	ConvertData(result);
	        }
	        
		}

	}
}
