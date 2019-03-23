package com.inveniotechnologies.neophyte.ui.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.inveniotechnologies.neophyte.network.models.Release;

/**
 * Created by winner-timothybolorunduro on 16/06/2017.
 */

public class DownloadManager {
    void download(final Activity context, Release release) {
        if (release.getAssets().size() > 0) {
            final String updateUrl = release.getAssets()
                    .get(0).getDownloadUrl();

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
            context.startActivity(intent);
        }
    }
}
