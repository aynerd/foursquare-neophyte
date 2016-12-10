package com.inveniotechnologies.neophyte.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bolorundurowb on 12/9/16.
 */

public class Asset {
    @SerializedName("browser_download_url")
    private String DownloadUrl;

    public Asset(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }
}
