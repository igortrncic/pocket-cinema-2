package com.trncic.igor.pocketcinema;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by igortrncic on 7/6/15.
 */
public class PocketCinemaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default
    }
}
