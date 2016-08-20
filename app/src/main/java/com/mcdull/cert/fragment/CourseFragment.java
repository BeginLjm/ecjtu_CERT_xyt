package com.mcdull.cert.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.R;
import com.mcdull.cert.View.CourseView;
import com.mcdull.cert.activity.ModifyActivity;
import com.mcdull.cert.activity.SettingActivity;
import com.mcdull.cert.adapter.SelectAdapter;
import com.mcdull.cert.domain.Course;
import com.mcdull.cert.utils.CourseUtilForSql;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowEditDialog;
import com.mcdull.cert.utils.ShowSureDialog;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class CourseFragment extends Fragment implements CourseView.OnItemClick, CourseView.OnItemLongClick, View.OnClickListener {
    private SharedPreferences SP;
    private View view;
    private ShowWaitPopupWindow waitWin;
    private View mBackGround;
    private ShowSureDialog sureDialog;

    Handler courseHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 1) {

                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                SharedPreferences.Editor edit = SP.edit();
                int[] time = Util.getSystemTime();
                edit.putString("time", time[0] + "." + time[1]);
                edit.apply();

                CourseUtilForSql.saveCourse(json, getActivity());

                mCourseList = CourseUtilForSql.getCourse(getActivity());
                mBackGround.setVisibility(View.VISIBLE);
                if (mCourseList == null || mCourseList.size() == 0) {
                    mBackGround.setVisibility(View.VISIBLE);
                    return;
                }

                for (int i = 0; i < mCourseList.size(); i++)
                    if (!TextUtils.isEmpty(mCourseList.get(i).getCourseName()))
                        mBackGround.setVisibility(View.GONE);
                mCourseView.setCourseAndWeek(mCourseList, Util.getNowWeekNumber(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("weekStartDate", "")));
            } else {
                mBackGround.setVisibility(View.VISIBLE);
            }
        }
    };
    private CourseView mCourseView;
    private List<Course> mCourseList;
    private TextView mTvWeekNumber;
    private ShowEditDialog alertDialog;
    private AlertDialog selectAlertDialog;

    @Override
    public void onResume() {
        super.onResume();
        final String studentId = AVUser.getCurrentUser().getString("StudentId");

        mTvWeekNumber.setText("第" + Util.getNowWeekNumber(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("weekStartDate", "")) + "周");

        if (TextUtils.isEmpty(studentId)) {
            return;
        }

        Boolean isCourse = (CourseUtilForSql.getCourse(getActivity()).size() != 0);
        if (!isCourse && studentId.length() == 14) {
            Map<String, String> map = new ArrayMap<>();
            map.put("classId", studentId.substring(0, 12));//设置get参数
            Log.d("classId", studentId.substring(0, 12));
            new InternetUtil(courseHandler, InternetUtil.URL_COURSE, map).get();//传入参数
            return;
        }
        if (!isCourse && studentId.length() == 16) {
            //获取新生课表
            String password = AVUser.getCurrentUser().getString("JwcPwd");
            if (!TextUtils.isEmpty(password)) {
                Map<String, String> map = new ArrayMap<>();
                map.put("UserName", studentId);//设置get参数
                map.put("Password", password);//设置get参数
                new InternetUtil(courseHandler, InternetUtil.URL_COURSE, map).get();//传入参数
            }
            return;

        }
        mCourseList = CourseUtilForSql.getCourse(getActivity());
        mBackGround.setVisibility(View.VISIBLE);

        for (int i = 0; i < mCourseList.size(); i++)
            if (!TextUtils.isEmpty(mCourseList.get(i).getCourseName()))
                mBackGround.setVisibility(View.GONE);
        mCourseView.setCourseAndWeek(mCourseList, Util.getNowWeekNumber(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("weekStartDate", "")));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_course, container, false);

        waitWin = new ShowWaitPopupWindow(getActivity());
        mCourseList = new ArrayList<>();
        mCourseView = (CourseView) view.findViewById(R.id.course_view);
        mCourseView.setOnItemClick(this);
        mCourseView.setOnItemLongClick(this);

        mBackGround = view.findViewById(R.id.back);
        mBackGround.setVisibility(View.VISIBLE);
        mBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });

        mTvWeekNumber = (TextView) view.findViewById(R.id.tv_week_number);
        mTvWeekNumber.setText("第" + Util.getNowWeekNumber(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("weekStartDate", "")) + "周");
        mTvWeekNumber.setOnClickListener(this);


        view.findViewById(R.id.fab_course_add).setOnClickListener(this);

        this.SP = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        final String studentId = AVUser.getCurrentUser().getString("StudentId");

        if (TextUtils.isEmpty(studentId)) {
            return view;
        }
        int[] time = Util.getSystemTime();
        final String timeString = time[0] + "." + time[1];
        Boolean isCourse = (CourseUtilForSql.getCourse(getActivity()).size() != 0);
        if (!timeString.equals(SP.getString("time", null)) || !isCourse) {
            if (studentId.length() == 14) {
                //获取老生课表
                waitWin.showWait();
                Map<String, String> map = new ArrayMap<>();
                map.put("class_id", studentId.substring(0, 12));//设置get参数
                new InternetUtil(courseHandler, InternetUtil.URL_COURSE, map).get();//传入参数

                return view;
            } else if (studentId.length() == 16) {
                //获取新生课表
                String password = AVUser.getCurrentUser().getString("JwcPwd");
                if (!TextUtils.isEmpty(password)) {
                    Map<String, String> map = new ArrayMap<>();
                    map.put("UserName", studentId);//设置get参数
                    map.put("Password", password);//设置get参数
                    String url = InternetUtil.URL_COURSE;//设置url
                    new InternetUtil(courseHandler, url, map).get();//传入参数
                } else {
                    return view;
                }
            } else {
                return view;
            }
        } else {
            mCourseList = CourseUtilForSql.getCourse(getActivity());
            for (int i = 0; i < mCourseList.size(); i++)
                if (!TextUtils.isEmpty(mCourseList.get(i).getCourseName()))
                    mBackGround.setVisibility(View.GONE);
            mCourseView.setCourseAndWeek(mCourseList, Util.getNowWeekNumber(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("weekStartDate", "")));
            return view;
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
        if (sureDialog != null)
            sureDialog.dismiss();
        sureDialog = null;
    }

    @Override
    public void onItemClick(int id, View view) {
        Intent intent = new Intent(getActivity(), ModifyActivity.class);
        intent.putExtra("CourseId", id);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(final int id, View view) {
        sureDialog = new ShowSureDialog(getActivity(), "是否确认删除？", new ShowSureDialog.CallBack() {
            @Override
            public void CallBack() {
                CourseUtilForSql.delete(id, getActivity());
                mCourseView.setCourseAndWeek(CourseUtilForSql.getCourse(getActivity()), Util.getNowWeekNumber(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("weekStartDate", "")));
                sureDialog.dismiss();
            }
        });
        sureDialog.showDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_course_add:
                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_week_number:
                showSelectWeekDialog();
                break;
        }
    }

    private void showSelectWeekDialog() {
        List<String> select = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            select.add(i + "");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.select_dialog, null);
        ListView lvSelect = (ListView) view.findViewById(R.id.lv_select);
        SelectAdapter selectAdapter = new SelectAdapter(getActivity(), select);
        lvSelect.setAdapter(selectAdapter);

        lvSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mCourseView.setWeek(position + 1);
                mTvWeekNumber.setText("第" + (position + 1) + "周");
                selectAlertDialog.dismiss();
            }
        });

        selectAlertDialog = builder.create();
        selectAlertDialog.setView(view, 0, 0, 0, 0);
        selectAlertDialog.show();

        select = null;

    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (position < 8 || position % 8 == 0) {
//            return;
//        }
//        final int i, j, l;
//        int k = position + 1;
//        if (k % 8 == 0) {
//            l = ((k / 8) - 1) * 7;
//        } else {
//            l = ((k % 8) - 1) + (((k / 8) - 1) * 7);
//        }
//        if (l % 7 == 0) {
//            i = 6;
//            j = (l / 7) - 1;
//        } else {
//            i = (l % 7) - 1;
//            j = l / 7;
//        }
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), ModifyActivity.class);
//        intent.putExtra("type", new int[]{i, j});
//        startActivity(intent);
//    }

//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        if (position < 8 || position % 8 == 0) {
//            return false;
//        }
//        final int i, j, l;
//        int k = position + 1;
//        if (k % 8 == 0) {
//            l = ((k / 8) - 1) * 7;
//        } else {
//            l = ((k % 8) - 1) + (((k / 8) - 1) * 7);
//        }
//        if (l % 7 == 0) {
//            i = 6;
//            j = (l / 7) - 1;
//        } else {
//            i = (l % 7) - 1;
//            j = l / 7;
//        }
//
//        List<String> list = CourseUtil.getCourse(i, j, getActivity());
//        if (TextUtils.isEmpty(list.get(0)))
//            return false;
//
//        sureDialog = new ShowSureDialog(getActivity(), "是否确认删除？", new ShowSureDialog.CallBack() {
//            @Override
//            public void CallBack() {
//                CourseUtil.removeCourse(getActivity(), i, j);
////                adapter.setCourseList(CourseUtil.getCourse(getActivity()));
////                adapter.notifyDataSetChanged();
//                sureDialog.dismiss();
//            }
//        });
//        sureDialog.showDialog();
//        return true;
//    }
}
