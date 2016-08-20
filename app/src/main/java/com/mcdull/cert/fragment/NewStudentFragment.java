package com.mcdull.cert.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mcdull.cert.R;
import com.mcdull.cert.activity.EnrollActivity;
import com.mcdull.cert.activity.MapActivity;
import com.mcdull.cert.activity.TripActivity;
import com.mcdull.cert.utils.InternetUtil;

public class NewStudentFragment extends Fragment implements View.OnClickListener {
    private View view;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_new_student, container, false);
        initView();
        return view;
    }

    private void initView() {
        view.findViewById(R.id.bt_map).setOnClickListener(this);
        view.findViewById(R.id.bt_enroll).setOnClickListener(this);
        view.findViewById(R.id.bt_dxzn).setOnClickListener(this);
        view.findViewById(R.id.bt_dxcx).setOnClickListener(this);
        view.findViewById(R.id.bt_jdgk).setOnClickListener(this);
        view.findViewById(R.id.bt_hqss).setOnClickListener(this);
        view.findViewById(R.id.bt_xyhd).setOnClickListener(this);
        view.findViewById(R.id.bt_xxzn).setOnClickListener(this);
        view.findViewById(R.id.bt_hkqy).setOnClickListener(this);
        view.findViewById(R.id.bt_lxdh).setOnClickListener(this);
        view.findViewById(R.id.bt_pkbz).setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (!InternetUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()){
            case R.id.bt_map:
                intent = new Intent(getActivity(),MapActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_enroll:
                intent = new Intent(getActivity(),EnrollActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_dxzn:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","到校指南");
                startActivity(intent);
                break;
            case R.id.bt_dxcx:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","到校出行");
                startActivity(intent);
                break;
            case R.id.bt_jdgk:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","交大概况");
                startActivity(intent);
                break;
            case R.id.bt_hqss:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","后勤设施");
                startActivity(intent);
                break;
            case R.id.bt_xyhd:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","校园活动");
                startActivity(intent);
                break;
            case R.id.bt_xxzn:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","学习指南");
                startActivity(intent);
                break;
            case R.id.bt_hkqy:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","户口迁移");
                startActivity(intent);
                break;
            case R.id.bt_lxdh:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","联系电话");
                startActivity(intent);
                break;
            case R.id.bt_pkbz:
                intent = new Intent(getActivity(),TripActivity.class);
                intent.putExtra("Title","贫困补助");
                startActivity(intent);
                break;
        }
    }
}
