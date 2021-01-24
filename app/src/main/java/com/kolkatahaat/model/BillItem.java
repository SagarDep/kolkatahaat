package com.kolkatahaat.model;

import java.util.ArrayList;

public class BillItem {
    private String userId;
    private String docId;
    private String uuId;
    private ArrayList<OrdersItem> itemArrayList;
    private Users itemUsers;
    private String orderStatus; //Pending , Accept, Dispatch, Delivered
    private Object billCreatedDate;

    public BillItem(){

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

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public ArrayList<OrdersItem> getItemArrayList() {
        return itemArrayList;
    }

    public void setItemArrayList(ArrayList<OrdersItem> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }

    public Users getItemUsers() {
        return itemUsers;
    }

    public void setItemUsers(Users itemUsers) {
        this.itemUsers = itemUsers;
    }

    public Object getBillCreatedDate() {
        return billCreatedDate;
    }

    public void setBillCreatedDate(Object billCreatedDate) {
        this.billCreatedDate = billCreatedDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
