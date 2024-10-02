package com.example.web3wallet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    private LinearLayout transactionHistoryLayout;
    private ProgressBar progressBar; // 添加 ProgressBar
    private String etherscanApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // 初始化 UI 元素
        transactionHistoryLayout = findViewById(R.id.transactionHistoryLayout);
        progressBar = findViewById(R.id.progressBar); // 初始化 ProgressBar

        // 从 SharedPreferences 加载 API 密钥
        SharedPreferences sharedPreferences = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        etherscanApiKey = sharedPreferences.getString("etherscanapi", null);

        if (etherscanApiKey == null) {
            Toast.makeText(this, "API 密钥未设置", Toast.LENGTH_SHORT).show();
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

    // 加载交易历史的方法
    private void loadTransactionHistory(String walletAddress) {
        // 显示 ProgressBar 并隐藏内容
        progressBar.setVisibility(View.VISIBLE);
        transactionHistoryLayout.setVisibility(View.GONE);

        OkHttpClient client = new OkHttpClient();
        String url = "https://api.etherscan.io/api?module=account&action=txlist&address=" + walletAddress +
                "&startblock=0&endblock=99999999&sort=asc&apikey=" + etherscanApiKey;

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    // 隐藏 ProgressBar 并显示错误提示
                    progressBar.setVisibility(View.GONE);
                    transactionHistoryLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(TransactionHistoryActivity.this, "加载交易历史失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

                                    // 加载完成后隐藏 ProgressBar 并显示内容
                                    progressBar.setVisibility(View.GONE);
                                    transactionHistoryLayout.setVisibility(View.VISIBLE);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(TransactionHistoryActivity.this, "解析交易记录失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String message = json.getString("message");
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE);
                                transactionHistoryLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(TransactionHistoryActivity.this, "加载交易历史失败：" + message, Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            transactionHistoryLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(TransactionHistoryActivity.this, "解析交易记录失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        transactionHistoryLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(TransactionHistoryActivity.this, "加载交易历史失败：请求失败", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
