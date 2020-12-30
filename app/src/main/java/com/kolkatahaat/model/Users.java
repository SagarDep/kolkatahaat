package com.kolkatahaat.model;

import com.google.firebase.firestore.FieldValue;

public class Users {
    private String userId;
    private String userUId;
    private String userName;
    private String userEmail;
    private String userMobile;
    private String userPassword;
    private String userToken;
    private int userType;
    private FieldValue userCreatedDate;


    public Users(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserUId() {
        return userUId;
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public FieldValue getUserCreatedDate() {
        return userCreatedDate;
    }

    public void setUserCreatedDate(FieldValue userCreatedDate) {
        this.userCreatedDate = userCreatedDate;
    }
}
