package com.example.murphy.cascara;

import android.app.Application;
/** A subclass of the Application class to hold a single instance of the Repo class */
public class CascaraApplication extends Application {
    CascaraRepository repo = new CascaraRepository(this);

    public CascaraRepository getRepo() {
        return repo;
    }
}
