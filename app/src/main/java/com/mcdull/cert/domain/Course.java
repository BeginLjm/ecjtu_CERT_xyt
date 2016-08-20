package com.mcdull.cert.domain;

/**
 * Created by Begin on 15/12/6.
 */
public class Course {
    private int id;
    private String courseName;
    private String teacherName;
    private String time;
    private String week;
    private String location;
    private int weekTime;

    public Course() {
    }

    public Course(int id,String courseName, String teacherName, String time, String week, String location, int weekTime) {
        this.id = id;
        this.courseName = courseName;
        this.weekTime = weekTime;
        this.teacherName = teacherName;
        this.time = time;
        this.week = week;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeekTime() {
        return weekTime;
    }

    public void setWeekTime(int weekTime) {
        this.weekTime = weekTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getLocation() {
        return location;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTime() {
        return time;
    }

    public String getWeek() {
        return week;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
