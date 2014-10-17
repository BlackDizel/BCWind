package ry.byters.bcwind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
		    Intent intent = new Intent(getApplicationContext(), ActivityAddCity.class); 
	    	startActivity(intent);
	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment 
	{
		ArrayList<CityInfo> ListViewSource;
		ListView lv;
		Activity act;
		public PlaceholderFragment() {
		}
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_activity_main,
					container, false);
			lv = (ListView)rootView.findViewById(R.id.listViewTowns);
			
			if (act!=null)
			lv.setOnItemClickListener(new OnItemClickListener() 
			{			
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
    			{
    			    Intent intent = new Intent(act.getApplicationContext(), ActivityDetails.class); 
			    	String cId = (String) arg1.getTag();
			    	
			    	intent.putExtra("id", cId);
    			    startActivity(intent);
				}
			});
			
			return rootView;
		}
		
		
		@Override
		public void onAttach(Activity activity) {
			act=activity;
			new DownloadDataTask().execute();
			
			super.onAttach(activity);
		}
		
		 void ConvertData(String data)
        {
        	try
        	{
        		if (ListViewSource==null) ListViewSource = new ArrayList<CityInfo>();
        		JSONObject o = new JSONObject(data);
        		JSONArray a= o.getJSONArray("list");
        		for (int i=0;i<a.length();++i)
        		{
        			CityInfo c = new CityInfo();
        			c.temp = a.getJSONObject(i).getJSONObject("main").getString("temp");
        			c.name = a.getJSONObject(i).getString("name");
        			c.windspeed = a.getJSONObject(i).getJSONObject("wind").getString("speed");
        			c.id = a.getJSONObject(i).getString("id");
        			c.forecast=new String[]{"0","0","0","0","0","0","0"};
        			ListViewSource.add(c);
        		}	
        		Utils.SaveListToFile(act.getApplicationContext());
        	}
        	catch (Exception e)
        	{
        		ListViewSource = Utils.Cityes;	        		
        	}
        	SetAdapter();
        	
        }
	        
        void SetAdapter()
        {
     		if (act!=null)
     		{
	        	@SuppressWarnings({ "rawtypes", "unchecked" })
	            ArrayAdapter mAdapter = new ArrayAdapter(act.getBaseContext(), R.layout.citylist_item, R.id.cName, ListViewSource) 
	            {
	                @Override
	                public View getView(int position, View convertView, ViewGroup parent) 
	                {
	                    View view = super.getView(position, convertView, parent);
	                    TextView tvCityName = 	(TextView) view.findViewById(R.id.cName);
	                    TextView tvCityTemp = (TextView) view.findViewById(R.id.cTemp);
	                    
	                    tvCityName.setText(	ListViewSource.get(position).name);	                
	                    tvCityTemp.setText(	ListViewSource.get(position).temp);
	    	            
	                    view.setTag(		ListViewSource.get(position).id);
	                    
	                    return view;
	                }
	            };
	            if (lv!=null) lv.setAdapter(mAdapter);
     		}	        	
        }
	        
		private class DownloadDataTask extends AsyncTask<Void, Void, String> 
		{			
	        protected String doInBackground(Void... params) 
	        {
	        	StringBuilder builder = new StringBuilder();
	        	HttpClient client = new DefaultHttpClient();
	        	if (Utils.Cityes==null) Utils.LoadCityes(act.getApplicationContext());
	        		
	        	StringBuilder s = new StringBuilder();
	        	s.append("http://api.openweathermap.org/data/2.5/group?id=");
	        	
	        	for (int i=0;i<Utils.Cityes.size();++i)
	        		s.append(Utils.Cityes.get(i).id+",");
	        	
	        	s.deleteCharAt(s.length()-1);	        	
	        	s.append("&units=metric&lang=ru");
	        	HttpGet httpGet = new HttpGet(s.toString());
	            
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
	            	 return ""; //raise some error or load local data
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
	        	if (result!="")
	        	{
	        		ConvertData(result);
	        	}
	        		else
	        	{
        			ListViewSource = Utils.Cityes;
                	SetAdapter();
	        	}
	        }
	        
	       
		}
		

		
		
		
	}
}
