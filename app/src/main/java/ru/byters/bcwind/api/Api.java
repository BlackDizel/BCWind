package ru.byters.bcwind.api;

public class Api {
    private static final String API_FIND = "http://api.openweathermap.org/data/2.5/find?q=%s&type=like&lang=ru&units=metric";

    public static void find(String query, final OnCompleteListener listener) {
        if (!query.isEmpty()) {
            new DownloadDataTask() {
                @Override
                protected void onPostExecute(String result) {
                    if (!result.isEmpty()) {
                        if (listener != null) listener.onComplete(result);
                    } else if (listener != null) listener.onError();
                }
            }.execute(String.format(API_FIND, query.replace(" ", "%20")));
        }
    }
}
