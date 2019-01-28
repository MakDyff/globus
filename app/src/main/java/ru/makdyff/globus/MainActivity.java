package ru.makdyff.globus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cache.Valute;
import sqlite.SQLiteConnector2;

public class MainActivity extends AppCompatActivity {
    ExecutorService service = Executors.newCachedThreadPool();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        App.SqlCon.delete(Valute.class);

        Valute v = new Valute();
        v.CharCode = "123";
        v.Name = "Name";
        v.Value = 1.333f;

        App.SqlCon.addObject(Valute.class, v);

        service.submit(new Runnable() {
            @Override
            public void run() {
                String vvv = "";

                try {
                    vvv = App.Disp.request();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }
}
