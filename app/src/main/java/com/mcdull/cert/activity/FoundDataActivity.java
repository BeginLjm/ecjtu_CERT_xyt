package com.mcdull.cert.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.View.ImagePage;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowSureDialog;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Deprecated
public class FoundDataActivity extends MyTitleActivity implements View.OnClickListener {

    //    private SmartImageView mIvImg;
    private TextView mTvFoundTitle;
    private SmartImageView mIvIcon;
    private TextView mTvNameDate;
    private TextView mTvMore;
    private String mTel;
    private ShowSureDialog sureDialog;
    private TextView mTvLocation;
    private Boolean isWait = true;
    private ShowWaitPopupWindow waitWin;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isWait = false;
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    if (code == 200) {
                        Map<String, String> map = parsingJson(jsonObject);
                        LinkedList<String> list = new LinkedList<>();
                        if (!TextUtils.isEmpty(map.get("publishPic1")) && map.get("publishPic1") != "")
                            list.add(map.get("publishPic1"));
                        if (!TextUtils.isEmpty(map.get("publishPic2")) && map.get("publishPic2") != "")
                            list.add(map.get("publishPic2"));
                        if (!TextUtils.isEmpty(map.get("publishPic3")) && map.get("publishPic3") != "")
                            list.add(map.get("publishPic3"));
                        if (!TextUtils.isEmpty(map.get("publishPic4")) && map.get("publishPic4") != "")
                            list.add(map.get("publishPic4"));
                        mImagePage.setImages(list);
                        showData(map);
                        mTel = map.get("publishManTel");
                    } else {
                        Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
    };
    private ImagePage mImagePage;

    private void showData(Map<String, String> map) {
//        mIvImg.setImageUrl(map.get("publishPic1"));
        mTvFoundTitle.setText(map.get("publishName"));
        mTvMore.setText(map.get("publishDepict"));
        mIvIcon.setImageUrl(map.get("publishManPhoto"));
        mTvLocation.setText(map.get("publishAddress"));
        mTvNameDate.setText(map.get("publishManName") + "\n" + map.get("publishGetTime"));
    }

    private Map<String, String> parsingJson(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject detail = jsonObject.getJSONObject("detail");
            map.put("publishId", detail.getString("publishId"));
            map.put("publishName", detail.getString("publishName"));
            map.put("publishManId", detail.getString("publishManId"));
            map.put("publishManTel", detail.getString("publishManTel"));
            map.put("publishAddress", detail.getString("publishAddress"));
            map.put("publishGetTime", detail.getString("publishGetTime"));
            map.put("publishDepict", detail.getString("publishDepict"));
            map.put("publishManName", detail.getString("publishManName"));
            map.put("publishManPhoto", detail.getString("publishManPhoto"));
            map.put("publishPic1", detail.getString("publishPic1"));
            map.put("publishPic2", detail.getString("publishPic2"));
            map.put("publishPic3", detail.getString("publishPic3"));
            map.put("publishPic4", detail.getString("publishPic4"));
        } catch (Exception e) {
        }
        return map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_found_data);
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.tv_title)).setText("失物详情");

        waitWin = new ShowWaitPopupWindow(getActivity());

        String id = getIntent().getStringExtra("id");

        init();

        isWait = true;
        Map<String, String> map = new HashMap<>();
        map.put("publishId", id);
        new InternetUtil(handler, InternetUtil.URL_LOST_GETINFO, map).get();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void init() {
//        mIvImg = (SmartImageView) findViewById(R.id.iv_img);
        mImagePage = (ImagePage) findViewById(R.id.iv_img);
        mTvFoundTitle = (TextView) findViewById(R.id.tv_found_name);
        mIvIcon = (SmartImageView) findViewById(R.id.iv_icon);
        mTvNameDate = (TextView) findViewById(R.id.tv_name_date);
        mTvMore = (TextView) findViewById(R.id.tv_more);
        mTvLocation = (TextView) findViewById(R.id.tv_location);
        findViewById(R.id.fab_call).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_call:
                if (!TextUtils.isEmpty(mTel)) {
                    callTel();
                } else {
                    //无电话号码
                }
                break;
        }
    }


    private void callTel() {
        sureDialog = new ShowSureDialog(this, "是否拨打电话", new ShowSureDialog.CallBack() {
            @Override
            public void CallBack() {
                sureDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mTel));
                startActivity(intent);
            }
        });
        sureDialog.showDialog();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isWait)
            waitWin.showWait();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin != null)
            waitWin.dismissWait();
        waitWin = null;
        if (sureDialog != null)
            sureDialog.dismiss();
        sureDialog = null;
    }

    public Activity getActivity() {
        return FoundDataActivity.this;
    }
}
