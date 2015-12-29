package com.modsoft.resmigazete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.modsoft.resmigazete.database.FavorilerDB;
import com.modsoft.resmigazete.engine.DialogEngine;
import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.HaberType;
import com.modsoft.resmigazete.engine.PDFEngine;
import com.modsoft.resmigazete.engine.Screen;
import com.modsoft.resmigazete.utilities.Validater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PageView extends Activity {

	private ImageView FindButton, URLButton, ShareButton, BookmarkButton,
			PDFButton;
	private WebView Web;
	private DrawerLayout mDrawerLayout;
	private ScrollView mDrawer;
	private String fullHTML;
	private String rootLink;
	private String URL;
	private String Name;
	private String imagesHTML = "";
	private Bitmap bmp;
	private Picture picture;
	private List<String> images = new ArrayList<String>();
	private Haber currentHaber;
	private FavorilerDB Database;
	private SharedPreferences Pref;
	private boolean info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_view);
		InitValues();
		SetWebBrowser();
		GetIntents();
		CreatePage();
		if (info) {
			new DialogEngine(this).Info(Screen.PageView);
		}
	}

	private void SetWebBrowser() {
		Web = (WebView) findViewById(R.id.pageview_web_browser);
		Web.getSettings().setLoadWithOverviewMode(true);
		Web.getSettings().setUseWideViewPort(true);
		Web.getSettings().setBuiltInZoomControls(true);
		Web.getSettings().setSupportZoom(true);
		Web.getSettings().setLightTouchEnabled(true);
		Web.getSettings().setLoadsImagesAutomatically(true);
		Web.setOnLongClickListener(new android.view.View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if (mDrawerLayout.isDrawerOpen(mDrawer)) {
					mDrawerLayout.closeDrawer(mDrawer);
				} else {
					mDrawerLayout.openDrawer(mDrawer);
				}
				return false;
			}
		});
	}

	private void GetIntents() {
		Intent intent = getIntent();

		fullHTML = intent.getStringExtra("html");
		rootLink = intent.getStringExtra("rootLink");
		URL = intent.getStringExtra("Url");
		Name = intent.getStringExtra("Name");

		currentHaber = new Haber(Name, URL, HaberType.Haber);
	}

	private void CreatePage() {
		if (fullHTML.startsWith("http")) {
			Web.loadUrl(URL);
		} else {
			currentHaber.setType(HaberType.Ilan);
			if (Name.contains("Yargý Ýlânlarý")) {
				new LoadImages().execute();
			} else if (Name.contains("Artýrma, Eksiltme ve Ýhale Ýlânlarý")) {
				new CesitliIlanlar().execute();
			} else if (Name.contains("Çeþitli Ýlânlar")) {
				new CesitliIlanlar().execute();
			} else if (Name.contains("Merkez Bankasýnca Belirlenen")) {
				new LoadImages().execute();
			} else {
				currentHaber.setType(HaberType.Haber);
				int ara = fullHTML.indexOf("href=\"") + 6;
				StringBuffer sb = new StringBuffer(fullHTML);
				sb.insert(ara, rootLink);
				fullHTML = sb.toString();
				Web.loadDataWithBaseURL("", fullHTML, "text/html", "utf-8", "");
			}
		}
	}

	private class LoadImages extends AsyncTask<String, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(PageView.this);

		protected void onPreExecute() {
			Dialog.setMessage("Yükleniyor.\nLütfen Bekleyiniz...");
			Dialog.setCancelable(false);
			Dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			Document doc;
			try {
				doc = Jsoup.connect(URL).get();
				Elements links = doc.select("img[src]");
				String url = "";
				for (Element link : links) {
					String urlHead = rootLink;
					url = link.attr("src").startsWith("src") ? link.attr("src")
							: urlHead + link.attr("src");
					images.add(url);
				}
			} catch (Exception e) {
				Log.e("----> JSOUP ERROR :", "JSOUP API'sinde sorun oluþtu.");
			}
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();
			try {
				StringBuilder builder = new StringBuilder();
				String mobileCode = "<html><head><META name=\"viewport\" content=\"width=device-width, initial-scale=1\" />"
						+ "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-9\"/>"
						+ "<META HTTP-EQUIV=\"Content-language\" CONTENT=\"tr\"/>";
				builder.append(mobileCode);
				for (String image : images) {
					builder.append("<p><img src=\"" + image
							+ "\" border=\"0\"></p>");
				}
				builder.append("</head></html>");
				imagesHTML = builder.toString();
				Web.loadDataWithBaseURL("", imagesHTML, "text/html", "utf-8",
						"");
			} catch (Exception e) {
				Log.e("----> LoadImage ERROR :",
						"LoadImage Class'ýnda sorun oluþtu. PostExecute Sorunu.");
				Toast.makeText(
						getApplicationContext(),
						"Sayfa istenilen þekilde yüklenemiyor. Lütfen üreticiye sorunu bildiriniz.",
						Toast.LENGTH_SHORT).show();
				SendError(e, "LoadImage Class'ýnda sorun oluþtu. PostExecute Sorunu.");
			}
		}

		
	}

	private class CesitliIlanlar extends AsyncTask<String, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(PageView.this);
		private StringBuilder HTML;
		String linkler = "";

		protected void onPreExecute() {
			HTML = new StringBuilder();
			Dialog.setMessage("Yükleniyor.\nLütfen Bekleyiniz...");
			Dialog.setCancelable(false);
			Dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			Document doc;
			try {
				doc = Jsoup.connect(URL).get();
				Elements links = doc.select("a[href]");
				Elements innerLinks = doc.select("a[name]");
				String name = "";
				String url = "";
				try {
					for (Element link : links) {
						name = link.text();
						url = link.attr("href");
						for (Element innerLink : innerLinks) {
							if (url.equalsIgnoreCase("#"
									+ innerLink.attr("name"))) {
								getText(HTML, name, url, innerLink,
										innerLink.parent());
							}
						}
					}
				} catch (Exception e) {

				}
				Elements imgs = doc.select("img[src]");
				for (Element img : imgs) {
					String urlHead = rootLink;
					url = urlHead + img.attr("src");
					images.add(url);
				}
			} catch (Exception e) {

			}
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();
			try {
				StringBuilder builder = new StringBuilder();
				String mobileCode = "<html><head>"
						+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />"
						+ "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-9\">"
						+ "<META HTTP-EQUIV=\"Content-language\" CONTENT=\"tr\">";
				builder.append(mobileCode);
				builder.append(HTML);
				for (String image : images) {
					builder.append("<p><img src=\"" + image
							+ "\" border=\"0\"></p>");
				}
				builder.append("</head></html>");
				imagesHTML = builder.toString();
				Web.loadDataWithBaseURL("", imagesHTML, "text/html", "utf-8",
						"");
			} catch (Exception e) {

			}
		}
	}

	public void getText(StringBuilder h, String name, String url,
			Element innerLink, Element element) {
		h.append(element);
		Element e = element.nextElementSibling();
		if (!e.child(0).hasAttr("name")) {
			getText(h, name, url, innerLink, e);
		}
	}

	public void FindInPage(View view) {
		AlertDialog.Builder findAlert = new AlertDialog.Builder(PageView.this);
		findAlert.setTitle("Kelime Arama");
		final EditText word = new EditText(PageView.this);
		word.setHint("Aranacak kelime...");
		findAlert.setView(word);
		findAlert.setPositiveButton("Sayfada Bul", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String string = word.getText().toString();
				if (Build.VERSION.SDK_INT >= 16) {
					Web.findAllAsync(string); // works for API levels 16 and up
				} else {
					Web.findAll(string); // works for API levels 3 - 16
				}
			}
		});
		findAlert.setNegativeButton("Vazgeç", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		findAlert.show();
	}

	public void OpenURL(View view) {
		Intent web = new Intent(Intent.ACTION_VIEW);
		web.setData(Uri.parse(URL));
		startActivity(web);
	}

	public void SharePage(View view) {
		Intent share = new Intent();
		share.setAction(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT,
				"Resmi Gazete'deki þu ilana göz atýn :\n" + Name + "\n" + URL);
		startActivity(Intent.createChooser(share, "Bununla paylaþ..."));
	}

	public void AddToBookmarks(View view) {
		final Haber favori = currentHaber;
		AlertDialog.Builder alert = new AlertDialog.Builder(PageView.this);
		alert.setTitle("Favorilere Ekleme");
		LinearLayout linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);
		TextView message = new TextView(this);
		message.setText("Haberin ismini yazýnýz.\nMevcut ismi ile kaydetmek için doðrudan Ekle tuþuna basýnýz.");
		final EditText tag = new EditText(this);
		tag.setHint(favori.getTitle());
		alert.setPositiveButton("Ekle", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					String Tag = tag.getText().toString();
					if (Validater.isNotNullNotEmptyNotWhiteSpaceOnlyByJava(Tag)) {
						Database.Ekle(
								Tag
										+ "\nHaber Tarihi:"
										+ Pref.getString("current", "")
												.toString(), favori.getLink(),
								favori.getType());
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
												.toString(), favori.getLink(),
								favori.getType());
					}
				} catch (Exception e) {
					Log.e("----> Favoriler Ekleme ERROR :",
							"Ekleme düðmesinde sorun oluþtu.");
					Toast.makeText(getApplicationContext(),
							"Ýçerik paylaþýlamýyor.", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		alert.setNegativeButton("Vazgeç", new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		linear.addView(message);
		linear.addView(tag);
		alert.setView(linear);
		alert.show();
	}

	public void SaveAsPDF(View view) {
		// AlertDialog.Builder alert = new AlertDialog.Builder(this);
		// alert.setTitle("Bilgilendirme");
		// alert.setMessage("Bu seçenek bir sonraki sürümde daha kullanýlýr hale gelecektir. "
		// + "Sayfayý tarayýcý üzerinden indirmek ister misiniz?");
		// alert.setPositiveButton("Ýndir", new OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// DownloadPDF(URL);
		// }
		// });
		// alert.setNegativeButton("Vazgeç", new OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		//
		// }
		// });
		// alert.show();
		new PDFEngine(this).DownloadPDF(URL);
	}

	private void InitValues() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.pageview_drawer_layout);
		mDrawer = (ScrollView) findViewById(R.id.pageview_slider_menu);
		Database = new FavorilerDB(this);
		Pref = PreferenceManager.getDefaultSharedPreferences(this);
		info = Pref.getBoolean("show_pageview_tip", true);
	}

	private void SendError(final Exception exception, final String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(PageView.this);
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
