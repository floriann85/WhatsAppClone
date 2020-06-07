package com.example.fn.WhatsAppClone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("2zsWSMmWCr5w6zXWCADkYRuostIIuPauXnjUM848")
                // if defined
                .clientKey("xekxvDNJhhKDexEeCVkjBwGKPOi4hXb9KduttSHk")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
