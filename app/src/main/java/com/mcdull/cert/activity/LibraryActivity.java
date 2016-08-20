package com.mcdull.cert.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.LibraryAdapter;
import com.mcdull.cert.json.AbsJsonUtil;

public class LibraryActivity extends MyTitleActivity {

    private TextView tvQueryTitle;
    private ListView lvLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_library);
        super.onCreate(savedInstanceState);

        initView();

        init();
    }

    private void init() {
        String libraryJson = getIntent().getStringExtra("libraryJson");
        List<String> list = null;
        try {
            list = AbsJsonUtil.getJsonUtil(this).libraryParseJson(libraryJson);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询失败,请稍候再试.", Toast.LENGTH_SHORT).show();
            finish();
        }
        tvQueryTitle.setText("图书馆查询");
        if (list==null)
            return;
        if (list.size() != 0) {
            findViewById(R.id.text).setVisibility(View.GONE);
            LibraryAdapter libraryAdapter = new LibraryAdapter(this, list);
            lvLibrary.setAdapter(libraryAdapter);
        }
    }

    private void initView() {
        tvQueryTitle = (TextView) findViewById(R.id.tv_title);
        lvLibrary = (ListView) findViewById(R.id.lv_library);
    }
}
