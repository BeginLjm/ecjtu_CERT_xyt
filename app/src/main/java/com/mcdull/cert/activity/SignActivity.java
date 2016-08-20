package com.mcdull.cert.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

public class SignActivity extends MyTitleActivity implements View.OnClickListener {

    private TextInputLayout mEtJwcPwd;
    private TextInputLayout mEtECardPwd;
    private TextInputLayout mEtName;
    private TextInputLayout mEtStudentId;
    private TextInputLayout mEtTel;
    private String mEMail;
    private String mPwd;
    private ShowWaitPopupWindow waitWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sign);
        super.onCreate(savedInstanceState);


        init();

    }

    private void init() {
        ((TextView) findViewById(R.id.tv_title)).setText("完善个人信息");

        this.mEtStudentId = (TextInputLayout) findViewById(R.id.et_student_id);
        this.mEtName = (TextInputLayout) findViewById(R.id.et_name);
        this.mEtECardPwd = (TextInputLayout) findViewById(R.id.et_eCard_pwd);
        this.mEtJwcPwd = (TextInputLayout) findViewById(R.id.et_jwc_pwd);
        this.mEtTel = (TextInputLayout) findViewById(R.id.et_tel);
        mEtTel.setHint("手机号");
        mEtName.setHint("姓名");
        mEtStudentId.setHint("学号");
        mEtECardPwd.setHint("一卡通密码");
        mEtJwcPwd.setHint("学分制教务管理系统密码（选填）");

        findViewById(R.id.bt_sure).setOnClickListener(this);
        findViewById(R.id.tv_no_id).setOnClickListener(this);

        mEMail = getIntent().getStringExtra("email");
        mPwd = getIntent().getStringExtra("pwd");

        waitWin = new ShowWaitPopupWindow(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                toSign();
                break;
            case R.id.tv_no_id:
                noId();
                break;
        }
    }

    private void noId() {
        String name = mEtName.getEditText().getText().toString();

        mEtName.setErrorEnabled(false);

        if (TextUtils.isEmpty(name)) {
            mEtName.setErrorEnabled(true);
            mEtName.setError("请输入姓名");
            return;
        }

        waitWin.showWait();

        AVUser user = new AVUser();
        user.setEmail(mEMail);
        user.setUsername(mEMail);
        user.setPassword(mPwd);
        user.put("Name", name);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (waitWin!=null)
                waitWin.dismissWait();
                if (e == null) {
                    Toast.makeText(SignActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void toSign() {
        String studentId = mEtStudentId.getEditText().getText().toString();
        String tel = mEtTel.getEditText().getText().toString();
        String name = mEtName.getEditText().getText().toString();
        String eCardPwd = mEtECardPwd.getEditText().getText().toString();
        String jwcPwd = mEtJwcPwd.getEditText().getText().toString();

        mEtECardPwd.setErrorEnabled(false);
        mEtName.setErrorEnabled(false);
        mEtStudentId.setErrorEnabled(false);
        mEtTel.setErrorEnabled(false);
        mEtJwcPwd.setErrorEnabled(false);

        if (TextUtils.isEmpty(name)) {
            mEtName.setErrorEnabled(true);
            mEtName.setError("请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(studentId)) {
            mEtStudentId.setErrorEnabled(true);
            mEtStudentId.setError("请输入学号");
            return;
        }
        if (TextUtils.isEmpty(tel)) {
            mEtTel.setErrorEnabled(true);
            mEtTel.setError("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(eCardPwd)) {
            mEtECardPwd.setErrorEnabled(true);
            mEtECardPwd.setError("请输入一卡通密码");
            return;
        }

        if (studentId.length() >= 14) {
            int year = Integer.parseInt(studentId.substring(0, 4));
            if (year > 2014) {
                if (studentId.length() == 16) {
                    if (TextUtils.isEmpty(jwcPwd)) {
                        mEtJwcPwd.setErrorEnabled(true);
                        mEtJwcPwd.setError("学分制学生需填该项");
                        return;
                    } else {
                        sign(name, studentId, tel, eCardPwd, jwcPwd);
                    }
                } else {
                    mEtStudentId.setErrorEnabled(true);
                    mEtStudentId.setError("学号格式有误");
                }
            } else {
                if (studentId.length() == 14) {
                    if (TextUtils.isEmpty(jwcPwd)) {
                        sign(name, studentId, tel, eCardPwd, "");
                    } else {
                        sign(name, studentId, tel, eCardPwd, jwcPwd);
                    }
                } else {
                    mEtStudentId.setErrorEnabled(true);
                    mEtStudentId.setError("学号格式有误");
                }
            }
        } else {
            mEtStudentId.setErrorEnabled(true);
            mEtStudentId.setError("学号格式有误");
        }

    }

    private void sign(String name, String studentId, String tel, String eCardPwd, String jwcPwd) {

        waitWin.showWait();

        AVUser user = new AVUser();
        user.setUsername(mEMail);
        user.setEmail(mEMail);
        user.setPassword(mPwd);
        user.setMobilePhoneNumber(tel);
        user.put("Name", name);
        user.put("StudentId", studentId);
        user.put("EcardPwd", eCardPwd);
        user.put("JwcPwd", jwcPwd);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (waitWin!=null)
                waitWin.dismissWait();
                if (e == null) {
                    Toast.makeText(SignActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void back() {
        Intent intent = new Intent(SignActivity.this, MainActivity.class);
        intent.putExtra("back", true);
        startActivity(intent);
        finish();
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
