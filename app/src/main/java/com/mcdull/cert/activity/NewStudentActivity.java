package com.mcdull.cert.activity;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mcdull.cert.R;
import com.mcdull.cert.fragment.NewStudentFragment;

public class NewStudentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);


        //判断SDK版本，设置沉浸状态栏
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            findViewById(R.id.status_bar).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.tv_title)).setText("新生专题");

        FragmentManager fragmentManager = getSupportFragmentManager();

        NewStudentFragment newStudentFragment = new NewStudentFragment();
        fragmentManager.beginTransaction().replace(R.id.framelayout, newStudentFragment).commit();

    }
}
