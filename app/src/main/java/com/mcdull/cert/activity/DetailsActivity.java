package com.mcdull.cert.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.mcdull.cert.R;
import com.mcdull.cert.json.AbsJsonUtil;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

import java.util.Map;

public class DetailsActivity extends Activity implements View.OnClickListener {

    private TextView mTextView;
    private SmartImageView mImgView;
    private ShowWaitPopupWindow waitWin;
    private boolean isGetImg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);
        //判断SDK版本，设置沉浸状态栏
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            findViewById(R.id.status_bar).setVisibility(View.VISIBLE);
        }
        waitWin = new ShowWaitPopupWindow(DetailsActivity.this);

        String type = getIntent().getStringExtra("type");
        int id = getIntent().getIntExtra("id", 0);

        initView(type);

        getJson(id);

    }

    private void getJson(int id) {
        Map<String, String> map = new ArrayMap<>();
        map.put("sid", id + "");//设置get参数
        new InternetUtil(handler, InternetUtil.URL_MAP, map).get();//传入参数
    }

    private void initView(String type) {
        ((TextView) findViewById(R.id.tv_title)).setText(type);
        findViewById(R.id.bt_back).setOnClickListener(this);
        mImgView = (SmartImageView) findViewById(R.id.img);
        mTextView = (TextView) findViewById(R.id.text);
    }

    //获取到的结果在这里得到
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isGetImg = false;
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 1) {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                try {
                    Map<String, String> map = AbsJsonUtil.getJsonUtil(DetailsActivity.this).detailsParseJson(json);
                    String description = map.get("description");
                    String pictures = map.get("pictures");
                    DetailsActivity.this.mTextView.setText(description);
                    if (pictures == null || pictures.equals("NULL") || pictures.equals("null")) {
                        mImgView.setImageResource(R.drawable.notjpg);
                    } else {
                        mImgView.setImageUrl(InternetUtil.URL_IMG + pictures);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(DetailsActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                mImgView.setImageResource(R.drawable.notjpg);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                finish();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isGetImg) {
            waitWin.showWait();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImgView != null) {
            mImgView.setDrawingCacheEnabled(true);
            if (mImgView.getDrawingCache() != null) {
                mImgView.getDrawingCache().recycle();
            }
            mImgView.setDrawingCacheEnabled(false);
            mImgView = null;
        }
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
    }
}
