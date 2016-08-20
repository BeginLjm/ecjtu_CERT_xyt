package com.mcdull.cert.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.utils.CourseUtilForSql;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

import java.util.Map;

public class NewStudentCourse extends MyTitleActivity implements View.OnClickListener {

    private Button mBtSure;
    private TextInputLayout mEtId;
    private TextInputLayout mEtPass;
    private ShowWaitPopupWindow waitWin;

    Handler courseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(NewStudentCourse.this, "查询失败，请检查网络链接", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String data = bundle.getString("Json");
                if (Util.replace(data).equals("error")) {
                    Toast.makeText(NewStudentCourse.this, "查询失败，请检查密码是否错误", Toast.LENGTH_SHORT).show();
                } else {
                    CourseUtilForSql.saveCourse(data, NewStudentCourse.this);

                    SharedPreferences.Editor edit = NewStudentCourse.this.getSharedPreferences("config", MODE_PRIVATE).edit();
                    int[] time = Util.getSystemTime();
                    edit.putString("time", time[0] + "." + time[1]);
                    edit.putBoolean("isCourse", true);
                    edit.apply();

                    Toast.makeText(NewStudentCourse.this, "导入成功", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_student_course);
        super.onCreate(savedInstanceState);

        initView();

        waitWin = new ShowWaitPopupWindow(this);
    }

    private void initView() {

        ((TextView) findViewById(R.id.tv_title)).setText("导入课表");
        mBtSure = (Button) findViewById(R.id.bt_sure);
        mBtSure.setOnClickListener(this);
        mEtId = (TextInputLayout) findViewById(R.id.et_student_id);
        mEtPass = (TextInputLayout) findViewById(R.id.et_password);
        mEtId.setHint("学号");
        mEtPass.setHint("教务系统密码");

        mEtId.getEditText().setText(AVUser.getCurrentUser().getString("StudentId"));
        mEtPass.getEditText().setText(AVUser.getCurrentUser().getString("JwcPwd"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                getCourse();
                break;
        }
    }

    private void getCourse() {
        String studentId = mEtId.getEditText().getText().toString();
        String password = mEtPass.getEditText().getText().toString();
        mEtId.setErrorEnabled(false);
        mEtPass.setErrorEnabled(false);
        if (TextUtils.isEmpty(studentId)) {
            mEtId.setError("请填写学号");
            mEtId.setErrorEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mEtPass.setError("请填写密码");
            mEtPass.setErrorEnabled(true);
            return;
        }
        waitWin.showWait();

        String jwcPwd = AVUser.getCurrentUser().getString("JwcPwd");
        if (TextUtils.isEmpty(jwcPwd)) {
            AVUser.getCurrentUser().put("JwcPwd", password);
            AVUser.getCurrentUser().saveInBackground();
        }

        Map<String, String> map = new ArrayMap<>();
        map.put("UserName", studentId);//设置get参数
        map.put("Password", password);//设置get参数
        new InternetUtil(courseHandler, InternetUtil.URL_COURSE, map).get();//传入参数
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
