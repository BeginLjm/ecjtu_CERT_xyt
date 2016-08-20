package com.mcdull.cert.json;

import android.content.Context;

import java.util.List;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * Created by Begin on 16/8/4.
 */
public abstract class AbsJsonUtil {
    public abstract Map<String, String> cetParseJson(String s) throws Exception;

    public abstract List<List<String>> scoreParseJson(String s) throws Exception;

    public abstract Map<String, String> detailsParseJson(String s) throws Exception;

    public abstract Map<String, Object> eCardParseJson(String s) throws Exception;

    public abstract List<List<String>> examParseJson(String s) throws Exception;

    public abstract List<Map<String, String>> classParseJson(String s) throws Exception;

    public abstract List<String> libraryParseJson(String s) throws Exception;

    public abstract Map<String, Object> reExamJsonParseJson(String s) throws Exception;

    public abstract List<Map<String, String>> repairParseJson(String s) throws Exception;

    public abstract Map<String, String> homeECardParseJson(String s) throws Exception;

    public static AbsJsonUtil getJsonUtil(Context context) throws Exception {
        DexClassLoader dexClassLoader = new DexClassLoader("/data/data/com.mcdull.cert/files/json.jar", context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), null, context.getClassLoader());
        Class<?> myClass1 = dexClassLoader.loadClass("com.lu.json.JsonUtil");
        return (AbsJsonUtil) myClass1.newInstance();
    }
}
