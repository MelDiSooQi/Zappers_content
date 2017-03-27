package com.zapper.view.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zapper.R;


public class MasterFragment extends Fragment {

    public MasterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_master, container, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
//        ((LinearLayout)findViewById(R.id.webviewPlace)).removeAllViews();
        super.onConfigurationChanged(newConfig);

        initUI();
    }

    public void initUI()
    {
//        getActivity().setContentView(R.layout.main);
//        if (wv == null) wv = new WebView(this);
//        ((LinearLayout)findViewById(R.id.webviewPlace)).addView(wv);
//        findViewById(R.id.home).setOnClickListener(this);
    }
}
