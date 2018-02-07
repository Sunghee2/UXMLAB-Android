package com.example.hh960.uxmlab;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MenuPage extends AppCompatActivity {
    DefaultHttpClient httpClient;
    HttpPost httpPost;
    HttpResponse response;
    ArrayList<NameValuePair> nameValuePairArrayList;
    LinearLayout myCourseLayout;
    private ListView myCourseListView;
    private ListView allCourseListView;
    private MenuAdapter menuAdapter;
    private List<menu_item> menu_itemList;
    Button add_course_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        GlobalIdApplication idApp = (GlobalIdApplication) getApplication();
        add_course_btn = (Button) findViewById(R.id.add_course_btn);
        if(idApp.getIsStudent()==false){
            add_course_btn.setVisibility(View.VISIBLE);
        }

        myCourseListView = (ListView) findViewById(R.id.listview_my_course);
        allCourseListView = (ListView) findViewById(R.id.listview_all_course);

        menu_itemList = new ArrayList<>();

//        menuAdapter = new MenuAdapter(getApplicationContext(), menu_itemList);
//        myCourseListView.setAdapter(menuAdapter);


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
                    int course_no = jsonObject.optInt("course_no");
                    String course_name = jsonObject.optString("course_name");
                    String professor = jsonObject.optString("professor");
                    menu_itemList.add(new menu_item(course_no, course_name, professor));
                }
                menuAdapter = new MenuAdapter(getApplicationContext(), menu_itemList);
                myCourseListView.setAdapter(menuAdapter);
                setListViewHeightBaseOnChiledren(myCourseListView);

                List<menu_item> menu_itemList_my_course = new ArrayList<>();
                JSONArray jsonArray2 = new JSONObject(response).getJSONArray("all_course");
                for(int i = 0; i < jsonArray2.length(); i++){
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);
                    int course_no = jsonObject.optInt("course_no");
                    String course_name = jsonObject.optString("course_name");
                    String professor = jsonObject.optString("professor");
                    menu_itemList_my_course.add(new menu_item(course_no, course_name, professor));
                }
                menuAdapter = new MenuAdapter(getApplicationContext(), menu_itemList_my_course);
                allCourseListView.setAdapter(menuAdapter);
                setListViewHeightBaseOnChiledren(allCourseListView);
            } else if(check_my_course==0){

            }



        } catch (Exception e){

        }
    }

    public void CliAddCourse(View view)
    {
        Intent intent = new Intent(this, AddCourseActivity.class);
        startActivity(intent);
    }

    public void setListViewHeightBaseOnChiledren(ListView listView){
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


}