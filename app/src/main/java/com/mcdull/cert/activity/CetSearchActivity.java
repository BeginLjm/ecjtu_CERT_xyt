package com.mcdull.cert.activity;

import java.util.HashMap;
import java.util.Map;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.json.AbsJsonUtil;
import com.mcdull.cert.utils.UpdateJson;

public class CetSearchActivity extends MyTitleActivity {

    private TextView tvQueryTitle;
    private TextView tvStudentName;
    private TextView tvSchool;
    private TextView tvType;
    private TextView tvZkzh;
    private TextView tvDate;
    private TextView tvTotal;
    private TextView tvListening;
    private TextView tvReading;
    private TextView tvWriting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cet_search);
        super.onCreate(savedInstanceState);

        initView();

        init();
    }

    private void init() {
        tvQueryTitle.setText("四六级查询");

        String cetSearchJson = getIntent().getStringExtra("cetSearchJson");


        Map<String, String> cetSearchMap = new HashMap<>();
        try {
            cetSearchMap = AbsJsonUtil.getJsonUtil(this).cetParseJson(cetSearchJson);
        } catch (Exception e) {
            Toast.makeText(this, "查询失败.请稍候再试.", Toast.LENGTH_SHORT).show();
            UpdateJson.beUpdate(this);
            finish();
        }

        if (cetSearchMap != null) {
            tvStudentName.setText("姓名：" + cetSearchMap.get("name"));
            tvSchool.setText("学校：" + cetSearchMap.get("school"));
            tvType.setText("考试类型：" + cetSearchMap.get("type"));
            tvZkzh.setText("准考证号：" + cetSearchMap.get("ticket"));
            tvDate.setText("考试时间：" + cetSearchMap.get("date"));
            tvTotal.setText("总分：" + cetSearchMap.get("total"));
            tvListening.setText("听力：" + cetSearchMap.get("listening"));
            tvReading.setText("阅读：" + cetSearchMap.get("reading"));
            tvWriting.setText("写作和翻译：" + cetSearchMap.get("writing"));
        }
    }


    private void initView() {
        tvQueryTitle = (TextView) findViewById(R.id.tv_title);
        tvStudentName = (TextView) findViewById(R.id.tv_student_name);
        tvSchool = (TextView) findViewById(R.id.tv_school);
        tvType = (TextView) findViewById(R.id.tv_typel);
        tvZkzh = (TextView) findViewById(R.id.tv_zkzh);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        tvListening = (TextView) findViewById(R.id.tv_listening);
        tvReading = (TextView) findViewById(R.id.tv_reading);
        tvWriting = (TextView) findViewById(R.id.tv_writing);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvQueryTitle = null;
        tvStudentName = null;
        tvSchool = null;
        tvType = null;
        tvZkzh = null;
        tvDate = null;
        tvTotal = null;
        tvListening = null;
        tvReading = null;
        tvWriting = null;
    }
}
