package com.mcdull.cert.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.anim.ShakeAnim;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

import java.util.Map;

public class EnrollActivity extends MyTitleActivity implements View.OnClickListener {

    private TextInputLayout mEtZKZ;
    private TextInputLayout mEtName;
    private TextInputLayout mEtSFZ;
    private Button mBtSure;
    private ShowWaitPopupWindow waitWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_enroll);
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText("录取查询");
        mBtSure = (Button) findViewById(R.id.bt_sure);
        mBtSure.setOnClickListener(this);
        mEtZKZ = (TextInputLayout) findViewById(R.id.et_zkz);
        mEtName = (TextInputLayout) findViewById(R.id.et_name);
        mEtSFZ = (TextInputLayout) findViewById(R.id.et_sfz);
        mEtZKZ.setHint("准考证号");
        mEtSFZ.setHint("身份证号");
        mEtName.setHint("姓名");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                getJson();
                break;
        }
    }

    public void getJson() {
        String zkz = mEtZKZ.getEditText().getText().toString();
        String name = mEtName.getEditText().getText().toString();
        final String sfz = mEtSFZ.getEditText().getText().toString();
        mEtName.setErrorEnabled(false);
        mEtSFZ.setErrorEnabled(false);
        mEtZKZ.setErrorEnabled(false);
        boolean isError = false;
        if (TextUtils.isEmpty(zkz)) {
            isError = true;
            mEtZKZ.setError("请输入准考证号");
            mEtZKZ.setErrorEnabled(true);
        }
        if (TextUtils.isEmpty(name)) {
            isError = true;
            mEtName.setError("请输入姓名");
            mEtName.setErrorEnabled(true);
        }
        if (TextUtils.isEmpty(sfz)) {
            isError = true;
            mEtSFZ.setError("请输入身份证号");
            mEtSFZ.setErrorEnabled(true);
        }
        if (isError)
            return;

        if (zkz.length() != 14) {
            isError = true;
            mEtZKZ.setError("请检查准考证号是否正确");
            mEtZKZ.setErrorEnabled(true);
        }
        if (sfz.length() != 18) {
            isError = true;
            mEtSFZ.setError("请检查身份证号是否正确");
            mEtSFZ.setErrorEnabled(true);
        }

        if (isError)
            return;


        waitWin = new ShowWaitPopupWindow(this);
        waitWin.showWait();

        Map<String, String> map = new ArrayMap<>();
        map.put("idcard", sfz);//设置get参数
        new InternetUtil(handler, InternetUtil.URL_ENROLL, map).get();//传入参数
    }

    //获取到的结果在这里得到
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 1) {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                if (json != null) {
                    json = json.replaceAll("\n", "");
                    if (TextUtils.isEmpty(json) || json.equals("null") || json.equals("NULL")) {
                        Toast.makeText(EnrollActivity.this, "暂未公布你的录取信息，请耐心等候。", Toast.LENGTH_SHORT).show();
                        ShakeAnim skAnim = new ShakeAnim();
                        skAnim.setDuration(1000);
                        mBtSure.startAnimation(skAnim);
                        return;
                    }
                    Intent intent = new Intent(EnrollActivity.this, ShowEnrollActivity.class);
                    intent.putExtra("json", json);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(EnrollActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                ShakeAnim skAnim = new ShakeAnim();
                skAnim.setDuration(1000);
                mBtSure.startAnimation(skAnim);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
    }
}
