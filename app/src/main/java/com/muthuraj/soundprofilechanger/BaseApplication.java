package com.muthuraj.soundprofilechanger;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Muthuraj on 16/12/17.
 * <p>
 * Jambav, Zoho Corporation
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
