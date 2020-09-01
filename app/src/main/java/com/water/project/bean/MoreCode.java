package com.water.project.bean;

import java.io.Serializable;

public class MoreCode implements Serializable {

    private String code;
    private String other;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
