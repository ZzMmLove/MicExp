package cn.gdgst.palmtest.tab1.examsystem;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import cn.gdgst.entity.ExamTopic;
import cn.gdgst.palmtest.bean.TExamTopic;

/**
 * Created by JenfeeMa on 2017/1/11.
 * All right reserved
 * email 1017033168@qq.com
 */

public class ReaderViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<ExamTopic> list_XExamTopic;
    private ArrayList<TExamTopic> list_TExamTopic;
    private ArrayList<TExamTopic> list_JExamTopic;
    /**
     * 选择题的Fragment
     */
    private ReadFragment readFragment;
    private HashMap<Integer, ReadFragment> hashMap_ReadFragment = new HashMap<>();

    public ReaderViewPagerAdapter(FragmentManager fm, ArrayList<ExamTopic> list_ExamTopic, ArrayList<TExamTopic> list_TExamTopic,
                                  ArrayList<TExamTopic> list_JExamTopic) {
        super(fm);
        this.list_XExamTopic = list_ExamTopic;
        this.list_TExamTopic = list_TExamTopic;
        this.list_JExamTopic = list_JExamTopic;
    }

    @Override
    public Fragment getItem(int i) {
        Log.d("ReaderViewPagerAdapter", "创建第"+i+"Fragment");
        int ahead = list_XExamTopic.size() + list_TExamTopic.size();

        if (i+1 <= list_XExamTopic.size()) {
            readFragment = ReadFragment.newInstance(list_XExamTopic.get(i), null, null, 1);
        } else if ((i+1) > list_XExamTopic.size() && (i+1) <= ahead) {
            readFragment = ReadFragment.newInstance(null, list_TExamTopic.get(i - list_XExamTopic.size()), null, 2);
        } else if (i+1 > ahead) {
            readFragment = ReadFragment.newInstance(null, null, list_JExamTopic.get(i - ahead), 3);
        }
        hashMap_ReadFragment.put(i, readFragment);
        return readFragment;
    }

    @Override
    public int getCount() {
        return list_XExamTopic.size() + list_TExamTopic.size() + list_JExamTopic.size();
    }

    public HashMap<Integer, ReadFragment> getHashMap_ReadFragment() {
        return hashMap_ReadFragment;
    }

}
