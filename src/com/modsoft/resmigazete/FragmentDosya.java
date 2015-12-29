package com.modsoft.resmigazete;

import java.util.List;

import com.modsoft.resmigazete.adapters.HaberAdapter;
import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.PaperEngine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FragmentDosya  extends Fragment{
	private View rootView;
	private ListView list;
	private TextView info;

	public FragmentDosya() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_dosya, container, false);
		list = (ListView)rootView.findViewById(R.id.main_dosyalar_list);
		info = (TextView) rootView.findViewById(R.id.dosya_info_text);
		String main = rootView.getContext().getClass().getSimpleName();
		if (main.equalsIgnoreCase("AnaSayfa")) {
			CreateListView(1);
		}else
		{
			CreateListView(2);
		}
		return rootView;
	}
	public void CreateListView(int i)
	{
		switch (i) {
		case 1:
			AnaSayfa ana = (AnaSayfa)getActivity();
			if (ana.getDosyaList().size() > 0) {
				info.setVisibility(View.INVISIBLE);
				FillList(ana.getDosyaList(), i);
			} else
			{
				info.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			Favoriler fav = (Favoriler)getActivity();
			if (fav.getDosyaList().size() > 0) {
				info.setVisibility(View.INVISIBLE);
				FillList(fav.getDosyaList(), i);
			} else
			{
				info.setVisibility(View.VISIBLE);
			}
			break;
		}
		
		
	}

	public void FillList(final List<Haber> dosyalar, final int i) {
		list.setAdapter(null);
		list.setAdapter(new HaberAdapter(rootView.getContext(), dosyalar));
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Intent web = new Intent(Intent.ACTION_VIEW);
				web.setData(Uri.parse(dosyalar.get(position).getLink()));
				startActivity(web);
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener()  {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (i) {
				case 1:
					AnaSayfa ana = (AnaSayfa)getActivity();
					ana.ContextMenu(list, dosyalar.get(position));
					break;
				case 2:
					Favoriler fav = (Favoriler)getActivity();
					fav.ContextMenu(list, dosyalar.get(position), position);
					break;
				}
				return false;
			}
		});
	}
}
