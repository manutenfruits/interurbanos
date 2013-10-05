package com.manutenfruits.interurbanos;

import android.app.Application;

/**
 * Created by manutenfruits on 4/10/13.
 */
public class BusApplication extends Application {

    private static BusApplication singleton;

    public static BusApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
