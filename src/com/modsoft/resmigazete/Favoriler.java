package com.modsoft.resmigazete;

import java.util.ArrayList;
import java.util.List;

import com.modsoft.resmigazete.adapters.TabsPagerAdapter;
import com.modsoft.resmigazete.adapters.ZoomOutPageTransformer;
import com.modsoft.resmigazete.database.FavorilerDB;
import com.modsoft.resmigazete.engine.DialogEngine;
import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.PDFEngine;
import com.modsoft.resmigazete.engine.Screen;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

public class Favoriler  extends FragmentActivity{

	private FavorilerDB Database;
	private ViewPager pager;
	private PagerTabStrip tabStrip;
	private TabsPagerAdapter mAdapter;
	private List<Haber> Haberler = new ArrayList<Haber>(),
			haberList = new ArrayList<Haber>(),
			ilanList = new ArrayList<Haber>(),
			dosyaList = new ArrayList<Haber>();
	private Haber currentHaber;
	private int index;
	private boolean info;
	private SharedPreferences Pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favoriler);
		InitValues();
		Load();
		ViewPagerSet();
		if (info) {
			new DialogEngine(this).Info(Screen.Favoriler);
		}
	}
	
	private void InitValues() {
		pager = (ViewPager) findViewById(R.id.favoriler_main_pager);
		tabStrip = (PagerTabStrip) findViewById(R.id.favoriler_main_pager_header);
		Database = new FavorilerDB(this);
		Pref = PreferenceManager.getDefaultSharedPreferences(this);
		info = Pref.getBoolean("show_favori_tip", true);
	}
	
	private void Load() {
		try {
			Haberler = Database.Favoriler();
			if (Haberler.size() > 0) {
				for (Haber haber : Haberler) {
					switch (haber.getType()) {
					case Dosya:
						dosyaList.add(haber);
						break;
					case Haber:
						haberList.add(haber);
						break;
					case Ilan:
						ilanList.add(haber);
						break;
					case Kur:
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			Log.e("----> Load ERROR :",
					"Favoriler y�klenemedi.");
			Toast.makeText(getApplicationContext(), "Favori y�kleme Sorunu.",
					Toast.LENGTH_SHORT).show();
			SendError(e,"Favoriler y�klenemedi.");
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
	
	

	/**
	 * @return the haberList
	 */
	public List<Haber> getHaberList() {
		return haberList;
	}

	/**
	 * @param haberList the haberList to set
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
	 * @param ilanList the ilanList to set
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
	 * @param dosyaList the dosyaList to set
	 */
	public void setDosyaList(List<Haber> dosyaList) {
		this.dosyaList = dosyaList;
	}
	
	public void ContextMenu(View v, Haber haber, int i) {
		try {
			this.currentHaber = haber;
			this.index = i;
			registerForContextMenu(v);
		} catch (Exception e) {
			Log.e("----> Context ERROR :",
					"Favori Context Men�s� y�klenemedi.");
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
		menu.add(0, v.getId(), 0, "Favorilerden Sil");
		menu.add(0, v.getId(), 0, "PDF Olarak �ndir");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Taray�c�da A�") {
			try {
				Intent web = new Intent(Intent.ACTION_VIEW);
				web.setData(Uri.parse(currentHaber.getLink()));
				startActivity(web);
			} catch (Exception e) {
				Log.e("----> Taray�c� ERROR :",
						"Favori haber taray�c�da a��lamad�.");
				Toast.makeText(getApplicationContext(), "Taray�c� Sorunu.",
						Toast.LENGTH_SHORT).show();
				SendError(e,"Favori haber taray�c�da a��lamad�.");
			}
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
				SendError(e,"Haber ad� kopyalanamad�.");
			}
		} else if (item.getTitle() == "Adresi Kopyala") {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(currentHaber.getLink());
				Toast.makeText(getApplicationContext(), "Haber adresi kopyaland�.",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e("----> Copy ERROR :", "Haber adresi kopyalanamad�.");
				Toast.makeText(getApplicationContext(),
						"Adres kopyalanam�yor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Haber adresi kopyalanamad�.");
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
				startActivity(Intent.createChooser(intent2, "Bununla payla�..."));
			} catch (Exception e) {
				Log.e("----> Share ERROR :", "Payla��m men�s�nde sorun olu�tu.");
				Toast.makeText(getApplicationContext(),
						"��erik payla��lam�yor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Payla��m men�s�nde sorun olu�tu.");
			}
		} else if (item.getTitle() == "Favorilerden Sil") {
			try {
				Delete();
			} catch (Exception e) {
				Log.e("----> Delete ERROR :", "Favorinin silinmesinde sorun olu�tu.");
				Toast.makeText(getApplicationContext(),
						"Favori silinemiyor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Favorinin silinmesinde sorun olu�tu.");
			}
		} else if (item.getTitle() == "PDF Olarak �ndir") {
			try {
				new PDFEngine(this).DownloadPDF(currentHaber.getLink());
			} catch (Exception e) {
				Log.e("----> Download PDF ERROR :",
						"PDF olarak indirme esnas�nda sorun olu�tu.");
				Toast.makeText(getApplicationContext(),
						"Dosya PDF olarak kaydedilemiyor.", Toast.LENGTH_SHORT)
						.show();
				SendError(e,"PDF olarak indirme esnas�nda sorun olu�tu.");
			}
		} else {
			return false;
		}
		return true;
	}

	private void Delete() {
		switch (currentHaber.getType()) {
		case Dosya:
			dosyaList.remove(index);
			setDosyaList(dosyaList);
			break;
		case Haber:
			haberList.remove(index);
			setHaberList(haberList);
			break;
		case Ilan:
			ilanList.remove(index);
			setIlanList(ilanList);
			break;
		}
		ViewPagerSet();
		Database.Sil(currentHaber.getLink());
		Toast.makeText(getApplicationContext(),
				"Haber favorilerden silindi.", Toast.LENGTH_SHORT).show();
	}
	
	private void SendError(final Exception exception, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(Favoriler.this);
		alert.setTitle("Sorun G�nderme");
		alert.setMessage("Olu�an sorunu l�tfen mail ile bize iletiniz.");
		alert.setPositiveButton("G�nder", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse("mailto:"));
					intent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { "modsoftcom@gmail.com" });
					intent.putExtra(Intent.EXTRA_SUBJECT,
							"Resmi Gazete Uygulamas�nda Sorun Olu�tu");
					intent.putExtra(
							Intent.EXTRA_TEXT, message + "\n\n" + 
							exception.getMessage() != null ? exception
									.getMessage() : "");
					startActivity(Intent.createChooser(intent, "Sorun G�nderme"));
				} catch (Exception e) {
					Log.e("----> Intent ERROR :", "Sorun E-Mail ile g�nderilemedi.");
					Toast.makeText(getApplicationContext(), "E-Mail Sorunu.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		alert.setNegativeButton("Vazge�", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		alert.show();
		
	}
}
