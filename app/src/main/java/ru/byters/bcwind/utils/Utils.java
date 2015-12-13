package ru.byters.bcwind.utils;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.byters.bcwind.R;
import ru.byters.bcwind.model.CityInfo;
import ru.byters.bcwind.model.ForecastInfo;

public class Utils {
    private static final String FILENAME = "config.txt";


    public static ArrayList<CityInfo> Cities;

    public static void LoadCities(Context ctx) {
        ArrayList<CityInfo> arr = readFromFile(ctx);
        if (arr == null) {

            Cities = new ArrayList<CityInfo>();
            CityInfo c = new CityInfo();
            c.id = "524901";
            c.name = "Moscow";
            c.country = "RU";
            c.temp = "none";
            c.windspeed = "none";
            c.weather = "none";
            c.forecast = new ForecastInfo[]
                    {
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                    };
            Cities.add(c);

            c = new CityInfo();
            c.id = "703448";
            c.name = "Kiev";
            c.country = "UA";
            c.temp = "none";
            c.windspeed = "none";
            c.weather = "none";
            c.forecast = new ForecastInfo[]
                    {
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                    };
            Cities.add(c);

            c = new CityInfo();
            c.id = "2643743";
            c.name = "London";
            c.country = "GB";
            c.temp = "none";
            c.windspeed = "none";
            c.weather = "none";
            c.forecast = new ForecastInfo[]
                    {
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                            new ForecastInfo(),
                    };
            Cities.add(c);
        } else Cities = arr;
    }

    public static String calcDate(String format, String data) {
        long dv = Long.valueOf(data) * 1000;
        Date df = new Date(dv);
        return new SimpleDateFormat(format).format(df);

    }

    public static void SaveListToFile(Context context) {
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput(FILENAME, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(Cities);
            fileOut.getFD().sync();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<CityInfo> readFromFile(Context context) {

        ObjectInputStream objectIn = null;
        Object object = null;
        boolean needRemove = false;
        try {

            FileInputStream fileIn = context.getApplicationContext().openFileInput(FILENAME);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            needRemove = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            needRemove = true;
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (needRemove) RemoveFile(context);

        return (ArrayList<CityInfo>) object;
    }


    public static synchronized void RemoveFile(Context ctx) {
        try {
            ctx.getApplicationContext().deleteFile(FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String calcPressure(Context context, String data) {
        double mbar = Double.valueOf(data);
        int mmrs = (int) Math.round((mbar / 1.3332));
        return String.valueOf(mmrs) + context.getResources().getString(R.string.mmrs);
    }

}
