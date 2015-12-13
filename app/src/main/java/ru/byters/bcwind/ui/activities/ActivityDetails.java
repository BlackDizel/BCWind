package ru.byters.bcwind.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.byters.bcwind.R;
import ru.byters.bcwind.api.Api;
import ru.byters.bcwind.api.OnCompleteListener;
import ru.byters.bcwind.model.CityInfo;
import ru.byters.bcwind.utils.Utils;

public class ActivityDetails extends Activity implements OnCompleteListener {
    public static final String DATE_FORMAT = "HH:mm";
    static Boolean onProcess = false;
    int pos;
    View[] forecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            pos = extras.getInt(getString(R.string.id_listitem));
        else pos = 0;

        if (Utils.Cities != null)
            if (Utils.Cities.size() > pos) {
                setTitle(Utils.Cities.get(pos).name);

                TextView tvDate = (TextView) findViewById(R.id.tvDate);
                TextView tvTemp1 = (TextView) findViewById(R.id.tvTemp1);
                TextView tvTempMinMax = (TextView) findViewById(R.id.tvTempMinMax);
                TextView tvWeather = (TextView) findViewById(R.id.tvWeather);
                TextView tvSunDay = (TextView) findViewById(R.id.tvSunDay);
                TextView tvWind = (TextView) findViewById(R.id.tvWind);
                TextView tvPressure = (TextView) findViewById(R.id.tvPressure);
                TextView tvHumidity = (TextView) findViewById(R.id.tvHumidity);

                tvDate.setText(Utils.Cities.get(pos).date);
                tvTemp1.setText(Utils.Cities.get(pos).temp);

                tvTempMinMax.setText(String.format(getString(R.string.temp_from_to)
                        , Utils.Cities.get(pos).tempMin
                        , Utils.Cities.get(pos).tempMax));

                tvWeather.setText(Utils.Cities.get(pos).weather);
                tvSunDay.setText(String.format(getString(R.string.daytime_from_to)
                        , Utils.calcDate(DATE_FORMAT, Utils.Cities.get(pos).sunrise)
                        , Utils.calcDate(DATE_FORMAT, Utils.Cities.get(pos).sunset)));
                tvWind.setText(String.format(getString(R.string.SPEED_FORMAT), Utils.Cities.get(pos).windspeed));
                tvPressure.setText(Utils.calcPressure(this, Utils.Cities.get(pos).pressure));
                tvHumidity.setText(String.format(getString(R.string.PERCENT_FORMAT), Utils.Cities.get(pos).humidity));
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!onProcess)
            UpdateData();
        setForecast();
    }

    /**
     * update local data with data from server
     */
    void UpdateData() {
        onProcess = true;
        Api.daily(this, pos, this);
    }

    /**
     * put forecast info into activity
     */
    void setForecast() {
        CityInfo c = Utils.Cities.get(pos);
        {
            if (forecast == null) {
                forecast = new View[6];
                forecast[0] = findViewById(R.id.forecast1);
                forecast[1] = findViewById(R.id.forecast2);
                forecast[2] = findViewById(R.id.forecast3);
                forecast[3] = findViewById(R.id.forecast4);
                forecast[4] = findViewById(R.id.forecast5);
                forecast[5] = findViewById(R.id.forecast6);
            }

            for (int i = 0; i < c.forecast.length; ++i) {
                TextView forecastTemp = (TextView) forecast[i].findViewById(R.id.tvTemp);
                TextView tvDate = (TextView) forecast[i].findViewById(R.id.tvDate);
                TextView tvWeather = (TextView) forecast[i].findViewById(R.id.tvWeather);
                TextView tvMisc = (TextView) forecast[i].findViewById(R.id.tvMisc);

                forecastTemp.setText(String.format(getString(R.string.CELSIUM_FORMAT), c.forecast[i].temp));
                tvDate.setText(c.forecast[i].date);
                tvWeather.setText(c.forecast[i].weather);

                tvMisc.setText(String.format(getString(R.string.misc_format),
                        Utils.calcPressure(this, c.forecast[i].pressure)
                        , c.forecast[i].humidity
                        , c.forecast[i].speed));
            }
        }
    }

    @Override
    public void onComplete() {
        onProcess = false;
        Utils.SaveListToFile(this);
        setForecast();
    }

    @Override
    public void onError() {
        onProcess = false;
        //todo on error retrieve info from server
    }
}
