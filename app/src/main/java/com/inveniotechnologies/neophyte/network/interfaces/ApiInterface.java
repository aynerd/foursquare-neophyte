package com.inveniotechnologies.neophyte.network.interfaces;

import com.inveniotechnologies.neophyte.network.models.Release;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by bolorundurowb on 12/9/16.
 */

public interface ApiInterface {
    @GET("repos/bolorundurowb/Foursquare-Neophyte/releases/latest")
    Call<Release> getReleases();
}
