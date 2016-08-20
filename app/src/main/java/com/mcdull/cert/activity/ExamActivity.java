package com.mcdull.cert.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.ExamAdapter;
import com.mcdull.cert.json.AbsJsonUtil;

public class ExamActivity extends MyTitleActivity {

    private TextView tvQueryTitle;
    private TextView tvClass;
    private ListView lvExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_exam);
        super.onCreate(savedInstanceState);

        initView();

        intiNew();
    }

    private void intiNew() {
        tvQueryTitle.setText("考试安排");
        tvClass.setText("考试安排");
        String examJson = getIntent().getStringExtra("examJson");
        List<List<String>> list = null;
        try {
            list = AbsJsonUtil.getJsonUtil(this).examParseJson(examJson);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询失败,请稍候再试.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (list != null) {
            ExamAdapter examAdapter = new ExamAdapter(this, list);
            lvExam.setAdapter(examAdapter);
        }

    }

    private void initView() {
        tvQueryTitle = (TextView) findViewById(R.id.tv_title);
        tvClass = (TextView) findViewById(R.id.tv_class);
        lvExam = (ListView) findViewById(R.id.lv_exam);
    }

}
