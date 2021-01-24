package com.kolkatahaat.model;

public class AboutUs {

    private String aboutDetails;
    private String aboutPhone;
    private String aboutWeb;
    private String aboutEmail;
    private Object billCreatedDate;

    public AboutUs(){

    }

    public String getAboutDetails() {
        return aboutDetails;
    }

    public void setAboutDetails(String aboutDetails) {
        this.aboutDetails = aboutDetails;
    }

    public String getAboutPhone() {
        return aboutPhone;
    }

    public void setAboutPhone(String aboutPhone) {
        this.aboutPhone = aboutPhone;
    }

    public String getAboutWeb() {
        return aboutWeb;
    }

    public void setAboutWeb(String aboutWeb) {
        this.aboutWeb = aboutWeb;
    }

    public String getAboutEmail() {
        return aboutEmail;
    }

    public void setAboutEmail(String aboutEmail) {
        this.aboutEmail = aboutEmail;
    }

    public Object getBillCreatedDate() {
        return billCreatedDate;
    }

    public void setBillCreatedDate(Object billCreatedDate) {
        this.billCreatedDate = billCreatedDate;
    }
}
