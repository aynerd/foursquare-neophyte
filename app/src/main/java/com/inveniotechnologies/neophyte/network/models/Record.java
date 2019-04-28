package com.inveniotechnologies.neophyte.network.models;

/**
 * Created by bolorundurowb on 24-Aug-16.
 */
public class Record {
    private String Title;
    private String HomeAddress;
    private String HomeTel;
    private String OfficeTel;
    private String Mobile;
    private String Email;
    private String InvitedBy;
    private String BirthDay;
    private String AgeGroup;
    private String Decisions;
    private String Comments;
    private String FullName;

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        HomeAddress = homeAddress;
    }

    public String getHomeTel() {
        return HomeTel;
    }

    public void setHomeTel(String homeTel) {
        HomeTel = homeTel;
    }

    public String getOfficeTel() {
        return OfficeTel;
    }

    public void setOfficeTel(String officeTel) {
        OfficeTel = officeTel;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getInvitedBy() {
        return InvitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        InvitedBy = invitedBy;
    }

    public String getBirthDay() {
        return BirthDay;
    }

    public void setBirthDay(String birthDay) {
        BirthDay = birthDay;
    }

    public String getAgeGroup() {
        return AgeGroup;
    }

    public void setAgeGroup(String ageGroup) {
        AgeGroup = ageGroup;
    }

    public String getDecisions() {
        return Decisions;
    }

    public void setDecisions(String decisions) {
        Decisions = decisions;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }
}
