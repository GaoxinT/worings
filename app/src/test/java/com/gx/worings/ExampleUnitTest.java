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
        m.put("where","phone = 'MeizuM6 Note'");
        m.put("type","count");
        m.put("startIndex","1");
        m.put("endIndex","10");

        list.add(m);
//        try {
//            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(m);
//            String name = jsonObject.getString("das");
//            String age = jsonObject.getString("das1");
//            System.out.print(name);
//            System.out.print(age);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            URLConnection conn = HttpRequestUtil.sendPostRequest("http://183.215.2.237/woring/appexecute!ExecuteSql.action",m,null);
            String s = HttpRequestUtil.readString(conn.getInputStream());
            JSONArray jsonObject = (JSONArray) JSONArray.parse(s);
            JSONObject j = (JSONObject) jsonObject.get(0);
            System.out.println(j.getString("count"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}