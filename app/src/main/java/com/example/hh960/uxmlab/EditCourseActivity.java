package com.example.hh960.uxmlab;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Created by 630su on 2018-02-11.
 */

public class EditCourseActivity  extends AppCompatActivity{
        private EditText edit_course_key;
        private EditText edit_course_no;
        private EditText edit_course_name;
        private EditText edit_professor;
        private EditText edit_description;
        private Button btn_edit;
        private TextView date_text;
        private Calendar mCurrentDate;
        private int day, month, year;
        private String origin_course_no;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_course);

            Intent intent = getIntent();
            origin_course_no = intent.getStringExtra("course_no");

            edit_course_key = (EditText) findViewById(R.id.edit_course_key);
            edit_course_no = (EditText) findViewById(R.id.edit_course_no);
            edit_course_name = (EditText) findViewById(R.id.edit_course_name);
            edit_professor = (EditText)findViewById(R.id.edit_professor);
            edit_description = (EditText)findViewById(R.id.edit_description);
            btn_edit = (Button) findViewById(R.id.button);
            date_text = (TextView) findViewById(R.id.text_date);

            readCourse(origin_course_no);

            month = month+1;
            date_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(EditCourseActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            monthOfYear = monthOfYear+1;
                            date_text.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    edit(view, origin_course_no);
                }
            });
        }

    private void readCourse(String origin_course_no){
        class readData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(EditCourseActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONArray jsonArray = new JSONObject(s).getJSONArray("result");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    edit_course_key.setText(jsonObject.optString("course_key"));
                    edit_course_no.setText(jsonObject.optString("course_no"));
                    edit_course_name.setText(jsonObject.optString("course_name"));
                    edit_professor.setText(jsonObject.optString("professor"));
                    String description = jsonObject.optString("description");
                    if(!description.equals("null")) {
                        edit_description.setText(jsonObject.optString("description"));
                    }
                    String date[] = jsonObject.optString("start_date").split("[-]");
                    year = Integer.parseInt(date[0]);
                    month = Integer.parseInt(date[1]);
                    day = Integer.parseInt(date[2]);
                    date_text.setText(year+"-"+month+"-"+day);
                } catch(Exception e) {
                }
            }

            @Override
            protected String doInBackground(String... params){
                try{
                    String course_no = (String) params[0];

                    String link = "http://10.0.2.2:8080/uxmlab_course_read.php";
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
        readData task = new readData();
        task.execute(origin_course_no);

    }

    public void edit(View view, String origin_course_no){
        String key = edit_course_key.getText().toString();
        String no = edit_course_no.getText().toString();
        String name = edit_course_name.getText().toString();
        String professor = edit_professor.getText().toString();
        String date = date_text.getText().toString();
        String description = edit_description.getText().toString();

        if(key==null || key.equals("")){
            Toast.makeText(getApplicationContext(), "key를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (no==null || no.equals("")){
            Toast.makeText(getApplicationContext(), "강의번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if(name==null || name.equals("")){
            Toast.makeText(getApplicationContext(), "강의이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if(professor==null || professor.equals("")){
            Toast.makeText(getApplicationContext(), "교수이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            updateCourse(origin_course_no, key, no, name, professor, date, description);
        }
    }

    private void updateCourse(String origin_course_no, String key, String no, String name, String professor, String date, String description){
        class updateData extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(EditCourseActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("failure")){
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                } else if(s.equals("success")){
                    Toast.makeText(getApplicationContext(), "성공적으로 강의를 수정했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditCourseActivity.this, CourseListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            protected String doInBackground(String... params){
                try{
                    String origin_course_no = (String) params[0];
                    String key = (String) params[1];
                    String no = (String) params[2];
                    String name = (String) params[3];
                    String professor = (String) params[4];
                    String date = (String) params[5];
                    String description = (String) params[6];

                    Log.e("Adsfasdf", origin_course_no);
                    Log.e("Adsfasdf", key);
                    Log.e("Adsfasdf", no);
                    Log.e("Adsfasdf", name);
                    Log.e("Adsfasdf", professor);
                    Log.e("Adsfasdf", date);
                    Log.e("Adsfasdf", description);

                    String link = "http://10.0.2.2:8080/uxmlab_course_update.php";
                    String data = URLEncoder.encode("origin_course_no", "UTF-8")+"="+URLEncoder.encode(origin_course_no, "UTF-8");
                    data += "&" + URLEncoder.encode("key", "UTF-8")+"="+URLEncoder.encode(key, "UTF-8");
                    data += "&" + URLEncoder.encode("no", "UTF-8")+"="+URLEncoder.encode(no, "UTF-8");
                    data += "&" +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8");
                    data += "&" +URLEncoder.encode("professor", "UTF-8")+"="+URLEncoder.encode(professor, "UTF-8");
                    data += "&" +URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(date, "UTF-8");
                    data += "&" +URLEncoder.encode("description", "UTF-8")+"="+URLEncoder.encode(description, "UTF-8");

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
        updateData task = new updateData();
        task.execute(origin_course_no, key, no, name, professor, date, description);

    }
}

