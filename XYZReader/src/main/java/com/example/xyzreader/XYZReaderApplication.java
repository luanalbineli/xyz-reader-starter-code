package com.example.xyzreader;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class XYZReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
    }
}
