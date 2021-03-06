package com.mcdull.cert.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;

public class RepairSucActivity extends MyTitleActivity {

    private TextView myRepairTime;
    private String time;
    private ShowWaitPopupWindow waitWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_repair_suc);
        super.onCreate(savedInstanceState);

        time = getIntent().getStringExtra("time");
        waitWin = new ShowWaitPopupWindow(this);

        init();
    }

    private void init() {
        myRepairTime = (TextView) findViewById(R.id.repair_time);
        myRepairTime.setText("请在" + time + "前往教7栋105进行维修服务");

        ((TextView) findViewById(R.id.tv_title)).setText("电脑报修");
        findViewById(R.id.dis_repair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitWin.showWait();
                new InternetUtil(handle, InternetUtil.URL_REPAIR_STATUS + AVUser.getCurrentUser().getString("StudentId")).get();
            }
        });
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin!=null)
            waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(RepairSucActivity.this, "该预约已被取消", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                Toast.makeText(RepairSucActivity.this, "预约已取消成功", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin!=null){
            waitWin.dismissWait();
            waitWin = null;
        }
    }
}
