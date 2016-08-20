package com.mcdull.cert.activity;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.ReExamAdapter;
import com.mcdull.cert.json.AbsJsonUtil;

public class ReExamActivity extends MyTitleActivity {

    private TextView tvQueryTitle;
    private TextView tvStudentName;
    private TextView tvStudentId;
    private TextView tvClass;
    private ListView lvReExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_re_exam);
        super.onCreate(savedInstanceState);

        initView();

        init();

    }

    private void init() {
        String reExamJson = getIntent().getStringExtra("reExamJson");
        Map<String, Object> map = null;
        try {
            map = AbsJsonUtil.getJsonUtil(this).reExamJsonParseJson(reExamJson);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询失败,请稍候再试.", Toast.LENGTH_SHORT).show();
            finish();
        }
        tvQueryTitle.setText("补考安排");
        if (map != null) {
            tvStudentId.setText("学号：" + map.get("studentId"));
            tvStudentName.setText("姓名：" + map.get("studentName"));
            tvClass.setText("班级：" + map.get("studentClass"));
        }
        List<List<String>> list = (List<List<String>>) map.get("list");
        if (list != null) {
            ReExamAdapter reExamAdapter = new ReExamAdapter(this, list);
            lvReExam.setAdapter(reExamAdapter);
        }
    }

    private void initView() {
        tvQueryTitle = (TextView) findViewById(R.id.tv_title);
        tvStudentName = (TextView) findViewById(R.id.tv_student_name);
        tvStudentId = (TextView) findViewById(R.id.tv_student_id);
        tvClass = (TextView) findViewById(R.id.tv_class);
        lvReExam = (ListView) findViewById(R.id.lv_re_exam);
    }

}
