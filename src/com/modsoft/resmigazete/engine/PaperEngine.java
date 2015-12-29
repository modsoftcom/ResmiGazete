package com.modsoft.resmigazete.engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.modsoft.resmigazete.AnaSayfa;
import com.modsoft.resmigazete.FragmentHaber;
import com.modsoft.resmigazete.PageView;
import com.modsoft.resmigazete.R;
import com.modsoft.resmigazete.adapters.HaberAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class PaperEngine {

	private Context context;
	private String baseURL = "http://www.resmigazete.gov.tr/";
	private int year_;
	private String day_;
	private String month_;
	private String urlHead;
	private List<Haber> Haberler = new ArrayList<Haber>();
	private List<Haber> Ilanlar = new ArrayList<Haber>();
	private List<Haber> Dosyalar = new ArrayList<Haber>();
	private SharedPreferences Pref;
	DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	DateFormat write = new SimpleDateFormat("dd MMMM yyyy");
	private Date selectedDate = new Date();;

	public PaperEngine() {

	}

	public PaperEngine(Context c) {
		this.context = c;
		Pref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void Today() {
		DateTime date = new DateTime();
		year_ = date.getYearInt();
		day_ = date.getDay();
		month_ = date.getMonth();
		urlHead = baseURL + "eskiler/" + year_ + "/" + month_ + "/";
		Editor editor = Pref.edit();
		editor.putString("current", format.format(new Date()));
		editor.commit();
		new GazeteGetir().execute();
	}

	public void Mukerrer() {
		new Mukerrer().execute();
	}

	private class GazeteGetir extends AsyncTask<String, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(context);

		protected void onPreExecute() {
//			if (selectedDate.getYear() >= 1900) {
//				selectedDate.setYear(selectedDate.getYear() - 1900);
//			}
			String tarih = 	write.format(selectedDate);
			Dialog.setMessage(tarih + " tarihine ait Resmi Gazete yükleniyor.\nLütfen Bekleyiniz...");
			Dialog.setCancelable(false);
			Dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				bugunLoad(day_, month_, year_);
			} catch (Exception e) {
				Log.e("----> Loading Error", "Bugünkü gazete yüklenemedi.");
			}
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();
			CreateListView();
		}
	}

	private class Mukerrer extends AsyncTask<String, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(context);
		private String URL;

		protected void onPreExecute() {
			urlHead = "http://www.resmigazete.gov.tr/mukerrer/";
			String urlLink = "mukerrer.htm";
			URL = urlHead + urlLink;
			Dialog.setMessage("Yükleniyor.\nLütfen Bekleyiniz...");
			Dialog.setCancelable(false);
			Dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				mukerrerLoad(URL);
			} catch (Exception e) {
				Log.e("-----> Mukerrer Error", "Mukerrer Gazete Yüklenemedi.");
			}
			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();
			CreateListView();
		}
	}

	public void bugunLoad(String d, String m, int y) {
		Document doc;
		try {
			Date date = new Date();
			int yil = date.getYear() + 1900;
			final String day = d;// (date.getDay() < 10 ? "0"+date.getDay():
									// date.getDay()).toString();
			final String year = String.valueOf(y);
			final String month = m;// (date.getMonth() < 10 ?
									// "0"+date.getMonth():
									// date.getMonth()).toString();

			doc = Jsoup.connect(urlHead + year + month + day + ".htm").get();

			Elements links = doc.select("a[href]");

			String name = "";
			String url = "";
			LoadPaper(year, month, links);
		} catch (Exception e) {
			Log.e(">>> JSOUP Error", "Jsoup verileri alamadý");
		}
	}

	public void LoadPaper(final String year, final String month, Elements links) {
		String name;
		String url;
		for (Element link : links) {
			name = link.text();
			if (link.attr("href").contains("eskiilanlar")) {
				String rawLink = link.attr("href");
				int bas = rawLink.indexOf("main=http") + 5;
				rawLink = rawLink.substring(bas);
				bas = rawLink.indexOf(year + month);
				String urlLink = rawLink.substring(bas);
				urlHead = "http://www.resmigazete.gov.tr/ilanlar/eskiilanlar/"
						+ year + "/" + month + "/";
				url = urlHead + urlLink;
				urlHead = "http://www.resmigazete.gov.tr/eskiler/" + year + "/"
						+ month + "/";
				Ilanlar.add(new Haber(name, url, HaberType.Ilan));
			} else if (!link.attr("href").endsWith("htm")) {
				urlHead = "http://www.resmigazete.gov.tr/eskiler/" + year + "/"
						+ month + "/";
				url = link.attr("href").startsWith("http") ? link.attr("href")
						: urlHead + link.attr("href");
				Dosyalar.add(new Haber(name, url, HaberType.Dosya));
			} else if (!name.equalsIgnoreCase("Æ")
					&& !name.equalsIgnoreCase("Å")
					&& !name.equalsIgnoreCase("")) {
				urlHead = "http://www.resmigazete.gov.tr/eskiler/" + year + "/"
						+ month + "/";
				url = link.attr("href").startsWith("http") ? link.attr("href")
						: urlHead + link.attr("href");
				Haberler.add(new Haber(name, url, HaberType.Haber));
			}

		}
	}

	public void mukerrerLoad(String URL) {
		Document doc;
		try {
			doc = Jsoup.connect(URL).get();

			Elements links = doc.select("a[href]");
			String name = "";
			String url = "";
			for (Element link : links) {
				name = link.text();
				if (link.attr("href").contains("ilanlar")) {
					url = "http://www.resmigazete.gov.tr/mukerrer/"
							+ link.attr("href");
					Ilanlar.add(new Haber(name, url, HaberType.Ilan));
				} else if (link.attr("href").endsWith("pdf")) {
					url = "http://www.resmigazete.gov.tr/mukerrer/"
							+ link.attr("href");
					Dosyalar.add(new Haber(name, url, HaberType.Dosya));
				} else if (!name.equalsIgnoreCase("Æ")) {
					url = "http://www.resmigazete.gov.tr/mukerrer/"
							+ link.attr("href");
					Haberler.add(new Haber(name, url, HaberType.Haber));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void CreateListView() {
		AnaSayfa a = (AnaSayfa) context;
		a.setHaberList(Haberler);
		a.setIlanList(Ilanlar);
		a.setDosyaList(Dosyalar);
		a.ViewPagerSet();
	}

	public void CheckDate(int selectedYear, int selectedMonth, int selectedDay) {
		selectedDate.setDate(selectedDay);
		selectedDate.setMonth(selectedMonth);
		selectedDate.setYear(selectedYear-1900);
		year_ = selectedYear;
		int month = selectedMonth + 1;
		month_ = (month < 10 ? "0" + String.valueOf(month) : month).toString();
		day_ = String.valueOf(selectedDay < 10 ? "0" + selectedDay
				: selectedDay);
		urlHead = "http://www.resmigazete.gov.tr/eskiler/" + year_ + "/"
				+ month_ + "/";

		DateTime now = new DateTime();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1, date2;
			date1 = sdf.parse(now.getYear() + "-" + now.getMonth() + "-"
					+ now.getDay());
			date2 = sdf.parse(String.valueOf(year_) + "-"
					+ String.valueOf(month_) + "-" + String.valueOf(day_));
			if (date1.after(date2)) {
				if (year_ >= 2007) {
					Editor editor = Pref.edit();
					editor.putString("current", format.format(selectedDate));
					editor.commit();
					new GazeteGetir().execute();
				} else {
					Toast.makeText(context,
							"Lütfen 2006 yýlýndan sonraki bir tarih giriniz.",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(context, "Lütfen eski bir tarih giriniz.",
						Toast.LENGTH_LONG).show();
				Today();
			}
		} catch (Exception e) {
			Log.e("-----> CheckDate ERROR",
					"Tarihi kontrol ederken hata oluþtu.");
		}
	}

	public void SayfaGetir(Haber hbr) {
		new SayfaGetir(hbr).execute();
	}

	private class SayfaGetir extends AsyncTask<String, Void, Void> {
		private ProgressDialog Dialog = new ProgressDialog(context);
		private Haber haber;
		private String HTML;

		public SayfaGetir(Haber hbr) {
			this.haber = hbr;
		}

		protected void onPreExecute() {
			Dialog.setMessage("Sayfa yükleniyor.\nLütfen Bekleyiniz...");
			Dialog.setCancelable(false);
			Dialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				StringBuilder builder = new StringBuilder(100000);
				String hedef = haber.getLink();
				DefaultHttpClient client = new DefaultHttpClient();

				HttpGet httpGet = new HttpGet(hedef);

				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content, "iso-8859-9"));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						builder.append(s);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				HTML = builder.toString();
			} catch (Exception e) {

			}

			return null;
		}

		protected void onPostExecute(Void unused) {
			Dialog.dismiss();
			try {
				String raw = HTML;

				String ilk = "<html><head>";
				int bas = raw.indexOf("<tr style='mso-yfti-irow:1");
				int son = raw.indexOf("</html>") + 7;
				raw = raw.substring(bas, son);
				String mobileCode = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />"
						+ "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-9\">"
						+ "<META HTTP-EQUIV=\"Content-language\" CONTENT=\"tr\">";
				raw = ilk + mobileCode + raw;

				Intent intent = new Intent(context, PageView.class);
				if ((haber.getTitle().contains("Yargý Ýlânlarý") || haber
						.getTitle().contains(
								"Artýrma, Eksiltme ve Ýhale Ýlânlarý"))
						|| (haber.getTitle().contains("Çeþitli Ýlânlar") || haber
								.getTitle().contains(
										"MERKEZ BANKASINCA BELÝRLENEN"))) {
					intent.putExtra("html", "");
				} else {
					intent.putExtra("html", raw);
				}
				urlHead = getRoot(haber);
				intent.putExtra("rootLink", urlHead);
				intent.putExtra("Url", haber.getLink());
				intent.putExtra("Name", haber.getTitle());
				context.startActivity(intent);

			} catch (Exception e) {

			}
		}

		private String getRoot(Haber hbr) {
			String[] Parts = hbr.getLink().split("/");
			int son = Parts.length - 1;
			String root = "";
			for (String part : Parts) {
				if (!part.equalsIgnoreCase(Parts[son])) {
					root += part + "/";
				}
			}
			return root;
		}
	}

}
