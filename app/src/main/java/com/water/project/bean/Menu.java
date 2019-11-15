package com.water.project.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/11/15.
 */

public class Menu implements Serializable {

    private int img;
    private String name;
    public  Menu(int img,String name){
        this.img=img;
        this.name=name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
