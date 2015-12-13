package ru.byters.bcwind.api;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class DownloadDataTask extends AsyncTask<String, Void, String> {
    protected String doInBackground(String... params) {
        if (params.length < 1) return "";
        String uri = params[0];
        if (uri == null || uri.isEmpty()) return null;

        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } else
                return "";
        } catch (ClientProtocolException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    protected void onPostExecute(String result) {
    }

}
