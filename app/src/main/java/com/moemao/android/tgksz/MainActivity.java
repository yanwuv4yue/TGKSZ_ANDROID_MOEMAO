package com.moemao.android.tgksz;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MOEMAO = "moemao";

    private static final String SAVE_DATA_PATH = "/data/data/jp.co.sumzap.pj0007/shared_prefs/jp.co.sumzap.pj0007.v2.playerprefs.xml";

    private static final String SAVE_DATA_FOLDER = "/data/data/jp.co.sumzap.pj0007/shared_prefs";

    private EditText info;

    private EditText accountNo;

    private EditText transAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        this.info = findViewById(R.id.info);

        this.accountNo = findViewById(R.id.accountNo);

        this.transAccount = findViewById(R.id.transAccount);

        // 绑定按钮事件
        this.bindButtonClickAction();
    }

    private void bindButtonClickAction()
    {
        // 删除按钮
        Button btnDelete = findViewById(R.id.btn_konosuna_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                File file = new File(SAVE_DATA_PATH);

                // 申请root权限
                upgradeRootPermission(SAVE_DATA_FOLDER);

                if (file.exists())
                {
                    boolean resule = file.delete();
                    Log.d(TAG_MOEMAO, "delete jp.co.sumzap.pj0007.v2.playerprefs.xml is : " + String.valueOf(resule));
                    info.setText("delete jp.co.sumzap.pj0007.v2.playerprefs.xml is : " + String.valueOf(resule));
                }
                else
                {
                    Log.d(TAG_MOEMAO, "file jp.co.sumzap.pj0007.v2.playerprefs.xml not exist!");
                    info.setText("file jp.co.sumzap.pj0007.v2.playerprefs.xml not exist!");
                }
            }
        });

        // 拉取按钮
        Button btnPull = findViewById(R.id.btn_konosuna_pull);
        btnPull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String url = "gam/account/dailyLogin/selectAccount";

                Map params = new HashMap<String, String>();
                params.put("gameCode", "konosuba");

                String accountNoText = accountNo.getText().toString();

                if (null != accountNoText && !"".equals(accountNoText) && accountNoText.length() > 0)
                {
                    params.put("accountNo", accountNoText);
                }

                //params.put("type", "1");

                RequestManager.getInstance(getApplicationContext()).requestAsyn(url, RequestManager.TYPE_GET, params, new ReqCallBack(){
                    @Override
                    public void onReqSuccess(Object result)
                    {
                        Log.d(TAG_MOEMAO, result.toString());
                        JSONObject resultJson = JSONObject.parseObject(result.toString());
                        info.setText("获取服务器存档信息..." + resultJson.getString("msg"));

                        if (null == resultJson.getJSONObject("data") || "".equals(resultJson.getJSONObject("data")))
                        {
                            info.append("\n获取账号数据异常");
                            return;
                        }
                        else
                        {
                            info.append("\n当前账号：" + resultJson.getJSONObject("data").getString("accountNo"));
                        }

                        String fileContent = resultJson.getJSONObject("data").getString("fileContent");

                        File file = new File(SAVE_DATA_PATH);

                        // 申请root权限
                        upgradeRootPermission(SAVE_DATA_FOLDER);

                        if (file.exists())
                        {
                            boolean resule = file.delete();
                            Log.d(TAG_MOEMAO, "delete jp.co.sumzap.pj0007.v2.playerprefs.xml is : " + String.valueOf(resule));
                            info.append("\n删除当前存档...");
                        }

                        try
                        {
                            // 创建空文件
                            file.createNewFile();

                            // 申请root权限
                            upgradeRootPermission(SAVE_DATA_PATH);

                            // 写入内容
                            FileWriter fileWriter = new FileWriter(file);
                            fileWriter.write(fileContent);
                            fileWriter.flush();
                            fileWriter.close();

                            info.append("\n写入存档...完成，请登录游戏查看！");
                        }
                        catch (Exception e)
                        {
                            Log.d(TAG_MOEMAO, e.getMessage());
                        }
                    }
                    @Override
                    public void onReqFailed(String errorMsg)
                    {

                    }
                });

            }
        });

        // 读取按钮
        Button btnRead = findViewById(R.id.btn_konosuna_read);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                File file = new File(SAVE_DATA_PATH);

                // 申请root权限
                upgradeRootPermission(SAVE_DATA_PATH);

                if (file.exists())
                {
                    int length = (int) file.length();
                    byte[] buff = new byte[length];

                    try
                    {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        fileInputStream.read(buff);
                        fileInputStream.close();

                        String fileContent = new String(buff);
                        info.setText(fileContent);
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG_MOEMAO, e.getMessage());
                    }
                }
                else
                {
                    Log.d(TAG_MOEMAO, "file jp.co.sumzap.pj0007.v2.playerprefs.xml not exist!");
                    info.setText("file jp.co.sumzap.pj0007.v2.playerprefs.xml not exist!");
                }
            }
        });

        // 上传按钮
        Button btnUpload = findViewById(R.id.btn_konosuna_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                File file = new File(SAVE_DATA_PATH);

                // 申请root权限
                upgradeRootPermission(SAVE_DATA_PATH);

                if (file.exists())
                {
                    int length = (int) file.length();
                    byte[] buff = new byte[length];

                    try
                    {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        fileInputStream.read(buff);
                        fileInputStream.close();

                        // 账号xml
                        String fileContent = new String(buff);

                        String url = "gam/account/stone";

                        Map params = new HashMap<String, String>();
                        params.put("gameCode", "konosuba");
                        params.put("fileContent", fileContent);
                        params.put("crystal", "11400");

                        String transAccountText = transAccount.getText().toString();
                        if (null != transAccountText && !"".equals(transAccountText) && transAccountText.length() > 0)
                        {
                            params.put("transAccount", transAccountText);
                        }

                        RequestManager.getInstance(getApplicationContext()).requestAsyn(url, RequestManager.TYPE_GET, params, new ReqCallBack(){
                            @Override
                            public void onReqSuccess(Object result)
                            {
                                Log.d(TAG_MOEMAO, result.toString());
                                JSONObject resultJson = JSONObject.parseObject(result.toString());
                                info.setText("提交服务器存档信息..." + resultJson.getString("msg"));

                                File file = new File(SAVE_DATA_PATH);

                                // 申请root权限
                                upgradeRootPermission(SAVE_DATA_FOLDER);

                                if (file.exists())
                                {
                                    boolean resule = file.delete();
                                    Log.d(TAG_MOEMAO, "delete jp.co.sumzap.pj0007.v2.playerprefs.xml is : " + String.valueOf(resule));
                                    info.append("\n删除当前存档...");
                                }

                            }
                            @Override
                            public void onReqFailed(String errorMsg)
                            {

                            }
                        });

                    }
                    catch (Exception e)
                    {
                        Log.d(TAG_MOEMAO, e.getMessage());
                    }
                }
                else
                {
                    Log.d(TAG_MOEMAO, "file jp.co.sumzap.pj0007.v2.playerprefs.xml not exist!");
                    info.setText("file jp.co.sumzap.pj0007.v2.playerprefs.xml not exist!");
                }
            }
        });
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Log.d("root", "root path is : " + pkgCodePath);
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
