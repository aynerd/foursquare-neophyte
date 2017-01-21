package com.inveniotechnologies.neophyte.network.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bolorundurowb on 12/9/16.
 */

public class Release {
    @SerializedName("name")
    private String Name;

    @SerializedName("id")
    private int Id;

    @SerializedName("tag_name")
    private String TagName;

    @SerializedName("assets")
    private List<Asset> Assets = new ArrayList<Asset>();

    public Release(String name, int id, String tagName, List<Asset> assets) {
        Name = name;
        Id = id;
        TagName = tagName;
        Assets = assets;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public List<Asset> getAssets() {
        return Assets;
    }

    public void setAssets(List<Asset> assets) {
        Assets = assets;
    }
}
