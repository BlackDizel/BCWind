package ru.byters.bcwind.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.byters.bcwind.model.CityInfo;
import ru.byters.bcwind.model.ForecastInfo;

/**
 * Created by Sony on 13.12.2015.
 */
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

}
