package com.kolkatahaat.model;

import com.google.firebase.firestore.FieldValue;

public class Users {
    private String userId;
    private String userUId;
    private String userName;
    private String userEmail;
    private String userMobile;
    private String userAddress;
    private String userToken;
    private int userType;
    private Object userCreatedDate;
    private Object userUpdateDate;


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


    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
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

    public Object getUserCreatedDate() {
        return userCreatedDate;
    }

    public void setUserCreatedDate(Object userCreatedDate) {
        this.userCreatedDate = userCreatedDate;
    }

    public Object getUserUpdateDate() {
        return userUpdateDate;
    }

    public void setUserUpdateDate(Object userUpdateDate) {
        this.userUpdateDate = userUpdateDate;
    }
}
