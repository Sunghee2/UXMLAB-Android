package com.example.hh960.uxmlab;

/**
 * Created by 630su on 2018-02-04.
 */

public class menu_item {
    private String course_no;
    private String course_name;
    private String professor;

    public menu_item(String course_no, String course_name, String professor) {
        this.course_no = course_no;
        this.course_name = course_name;
        this.professor = professor;
    }

    public String getCourse_no() {
        return course_no;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getProfessor() {
        return professor;
    }

}
