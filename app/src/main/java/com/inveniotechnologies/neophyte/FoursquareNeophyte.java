package com.inveniotechnologies.neophyte;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bolorundurowb on 10/15/16.
 */

public class FoursquareNeophyte extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
