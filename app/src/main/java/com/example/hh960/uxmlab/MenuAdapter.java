package com.example.hh960.uxmlab;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by 630su on 2018-02-04.
 */

public class MenuAdapter extends BaseAdapter{
    private Context context;
    private List<menu_item> list_munuArrayList;
    private boolean isStudent;

    TextView courseNoTextView;

    public void setList_munuArrayList(List<menu_item> list_munuArrayList) {
        this.list_munuArrayList = list_munuArrayList;
    }

    TextView courseNameTextView;
    TextView professorTextView;
    Button edit_course_btn;
    Button delete_course_btn;

    public MenuAdapter(Context context, List<menu_item> list_munuArrayList, boolean isStudent) {
        this.context = context;
        this.list_munuArrayList = list_munuArrayList;
        this.isStudent = isStudent;
    }

    @Override
    public int getCount() {
        return this.list_munuArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list_munuArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.menu_item, null);

        courseNoTextView = (TextView) view.findViewById(R.id.courseNoTextView);
        courseNameTextView = (TextView) view.findViewById(R.id.courseNameTextView);
        professorTextView = (TextView) view.findViewById(R.id.professorTextView);
        edit_course_btn = (Button) view.findViewById(R.id.btn_edit_course);
        delete_course_btn = (Button) view.findViewById(R.id.btn_delete_course);

        courseNoTextView.setText(String.valueOf(list_munuArrayList.get(position).getCourse_no()));
        courseNameTextView.setText(list_munuArrayList.get(position).getCourse_name());
        professorTextView.setText(list_munuArrayList.get(position).getProfessor());

        if(!isStudent){
            edit_course_btn.setVisibility(View.VISIBLE);
            delete_course_btn.setVisibility(View.VISIBLE);
        }

        edit_course_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditCourseActivity.class);
                intent.putExtra("course_no", String.valueOf(list_munuArrayList.get(0).getCourse_no()));
                context.startActivity(intent);
            }
        });

        return view;
    }

}
