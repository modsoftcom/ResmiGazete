package com.modsoft.resmigazete.adapters;

import com.modsoft.resmigazete.FragmentDosya;
import com.modsoft.resmigazete.FragmentHaber;
import com.modsoft.resmigazete.FragmentIlan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	String[] Tabs = { "HABERLER","ÝLANLAR","DOSYALAR"};
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return new FragmentHaber();
		case 1:
			return new FragmentIlan();
		case 2:
			return new FragmentDosya();
		}

		return null;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return Tabs[position];
	}

}
