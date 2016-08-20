package com.mcdull.cert.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;

import java.util.LinkedList;
import java.util.List;

public class SearchFoundActivity extends MyTitleActivity implements View.OnClickListener {

    private Intent intent = new Intent();
    private List<LinearLayout> items = new LinkedList<>();
    private int type = 0;
    private TextInputLayout mEtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_found);
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        ((TextView) findViewById(R.id.tv_title)).setText("搜索失物");
        findViewById(R.id.bt_ok).setVisibility(View.VISIBLE);
        findViewById(R.id.bt_ok).setOnClickListener(this);


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

        mEtName = (TextInputLayout) findViewById(R.id.et_found_name);
        mEtName.setHint("请填写物品名称");

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
            case R.id.bt_ok:
                search();
                break;
        }
    }

    private void search() {
        if (type == 0 && TextUtils.isEmpty(mEtName.getEditText().getText().toString())) {
            Toast.makeText(SearchFoundActivity.this, "请填写物品名称或选择物品类型", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent intent = new Intent(SearchFoundActivity.this, FoundListActivity.class);
            if (TextUtils.isEmpty(mEtName.getEditText().getText().toString())) {
                intent.putExtra("name", "");
                intent.putExtra("type", (type + 9) % 10);
            } else {
                intent.putExtra("name", mEtName.getEditText().getText().toString());
                intent.putExtra("type", (type + 9) % 10);
            }
            startActivity(intent);
            finish();
        }

    }
}
