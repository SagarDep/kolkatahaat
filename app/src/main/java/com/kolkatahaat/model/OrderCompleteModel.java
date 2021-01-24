package com.kolkatahaat.model;


public class OrderCompleteModel {

    private String userId;
    private String docId;
    private BillItem billItem;
    //private String orderStatus; //Pending , Accept, Dispatch, Delivered
    private Object billCreatedDate;

    public OrderCompleteModel(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


    public BillItem getBillItem() {
        return billItem;
    }

    public void setBillItem(BillItem billItem) {
        this.billItem = billItem;
    }

    public Object getBillCreatedDate() {
        return billCreatedDate;
    }

    public void setBillCreatedDate(Object billCreatedDate) {
        this.billCreatedDate = billCreatedDate;
    }
}
