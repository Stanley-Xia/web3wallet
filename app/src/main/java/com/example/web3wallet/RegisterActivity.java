package com.example.web3wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    // UI 元素：用户名和密码输入框、注册按钮
    private EditText editUsername, editPassword;
    private Button btnRegister;

    // 数据库帮助类实例，用于操作用户数据
    private UserDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化 UI 元素
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // 初始化数据库帮助类
        db = new UserDatabaseHelper(this);

        // 注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户输入的用户名和密码
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();

                // 检查用户名和密码是否为空
                if (!username.isEmpty() && !password.isEmpty()) {
                    // 将用户信息插入数据库，不进行加密
                    boolean result = db.insertUser(username, password);

                    if (result) {
                        // 注册成功，提示并跳转到登录界面
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // 关闭注册界面
                    } else {
                        // 注册失败，可能用户名已存在
                        Toast.makeText(RegisterActivity.this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 提示用户输入完整的用户名和密码
                    Toast.makeText(RegisterActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
