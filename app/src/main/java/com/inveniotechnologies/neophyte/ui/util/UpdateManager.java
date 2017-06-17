package com.inveniotechnologies.neophyte.ui.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.inveniotechnologies.neophyte.BuildConfig;
import com.inveniotechnologies.neophyte.network.clients.ApiClient;
import com.inveniotechnologies.neophyte.network.interfaces.ApiInterface;
import com.inveniotechnologies.neophyte.network.models.Release;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by winner-timothybolorunduro on 16/06/2017.
 */

public class UpdateManager {
    public void update(final Activity context) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Release> call = apiInterface.getReleases();
        call.enqueue(new Callback<Release>() {
            @Override
            public void onResponse(Call<Release> call, Response<Release> response) {
                final Release release = response.body();
                if (release != null) {
                    final String tagName = release.getTagName();
                    final String versionName = BuildConfig.VERSION_NAME;
                    if (!tagName.equals(versionName)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(
                                "There is a new version.\nName: "
                                        + release.getName()
                                        + "\nVersion: "
                                        + release.getTagName()
                                        + "\nDo you want to download it?"
                        )
                                .setCancelable(false)
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DownloadManager downloadManager = new DownloadManager();
                                        downloadManager.download(context, release);
                                    }
                                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Updater:", "Declined to update.");
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Update App");
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Release> call, Throwable t) {
                Log.d("Updater Error:", t.toString());
            }
        });
    }
}
