package com.kolkatahaat.interfaces;

public interface AdminQuantityDialogListener {
    //void onAction(Object object);
    void onDialogPositive(String QuantityName, float price);
    void onDialogNegative(Object object);
}
