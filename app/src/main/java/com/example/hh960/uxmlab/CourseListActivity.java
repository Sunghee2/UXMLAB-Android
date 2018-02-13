package com.example.hh960.uxmlab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity{
    DefaultHttpClient httpClient;
    HttpPost httpPost;
    ArrayList<NameValuePair> nameValuePairArrayList;
    private NestedScrollView scrollView;
    private ListView myCourseListView;
    private ListView allCourseListView;
    private MenuAdapter menuAdapter;
    private MenuAdapter menuAdapter2;
    private List<menu_item> menu_itemList;
    private List<menu_item> menu_itemList_my_course;
//    private Button add_course_btn;
    private FloatingActionButton add_course_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        final GlobalIdApplication idApp = (GlobalIdApplication) getApplication();
//        add_course_btn = (Button) findViewById(R.id.add_course_btn);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        add_course_btn = (FloatingActionButton) findViewById(R.id.add_course_btn);
        if(idApp.getIsStudent()==false){
            add_course_btn.setVisibility(View.VISIBLE);
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(scrollY>oldScrollY) {
                        add_course_btn.hide();
                    } else {
                        add_course_btn.show();
                    }
                }
            });
        }

        myCourseListView = (ListView) findViewById(R.id.listview_my_course);
        allCourseListView = (ListView) findViewById(R.id.listview_all_course);

        menu_itemList = new ArrayList<>();

        try {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost("http://10.0.2.2:8080/uxmlab_course_list.php");
            nameValuePairArrayList = new ArrayList<NameValuePair>(1);
            nameValuePairArrayList.add(new BasicNameValuePair("id", idApp.getId()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairArrayList));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpClient.execute(httpPost, responseHandler);
            int check_my_course = new JSONObject(response).optInt("my_course");
            if(check_my_course==1){
                JSONArray jsonArray = new JSONObject(response).getJSONArray("course");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String course_no = jsonObject.optString("course_no");
                    String course_name = jsonObject.optString("course_name");
                    String professor = jsonObject.optString("professor");
                    menu_itemList.add(new menu_item(course_no, course_name, professor));
                }
                menuAdapter = new MenuAdapter(CourseListActivity.this, menu_itemList, idApp.getIsStudent());
                myCourseListView.setAdapter(menuAdapter);
                setListViewHeightBaseOnChildren(myCourseListView);

                menu_itemList_my_course = new ArrayList<>();
                JSONArray jsonArray2 = new JSONObject(response).getJSONArray("all_course");
                for(int i = 0; i < jsonArray2.length(); i++){
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);
                    String course_no = jsonObject.optString("course_no");
                    String course_name = jsonObject.optString("course_name");
                    String professor = jsonObject.optString("professor");
                    menu_itemList_my_course.add(new menu_item(course_no, course_name, professor));
                }
                menuAdapter2 = new MenuAdapter(CourseListActivity.this, menu_itemList_my_course, idApp.getIsStudent());
                allCourseListView.setAdapter(menuAdapter2);
                setListViewHeightBaseOnChildren(allCourseListView);
            } else if(check_my_course==0){
                final List<menu_item> menu_itemList2 = new ArrayList<>();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        menu_itemList2.add(new menu_item("", "등록된 강의가 없습니다.", ""));
                        MenuAdapter menuAdapter2 = new MenuAdapter(CourseListActivity.this, menu_itemList2, idApp.getIsStudent());
                        myCourseListView.setAdapter(menuAdapter2);
                        setListViewHeightBaseOnChildren(myCourseListView);
                    }
                });


                menu_itemList_my_course = new ArrayList<>();
                JSONArray jsonArray2 = new JSONObject(response).getJSONArray("all_course");
                for(int i = 0; i < jsonArray2.length(); i++){
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);
                    String course_no = jsonObject.optString("course_no");
                    String course_name = jsonObject.optString("course_name");
                    String professor = jsonObject.optString("professor");
                    menu_itemList_my_course.add(new menu_item(course_no, course_name, professor));
                }
                menuAdapter2 = new MenuAdapter(CourseListActivity.this, menu_itemList_my_course, idApp.getIsStudent());
                allCourseListView.setAdapter(menuAdapter2);
                setListViewHeightBaseOnChildren(allCourseListView);
            }

            myCourseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(CourseListActivity.this, CourseActivity.class);
                    intent.putExtra("course_no", String.valueOf(menu_itemList.get(i).getCourse_no()));
                    startActivity(intent);
                }
            });

            allCourseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showRegisterCourse(String.valueOf(menu_itemList_my_course.get(i).getCourse_no()));
                }
            });

        } catch (Exception e){

        }
    }

    public void CliAddCourse(View view)
    {
        Intent intent = new Intent(this, AddCourseActivity.class);
        startActivity(intent);
    }

    public void setListViewHeightBaseOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter==null){
            return;
        }

        int totalHeight = 0;

        for(int i = 0; i < listAdapter.getCount(); i++){
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void showRegisterCourse(final String course_no){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("강의 등록");
        alert.setMessage("키를 입력해주세요.");

        final EditText edit_key = new EditText(this);
        alert.setView(edit_key);

        alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String course_key = edit_key.getText().toString();
                findKey(course_key, course_no);
            }
        });

        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    public void findKey(String course_key, String course_no){
        class findData extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... strings) {
                try {
                    String course_key = (String) strings[0];
                    String course_no = (String) strings[1];
                    GlobalIdApplication idApplication = (GlobalIdApplication) getApplication();
                    String id = idApplication.getId();

                    String link = "http://10.0.2.2:8080/uxmlab_course_register.php";
                    String data = URLEncoder.encode("course_key", "UTF-8") + "=" + URLEncoder.encode(course_key, "UTF-8");
                    data += "&"+URLEncoder.encode("course_no", "UTF-8")+"="+URLEncoder.encode(course_no, "UTF-8");
                    data += "&"+URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while((line = reader.readLine())!=null){
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch(Exception e){
                    return new String("Exception: "+e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                if(s.equals("failed")){  //키가 틀린 경우
                    Toast.makeText(getApplicationContext(), "key가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if(s.equals("success")){ // insert 성공한 경우
                    Toast.makeText(getApplicationContext(), "성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    List<menu_item> menu_itemList = new ArrayList<>();;
                    List<menu_item> menu_itemList1 = new ArrayList<>();;
                    try {
                        GlobalIdApplication idApp = (GlobalIdApplication) getApplication();
                        httpClient = new DefaultHttpClient();
                        httpPost = new HttpPost("http://10.0.2.2:8080/uxmlab_course_list.php");
                        nameValuePairArrayList = new ArrayList<NameValuePair>(1);
                        nameValuePairArrayList.add(new BasicNameValuePair("id", idApp.getId()));
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairArrayList));
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        final String response = httpClient.execute(httpPost, responseHandler);
                        Log.e("sdafasfasdfsadf", response);
                        int check_my_course = new JSONObject(response).optInt("my_course");
                        if (check_my_course == 1) {
                            JSONArray jsonArray = new JSONObject(response).getJSONArray("course");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String course_no = jsonObject.optString("course_no");
                                String course_name = jsonObject.optString("course_name");
                                String professor = jsonObject.optString("professor");
                                menu_itemList.add(new menu_item(course_no, course_name, professor));
                            }

                            JSONArray jsonArray2 = new JSONObject(response).getJSONArray("all_course");
                            for (int i = 0; i < jsonArray2.length(); i++) {
                                JSONObject jsonObject2 = jsonArray2.getJSONObject(i);
                                String course_no = jsonObject2.optString("course_no");
                                String course_name = jsonObject2.optString("course_name");
                                String professor = jsonObject2.optString("professor");
                                menu_itemList1.add(new menu_item(course_no, course_name, professor));
                            }
                        }
                    } catch(Exception e){

                    }
                    menuAdapter.setList_munuArrayList(menu_itemList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            menuAdapter.notifyDataSetChanged();
                            myCourseListView.setAdapter(menuAdapter);
                            setListViewHeightBaseOnChildren(myCourseListView);
                        }
                    });

                    menuAdapter2.setList_munuArrayList(menu_itemList1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            menuAdapter2.notifyDataSetChanged();
                            allCourseListView.setAdapter(menuAdapter2);
                            setListViewHeightBaseOnChildren(allCourseListView);
                        }
                    });




                } else if(s.equals("failure")){ // 키는 맞았지만 insert가 되지 않은 경우
                    Toast.makeText(getApplicationContext(), "강의가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        findData task = new findData();
        task.execute(course_key, course_no);
    }

}