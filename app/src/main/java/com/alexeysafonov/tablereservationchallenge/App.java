package com.alexeysafonov.tablereservationchallenge;

import android.app.Application;

import com.alexeysafonov.tablereservationchallenge.model.Model;

/**
 * Application that plays a role of a model holder.
 */
public class App extends Application {

    Model mModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mModel = new Model();
        mModel.init(getApplicationContext());
    }

    public Model getModel() {
        return mModel;
    }
}
