package ru.byters.bcwind;

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

import ry.byters.bcwind.R;

import utils.CityInfo;
import utils.ForecastInfo;
import utils.Utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityAddCity extends Activity {

	Button AddCity;
	EditText etCity;
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_city);
		
		lv = (ListView)findViewById(R.id.listView1);
		SetAdapter();
		
		etCity = (EditText)findViewById(R.id.editText1);
		
		AddCity = (Button)findViewById(R.id.button1);
		AddCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (etCity.getText().toString()!="")
					new DownloadDataTask().execute();
			}
		});
	}
	
	 void SetAdapter()
     {	
     	@SuppressWarnings({ "rawtypes", "unchecked" })
         ArrayAdapter mAdapter = new ArrayAdapter(getBaseContext(), R.layout.citylistremove_item, R.id.textViewCityNameRemove, Utils.Cityes) 
         {
             @Override
             public View getView(final int position, View convertView, ViewGroup parent) 
             {
                 View view = super.getView(position, convertView, parent);
                 TextView tvCityName = 	(TextView) view.findViewById(R.id.textViewCityNameRemove);
                 Button b = (Button) view.findViewById(R.id.buttonRemove);
                 
                 tvCityName.setText(	Utils.Cityes.get(position).name);	                
                 
                 b.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						Utils.Cityes.remove(position);
						Utils.SaveListToFile(getApplicationContext());//FIX:only when user leave this activity
						SetAdapter();						
					}
				});                 
                return view;
             }
         };		            
         
         if (lv!=null) lv.setAdapter(mAdapter);
 	}
	
	private class DownloadDataTask extends AsyncTask<Void, Void, String> 
	{			
        protected String doInBackground(Void... params) 
        {
        	StringBuilder builder = new StringBuilder();
        	HttpClient client = new DefaultHttpClient();
        	if (Utils.Cityes==null) Utils.LoadCityes(getApplicationContext());
        		
        	HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/find?q="+etCity.getText().toString().replace(" ", "%20")+"&type=like&lang=ru&units=metric");
            
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
        	if (result!="") ConvertData(result);
        	else 			ShowError();        	
        }
        
        void ShowError()
        {
        	Toast.makeText(getApplicationContext(), "cannot add city", 
			Toast.LENGTH_LONG).show();        	
        }
        void ConvertData(String data)
        {
        	try
        	{
        		int count=0;
        		JSONObject o = new JSONObject(data);
        		count = o.getInt("count");
        		if (count==0) ShowError();
        		else
        		{
	        		JSONArray a= o.getJSONArray("list");
	        		for (int i=0;i<a.length();++i)
	        		{
	        			String id = a.getJSONObject(i).getString("id");
	        			String name = a.getJSONObject(i).getString("name");
	        			String temp = a.getJSONObject(i).getJSONObject("main").getString("temp");
	        			String windspeed = a.getJSONObject(i).getJSONObject("wind").getString("speed");
	        			
	        			if (Utils.Cityes!=null)
	        			{
	        				CityInfo ci = new CityInfo();
	        				ci.name=name;
	        				ci.id=id;	        				
	        				ci.temp=temp;
	        				ci.windspeed=windspeed;
	        				ci.forecast = new ForecastInfo[]
	        						{
	        						new ForecastInfo(),
	        						new ForecastInfo(),
	        						new ForecastInfo(),
	        						new ForecastInfo(),
	        						new ForecastInfo(),
	        						new ForecastInfo()
	        						};
	        				Utils.Cityes.add(ci);
	        			}
        			}
	        		Toast.makeText(getApplicationContext(), "City added", Toast.LENGTH_LONG).show(); 
	        		Utils.SaveListToFile(getApplicationContext()); //FIX:only when user leave this activity
	            	SetAdapter();
        		}
            	
        	}
        	catch (Exception e)
        	{
        		ShowError();	        		
        	}
        	
        	
        }
        
       
	}
	




}
