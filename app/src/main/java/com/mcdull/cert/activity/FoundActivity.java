package com.mcdull.cert.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.FoundAdapter;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class FoundActivity extends MyTitleActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView mLvFound;
    private Intent intent = new Intent();
    private Boolean isWait = true;
    private ShowWaitPopupWindow waitWin;
    private FoundAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private static String[] types = {"日用品", "图书", "包类", "穿戴设备", "电子设备", "运动类", "证件", "钥匙", "其他", "最新"};
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            swipeLayout.setRefreshing(false);
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
                    if (jsonObject.getInt("code") == 200) {
                        Map<String, List<Map<String, String>>> map = parsingJson(jsonObject);
                        adapter.setMap(map);
                    } else {
                        Toast.makeText(getActivity(), "code错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    };

    private Map<String, List<Map<String, String>>> parsingJson(JSONObject jsonObject) throws JSONException {
        Map<String, List<Map<String, String>>> founds = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            try {
                List<Map<String, String>> list = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray(i + "");
                for (int j = 0; j < 3; j++) {
                    try {
                        Map<String, String> map = new HashMap<>();
                        JSONObject object = jsonArray.getJSONObject(j);
                        map.put("publishId", object.getString("publishId"));
                        map.put("publishName", object.getString("publishName"));
                        map.put("publishGetTime", object.getString("publishGetTime"));
                        map.put("publishPic1", object.getString("publishPic1"));
                        list.add(map);
                    } catch (Exception e) {
                    }
                }
                founds.put(types[i], list);
            } catch (Exception e) {
            }
        }
        return founds;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_found);
        super.onCreate(savedInstanceState);

        waitWin = new ShowWaitPopupWindow(getActivity());

        init();

        adapter = new FoundAdapter(this);

        mLvFound.setAdapter(adapter);
        mLvFound.setOnItemClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isWait) {
            waitWin.showWait();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isWait = true;
        if (!InternetUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
        } else {
            new InternetUtil(handler, InternetUtil.URL_LOST_GETINFO).get();
        }
    }

    private void init() {
        ((TextView) findViewById(R.id.tv_title)).setText("失物招领");
        mLvFound = (ListView) findViewById(R.id.lv_found);
        findViewById(R.id.et_found).setOnClickListener(this);
        findViewById(R.id.fab_add).setOnClickListener(this);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_found:
                intent.setClass(FoundActivity.this, SearchFoundActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_add:
                intent.setClass(FoundActivity.this, AddFoundActivity.class);
                startActivity(intent);
                break;
        }

    }

    public Activity getActivity() {
        return FoundActivity.this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onRefresh() {
        if (!InternetUtil.isConnected(getActivity())) {
            swipeLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
        } else {
            new InternetUtil(handler, InternetUtil.URL_LOST_GETINFO).get();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin != null)
            waitWin.dismissWait();
        waitWin = null;
    }
}
