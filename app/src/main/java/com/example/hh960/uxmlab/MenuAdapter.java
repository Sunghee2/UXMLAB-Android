package com.example.hh960.uxmlab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 630su on 2018-02-04.
 */

public class MenuAdapter extends BaseAdapter{
    private Context context;
    private List<menu_item> list_munuArrayList;

    TextView courseNoTextView;

    public void setList_munuArrayList(List<menu_item> list_munuArrayList) {
        this.list_munuArrayList = list_munuArrayList;
    }

    TextView courseNameTextView;
    TextView professorTextView;

    public MenuAdapter(Context context, List<menu_item> list_munuArrayList) {
        this.context = context;
        this.list_munuArrayList = list_munuArrayList;
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

        courseNoTextView.setText(String.valueOf(list_munuArrayList.get(position).getCourse_no()));
        courseNameTextView.setText(list_munuArrayList.get(position).getCourse_name());
        professorTextView.setText(list_munuArrayList.get(position).getProfessor());

        return view;
    }
}
