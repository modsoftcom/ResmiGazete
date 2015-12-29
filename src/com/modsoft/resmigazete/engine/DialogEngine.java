package com.modsoft.resmigazete.engine;

import java.util.Date;

import com.modsoft.resmigazete.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogEngine implements OnDismissListener {

	private Context context;
	private String Message;
	private Drawable Image;
	private Screen screen;
	private boolean never = true;
	private SharedPreferences Pref;

	public DialogEngine(Context c) {
		this.context = c;
		Pref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void Info(Screen type) {
		screen = type;
		switch (type) {
		case AnaSayfa:
			Message = "Anasayfaki yön düðmelerini gösterildiði þekilde kullanabilirsiniz.";
			Image = context.getResources().getDrawable(R.drawable.main_info);
			break;
		case AnaSayfaLong:
			Message = "Bir haberin üzerine uzun süre basýlý tuttuðunuzda içerik menüsüne ulaþabilirsiniz.";
			Image = context.getResources().getDrawable(
					R.drawable.main_long_info);
			break;
		case Favoriler:
			Message = "Bir haberin üzerine uzun süre basýlý tuttuðunuzda içerik menüsüne ulaþabilirsiniz.";
			Image = context.getResources().getDrawable(R.drawable.favori_info);
			break;
		case PageView:
			Message = "Ekrana uzun süre basýlý tuttuðunuzda ya da ekranýn sol dýþýndan saða doðru parmaðýnýzý çektiðinizde içerik menüsüne ulaþabilirsiniz. ";
			Image = context.getResources()
					.getDrawable(R.drawable.pageview_info);
			break;
		}
		Show();
	}

	private void Show() {
		final Dialog dialog = new Dialog(context);
		dialog.setTitle("Yardým");
		dialog.setContentView(R.layout.info_dialog);
		dialog.setOnDismissListener(this);
		ImageView image = (ImageView) dialog.findViewById(R.id.info_image);
		image.setImageDrawable(Image);
		TextView message = (TextView) dialog.findViewById(R.id.info_text);
		message.setText(Message);
		CheckBox neverCheck = (CheckBox) dialog
				.findViewById(R.id.info_show_never);
		neverCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				never = !isChecked;
			}
		});
		dialog.show();
	}

	public void onDismiss(DialogInterface dialog) {
		Editor editor = Pref.edit();
		switch (screen) {
		case AnaSayfa:
			editor.putBoolean("show_main_tip", never);
			break;
		case AnaSayfaLong:
			editor.putBoolean("show_main_long_tip", never);
			break;
		case Favoriler:
			editor.putBoolean("show_favori_tip", never);
			break;
		case PageView:
			editor.putBoolean("show_pageview_tip", never);
			break;
		}
		editor.commit();
		
		
	}
}
