package com.example.web3wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // UI 元素：用户名和密码输入框，登录按钮
    private EditText editUsername, editPassword;
    private Button btnLogin;

    // 用户数据库帮助类，用于验证用户信息
    private UserDatabaseHelper db;

    // SharedPreferences 文件名
    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_USERNAME = "username"; // 用户名的键
    private static final String KEY_WALLET = "walletAddress"; // 钱包地址的键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化 UI 元素
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 初始化数据库帮助类，用于用户信息验证
        db = new UserDatabaseHelper(this);

        // 设置登录按钮的点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                // 检查用户名和密码是否为空
                if (!username.isEmpty() && !password.isEmpty()) {
                    // 验证用户名和密码（不进行加密）
                    if (db.validateUser(username, password)) {
                        // 登录成功，保存用户名到 SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_USERNAME, username);

                        // 登录成功后，清空钱包地址
                        editor.remove(KEY_WALLET);
                        editor.apply();

                        // 登录成功，跳转到主界面
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // 结束当前 Activity
                    } else {
                        // 登录失败，提示错误信息
                        Toast.makeText(LoginActivity.this, "登录失败，用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 提示输入用户名和密码
                    Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
