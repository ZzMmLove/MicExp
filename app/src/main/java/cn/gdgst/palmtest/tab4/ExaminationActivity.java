package cn.gdgst.palmtest.tab4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.gdgst.entity.ExamPaper;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExaminationActivity extends Activity implements View.OnClickListener {

    private Button button;
    private TextView textView;
    private ListView listViewExamList;
    private List<ExamPaper> listExamPaper = new ArrayList<ExamPaper>();
    private Button button_local_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);
        button = (Button) findViewById(R.id.activity_examination_button);
        button_local_video = (Button) findViewById(R.id.activity_examination_button_video);
        button.setOnClickListener(this);
        button_local_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
                    /*File file_sdcard = Environment.getExternalStorageDirectory();
                    Log.v("ExaminationActivity", "可扩展存储目录为:"+file_sdcard.getAbsolutePath());
                    File file_PalmTest = new File(file_sdcard.getAbsolutePath()+"/PlamTest");
                    boolean isCreateDir_PlamTest = file_PalmTest.mkdir();
                    Log.v("ExaminationActivity", "是否创建PalmTest文件夹"+isCreateDir_PlamTest);
                    File file_Video = new File(file_PalmTest.getAbsolutePath()+"/Video");
                    boolean isCreateDir_Video = file_Video.mkdir();
                    Log.v("ExaminationActivity", "是否创建Video文件加"+isCreateDir_Video);*/
                    File file_sdcard = Environment.getExternalStorageDirectory();
                    File[] file_sdcard_list = file_sdcard.listFiles();
                    for (int a = 0; a < file_sdcard_list.length; a ++) {
                        Log.v("ExaminationActivity", file_sdcard_list[a].getAbsolutePath());
                        if (file_sdcard_list[a].getName() == "PalmTest") {
                            Log.v("ExaminationActivity", "PalmTest的绝对路径是:"+file_sdcard_list[a].getAbsolutePath());
                        }
                    }
                    File file_PalmTest = new File(Environment.getExternalStorageDirectory()+"/PlamTest");
                    Log.v("ExaminationActivity", "PalmTest的绝对路径是:"+file_PalmTest.getAbsolutePath());
                    File file_Video = new File(file_PalmTest.getAbsolutePath()+"/Video");
                    File[] file_Video_list = file_Video.listFiles();
                    Log.v("ExaminationActivity", "file_Video的绝对路径是:"+file_Video.getAbsolutePath());
                    File file_a_video = new File(file_Video.getAbsolutePath()+"/b.mp4");
                    Log.v("ExaminationActivity", "a视频的绝对路径为:"+file_a_video.getAbsolutePath()+"是否是一个文件"+String.valueOf(file_a_video.isFile()));
                    if (file_Video_list.length == 0) {
                        return;
                    }else {
                        for (int i = 0; i < file_Video_list.length; i ++) {
                            Log.v("jenfee", "各个视频的绝对路径:"+file_Video_list[i].getAbsolutePath());
                        }
                    }

                    Intent intent = new Intent(ExaminationActivity.this, Vid_Play_Activity.class);
                    intent.putExtra("a_video_path", file_Video.getAbsoluteFile()+"/b.mp4");
                    startActivity(intent);
                }

            }
        });
        textView = (TextView) findViewById(R.id.activity_examination_textView);
        listViewExamList = (ListView) findViewById(R.id.activity_examinaiton_listview);
    }

    @Override
    public void onClick(View v) {
        /**
         * 下拉刷新的页数
         */
        int page = 1;
        /**
         * 按分类来筛选(高中物理:371,高中化学:373,高中生物:375,通用技术:406,
         *              初中物理:386,初中化学:387,初中生物:388,初中科学:404,信息技术:393)
         */
        int cid = 0;

        APIWrapper.getInstance().examinPaperList(page, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<List<ExamPaper>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HttpResult<List<ExamPaper>> listHttpResult) {
                        listExamPaper = listHttpResult.getData();

                        for (int i = 0; i<listExamPaper.size(); i ++) {
                            ExamPaper examPaper = listExamPaper.get(i);
                            Log.v("jenfee's", examPaper.getPaper());
                        }
                        Log.v("jenfee's", listExamPaper.toString());
                    }
                });
    }
}
