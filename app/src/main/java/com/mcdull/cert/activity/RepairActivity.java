package com.mcdull.cert.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.MySpinnerAdapter;
import com.mcdull.cert.json.AbsJsonUtil;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RepairActivity extends MyTitleActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String orderTimeJson;
    private TextView mTvType0;
    private TextView mTvType1;
    private TextView mTvType2;
    private TextView mTvType3;
    private TextView mTvType4;
    private TextView mTvType5;
    private TextInputLayout mEtModel;
    private TextInputLayout mEtDiscribe;
    private TextInputLayout mEtTel;
    private Spinner mSnTime;
    private String ftype = "0";
    private String lid = "0";
    private LinkedHashMap<String, String> orderTimeMap;
    private ShowWaitPopupWindow waitWin;
    private List<NameValuePair> pairList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_repair);
        super.onCreate(savedInstanceState);

        waitWin = new ShowWaitPopupWindow(this);

        init();

        initView();

    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("电脑报修");

        mTvType0 = (TextView) findViewById(R.id.type_0);
        mTvType1 = (TextView) findViewById(R.id.type_1);
        mTvType2 = (TextView) findViewById(R.id.type_2);
        mTvType3 = (TextView) findViewById(R.id.type_3);
        mTvType4 = (TextView) findViewById(R.id.type_4);
        mTvType5 = (TextView) findViewById(R.id.type_5);

        mEtModel = (TextInputLayout) findViewById(R.id.et_model);
        mEtDiscribe = (TextInputLayout) findViewById(R.id.et_discribe);
        mEtTel = (TextInputLayout) findViewById(R.id.et_tel);
        mSnTime = (Spinner) findViewById(R.id.sn_time);
        mEtModel.setHint("电脑品牌及型号");
        mEtDiscribe.setHint("故障详情");
        mEtTel.setHint("手机号码");

        findViewById(R.id.bt_sure).setOnClickListener(this);

        mTvType0.setOnClickListener(new TypeClick());
        mTvType1.setOnClickListener(new TypeClick());
        mTvType2.setOnClickListener(new TypeClick());
        mTvType3.setOnClickListener(new TypeClick());
        mTvType4.setOnClickListener(new TypeClick());
        mTvType5.setOnClickListener(new TypeClick());

        LinkedList<String> list = new LinkedList<>();
        for (String s : orderTimeMap.keySet()) {
            list.add(s);
        }
        mSnTime.setAdapter(new MySpinnerAdapter(this, list));
        mSnTime.setOnItemSelectedListener(this);
    }

    private void init() {
        orderTimeJson = getIntent().getStringExtra("TimeJson");
        orderTimeMap = new LinkedHashMap<>();
        orderTimeMap.put("请选择预约时间", "0");
        try {
            List<Map<String, String>> list = AbsJsonUtil.getJsonUtil(this).repairParseJson(orderTimeJson);
            for (Map<String, String> m : list)
                orderTimeMap.put(m.get("data"), m.get("num"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询失败,请稍候再试.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                repair();
                break;
        }
    }

    private void repair() {
        if (!InternetUtil.isConnected(this)) {
            Toast.makeText(this, "请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }
        String act = "submitOrder";
        String student_id = AVUser.getCurrentUser().getString("StudentId");
        String email = AVUser.getCurrentUser().getEmail();
        String discribe = mEtDiscribe.getEditText().getText().toString();
        String model = mEtModel.getEditText().getText().toString();
        String tel = mEtTel.getEditText().getText().toString();

        mEtTel.setErrorEnabled(false);
        mEtModel.setErrorEnabled(false);
        mEtDiscribe.setErrorEnabled(false);
        boolean isError = false;

        if (TextUtils.isEmpty(discribe)) {
            isError = true;
            mEtDiscribe.setError("请填写故障详情");
            mEtDiscribe.setErrorEnabled(true);
        }
        if (TextUtils.isEmpty(model)) {
            isError = true;
            mEtDiscribe.setError("请填写设备型号");
            mEtDiscribe.setErrorEnabled(true);
        }
        if (TextUtils.isEmpty(tel)) {
            isError = true;
            mEtModel.setError("请填写手机号码");
            mEtModel.setErrorEnabled(true);
        }
        if (isError)
            return;

        if (lid.equals("0")) {
            Toast.makeText(this, "请选择预约时间", Toast.LENGTH_SHORT).show();
            return;
        }

        waitWin.showWait();

        Map<String, String> map = new HashMap<>();
        map.put("act", act);
        map.put("student_id", student_id);
        map.put("ftype", ftype);
        map.put("discribe", discribe);
        map.put("email", email);
        map.put("lid", lid);
        map.put("model", model);
        map.put("tel", tel);

        new InternetUtil(repairHandler, InternetUtil.URL_REPAIR, map).post();

    }

    Handler repairHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(RepairActivity.this, "链接网络失败，请稍后再试", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                if (Util.replace(json).equals("\"succeed\"")) {
                    for (String s : orderTimeMap.keySet()) {
                        if (orderTimeMap.get(s).equals(lid)) {
                            Intent intent = new Intent(RepairActivity.this, RepairSucActivity.class);
                            intent.putExtra("time", s);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                    Toast.makeText(RepairActivity.this, "预约成功,请在预约时间内到教7栋-105进行维修", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Util.replace(json).equals("\"1\"")) {
                    Toast.makeText(RepairActivity.this, "该时间预约人数已满", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Util.replace(json).equals("\"2\"")) {
                    Toast.makeText(RepairActivity.this, "您已提交预约,请在预约时间内到教7栋-105进行维修", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Util.replace(json).equals("\"3\"")) {
                    Toast.makeText(RepairActivity.this, "请先在个人信息页检查学号是否填写错误", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        lid = orderTimeMap.get(((TextView) view.findViewById(R.id.tv_select_item)).getText().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class TypeClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mTvType0.setBackgroundColor(0xffaaccdd);
            mTvType1.setBackgroundColor(0xffaaccdd);
            mTvType2.setBackgroundColor(0xffaaccdd);
            mTvType3.setBackgroundColor(0xffaaccdd);
            mTvType4.setBackgroundColor(0xffaaccdd);
            mTvType5.setBackgroundColor(0xffaaccdd);
            switch (v.getId()) {
                case R.id.type_0:
                    mTvType0.setBackgroundColor(0xff29b6f6);
                    ftype = "0";
                    break;
                case R.id.type_1:
                    mTvType1.setBackgroundColor(0xff29b6f6);
                    ftype = "1";
                    break;
                case R.id.type_2:
                    mTvType2.setBackgroundColor(0xff29b6f6);
                    ftype = "2";
                    break;
                case R.id.type_3:
                    mTvType3.setBackgroundColor(0xff29b6f6);
                    ftype = "3";
                    break;
                case R.id.type_4:
                    mTvType4.setBackgroundColor(0xff29b6f6);
                    ftype = "4";
                    break;
                case R.id.type_5:
                    mTvType5.setBackgroundColor(0xff29b6f6);
                    ftype = "5";
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
    }
}
