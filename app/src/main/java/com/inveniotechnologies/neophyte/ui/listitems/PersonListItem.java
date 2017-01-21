package com.inveniotechnologies.neophyte.ui.listitems;

/**
 * Created by bolorundurowb on 27-Aug-16.
 */
public class PersonListItem {
    private String FullName;
    private String Mobile;
    private String UID;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }
}
