package com.modsoft.resmigazete;

import java.util.List;

import com.modsoft.resmigazete.adapters.HaberAdapter;
import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.PaperEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

public class FragmentHaber extends Fragment {
	private View rootView;
	private ListView list;
	private TextView info;

	public FragmentHaber() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_haber, container, false);
		list = (ListView) rootView.findViewById(R.id.main_haberler_list);
		info = (TextView) rootView.findViewById(R.id.haber_info_text);
		String main = rootView.getContext().getClass().getSimpleName();
		if (main.equalsIgnoreCase("AnaSayfa")) {
			CreateListView(1);
		}else
		{
			CreateListView(2);
		}
		
		return rootView;
	}

	public void CreateListView(int i) {
		switch (i) {
		case 1:
			AnaSayfa ana = (AnaSayfa) getActivity();
			if (ana.getHaberList().size() > 0) {
				info.setVisibility(View.INVISIBLE);
				FillList(ana.getHaberList(), i);
			} else
			{
				info.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			Favoriler fav = (Favoriler) getActivity();
			if (fav.getHaberList().size() > 0) {
				info.setVisibility(View.INVISIBLE);
				FillList(fav.getHaberList(), i);
			} else
			{
				info.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	public void FillList(final List<Haber> haberler, final int i) {
		list.setAdapter(null);
		list.setAdapter(new HaberAdapter(rootView.getContext(), haberler));
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new PaperEngine(rootView.getContext()).SayfaGetir(haberler.get(position));
			}
		});

		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (i) {
				case 1:
					AnaSayfa ana = (AnaSayfa) getActivity();
					ana.ContextMenu(list, haberler.get(position));
					break;
				case 2:
					Favoriler fav = (Favoriler) getActivity();
					fav.ContextMenu(list, haberler.get(position), position);
					break;
				}
				
				return false;
			}
		});
	}
}
