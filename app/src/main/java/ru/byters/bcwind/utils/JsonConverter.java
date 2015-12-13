package ru.byters.bcwind.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.byters.bcwind.model.CityInfo;
import ru.byters.bcwind.model.ForecastInfo;

public class JsonConverter {
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
    public static final String FIELD_DAY = "day";
    public static final String FIELD_DT = "dt";
    public static final String FIELD_WEATHER = "weather";
    public static final String FIELD_PRESSURE = "pressure";
    public static final String FIELD_HUMIDITY = "humidity";
    public static final String DATE_FORMAT = "dd MMMM";
    public static final String FIELD_SUNRISE = "sunrise";
    public static final String FIELD_SUNSET = "sunset";
    public static final String FIELD_TEMP_MIN = "temp_min";
    public static final String FIELD_TEMP_MAX = "temp_max";

    public static void toCities(String data) throws Exception {
        int count = 0;
        JSONObject o = new JSONObject(data);
        count = o.getInt(FIELD_COUNT);
        if (count == 0) throw new Exception();
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

        }
    }

    public static void toForecast(int pos, String data) throws Exception {
        JSONArray a = new JSONObject(data).getJSONArray(FIELD_LIST);
        for (int i = 1; i < a.length(); ++i) {
            ForecastInfo info = new ForecastInfo();

            info.temp = a.getJSONObject(i).getJSONObject(FIELD_TEMP).getString(FIELD_DAY);
            info.date = Utils.calcDate(DATE_FORMAT, a.getJSONObject(i).getString(FIELD_DT));
            info.weather = a.getJSONObject(i).getJSONArray(FIELD_WEATHER).getJSONObject(0).getString(FIELD_MAIN);
            info.pressure = a.getJSONObject(i).getString(FIELD_PRESSURE);
            info.humidity =  a.getJSONObject(i).getString(FIELD_HUMIDITY);
            info.speed =  a.getJSONObject(i).getString(FIELD_SPEED);

            Utils.Cities.get(pos).forecast[i - 1] = info;
        }
    }

    public static void toGroup(String data) throws JSONException {

        ArrayList<CityInfo> list = new ArrayList<CityInfo>();
        JSONObject o = new JSONObject(data);
        JSONArray a = o.getJSONArray(FIELD_LIST);
        for (int i = 0; i < a.length(); ++i) {
            CityInfo c = new CityInfo();
            JSONArray w = a.getJSONObject(i).getJSONArray(FIELD_WEATHER);

            c.id = a.getJSONObject(i).getString(FIELD_ID);
            c.name = a.getJSONObject(i).getString(FIELD_NAME);
            c.temp = a.getJSONObject(i).getJSONObject(FIELD_MAIN).getString(FIELD_TEMP);
            c.windspeed = a.getJSONObject(i).getJSONObject(FIELD_WIND).getString(FIELD_SPEED);
            c.date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            c.humidity = a.getJSONObject(i).getJSONObject(FIELD_MAIN).getString(FIELD_HUMIDITY);
            c.pressure = a.getJSONObject(i).getJSONObject(FIELD_MAIN).getString(FIELD_PRESSURE);
            c.sunrise = a.getJSONObject(i).getJSONObject(FIELD_SYS).getString(FIELD_SUNRISE);
            c.sunset = a.getJSONObject(i).getJSONObject(FIELD_SYS).getString(FIELD_SUNSET);
            c.tempMin = a.getJSONObject(i).getJSONObject(FIELD_MAIN).getString(FIELD_TEMP_MIN);
            c.tempMax = a.getJSONObject(i).getJSONObject(FIELD_MAIN).getString(FIELD_TEMP_MAX);
            c.country = a.getJSONObject(i).getJSONObject(FIELD_SYS).getString(FIELD_COUNTRY);

            c.weather = "";
            for (int j = 0; j < w.length(); ++j)
                c.weather += (j != w.length() - 1) ? w.getJSONObject(j).getString(FIELD_MAIN) + ", " :
                        w.getJSONObject(j).getString(FIELD_MAIN);

            c.forecast = new ForecastInfo[]
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
        Utils.Cities = list;
    }
}
