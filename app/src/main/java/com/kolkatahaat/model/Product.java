package com.kolkatahaat.model;

import com.google.firebase.firestore.FieldValue;

public class Product {
    private String productId;
    private String productImg;
    private String productCategory;
    private String productName;
    private String productPacking;
    private String productPrice;
    private String productDeliveryChange;
    private FieldValue productCreatedDate;

    public Product(){

    }



    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getProductPacking() {
        return productPacking;
    }

    public void setProductPacking(String productPacking) {
        this.productPacking = productPacking;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDeliveryChange() {
        return productDeliveryChange;
    }

    public void setProductDeliveryChange(String productDeliveryChange) {
        this.productDeliveryChange = productDeliveryChange;
    }

    public FieldValue getProductCreatedDate() {
        return productCreatedDate;
    }

    public void setProductCreatedDate(FieldValue productCreatedDate) {
        this.productCreatedDate = productCreatedDate;
    }
}
