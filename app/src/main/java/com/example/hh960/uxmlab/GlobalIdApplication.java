package com.example.hh960.uxmlab;

import android.app.Application;

public class GlobalIdApplication extends Application {
    private String id;
    private boolean isStudent;

    public void setIsStudent(boolean student) {
        isStudent = student;
    }

    public boolean getIsStudent() {
        return isStudent;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
