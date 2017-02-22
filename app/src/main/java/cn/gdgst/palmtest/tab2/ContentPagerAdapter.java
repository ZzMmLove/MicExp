package cn.gdgst.palmtest.tab2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * @author Don
 *FragmentPagerAdapter页面选项卡适配器
 */
public class ContentPagerAdapter extends FragmentPagerAdapter {

	private Fragment[] fragments;

	public ContentPagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new Fragment[2];
		fragments[0] = new video();
		fragments[1] = new Album();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments[position];
	}

	@Override
	public int getCount() {
		return fragments.length;
	}
}
