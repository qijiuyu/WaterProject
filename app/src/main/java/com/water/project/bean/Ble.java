package com.water.project.bean;

import java.io.Serializable;

public class Ble implements Serializable {

    private String bleName;

    private String bleMac;

    public Ble(){}

    public Ble(String bleName,String bleMac){
        this.bleMac=bleMac;
        this.bleName=bleName;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }
}
