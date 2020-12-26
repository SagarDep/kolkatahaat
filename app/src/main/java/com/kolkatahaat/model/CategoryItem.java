package com.kolkatahaat.model;

import java.util.ArrayList;

public class CategoryItem {
    public enum STATE {
        CLOSED,
        OPENED
    }

    public String name;
    public int level;
    public STATE state = STATE.CLOSED;
    public String designation;
    public ArrayList<CategoryItem> models = new ArrayList<>();

    public CategoryItem(String name, int level , String designation) {
        this.name = name;
        this.level = level;
        this.designation = designation;
    }
}
