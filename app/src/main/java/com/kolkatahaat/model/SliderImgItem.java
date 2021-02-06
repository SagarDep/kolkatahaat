package com.kolkatahaat.model;

public class SliderImgItem {
    private String docId;
    private String userUId;
    private String imgUrl;
    private String imgName;
    private Object currentDate;

    public SliderImgItem(){
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserUId() {
        return userUId;
    }

    public void setUserUId(String userUId) {
        this.userUId = userUId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Object getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Object currentDate) {
        this.currentDate = currentDate;
    }
}
