package cn.gdgst.palmtest.tab1.examsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.gdgst.palmtest.bean.ExamTopic;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.bean.TExamTopic;

public class ExamPaperResultActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private Intent intent;
    private ArrayList<ExamTopic> examTopicsList;

    private ArrayList<TExamTopic> ArrayList_TExamTopic;
    private ArrayList<TExamTopic> ArrayList_JExamTopic;

    private HashMap<Integer, String> hashmap_selected_result;
    private FrameLayout frameLayout;
    private Button buttonX,buttonTJ;
    private XResultFragment xResultFragment;
    private TJResultFragment tjResultFragment;
    HashMap<Integer, String> hashmap_selected_resultS;
    private List<String> ListJAnswer;
    private List<String> ListT = new ArrayList<>();
    private int AllCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exampaper_result);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("试题详解");
        //listView = (ListView) findViewById(R.id.activity_exampaper_result);
        buttonX = (Button) findViewById(R.id.activity_exampaper_result_button_x);
        buttonX.setOnClickListener(this);
        buttonTJ = (Button) findViewById(R.id.activity_exampaper_result_button_tj);
        buttonTJ.setOnClickListener(this);
        intent = getIntent();
        examTopicsList = (ArrayList<ExamTopic>) intent.getSerializableExtra("examTopicList");
        hashmap_selected_result = (HashMap<Integer, String>) intent.getSerializableExtra("hashmap_selected_result");
        ArrayList_TExamTopic = (ArrayList<TExamTopic>) intent.getSerializableExtra("TexamTopicList");
        ArrayList_JExamTopic = (ArrayList<TExamTopic>) intent.getSerializableExtra("JexamTopicList");
        hashmap_selected_resultS = (HashMap<Integer, String>) intent.getSerializableExtra("hashmap_selected_resultS");
        ListJAnswer = (List<String>) intent.getSerializableExtra("ListJAnswer");
        AllCount = ArrayList_TExamTopic.size() + ArrayList_JExamTopic.size();
        ArrayList_TExamTopic.addAll(ArrayList_JExamTopic);
        /**
         * 把HashMap答案转换成List答案
         */
        for (Integer order:hashmap_selected_resultS.keySet()) {
            ListT.add(hashmap_selected_resultS.get(order));
        }
        /**
         * 把解答题的答案合并到一个集合里面
         */
        ListT.addAll(ListJAnswer);

        setDefaultFragment();
        //ExamPaperResultAdapter examPaperResultAdapter = new ExamPaperResultAdapter(this,examTopicsList, hashmap_selected_result);
        //listView.setAdapter(examPaperResultAdapter);
    }

    private void setDefaultFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (xResultFragment == null) {
            xResultFragment = new XResultFragment();
            xResultFragment.setParams(examTopicsList, hashmap_selected_result);
            fragmentTransaction.replace(R.id.activity_exampaper_result_FrameLayout, xResultFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.activity_exampaper_result_button_x:
                if (xResultFragment == null) {
                    xResultFragment = new XResultFragment();
                    xResultFragment.setParams(examTopicsList, hashmap_selected_result);
                }
                ft.replace(R.id.activity_exampaper_result_FrameLayout, xResultFragment);
                break;
            case R.id.activity_exampaper_result_button_tj:
                if (tjResultFragment == null) {
                    tjResultFragment = new TJResultFragment(ArrayList_TExamTopic, ListT, AllCount);
                }
                ft.replace(R.id.activity_exampaper_result_FrameLayout, tjResultFragment);
                break;
        }
        ft.commit();
    }
}
