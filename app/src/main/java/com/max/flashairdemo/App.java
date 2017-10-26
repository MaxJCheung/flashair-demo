package com.max.flashairdemo;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class App extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}
