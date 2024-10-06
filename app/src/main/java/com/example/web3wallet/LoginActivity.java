package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    // 用户名和密码输入框，登录按钮
    private TextInputEditText editUsername, editPassword;
    private LinearLayout btnLogin;

    // 用户数据库帮助类
    private UserDatabaseHelper db;

    // SharedPreferences
    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_WALLET = "walletAddress";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化 UI 元素
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 初始化数据库帮助类
        db = new UserDatabaseHelper(this);

        // 登录按钮触摸事件
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小并降低透明度
                        v.animate().scaleX(0.9f).scaleY(0.9f).alpha(0.8f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 松开时恢复原样和透明度
                        v.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        // 设置登录按钮的点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String username = editUsername.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                // 检查用户名和密码是否为空
                if (!username.isEmpty() && !password.isEmpty()) {

                    // 验证用户名和密码（使用原始密码和数据库中的哈希密码进行比对）
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
