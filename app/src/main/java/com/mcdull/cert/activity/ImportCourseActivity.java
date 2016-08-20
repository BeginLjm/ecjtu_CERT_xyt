package com.mcdull.cert.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.R;
import com.mcdull.cert.adapter.SelectAdapter;
import com.mcdull.cert.anim.ShakeAnim;
import com.mcdull.cert.domain.CollegeIdName;
import com.mcdull.cert.json.AbsJsonUtil;
import com.mcdull.cert.utils.CourseUtilForSql;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

public class ImportCourseActivity extends MyTitleActivity implements OnClickListener {

    private CollegeIdName colleges[] = {new CollegeIdName("01", "土木建筑学院"),
            new CollegeIdName("02", "电气与电子工程学院"),
            new CollegeIdName("03", "机电工程学院"),
            new CollegeIdName("04", "经济管理学院"), new CollegeIdName("05", "体育学院"),
            new CollegeIdName("06", "信息工程学院"),
            new CollegeIdName("07", "人文社会科学学院"),
            new CollegeIdName("08", "基础科学学院"),
            new CollegeIdName("09", "外国语学院"), new CollegeIdName("10", "学工处"),
            new CollegeIdName("11", "艺术学院"), new CollegeIdName("12", "国际学院"),
            new CollegeIdName("13", "轨道交通学院"),
            new CollegeIdName("14", "马克思主义学院"),
            new CollegeIdName("21", "软件学院"), new CollegeIdName("31", "理学院"),
            new CollegeIdName("40", "现代教育技术中心"),
            new CollegeIdName("51", "职业技术学院"), new CollegeIdName("61", "国防生学院")};
    private TextView tvTitleView;
    private TextView tvSelectYearView;
    private TextView tvSelectCollegeView;
    private TextView tvSelectClassView;
    private Button btSureView;
    private AlertDialog alertDialog;
    private int[] years;
    private String year;
    private String collegeId;
    private List<Map<String, String>> classList;
    private String classId;
    private ShowWaitPopupWindow waitWin;
    private SharedPreferences SP;
    private Intent intent;

