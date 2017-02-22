package cn.gdgst.palmtest.tab1.examsystem;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import cn.gdgst.entity.ExamTopic;

/**
 * Created by JenfeeMa on 2017/1/11.
 * All right reserved
 * email 1017033168@qq.com
 */

public class ReaderViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<ExamTopic> list_ExamTopic;
    private ReadFragment readFragment;
    private HashMap<Integer, ReadFragment> hashMap_ReadFragment = new HashMap<>();

    public ReaderViewPagerAdapter(FragmentManager fm, ArrayList<ExamTopic> list_ExamTopic) {
        super(fm);
        this.list_ExamTopic = list_ExamTopic;
    }

    @Override
    public Fragment getItem(int i) {
        readFragment = ReadFragment.newInstance(list_ExamTopic.get(i));
        hashMap_ReadFragment.put(i, readFragment);
        return readFragment;
    }

    @Override
    public int getCount() {
        return list_ExamTopic.size();
    }

    public HashMap<Integer, ReadFragment> getHashMap_ReadFragment() {
        return hashMap_ReadFragment;
    }
}
