package com.example.web3wallet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ApiKeyActivity extends AppCompatActivity {

    // API Key 输入框和保存按钮
    private EditText editEtherscanApi, editAlchemyApi;
    private Button btnSaveApiKey;

    // 用于保存 API Key 的 SharedPreferences
    private SharedPreferences sharedPreferences;

    // SharedPreferences 文件名和 API Key 的键名
    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_ETHERSCAN_API = "etherscanapi";
    private static final String KEY_ALCHEMY_API = "alchemyapi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_key);

        // 初始化 UI 元素
        editEtherscanApi = findViewById(R.id.editEtherscanApi);
        editAlchemyApi = findViewById(R.id.editAlchemyApi);
        btnSaveApiKey = findViewById(R.id.btnSaveApi);

        // 获取 SharedPreferences 实例
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 从 SharedPreferences 中读取已保存的 API Key，并设置到输入框中
        String savedEtherscanApiKey = sharedPreferences.getString(KEY_ETHERSCAN_API, "");
        String savedAlchemyApiKey = sharedPreferences.getString(KEY_ALCHEMY_API, "");
        editEtherscanApi.setText(savedEtherscanApiKey);
        editAlchemyApi.setText(savedAlchemyApiKey);

        // 设置保存按钮的点击事件
        btnSaveApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etherscanApiKey = editEtherscanApi.getText().toString();
                String alchemyApiKey = editAlchemyApi.getText().toString();

                // 检查输入框是否为空
                if (!etherscanApiKey.isEmpty() && !alchemyApiKey.isEmpty()) {
                    // 将 Etherscan 和 Alchemy API Key 保存到 SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_ETHERSCAN_API, etherscanApiKey);
                    editor.putString(KEY_ALCHEMY_API, alchemyApiKey);
                    editor.apply();

                    // 显示保存成功的提示
                    Toast.makeText(ApiKeyActivity.this, "API Keys 已保存", Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    // 提示用户输入有效的 API Key
                    Toast.makeText(ApiKeyActivity.this, "请输入有效的 API Keys", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
