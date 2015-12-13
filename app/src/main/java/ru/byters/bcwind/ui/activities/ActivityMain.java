package ru.byters.bcwind.ui.activities;

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

import ru.byters.bcwind.R;
import ru.byters.bcwind.api.Api;
import ru.byters.bcwind.api.OnCompleteListener;
import ru.byters.bcwind.utils.Utils;


public class ActivityMain extends Activity implements OnCompleteListener {
    /**
     * is download data on progress
     */
    static Boolean onProcess = false;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listViewTowns);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(getApplicationContext(), ActivityDetails.class);
                int pos = Integer.valueOf(arg1.getTag().toString());
                intent.putExtra(getString(R.string.id_listitem), pos);
                startActivity(intent);
            }
        });

        if (Utils.Cities == null)
            Utils.LoadCities(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetAdapter();

        if (!onProcess)
            UpdateData();
    }

    /**
     * download data from server
     */
    void UpdateData() {
        onProcess = true;

        StringBuilder s = new StringBuilder();
        if (Utils.Cities.size() > 0) {
            s.append(Utils.Cities.get(0).id);
            for (int i = 1; i < Utils.Cities.size(); ++i)
                s.append(",").append(Utils.Cities.get(i).id);
        }

        Api.group(s.toString(), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), ActivityAddCity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * show cities on activity
     */
    void SetAdapter() {
        if (lv != null) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            ArrayAdapter mAdapter = new ArrayAdapter(this, R.layout.citylist_item, R.id.cName, Utils.Cities) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tvCityName = (TextView) view.findViewById(R.id.cName);
                    TextView tvCityTemp = (TextView) view.findViewById(R.id.cTemp);
                    TextView tvCityWeather = (TextView) view.findViewById(R.id.cWeather);

                    tvCityName.setText(Utils.Cities.get(position).name);
                    tvCityTemp.setText(String.format("%s%s"
                            , Utils.Cities.get(position).temp
                            , getString(R.string.celsium)));
                    tvCityWeather.setText(Utils.Cities.get(position).weather);

                    view.setTag(position);

                    return view;
                }
            };
            lv.setAdapter(mAdapter);
        }
    }

    @Override
    public void onComplete() {
        onProcess = false;
        Utils.SaveListToFile(this);
        SetAdapter();
    }

    @Override
    public void onError() {
        onProcess = false;
    }
}
