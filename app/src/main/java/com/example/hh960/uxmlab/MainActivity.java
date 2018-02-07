package com.example.hh960.uxmlab;

import android.app.Application;
import android.app.ProgressDialog;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;


import android.view.View.OnClickListener;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewFlipper Vf;
    Button BtnSignIn, BtnSignUp;
    EditText inputID, inputPW;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    ArrayList<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        BtnSignUp = (Button)findViewById(R.id.btn_signup);
        BtnSignIn = (Button)findViewById(R.id.btn_signin);
        inputID = (EditText)findViewById(R.id.user_id);
        inputPW = (EditText)findViewById(R.id.user_pw);


        BtnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }
        });
    }

    void login() {
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://10.0.2.2:8080/uxmlab_login.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", inputID.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", inputPW.getText().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);

            if(response.equalsIgnoreCase("No Such User Found")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "아이디 혹은 비밀번호 오류입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    }
                });
                GlobalIdApplication idApp = (GlobalIdApplication) getApplication();
                idApp.setId(inputID.getText().toString());
                idApp.setIsStudent(GetIsStudent(response));
                startActivity((new Intent(MainActivity.this, MenuPage.class)));
                finish();
            }
        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void CliSignUp(View view)
    {
        Intent intent = new Intent(this, SignupPage.class);
        startActivity(intent);
    }

    private boolean GetIsStudent(String response){
        boolean is_student = true;
        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//            String json = reader.readLine();
            JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            is_student = (1==jsonObject.optInt("is_student"));
        } catch (Exception e){

        }
        return is_student;
    }
}
