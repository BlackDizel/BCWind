package ru.byters.bcwind;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import ry.byters.bcwind.R;
import utils.CityInfo;
import utils.DownloadDataTask;
import utils.ForecastInfo;
import utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityAddCity extends Activity
{
	ListView lv;
	static Boolean onProcess=false;
	SearchView searchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_city);
		
		lv = (ListView)findViewById(R.id.listView1);
		SetAdapter();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_add_city, menu);
		if (searchView==null)
		{
			MenuItem item = menu.findItem(R.id.action_search);
			searchView = (SearchView)item.getActionView();
		    searchView.setOnQueryTextListener(new OnQueryTextListener() 
			   {
				
				@Override
				public boolean onQueryTextSubmit(String query) 
				{
					UpdateData(query);
					return true;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) 
				{					
					return true;					
				}					
			});
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//int id = item.getItemId();		
		return super.onOptionsItemSelected(item);
	}
	
	/**set adapter for current cities*/
	 void SetAdapter()
     {	
		 if (Utils.Cityes==null) Utils.LoadCityes(this);
		 if (lv!=null)
		 {
	     	@SuppressWarnings({ "rawtypes", "unchecked" })
	         ArrayAdapter mAdapter = new ArrayAdapter(this, R.layout.citylistremove_item, R.id.textViewCityNameRemove, Utils.Cityes) 
	         {
	             @Override
	             public View getView(final int position, View convertView, ViewGroup parent) 
	             {
	                 View view = super.getView(position, convertView, parent);
	                 TextView tvCityName = 	(TextView) 	view.findViewById(R.id.textViewCityNameRemove);
	                 Button b = 			(Button) 	view.findViewById(R.id.buttonRemove);
	                 
	                 Locale l = new Locale("",Utils.Cityes.get(position).country);
	                 tvCityName.setText(	Utils.Cityes.get(position).name+", "+l.getDisplayCountry());	                
	                 
	                 b.setOnClickListener(new OnClickListener() 
	                 {						
						@Override
						public void onClick(View v) 
						{
							Utils.Cityes.remove(position);
							Utils.SaveListToFile(getApplicationContext());//FIX:only when user leave this activity
							notifyDataSetChanged();						
						}
					});   
	                 
	                return view;
	             }
	         };		            
	         
	         lv.setAdapter(mAdapter);
		 }
 	}
	 /**show message error in toast*/
	 void ShowError()
     {
     	Toast.makeText(getApplicationContext(), "cannot add city", Toast.LENGTH_LONG).show();        	
     }
     
	 /**parse json object to city list*/
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
        			if (Utils.Cityes!=null)
        			{	
        				CityInfo ci = new CityInfo();
        				ci.name= 		a.getJSONObject(i).getString("name");            			
        				ci.id=			a.getJSONObject(i).getString("id");	        				
        				ci.temp=		a.getJSONObject(i).getJSONObject("main").getString("temp");            			
        				ci.windspeed=	a.getJSONObject(i).getJSONObject("wind").getString("speed");            			
        				ci.country= 	a.getJSONObject(i).getJSONObject("sys").getString("country");
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

	
	 void UpdateData(String data)
	 {
		 if ((!data.isEmpty())&&(!onProcess))		
		 {
			 onProcess=true;
			 new DownloadDataTask()
			 {
				@Override
				protected void onPostExecute(String result) 
			    {	     
					onProcess=false;
			    	if (!result.isEmpty()) 	ConvertData(result);
			    	else 					ShowError();        	
			    }
			}.execute(	"http://api.openweathermap.org/data/2.5/find?q="+
						data.replace(" ", "%20")+
						"&type=like&lang=ru&units=metric");				
		}
	 }

	   
	
}
