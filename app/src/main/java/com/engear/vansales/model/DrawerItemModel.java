package com.engear.vansales.model;

/**
 * Created by aipxperts on 15/10/19.
 */

public class DrawerItemModel {
    private String name;
    private boolean selected = false;
    private int imageId;

    public DrawerItemModel(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
