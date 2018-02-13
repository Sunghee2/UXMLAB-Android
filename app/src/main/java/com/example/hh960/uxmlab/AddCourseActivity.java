package com.example.hh960.uxmlab;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;


public class AddCourseActivity extends AppCompatActivity {
    private EditText edit_course_key;
    private EditText edit_course_no;
    private EditText edit_course_name;
    private EditText edit_professor;
    private EditText edit_description;
    private Button btn_add;
    private TextView date_text;
    private Calendar mCurrentDate;
    private int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        edit_course_key = (EditText) findViewById(R.id.edit_course_key);
        edit_course_no = (EditText) findViewById(R.id.edit_course_no);
        edit_course_name = (EditText) findViewById(R.id.edit_course_name);
        edit_professor = (EditText)findViewById(R.id.edit_professor);
        edit_description = (EditText)findViewById(R.id.edit_description);
        btn_add = (Button) findViewById(R.id.button);
        date_text = (TextView) findViewById(R.id.text_date);
        mCurrentDate = Calendar.getInstance();

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        month = month+1;
        date_text.setText(year+"-"+month+"-"+day);
        date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCourseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        date_text.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.show();
                }
            });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert(view);
            }
        });
        }

        public void insert(View view){
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
                insertoToDatabase(key, no, name, professor, date, description);
            }
        }

        private void insertoToDatabase(String key, String no, String name, String professor, String date, String description){
            class InsertDate extends AsyncTask<String, Void, String>{
                ProgressDialog loading;

                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    loading = ProgressDialog.show(AddCourseActivity.this, "Please Wait", null, true, true);
                }

                @Override
                protected void onPostExecute(String s){
                    super.onPostExecute(s);
                    loading.dismiss();
                    if(s.equals("failure")){
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    } else if(s.equals("success")){
                        Toast.makeText(getApplicationContext(), "강의를 추가했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                protected String doInBackground(String... params){
                    try{
                        String key = (String) params[0];
                        String no = (String) params[1];
                        String name = (String) params[2];
                        String professor = (String) params[3];
                        String date = (String) params[4];
                        String description = (String) params[5];

                        String link = "http://10.0.2.2:8080/uxmlab_course_add.php";
                        String data = URLEncoder.encode("key", "UTF-8")+"="+URLEncoder.encode(key, "UTF-8");
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
            InsertDate task = new InsertDate();
            task.execute(key, no, name, professor, date, description);

        }
    }

