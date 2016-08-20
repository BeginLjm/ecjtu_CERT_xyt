package com.mcdull.cert.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.View.MyDatePickerDialog;
import com.mcdull.cert.utils.GetIcon;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Deprecated
public class AddFoundActivity extends MyTitleActivity implements View.OnClickListener {

    private List<ImageView> views = new LinkedList<>();
    private List<LinearLayout> items = new LinkedList<>();
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private int selectImg = 0;
    private TextInputLayout mEtName;
    private Button mBtFoundTime;
    private int[] times = new int[5];
    private int type = 0;
    private TextInputLayout mEtMore;
    private TextInputLayout mEtLocation;
    private ShowWaitPopupWindow waitWin;
    private LinkedList<Bitmap> bitmaps = new LinkedList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                Log.d("AddFound", json);
                String code = "";
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    code = jsonObject.getString("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(code) && "200".equals(code)) {
                    Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_found);
        super.onCreate(savedInstanceState);

        waitWin = new ShowWaitPopupWindow(getActivity());

        init();

    }

    private void init() {
        ((TextView) findViewById(R.id.tv_title)).setText("发布招领");
        findViewById(R.id.bt_ok).setVisibility(View.VISIBLE);
        findViewById(R.id.bt_ok).setOnClickListener(this);

        views.add((ImageView) findViewById(R.id.view1));
        views.add((ImageView) findViewById(R.id.view2));
        views.add((ImageView) findViewById(R.id.view3));
        views.add((ImageView) findViewById(R.id.view4));

        items.add((LinearLayout) findViewById(R.id.ll_commodity));
        items.add((LinearLayout) findViewById(R.id.ll_book));
        items.add((LinearLayout) findViewById(R.id.ll_bag));
        items.add((LinearLayout) findViewById(R.id.ll_wear));
        items.add((LinearLayout) findViewById(R.id.ll_avionics));
        items.add((LinearLayout) findViewById(R.id.ll_sport));
        items.add((LinearLayout) findViewById(R.id.ll_card));
        items.add((LinearLayout) findViewById(R.id.ll_key));
        items.add((LinearLayout) findViewById(R.id.ll_other));

        for (View v : items) {
            v.setOnClickListener(new ItemOnclick());
        }

        for (View v : views) {
            v.setOnClickListener(this);
            v.setVisibility(View.GONE);
        }

        views.get(0).setVisibility(View.VISIBLE);

        mEtName = (TextInputLayout) findViewById(R.id.et_found_name);
        mEtName.setHint("物品名称");

        mEtLocation = (TextInputLayout) findViewById(R.id.et_found_location);
        mEtLocation.setHint("捡到的地点");

        mBtFoundTime = (Button) findViewById(R.id.bt_found_time);

        mBtFoundTime.setOnClickListener(this);

        mEtMore = (TextInputLayout) findViewById(R.id.et_more);
        mEtMore.setHint("详细信息");

    }

    public Activity getActivity() {
        return AddFoundActivity.this;
    }

    class ItemOnclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            for (int i = 0; i < items.size(); i++) {
                if (Build.VERSION.SDK_INT > 15) {
                    items.get(i).setBackground(null);
                } else {
                    items.get(i).setBackgroundColor(0xffe0e0e0);
                }
                if (v.getId() == items.get(i).getId()) {
                    items.get(i).setBackgroundColor(0xffc5c5c5);
                    type = i + 1;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view1:
                selectIcon();
                selectImg = 0;
                break;
            case R.id.view2:
                selectIcon();
                selectImg = 1;
                break;
            case R.id.view3:
                selectIcon();
                selectImg = 2;
                break;
            case R.id.view4:
                selectIcon();
                selectImg = 3;
                break;
            case R.id.bt_ok:
                addFound();
                break;
            case R.id.bt_found_time:
                selectDate();
                break;
        }
    }

    private void addFound() {
        mEtName.setErrorEnabled(false);
        mEtLocation.setErrorEnabled(false);
        mEtMore.setErrorEnabled(false);
        String name = mEtName.getEditText().getText().toString();
        String location = mEtLocation.getEditText().getText().toString();
        String time = mBtFoundTime.getText().toString();
        String more = mEtMore.getEditText().getText().toString();

        if (views.get(1).getVisibility() == View.GONE) {
            Toast.makeText(AddFoundActivity.this, "请上传至少一张图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            mEtName.setError("请填写物品名称");
            mEtName.setErrorEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(location)) {
            mEtLocation.setError("请填捡到物品的地点");
            mEtLocation.setErrorEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(more)) {
            mEtMore.setError("请填写详细信息");
            mEtMore.setErrorEnabled(true);
            return;
        }
        if ("全部".equals(time)) {
            Toast.makeText(AddFoundActivity.this, "请选择捡到时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (type == 0) {
            Toast.makeText(AddFoundActivity.this, "请选选择物品类型", Toast.LENGTH_SHORT).show();
            return;
        }

        final List<Bitmap> bm = bitmaps;

        Map<String, String> map = new ArrayMap<>();
        map.put("lostName", name);
        map.put("getManName", AVUser.getCurrentUser().getString("Name"));
        map.put("getManId", AVUser.getCurrentUser().getString("StudentId"));
        map.put("getManTel", AVUser.getCurrentUser().getMobilePhoneNumber());
        map.put("getWhere", location);
        map.put("describe", more);
        map.put("getTime", times[0] + "-" + times[1] + "-" + times[2]);
        map.put("lostType", (type - 1) + "");
        final Map<String, String> m = map;

        waitWin.showWait();

        new GetIcon(getActivity(), new GetIcon.GetIconCallBack() {
            @Override
            public void done(Bitmap bitmap) {
                new InternetUtil(handler, InternetUtil.URL_LOST, m, bm, bitmap).post();
            }
        });


    }

    private void selectDate() {

        final int[] time = Util.getNowTimes();

        final MyDatePickerDialog myDatePickerDialog = new MyDatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                times[0] = year;
                times[1] = monthOfYear + 1;
                times[2] = dayOfMonth;
                selectTime();
            }
        }, time[0], time[1], time[2]);

        myDatePickerDialog.getDatePicker().setMaxDate(Util.getNowTime() + 86300000);
        myDatePickerDialog.getDatePicker().init(time[0], time[1], time[2], null);

        myDatePickerDialog.show();
    }

    private void selectTime() {

        final int[] time = Util.getNowTimes();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                times[3] = hourOfDay;
                times[4] = minute;
                mBtFoundTime.setText(times[0] + "-" + times[1] + "-" + times[2] + " " + times[3] + ":" + times[4]);
            }
        }, time[3], time[4], true);

        timePickerDialog.show();

    }


    private void selectIcon() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    startPhotoZoom(data.getData(), 300);
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
            Bitmap bmp = bundle.getParcelable("data");
            bitmaps.add(selectImg, bmp);
            Matrix matrix = new Matrix();
            matrix.postScale(0.2f, 0.2f);
            views.get(selectImg).setImageBitmap(bmp);
            if (selectImg != 3) {
                if (views.get(selectImg + 1).getVisibility() == View.GONE) {
                    views.get(selectImg + 1).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ImageView v : views) {
            v.setDrawingCacheEnabled(true);
            if (v.getDrawingCache() != null) {
                v.getDrawingCache().recycle();
            }
            v.setDrawingCacheEnabled(false);
        }
        mEtName = null;
        mBtFoundTime = null;
        mEtMore = null;
        mEtLocation = null;
        if (waitWin != null)
            waitWin.dismissWait();
        waitWin = null;
        for (Bitmap b : bitmaps) {
            b.recycle();
        }
    }
}
