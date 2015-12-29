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
					"Favoriler yüklenemedi.");
			Toast.makeText(getApplicationContext(), "Favori yükleme Sorunu.",
					Toast.LENGTH_SHORT).show();
			SendError(e,"Favoriler yüklenemedi.");
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
					"Favori Context Menüsü yüklenemedi.");
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// Create your context menu here
		menu.setHeaderTitle("Seçenekler");
		menu.add(0, v.getId(), 0, "Tarayýcýda Aç");
		menu.add(0, v.getId(), 0, "Metni Kopyala");
		menu.add(0, v.getId(), 0, "Adresi Kopyala");
		menu.add(0, v.getId(), 0, "Paylaþ");
		menu.add(0, v.getId(), 0, "Favorilerden Sil");
		menu.add(0, v.getId(), 0, "PDF Olarak Ýndir");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Tarayýcýda Aç") {
			try {
				Intent web = new Intent(Intent.ACTION_VIEW);
				web.setData(Uri.parse(currentHaber.getLink()));
				startActivity(web);
			} catch (Exception e) {
				Log.e("----> Tarayýcý ERROR :",
						"Favori haber tarayýcýda açýlamadý.");
				Toast.makeText(getApplicationContext(), "Tarayýcý Sorunu.",
						Toast.LENGTH_SHORT).show();
				SendError(e,"Favori haber tarayýcýda açýlamadý.");
			}
		} else if (item.getTitle() == "Metni Kopyala") {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(currentHaber.getTitle());
				Toast.makeText(getApplicationContext(),
						"Haber baþlýðý kopyalandý.", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e("----> Copy ERROR :", "Haber adý kopyalanamadý.");
				Toast.makeText(getApplicationContext(),
						"Baþlýk kopyalanamýyor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Haber adý kopyalanamadý.");
			}
		} else if (item.getTitle() == "Adresi Kopyala") {
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(currentHaber.getLink());
				Toast.makeText(getApplicationContext(), "Haber adresi kopyalandý.",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e("----> Copy ERROR :", "Haber adresi kopyalanamadý.");
				Toast.makeText(getApplicationContext(),
						"Adres kopyalanamýyor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Haber adresi kopyalanamadý.");
			}
		} else if (item.getTitle() == "Paylaþ") {
			try {
				Intent intent2 = new Intent();
				intent2.setAction(Intent.ACTION_SEND);
				intent2.setType("text/plain");
				intent2.putExtra(
						Intent.EXTRA_TEXT,
						"Resmi Gazete'deki þu ilana göz atýn :\n"
								+ currentHaber.getTitle() + "\n"
								+ currentHaber.getLink());
				startActivity(Intent.createChooser(intent2, "Bununla paylaþ..."));
			} catch (Exception e) {
				Log.e("----> Share ERROR :", "Paylaþým menüsünde sorun oluþtu.");
				Toast.makeText(getApplicationContext(),
						"Ýçerik paylaþýlamýyor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Paylaþým menüsünde sorun oluþtu.");
			}
		} else if (item.getTitle() == "Favorilerden Sil") {
			try {
				Delete();
			} catch (Exception e) {
				Log.e("----> Delete ERROR :", "Favorinin silinmesinde sorun oluþtu.");
				Toast.makeText(getApplicationContext(),
						"Favori silinemiyor.", Toast.LENGTH_SHORT).show();
				SendError(e,"Favorinin silinmesinde sorun oluþtu.");
			}
		} else if (item.getTitle() == "PDF Olarak Ýndir") {
			try {
				new PDFEngine(this).DownloadPDF(currentHaber.getLink());
			} catch (Exception e) {
				Log.e("----> Download PDF ERROR :",
						"PDF olarak indirme esnasýnda sorun oluþtu.");
				Toast.makeText(getApplicationContext(),
						"Dosya PDF olarak kaydedilemiyor.", Toast.LENGTH_SHORT)
						.show();
				SendError(e,"PDF olarak indirme esnasýnda sorun oluþtu.");
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
		alert.setTitle("Sorun Gönderme");
		alert.setMessage("Oluþan sorunu lütfen mail ile bize iletiniz.");
		alert.setPositiveButton("Gönder", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					Intent intent = new Intent(Intent.ACTION_SENDTO);
					intent.setData(Uri.parse("mailto:"));
					intent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { "modsoftcom@gmail.com" });
					intent.putExtra(Intent.EXTRA_SUBJECT,
							"Resmi Gazete Uygulamasýnda Sorun Oluþtu");
					intent.putExtra(
							Intent.EXTRA_TEXT, message + "\n\n" + 
							exception.getMessage() != null ? exception
									.getMessage() : "");
					startActivity(Intent.createChooser(intent, "Sorun Gönderme"));
				} catch (Exception e) {
					Log.e("----> Intent ERROR :", "Sorun E-Mail ile gönderilemedi.");
					Toast.makeText(getApplicationContext(), "E-Mail Sorunu.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		alert.setNegativeButton("Vazgeç", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		alert.show();
		
	}
}
