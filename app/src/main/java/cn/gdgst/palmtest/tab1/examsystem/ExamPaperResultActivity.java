package cn.gdgst.palmtest.tab1.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.gdgst.entity.ExamTopic;
import cn.gdgst.palmtest.R;

/**
 * Created by JenfeeMa on 2016/12/30.
 * All right reserved
 * email 1017033168@qq.com
 */

public class ExamPaperResultActivity extends AppCompatActivity {
    private ListView listView;
    private Intent intent;
    private ArrayList<ExamTopic> examTopicsList;
    private HashMap<Integer, String> hashmap_selected_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exampaper_result);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("试题详解");
        listView = (ListView) findViewById(R.id.activity_exampaper_result);
        intent = getIntent();
        examTopicsList = (ArrayList<ExamTopic>) intent.getSerializableExtra("examTopicList");
        hashmap_selected_result = (HashMap<Integer, String>) intent.getSerializableExtra("hashmap_selected_result");
        ExamPaperResultAdapter examPaperResultAdapter = new ExamPaperResultAdapter(this,examTopicsList, hashmap_selected_result);
        listView.setAdapter(examPaperResultAdapter);
    }
}
