package com.inveniotechnologies.neophyte;

import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bolorundurowb on 10/15/16.
 */

public class FoursquareNeophyte extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        /* Check for updates */
        new Thread(new Runnable() {
            @Override
            public void run() {
                runUpdater();
            }
        }).start();
    }

    private void runUpdater() {
        try {
            URL url = new URL("https://api.github.com/repos/bolorundurowb/Foursquare-Neophyte/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Make things readable
            String responseString;
            while ((responseString = bufferedReader.readLine()) != null) {
                stringBuffer.append(responseString);
            }
            System.out.println(stringBuffer.toString());
        } catch (IOException ex) {
            System.out.println("The URL was malformed. " + ex.getMessage());
        }

    }
}
