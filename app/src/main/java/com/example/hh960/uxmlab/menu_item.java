package com.example.hh960.uxmlab;

/**
 * Created by 630su on 2018-02-04.
 */

public class menu_item {
    private int course_no;
    private String course_name;
    private String professor;

    public menu_item(int course_no, String course_name, String professor) {
        this.course_no = course_no;
        this.course_name = course_name;
        this.professor = professor;
    }

    public int getCourse_no() {
        return course_no;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setCourse_no(int course_no) {
        this.course_no = course_no;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }
}
