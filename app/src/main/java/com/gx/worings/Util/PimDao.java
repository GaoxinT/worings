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
            System.out.println(System.currentTimeMillis());
            Map<String, String> params = new HashMap<>();
            int count = PimDao.selectForInteger(tab, where);
            int startIndex = (page - 1) * cons.limit + 1;
            int endIndex = page * cons.limit;
            params.put("startIndex", String.valueOf(startIndex));
            params.put("endIndex", String.valueOf(endIndex));
            params.put("where", where);
            params.put("type", cons.insert);
            URLConnection conn = HttpRequestUtil.sendPostRequest("http://" + cons.HOST + "/woring/appexecute!ExecuteSql.action", params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            System.out.println(result);
            System.out.println(System.currentTimeMillis());
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
        String result = "";
        Map<String, String> params = new HashMap<>();
        try {
            System.out.println(System.currentTimeMillis());
            URLConnection conn = HttpRequestUtil.sendPostRequest("http://" + cons.HOST + "/woring/appexecute!ExecuteSql.action", params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            System.out.println(result);
            System.out.println(System.currentTimeMillis());
            return 0;
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
    public static int insert(String tab, String where) {
        return 0;
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
        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest("http://" + cons.HOST + "/woring/appexecute!ExecuteSql.action", params, null);
            result = HttpRequestUtil.readString(conn.getInputStream());
            JSONArray jsonObject = (JSONArray) JSONArray.parse(result);
            JSONObject j = (JSONObject) jsonObject.get(0);
            if (null == j){

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
    public static JSONObject selectOne() {
        return null;
    }

}
