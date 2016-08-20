package com.mcdull.cert.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.ECardAdapter;
import com.mcdull.cert.json.AbsJsonUtil;

public class ECardActivity extends MyTitleActivity {

    private TextView tvStudentName;
    private TextView tvStudentId;
    private TextView tvECardId;
    private TextView tvBalance;
    private TextView tvTotal;
    private ListView lvECard;
    private TextView tvQueryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ecard);
        super.onCreate(savedInstanceState);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
        initView();

        init();
    }

    private void init() {

        String eCardJson = getIntent().getStringExtra("eCardJson");

        tvQueryTitle.setText("一卡通查询");
        Map<String, Object> map = new HashMap<>();
        try {
            map = AbsJsonUtil.getJsonUtil(this).eCardParseJson(eCardJson);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询失败,请稍候再试.", Toast.LENGTH_SHORT).show();
            finish();
        }

        List<Map<String, String>> list = (List<Map<String, String>>) map.get("list");
        if (map != null) {
            tvStudentName.setText("姓名：" + map.get("name"));
            tvStudentId.setText("学号：" + AVUser.getCurrentUser().getString("StudentId"));
            tvECardId.setText("卡号：" + map.get("ecardid"));
            tvBalance.setText("余额：" + map.get("balance"));
            tvTotal.setText("今日" + map.get("total"));
        }
        if (list != null) {
            ECardAdapter ECardAdapter = new ECardAdapter(this, list);
            lvECard.setAdapter(ECardAdapter);
        }
    }

    private void initView() {
        tvStudentName = (TextView) findViewById(R.id.tv_student_name);
        tvStudentId = (TextView) findViewById(R.id.tv_student_id);
        tvECardId = (TextView) findViewById(R.id.tv_ecard_id);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        lvECard = (ListView) findViewById(R.id.lv_ecard);
        tvQueryTitle = (TextView) findViewById(R.id.tv_title);
    }

}
