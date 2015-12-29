package com.modsoft.resmigazete.adapters;

import java.util.List;

import com.modsoft.resmigazete.R;
import com.modsoft.resmigazete.engine.Haber;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HaberAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ImageView thumb_image;
	List<Haber> haberler;
	ViewHolder holder;

	public HaberAdapter() {
		// TODO Auto-generated constructor stub
	}

	public HaberAdapter(Context act, List<Haber> hbrs) {

		this.haberler = hbrs;
		inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		// return idlist.size();
		return haberler.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}



	static class ViewHolder {
		TextView haber_title;
	}



	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {

			vi = inflater.inflate(R.layout.haber_row, null);
			holder = new ViewHolder();

			holder.haber_title = (TextView) vi
					.findViewById(R.id.haber_row_text);
			vi.setTag(holder);
		} else {

			holder = (ViewHolder) vi.getTag();
		}

		// Setting all values in listview
		String title = haberler.get(position).getTitle().toString();
		if (!title.equalsIgnoreCase("")) {
			holder.haber_title.setText(title);
		}
		else
		{
			holder.haber_title.setText("PDF Olarak Kaydet");
		}
		return vi;
	}

}
