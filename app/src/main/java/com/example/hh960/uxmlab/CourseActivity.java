package com.example.hh960.uxmlab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by 630su on 2018-02-07.
 */
//강의 상세 페이지
public class CourseActivity extends AppCompatActivity {
    private TextView tv_course_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Intent intent = getIntent();
        String course_no = intent.getStringExtra("course_no");

        tv_course_no = (TextView) findViewById(R.id.textView);
        tv_course_no.setText(course_no);
    }
}
