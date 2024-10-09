package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private EditText editWalletAddress;
    private Button btnCheckBalance, btnTransfer, btnQueryTokens;
    private FrameLayout btnBlockchain, btnMyAccount;
    private ImageView ethLogo, btnGoToApiKey, btnSaveAddress;
    private TextView textBalance, tvAccount;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_ETHERSCAN_API = "etherscanapi";
    private static final String KEY_WALLET = "walletAddress";
    private static final String KEY_USERNAME = "username";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editWalletAddress = findViewById(R.id.editWalletAddress);
        btnCheckBalance = findViewById(R.id.btnCheckBalance);
        btnGoToApiKey = findViewById(R.id.btnGoToApiKey);
        btnTransfer = findViewById(R.id.btnTransfer);
        btnBlockchain = findViewById(R.id.btnBlockchain);
        btnMyAccount = findViewById(R.id.btnMyAccount);
        btnQueryTokens = findViewById(R.id.btnQueryTokens);
        btnSaveAddress = findViewById(R.id.ivSaveIcon);
        ethLogo = findViewById(R.id.ethLogo);
        textBalance = findViewById(R.id.textBalance);
        tvAccount = findViewById(R.id.tvAccount);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null && !username.isEmpty()) {
            tvAccount.setText(username);
        } else {
            tvAccount.setText("Login / Sign up");
        }

        // 设置接口按钮触摸事件
        btnGoToApiKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        // 设置接口按钮点击事件
        btnGoToApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ApiKeyActivity.class);
                startActivity(intent);
            }
        });

        // 查询余额按钮触摸事件
        btnCheckBalance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        // 查询余额按钮点击事件
        btnCheckBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etherscanapi = sharedPreferences.getString(KEY_ETHERSCAN_API, "");
                String walletAddress = editWalletAddress.getText().toString();

                if (!etherscanapi.isEmpty() && !walletAddress.isEmpty()) {
                    saveWalletAddress(walletAddress);
                    checkBalance(etherscanapi, walletAddress);
                } else {
                    Toast.makeText(MainActivity.this, "请设置API Key和钱包地址", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 区块链按钮触摸事件
        btnBlockchain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        // 区块链按钮点击事件
        btnBlockchain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BlockchainActivity.class);
                startActivity(intent);
            }
        });

        // 转账按钮触摸事件
        btnTransfer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
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

        // 我的账号按钮触摸事件
        btnMyAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
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

        // 查询代币按钮触摸事件
        btnQueryTokens.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
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

        // 保存按钮触摸事件
        btnSaveAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        // 保存按钮点击事件
        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String walletAddress = editWalletAddress.getText().toString();

                // 清除焦点并隐藏键盘
                editWalletAddress.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editWalletAddress.getWindowToken(), 0);
                }

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

        // 以太坊logo点击事件
        ethLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加旋转动画
                v.animate().rotationBy(360f).setDuration(500).start();
            }
        });

        // 以太坊logo触摸事件
        ethLogo.setOnTouchListener(new View.OnTouchListener() {
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
    private void checkBalance(String etherscanapi, String walletAddress) {
        String url = "https://api.etherscan.io/api?module=account&action=balance&address=" + walletAddress + "&tag=latest&apikey=" + etherscanapi;

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

                        BigDecimal balanceInEther = new BigDecimal(balanceInWei).divide(new BigDecimal("1000000000000000000"), 5, RoundingMode.HALF_UP);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status.equals("1") && message.equals("OK")) {
                                    textBalance.setText(balanceInEther.toString() + " ETH");
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

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.clearFocus();
        }

        loadSavedWalletAddress();

        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null && !username.isEmpty()) {
            tvAccount.setText(username);
        } else {
            tvAccount.setText("Login / Sign up");
        }
    }

}
