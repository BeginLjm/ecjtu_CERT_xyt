package com.mcdull.cert.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.utils.GetIcon;
import com.mcdull.cert.utils.ShowEditDialog;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

import java.io.FileOutputStream;
import java.io.IOException;

public class MyDataActivity extends MyTitleActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private TextInputLayout mEtJwcPwd;
    private TextInputLayout mEtStudentId;
    private TextInputLayout mEtEcardPwd;
    private CheckBox mCbMan;
    private CheckBox mCbWoman;
    private TextView mTvName;
    private ImageView mIvIcon;
    private int MAN = 1;
    private int WOMAN = -1;
    private Bitmap bmp;
    private ShowWaitPopupWindow waitWin;
    private TextInputLayout mEtTel;
    private ShowEditDialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_data);
        super.onCreate(savedInstanceState);

        waitWin = new ShowWaitPopupWindow(this);

        initView();


    }

    private void initView() {

        ((TextView) findViewById(R.id.tv_title)).setText("个人信息");
        findViewById(R.id.bt_save).setOnClickListener(this);
        findViewById(R.id.bt_name).setOnClickListener(this);
        this.mIvIcon = (ImageView) findViewById(R.id.iv_icon);
        this.mTvName = (TextView) findViewById(R.id.tv_name);
        this.mCbMan = (CheckBox) findViewById(R.id.cb_man);
        this.mCbWoman = (CheckBox) findViewById(R.id.cb_woman);

        this.mEtStudentId = (TextInputLayout) findViewById(R.id.et_student_id);
        this.mEtJwcPwd = (TextInputLayout) findViewById(R.id.et_jwc_pwd);
//        this.mEtEcardId = (TextInputLayout) findViewById(R.id.et_ecard_id);
        this.mEtEcardPwd = (TextInputLayout) findViewById(R.id.et_ecard_pwd);
        this.mEtTel = (TextInputLayout) findViewById(R.id.et_tel);
        mEtStudentId.setHint("学号");
        mEtJwcPwd.setHint("学分制教务系统密码");
//        mEtEcardId.setHint("一卡通卡号");
        mEtEcardPwd.setHint("一卡通密码");
        mEtStudentId.setErrorEnabled(false);
        mEtTel.setHint("手机号");

        mIvIcon.setOnClickListener(this);
        mCbMan.setOnCheckedChangeListener(this);
        mCbWoman.setOnCheckedChangeListener(this);

        String name = AVUser.getCurrentUser().getString("Name");
        mTvName.setText(name);

        new GetIcon(MyDataActivity.this, new GetIcon.GetIconCallBack() {
            @Override
            public void done(Bitmap bitmap) {
                if (bitmap != null) {
                    MyDataActivity.this.bmp = bitmap;
                    mIvIcon.setImageBitmap(bmp);
                }
            }
        });

        int sex = AVUser.getCurrentUser().getInt("Sex");
        if (sex == WOMAN) {
            mCbMan.setChecked(false);
            mCbWoman.setChecked(true);
        } else if (sex == MAN) {
            mCbMan.setChecked(true);
            mCbWoman.setChecked(false);
        }

        String studentId = AVUser.getCurrentUser().getString("StudentId");
        if (!TextUtils.isEmpty(studentId)) {
            mEtStudentId.getEditText().setText(studentId);
        }

        String tel = AVUser.getCurrentUser().getMobilePhoneNumber();
        if (!TextUtils.isEmpty(tel)) {
            mEtTel.getEditText().setText(tel);
        }

        String jwcPwd = AVUser.getCurrentUser().getString("JwcPwd");
        if (!TextUtils.isEmpty(jwcPwd)) {
            mEtJwcPwd.getEditText().setText(jwcPwd);
        }

//        String eCardId = AVUser.getCurrentUser().getString("EcardId");
//        if (!TextUtils.isEmpty(eCardId)) {
//            mEtEcardId.getEditText().setText(eCardId);
//        }

        String eCardPwd = AVUser.getCurrentUser().getString("EcardPwd");
        if (!TextUtils.isEmpty(eCardPwd)) {
            mEtEcardPwd.getEditText().setText(eCardPwd);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                mEtStudentId.setErrorEnabled(false);
                save();
                break;
            case R.id.iv_icon:
                selectIcon();
                break;
            case R.id.bt_name:
                showEditName();
                break;
        }
    }

    private void showEditName() {
        editDialog = new ShowEditDialog(this, "请输入姓名", new ShowEditDialog.CallBack() {
            @Override
            public void CallBack(EditText editText) {
                String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                mTvName.setText(name);
                editDialog.dismiss();
            }
        });
    }

    private void selectIcon() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    startPhotoZoom(data.getData(), 150);
                }
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle == null) {
            Toast.makeText(this, "Sorry，部分三星手机暂不支持自定义头像，后期我们将会解决这个问题。", Toast.LENGTH_SHORT).show();
        } else {
            bmp = bundle.getParcelable("data");
            mIvIcon.setImageBitmap(bmp);
        }
    }

    private void save() {
        String name = mTvName.getText().toString();
        int sex = 0;
        if (mCbMan.isChecked()) {
            sex = MAN;
        }
        if (mCbWoman.isChecked()) {
            sex = WOMAN;
        }
        String studentId = null;
        if (!TextUtils.isEmpty(mEtStudentId.getEditText().getText().toString())) {
            studentId = mEtStudentId.getEditText().getText().toString();
            if (studentId.length() != 14 && studentId.length() != 16) {
                mEtStudentId.setError("学号格式有误");
                mEtStudentId.setErrorEnabled(true);
//                Toast.makeText(getApplicationContext(), "学号格式有误", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String jwcPwd = null;
        if (!TextUtils.isEmpty(mEtJwcPwd.getEditText().getText().toString())) {
            jwcPwd = mEtJwcPwd.getEditText().getText().toString();
        }
//        String eCardId = null;
//        if (!TextUtils.isEmpty(mEtEcardId.getEditText().getText().toString())) {
//            eCardId = mEtEcardId.getEditText().getText().toString();
//        }
        String eCardPwd = null;
        if (!TextUtils.isEmpty(mEtEcardPwd.getEditText().getText().toString())) {
            eCardPwd = mEtEcardPwd.getEditText().getText().toString();
        }
        String tel = null;
        if (!TextUtils.isEmpty(mEtTel.getEditText().getText().toString())) {
            tel = mEtTel.getEditText().getText().toString();
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sex == 0) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }

        mEtStudentId.setErrorEnabled(false);
//        mEtEcardId.setErrorEnabled(false);
        mEtEcardPwd.setErrorEnabled(false);
        mEtTel.setErrorEnabled(false);

        if (TextUtils.isEmpty(studentId)) {
            mEtStudentId.setErrorEnabled(true);
            mEtStudentId.setError("请填写学号");
            return;
        }
//        if (TextUtils.isEmpty(eCardId)) {
//            mEtEcardId.setErrorEnabled(true);
//            mEtEcardId.setError("请填写一卡通卡号");
//            return;
//        }
        if (TextUtils.isEmpty(eCardPwd)) {
            mEtEcardPwd.setErrorEnabled(true);
            mEtEcardPwd.setError("请填写一卡通密码");
            return;
        }
        if (TextUtils.isEmpty(tel)) {
            mEtTel.setErrorEnabled(true);
            mEtTel.setError("请填写手机号");
            return;
        }

//        AVUser.requestLoginSmsCodeInBackground(tel, new RequestMobileCodeCallback() {
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    showEditCode();
//                } else {
//                    Toast.makeText(MyDataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        waitWin.showWait();
        AVUser user = AVUser.getCurrentUser();
        user.setMobilePhoneNumber(tel);
        user.put("Name", name);
        user.put("Sex", sex);
        user.put("StudentId", studentId);
        user.put("JwcPwd", jwcPwd);
//        user.put("EcardId", eCardId);
        user.put("EcardPwd", eCardPwd);
        if (bmp != null) {

            AVFile icon = user.getAVFile("Icon");
            if (icon != null) {
                icon.deleteInBackground();
            }

            AVFile file = new AVFile(AVUser.getCurrentUser().getUsername(), Util.Bitmap2Bytes(bmp));
            user.put("Icon", file);

            try {
                FileOutputStream fos = openFileOutput("Icon.png", MODE_PRIVATE);
                fos.write(Util.Bitmap2Bytes(bmp));
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            SharedPreferences SP = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor edit = SP.edit();
            edit.putBoolean("Icon", true);
            edit.apply();
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (waitWin!=null)
                waitWin.dismissWait();
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "保存失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void showEditCode() {
//        editDialog = new ShowEditDialog(this, "请输入验证码", new ShowEditDialog.CallBack() {
//            @Override
//            public void CallBack(EditText editText) {
//                AVUser.verifyMobilePhoneInBackground(editText.getText().toString(), new AVMobilePhoneVerifyCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (e == null) {
//
//                        } else {
//                            Toast.makeText(MyDataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_man:
                Log.d("Check", isChecked + "男");
                if (isChecked) {
                    mCbWoman.setChecked(false);
                }
                break;
            case R.id.cb_woman:
                Log.d("Check", isChecked + "女");
                if (isChecked) {
                    mCbMan.setChecked(false);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (editDialog != null)
            editDialog.dismiss();
        editDialog = null;
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
    }
}
