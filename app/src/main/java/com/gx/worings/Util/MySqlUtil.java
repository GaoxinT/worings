package com.gx.worings.Util;

/**
 * Created by GX on 2017/1/14.
 */

import android.util.Log;

import com.gx.worings.Entry.Dairy_Context;
import com.gx.worings.Entry.Location;
import com.gx.worings.Entry.MessageInfo;
import com.gx.worings.Entry.Uesr;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlUtil {


    private static String REMOTE_IP = "183.215.2.237:50890";
    private static String url = "jdbc:mysql://" + REMOTE_IP + "/woring?useUnicode=true&characterEncoding=utf-8";
    private static String user = "root";
    private static String password = "qqwq1121";
    private static Statement statement = null;
    private static ResultSet result = null;
    private static Connection conn = null;
    private static PreparedStatement ps = null;

    public static Connection openConnection() {

        try {
            String DRIVER_NAME = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            conn = null;
            Log.v("tag", e.toString());
            Log.v("tag", "-----------------");
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * 下载数据
     *
     * @param sql
     */
    public static List<Dairy_Context> queryAllDownlod(String sql) {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }
        List<Dairy_Context> dairys = new ArrayList<>();
        Dairy_Context dairy;

        try {
            Log.v("sql", "--------" + sql);
            statement = conn.createStatement();
            result = statement.executeQuery(sql);
            while (result.next()) {
                String _id = result.getString("dairy_id");
                String writedate = result.getTimestamp("writedate").toString();
                String title = result.getString("title");
                String context = result.getString("context");
                dairy = new Dairy_Context(_id, "", writedate, title, context);
                Log.v("download", dairy.toString());
                dairys.add(dairy);
            }
            return dairys;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException sqle) {

            }
        }
        return null;
    }

    /**
     * 下载数据
     *
     * @param sql
     */
    public static JSONArray queryBack(String sql) {
        Connection conn = openConnection();
        List<Map> list = new ArrayList<Map>();
        if (conn == null) {
            return null;
        }
        try {
            ps = conn.prepareStatement(sql);
            result = ps.executeQuery();//执行语句，得到结果集
            while (result.next()) {
                ResultSetMetaData resultSetMetaData = result.getMetaData();
                int length = resultSetMetaData.getColumnCount();
                Map<String, String> map = new HashMap<>();
                Map<String, Object> map1 = new HashMap<>();
                for (int i = 1; i <= length; i++) {
                    map.put(resultSetMetaData.getColumnLabel(i), result.getString(i));
                    //map1.put(result.getString("id"), map);//id是主键
                }
                list.add(map);
            }
            return new JSONArray(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException sqle) {

            }
        }
        return null;
    }

    /**
     * 执行sql查询
     *
     * @param sql
     * @return
     */
    public static boolean execSQL(String sql) {
        boolean execResult = false;
        Connection conn = openConnection();
        if (conn == null) {
            return execResult;
        }

        try {
            statement = conn.createStatement();
            if (statement != null) {
                Log.v("sql", "--------" + sql);
                statement.execute(sql);
                execResult = true;
            }
        } catch (SQLException e) {
            execResult = false;
            e.printStackTrace();
        }
        return execResult;
    }

    /**
     * 执行sql查询
     *
     * @param sql
     * @return
     */
    public static boolean execSQLBathh(String sql) {
        boolean execResult = false;
        Connection conn = openConnection();
        if (conn == null) {
            return execResult;
        }
        String[] sqls = sql.split(";");
        try {
            statement = conn.createStatement();
            if (statement != null) {
                for (String str : sqls) {
                    Log.v("sql", "--------" + str);
                    statement.addBatch(str);
                }
                statement.executeBatch();
                execResult = true;
            }
        } catch (SQLException e) {
            execResult = false;
            e.printStackTrace();
        }

        return execResult;
    }

    /**
     * 插入信息
     * @param sql
     * @param messageInfos
     * @return
     */
    public static boolean execSQLBathh1(String sql, List<MessageInfo> messageInfos) {

        Connection conn = openConnection();
        if (conn == null) {
            return false;
        }
        try {
            PreparedStatement mPreparedStatement = conn.prepareStatement(sql);
            if (mPreparedStatement != null) {
                for (MessageInfo messageInfo : messageInfos) {
                    mPreparedStatement.setString(1, messageInfo.getName());
                    mPreparedStatement.setString(2, messageInfo.getSmsContent());
                    mPreparedStatement.setString(3, messageInfo.getSmsDate());
                    mPreparedStatement.addBatch();
                }
                mPreparedStatement.executeBatch();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }

    public static boolean execSQLBathh2(String sql, List<HashMap<String, String>> list) {
        Connection conn = openConnection();
        if (conn == null) {
            return false;
        }
        try {
            PreparedStatement mPreparedStatement = conn.prepareStatement(sql);
            if (mPreparedStatement != null) {
                for (HashMap<String, String> map : list) {
                    mPreparedStatement.setString(1, map.get("name"));
                    mPreparedStatement.setString(2, map.get("phone"));
                    mPreparedStatement.addBatch();
                }
                mPreparedStatement.executeBatch();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
