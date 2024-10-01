package com.example.web3wallet;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    private LinearLayout transactionHistoryLayout;
    private String etherscanApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // 初始化 LinearLayout，用于动态添加交易记录
        transactionHistoryLayout = findViewById(R.id.transactionHistoryLayout);

        // 从配置文件加载 API 密钥
        etherscanApiKey = loadApiKeyFromConfig();

        if (etherscanApiKey == null) {
            Toast.makeText(this, "API 密钥加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取钱包地址
        String walletAddress = getIntent().getStringExtra("walletAddress");
        if (walletAddress != null) {
            loadTransactionHistory(walletAddress);
        } else {
            Toast.makeText(this, "未找到钱包地址", Toast.LENGTH_SHORT).show();
        }
    }

    // 加载 API 密钥的方法
    private String loadApiKeyFromConfig() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("etherscan.api.key"); // 确保与 config.properties 中的键名一致
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 加载交易历史的方法
    private void loadTransactionHistory(String walletAddress) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.etherscan.io/api?module=account&action=txlist&address=" + walletAddress +
                "&startblock=0&endblock=99999999&sort=asc&apikey=" + etherscanApiKey;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(TransactionHistoryActivity.this, "加载交易历史失败：" + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String status = json.getString("status");
                        if (status.equals("1")) {
                            JSONArray resultArray = json.getJSONArray("result");

                            // 在 UI 线程中更新 UI
                            runOnUiThread(() -> {
                                try {
                                    for (int i = 0; i < resultArray.length(); i++) {
                                        JSONObject txObject = resultArray.getJSONObject(i);
                                        String hash = txObject.getString("hash");
                                        String value = txObject.getString("value");
                                        String from = txObject.getString("from");
                                        String to = txObject.getString("to");
                                        String timeStamp = txObject.getString("timeStamp");

                                        // 动态创建 TextView 并添加到 LinearLayout
                                        TextView textView = new TextView(TransactionHistoryActivity.this);
                                        textView.setText("交易哈希: " + hash + "\n金额: " + value + " wei\n发送方: " + from +
                                                "\n接收方: " + to + "\n时间戳: " + timeStamp);
                                        textView.setPadding(0, 16, 0, 16);

                                        transactionHistoryLayout.addView(textView);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(TransactionHistoryActivity.this, "解析交易记录失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String message = json.getString("message");
                            runOnUiThread(() ->
                                    Toast.makeText(TransactionHistoryActivity.this, "加载交易历史失败：" + message, Toast.LENGTH_SHORT).show()
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(TransactionHistoryActivity.this, "解析交易记录失败", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(TransactionHistoryActivity.this, "加载交易历史失败：请求失败", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
