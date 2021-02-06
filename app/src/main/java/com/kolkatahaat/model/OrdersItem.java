package com.kolkatahaat.model;

import java.util.ArrayList;

public class OrdersItem {
    private String productId;
    private String docId;
    private String uuId;
    private String productImg;
    private String productCategory;
    private String productName;

    //private ArrayList<QuantityPrice> productQuantityPrice;
    private String productPacking;
    private float productPrice;
    private int productQuantity = 0;

    //private String productDeliveryChange;
    private String productItemTotal;
    private float productTotalAmount;

    private Object productCreatedDate;


    public OrdersItem(){

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

   /* public ArrayList<QuantityPrice> getProductQuantityPrice() {
        return productQuantityPrice;
    }

    public void setProductQuantityPrice(ArrayList<QuantityPrice> productQuantityPrice) {
        this.productQuantityPrice = productQuantityPrice;
    }*/

    public String getProductPacking() {
        return productPacking;
    }

    public void setProductPacking(String productPacking) {
        this.productPacking = productPacking;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    /*public String getProductDeliveryChange() {
        return productDeliveryChange;
    }

    public void setProductDeliveryChange(String productDeliveryChange) {
        this.productDeliveryChange = productDeliveryChange;
    }*/

    public String getProductItemTotal() {
        return productItemTotal;
    }

    public void setProductItemTotal(String productItemTotal) {
        this.productItemTotal = productItemTotal;
    }

    public float getProductTotalAmount() {
        return productTotalAmount;
    }

    public void setProductTotalAmount(float productTotalAmount) {
        this.productTotalAmount = productTotalAmount;
    }

    public Object getProductCreatedDate() {
        return productCreatedDate;
    }

    public void setProductCreatedDate(Object productCreatedDate) {
        this.productCreatedDate = productCreatedDate;
    }
}
