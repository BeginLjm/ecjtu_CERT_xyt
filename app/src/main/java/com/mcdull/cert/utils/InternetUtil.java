package com.mcdull.cert.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mcdull on 15/7/23.
 */
public class InternetUtil {

    public static void main(String[] args) {
        System.out.println(BATE_URL);
    }

    public static String BATE_URL = "";

    //新接口
    public static String URL_MAP = "";
    public static String URL_IMG = "";
    public static String URL_SCORE = "";
    public static String URL_EXAM = "";

    //待更新及修复
    public static String URL_COURSE = "";

    //报修接口
    public static String URL_REPAIR_GETORDER = "";
    public static String URL_REPAIR_STATUS = "";
    public static String URL_REPAIR = "";
    public static String URL_REPAIR_TIME = "";

    public static String URL_LIBRARY = "";
    public static String URL_ECARD = "";//
    public static String URL_CET = "";

    public static String URL_ENROLL = "";
    public static String URL_EMS = "";

    public static String URL_OLD_EXAM = "";
    public static String URL_OLD_CLASSLIST = "";
    public static String URL_OLD_REEXAM = "";

    //待迁移
    public static String URL_OLD_CLASSID = "";
    public static String URL_GETSTUDENTINFO = "";

    //失物招领接口 已无效
    public static String URL_LOST_SEARCHLOST = "";
    public static String URL_LOST_GETINFO = "";
    public static String URL_LOST = "";

    static {
        File file = new File("/data/data/com.mcdull.cert/files/url");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                String name = s;
                String url = bufferedReader.readLine();
                Class aClass = Class.forName("com.mcdull.cert.utils.InternetUtil");
                try {
                    Field field = aClass.getField(name);
                    field.set(aClass, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap icon;
    private List<Bitmap> bitmaps;
    private Map<String, String> map;
    private String url;
    private Handler handler;

    public InternetUtil(Handler handler, String url) {
        this.handler = handler;
        this.url = url;
    }

    public InternetUtil(Handler handler, String url, Map<String, String> map) {
        this.handler = handler;
        this.url = url;
        this.map = map;
    }

    public InternetUtil(Handler handler, String url, Map<String, String> map, List<Bitmap> bitmaps, Bitmap icon) {
        this.handler = handler;
        this.url = url;
        this.map = map;
        this.bitmaps = bitmaps;
        this.icon = icon;
    }

    /**
     * 判断是否连接网络
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public void post() {
        if (handler == null || url == null || url.equals("")) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = "";
                if (bitmaps != null && bitmaps.size() != 0) {
                    s = postJsonForBitmap();
                } else {
                    s = getJson();
                }
                Message msg = new Message();
                if (TextUtils.isEmpty(s)) {
                    msg.what = 0;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("Json", s);
                    msg.obj = bundle;
                    msg.what = 1;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    private String postJsonForBitmap() {
        URL realUrl = null;
        DataOutputStream out = null;
        String PREFIX = "----WebKitFormBoundarympitttHASlTkwZBa";
        String LINE_END = "\r\n";
        try {
            realUrl = new URL(url);
            HttpURLConnection huc = (HttpURLConnection) realUrl.openConnection();

            huc.setRequestProperty("accept", "*/*");
            huc.setRequestProperty("connection", "Keep-Alive");
            huc.setRequestProperty("Charset", "utf-8"); // 设置编码
            huc.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            huc.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + PREFIX);

            // 发送POST请求必须设置如下两行
            huc.setDoOutput(true);
            huc.setDoInput(true);

            // 获取URLConnection对象对应的输出流
            out = new DataOutputStream(huc.getOutputStream());


            for (Bitmap b : bitmaps) {
                StringBuffer sb = new StringBuffer();
                sb.append("--" + PREFIX);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"upload_file[]\"; filename=\""
                        + "abc.png" + "\"" + LINE_END);
                sb.append("Content-Type: image/png" + LINE_END);
                sb.append(LINE_END);
                out.write(sb.toString().getBytes());
                out.write(Util.Bitmap2Bytes(b));
                out.write(LINE_END.getBytes());
            }
            for (String s : map.keySet()) {
                StringBuffer sb = new StringBuffer();
                sb.append("--" + PREFIX);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"" + s + "\"" + LINE_END);
                sb.append(LINE_END);
                out.write(sb.toString().getBytes());
                out.write(map.get(s).getBytes());
                out.write(LINE_END.getBytes());
            }

            StringBuffer sb = new StringBuffer();
            sb.append("--" + PREFIX);
            sb.append(LINE_END);
            sb.append("Content-Disposition: form-data; name=\"photo_file\"; filename=\""
                    + "abc.png" + "\"" + LINE_END);
            sb.append("Content-Type: image/png" + LINE_END);
            sb.append(LINE_END);
            out.write(sb.toString().getBytes());
            out.write(Util.Bitmap2Bytes(icon));
            out.write(LINE_END.getBytes());

            byte[] end_data = ("--" + PREFIX + LINE_END).getBytes();
            out.write(end_data);

            // flush输出流的缓冲
            out.flush();

            String s = huc.getOutputStream().toString();
            Log.d("Post", s);

            // 显示响应
            String json = null;
            int statusCode = huc.getResponseCode();
            if (statusCode == 200) {
                InputStream is = huc.getInputStream();
                json = Util.convertStreamToString(is);
                return json;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void get() {
        if (handler == null || url == null || url.equals("")) {
            return;
        }

        Observable.just("")
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return getJson();
                    }
                })
                .observeOn(Schedulers.newThread())
                .map(new Func1<String, Message>() {
                    @Override
                    public Message call(String s) {
                        Message message = new Message();
                        if (TextUtils.isEmpty(s)) {
                            message.what = 0;
                        } else {
                            message.what = 1;
                            Bundle bundle = new Bundle();
                            bundle.putString("Json", s);
                            message.obj = bundle;
                        }
                        return message;
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {
                        handler.sendMessage(message);
                    }
                });
    }

    private String getJson() {
        String URL = url;

        try {
            if (map != null && map.size() != 0) {
                URL = URL + "?";
                for (String key : map.keySet()) {
                    String v = URLEncoder.encode(map.get(key), "UTF-8");
                    URL = URL + key + "=" + v + "&";
                }
            }
        } catch (IOException e) {
            return null;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String s = response.body().string();
                return s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
