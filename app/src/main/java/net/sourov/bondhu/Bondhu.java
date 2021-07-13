package net.sourov.bondhu;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Bondhu extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
