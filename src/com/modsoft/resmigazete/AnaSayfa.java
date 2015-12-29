package com.modsoft.resmigazete;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.modsoft.resmigazete.adapters.TabsPagerAdapter;
import com.modsoft.resmigazete.adapters.ZoomOutPageTransformer;
import com.modsoft.resmigazete.database.FavorilerDB;
import com.modsoft.resmigazete.engine.DateTime;
import com.modsoft.resmigazete.engine.DialogEngine;
import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.PDFEngine;
import com.modsoft.resmigazete.engine.PaperEngine;
import com.modsoft.resmigazete.engine.Screen;
import com.modsoft.resmigazete.utilities.Validater;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AnaSayfa extends FragmentActivity implements OnClickListener {

	private ActionBar actionBar;
	private DrawerLayout mDrawerLayout;
	private RelativeLayout mDrawerRelative;
	private ActionBarDrawerToggle mDrawerToggle;
	private List<Haber> haberList = new ArrayList<Haber>(),
			ilanList = new ArrayList<Haber>(),
			dosyaList = new ArrayList<Haber>();
	private LinearLayout TodayButton, ArchiveButton, WordSearchButton,
			MukerrerButton, FavoritesButton, LastPushButton, ReviewButton,
			SettingsButton, HelpButton, AboutButton;
	private TextView InfoText;
	private ViewPager pager;
	private PagerTabStrip tabStrip;
	private TabsPagerAdapter mAdapter;
	private PaperEngine Gazete;
	private static final int DATE_DIALOG_ID = 999;
	private static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	private String m_chosenDir = "/sdcard/Download/";
	private Haber currentHaber;
	private FavorilerDB Database;
	private SharedPreferences Pref;
	DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	DateFormat DateText = new SimpleDateFormat("dd MMMM yyyy");
	public Date currentDate = new Date();
	private boolean firstRun, main, main_long;
	Animation animationLeft;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ana_sayfa);
		ActionBarSet();
		NavigationDrawerSet();
		InitValues();
		ViewPagerSet();
		Gazete = new PaperEngine(this);
		Gazete.Today();
		InfoText.setText(DateText.format(currentDate));
		actionBar.setTitle(DateText.format(currentDate));
		InfoScreen();
	}

	private void InfoScreen() {
		if (firstRun) {
			ShowFirstRunDialog();
		}
		if (main_long) {
			new DialogEngine(this).Info(Screen.AnaSayfaLong);
		}
		if (main) {
			new DialogEngine(this).Info(Screen.AnaSayfa);
		}

	}

	public void ActionBarSet() {
		actionBar = getActionBar();
		actionBar.setIcon(getResources()
				.getDrawable(R.drawable.resmigazeteicon));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_CUSTOM);

	}

	public void NavigationDrawerSet() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
		mDrawerRelative = (RelativeLayout) findViewById(R.id.slider_relative_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.main_drawerlistimage, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {

				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		mDrawerToggle.syncState();
	}

	private void InitValues() {
		InfoText = (TextView) findViewById(R.id.drawer_info_text);
		TodayButton = (LinearLayout) findViewById(R.id.drawer_today_layout);
		TodayButton.setOnClickListener(this);
		ArchiveButton = (LinearLayout) findViewById(R.id.drawer_archive_layout);
		ArchiveButton.setOnClickListener(this);
		WordSearchButton = (LinearLayout) findViewById(R.id.drawer_find_layout);
		WordSearchButton.setOnClickListener(this);
		MukerrerButton = (LinearLayout) findViewById(R.id.drawer_mukerrer_layout);
		MukerrerButton.setOnClickListener(this);
		FavoritesButton = (LinearLayout) findViewById(R.id.drawer_favorites_layout);
		FavoritesButton.setOnClickListener(this);
		LastPushButton = (LinearLayout) findViewById(R.id.drawer_lastpush_layout);
		LastPushButton.setOnClickListener(this);
		ReviewButton = (LinearLayout) findViewById(R.id.drawer_review_layout);
		ReviewButton.setOnClickListener(this);
		SettingsButton = (LinearLayout) findViewById(R.id.drawer_settings_layout);
		SettingsButton.setOnClickListener(this);
		HelpButton = (LinearLayout) findViewById(R.id.drawer_help_layout);
		HelpButton.setOnClickListener(this);
		AboutButton = (LinearLayout) findViewById(R.id.drawer_about_layout);
		AboutButton.setOnClickListener(this);
		pager = (ViewPager) findViewById(R.id.main_menu_pager);
		tabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
		Database = new FavorilerDB(this);
		Pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = Pref.edit();
		editor.putString("current", format.format(new Date()));
		editor.commit();
		firstRun = Pref.getBoolean("first_run", true);
		main = Pref.getBoolean("show_main_tip", true);
		main_long = Pref.getBoolean("show_main_long_tip", true);
		CheckNotification();
	}

	private void CheckNotification() {
		boolean hasNotification = Pref.getBoolean("notification_received",
				false);
		String msg = Pref.getString("notification", "");
		if (hasNotification) {
			AlertDialog.Builder alert = new AlertDialog.Builder(AnaSayfa.this);
			alert.setTitle("Yeni Bildirim Al�nd�!");
			if (msg.contains("http")) {
				final String link = msg.substring(msg.indexOf("http"));
				msg = msg.substring(0, msg.indexOf("http"));
				alert.setPositiveButton("Tamam",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent web = new Intent(Intent.ACTION_VIEW);
								web.setData(Uri.parse(link));
								startActivity(web);
							}
						});
				alert.setNegativeButton("Vazge�",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
			}

			alert.setMessage(msg);
			alert.show();
			Editor editor = Pref.edit();
			editor.putBoolean("notification_received", false);
			editor.commit();
		}
	}

	public void ViewPagerSet() {
		tabStrip.setTextColor(Color.WHITE);
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(mAdapter);
		pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			public void onPageScrollStateChanged(int arg0) {
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageSelected(int position) {

			}
		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.drawer_today_layout:
			Clean();
			currentDate = new Date();
			InfoText.setText(DateText.format(currentDate));
			actionBar.setTitle(DateText.format(currentDate));
			Gazete.Today();
			break;
		case R.id.drawer_archive_layout:
			Clean();
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.drawer_find_layout:
			Search();
			break;
		case R.id.drawer_mukerrer_layout:
			Clean();
			Gazete.Mukerrer();
			break;
		case R.id.drawer_favorites_layout:
			startActivity(new Intent(AnaSayfa.this, Favoriler.class));
			break;
		case R.id.drawer_lastpush_layout:
			String msg = Pref.getString("notification", "");
			if (Validater.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(msg)) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						AnaSayfa.this);
				alert.setTitle("Son Al�nan Bildirim");
				if (msg.contains("http")) {
					final String link = msg.substring(msg.indexOf("http"));
					msg = msg.substring(0, msg.indexOf("http"));
					alert.setPositiveButton("Tamam",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent web = new Intent(Intent.ACTION_VIEW);
									web.setData(Uri.parse(link));
									startActivity(web);
								}
							});
					alert.setNegativeButton("Vazge�",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
				}

				alert.setMessage(msg);
				alert.show();
			} else {
				Toast.makeText(getApplicationContext(), "Son gelen mesaj yok!",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.drawer_review_layout:
			SendReview();
			break;
		case R.id.drawer_settings_layout:
			startActivity(new Intent(AnaSayfa.this, Ayarlar.class));
			break;
		case R.id.drawer_help_layout:
			Help();
			break;
		case R.id.drawer_about_layout:
			ShowAboutDialog();
			break;
		}
		mDrawerLayout.closeDrawer(mDrawerRelative);
	}

	private void Help() {
		AlertDialog.Builder alert = new AlertDialog.Builder(AnaSayfa.this);
		alert.setTitle("Yard�m");
		alert.setMessage("Yard�m pencerelerini a�mak istiyor musunuz?");
		alert.setPositiveButton("A�", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = Pref.edit();
				editor.putBoolean("show_main_tip", true);
				editor.putBoolean("show_main_long_tip", true);
				editor.putBoolean("show_favori_tip", true);
				editor.putBoolean("show_pageview_tip", true);
				editor.commit();
				Toast.makeText(getApplicationContext(),
						"T�m yard�m pencereleri g�sterilecek",
						Toast.LENGTH_SHORT).show();
				main = Pref.getBoolean("show_main_tip", true);
				main_long = Pref.getBoolean("show_main_long_tip", true);
				InfoScreen();
			}
		});
		alert.setNegativeButton("Vazge�", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alert.show();
	}

	private void Search() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Bilgilendirme");
		alert.setMessage("Bu se�enek bir sonraki s�r�mde kullan�l�r hale gelecektir. "
				+ "Aramay� Resmi Gazete'nin kendi sayfas�nda yapmak ister misiniz?");
		alert.setPositiveButton("A�", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent web = new Intent(Intent.ACTION_VIEW);
				web.setData(Uri
						.parse("http://www.resmigazete.gov.tr/default.aspx"));
				startActivity(web);
			}
		});
		alert.setNegativeButton("Vazge�", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alert.show();
	}

	private void SendReview() {
		try {
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:"));
			intent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "modsoftcom@gmail.com" });
			intent.putExtra(Intent.EXTRA_SUBJECT,
					"Resmi Gazete Uygulamas� Hakk�nda");
			startActivity(Intent.createChooser(intent, "G�r�� G�nderme"));
		} catch (Exception e) {
			Log.e("----> Intent ERROR :", "E-Mail Intent'i a��lamad�.");
			Toast.makeText(getApplicationContext(), "E-Mail Sorunu.",
					Toast.LENGTH_SHORT).show();
			SendError(e, "E-Mail Intent'i a��lamad�.");
		}
	}

	private void Clean() {
		Gazete = new PaperEngine(this);
		haberList = new ArrayList<Haber>();
		ilanList = new ArrayList<Haber>();
		dosyaList = new ArrayList<Haber>();
		ViewPagerSet();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mDrawerLayout.isDrawerOpen(mDrawerRelative)) {
				mDrawerLayout.closeDrawer(mDrawerRelative);
			} else {
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @return the haberList
	 */
	public List<Haber> getHaberList() {
		return haberList;
	}

	/**
	 * @param haberList
	 *            the haberList to set
	 */
	public void setHaberList(List<Haber> haberList) {
		this.haberList = haberList;
	}

	/**
	 * @return the ilanList
	 */
	public List<Haber> getIlanList() {
		return ilanList;
	}

	/**
	 * @param ilanList
	 *            the ilanList to set
	 */
	public void setIlanList(List<Haber> ilanList) {
		this.ilanList = ilanList;
	}

	/**
	 * @return the dosyaList
	 */
	public List<Haber> getDosyaList() {
		return dosyaList;
	}

	/**
	 * @param dosyaList
	 *            the dosyaList to set
	 */
	public void setDosyaList(List<Haber> dosyaList) {
		this.dosyaList = dosyaList;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		int year_;
		String day_;
		String month_;
		switch (id) {
		case DATE_DIALOG_ID:
			try {
				DateTime date = new DateTime();
				year_ = date.getYearInt();
				day_ = date.getDay();
				month_ = date.getMonth();
				int month = Integer.parseInt(month_) - 1;
				DatePickerDialog dpd = new DatePickerDialog(this,
						datePickerListener, year_, Integer.parseInt(String
								.valueOf(month)), Integer.parseInt(day_));
				return dpd;
			} catch (NumberFormatException e) {
				Log.e("----> Date Dialog ERROR :",
						"Date Dialog'taki say�lar Parse edilemedi.");
				Toast.makeText(getApplicationContext(), "Tarih Se�im Sorunu.",
						Toast.LENGTH_SHORT).show();
				SendError(e, "Date Dialog'taki say�lar Parse edilemedi.");
			} catch (Exception e) {
				Log.e("----> Date Dialog ERROR :",
						"Date Dialog'ta sorun olu�tu.");
				Toast.makeText(getApplicationContext(), "Tarih Se�im Sorunu.",
						Toast.LENGTH_SHORT).show();
				SendError(e, "Date Dialog'ta sorun olu�tu.");
			}

		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog
					.setMessage("Dosya indiriliyor. L�tfen bekleyiniz...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
			return mProgressDialog;

		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			try {
				Date selectedDate = new Date(selectedYear - 1900,
						selectedMonth, selectedDay);
				Date firstDate = new Date(106, 0, 1);
				if (!selectedDate.after(new Date())) {
					if (!firstDate.after(selectedDate)) {
						currentDate = selectedDate;
						InfoText.setText(DateText.format(currentDate));
						actionBar.setTitle(DateText.format(currentDate));
						Gazete.CheckDate(selectedYear, selectedMonth,
								selectedDay);
					} else {
						Toast.makeText(
								getApplicationContext(),
								"L�tfen 2006 y�l�ndan sonraki bir tarih giriniz.",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"L�tfen eski bir tarih giriniz.", Toast.LENGTH_LONG)
							.show();
				}
			} catch (Exception e) {
				Log.e("----> Ar�iv ERROR :",
						"Ar�iv'de istenen tarih getirilemedi.");
				Toast.makeText(getApplicationContext(),
						"Ar�ive eri�im durduruldu.", Toast.LENGTH_SHORT).show();
				SendError(e, "Ar�iv'de istenen tarih getirilemedi.");
			}
		}
	};

	private void DownloadFile(String fUrl) {
		new DownloadFile().execute(fUrl);
	}

	class DownloadFile extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {

				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				// getting file length
				int lenghtOfFile = conection.getContentLength();

				// input stream to read file - with 8k buffer
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				OutputStream output = new FileOutputStream(m_chosenDir
						+ "Deneme");

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));

					// writing data to file
					output.write(data, 0, count);
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();

			} catch (Exception e) {
				Log.e("-----> Download Error", "Dosya �ndirme Hatas�");
				SendError(e, "Dosya �ndirme Hatas�");
			}
			return null;
		}

		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			Toast.makeText(getApplicationContext(),
					"Dosya Download klas�r�ne indirildi", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void ContextMenu(View v, Haber haber) {
		this.currentHaber = haber;
		try {
			registerForContextMenu(v);
		} catch (Exception e) {
			Log.e("----> Context Menu ERROR :", "Context Men�de sorun olu�tu.");
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// Create your context menu here
		menu.setHeaderTitle("Se�enekler");
		menu.add(0, v.getId(), 0, "Taray�c�da A�");
		menu.add(0, v.getId(), 0, "Metni Kopyala");
		menu.add(0, v.getId(), 0, "Adresi Kopyala");
		menu.add(0, v.getId(), 0, "Payla�");
		menu.add(0, v.getId(), 0, "Favorilere Ekle");
		menu.add(0, v.getId(), 0, "PDF Olarak �ndir");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Taray�c�da A�") {
			Intent web = new Intent(Intent.ACTION_VIEW);
			web.setData(Uri.parse(currentHaber.getLink()));
			startActivity(web);
		} else if (item.getTitle() == "Metni Kopyala") {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(currentHaber.getTitle());
				Toast.makeText(getApplicationContext(),
						"Haber ba�l��� kopyaland�.", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e("----> Copy ERROR :", "Haber ad� kopyalanamad�.");
				Toast.makeText(getApplicationContext(),
						"Ba�l�k kopyalanam�yor.", Toast.LENGTH_SHORT).show();
				SendError(e, "Haber ad� kopyalanamad�.");
			}
		} else if (item.getTitle() == "Adresi Kopyala") {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(currentHaber.getLink());
				Toast.makeText(getApplicationContext(),
						"Haber adresi kopyaland�.", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e("----> Copy ERROR :", "Haber adresi kopyalanamad�.");
				Toast.makeText(getApplicationContext(),
						"Adres kopyalanam�yor.", Toast.LENGTH_SHORT).show();
				SendError(e, "Haber adresi kopyalanamad�.");
			}
		} else if (item.getTitle() == "Payla�") {
			try {
				Intent intent2 = new Intent();
				intent2.setAction(Intent.ACTION_SEND);
				intent2.setType("text/plain");
				intent2.putExtra(
						Intent.EXTRA_TEXT,
						"Resmi Gazete'deki �u ilana g�z at�n :\n"
								+ currentHaber.getTitle() + "\n"
								+ currentHaber.getLink());
				startActivity(Intent
						.createChooser(intent2, "Bununla payla�..."));
			} catch (Exception e) {
				Log.e("----> Share ERROR :", "Payla��m men�s�nde sorun olu�tu.");
				Toast.makeText(getApplicationContext(),
						"��erik payla��lam�yor.", Toast.LENGTH_SHORT).show();
				SendError(e, "Payla��m men�s�nde sorun olu�tu.");
			}
		} else if (item.getTitle() == "Favorilere Ekle") {
			final Haber favori = currentHaber;
			AlertDialog.Builder alert = new AlertDialog.Builder(AnaSayfa.this);
			alert.setTitle("Favorilere Ekleme");
			LinearLayout linear = new LinearLayout(this);
			linear.setOrientation(LinearLayout.VERTICAL);
			TextView message = new TextView(this);
			message.setText("Haberin ismini yaz�n�z.\nMevcut ismi ile kaydetmek i�in do�rudan Ekle tu�una bas�n�z.");
			final EditText tag = new EditText(this);
			tag.setHint(favori.getTitle());
			alert.setPositiveButton("Ekle", new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try {
						String Tag = tag.getText().toString();
						if (Validater
								.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(Tag)) {
							Database.Ekle(Tag + "\nHaber Tarihi:"
									+ Pref.getString("current", "").toString(),
									favori.getLink(), favori.getType());
						} else if (Validater
								.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(favori
										.getTitle())) {
							Database.Ekle(favori.getTitle() + "\nHaber Tarihi:"
									+ Pref.getString("current", "").toString(),
									favori.getLink(), favori.getType());
						} else {
							Database.Ekle(
									"Haber ("
											+ new Date().toLocaleString()
											+ ")\nHaber Tarihi:"
											+ Pref.getString("current", "")
													.toString(),
									favori.getLink(), favori.getType());
						}
					} catch (Exception e) {
						Log.e("----> Favoriler Ekleme ERROR :",
								"Ekleme d��mesinde sorun olu�tu.");
						Toast.makeText(getApplicationContext(),
								"��erik payla��lam�yor.", Toast.LENGTH_SHORT)
								.show();
						SendError(e, "Ekleme d��mesinde sorun olu�tu.");
					}

				}
			});
			alert.setNegativeButton("Vazge�", new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			linear.addView(message);
			linear.addView(tag);
			alert.setView(linear);
			alert.show();
		} else if (item.getTitle() == "PDF Olarak �ndir") {
			try {
				new PDFEngine(this).DownloadPDF(currentHaber.getLink());
			} catch (Exception e) {
				Log.e("----> Download PDF ERROR :",
						"PDF olarak indirme esnas�nda sorun olu�tu.");
				Toast.makeText(getApplicationContext(),
						"Dosya PDF olarak kaydedilemiyor.", Toast.LENGTH_SHORT)
						.show();
				SendError(e, "PDF olarak indirme esnas�nda sorun olu�tu.");
			}
		} else {
			return false;
		}
		return true;
	}

	private void SendError(final Exception exception, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(AnaSayfa.this);
		alert.setTitle("Sorun G�nderme");
		alert.setMessage("Olu�an sorunu l�tfen mail ile bize iletiniz.");
		alert.setPositiveButton("G�nder", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse("mailto:"));
					intent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { "modsoftcom@gmail.com" });
					intent.putExtra(Intent.EXTRA_SUBJECT,
							"Resmi Gazete Uygulamas�nda Sorun Olu�tu");
					intent.putExtra(
							Intent.EXTRA_TEXT,
							message + "\n\n" + exception.getMessage() != null ? exception
									.getMessage() : "");
					startActivity(Intent
							.createChooser(intent, "Sorun G�nderme"));
				} catch (Exception e) {
					Log.e("----> Intent ERROR :",
							"Sorun E-Mail ile g�nderilemedi.");
					Toast.makeText(getApplicationContext(), "E-Mail Sorunu.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		alert.setNegativeButton("Vazge�", new Dialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alert.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.previous_day:
			Clean();
			currentDate = new DateTime(currentDate).getYesterday();
			InfoText.setText(DateText.format(currentDate));
			actionBar.setTitle(DateText.format(currentDate));
			Gazete.CheckDate(currentDate.getYear() + 1900,
					currentDate.getMonth(), currentDate.getDate());
			return true;
		case R.id.next_day:

			if (!today()) {
				Clean();
				currentDate = new DateTime(currentDate).getTomorrow();
				InfoText.setText(DateText.format(currentDate));
				actionBar.setTitle(DateText.format(currentDate));
				Gazete.CheckDate(currentDate.getYear() + 1900,
						currentDate.getMonth(), currentDate.getDate());
			} else {
				Clean();
				Gazete.Today();
				Toast.makeText(getApplicationContext(),
						"Bug�nden sonras� g�r�nt�lenemez.", Toast.LENGTH_SHORT)
						.show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private boolean today() {
		if (currentDate.getDate() == new Date().getDate()) {
			if (currentDate.getMonth() == new Date().getMonth()) {
				if (currentDate.getYear() == new Date().getYear()) {
					return true;
				}
			}
		}
		return false;
	}

	private void ShowAboutDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(AnaSayfa.this);

		alert.setTitle("Hakk�nda");
		alert.setMessage("Bu uygulama, MODSoft taraf�ndan \"http://www.resmigazete.gov.tr/\" sitesindeki veriler ile haz�rlanm��t�r."
				+ "\nBurada yer alan t�m bilgi, belge ve veriler, Resmi Gazete'de yer almaktad�r. Ancak yaz�m"
				+ " yanl��lar� ya da kodlama hatas� nedeniyle olu�abilecek yanl��lardan ve bu sebeple do�abilecek"
				+ " zarardan taraf�m�z sorumlu de�ildir.");
		alert.setPositiveButton("Payla�",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent2 = new Intent();
						intent2.setAction(Intent.ACTION_SEND);
						intent2.setType("text/plain");
						intent2.putExtra(
								Intent.EXTRA_TEXT,
								"T.C. Resmi Gazetesi Android Uygulamas�\n"
										+ "https://play.google.com/store/apps/details?id=com.modsoft.resmigazete");
						startActivity(Intent.createChooser(intent2,
								"Bununla payla�..."));
					}
				});

		alert.setNegativeButton("Oy Ver",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("market://details?id="
								+ getPackageName()));
						startActivity(i);
					}
				});
		alert.setNeutralButton("Takip Et", new Dialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// https://twitter.com/modsoftcom
				Intent web = new Intent(Intent.ACTION_VIEW);
				web.setData(Uri.parse("http://twitter.com/modsoftcom"));
				startActivity(web);
			}
		});
		alert.show();
	}

	private void ShowFirstRunDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(AnaSayfa.this);
		alert.setTitle("Merhaba");
		alert.setCancelable(false);
		alert.setMessage("Resmi Gazete'nin yeni s�r�m�n� indirdi�iniz i�in te�ekk�r ederim. "
				+ "Bu s�r�mde eskisinden farkl� olarak,"
				+ "\n- Bildirim alma,"
				+ "\n- Bildirim tonu ayarlama,"
				+ "\n- Farkl� arkaplan"
				+ "\n�zellikleri geli�tirilmi�tir."
				+ "Umar�m be�enirsiniz."
				+ "\n\nL�tfen eksikler, hatalar ve geli�tirme fikirleriniz i�in "
				+ "mail ile irtibata ge�iniz. \"Geri Bildirim\" se�ene�inden mail g�nderebilirsiniz."
				+ "\n\nMemnuniyetinizi oylar�n�z ve yorumlar�n�zla Google Play sayfas�ndan bildirebilirsiniz."
				+ "\n\nSayg�lar�mla.");
		alert.setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});

		alert.setPositiveButton("Oy Ver",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("market://details?id="
								+ getPackageName()));
						startActivity(i);
					}
				});
		alert.setNeutralButton("Takip Et", new Dialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// https://twitter.com/modsoftcom
				Intent web = new Intent(Intent.ACTION_VIEW);
				web.setData(Uri.parse("http://twitter.com/modsoftcom"));
				startActivity(web);
			}
		});
		alert.show();
		Editor editor = Pref.edit();
		editor.putBoolean("first_run", false);
		editor.commit();
	}
}
