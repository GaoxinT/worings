package com.gx.worings.activitys;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.gx.worings.Entry.Uesr;
import com.gx.worings.R;
import com.gx.worings.Util.HttpRequestUtil;
import com.gx.worings.Util.MySqlUtil;
import com.gx.worings.Util.PimDao;
import com.gx.worings.Util.UtilToos;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.gx.worings.constants.cons.sql;

public class LoginActivity extends BaseActivity {

    private CheckBox checkBox = null;
    private Button btnSignin = null;
    private EditText accountEt = null;
    private EditText pwdEt = null;
    private TextView tv_register = null;
    private ProgressBar progressBar = null;

    private Button subBtn_regidit = null;
    private EditText edit_Phone = null;
    private EditText pwdEt_regedit = null;

    private LinearLayout loginPanel = null;
    private LinearLayout regeditPanel = null;

    private SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        setListeners();
        init();
    }

    @Override
    public void findViews() {


        checkBox = (CheckBox) findViewById(R.id.remonberPwd);
        btnSignin = (Button) findViewById(R.id.subBtn);
        accountEt = (EditText) findViewById(R.id.accountEt);
        pwdEt = (EditText) findViewById(R.id.pwdEt);
        progressBar = (ProgressBar) findViewById(R.id.progressBarlogin);
        tv_register = (TextView) findViewById(R.id.tv_register);

        subBtn_regidit = (Button) findViewById(R.id.subBtn_regidit);
        edit_Phone = (EditText) findViewById(R.id.edit_Phone);
        pwdEt_regedit = (EditText) findViewById(R.id.pwdEt_regedit);

        loginPanel = (LinearLayout) findViewById(R.id.loginPanel);
        regeditPanel = (LinearLayout) findViewById(R.id.regeditPanel);
    }

    @Override
    public void init() {
        checkBox.setChecked(true);
        mSp = getSharedPreferences("login", MODE_PRIVATE);
        String id = mSp.getString("id", "");
        String password = mSp.getString("password", "");
        accountEt.setText(id);
        pwdEt.setText(password);
    }

    @Override
    public void setListeners() {
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = accountEt.getText().toString();
                String pwd = pwdEt.getText().toString();
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", id);
                map.put("password", pwd);
                progressBar.setVisibility(View.VISIBLE);
                new Login().execute(map);
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginPanel.setVisibility(View.GONE);
                regeditPanel.setVisibility(View.VISIBLE);
            }
        });

        subBtn_regidit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Regeidt mRegeidt = new Regeidt();
                mRegeidt.execute(edit_Phone.getText().toString(), pwdEt_regedit.getText().toString());
            }
        });
    }

    class Regeidt extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... obj) {

            try {
                Map map = new HashMap();
                String userID = UUID.randomUUID().toString().replace("-", "");
                map.put("USER_ID", userID);
                map.put("USER_NAME", obj[0]);
                map.put("USER_ACCOUNT", obj[0]);
                map.put("USER_STATE", "1");
                map.put("USER_PHONE", obj[0]);
                map.put("USER_PWD", MD5.digest(obj[1] + userID));
                map.put("USER_LAST_LOGIN", UtilToos.getTimeToString(1));
                PimDao.insert("t_sys_user", map);
                return "0";
            } catch (Exception e) {
                e.printStackTrace();
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            mSp = getSharedPreferences("login", MODE_PRIVATE);
            if (s == "0") {

                SharedPreferences.Editor editor = mSp.edit(); //会生成一个Editor类型的引用变量
                editor.putString("id", edit_Phone.getText().toString());
                editor.putString("password", "");
                editor.commit();

                Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                loginPanel.setVisibility(View.VISIBLE);
                regeditPanel.setVisibility(View.GONE);
            } else {
                Toast.makeText(getApplicationContext(), "注册失败！手机号已被注册！", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(s);
        }
    }

    /**
     * 登入
     */
    class Login extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... params) {
            Map<String, String> map = (Map<String, String>) params[0];
            try {
                Map<String, String> result = PimDao.selectOne("t_sys_user", "USER_ACCOUNT = '" + map.get("username") + "'",null);
                if (result.size() != 0) {
                    if (result.get("USER_PWD").equals(MD5.digest(pwdEt.getText().toString() + result.get("USER_ID")))) {
                        Uesr.getInstance().setUserId(result.get("USER_ID"));
                        Uesr.getInstance().setUserPwd(result.get("USER_PWD"));
                        Uesr.getInstance().setUserSex(result.get("USER_SEX"));
                        Uesr.getInstance().setUserName(result.get("USER_NAME"));
                        Uesr.getInstance().setUserPhone(result.get("USER_PHONE"));
                        Uesr.getInstance().setUserLastLogin(result.get("USER_LAST_LOGIN"));
                        return "1";
                    } else {
                        return "2";
                    }
                } else {
                    return "3";
                }
            } catch (Exception r) {
                return "4";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("1")) {
                mSp = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = mSp.edit(); //会生成一个Editor类型的引用变量
                editor.putString("id", accountEt.getText().toString());
                editor.putString("password", pwdEt.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "登入成功！", Toast.LENGTH_LONG).show();
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "信息错误！登入失败！", Toast.LENGTH_LONG).show();
            }
        }
    }
}

class MD5 {

    public static String digest(String str) {
        StringBuffer sb = new StringBuffer();

        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.update(str.getBytes("ISO8859-1"));
            byte[] array = md5.digest();
            for (int x = 0; x < 16; x++) {
                if ((array[x] & 0xff) < 0x10)
                    sb.append("0");

                sb.append(Long.toString(array[x] & 0xff, 16));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return sb.toString();
    }
}

