package ru.byters.bcwind.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.util.Log;

import ru.byters.bcwind.model.CityInfo;
import ru.byters.bcwind.model.ForecastInfo;

public class Utils 
{ 
	private static final String ID="id";
	private static final String NAME="name";
	private static final String TEMP="temp";
	private static final String WINDSPEED="windspeed";
	private static final String DATE="date";
	private static final String WEATHER="weather";
	private static final String TEMPMINMAX="tempminmax";
	private static final String SUNDAY="sunday";
	private static final String PRESSURE="pressure";
	private static final String HUMIDITY="humidity";
	private static final String COUNTRY="contry";
	private static final String MISC="misc";
	private static final String FORECAST="forecast";
	private static final String FILENAME="config.txt";
	
	
	
	
	public static ArrayList<CityInfo> Cityes;
	
	public static void LoadCityes(Context ctx)
	{
		ArrayList<CityInfo> arr = readFromFile(ctx);
		if (arr==null)
		{
			
			Cityes = new ArrayList<CityInfo>();
			CityInfo c = new CityInfo();
			c.id="524901";
			c.name="Moscow";
			c.country="RU";
			c.temp="none";
			c.windspeed="none";
			c.weather="none";
			c.forecast= new ForecastInfo[]
					{
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					};
			Cityes.add(c);
			
			c = new CityInfo();
			c.id="703448";
			c.name="Kiev";
			c.country="UA";
			c.temp="none";
			c.windspeed="none";
			c.weather="none";
			c.forecast= new ForecastInfo[]
					{
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					};
			Cityes.add(c);
			
			c = new CityInfo();
			c.id="2643743";
			c.name="London";
			c.country="GB";
			c.temp="none";
			c.windspeed="none";
			c.weather="none";
			c.forecast= new ForecastInfo[]
					{
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					new ForecastInfo(),
					};
			Cityes.add(c);	
		}
		else Cityes = arr;		
	}
	
	/**serialization arraylist*/
	public static void SaveListToFile(Context ctx) 
	{
		JSONArray arraylist = new JSONArray();
		for (CityInfo ci:Cityes)
		{	
			JSONObject object = new JSONObject();
			try 
			{
				object.put(ID, 			ci.id);
				object.put(NAME, 		ci.name);
				object.put(TEMP, 		ci.temp);
				object.put(WINDSPEED, 	ci.windspeed);
				object.put(DATE, 		ci.date);
				object.put(WEATHER, 	ci.weather);	
				object.put(TEMPMINMAX, 	ci.tempMinMax);
				object.put(SUNDAY, 		ci.sunDay);
				object.put(PRESSURE, 	ci.pressure);
				object.put(HUMIDITY, 	ci.humidity);
				object.put(COUNTRY, 	ci.country);
				
				
				JSONArray forecast = new JSONArray();
				for(ForecastInfo f:ci.forecast) 
				{
					JSONObject j = new JSONObject();
					j.put(DATE, 	f.date);
					j.put(MISC, 	f.misc);
					j.put(TEMP, 	f.temp);
					j.put(WEATHER, 	f.weather);
					
					forecast.put(j);
				}
				
				object.put(FORECAST, forecast);
				
			} catch (JSONException e) {
			    return;
			}
			arraylist.put(object);
		}

		String data = arraylist.toString();
		  
	    try 
	    {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}

	/**deserialization arraylist*/
	private static ArrayList<CityInfo> readFromFile(Context ctx) 
	{
	    String ret = "";
	    try 
	    {
	        InputStream inputStream = ctx.openFileInput(FILENAME);
	        if ( inputStream != null ) 
	        {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();
	            while ( (receiveString = bufferedReader.readLine()) != null ) stringBuilder.append(receiveString);
	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (IOException e) 
	    {
	       return null;
	    }
	    
	    if (!ret.isEmpty())
	    	try
	    	{
	    		ArrayList<CityInfo> l = new ArrayList<CityInfo>();
	    		JSONArray a = new JSONArray(ret);
	    		for (int i=0;i<a.length();++i)
	    		{
	    			CityInfo c = new CityInfo();
	    			c.temp = 		a.getJSONObject(i).getString(TEMP);
	    			c.name = 		a.getJSONObject(i).getString(NAME);
	    			c.windspeed = 	a.getJSONObject(i).getString(WINDSPEED);
	    			c.id = 			a.getJSONObject(i).getString(ID);
	    			c.date =		a.getJSONObject(i).getString(DATE);
	    			c.weather =		a.getJSONObject(i).getString(WEATHER);
	    			c.tempMinMax =	a.getJSONObject(i).getString(TEMPMINMAX);
	    			c.sunDay =		a.getJSONObject(i).getString(SUNDAY);
	    			c.pressure =	a.getJSONObject(i).getString(PRESSURE);
	    			c.humidity =	a.getJSONObject(i).getString(HUMIDITY);
	    			c.country =		a.getJSONObject(i).getString(COUNTRY);
	    			
	    			
	    			JSONArray forecast = a.getJSONObject(i).getJSONArray(FORECAST);
	    			c.forecast = new ForecastInfo[forecast.length()];
	    			
	    			for (int j=0;j<forecast.length();++j)
	    			{
	    				JSONObject jo = forecast.getJSONObject(j);
	    				ForecastInfo fi = new ForecastInfo();
	    				try
	    				{	
		    				fi.date= jo.getString(DATE);
		    				fi.misc= jo.getString(MISC);
		    				fi.temp= jo.getString(TEMP);
		    				fi.weather= jo.getString(WEATHER);
	    				}
	    				catch (Exception e1)
	    				{	    					
	    				}
	    				c.forecast[j] = fi;
	    			}
	    			l.add(c);
	    		}
	    		return l;	    		
	    	}
		    catch (Exception e)
	    	{
	    		return null;	        		
	    	}
	    else 
	    	{
	    		return null;
	    	}
	}
	
	public static String calcDate(String format,String data)
	{
		long dv = Long.valueOf(data)*1000;
		Date df = new Date(dv);
		return new SimpleDateFormat(format).format(df);
		 
	}
	public static String calcPressure(String data)
	{
		double mbar = Double.valueOf(data);
		int mmrs = (int)Math.round((mbar/ 1.3332));
		return String.valueOf(mmrs)+"мм р.с.";
	}
	
}
