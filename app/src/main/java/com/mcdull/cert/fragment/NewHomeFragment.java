package com.mcdull.cert.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.mcdull.cert.R;
import com.mcdull.cert.activity.CetSearchScheduleActivity;
import com.mcdull.cert.activity.ECardActivity;
import com.mcdull.cert.activity.ExamActivity;
import com.mcdull.cert.activity.FoundActivity;
import com.mcdull.cert.activity.LibraryActivity;
import com.mcdull.cert.activity.MapActivity;
import com.mcdull.cert.activity.NewStudentActivity;
import com.mcdull.cert.activity.ReExamActivity;
import com.mcdull.cert.activity.RepairActivity;
import com.mcdull.cert.activity.RepairSucActivity;
import com.mcdull.cert.activity.ScoreActivity;
import com.mcdull.cert.activity.TripActivity;
import com.mcdull.cert.json.AbsJsonUtil;
import com.mcdull.cert.utils.InternetUtil;
import com.mcdull.cert.utils.ShowWaitPopupWindow;
import com.mcdull.cert.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class NewHomeFragment extends Fragment implements View.OnClickListener {

    private Intent intent;
    private ShowWaitPopupWindow waitWin;
    private AVUser user;
    private TextView mTvConsume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_home, container, false);

        waitWin = new ShowWaitPopupWindow(getActivity());

        user = AVUser.getCurrentUser();

        init(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final String studentId = user.getString("StudentId");
        final String eCardPassword = user.getString("EcardPwd");
        if (TextUtils.isEmpty(studentId) || studentId.equals("null")) {
            mTvConsume.setText("请填写学号及一卡通密码");
            mTvConsume.setTextSize(14);
            return;
        }
        if (TextUtils.isEmpty(eCardPassword) || eCardPassword.equals("null")) {
            mTvConsume.setText("请填写学号及一卡通密码");
            mTvConsume.setTextSize(14);
            return;
        }

        Map<String, String> map = new ArrayMap<>();
        map.put("cardid", studentId);//设置get参数
        map.put("passwd", eCardPassword);//设置get参数
        map.put("format", "json");
        new InternetUtil(consumeHandler, InternetUtil.URL_ECARD, map).get();//传入参数
    }

    Handler consumeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (waitWin != null) {
                waitWin.dismissWait();
            }
            if (msg.what == 0) {
                mTvConsume.setText("加载失败");
                mTvConsume.setTextSize(17);
            } else {

                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                if (Util.replace(json).equals("false")) {
                    mTvConsume.setText("加载失败");
                    mTvConsume.setTextSize(17);
                    return;
                }
                String times = null;
                String num = null;
                try {
                    Map<String, String> map = AbsJsonUtil.getJsonUtil(getActivity()).homeECardParseJson(json);
                    times = map.get("times");
                    num = map.get("num");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ("".equals(times) || "0".equals(times)) {
                    mTvConsume.setText("今日暂无消费");
                    mTvConsume.setTextSize(17);
                } else {
                    mTvConsume.setText(num);
                    mTvConsume.setTextSize(29);
                }
            }
        }
    };

    private void init(View view) {
        view.findViewById(R.id.ll_grade).setOnClickListener(this);
        view.findViewById(R.id.ll_exam).setOnClickListener(this);
        view.findViewById(R.id.ll_re_exam).setOnClickListener(this);
        view.findViewById(R.id.ll_ecard).setOnClickListener(this);
        view.findViewById(R.id.ll_cet).setOnClickListener(this);
        view.findViewById(R.id.ll_library).setOnClickListener(this);
        view.findViewById(R.id.ll_repair).setOnClickListener(this);
        view.findViewById(R.id.ll_logistics).setOnClickListener(this);
        view.findViewById(R.id.rl_found).setOnClickListener(this);
        view.findViewById(R.id.ll_map).setOnClickListener(this);
        view.findViewById(R.id.new_student).setOnClickListener(this);
        mTvConsume = (TextView) view.findViewById(R.id.tv_eCard_consume);
    }


    @Override
    public void onClick(View v) {
        if (!InternetUtil.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.ll_grade:
                findGrade();
                break;
            case R.id.ll_exam:
                findExam();
                break;
            case R.id.ll_re_exam:
                findReExam();
                break;
            case R.id.ll_ecard:
                findECard();
                break;
            case R.id.ll_cet:
                intent = new Intent(getActivity(), CetSearchScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_library:
                findLibrary();
                break;
            case R.id.ll_repair:
                getOrderState();
                break;
            case R.id.ll_logistics:
                intent = new Intent(getActivity(), TripActivity.class);
                intent.putExtra("Title", "花椒维权");
                startActivity(intent);
                break;
            case R.id.ll_map:
                intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_found:
                if (!TextUtils.isEmpty(AVUser.getCurrentUser().getMobilePhoneNumber())) {
                    intent = new Intent(getActivity(), FoundActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "请在个人信息页填写手机号", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.new_student:
                intent = new Intent(getActivity(), NewStudentActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void getOrderState() {
        waitWin.showWait();
        new InternetUtil(orderStateHandle, InternetUtil.URL_REPAIR_GETORDER + AVUser.getCurrentUser().getString("StudentId")).get();
    }

    Handler orderStateHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "链接网络失败，请稍后再试", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                if (Util.replace(json).length() == 2) {
                    getOrderTime();
                } else {
                    intent.putExtra("time", json);
                    startActivity(intent);
                }
            }
        }
    };

    private void getOrderTime() {
        waitWin.showWait();
        new InternetUtil(orderTimeHandle, InternetUtil.URL_REPAIR_TIME).get();//传入参数
    }

    Handler orderTimeHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "链接网络失败，请稍后再试", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                intent = new Intent(getActivity(), RepairActivity.class);
                intent.putExtra("TimeJson", json);
                startActivity(intent);
            }
        }
    };

    private void findLibrary() {
        final String studentId = user.getString("StudentId");
        if (TextUtils.isEmpty(studentId) || studentId.equals("null")) {
            Toast.makeText(getActivity(), "请先在个人信息页补全信息", Toast.LENGTH_SHORT).show();
            return;
        }

        waitWin.showWait();
        Map<String, String> map = new ArrayMap<>();
        map.put("student_id", studentId);//设置get参数
        new InternetUtil(libraryHandler, InternetUtil.URL_LIBRARY, map).get();//传入参数

    }

    Handler libraryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "查询失败，请稍后再试", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                if (Util.replace(json).equals("false") || Util.replace(json).equals("null")) {
                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), LibraryActivity.class);
                intent.putExtra("libraryJson", json);
                startActivity(intent);
            }
        }
    };

    private void findECard() {
        final String studentId = user.getString("StudentId");
        final String eCardPassword = user.getString("EcardPwd");
        if (TextUtils.isEmpty(studentId) || studentId.equals("null")) {
            Toast.makeText(getActivity(), "请先在个人信息页补全信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(eCardPassword) || eCardPassword.equals("null")) {
            Toast.makeText(getActivity(), "请先在个人信息页补全信息", Toast.LENGTH_SHORT).show();
            return;
        }

        waitWin.showWait();

        Map<String, String> map = new ArrayMap<>();
        map.put("cardid", studentId);//设置get参数
        map.put("passwd", eCardPassword);//设置get参数
        map.put("format", "json");
        new InternetUtil(eCardHandler, InternetUtil.URL_ECARD, map).get();//传入参数
    }

    Handler eCardHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "查询失败，请稍后再试", Toast.LENGTH_SHORT).show();
            } else {

                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                if (Util.replace(json).equals("false")) {
                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), ECardActivity.class);
                intent.putExtra("eCardJson", json);
                startActivity(intent);
            }
        }
    };

    private void findReExam() {
        final String studentId = user.getString("StudentId");
        if (TextUtils.isEmpty(studentId) || studentId.equals("null")) {
            Toast.makeText(getActivity(), "请先在个人信息页补全信息", Toast.LENGTH_SHORT).show();
            return;
        }

        waitWin.showWait();
        Map<String, String> map = new ArrayMap<>();
        map.put("student_id", studentId);//设置get参数
        new InternetUtil(reExamHandler, InternetUtil.URL_OLD_REEXAM, map).get();//传入参数
    }

    Handler reExamHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "查询失败，请稍后重试", Toast.LENGTH_SHORT).show();
            } else {

                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                if (Util.replace(json).equals("false")) {
                    Toast.makeText(getActivity(), "还未公布补考安排", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), ReExamActivity.class);
                intent.putExtra("reExamJson", json);
                startActivity(intent);
            }
        }
    };

    private void findExam() {
        final String studentId = user.getString("StudentId");
        if (TextUtils.isEmpty(studentId) || studentId.equals("null")) {
            Toast.makeText(getActivity(), "请先在个人信息页补全信息", Toast.LENGTH_SHORT).show();
            return;
        }

        waitWin.showWait();
        if (studentId.length() < 15) {
            Map<String, String> map = new ArrayMap<>();
            map.put("studentId", studentId);//设置get参数
            new InternetUtil(classIdHandler, InternetUtil.URL_OLD_CLASSID, map).get();//传入参数
        } else {
            //新生考试安排
            if (TextUtils.isEmpty(AVUser.getCurrentUser().getString("JwcPwd"))) {
                Toast.makeText(getActivity(), "请先在个人信息页填写教务系统密码", Toast.LENGTH_LONG).show();
            } else {
                Map<String, String> map = new ArrayMap<>();
                map.put("UserName", studentId);//设置get参数
                map.put("Password", AVUser.getCurrentUser().getString("JwcPwd"));
                new InternetUtil(classIdHandler, InternetUtil.URL_EXAM, map).get();
            }
        }

    }

    Handler classIdHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "查询失败，请稍后重试", Toast.LENGTH_SHORT).show();

                if (waitWin != null)
                    waitWin.dismissWait();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                if (AVUser.getCurrentUser().getString("StudentId").length() < 15) {
                    json = Util.replace(json);
                    if (TextUtils.isEmpty(json) || Util.replace(json).equals("null")) {
                        Toast.makeText(getActivity(), "请检查个人信息中学号是否有误", Toast.LENGTH_SHORT).show();

                        if (waitWin != null)
                            waitWin.dismissWait();
                    } else {
                        String finalJson = json;
                        Map<String, String> map = new ArrayMap<>();
                        map.put("class_id", Util.replace(finalJson));//设置get参数
                        new InternetUtil(examHandle, InternetUtil.URL_OLD_EXAM, map).get();//传入参数
                    }
                } else {
                    if (waitWin != null)
                        waitWin.dismissWait();
                    json = Util.replace(json);
                    if ("error".equals(json)) {
                        Toast.makeText(getActivity(), "查询失败，请稍候重试", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), ExamActivity.class);
                        intent.putExtra("examJson", json);
                        startActivity(intent);
                    }
                }
            }
        }
    };

    Handler examHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "查询失败，请稍后重试", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");

                Intent intent = new Intent(getActivity(), ExamActivity.class);
                intent.putExtra("examJson", json);
                startActivity(intent);
            }
        }
    };

    private void findGrade() {
        final String studentId = user.getString("StudentId");
        if (TextUtils.isEmpty(studentId) || Util.replace(studentId).equals("null")) {
            Toast.makeText(getActivity(), "请先在个人信息页补全信息", Toast.LENGTH_SHORT).show();
            return;
        }
        waitWin.showWait();
        Map<String, String> map = new ArrayMap<>();
        map.put("uid", studentId);//设置get参数
        if (studentId.length() >= 15)
            if (!TextUtils.isEmpty(AVUser.getCurrentUser().getString("JwcPwd")))
                map.put("pwd", AVUser.getCurrentUser().getString("JwcPwd"));
        int[] time = Util.getSystemTime();
        if (time[1] == 1) {
            time[0]--;
            time[1]++;
        } else {
            time[1]--;
        }
        map.put("term", time[0] + "." + time[1]);//设置get参数
//        map.put("term", "2015.2");
        new InternetUtil(scoreHandler, InternetUtil.URL_SCORE, map).get();//传入参数
    }

    Handler scoreHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (waitWin != null)
                waitWin.dismissWait();
            if (msg.what == 0) {
                Toast.makeText(getActivity(), "查询失败，请稍后重试", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = (Bundle) msg.obj;
                String json = bundle.getString("Json");
                Intent intent = new Intent(getActivity(), ScoreActivity.class);
                intent.putExtra("scoreJson", json);
                startActivity(intent);
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
    }
}
