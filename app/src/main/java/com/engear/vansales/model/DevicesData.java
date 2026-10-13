package com.engear.vansales.model;
/**
 * Created by Rakshit on 03-Feb-17.
 */
public class DevicesData {
    private String address;
    private String name;

    public DevicesData() {
        this.name = "";
        this.address = "";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
