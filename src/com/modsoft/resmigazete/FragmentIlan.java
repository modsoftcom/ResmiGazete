package com.modsoft.resmigazete;

import java.util.List;

import com.modsoft.resmigazete.adapters.HaberAdapter;
import com.modsoft.resmigazete.engine.Haber;
import com.modsoft.resmigazete.engine.PaperEngine;

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

public class FragmentIlan extends Fragment {
	private View rootView;
	private ListView list;
	private TextView info;
	
	public FragmentIlan() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_ilan, container, false);
		list = (ListView) rootView.findViewById(R.id.main_ilan_list);
		info = (TextView) rootView.findViewById(R.id.ilan_info_text);
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
			if (ana.getIlanList().size() > 0) {
				info.setVisibility(View.INVISIBLE);
				FillList(ana.getIlanList(), i);
			} else
			{
				info.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			Favoriler fav = (Favoriler) getActivity();
			if (fav.getIlanList().size() > 0) {
				info.setVisibility(View.INVISIBLE);
				FillList(fav.getIlanList(), i);
			} else
			{
				info.setVisibility(View.VISIBLE);
			}
			break;
		}
		
	}

	public void FillList(final List<Haber> ilanlar, final int i) {
		list.setAdapter(null);
		list.setAdapter(new HaberAdapter(rootView.getContext(), ilanlar));
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new PaperEngine(rootView.getContext()).SayfaGetir(ilanlar.get(position));
			}
		});
		
		list.setOnItemLongClickListener(new OnItemLongClickListener()  {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (i) {
				case 1:
					AnaSayfa ana = (AnaSayfa)getActivity();
					ana.ContextMenu(list, ilanlar.get(position));
					break;
				case 2:
					Favoriler fav = (Favoriler)getActivity();
					fav.ContextMenu(list, ilanlar.get(position), position);
					break;
				}
				return false;
			}
		});
	}

	
}
