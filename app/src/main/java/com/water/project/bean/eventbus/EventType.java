package com.water.project.bean.eventbus;

public class EventType {

    private int status;

    private Object object;

    public EventType(int status){
        this.status=status;
    }

    public EventType(int status,Object object){
        this.status=status;
        this.object=object;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
