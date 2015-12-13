package ru.byters.bcwind.api;

import ru.byters.bcwind.utils.JsonConverter;

public class Api {
    private static final String API_FIND = "http://api.openweathermap.org/data/2.5/find?q=%s&type=like&lang=ru&units=metric";


    public static void find(String query, final OnCompleteListener listener) {
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
            }.execute(String.format(API_FIND, query.replace(" ", "%20")));
        }
    }
}
