package com.gx.worings;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gx.worings.Util.HttpRequestUtil;

import org.junit.Test;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        //System.out.print("sadasdasd");
//        String s = "{\"sd\":{\"das\":\"qd\",\"das1\":\"qd2\"}}";
        Map m = new HashMap();
        List<Map> list = new ArrayList();

        m.put("tab","t_location");
        m.put("where","id = '52'");
        m.put("type","selectOne");
        //m.put("startIndex","1");
        //m.put("endIndex","10");

        list.add(m);
        System.out.println(mapToInsertSql(m));
        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest(cons.SQL_URL_LOCAL,m,null);
            String s = HttpRequestUtil.readString(conn.getInputStream());
            JSONArray jsonObject = (JSONArray) JSONArray.parse(s);
            JSONObject j = (JSONObject) jsonObject.get(0);
            System.out.println(j.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 插入数据
     *
     * @return
     */
    public static String mapToInsertSql(Map<String, Object> params) {
        String Field = "";
        String values = "";
        try {
            if (null != params) {
                Boolean isFrist = true;
                for (String key : params.keySet()) {
                    if (isFrist) {
                        Field += "(" + key;
                        values += "('" + params.get(key) + "'";
                        isFrist = false;
                    }else {
                        Field += "," + key;
                        values += ",'" + params.get(key) + "'";
                    }
                }
            }
            Field +=  ")";
            values +=  ")";
            return  Field + " values " + values;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}