package ru.makdyff.globus;

import android.os.Bundle;
import cache.Valute;
import navigation.MainNavigation;

public class MainActivity extends FragmentActivityBase {

    private MainNavigation _navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _navigation = new MainNavigation(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        _navigation.start();
    }

    @Override
    public void onBackPressed() {
        _navigation.backClick();
    }
}
