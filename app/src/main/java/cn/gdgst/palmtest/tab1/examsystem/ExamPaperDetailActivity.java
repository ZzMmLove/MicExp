package cn.gdgst.palmtest.tab1.examsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.bean.ExamTopic;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.bean.TExamTopic;
import cn.gdgst.palmtest.utils.ToastUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JenfeeMa on 2017/1/11.
 * All right reserved
 * email 1017033168@qq.com
 */

public class ExamPaperDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int THREAD_GET_PAPER_DETAIL_SUCCESS = 0;
    private static final int THREAD_SUBMIT_PAPER_JSON_DATA = 1;
    private ReaderViewPager readerViewPager;
    private ImageView imageView_Shadow;
    private MessageHandler messageHandler;
    private int int_ExamPaperId;
    private String examPaperTitle;
    private ArrayList<ExamTopic> ArrayList_ExamTopic = new ArrayList<>();
    private ArrayList<TExamTopic> ArrayList_TExamTopic = new ArrayList<>();
    private ArrayList<TExamTopic> ArrayList_JExamTopic = new ArrayList<>();
    private Chronometer chronometer;
    private ProgressDialog progressDialog;
    private ImageButton imageButton_previous;
    private ImageButton imageButton_next;
    private Button button_Submit;
    private long long_spend_time;
    private boolean isSuccessSubmitJson;
    private ReaderViewPagerAdapter readerViewPagerAdapter;
    private HashMap<Integer, String> hashmap_selected_result = new HashMap<>();
    HashMap<Integer, ReadFragment> hashMap_ReadFragment;
    private List<List> ListALLTAnswer = new ArrayList<>();
    /**
     * 存放一道填空题答案的列表
     */
    private List<String> ListTAnswer = new ArrayList<>();
    /**
     * 存放解答题答案的列表
     */
    private ArrayList<String> ListJAnswer = new ArrayList<String>();
    /**
     * 选择题的总数
     */
    private int xcontentCount;
    HashMap<Integer, List> hashmap_selected_resultT = new HashMap<Integer, List>();
    HashMap<Integer, String> hashmap_selected_resultS = new HashMap<Integer, String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exampaperdetail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("答题");
        progressDialog = new ProgressDialog(ExamPaperDetailActivity.this);
        showDialog("正在加载...");
        Intent intent = getIntent();
        int_ExamPaperId = intent.getIntExtra("exampaperItemId",0);
        examPaperTitle = intent.getStringExtra("exampaperItemTitle");
        //开启线程从服务器获取试卷内容
        new PostThread().start();
        messageHandler = new MessageHandler();
        chronometer = (Chronometer) findViewById(R.id.chronometer2);
        chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
        button_Submit = (Button) findViewById(R.id.activity_exampaperdetail_button_submit);
        button_Submit.setOnClickListener(this);
        readerViewPager = (ReaderViewPager) findViewById(R.id.activity_exampaperdetail_ReaderViewPager);

        imageView_Shadow = (ImageView) findViewById(R.id.activity_exampaperdetail_shadowView);
        readerViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                imageView_Shadow.setTranslationX(readerViewPager.getWidth() - i1);
                if (hashMap_ReadFragment == null) {
                    return;
                }else if (i == (hashMap_ReadFragment.size()-1)) {
                    ReadFragment readFragment_end = hashMap_ReadFragment.get(i);
                    hashmap_selected_result.put(hashMap_ReadFragment.size(),readFragment_end.getSelected_result());
                }
            }

            @Override
            public void onPageSelected(int i) {
                ReadFragment readFragment;
                hashMap_ReadFragment = readerViewPagerAdapter.getHashMap_ReadFragment();
                if (i == 0) {
                    readFragment = hashMap_ReadFragment.get(0);
                    hashmap_selected_result.put(i+1, readFragment.getSelected_result());
                    String stringJ;
                    //获得解答题的答案列表
                    stringJ = readFragment.getJAnswer();

                } else {
                    readFragment = hashMap_ReadFragment.get(i-1);

                    if (readFragment.getCurrentFragmentState() == 1) {
                        //获得选择题的答案列表
                        if (readFragment.getSelected_result() != null) {
                            Log.d("ExamPaperDetailActivity", "打印选择题:"+readFragment.getSelected_result());
                            hashmap_selected_result.put(i, readFragment.getSelected_result());
                        }
                    } else if (readFragment.getCurrentFragmentState() == 2) {
                        //获得填空题的答案HashMap列表,并转换成ArrayList列表中
                        HashMap<Integer, String> hashMapResult;
                        hashMapResult = readFragment.getTAnswerList();
                        if (!hashMapResult.isEmpty()) {
                            ListTAnswer.clear();
                            ListALLTAnswer.clear();
                            for (int n = 1; n <= hashMapResult.size(); n ++) {
                                ListTAnswer.add(hashMapResult.get(n));
                                Log.d("ExamPaperDetailActivity", "打印填空题："+hashMapResult.get(n));
                            }
                            if (!ListTAnswer.isEmpty()) {
                                //把每一道填空题的答案放入到所有的填空题列表中
                                Iterator i1 = ListTAnswer.iterator();
                                StringBuilder sb = new StringBuilder();
                                    while (i1.hasNext()) {
                                        sb.append(i1.next().toString());
                                        sb.append(",");
                                    }
                                Log.d("ExamPaperDetailActivity", "字符串储存输出:"+sb);
                                hashmap_selected_resultS.put(i, sb.toString());
                                hashmap_selected_resultT.put(i, ListTAnswer);

                                for (int a:hashmap_selected_resultS.keySet()) {
                                    Log.d("ExamPaperDetailActivity", "序列化成字符串输出->"+a+"序列化成字符串输出->"+hashmap_selected_resultS.get(a));
                                }

                                ListALLTAnswer.add(ListTAnswer);
                            }
                        }
                    } else if (readFragment.getCurrentFragmentState() == 3) {
                        //获得解答题的答案列表
                        String stringJ;
                        stringJ = readFragment.getJAnswer();
                        if (stringJ != null) {
                            ListJAnswer.clear();
                            ListJAnswer.add(stringJ);
                        }
                    }
                }
                if (i == ((ArrayList_ExamTopic.size() + ArrayList_TExamTopic.size() + ArrayList_JExamTopic.size()) - 1)) {
                    button_Submit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        imageButton_previous = (ImageButton) findViewById(R.id.activity_exampaperdetail_imageButton_previous);
        imageButton_previous.setOnClickListener(this);
        imageButton_next = (ImageButton) findViewById(R.id.activity_exampaperdetail_imageButton_next);
        imageButton_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_exampaperdetail_imageButton_previous:
                int currentItemId = readerViewPager.getCurrentItem();
                currentItemId = currentItemId + 1;
                if (currentItemId > (ArrayList_ExamTopic.size() - 1)) {
                    return;
                }
                readerViewPager.setCurrentItem(currentItemId, true);
                break;
            case R.id.activity_exampaperdetail_imageButton_next:
                int currentItemId1 = readerViewPager.getCurrentItem();
                currentItemId1 = currentItemId1 - 1;
                if (currentItemId1 < 0) {
                    return;
                }
                readerViewPager.setCurrentItem(currentItemId1, true);
                break;
            case R.id.activity_exampaperdetail_button_submit:
                int FragmentCount = ArrayList_ExamTopic.size() + ArrayList_TExamTopic.size() + ArrayList_JExamTopic.size();
                ReadFragment readFragment_end = hashMap_ReadFragment.get(FragmentCount - 1);
                hashmap_selected_result.put(hashMap_ReadFragment.size(),readFragment_end.getSelected_result());

                //把最后一道解答题的答案加入到ListJAnswer中
                String endJContentAnswer = readFragment_end.getJAnswer();
                if (endJContentAnswer != null) {
                    ListJAnswer.add(endJContentAnswer);
                }

                HashMap<Integer, ReadFragment> hashMap_ReadFragment1 = readerViewPagerAdapter.getHashMap_ReadFragment();
                for (int x = 1; x <= hashMap_ReadFragment.size(); x ++){
                    hashmap_selected_result.put(x, hashMap_ReadFragment1.get(x-1).getSelected_result());
                }
                //打印测试
                printlTest();
                List<EditText> listEdits = new ArrayList<>();
                listEdits = readFragment_end.getListEditText();
                for (int i = 0 ; i < listEdits.size(); i ++){
                    if (TextUtils.isEmpty(listEdits.get(i).getText().toString())){
                        ToastUtil.show("您还没有作答的题,请答完再交卷...");
                        return;
                    }
                }

                showInputDialog();
                break;
        }
    }

    private void printlTest(){
        //打印所有选择题的答案
        for (int y = 1; y <= hashmap_selected_result.size(); y ++) {
            String test = hashmap_selected_result.get(y);
            Log.v("ExamPaperDetailActivity", "打印所有选择题的答案:"+String.valueOf(y)+test);
        }

        for (int a:hashmap_selected_resultS.keySet()) {
            Log.d("ExamPaperDetailActivity", "点击之后序列化成字符串输出->"+a+"点击之后序列化成字符串输出->"+hashmap_selected_resultS.get(a));
        }

        Log.d("ExamPaperDetailActivity", "所有填空题的总数="+ListALLTAnswer.size());
        //打印所有填空题的答案
        for (int f = 0; f < ListALLTAnswer.size(); f ++) {
            List<String> printTest = ListALLTAnswer.get(f);


                  /*  for (int g = 0; g < printTest.size(); g ++) {
                        Log.d("ExamPaperDetailActivity", "第"+f+"道填空题的答案:"+printTest.get(g));

                    }*/
        }

        //打印所有解答题的答案
        for (int h = 0; h < ListJAnswer.size(); h ++) {
            Log.d("ExamPaperDetailActivity", "第"+h+"道解答题的答案:"+ListJAnswer.get(h));
        }
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case THREAD_GET_PAPER_DETAIL_SUCCESS://请求试卷内容
                    JSONObject jsonObjectExamPaper = (JSONObject) msg.obj;
                    try {
                        boolean success = jsonObjectExamPaper.getBoolean("success");
                        int error_code = jsonObjectExamPaper.getInt("error_code");
                        String message = jsonObjectExamPaper.getString("message");
                        /**
                         * 如果获取试卷详细内容不成功，则提示用户，终止剩余操作
                         */
                        if (success == false) {
                            Toast.makeText(ExamPaperDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject jsonObjectXExamTopic = jsonObjectExamPaper.optJSONObject("xcontent");
                            JSONObject jSONObjectTExamTopic = jsonObjectExamPaper.optJSONObject("tcontent");
                            JSONObject jSONObjectJExamTopic = jsonObjectExamPaper.optJSONObject("jcontent");
                            if (jsonObjectXExamTopic != null) {
                            /**
                             * 此部分代码为手动解析选择题json数据
                             * 循环创建10道题的对象,并把每个对象加入到ArrayList_ExamTopic
                             */
                            for (int i = 1; i <= jsonObjectXExamTopic.length(); i++) {
                                ExamTopic examTopic = new ExamTopic();
                                JSONObject jsonObject_Topic = jsonObjectXExamTopic.getJSONObject(String.valueOf(i));
                                Map<Integer, String> map_Answer = new HashMap<>();
                                /**
                                 * 选项A
                                 */
                                String string_optionA = jsonObject_Topic.getString("0");
                                examTopic.setOptionA(string_optionA);
                                map_Answer.put(0, string_optionA);
                                /**
                                 * 选项B
                                 */
                                String string_optionB = jsonObject_Topic.getString("1");
                                examTopic.setOptionB(string_optionB);
                                map_Answer.put(1, string_optionB);
                                /**
                                 * 选项C
                                 */
                                String string_optionC = jsonObject_Topic.getString("2");
                                examTopic.setOptionC(string_optionC);
                                map_Answer.put(2, string_optionC);
                                /**
                                 * 选项D
                                 */
                                String string_optionD = jsonObject_Topic.getString("3");
                                examTopic.setOptionD(string_optionD);
                                map_Answer.put(3, string_optionD);
                                /**
                                 * 题目
                                 */
                                String string_title= jsonObject_Topic.getString("title");
                                examTopic.setTitle(string_title);
                                /**
                                 * 题目的图片的路径
                                 */
                                String string_img = jsonObject_Topic.getString("img");
                                examTopic.setImg(string_img);
                                /**
                                 * 题目的正确答案
                                 */
                                String string_right = jsonObject_Topic.getString("right");
                                examTopic.setRight(string_right);

                                /**
                                 * 该题的正确的答案的解析
                                 */
                                String string_analysis = jsonObject_Topic.getString("jiexi");
                                examTopic.setAnalysis(string_analysis);
                                /**
                                 * 题目的序号
                                 */
                                examTopic.setId(i);
                                /**
                                 * 把ABCD四个选项的文本加入到examTopic对象中
                                 */
                                examTopic.setMap_Answer(map_Answer);
                                /**
                                 * 当一道题的对象创建完毕之后，把这道题加入到examTopList中
                                 */
                                ArrayList_ExamTopic.add(examTopic);
                            }
                        }else {
                            Log.d("ExamPaperDetailActivity", "没有选择题");
                        }
                        if (jSONObjectTExamTopic != null) {
                            /**
                             * 此部分代码为手动解析填空题json数据
                             */
                            for (int a = 1; a <= jSONObjectTExamTopic.length(); a ++) {
                                TExamTopic tExamTopic = new TExamTopic();
                                /**
                                 * 根据填空题的数量把填空题的JSON字符串解析成对象
                                 */
                                JSONObject jSONObject_TNumber = jSONObjectTExamTopic.getJSONObject(String.valueOf(a));
                                tExamTopic.setTitle(jSONObject_TNumber.getString("title"));
                                tExamTopic.setImg(jSONObject_TNumber.getString("img"));
                                Log.e("TAG_img", "====="+jSONObject_TNumber.getString("img"));

                                tExamTopic.setRight(jSONObject_TNumber.getString("right"));
                                tExamTopic.setJiexi(jSONObject_TNumber.getString("jiexi"));
                                ArrayList_TExamTopic.add(tExamTopic);
                            }
                        }else {
                            Log.d("ExamPaperDetailActivity", "没有填空题");
                        }
                        if (jSONObjectJExamTopic != null) {
                            /**
                             * 此部分代码为手动解析解答题json数据
                             */
                            for (int b = 1; b <= jSONObjectJExamTopic.length(); b ++) {
                                TExamTopic jExamTopic = new TExamTopic();
                                JSONObject jSONObject_JNumber = jSONObjectJExamTopic.getJSONObject(String.valueOf(b));
                                jExamTopic.setTitle(jSONObject_JNumber.getString("title"));
                                jExamTopic.setImg(jSONObject_JNumber.getString("img"));
                                jExamTopic.setRight(jSONObject_JNumber.getString("right"));
                                jExamTopic.setJiexi(jSONObject_JNumber.getString("jiexi"));
                                ArrayList_JExamTopic.add(jExamTopic);
                            }
                        }else {
                            Log.d("ExamPaperDetailActivity", "没有解答题");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    chronometer.start();
                    long_spend_time = System.currentTimeMillis();
                    readerViewPagerAdapter = new ReaderViewPagerAdapter(getSupportFragmentManager(), ArrayList_ExamTopic, ArrayList_TExamTopic, ArrayList_JExamTopic);
                    readerViewPager.setAdapter(readerViewPagerAdapter);
                    break;
                case THREAD_SUBMIT_PAPER_JSON_DATA://把用户作答的试卷答案提交到服务器
                    Intent intent_ExamPaperResultActivity = new Intent(ExamPaperDetailActivity.this, ExamPaperResultActivity.class);
                    if (ArrayList_ExamTopic != null) {
                        intent_ExamPaperResultActivity.putExtra("examTopicList", ArrayList_ExamTopic);
                    }
                    if (ArrayList_TExamTopic != null){
                        intent_ExamPaperResultActivity.putExtra("TexamTopicList", ArrayList_TExamTopic);
                    }
                    if (ArrayList_JExamTopic != null){
                        intent_ExamPaperResultActivity.putExtra("JexamTopicList",ArrayList_JExamTopic );
                    }
                    if (hashmap_selected_result != null) {
                        intent_ExamPaperResultActivity.putExtra("hashmap_selected_result", hashmap_selected_result);
                    }
                    if (hashmap_selected_resultS != null){

                        intent_ExamPaperResultActivity.putExtra("hashmap_selected_resultS", hashmap_selected_resultS);
                    }
                    if (ListJAnswer != null){
                        intent_ExamPaperResultActivity.putExtra("ListJAnswer", ListJAnswer);

                    }
                    startActivity(intent_ExamPaperResultActivity);
                    finish();
                    break;
            }
        }
    }

    /**
     * 网络线程，用于请求服务器上的试卷内容
     */
    class PostThread extends Thread {
        public void run() {
            String result;
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://shiyan360.cn/index.php/api/examinPaperDetail?id="+int_ExamPaperId);
                connection = (HttpURLConnection) url.openConnection();
                // 设置请求方式
                connection.setRequestMethod("POST");
                // 设置编码格式
                connection.setRequestProperty("Charset", "UTF-8");
                // 传递自定义参数
                connection.setRequestProperty("id", "5");
                // 设置容许输出
                connection.setDoOutput(true);
                // 获取返回数据
                if(connection.getResponseCode() == 200){
                    InputStream is = connection.getInputStream();
                    ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024 * 8];
                    int size = 0;
                    while ((size = is.read(buffer)) >= 1) {
                        byteArrayOutStream.write(buffer, 0, size);
                    }
                    result = byteArrayOutStream.toString();
                    /**
                     * 创建试卷JSON对象
                     */
                    JSONObject jsonObjectExamPaper = new JSONObject(result);
                    Message message1 = new Message();
                    message1.obj = jsonObjectExamPaper;
                    message1.what = THREAD_GET_PAPER_DETAIL_SUCCESS;
                    messageHandler.sendMessage(message1);

                }else {
                    messageHandler.sendEmptyMessage(4);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null){
                    connection.disconnect();
                }
            }
        }
    }

    private void showDialog(String showMessage) {
        progressDialog.setMessage(showMessage+"...");
        progressDialog.setIndeterminate(true);
        progressDialog.setButton("重新加载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog.show();
                new PostThread().start();
            }
        });
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void showInputDialog() {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("提交信息").setMessage("您确定全部作答完成吗？");
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long long_end_time = System.currentTimeMillis() - long_spend_time;
                long mSecond = long_end_time / 1000;
                long mMinute = mSecond / 60;
                long mHour = 0;
                long end_Minute;
                if (mMinute > 60) {
                    mHour = mMinute / 60;
                    end_Minute = mMinute % 60;
                    Log.v("ExamPaperDetailActivity", "时:"+String.valueOf(mHour)+"分:"+String.valueOf(end_Minute));
                }else {
                    Log.v("ExamPaperDetailActivity", "时:"+mHour+"分:"+String.valueOf(mMinute));
                }
                /**
                 * 此次需要获取用户的SID，把参数传入bean2json函数
                 */
                SharedPreferences sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                String sid = sp.getString("id", null);
                if (sid == null || sid.equals("")) {
                    ToastUtil.show("您的资料没有完善..,");
                    return;
                }
                int sidValid = Integer.parseInt(sid);
                submitPaperJsonWithRetrofit(bean2json(hashmap_selected_result, ListALLTAnswer, hashmap_selected_resultS, ListJAnswer, int_ExamPaperId, sidValid, examPaperTitle,
                        String.valueOf(mHour), String.valueOf(mMinute)));
            }
        }).show();
    }

    /**
     * 由于服务器需要的JSON数据繁杂,无法自动生成,需手动转换
     * 手动把
     * cp_id 试卷的序号,
     * sid 用户的ID
     * title 学生的姓名,
     * number 学生的学号,
     * hour 做试卷时所用的小时,
     * minute 做试卷所用的分钟
     * @param map_select 装选择题答案的HashMap
     * @param ListALLTAnswer 装填空题答案的List
     * @param ListJAnswer 装解答题答案的List
     */
    private String bean2json(Map<Integer, String> map_select, List<List> ListALLTAnswer, HashMap<Integer, String> hashmapString, List<String> ListJAnswer, int cp_id, int sid, String title, String hour, String minute) {
        xcontentCount = map_select.size() - hashmapString.size() - ListJAnswer.size();
        /**
         * 创建试卷Json对象
         */
        JSONObject jsonObject_Paper = new JSONObject();
        try {
            jsonObject_Paper.put("cp_id", cp_id);//int型
            jsonObject_Paper.put("sid", sid);
            jsonObject_Paper.put("title", title);//字符串型

            /**
             * 把经过解析之后的选择题答案装入到jsonObject_Paper
             */
            JSONObject jsonObjectXTopic = new JSONObject();
            for (int i = 1; i <= xcontentCount; i++) {
                JSONObject jsonObject_select = new JSONObject();
                /**
                 * 赋值选择的答案的属性,属性值是一个String类型(ABCD中的某一个)
                 */
                jsonObject_select.put("user", map_select.get(i));
                /**
                 * 赋值第一道题到第十道题属性,属性值是一个JSONObject对象
                 */
                jsonObjectXTopic.put(String.valueOf(i), jsonObject_select);
            }
            jsonObject_Paper.put("xcontent", jsonObjectXTopic);
            Log.d("ExamPaperDetailActivity", "准备发送到服务器的选择题的JSON"+jsonObjectXTopic.toString());

            /**
             * 把经过解析之后的填空题答案装入jsonObject_Paper中
             */
            JSONObject jsonObjectTTopic = new JSONObject();//tcontent属性的值（为一个对象）
            int topicOder = 0;
            for (int order:hashmapString.keySet()) {
                topicOder = topicOder + 1;
                JSONObject JSONObjectUser = new JSONObject();//第几道题属性的值（为一个对象）
                JSONArray JSONArrayPerBracket = new JSONArray();//user属性的值（为一个数组）
                String a = hashmapString.get(order);
                Log.d("ExamPaperDetailActivity", "Json解析输出:"+a);
                String splitString[] = null;
                splitString = a.split(",");
                for (int k = 0; k < splitString.length; k ++) {
                    JSONArrayPerBracket.put(k, splitString[k]);
                }
                JSONObjectUser.put("user", JSONArrayPerBracket);
                jsonObjectTTopic.put(String.valueOf(topicOder), JSONObjectUser);
            }
            jsonObject_Paper.put("tcontent", jsonObjectTTopic);
            Log.d("ExamPaperDetailActivity", "准备发送到服务器的填空题的JSON"+jsonObjectTTopic.toString());

            /**
             * 把经过解析之后的填空题答案装入jsonObject_Paper中
             */
            /*JSONObject jsonObjectTTopic = new JSONObject();//tcontent属性的值（为一个对象）
            for (int j = 1; j <= ListALLTAnswer.size(); j ++) {
                JSONObject JSONObjectUser = new JSONObject();//第几道题属性的值（为一个对象）
                JSONArray JSONArrayPerBracket = new JSONArray();//user属性的值（为一个数组）
                List<String> AllBracket = ListALLTAnswer.get(j - 1);
                for (int k = 0; k < AllBracket.size(); k ++) {
                    JSONArrayPerBracket.put(k, AllBracket.get(k));
                }
                JSONObjectUser.put("user", JSONArrayPerBracket);
                jsonObjectTTopic.put(String.valueOf(j), JSONObjectUser);
            }
            jsonObject_Paper.put("tcontent", jsonObjectTTopic);
            Log.d("ExamPaperDetailActivity", "准备发送到服务器的填空题的JSON"+jsonObjectTTopic.toString());*/

            /**
             * 把解答题答案装入jsonObjectJTopic中
             */
            JSONObject jsonObjectJTopic = new JSONObject();//jsonObjectJTopic是属性jcontent的值
            for (int l = 1; l <= ListJAnswer.size(); l ++) {
                JSONObject jsonObjectDiJiDaoTi = new JSONObject();//jsonObjectDiJiDaoTi是属性题号的值
                jsonObjectDiJiDaoTi.put("user", ListJAnswer.get(l - 1));
                jsonObjectJTopic.put(String.valueOf(l), jsonObjectDiJiDaoTi);
            }
            jsonObject_Paper.put("jcontent", jsonObjectJTopic);
            Log.d("ExamPaperDetailActivity", "准备发送到服务器的解答题的JSON"+jsonObjectJTopic.toString());

            jsonObject_Paper.put("hour",hour);//字符串型
            jsonObject_Paper.put("minute", minute);//字符串型
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("ExamPaperDetailActivity", "测试提交服务器前的试卷JSON数据:"+jsonObject_Paper.toString());
        return jsonObject_Paper.toString();
    }

    private void submitPaperJsonWithRetrofit(String toJson) {
        APIWrapper.getInstance().submitPaperJson(toJson)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult>() {
                    @Override
                    public void onCompleted() {
                        if (isSuccessSubmitJson == true) {
                            messageHandler.sendEmptyMessage(THREAD_SUBMIT_PAPER_JSON_DATA);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        Log.v("ExamPaperDetailActivity", "测试提交到服务器中的数据是否成功:"+String.valueOf(httpResult.getSuccess()));
                        if (httpResult.getSuccess() == true) {
                            isSuccessSubmitJson = true;
                        }
                    }
                });
    }
}
