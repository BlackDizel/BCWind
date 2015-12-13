package ru.byters.bcwind.ui.activities;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

import ru.byters.bcwind.R;
import ru.byters.bcwind.api.Api;
import ru.byters.bcwind.api.OnCompleteListener;
import ru.byters.bcwind.model.CityInfo;
import ru.byters.bcwind.model.ForecastInfo;
import ru.byters.bcwind.utils.Utils;

public class ActivityAddCity extends Activity implements OnCompleteListener {
    public static final String FIELD_COUNT = "count";
    public static final String FIELD_LIST = "list";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ID = "id";
    public static final String FIELD_MAIN = "main";
    public static final String FIELD_TEMP = "temp";
    public static final String FIELD_WIND = "wind";
    public static final String FIELD_SPEED = "speed";
    public static final String FIELD_SYS = "sys";
    public static final String FIELD_COUNTRY = "country";
    ListView lv;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        lv = (ListView) findViewById(R.id.listView1);
        SetAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_city, menu);
        if (searchView == null) {
            MenuItem item = menu.findItem(R.id.action_search);
            searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    UpdateData(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
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

    /**
     * set adapter for current cities
     */
    void SetAdapter() {
        if (Utils.Cities == null) Utils.LoadCityes(this);
        if (lv != null) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            ArrayAdapter mAdapter = new ArrayAdapter(this, R.layout.citylistremove_item, R.id.textViewCityNameRemove, Utils.Cities) {
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tvCityName = (TextView) view.findViewById(R.id.textViewCityNameRemove);
                    Button b = (Button) view.findViewById(R.id.buttonRemove);

                    Locale l = new Locale("", Utils.Cities.get(position).country);
                    tvCityName.setText(String.format("%s, %s", Utils.Cities.get(position).name, l.getDisplayCountry()));

                    b.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.Cities.remove(position);
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

    /**
     * show message error in toast
     */
    void ShowError() {
        Toast.makeText(getApplicationContext(), R.string.error_add_city, Toast.LENGTH_LONG).show();
    }

    /**
     * parse json object to city list
     */
    void ConvertData(String data) {
        try {
            int count = 0;
            JSONObject o = new JSONObject(data);
            count = o.getInt(FIELD_COUNT);
            if (count == 0) ShowError();
            else {
                JSONArray a = o.getJSONArray(FIELD_LIST);
                for (int i = 0; i < a.length(); ++i) {
                    if (Utils.Cities != null) {
                        CityInfo ci = new CityInfo();
                        ci.name = a.getJSONObject(i).getString(FIELD_NAME);
                        ci.id = a.getJSONObject(i).getString(FIELD_ID);
                        ci.temp = a.getJSONObject(i).getJSONObject(FIELD_MAIN).getString(FIELD_TEMP);
                        ci.windspeed = a.getJSONObject(i).getJSONObject(FIELD_WIND).getString(FIELD_SPEED);
                        ci.country = a.getJSONObject(i).getJSONObject(FIELD_SYS).getString(FIELD_COUNTRY);
                        ci.forecast = new ForecastInfo[]
                                {
                                        new ForecastInfo(),
                                        new ForecastInfo(),
                                        new ForecastInfo(),
                                        new ForecastInfo(),
                                        new ForecastInfo(),
                                        new ForecastInfo()
                                };
                        Utils.Cities.add(ci);
                    }
                }
                Toast.makeText(getApplicationContext(), R.string.city_added, Toast.LENGTH_LONG).show();
                Utils.SaveListToFile(getApplicationContext()); //todo only when user leave this activity
                SetAdapter();
            }

        } catch (Exception e) {
            ShowError();
        }

    }

    void UpdateData(String query) {
        if (searchView != null)
            searchView.setEnabled(false);
        Api.find(query, this);
    }

    @Override
    public void onComplete(String s) {
        if (searchView != null)
            searchView.setEnabled(true);
        ConvertData(s);
    }

    @Override
    public void onError() {
        if (searchView != null)
            searchView.setEnabled(true);
        ShowError();
    }
}
