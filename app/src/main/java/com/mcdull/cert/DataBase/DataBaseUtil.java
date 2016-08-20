package com.mcdull.cert.DataBase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mcdull.cert.domain.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Begin on 15/12/7.
 */
public class DataBaseUtil extends SQLiteOpenHelper {

    public DataBaseUtil(Context context) {
        super(context, "Course.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE COURSES (" +
                "id INTEGER primary key autoincrement," +
                "courseName text," +
                "teacherName text," +
                "time text," +
                "location text," +
                "week text," +
                "weekTime integer" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS COURSES";
        db.execSQL(sql);
        onCreate(db);
    }

    public List<Course> select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor courses = db.query("COURSES", null, null, null, null, null, null);
        List<Course> list = new ArrayList<>();
        while (courses.moveToNext()) {
            Course course = new Course();
            course.setId(courses.getInt(courses.getColumnIndex("id")));
            course.setCourseName(courses.getString(courses.getColumnIndex("courseName")));
            course.setTeacherName(courses.getString(courses.getColumnIndex("teacherName")));
            course.setTime(courses.getString(courses.getColumnIndex("time")));
            course.setLocation(courses.getString(courses.getColumnIndex("location")));
            course.setWeek(courses.getString(courses.getColumnIndex("week")));
            course.setWeekTime(courses.getInt(courses.getColumnIndex("weekTime")));
            list.add(course);
        }
        return list;
    }

    public Course select(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor courses = db.query("COURSES", null, "id=?", new String[]{"" + id}, null, null, null);
        courses.moveToFirst();
        Course course = new Course();
        course.setId(courses.getInt(courses.getColumnIndex("id")));
        course.setCourseName(courses.getString(courses.getColumnIndex("courseName")));
        course.setTeacherName(courses.getString(courses.getColumnIndex("teacherName")));
        course.setTime(courses.getString(courses.getColumnIndex("time")));
        course.setLocation(courses.getString(courses.getColumnIndex("location")));
        course.setWeek(courses.getString(courses.getColumnIndex("week")));
        course.setWeekTime(courses.getInt(courses.getColumnIndex("weekTime")));
        return course;
    }

    //增加操作
    public long insert(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor courses = db.query("COURSES", null, "weekTime=?", new String[]{"" + course.getWeekTime()}, null, null, null);
        while (courses.moveToNext()) {
            String time = courses.getString(courses.getColumnIndex("time"));
            int oldTimeStart = Integer.parseInt(time.split("-")[0]);
            int oldTimeEnd = Integer.parseInt(time.split("-")[1]);
            int timeStart = Integer.parseInt(course.getTime().split("-")[0]);
            int timeEnd = Integer.parseInt(course.getTime().split("-")[1]);
            if (timeStart <= oldTimeStart && oldTimeStart <= timeEnd && course.getWeek().equals(courses.getColumnIndex("week"))) {
                return 0;
            }
            if (timeStart <= oldTimeEnd && oldTimeEnd <= timeEnd && course.getWeek().equals(courses.getColumnIndex("week"))) {
                return 0;
            }
        }

        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("courseName", course.getCourseName());
        cv.put("teacherName", course.getTeacherName());
        cv.put("time", course.getTime());
        cv.put("location", course.getLocation());
        cv.put("week", course.getWeek());
        cv.put("weekTime", course.getWeekTime());
        long row = db.insert("COURSES", null, cv);
        return row;
    }

    //删除操作
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "id" + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.delete("COURSES", where, whereValue);
    }

    //修改操作
    public Boolean update(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor courses = db.query("COURSES", null, "weekTime=? AND id!=?", new String[]{"" + course.getWeekTime(), "" + course.getId()}, null, null, null);
        while (courses.moveToNext()) {
            String time = courses.getString(courses.getColumnIndex("time"));
            int oldTimeStart = Integer.parseInt(time.split("-")[0]);
            int oldTimeEnd = Integer.parseInt(time.split("-")[1]);
            int timeStart = Integer.parseInt(course.getTime().split("-")[0]);
            int timeEnd = Integer.parseInt(course.getTime().split("-")[1]);
            if (timeStart <= oldTimeStart && oldTimeStart <= timeEnd) {
                return false;
            }
            if (timeStart <= oldTimeEnd && oldTimeEnd <= timeEnd) {
                return false;
            }
        }

        String where = "id" + " = ?";
        String[] whereValue = {Integer.toString(course.getId())};

        ContentValues cv = new ContentValues();
        cv.put("courseName", course.getCourseName());
        cv.put("teacherName", course.getTeacherName());
        cv.put("time", course.getTime());
        cv.put("location", course.getLocation());
        cv.put("week", course.getWeek());
        cv.put("weekTime", course.getWeekTime());
        int id = db.update("COURSES", cv, where, whereValue);
        if (id != -1) {
            return true;
        } else {
            return false;
        }
    }

}
