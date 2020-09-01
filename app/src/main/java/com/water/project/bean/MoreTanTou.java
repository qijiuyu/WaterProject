package com.water.project.bean;

import java.io.Serializable;

public class MoreTanTou implements Serializable {

    private String maishen;
    private String midu;
    private String pianyi;

    public String getMaishen() {
        return maishen;
    }

    public void setMaishen(String maishen) {
        this.maishen = maishen;
    }

    public String getMidu() {
        return midu;
    }

    public void setMidu(String midu) {
        this.midu = midu;
    }

    public String getPianyi() {
        return pianyi;
    }

    public void setPianyi(String pianyi) {
        this.pianyi = pianyi;
    }
}
