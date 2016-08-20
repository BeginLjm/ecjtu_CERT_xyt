package com.mcdull.cert.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.R;
import com.mcdull.cert.activity.ImportCourseActivity;
import com.mcdull.cert.activity.MyDataActivity;
import com.mcdull.cert.activity.NewStudentCourse;
import com.mcdull.cert.utils.GetIcon;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mcdull on 15/8/10.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ViewPager vpMain;
    private List<Fragment> fragments;
    private String studentId;
    private PopupWindow popupWindow;
    private Button btCourse;
    private ShowWaitPopupWindow waitWin;
    private ImageView mIvTX;
    private TabLayout mTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.activity_query, container, false);

        XiaomiUpdateAgent.setCheckUpdateOnlyWifi(false);
        XiaomiUpdateAgent.update(getActivity());

        waitWin = new ShowWaitPopupWindow(getActivity());
        String studentId = AVUser.getCurrentUser().getString("StudentId");

        initView();

        init();

        initIv();
        vpMain.setCurrentItem(0);
        if (getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getBoolean("homeType", false)) {
            vpMain.setCurrentItem(1);
        }


        Drawable drawable = getResources().getDrawable(R.drawable.icon);
        mIvTX.setImageBitmap(Util.toRoundBitmap(Util.drawableToBitmap(drawable)));
        drawable = null;

        new GetIcon(getActivity(), new GetIcon.GetIconCallBack() {
            @Override
            public void done(Bitmap bitmap) {
                if (bitmap != null) {
                    mIvTX.setImageBitmap(Util.toRoundBitmap(bitmap));
                }
            }
        });

        if (studentId != null) {
            SharedPreferences SP = getActivity().getSharedPreferences("myInfo", Context.MODE_PRIVATE);
            if (SP.getString("name", null) == null) {

                Map<String, String> map = new ArrayMap<>();
                map.put("studentId", studentId);//设置get参数
                new InternetUtil(infoHandler, InternetUtil.URL_GETSTUDENTINFO, map).get();//传入参数
            }
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences SP = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        getActivity().findViewById(R.id.view_home_title).setBackgroundColor(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getInt("theme", 0xff009688));
        getActivity().findViewById(R.id.tab).setBackgroundColor(getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE).getInt("theme", 0xff009688));

        if (SP.getBoolean("Icon", true)) {
            new GetIcon(getActivity(), new GetIcon.GetIconCallBack() {
                @Override
                public void done(Bitmap bitmap) {
                    if (bitmap != null) {
                        mIvTX.setImageBitmap(Util.toRoundBitmap(bitmap));
                    }
                }
            });
        }

        int num = vpMain.getCurrentItem();
        this.studentId = AVUser.getCurrentUser().getString("StudentId");
        if (num == 1 || num == 2) {
            if (TextUtils.isEmpty(studentId)) {
                //提示新生输入学号、
                showEditIdWin();
            }
        }
    }

    private void init() {

        this.studentId = AVUser.getCurrentUser().getString("StudentId");

        CourseFragment course = new CourseFragment();
        NewHomeFragment newHome = new NewHomeFragment();

        fragments = new ArrayList<>();
        fragments.add(newHome);
        fragments.add(course);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            String[] titles = {"个人", "课表"};

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };

        vpMain.setOffscreenPageLimit(5);
        vpMain.setAdapter(adapter);
        vpMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        initIv();
                        break;
                    case 1:
                        initIv();
                        btCourse.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(studentId)) {
                            //提示新生输入学号、
                            showEditIdWin();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setTabsFromPagerAdapter(adapter);
        mTabLayout.setupWithViewPager(vpMain);
    }

    private void showEditIdWin() {
        Rect outRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        View view = View.inflate(getActivity(), R.layout.win_prompt_id, null);
        view.findViewById(R.id.other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpMain.setCurrentItem(0);
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.bt_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入个人信息页
                popupWindow.dismiss();
                Intent intent = new Intent(getActivity(), MyDataActivity.class);
                startActivity(intent);
            }
        });
        popupWindow = new PopupWindow(view, outRect.width(), outRect.height());
        popupWindow.showAsDropDown(View.inflate(getActivity(), R.layout.activity_home, null), 0, outRect.top);
    }

    private void initIv() {
        btCourse.setVisibility(View.GONE);
    }


    private void initView() {
        btCourse = (Button) view.findViewById(R.id.bt_add_course);
        btCourse.setOnClickListener(this);
        view.findViewById(R.id.bt_me).setOnClickListener(this);
        vpMain = (ViewPager) view.findViewById(R.id.vp_main);
        mIvTX = (ImageView) view.findViewById(R.id.iv_tx);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_course:
                Intent intent = null;
                if (studentId.length()>15){
                    intent = new Intent(getActivity(), NewStudentCourse.class);
                }else {
                     intent = new Intent(getActivity(), ImportCourseActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.bt_me:
                openLeftWin();
                break;
        }
    }

    private void openLeftWin() {
        Intent intent = new Intent();
        intent.setAction("com.mcdull.cert.Home");
        intent.putExtra("type", 1);
        getActivity().sendBroadcast(intent);
    }

    Handler infoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json", null);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String departId = jsonObject.getString("departId");
                    if (TextUtils.isEmpty(departId) || departId.equals("null")) {
                        return;
                    }
                    String depart = jsonObject.getString("depart");
                    String year = jsonObject.getString("year");
                    String classId = jsonObject.getString("classId");
                    String className = jsonObject.getString("className");
                    String name = jsonObject.getString("name");
                    String studentId = jsonObject.getString("studentId");
                    SharedPreferences SP = getActivity().getSharedPreferences("myInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = SP.edit();
                    edit.putString("departId", departId);
                    edit.putString("depart", depart);
                    edit.putString("year", year);
                    edit.putString("classId", classId);
                    edit.putString("className", className);
                    edit.putString("name", name);
                    edit.putString("studentId", studentId);
                    edit.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (waitWin != null) {
            waitWin.dismissWait();
            waitWin = null;
        }
        if (popupWindow != null)
            popupWindow.dismiss();
        popupWindow = null;
    }
}
