package com.mcdull.cert.View;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcdull.cert.R;
import com.mcdull.cert.domain.Course;
import com.mcdull.cert.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Begin on 15/12/6.
 */
public class CourseView extends RelativeLayout {
    private final Context context;
    private final int tvWidth;
    private final int tvHeight;
    private RelativeLayout courseLayout;
    private View view;
    private List<Course> list;
    private List<TextView> textViews;
    private String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] times = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private OnItemClick click;
    private OnItemLongClick longClick;
    private int weekNumber = 1;

    public CourseView(Context context) {
        this(context, null);
    }

    public CourseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CourseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.view = View.inflate(context, R.layout.course_layout, null);
        this.context = context;
        this.addView(view);

        courseLayout = (RelativeLayout) view.findViewById(R.id.course_layout);
        LinearLayout courseTop = (LinearLayout) view.findViewById(R.id.course_top);
        LinearLayout courseTime = (LinearLayout) view.findViewById(R.id.time_layout);

        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        courseLayout.measure(w, h);
        int height = courseLayout.getMeasuredHeight();
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        this.tvWidth = 2 * outMetrics.widthPixels / 15;
        this.tvHeight = height / 10;

        TextView t = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tvWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        t.setLayoutParams(params);
        courseTop.addView(t);

        for (int i = 0; i < 7; i++) {
            TextView textView = new TextView(context);
            params = new LinearLayout.LayoutParams(tvWidth - 6, Util.Dp2Px(context, 27));
            params.leftMargin = 3;
            params.rightMargin = 3;
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setText(weeks[i]);
            textView.setBackgroundResource(R.drawable.ic_course_bg_pressed);
            courseTop.addView(textView);
        }

        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(context);
            params = new LinearLayout.LayoutParams(tvWidth / 2 - 6, tvHeight - 6);
            params.leftMargin = 3;
            params.rightMargin = 3;
            params.topMargin = 3;
            params.bottomMargin = 3;
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setText(times[i]);
            textView.setBackgroundResource(R.drawable.ic_course_bg_pressed);
            courseTime.addView(textView);
        }
    }

    public void loadView() {
        if (textViews != null && textViews.size() != 0)
            textViews.removeAll(textViews);
        textViews = new ArrayList<>();
        if (list != null && list.size() != 0) {
            courseLayout.removeAllViews();
            for (Course c : list) {


                try {
                    if (Integer.parseInt(c.getWeek()) > weekNumber || Integer.parseInt(c.getWeek()) < weekNumber) {
                        continue;
                    }
                } catch (Exception e) {
                    try {
                        String[] split = c.getWeek().split("-");
                        if (Integer.parseInt(split[0]) > weekNumber || Integer.parseInt(split[1]) < weekNumber) {
                            continue;
                        }
                    } catch (Exception e1) {

                    }
                }

                String time = c.getTime();
                int timeStart = Integer.parseInt(time.split("-")[0]);
                int timeEnd = Integer.parseInt(time.split("-")[1]);
                TextView textView = new TextView(context);
                LayoutParams params = new LayoutParams(tvWidth - 6, (timeEnd - timeStart + 1) * tvHeight - 6);
                params.topMargin = (timeStart - 1) * tvHeight + 3;
                params.leftMargin = (c.getWeekTime() - 1) * tvWidth + 3;
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(params);
                textView.setText(c.getCourseName() + "\n" + c.getLocation());
                textView.setTextSize(11);
                textView.setTextColor(0xffffffff);

                int x = string2int(c.getCourseName()) % 14;
                switch (x) {
                    case 0:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_bohelv);
                        break;
                    case 1:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_cheng);
                        break;
                    case 2:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_cyan);
                        break;
                    case 3:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_fen);
                        break;
                    case 4:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_huang);
                        break;
                    case 5:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_zi);
                        break;
                    case 6:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_kafei);
                        break;
                    case 7:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_lan);
                        break;
                    case 8:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_lv);
                        break;
                    case 9:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_molan);
                        break;
                    case 10:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_pulan);
                        break;
                    case 11:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_qing);
                        break;
                    case 12:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_tao);
                        break;
                    case 13:
                        textView.setBackgroundResource(R.drawable.ic_course_bg_tuhuang);
                        break;
                }

                TextView id = new TextView(context);
                params = new RelativeLayout.LayoutParams(tvWidth, (timeEnd - timeStart + 1) * tvHeight);
                params.topMargin = (timeStart - 1) * tvHeight;
                params.leftMargin = (c.getWeekTime() - 1) * tvWidth;
                id.setLayoutParams(params);
                id.setText("" + c.getId());
                id.setTextColor(0x00000000);

                id.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (click != null)
                            click.onItemClick(Integer.parseInt(((TextView) v).getText().toString()), v);
                    }
                });
                id.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (longClick != null)
                            longClick.onItemLongClick(Integer.parseInt(((TextView) v).getText().toString()), v);
                        return false;
                    }
                });

                textViews.add(id);

                courseLayout.addView(textView);
                courseLayout.addView(id);

            }
        }
    }

    public void setCourseAndWeek(List<Course> list, int week) {
        this.list = list;
        this.weekNumber = week;
        loadView();
    }

    public void setWeek(int week) {
        this.weekNumber = week;
        loadView();
    }

    private static int string2int(String decript) {
        byte[] bytes = decript.getBytes();
        int a = 0;
        for (byte aByte : bytes) {
            a = a + (aByte & 0xFF);
        }
        return a;
    }

    public void setOnItemClick(final OnItemClick click) {
        this.click = click;
    }

    public void setOnItemLongClick(final OnItemLongClick click) {
        this.longClick = click;
    }


    public interface OnItemLongClick {
        void onItemLongClick(int id, View view);
    }

    public interface OnItemClick {
        void onItemClick(int id, View view);
    }
}
