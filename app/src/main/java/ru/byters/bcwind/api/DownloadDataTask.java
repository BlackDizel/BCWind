package ru.byters.bcwind.api;

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

import android.os.AsyncTask;

public class DownloadDataTask extends AsyncTask<String, Void, String> 
{
    protected String doInBackground(String... params) 
    {
    	StringBuilder builder = new StringBuilder();
    	HttpClient client = new DefaultHttpClient();
    	
    	HttpGet httpGet = new HttpGet(params[0]);
        
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
    {}

}
