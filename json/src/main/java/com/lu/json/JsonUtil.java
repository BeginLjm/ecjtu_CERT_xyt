package com.lu.json;

import android.support.v4.util.ArrayMap;

import com.mcdull.cert.json.AbsJsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Begin on 16/8/4.
 */
public class JsonUtil extends AbsJsonUtil {
    /**
     * 四六级 json 解析
     *
     * @param s
     * @return
     * @throws JSONException
     */
    @Override
    public Map<String, String> cetParseJson(String s) throws JSONException {
        JSONObject jsonObject;
        Map<String, String> cetSearchMap = new HashMap<>();
        jsonObject = new JSONObject(s);
        String name = jsonObject.getString("name");
        cetSearchMap.put("name", name);
        String school = jsonObject.getString("school");
        cetSearchMap.put("school", school);
        String type = jsonObject.getString("type");
        cetSearchMap.put("type", type);
        String ticket = jsonObject.getString("ticket");
        cetSearchMap.put("ticket", ticket);
        String date = jsonObject.getString("date");
        cetSearchMap.put("date", date);
        String total = jsonObject.getString("total");
        cetSearchMap.put("total", total);
        String listening = jsonObject.getString("listening");
        cetSearchMap.put("listening", listening);
        String reading = jsonObject.getString("reading");
        cetSearchMap.put("reading", reading);
        String writing = jsonObject.getString("writing");
        cetSearchMap.put("writing", writing);
        return cetSearchMap;
    }

    @Override
    public List<List<String>> scoreParseJson(String s) throws JSONException {
        List<List<String>> lists = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(s);
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject object = (JSONObject) data.get(i);
            List<String> list = new LinkedList<>();
            list.add(object.getString("course"));
            list.add(object.getString("score"));
            list.add(object.getString("credit"));
            lists.add(list);
        }
        return lists;
    }

    @Override
    public Map<String, String> detailsParseJson(String s) throws JSONException {
        HashMap<String, String> map = new HashMap<>();
        JSONObject jsonObject = (new JSONObject(s)).getJSONObject("data");
        String pictures = jsonObject.getString("pictures");
        String description = jsonObject.getString("description");
        map.put("pictures", pictures);
        map.put("description", description);
        return map;
    }

    @Override
    public Map<String, Object> eCardParseJson(String s) throws Exception {
        JSONObject jsonObject;
        Map<String, Object> eCardMap = new ArrayMap<>();
        jsonObject = new JSONObject(s);
        String name = jsonObject.getString("name");
        eCardMap.put("name", name);
        String eCardId = jsonObject.getString("cardid");
        eCardMap.put("ecardid", eCardId);
        String balance = jsonObject.getString("balance");
        eCardMap.put("balance", balance);
        String total = jsonObject.getString("total");
        eCardMap.put("total", total);

        List<Map<String, String>> list = new ArrayList<>();
        if ("".equals(jsonObject.getString("times")) || "0".equals(jsonObject.getString("times"))) {
            eCardMap.put("list", list);
        } else {
            int times = Integer.parseInt(jsonObject.getString("times"));
            JSONObject jsonObject2 = jsonObject.getJSONObject("tradedata");
            for (int i = 1; i <= times; i++) {
                Map<String, String> itemMap = new ArrayMap<>();
                JSONObject item = jsonObject2.getJSONObject(i + "");
                itemMap.put("time", item.getString("date"));
                itemMap.put("SystemName", item.getString("address"));
                itemMap.put("account", item.getString("spent"));
                list.add(itemMap);
            }
            eCardMap.put("list", list);
        }
        return eCardMap;
    }

    @Override
    public List<List<String>> examParseJson(String s) throws Exception {
        List<List<String>> lists = new LinkedList<>();
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                List<String> list = new LinkedList<>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                list.add(jsonObject.getString("name"));
                list.add("");
                list.add("");
                list.add(jsonObject.getString("time"));
                list.add(jsonObject.getString("location"));
                lists.add(list);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lists;
    }

    @Override
    public List<Map<String, String>> classParseJson(String s) throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(s);
        for (int i = 0; i < jsonArray.length(); i++) {
            Map<String, String> itemMap = new ArrayMap<>();
            JSONObject item = jsonArray.getJSONObject(i);
            itemMap.put("class_id", item.getString("class_id"));
            itemMap.put("class_name", item.getString("class_name"));
            list.add(itemMap);
        }
        return list;
    }

    @Override
    public List<String> libraryParseJson(String s) throws Exception {
        List<String> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getJSONObject(i).getString("BZ"));
        }
        return list;
    }

    @Override
    public Map<String, Object> reExamJsonParseJson(String reExamJson) throws Exception {
        Map<String, Object> map = new ArrayMap<>();
        JSONObject jsonObject = new JSONObject(reExamJson);
        map.put("studentName", jsonObject.getString("student_name"));
        map.put("studentId", jsonObject.getString("student_id"));
        map.put("studentClass", jsonObject.getString("student_class"));

        String reExamString = jsonObject.getString("arrange");
        if (reExamString.equals("false")) {
            return map;
        } else {
            List<List<String>> reExamList = new ArrayList<>();

            String[] reExams = reExamString.split("\\|");

            for (String reExam : reExams) {
                String[] reExamItem;
                reExamItem = reExam.split(";");
                List<String> item = new ArrayList<>();
                Collections.addAll(item, reExamItem);
                reExamList.add(item);
            }
            map.put("list", reExamList);
        }
        return map;
    }

    @Override
    public List<Map<String, String>> repairParseJson(String s) throws Exception {
        JSONObject jsonObject = new JSONObject(s);
        JSONObject json = jsonObject.getJSONObject("json");
        JSONArray numArray = json.getJSONArray("num");
        JSONArray dateArray = json.getJSONArray("date");
        List<Map<String, String>> list = new LinkedList<>();
        for (int i = 0; i < numArray.length(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(dateArray.getString(i), numArray.getString(i));
            list.add(map);
        }
        return list;
    }

    @Override
    public Map<String, String> homeECardParseJson(String s) throws Exception {
        JSONObject jsonObject = new JSONObject(s);
        String times = jsonObject.getString("times");
        double num = 0;
        JSONObject tradedata = jsonObject.getJSONObject("tradedata");
        int time = Integer.parseInt(times);
        for (int i = 1; i <= time; i++) {
            JSONObject item = tradedata.getJSONObject(i + "");
            double a = Double.parseDouble(item.getString("spent"));
            if (a < 0) {
                num = num - a;
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("times", times);
        map.put("num", num + "");
        return map;
    }

}
