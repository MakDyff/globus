package ru.makdyff.globus;

import android.app.Application;

import service.Dispatcher;
import sqlite.SQLiteConnector2;

public class App extends Application {

    public static SQLiteConnector2 SqlCon;
    public static Dispatcher Disp;

    @Override
    public void onCreate() {
        super.onCreate();

        SqlCon = new SQLiteConnector2(getApplicationContext());
        Disp = new Dispatcher();

    }
}
