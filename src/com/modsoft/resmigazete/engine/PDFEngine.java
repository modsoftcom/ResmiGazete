package com.modsoft.resmigazete.engine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;

public class PDFEngine{

	private String URL = "";
	private String FileName = "";
	private WebView WEB;
	private Bitmap bmp;
	private Context context;

	public PDFEngine(Context c)
	{
		this.context = c;
	}
	
	public String getDownloadURL(String s)
	{
		String URL = "http://convertmyurl.net/?url=";
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectOrientation=Portrait").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderLeft=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderRight=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderCenter=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderFont=12").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectFooterLeft=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectFooterRight=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectFooterCenter=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectUserAgent=default").toString();
		String adres = URL
				+ (new StringBuilder(String.valueOf(s))).append(
						"&user_agent=urltopdf_v0").toString();
		return adres;
	}
	
	public void DownloadPDF(String s) {

		String URL = "http://convertmyurl.net/?url=";
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectOrientation=Portrait").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderLeft=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderRight=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderCenter=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectHeaderFont=12").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectFooterLeft=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectFooterRight=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectFooterCenter=none").toString();
		s = (new StringBuilder(String.valueOf(s))).append(
				"&selectUserAgent=default").toString();
		String adres = URL
				+ (new StringBuilder(String.valueOf(s))).append(
						"&user_agent=urltopdf_v0").toString();
		Intent web = new Intent(Intent.ACTION_VIEW);
		web.setData(Uri.parse(adres));
		context.startActivity(web);
	}


	
//	public void AddTagToDocument(String file) {
//		Document document = new Document();
//
//		try {
//			PdfWriter.getInstance(document, new FileOutputStream(file));
//
//			document.open();
//			document.add(new Paragraph(
//					"https://play.google.com/store/apps/details?id=com.modsoft.resmigazete&hl=tr"));
//			document.close(); // no need to close PDFwriter?
//
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
}
