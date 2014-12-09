package ru.byters.bcwind;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import ry.byters.bcwind.R;
import utils.CityInfo;
import utils.DownloadDataTask;
import utils.ForecastInfo;
import utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ActivityMain extends Activity 
{
	ListView lv;
	/**is download data on progress*/
	static Boolean onProcess=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lv = (ListView)findViewById(R.id.listViewTowns);
		lv.setOnItemClickListener(new OnItemClickListener() 
		{			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
			    Intent intent = new Intent(getApplicationContext(), ActivityDetails.class); 			    
		    	int pos = Integer.valueOf( arg1.getTag().toString());			    	
		    	intent.putExtra(getString(R.string.id_listitem), pos);
			    startActivity(intent);
			}
		});		

		if (Utils.Cityes==null) 
			Utils.LoadCityes(this);		
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		SetAdapter();	
		
		if (!onProcess) 
			UpdateData();	
		
	};
	
	/**download data from server*/
	void UpdateData()
	{	
		onProcess=true;
		StringBuilder s = new StringBuilder();
    	s.append("http://api.openweathermap.org/data/2.5/group?id=");
    	if (Utils.Cityes.size()>0)
    	{
    		s.append(Utils.Cityes.get(0).id); 
    		for (int i=1;i<Utils.Cityes.size();++i)
        		s.append(","+Utils.Cityes.get(i).id);
    	}
    	s.append("&units=metric&lang=ru");
    	
		new DownloadDataTask()
		{
			@Override
			 protected void onPostExecute(String result) 
		        {	
					onProcess=false;
		        	if (!result.isEmpty())
		        		Utils.Cityes = ConvertData(result);
	                SetAdapter();
		        }
		}.execute(s.toString());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
		    Intent intent = new Intent(getApplicationContext(), ActivityAddCity.class); 
	    	startActivity(intent);	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**parse json to city list*/
	ArrayList<CityInfo> ConvertData(String data)
    {
    	try
    	{
    		ArrayList<CityInfo> list = new ArrayList<CityInfo>();
    		JSONObject o = new JSONObject(data);
    		JSONArray a= o.getJSONArray("list");
    		for (int i=0;i<a.length();++i)
    		{
    			String now= new SimpleDateFormat("dd MMMM").format(new Date());
    			
    			CityInfo c = new CityInfo();
    			JSONArray w =a.getJSONObject(i).getJSONArray("weather"); 
    			
    			c.id = 			a.getJSONObject(i).getString("id");
    			c.name = 		a.getJSONObject(i).getString("name");
    			c.temp = 		a.getJSONObject(i).getJSONObject("main").getString("temp")+"°";
    			c.windspeed = 	a.getJSONObject(i).getJSONObject("wind").getString("speed")+"м/с";
    			c.date = 		now.toString(); 
    			c.humidity = 	a.getJSONObject(i).getJSONObject("main").getString("humidity")+"%";
    			c.pressure = 	Utils.calcPressure(a.getJSONObject(i).getJSONObject("main").getString("pressure"));
    			c.sunDay = 		"с "+Utils.calcDate("HH:mm",a.getJSONObject(i).getJSONObject("sys").getString("sunrise"))+
    							" по "+Utils.calcDate("HH:mm",a.getJSONObject(i).getJSONObject("sys").getString("sunset"));
    			c.tempMinMax = 	"от "+a.getJSONObject(i).getJSONObject("main").getString("temp_min")+"°"+
    							" до "+a.getJSONObject(i).getJSONObject("main").getString("temp_max")+"°";
    			c.country = 	a.getJSONObject(i).getJSONObject("sys").getString("country");        			
    			        			
    			c.weather="";
    			for (int j=0;j<w.length();++j)
    				c.weather += (j!=w.length()-1)?	w.getJSONObject(j).getString("main")+", ":
    												w.getJSONObject(j).getString("main");
    			
    			c.forecast=new ForecastInfo[]
    					{
        					new ForecastInfo(),
        					new ForecastInfo(),
        					new ForecastInfo(),
        					new ForecastInfo(),
        					new ForecastInfo(),
        					new ForecastInfo()
    					};
    			
    			list.add(c);
    		}	
    		Utils.SaveListToFile(this);
    		return list;
    	}
    	catch (Exception e)
    	{
    		return Utils.Cityes;	        		
    	}        	
    }
    
	/**show cities on activity*/
	void SetAdapter()
    {
		if (lv!=null)
		{
	    	@SuppressWarnings({ "rawtypes", "unchecked" })
	        ArrayAdapter mAdapter = new ArrayAdapter(this, R.layout.citylist_item, R.id.cName, Utils.Cityes) 
	        {
	            @Override
	            public View getView(int position, View convertView, ViewGroup parent) 
	            {
	                View view = super.getView(position, convertView, parent);
	                TextView tvCityName = 		(TextView) view.findViewById(R.id.cName);
	                TextView tvCityTemp = 		(TextView) view.findViewById(R.id.cTemp);
	                TextView tvCityWeather = 	(TextView) view.findViewById(R.id.cWeather);
	                
	                tvCityName.setText(		Utils.Cityes.get(position).name);	                
	                tvCityTemp.setText(		Utils.Cityes.get(position).temp);
	                tvCityWeather.setText( 	Utils.Cityes.get(position).weather);
		            
	                view.setTag(position);
	                
	                return view;
	            }
	        };
	        lv.setAdapter(mAdapter);
		}
	}

}
