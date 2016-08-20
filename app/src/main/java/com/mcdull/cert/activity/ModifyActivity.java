package com.mcdull.cert.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcdull.cert.ActivityMode.MyTitleActivity;
import com.mcdull.cert.DataBase.DataBaseUtil;
import com.mcdull.cert.R;
import com.mcdull.cert.domain.Course;
import com.mcdull.cert.utils.CourseUtilForSql;
import com.mcdull.cert.utils.Util;

import java.util.LinkedList;
import java.util.List;

public class ModifyActivity extends MyTitleActivity implements View.OnClickListener {

    private TextInputLayout mEtName;
    private TextInputLayout mEtTeacher;
    private TextInputLayout mEtSite;
    private TextInputLayout mEtWeek;
    private int mCourseid;
    private Course course;
    private TextInputLayout mEtTime;
    private TextInputLayout mEtWeekTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify);
        super.onCreate(savedInstanceState);

        mCourseid = getIntent().getIntExtra("CourseId", 0);
        course = null;

        initView();
        if (mCourseid != 0) {
            course = CourseUtilForSql.getCourse(this, mCourseid);
            ((TextView) findViewById(R.id.tv_title)).setText("修改课表");
        } else {
            course = new Course();
            course.setId(-1);
            mEtWeekTime.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_title)).setText("添加课表");
        }

        if (course != null && !TextUtils.isEmpty(course.getCourseName())) {
            mEtName.getEditText().setText(course.getCourseName());
            mEtTeacher.getEditText().setText(course.getTeacherName());
            mEtSite.getEditText().setText(course.getLocation());
            mEtWeek.getEditText().setText(course.getWeek());
            mEtTime.getEditText().setText(course.getTime());
        }
    }

    private void initView() {
        findViewById(R.id.bt_save).setOnClickListener(this);
        mEtName = (TextInputLayout) findViewById(R.id.et_name);
        mEtTeacher = (TextInputLayout) findViewById(R.id.et_teacher);
        mEtSite = (TextInputLayout) findViewById(R.id.et_site);
        mEtWeek = (TextInputLayout) findViewById(R.id.et_week);
        mEtWeekTime = (TextInputLayout) findViewById(R.id.et_week_time);

        mEtTime = (TextInputLayout) findViewById(R.id.et_time);
        mEtWeekTime.setHint("星期几");
        mEtTime.setHint("上课时间(格式：5-7)");
        mEtName.setHint("课程名称");
        mEtTeacher.setHint("授课教师");
        mEtSite.setHint("上课地点");
        mEtWeek.setHint("上课周次(格式：1-16)");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                modify();
                break;
        }
    }

    private void modify() {
        mEtName.setErrorEnabled(false);
        mEtTime.setErrorEnabled(false);
        String name = mEtName.getEditText().getText().toString();
        String teacher = mEtTeacher.getEditText().getText().toString();
        String site = mEtSite.getEditText().getText().toString();
        String week = mEtWeek.getEditText().getText().toString();
        String time = mEtTime.getEditText().getText().toString();
        List<String> list = new LinkedList<>();
        if (!TextUtils.isEmpty(name)) {
            list.add(name);
        } else {
            mEtName.setError("课程名称不能为空");
            mEtName.setErrorEnabled(true);
            return;
        }
        if (!TextUtils.isEmpty(time)) {
            if (Util.matcher(time, "[0-9]*-[0-9]*")) {
                String[] split = time.split("-");
                int start = Integer.parseInt(split[0]);
                int end = Integer.parseInt(split[1]);
                if (start > 0 && start < 11 && end > 0 && end < 11) {
                    course.setTime(time);
                } else {
                    mEtTime.setError("格式有误");
                    mEtTime.setErrorEnabled(false);
                    return;
                }
            } else {
                mEtTime.setError("格式有误");
                mEtTime.setErrorEnabled(false);
                return;
            }
        }else {
            mEtTime.setError("请填写上课时间");
            mEtTime.setErrorEnabled(false);
            return;
        }
        if (mEtWeekTime.getVisibility() == View.VISIBLE) {
            mEtWeekTime.setErrorEnabled(false);
            if (!TextUtils.isEmpty(mEtWeekTime.getEditText().getText().toString())) {
                int weekTime = Integer.parseInt(mEtWeekTime.getEditText().getText().toString());
                if (weekTime > 0 && weekTime < 8) {
                    course.setWeekTime(weekTime);
                } else {
                    mEtWeekTime.setErrorEnabled(true);
                    mEtWeekTime.setError("格式有误");
                    return;
                }
            } else {
                mEtWeekTime.setErrorEnabled(true);
                mEtWeekTime.setError("请填是星期几");
                return;
            }
            if (TextUtils.isEmpty(week)) {
                mEtWeek.setErrorEnabled(true);
                mEtWeek.setError("请填写上课周次");
                return;
            } else if (!Util.matcher(week, "[0-9]*-[0-9]*")) {
                mEtWeek.setErrorEnabled(true);
                mEtWeek.setError("格式有误");
                return;
            } else if (Integer.parseInt(week.split("-")[0]) < 1) {
                mEtWeek.setErrorEnabled(true);
                mEtWeek.setError("格式有误");
                return;
            }
        }
        course.setCourseName(name);
        course.setTeacherName(teacher);
        course.setLocation(site);
        course.setWeek(week);
        Boolean update;
        if (mEtWeekTime.getVisibility() == View.VISIBLE) {
            long insert = new DataBaseUtil(this).insert(course);
            if (insert == 0)
                update = false;
            else
                update = true;
        } else {
            update = CourseUtilForSql.updateCourse(this, course);
        }
        if (update) {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
    }
}
