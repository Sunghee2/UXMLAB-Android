package com.example.hh960.uxmlab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupPage  extends Activity {
    private EditText editTextId;
    private EditText editTextPw;
    private EditText editTextPw2; // 비밀번호 확인용
    private EditText editTextName;
    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        editTextId = (EditText) findViewById(R.id.new_id);
        editTextPw = (EditText) findViewById(R.id.new_pw);
        editTextPw2 = (EditText) findViewById(R.id.new_pw_check);
        editTextName = (EditText) findViewById(R.id.new_name);
        editTextEmail = (EditText) findViewById(R.id.new_email);

    }

    public static boolean checkEmail(String email){
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    public void insert(View view) {
        String id = editTextId.getText().toString();
        String password = editTextPw.getText().toString();
        String check_password = editTextPw2.getText().toString();
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();

        if (id.length() != 8) {
            Toast.makeText(getApplicationContext(), "아이디를 잘못 입력하셨습니다.", Toast.LENGTH_LONG).show();
        } else if(password.length() < 8){
            Toast.makeText(getApplicationContext(), "비밀번호를 8글자 이상 입력해주세요.", Toast.LENGTH_LONG).show();
        } else if(!password.equals(check_password)){
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
        } else if(!checkEmail(email)){
            Toast.makeText(getApplicationContext(), "이메일 형식이 맞지 않습니다.", Toast.LENGTH_LONG).show();
        } else {
            insertoToDatabase(id, password, name, email);
        }
    }
    private void insertoToDatabase(String id, String password, String name, String email) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignupPage.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("failure")){
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                } else if(s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
    @Override
    protected String doInBackground(String... params) {

        try {
            String id = (String) params[0];
            String password = (String) params[1];
            String name = (String) params[2];
            String email = (String) params[3];

            String link = "http://10.0.2.2:8080/uxmlab_regis.php";
            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }
}
        InsertData task = new InsertData();
                task.execute(id, password, name, email);
                }


}