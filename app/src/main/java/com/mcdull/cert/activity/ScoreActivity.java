package com.mcdull.cert.activity;


import java.util.ArrayList;
import java.util.List;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.ScoreAdapter;
import com.mcdull.cert.json.AbsJsonUtil;
import com.mcdull.cert.utils.UpdateJson;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends MyTitleActivity {

    private ListView lvScore;
    private String scoreJson;
    private TextView tvStudentName;
    private TextView tvStudentId;
    private TextView tvTremScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_score);
        super.onCreate(savedInstanceState);

        initView();

        this.scoreJson = getIntent().getStringExtra("scoreJson");

        ((TextView) findViewById(R.id.tv_title)).setText("成绩查询");

        tvStudentId.setVisibility(View.GONE);
        tvStudentName.setVisibility(View.GONE);
        tvTremScore.setVisibility(View.GONE);
        List<List<String>> lists = new ArrayList<>();
        try {
            lists = AbsJsonUtil.getJsonUtil(this).scoreParseJson(scoreJson);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询失败.请稍候再试.", Toast.LENGTH_SHORT).show();
            UpdateJson.beUpdate(this);
            finish();
        }
        if (lists != null) {
            ScoreAdapter scoreAdapter = new ScoreAdapter(this, lists);
            lvScore.setAdapter(scoreAdapter);
        }
    }

    private void initView() {
        lvScore = (ListView) findViewById(R.id.lv_score);
        tvStudentName = (TextView) findViewById(R.id.tv_student_name);
        tvStudentId = (TextView) findViewById(R.id.tv_student_id);
        tvTremScore = (TextView) findViewById(R.id.tv_trem_score);
    }

}
