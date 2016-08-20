package com.mcdull.cert.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mcdull.cert.R;
import com.mcdull.cert.adapter.FoundAdapter;

public class FoundFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_found, container, false);

        ListView mLvFound = (ListView) view.findViewById(R.id.lv_found);

        FoundAdapter adapter = new FoundAdapter(getActivity());

        mLvFound.setAdapter(adapter);

        return view;
    }

}
