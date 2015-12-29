package com.modsoft.resmigazete.database;

import java.util.ArrayList;
import java.util.List;

import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.HaberType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class FavorilerDB extends SQLiteOpenHelper {

	private static final String VERITABANI_ADI = "DATABASE";
	private static final int SURUM = 1;
	private static String TABLE_NAME = "Favoriler";
	private static String FAVORI_ID = "ID";
	private static String FAVORI_NAME = "NAME";
	private static String FAVORI_LINK = "LINK";
	private static String FAVORI_TYPE = "TYPE";
	private Context context;

	public FavorilerDB(Context c) {
		super(c, VERITABANI_ADI, null, SURUM);
		this.context = c;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + FAVORI_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + FAVORI_NAME
				+ " TEXT," + FAVORI_LINK + " TEXT," + FAVORI_TYPE + " TEXT)";
		db.execSQL(CREATE_TABLE);
	}

	public void Ekle(String name, String link, HaberType type) {
		if (!isExist(new Haber(link))) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FAVORI_NAME, name);
			values.put(FAVORI_LINK, link);
			values.put(FAVORI_TYPE, type.toString());

			db.insert(TABLE_NAME, null, values);
			db.close();
			Toast.makeText(context,
					"Haber favorilere eklendi.", Toast.LENGTH_SHORT)
					.show();
		}else {
			Toast.makeText(context, "Haber, favorilerde zaten mevcut",
					Toast.LENGTH_SHORT).show();
		}
		
	}

	public void Ekle(Haber haber) {
		if (!isExist(haber)) {
			String name = haber.getTitle();
			String link = haber.getLink();
			String type = haber.getType().toString();
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FAVORI_NAME, name);
			values.put(FAVORI_LINK, link);
			values.put(FAVORI_TYPE, type);
			db.insert(TABLE_NAME, null, values);
			db.close();
			Toast.makeText(context,
					"Haber favorilere eklendi.", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(context, "Haber, favorilerde zaten mevcut",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isExist(Haber haber) {
		boolean result = false;

		String selectedQuery = "SELECT * FROM " + TABLE_NAME + " WHERE LINK=\'"
				+ haber.getLink() +"\'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectedQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	public void Sil(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, FAVORI_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void Sil(String link) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, FAVORI_LINK + " = ?", new String[] { link });
		db.close();
	}

	public Haber FavoriDetay(int id) {
		Haber favori = new Haber();
		String selectedQuery = "SELECT * FROM " + TABLE_NAME + " WHERE ID="
				+ id;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectedQuery, null);

		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			favori.setTitle(cursor.getString(1));
			favori.setLink(cursor.getString(2));
			favori.setType(getType(cursor));
		}
		cursor.close();
		db.close();

		return favori;
	}

	private HaberType getType(Cursor cursor) {
		if (cursor.getString(3).equalsIgnoreCase(HaberType.Haber.toString())) {
			return HaberType.Haber;
		} else if (cursor.getString(3).equalsIgnoreCase(
				HaberType.Ilan.toString())) {
			return HaberType.Ilan;
		} else if (cursor.getString(3).equalsIgnoreCase(
				HaberType.Dosya.toString())) {
			return HaberType.Dosya;
		} else if (cursor.getString(3).equalsIgnoreCase(
				HaberType.Kur.toString())) {
			return HaberType.Kur;
		} else
			return HaberType.Haber;
	}

	public List<Haber> Favoriler() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
		List<Haber> favoriList = new ArrayList<Haber>();

		if (cursor.moveToFirst()) {
			do {
				favoriList.add(new Haber(cursor.getString(1), cursor
						.getString(2), getType(cursor)));
			} while (cursor.moveToNext());
		}
		db.close();

		return favoriList;
	}

	public void favoriDuzenle(String name, String link, String type, int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FAVORI_NAME, name);
		values.put(FAVORI_LINK, link);
		values.put(FAVORI_TYPE, type);

		db.update(TABLE_NAME, values, FAVORI_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public void favoriDuzenle(Haber haber, int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FAVORI_NAME, haber.getTitle());
		values.put(FAVORI_LINK, haber.getLink());
		values.put(FAVORI_TYPE, haber.getType().toString());

		db.update(TABLE_NAME, values, FAVORI_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXIST favoriler");
		onCreate(db);
	}
}
