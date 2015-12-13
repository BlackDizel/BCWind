package ru.byters.bcwind.api;

import android.content.Context;

import ru.byters.bcwind.R;
import ru.byters.bcwind.utils.JsonConverter;
import ru.byters.bcwind.utils.Utils;

public class Api {
    private static final String SERVER_URI = "http://api.openweathermap.org/data/2.5";
    private static final String API_FIND = "/find?q=%s&type=like&lang=ru&units=metric";
    private static final String API_DAILY = "/forecast/daily?id=%s&units=metric";
    private static final String API_GROUP = "/group?id=%s&units=metric&lang=ru";
    private static final String APP_ID = "&APPID=";

    private static String getUri(Context context, String method) {
        //todo add openweathermap_key by register on openweathermap.org
        return String.format("%s%s%s%s"
                , SERVER_URI
                , method
                , APP_ID
                , context.getString(R.string.openweathermap_key));
    }

    public static void find(Context context, String query, final OnCompleteListener listener) {
        if (!query.isEmpty()) {
            new DownloadDataTask() {
                @Override
                protected void onPostExecute(String result) {
                    if (!result.isEmpty()) {
                        try {
                            JsonConverter.toCities(result);
                            if (listener != null) listener.onComplete();
                        } catch (Exception e) {
                            if (listener != null) listener.onError();
                        }
                    } else if (listener != null) listener.onError();
                }
            }.execute(String.format(getUri(context, API_FIND), query.replace(" ", "%20")));
        }
    }

    public static void daily(Context context, final int pos, final OnCompleteListener listener) {
        new DownloadDataTask() {
            @Override
            protected void onPostExecute(String result) {
                if (!result.isEmpty()) {
                    try {
                        JsonConverter.toForecast(pos, result);
                        if (listener != null) listener.onComplete();
                    } catch (Exception e) {
                        if (listener != null) listener.onError();
                    }
                } else if (listener != null) listener.onError();
            }
        }.execute(String.format(getUri(context, API_DAILY), Utils.Cities.get(pos).id));

    }

    public static void group(Context context, String cities, final OnCompleteListener listener) {
        new DownloadDataTask() {
            @Override
            protected void onPostExecute(String result) {
                if (!result.isEmpty()) {
                    try {
                        JsonConverter.toGroup(result);
                        if (listener != null) listener.onComplete();
                    } catch (Exception e) {
                        if (listener != null) listener.onError();
                    }
                } else if (listener != null) listener.onError();
            }
        }.execute(String.format(getUri(context, API_GROUP), cities));
    }
}
