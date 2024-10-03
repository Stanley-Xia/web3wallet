package com.example.web3wallet;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MyAccountActivity extends AppCompatActivity {

    private TextView tvAccountName;
    private Button btnLogin, btnRegister, btnBack, btnLogout, btnImportKeys, btnExportKeys;

    private SharedPreferences sharedPreferences;
    private UserDatabaseHelper userDatabaseHelper;

    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvAccountName = findViewById(R.id.tv_account_name);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnImportKeys = findViewById(R.id.btnImportKeys);
        btnExportKeys = findViewById(R.id.btnExportKeys);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userDatabaseHelper = new UserDatabaseHelper(this);

        loadSavedUsername();

        // 登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 退出登录按钮点击事件
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // 更多按钮点击事件
        Button btnMoreFeatures = findViewById(R.id.btnMoreFeatures);

        btnMoreFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MoreFeaturesActivity.class);
                startActivity(intent);
            }
        });

        // 返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 结束当前Activity
            }
        });

        // 导入私钥按钮点击事件
        btnImportKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportKeyDialog(); // 调用方法显示导入私钥对话框
            }
        });

        // 导出私钥按钮点击事件
        btnExportKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportPrivateKey(); // 调用方法导出私钥
            }
        });
    }

    private void loadSavedUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, "未登录");
        tvAccountName.setText("用户名: " + username);
    }

    // 退出登录方法
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.apply();

        Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // 显示导入私钥对话框
    private void showImportKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("导入私钥");

        final EditText input = new EditText(this); // 创建一个输入框
        input.setHint("请输入私钥");
        builder.setView(input);

        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String privateKey = input.getText().toString(); // 获取用户输入的私钥
                if (!privateKey.isEmpty()) {
                    savePrivateKey(privateKey); // 保存私钥到数据库
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // 取消对话框
            }
        });

        builder.show(); // 显示对话框
    }

    // 将私钥保存到数据库
    private void savePrivateKey(String privateKey) {
        String username = sharedPreferences.getString(KEY_USERNAME, null); // 获取当前用户名
        if (username != null) {
            boolean isUpdated = userDatabaseHelper.updatePrivateKey(username, privateKey); // 使用正确的方法更新私钥
            if (isUpdated) {
                // 私钥保存成功
            } else {
                // 保存失败
            }
        }
    }

    // 导出私钥，将私钥复制到剪贴板
    private void exportPrivateKey() {
        String username = sharedPreferences.getString(KEY_USERNAME, null); // 获取当前用户名
        if (username != null) {
            String privateKey = userDatabaseHelper.getPrivateKey(username); // 获取私钥
            if (privateKey != null) {
                // 复制私钥到剪贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Private Key", privateKey);
                clipboard.setPrimaryClip(clip);

                // 提示用户已复制
                Toast.makeText(MyAccountActivity.this, "私钥已复制到剪贴板", Toast.LENGTH_SHORT).show();
            } else {
                // 提示用户私钥不存在
                Toast.makeText(MyAccountActivity.this, "无法找到私钥", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
