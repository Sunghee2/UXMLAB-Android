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

//            edit_course_key.setText("safsdfsdfasf");

            readCourse(origin_course_no);

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
                Log.e("asdfsdf", s);
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
                    String date[] = jsonObject.optString("start_date").split("-");
                    String year = date[1];
                    String month = date[2];
                    String day = date[3];
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
}

