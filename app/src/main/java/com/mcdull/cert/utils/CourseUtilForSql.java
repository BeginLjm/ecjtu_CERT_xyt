package com.mcdull.cert.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.mcdull.cert.DataBase.DataBaseUtil;
import com.mcdull.cert.domain.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Begin on 15/12/7.
 */
public class CourseUtilForSql {
    public static void saveCourse(String json, Context context) {
        if (TextUtils.isEmpty(json))
            return;
        List<Course> list = jsonParseCourse(json);
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        List<Course> select = dataBaseUtil.select();
        for (Course c : select) {
            dataBaseUtil.delete(c.getId());
        }
        for (Course c : list) {
            dataBaseUtil.insert(c);
        }
    }

    public static List<Course> getCourse(Context context) {
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        List<Course> select = dataBaseUtil.select();
        return select;
    }

    public static void delete(int id, Context context) {
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        dataBaseUtil.delete(id);
    }

    public static Course getCourse(Context context, int id) {
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        return dataBaseUtil.select(id);
    }

    public static Boolean updateCourse(Context context, Course course) {
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        return dataBaseUtil.update(course);

    }

    private static List<Course> jsonParseCourse(String json) {
        List<Course> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 1; i <= jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i - 1);
                for (int j = 0; j < array.length(); j++) {
                    JSONObject jsonObject = array.getJSONObject(j);
                    if (!TextUtils.isEmpty(jsonObject.getString("name"))) {
                        Course course = new Course();
                        course.setCourseName(jsonObject.getString("name").replace(" ", ""));
                        course.setTeacherName(jsonObject.getString("teacher").replace(" ", ""));
                        course.setLocation(jsonObject.getString("className").replace(" ", ""));
                        course.setWeek(jsonObject.getString("week").replace(" ", ""));
                        String time = jsonObject.getString("time").replace(" ", "");
                        time = time.split(",")[0] + "-" + time.split(",")[time.split(",").length - 1];
                        course.setTime(time);
                        course.setWeekTime(i);
                        list.add(course);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
