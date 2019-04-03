package reco.frame.tv.view.component;

import java.util.List;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FragmentAdapter extends PagerAdapter{
	private List<Fragment> fragments; // ÿ��Fragment��Ӧһ��Page
	private FragmentManager fragmentManager;
	private int currentPageIndex = 0; // ��ǰpage�������л�֮ǰ��

	public FragmentAdapter(FragmentManager fragmentManager,
			List<Fragment> fragments) {
		this.fragments = fragments;
		this.fragmentManager = fragmentManager;
	}

	public int getCount() {
		return fragments.size();
	}

	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(fragments.get(position).getView()); // �Ƴ�viewpager����֮���page����
	}

	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = fragments.get(position);
		if (!fragment.isAdded()) { // ���fragment��û��added
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			/**
			 * ����FragmentTransaction.commit()�����ύFragmentTransaction�����
			 * ���ڽ��̵����߳��У����첽�ķ�ʽ��ִ�С� �����Ҫ����ִ������ȴ��еĲ�������Ҫ�������������ֻ�������߳��е��ã���
			 * Ҫע����ǣ����еĻص�����ص���Ϊ��������������б�ִ����ɣ����Ҫ��ϸȷ����������ĵ���λ�á�
			 */
			fragmentManager.executePendingTransactions();
		}
		
		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // Ϊviewpager���Ӳ���
		}
		

		return fragment.getView();
	}

	/**
	 * ��ǰpage�������л�֮ǰ��
	 * 
	 * @return
	 */
	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

}
