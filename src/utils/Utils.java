package utils;

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

public class Utils 
{ 
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
	
	//serialization arraylist
	public static void SaveListToFile(Context ctx) 
	{
		JSONArray arraylist = new JSONArray();
		for (CityInfo ci:Cityes)
		{	
			JSONObject object = new JSONObject();
			try 
			{
				object.put("id", ci.id);
				object.put("name", ci.name);
				object.put("temp", ci.temp);
				object.put("windspeed", ci.windspeed);
				object.put("date", ci.date);
				object.put("weather", ci.weather);	
				object.put("tempMinMax", ci.tempMinMax);
				object.put("sunDay", ci.sunDay);
				object.put("pressure", ci.pressure);
				object.put("humidity", ci.humidity);
				
				JSONArray forecast = new JSONArray();
				for(ForecastInfo f:ci.forecast) 
				{
					JSONObject j = new JSONObject();
					j.put("date", f.date);
					j.put("misc", f.misc);
					j.put("temp", f.temp);
					j.put("weather", f.weather);
					
					forecast.put(j);
				}
				
				object.put("forecast", forecast);
				
			} catch (JSONException e) {
			    return;
			}
			arraylist.put(object);
		}

		String data = arraylist.toString();
		  
	    try 
	    {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput("config.txt", Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}

	//deserialization arraylist
	private static ArrayList<CityInfo> readFromFile(Context ctx) 
	{
	    String ret = "";
	    try 
	    {
	        InputStream inputStream = ctx.openFileInput("config.txt");
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
	    
	    if (ret!="")
	    	try
	    	{
	    		ArrayList<CityInfo> l = new ArrayList<CityInfo>();
	    		JSONArray a = new JSONArray(ret);
	    		for (int i=0;i<a.length();++i)
	    		{
	    			CityInfo c = new CityInfo();
	    			c.temp = 		a.getJSONObject(i).getString("temp");
	    			c.name = 		a.getJSONObject(i).getString("name");
	    			c.windspeed = 	a.getJSONObject(i).getString("windspeed");
	    			c.id = 			a.getJSONObject(i).getString("id");
	    			c.date =		a.getJSONObject(i).getString("date");
	    			c.weather =		a.getJSONObject(i).getString("weather");
	    			c.tempMinMax =	a.getJSONObject(i).getString("tempMinMax");
	    			c.sunDay =		a.getJSONObject(i).getString("sunDay");
	    			c.pressure =	a.getJSONObject(i).getString("pressure");
	    			c.humidity =	a.getJSONObject(i).getString("humidity");
	    			
	    			JSONArray forecast = a.getJSONObject(i).getJSONArray("forecast");
	    			c.forecast = new ForecastInfo[forecast.length()];
	    			
	    			for (int j=0;j<forecast.length();++j)
	    			{
	    				JSONObject jo = forecast.getJSONObject(j);
	    				ForecastInfo fi = new ForecastInfo();
	    				try
	    				{	
		    				fi.date= jo.getString("date");
		    				fi.misc= jo.getString("misc");
		    				fi.temp= jo.getString("temp");
		    				fi.weather= jo.getString("weather");
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
		return String.valueOf(mmrs)+"μμ π.ρ.";
	}
	
}
