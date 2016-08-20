package com.mcdull.cert.adapter;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.mcdull.cert.R;

import java.util.List;

/**
 * Created by mcdull on 15/7/10.
 */

@Deprecated
public class CourseAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private List<List<String>> courseList;
    private String week[] = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private GridView gridView;
    TextView tv;

    private FragmentActivity getActivity() {
        return activity;
    }


    public void setCourseList(List<List<String>> courseList) {
        this.courseList = courseList;
    }

    public CourseAdapter(FragmentActivity activity, List<List<String>> courseList, GridView gridView) {
        this.activity = activity;
        this.courseList = courseList;
        this.gridView = gridView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (courseList == null || courseList.size() == 0) {
            return 0;
        }
        return 48;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (courseList.size() == 0) {
            return null;
        }

        int x = position % 15;
        if (position < 8) {
            view = View.inflate(getActivity(), R.layout.item_week, null);
            tv = (TextView) view.findViewById(R.id.ItemText);
        } else {
            view = View.inflate(getActivity(), R.layout.item_course, null);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)(gridView.getHeight() / 4.7));
            view.setLayoutParams(param);
            tv = (TextView) view.findViewById(R.id.ItemText);
            tv.setTextSize(11);
        }
        if (position < 8) {
            tv.setText(week[position]);
        } else if (position == 8) {
            tv.setText("\n一\n\n\n二\n");
        } else if (position == 16) {
            tv.setText("\n三\n\n\n四\n");
        } else if (position == 24) {
            tv.setText("\n五\n\n\n六\n");
        } else if (position == 32) {
            tv.setText("\n七\n\n\n八\n");
        } else if (position == 40) {
            tv.setText("\n九\n\n\n十\n");
        } else {
            int i;
            int k = position + 1;
            if (k % 8 == 0) {
                i = ((k % 8)) + (((k / 8) - 1) * 7);
            } else {
                i = ((k % 8) - 1) + (((k / 8) - 1) * 7);
            }
            int j;
            if (i % 7 == 0) {
                j = (6 * 5) + (i / 7);
            } else {
                j = (((i % 7) - 1) * 5) + ((i / 7) + 1);
            }
            StringBuilder text = new StringBuilder();
            for (int a = 0; a < courseList.get(j - 1).size(); a++) {
                if (a != 1 && a != 4)
                    if (!TextUtils.isEmpty(courseList.get(j - 1).get(a)))
                        text.append(courseList.get(j - 1).get(a) + " ");
            }

            tv.setText(text.toString());

            if (TextUtils.isEmpty(courseList.get(j - 1).get(0))) {
                view.setBackgroundColor(0x00000000);
                x = 16;
            } else {
                x = string2int(courseList.get(j - 1).get(0)) % 15;
            }
        }

        switch (x) {
            case 0:
                view.setBackgroundResource(R.drawable.ic_course_bg_bohelv);
                break;
            case 1:
                view.setBackgroundResource(R.drawable.ic_course_bg_cheng);
                break;
            case 2:
                view.setBackgroundResource(R.drawable.ic_course_bg_cyan);
                break;
            case 3:
                view.setBackgroundResource(R.drawable.ic_course_bg_fen);
                break;
            case 4:
                view.setBackgroundResource(R.drawable.ic_course_bg_huang);
                break;
            case 5:
                view.setBackgroundResource(R.drawable.ic_course_bg_zi);
                break;
            case 6:
                view.setBackgroundResource(R.drawable.ic_course_bg_kafei);
                break;
            case 7:
                view.setBackgroundResource(R.drawable.ic_course_bg_lan);
                break;
            case 8:
                view.setBackgroundResource(R.drawable.ic_course_bg_lv);
                break;
            case 9:
                view.setBackgroundResource(R.drawable.ic_course_bg_molan);
                break;
            case 10:
                view.setBackgroundResource(R.drawable.ic_course_bg_pressed);
                break;
            case 11:
                view.setBackgroundResource(R.drawable.ic_course_bg_pulan);
                break;
            case 12:
                view.setBackgroundResource(R.drawable.ic_course_bg_qing);
                break;
            case 13:
                view.setBackgroundResource(R.drawable.ic_course_bg_tao);
                break;
            case 14:
                view.setBackgroundResource(R.drawable.ic_course_bg_tuhuang);
                break;
        }
        return view;
    }

    private static int string2int(String decript) {
        byte[] bytes = decript.getBytes();
        int a = 0;
        for (byte aByte : bytes) {
            a = a + (aByte & 0xFF);
        }
        return a;
    }
}