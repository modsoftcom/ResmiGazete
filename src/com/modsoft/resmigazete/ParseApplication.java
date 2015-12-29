package com.modsoft.resmigazete;

import com.modsoft.resmigazete.utilities.Keys;
import com.parse.Parse;
import com.parse.PushService;

import android.app.Application;

public class ParseApplication extends Application {
	public ParseApplication() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, Keys.applicationID, Keys.clientKey);
	}
}
