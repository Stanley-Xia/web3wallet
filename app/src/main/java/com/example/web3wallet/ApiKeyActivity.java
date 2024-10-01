package com.example.web3wallet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ApiKeyActivity extends AppCompatActivity {

    // UI 元素：API Key 输入框和保存按钮
    private EditText editApiKey;
    private Button btnSaveApiKey;

    // 用于保存 API Key 的 SharedPreferences
    private SharedPreferences sharedPreferences;

    // SharedPreferences 文件名和 API Key 的键名
    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_API = "apiKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_key);

        // 初始化 UI 元素
        editApiKey = findViewById(R.id.editApiKey);
        btnSaveApiKey = findViewById(R.id.btnSaveApiKey);

        // 获取 SharedPreferences 实例，用于存储和读取 API Key
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 从 SharedPreferences 中读取已保存的 API Key，并设置到输入框中
        String savedApiKey = sharedPreferences.getString(KEY_API, "");
        editApiKey.setText(savedApiKey);

        // 设置保存按钮的点击事件
        btnSaveApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiKey = editApiKey.getText().toString();

                // 检查输入框是否为空
                if (!apiKey.isEmpty()) {
                    // 将 API Key 保存到 SharedPreferences 中
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_API, apiKey);
                    editor.apply(); // 异步提交更改

                    // 显示保存成功的提示
                    Toast.makeText(ApiKeyActivity.this, "API Key 已保存", Toast.LENGTH_SHORT).show();

                    // 关闭当前 Activity，返回主界面
                    finish();
                } else {
                    // 提示用户输入有效的 API Key
                    Toast.makeText(ApiKeyActivity.this, "请输入有效的 API Key", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
