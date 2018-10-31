package com.gx.worings.Util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gx.worings.constants.cons;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作数据库类
 */
public class PimDao {

    /**
     * 查询列表
     *
     * @return
     */
    public static JSONArray list(String tab, String where, int page) {
        String result = "";
        try {
            Map<String, String> params = new HashMap<>();
            int count = PimDao.selectForInteger(tab, where);
            int startIndex = (page - 1) * cons.limit + 1;
            int endIndex = page * cons.limit;
            params.put("startIndex", String.valueOf(startIndex));
            params.put("endIndex", String.valueOf(endIndex));
            params.put("where", where);
            params.put("type", cons.selectList);
            URLConnection conn = HttpRequestUtil.sendPostRequest(cons.SQL_URL, params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            System.out.println(result);
            return JSONArray.parseArray(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除数据
     *
     * @return
     */
    public static int delete(String tab, String where) {
        String result;
        Map<String, String> params = new HashMap<>();
        params.put("tab", tab);
        params.put("where", where);
        params.put("type", cons.delete);
        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest(cons.SQL_URL, params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            JSONArray jsonObject = (JSONArray) JSONArray.parse(result);
            JSONObject j = (JSONObject) jsonObject.get(0);
            if (null == j){
                return 0;
            }
            return Integer.parseInt(j.getString("count"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 插入数据
     *
     * @return
     */
    public static int insert(String tab, String values) {
        String result;
        Map<String, String> params = new HashMap<>();
        params.put("tab", tab);
        params.put("values", values);
        params.put("type", cons.insert);
        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest(cons.SQL_URL, params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            JSONArray jsonObject = (JSONArray) JSONArray.parse(result);
            JSONObject j = (JSONObject) jsonObject.get(0);
            if (null == j){
                return 0;
            }
            return Integer.parseInt(j.getString("count"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 查询条数
     *
     * @return
     */
    public static int selectForInteger(String tab, String where) {
        String result;
        Map<String, String> params = new HashMap<>();
        params.put("tab", tab);
        params.put("where", where);
        params.put("type", cons.count);
        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest(cons.SQL_URL, params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            JSONArray jsonObject = (JSONArray) JSONArray.parse(result);
            JSONObject j = (JSONObject) jsonObject.get(0);
            if (null == j){
                return 0;
            }
            return Integer.parseInt(j.getString("count"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 删除数据
     *
     * @return
     */
    public static JSONObject selectOne(String tab, String where) {
        Map<String, String> params = new HashMap<>();
        int count = PimDao.selectForInteger(tab, where);
        params.put("where", where);
        params.put("type", cons.selectOne);
        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest(cons.SQL_URL, params, null);
            String result = HttpRequestUtil.readString(conn.getInputStream());
            System.out.println(result);
            return JSONObject.parseObject(result);
        }catch (Exception es){
            es.printStackTrace();
            return null;
        }
    }

}
