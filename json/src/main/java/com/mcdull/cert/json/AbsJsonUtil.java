package com.mcdull.cert.json;

import java.util.List;
import java.util.Map;

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

}
