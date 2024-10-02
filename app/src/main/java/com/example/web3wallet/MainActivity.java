package com.example.web3wallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editWalletAddress;
    private Button btnCheckBalance, btnGoToApiKey, btnTransfer, btnSmartContract, btnMyAccount, btnQueryTokens, btnSaveAddress;
    private ImageView ethLogo;
    private TextView textBalance;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_API = "apiKey";
    private static final String KEY_WALLET = "walletAddress";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 UI 元素
        editWalletAddress = findViewById(R.id.editWalletAddress);
        btnCheckBalance = findViewById(R.id.btnCheckBalance);
        btnGoToApiKey = findViewById(R.id.btnGoToApiKey);
        btnTransfer = findViewById(R.id.btnTransfer);
        btnSmartContract = findViewById(R.id.btnSmartContract);
        btnMyAccount = findViewById(R.id.btnMyAccount);
        btnQueryTokens = findViewById(R.id.btnQueryTokens);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        ethLogo = findViewById(R.id.ethLogo);
        textBalance = findViewById(R.id.textBalance);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 设置接口按钮点击事件
        btnGoToApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ApiKeyActivity.class);
                startActivity(intent);
            }
        });

        // 查询余额按钮点击事件
        btnCheckBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiKey = sharedPreferences.getString(KEY_API, "");
                String walletAddress = editWalletAddress.getText().toString();

                if (!apiKey.isEmpty() && !walletAddress.isEmpty()) {
                    saveWalletAddress(walletAddress);
                    checkBalance(apiKey, walletAddress);
                } else {
                    Toast.makeText(MainActivity.this, "请设置API Key和钱包地址", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ERC-20智能合约入口按钮点击事件
        btnSmartContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SmartContractActivity.class);
                startActivity(intent);
            }
        });

        // 转账按钮点击事件
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                startActivity(intent);
            }
        });

        // 我的账号按钮点击事件
        btnMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyAccountActivity.class);
                startActivity(intent);
            }
        });

        // 查询代币按钮点击事件
        btnQueryTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TokenListActivity.class);
                startActivity(intent);
            }
        });

        // 保存按钮点击事件
        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String walletAddress = editWalletAddress.getText().toString();
                if (!walletAddress.isEmpty()) {
                    String username = sharedPreferences.getString(KEY_USERNAME, null);
                    if (username != null) {
                        saveWalletAddressForUser(username, walletAddress);
                        Toast.makeText(MainActivity.this, "地址已保存", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "请输入有效的钱包地址", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadSavedWalletAddress();
    }

    // 保存钱包地址到 SharedPreferences
    private void saveWalletAddress(String walletAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WALLET, walletAddress);
        editor.apply();
    }

    // 将钱包地址与当前登录的用户关联
    private void saveWalletAddressForUser(String username, String walletAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username + "_walletAddress", walletAddress);
        editor.apply();
    }

    // 从 SharedPreferences 中读取并加载已保存的钱包地址
    private void loadSavedWalletAddress() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null) {
            String savedWalletAddress = sharedPreferences.getString(username + "_walletAddress", "");
            editWalletAddress.setText(savedWalletAddress);
        }
    }

    // 查询余额函数
    private void checkBalance(String apiKey, String walletAddress) {
        String url = "https://api.etherscan.io/api?module=account&action=balance&address=" + walletAddress + "&tag=latest&apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_CALL", "请求失败", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        String balanceInWei = jsonObject.getString("result");

                        BigDecimal balanceInEther = new BigDecimal(balanceInWei).divide(new BigDecimal("1000000000000000000"), 10, RoundingMode.HALF_UP);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status.equals("1") && message.equals("OK")) {
                                    textBalance.setText("余额: " + balanceInEther.toString() + " ETH");
                                } else {
                                    textBalance.setText("查询失败: " + message);
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textBalance.setText("解析错误");
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
