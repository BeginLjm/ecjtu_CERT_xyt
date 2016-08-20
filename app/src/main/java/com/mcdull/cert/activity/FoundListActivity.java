package com.mcdull.cert.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.FoundListAdapter;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Deprecated
public class FoundListActivity extends MyTitleActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, FoundListAdapter.GetNextCallBack {

    private ListView mLvFound;
    private SwipeRefreshLayout swipeLayout;
    private Intent intent = new Intent();
    private Map<String, String> map;
    private Boolean isWait = true;
    private ShowWaitPopupWindow waitWin;
    private int page = 1;
    private int pageNum;
    private Boolean isMore = false;
    private FoundListAdapter adapter;
    public List<Map<String, String>> foundList;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isWait = false;
            if (waitWin != null)
                waitWin.dismissWait();
            swipeLayout.setRefreshing(false);
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                //错误
            } else {
                String json = ((Bundle) msg.obj).getString("Json");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    if ("200".equals(code)) {
                        pageNum = jsonObject.getInt("totalPage");
                        List<Map<String, String>> list = parsingJson(jsonObject);
                        if (isMore) {
                            foundList.addAll(list);
                        } else {
                            foundList = list;
                        }
                        isMore = false;
                        adapter.setNext(pageNum > page);
                        adapter.setMap(foundList);

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                        //错误
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "错误", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    };

    private List<Map<String, String>> parsingJson(JSONObject jsonObject) throws JSONException {
        List<Map<String, String>> list = new LinkedList<>();
        JSONArray searchInfo = jsonObject.getJSONArray("searchInfo");
        for (int i = 0; i < searchInfo.length(); i++) {
            Map<String, String> map = new HashMap<>();
            JSONObject item = searchInfo.getJSONObject(i);
            map.put("publishId", item.getString("publishId"));
            map.put("publishName", item.getString("publishName"));
            map.put("publishGetTime", item.getString("publishGetTime"));
            map.put("publishPic1", item.getString("publishPic1"));
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_found_list);
        super.onCreate(savedInstanceState);

        waitWin = new ShowWaitPopupWindow(getActivity());

        page = 1;

        String name = getIntent().getStringExtra("name");
        int type = getIntent().getIntExtra("type", 9);
        this.map = new HashMap<>();
        map.put("lostName", name + "");
        map.put("lostType", type + "");
        map.put("page", page + "");

        init();

        adapter = new FoundListAdapter(getActivity(), this);
        mLvFound.setAdapter(adapter);

        isWait = true;
        if (!InternetUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
        } else {
            new InternetUtil(handler, InternetUtil.URL_LOST_SEARCHLOST, map).get();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isWait)
            waitWin.showWait();
    }

    private void init() {
        ((TextView) findViewById(R.id.tv_title)).setText("失物招领");

        mLvFound = (ListView) findViewById(R.id.lv_found);
        findViewById(R.id.et_found).setOnClickListener(this);
        findViewById(R.id.fab_add).setOnClickListener(this);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    public Activity getActivity() {
        return FoundListActivity.this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_found:
                intent.setClass(getActivity(), SearchFoundActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_add:
                intent.setClass(getActivity(), AddFoundActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin!=null)
            waitWin.dismissWait();
        waitWin=null;
    }

    @Override
    public void onRefresh() {
        page = 1;
        isMore = false;
        map.put("page", page + "");
        if (!InternetUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
        } else {
            new InternetUtil(handler, InternetUtil.URL_LOST_SEARCHLOST, map).get();
        }
    }

    @Override
    public void getNextCallBack() {
        page = page + 1;
        isMore = true;
        map.put("page", page + "");
        if (!InternetUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
        } else {
            new InternetUtil(handler, InternetUtil.URL_LOST_SEARCHLOST, map).get();
        }
    }
}
