package com.inveniotechnologies.neophyte.network.util;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by winner-timothybolorunduro on 16/06/2017.
 */

public class Uploader implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "Neophyte: ";
    private GoogleApiClient mGoogleApiClient;
    private Activity mContext;
    private final ResultCallback<DriveFolder.DriveFileResult> fileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
            if (driveFileResult.getStatus().isSuccess()) {
                Toast.makeText(mContext, "File " + driveFileResult.getDriveFile().getDriveId() + " written successfully.", Toast.LENGTH_LONG).show();
            }
        }
    };
    private String mCsvString;
    private String mFileName;
    private final ResultCallback<DriveApi.DriveContentsResult> driveContentsResultResultCallback
            = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
            if (driveContentsResult.getStatus().isSuccess()) {
                createFileOnDrive(driveContentsResult);
            }
        }
    };

    public Uploader(Activity context) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(mContext, "Google connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());

        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(mContext, connectionResult.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {
            connectionResult.startResolutionForResult(mContext, 1001);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    private void createFile(String fileName, String csvString) {
        mCsvString = csvString;
        mFileName = fileName;

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsResultResultCallback);
    }

    private void createFileOnDrive(DriveApi.DriveContentsResult result) {
        final DriveContents driveContents = result.getDriveContents();
        new Thread() {
            @Override
            public void run() {
                OutputStream outputStream = driveContents.getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);

                try {
                    writer.write(mCsvString);
                    writer.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(mFileName)
                        .setMimeType("text/plain")
                        .build();
                Drive.DriveApi.getAppFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, driveContents)
                        .setResultCallback(fileCallback);
            }
        };
    }
}
