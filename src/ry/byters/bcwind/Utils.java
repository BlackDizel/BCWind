package ry.byters.bcwind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
			c.temp="0";
			c.windspeed="0";
			c.forecast= new String[]{"0","0","0","0","0","0","0"};
			Cityes.add(c);
			
			c = new CityInfo();
			c.id="703448";
			c.name="Kiev";
			c.temp="0";
			c.windspeed="0";
			c.forecast= new String[]{"0","0","0","0","0","0","0"};
			Cityes.add(c);
			
			c = new CityInfo();
			c.id="2643743";
			c.name="London";
			c.temp="0";
			c.windspeed="0";
			c.forecast= new String[]{"0","0","0","0","0","0","0"};
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
				
				JSONArray forecast = new JSONArray();
				for(String f:ci.forecast) forecast.put(f);
				
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
	    			
	    			JSONArray forecast = a.getJSONObject(i).getJSONArray("forecast");
	    			c.forecast = new String[forecast.length()];
	    			for (int j=0;j<forecast.length();++j)
	    				c.forecast[j] = forecast.getString(j);
	    			l.add(c);
	    		}
	    		return l;	    		
	    	}
		    catch (Exception e)
	    	{
				return null;	        		
	    	}
	    else return null;
	    
	    
	    
	}
	
}
