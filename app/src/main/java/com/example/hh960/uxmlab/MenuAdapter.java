package com.example.hh960.uxmlab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

        edit_course_btn.setTag(position);
        edit_course_btn.setOnClickListener(editBtnClickListener);

        delete_course_btn.setTag(position);
        delete_course_btn.setOnClickListener(deleteBtnClickListener);

        return view;
    }


    Button.OnClickListener editBtnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = Integer.parseInt(view.getTag().toString());
            Intent intent = new Intent(context, EditCourseActivity.class);
            intent.putExtra("course_no", list_munuArrayList.get(position).getCourse_no());
            context.startActivity(intent);
        }
    };

    Button.OnClickListener deleteBtnClickListener = new Button.OnClickListener(){
      @Override
      public void onClick(View view) {
          final int position = Integer.parseInt(view.getTag().toString());
          AlertDialog.Builder alert = new AlertDialog.Builder(context);

          alert.setTitle("강의 삭제");
          alert.setMessage(list_munuArrayList.get(position).getCourse_name()+"를 삭제하시겠습니까?");

          alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  deleteCourse(list_munuArrayList.get(position).getCourse_no());
              }
          });

          alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {

              }
          });
          AlertDialog dialog = alert.create();
          dialog.show();
      }
    };

    private void deleteCourse(String course_no){
        class DeleteData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(context, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("failure")){
                    Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                } else if(s.equals("success")){
                    Toast.makeText(context, "성공적으로 강의를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, CourseListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }

            @Override
            protected String doInBackground(String... params){
                try{
                    String course_no = (String) params[0];

                    String link = "http://10.0.2.2:8080/uxmlab_course_delete.php";
                    String data = URLEncoder.encode("course_no", "UTF-8")+"="+URLEncoder.encode(course_no, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line=reader.readLine())!=null){
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch(Exception e){
                    return new String("Exception: "+e.getMessage());
                }
            }
        }
        DeleteData task = new DeleteData();
        task.execute(course_no);

    }
}
