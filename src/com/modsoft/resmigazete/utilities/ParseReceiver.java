package com.modsoft.resmigazete.utilities;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;
import com.modsoft.resmigazete.AnaSayfa;
import com.modsoft.resmigazete.R;
import com.parse.ParsePushBroadcastReceiver;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/*****************************
 * This class will receive custom push notifications from parse.com. These are
 * different than the "plain" message push notifications.
 * 
 * There must be an action defined within the Intent-Filter for this receiver in
 * the manifest.xml file. And the same action must be specified on the
 * notification when it is pushed.
 * 
 * You can optionally pass JSON data from parse.com which will be avaialable in
 * the onReceive() method here.
 *****************************/
public class ParseReceiver extends ParsePushBroadcastReceiver {
	private final String TAG = "Parse Notification";
	private String incoming = "", msg = "";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		try {
			boolean receiving = PreferenceManager.getDefaultSharedPreferences(
					ctx).getBoolean("notifications", true);

			Log.i(TAG, "PUSH RECEIVED!!!");
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			incoming = intent.getExtras().getString("com.parse.Data");
			msg = new GsonBuilder().create().fromJson(incoming, InComing.class).alert;
			Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx)
					.edit();
			editor.putString("notification", msg);
			editor.putBoolean("notification_received", true);
			editor.commit();

			if (receiving) {
				Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(),
						R.drawable.ic_launcher);

				Intent launchActivity = new Intent(ctx, AnaSayfa.class);
				PendingIntent pi = PendingIntent.getActivity(ctx, 0,
						launchActivity, 0);

				Notification noti = new NotificationCompat.Builder(ctx)
						.setContentTitle("Resmi Gazete")
						.setContentText(msg)
						.setSmallIcon(R.drawable.ic_launcher)
						.setLargeIcon(icon)
						.setContentIntent(pi)
						.setAutoCancel(true)
						.setSound(
								Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
										+ "://com.modsoft.resmigazete/raw/"
										+ PreferenceManager
												.getDefaultSharedPreferences(
														ctx).getString(
														"notification_tone",
														"chime"))).build();

				NotificationManager nm = (NotificationManager) ctx
						.getSystemService(Context.NOTIFICATION_SERVICE);
				nm.notify(0, noti);
			}
		} catch (Exception e) {
			Log.e("Hata", "Mesaj Alýnamadý!");
		}

	}

	private class InComing {
		public String alert;
		public String push_hash;
	}
}
