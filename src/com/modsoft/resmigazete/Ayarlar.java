package com.modsoft.resmigazete;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Ayarlar extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.outer_settings);
		PreferenceManager.setDefaultValues(Ayarlar.this, R.xml.outer_settings, false);
	}
}
