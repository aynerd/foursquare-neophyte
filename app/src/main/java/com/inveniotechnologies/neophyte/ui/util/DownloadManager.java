package com.inveniotechnologies.neophyte.ui.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.inveniotechnologies.neophyte.R;
import com.inveniotechnologies.neophyte.network.models.Release;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static android.R.attr.id;

/**
 * Created by winner-timothybolorunduro on 16/06/2017.
 */

public class DownloadManager {
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public void download(final Activity context, Release release) {
        if (release.getAssets().size() > 0) {
            final String tagName = release.getTagName();
            final String updateUrl = release.getAssets()
                    .get(0).getDownloadUrl();
            notificationBuilder = new NotificationCompat.Builder(context);
            notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE
            );
            notificationBuilder.setContentTitle("Foursquare Update")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.mipmap.ic_launcher);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int count;
                        URL url = new URL(updateUrl);
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        // Get the file length
                        int fileLength = urlConnection.getContentLength();
                        //
                        InputStream inputStream = new BufferedInputStream(
                                url.openStream()
                        );
                        final File folder = new File(
                                Environment.getExternalStorageDirectory()
                                        + "/FoursquareNewcomers/Updates"
                        );
                        if (!folder.exists())
                            folder.mkdir();
                        final File file = new File(
                                folder.getAbsolutePath()
                                        + "/" + tagName
                                        + ".apk"
                        );
                        if (!file.exists())
                            file.createNewFile();
                        OutputStream outputStream = new FileOutputStream(
                                file
                        );
                        // Download the file and update the notification
                        byte data[] = new byte[1024];
                        long total = 0;
                        while ((count = inputStream.read(data)) != -1) {
                            total += count;
                            notificationBuilder.setProgress(
                                    100,
                                    (int) ((total * 100) / fileLength),
                                    false
                            );
                            notificationManager.notify(
                                    id,
                                    notificationBuilder.build()
                            );
                            outputStream.write(data, 0, count);
                        }
                        // Clean up
                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();
                        // Open the installer
                        Intent intent = new Intent();
                        intent.setAction(
                                Intent.ACTION_VIEW
                        );
                        intent.setDataAndType(
                                Uri.fromFile(file),
                                MimeTypeMap.getSingleton()
                                        .getMimeTypeFromExtension("apk")
                        );
                        PendingIntent pendingIntent = PendingIntent
                                .getActivity(
                                        context,
                                        0,
                                        intent,
                                        0
                                );
                        // Display the new status
                        notificationBuilder
                                .setContentText("Download complete")
                                .setProgress(0, 0, false)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManager.notify(
                                id,
                                notificationBuilder.build()
                        );
                    } catch (Exception e) {
                        Log.e("Updater:", "Error occurred.");
                    }
                }
            }).start();
        }
    }
}