    Handler classHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 1) {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                try {
                    classList = AbsJsonUtil.getJsonUtil(ImportCourseActivity.this).classParseJson(json);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ImportCourseActivity.this, "查询失败,请稍候再试.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(ImportCourseActivity.this, "获取班级列表失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Handler courseHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 1) {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                Editor edit = SP.edit();
                int[] time = Util.getSystemTime();
                edit.putString("time", time[0] + "." + time[1]);
                edit.putBoolean("isCourse", true);
                edit.apply();

                CourseUtilForSql.saveCourse(json, ImportCourseActivity.this);

                finish();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_import_course);
        super.onCreate(savedInstanceState);

        initView();

        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
        if (alertDialog != null)
            alertDialog.dismiss();
        alertDialog = null;
    }

    private void initView() {
        tvTitleView = (TextView) findViewById(R.id.tv_title);
        tvSelectYearView = (TextView) findViewById(R.id.tv_select_year);
        tvSelectCollegeView = (TextView) findViewById(R.id.tv_select_college);
        tvSelectClassView = (TextView) findViewById(R.id.tv_select_class);
        btSureView = (Button) findViewById(R.id.bt_sure);
        findViewById(R.id.new_course).setOnClickListener(this);
    }

    private void init() {
        tvTitleView.setText("导入课表");
        tvSelectYearView.setOnClickListener(this);
        tvSelectCollegeView.setOnClickListener(this);
        tvSelectClassView.setOnClickListener(this);
        btSureView.setOnClickListener(this);
        SP = getSharedPreferences("config", MODE_PRIVATE);
//        int year = Util.getSystemYear();
        int year = 2014;
//        years = new int[]{year - 4, year - 3, year - 2, year - 1, year};
        years = new int[]{year - 2, year - 1, year};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                getCourse();
                break;
            case R.id.tv_select_year:
                showSelectYearDialog();
                break;
            case R.id.tv_select_college:
                showSelectCollegeDialog();
                break;
            case R.id.tv_select_class:
                if (classList != null) {
                    showSelectClassDialog();
                } else {
                    Toast.makeText(this, "未获取到班级列表", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.new_course:
                intent = new Intent(ImportCourseActivity.this, NewStudentCourse.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void getCourse() {
        if (TextUtils.isEmpty(collegeId)) {
            Toast.makeText(this, "请选择学院", Toast.LENGTH_SHORT).show();
            ShakeAnim skAnim = new ShakeAnim();
            skAnim.setDuration(1000);
            btSureView.startAnimation(skAnim);
            return;
        }
        if (TextUtils.isEmpty(year)) {
            Toast.makeText(this, "请选年级", Toast.LENGTH_SHORT).show();
            ShakeAnim skAnim = new ShakeAnim();
            skAnim.setDuration(1000);
            btSureView.startAnimation(skAnim);
            return;
        }
        if (TextUtils.isEmpty(classId)) {
            Toast.makeText(this, "请选择班级", Toast.LENGTH_SHORT).show();
            ShakeAnim skAnim = new ShakeAnim();
            skAnim.setDuration(1000);
            btSureView.startAnimation(skAnim);
            return;
        }
        waitWin = new ShowWaitPopupWindow(ImportCourseActivity.this);
        waitWin.showWait();

        final int[] time = Util.getSystemTime();


        Map<String, String> map = new ArrayMap<>();
        map.put("classId", classId);//设置get参数
        new InternetUtil(courseHandler, InternetUtil.URL_COURSE, map).get();//传入参数
    }

    private void showSelectYearDialog() {
        List<String> select = new ArrayList<>();
        for (int year : years) {
            select.add(year + "");
        }

        AlertDialog.Builder builder = new Builder(ImportCourseActivity.this);
        View view = View.inflate(ImportCourseActivity.this,
                R.layout.select_dialog, null);
        ListView lvSelect = (ListView) view.findViewById(R.id.lv_select);
        SelectAdapter selectAdapter = new SelectAdapter(this, select);
        lvSelect.setAdapter(selectAdapter);

        lvSelect.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                year = years[position] + "";
                tvSelectYearView.setText(years[position] + "");
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(collegeId)) {
                    waitWin = new ShowWaitPopupWindow(
                            ImportCourseActivity.this);
                    waitWin.showWait();

                    Map<String, String> map = new ArrayMap<>();
                    map.put("college_id", collegeId);//设置get参数
                    map.put("grade", year);//设置get参数
                    new InternetUtil(classHandler, InternetUtil.URL_OLD_CLASSLIST, map).get();//传入参数
                }
            }
        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();

        select = null;
    }

    private void showSelectCollegeDialog() {
        List<String> select = new ArrayList<>();
        for (CollegeIdName college : colleges) {
            select.add(college.getCollege_name());
        }

        AlertDialog.Builder builder = new Builder(ImportCourseActivity.this);
        View view = View.inflate(ImportCourseActivity.this,
                R.layout.select_dialog, null);

        ListView lvSelect = (ListView) view.findViewById(R.id.lv_select);
        SelectAdapter selectAdapter = new SelectAdapter(this, select);
        lvSelect.setAdapter(selectAdapter);

        lvSelect.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                collegeId = colleges[position].getCollege_id();
                tvSelectCollegeView.setText(colleges[position]
                        .getCollege_name());
                alertDialog.dismiss();
                if (!TextUtils.isEmpty(year)) {
                    waitWin = new ShowWaitPopupWindow(
                            ImportCourseActivity.this);
                    waitWin.showWait();

                    Map<String, String> map = new ArrayMap<>();
                    map.put("college_id", collegeId);//设置get参数
                    map.put("grade", year);//设置get参数
                    new InternetUtil(classHandler, InternetUtil.URL_OLD_CLASSLIST, map).get();//传入参数
                }
            }

        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();

        select = null;

    }

    private void showSelectClassDialog() {
        List<String> select = new ArrayList<>();
        for (int i = 0; i < classList.size(); i++) {
            select.add(classList.get(i).get("class_name"));
        }

        AlertDialog.Builder builder = new Builder(ImportCourseActivity.this);
        View view = View.inflate(ImportCourseActivity.this,
                R.layout.select_dialog, null);

        ListView lvSelect = (ListView) view.findViewById(R.id.lv_select);
        SelectAdapter selectAdapter = new SelectAdapter(this, select);
        lvSelect.setAdapter(selectAdapter);

        lvSelect.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                tvSelectClassView.setText(classList.get(position).get(
                        "class_name"));
                classId = classList.get(position).get("class_id");
                alertDialog.dismiss();
            }

        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();

        select = null;
    }

}
