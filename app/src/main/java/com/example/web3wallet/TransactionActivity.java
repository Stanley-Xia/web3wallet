package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionActivity extends AppCompatActivity {

    // 定义收款地址和交易金额的输入框
    private EditText editReceiverAddress, editAmount;

    // 定义发送交易、接收钱包地址和记录按钮
    private Button btnRecords;
    private FrameLayout btnSend, btnReceive;
    private TextView tvCurrentAccount, tvCurrentPrice;

    // 数据库助手实例
    private UserDatabaseHelper databaseHelper;

    // Alchemy API URL
    private String alchemyUrl;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // 初始化 UI 元素
        editReceiverAddress = findViewById(R.id.editReceiverAddress);
        editAmount = findViewById(R.id.editAmount);
        btnSend = findViewById(R.id.btnSend);
        btnReceive = findViewById(R.id.btnReceive);
        btnRecords = findViewById(R.id.btnRecords);
        tvCurrentAccount = findViewById(R.id.tvCurrentAccount);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);

        // 初始化数据库助手
        databaseHelper = new UserDatabaseHelper(this);

        // 从 SharedPreferences 加载 Alchemy API URL
        SharedPreferences sharedPreferences = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        alchemyUrl = sharedPreferences.getString("alchemyapi", null);

        if (alchemyUrl == null || alchemyUrl.isEmpty()) {
            Toast.makeText(this, "Alchemy API 未设置", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取当前登录的用户名
        String username = sharedPreferences.getString("username", null);

        // 设置账号显示
        tvCurrentAccount.setText(username);

        // 初始化 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CoinGeckoPriceService service = retrofit.create(CoinGeckoPriceService.class);

        // 获取ETH实时价格
        fetchEthPrice(service);

        // 发送按钮触摸事件
        btnSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 抬起时恢复原样
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false; // 返回 false 让点击事件继续传播
            }
        });

        // 发送按钮点击事件
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的收款地址和交易金额
                String receiverAddress = editReceiverAddress.getText().toString();
                String amount = editAmount.getText().toString();

                // 检查是否填写了收款地址和金额
                if (!receiverAddress.isEmpty() && !amount.isEmpty()) {
                    // 从数据库中获取用户的私钥
                    String username = sharedPreferences.getString("username", null);
                    if (username != null) {
                        // 弹出密码输入框，获取用户密码以解密私钥
                        showPasswordDialogAndSendTransaction(username, receiverAddress, amount);
                    } else {
                        Toast.makeText(TransactionActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 提示用户填写完整信息
                    Toast.makeText(TransactionActivity.this, "请输入有效的收款地址和金额", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 接收按钮触摸事件
        btnReceive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 抬起时恢复原样
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false; // 返回 false 让点击事件继续传播
            }
        });

        // 接收按钮点击事件
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前登录的用户名
                String username = sharedPreferences.getString("username", null);

                if (username != null) {
                    // 从 SharedPreferences 中获取保存的与用户名关联的钱包地址
                    String walletAddress = sharedPreferences.getString(username + "_walletAddress", null);

                    if (walletAddress != null && !walletAddress.isEmpty()) {
                        // 复制钱包地址到剪贴板
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("walletAddress", walletAddress);
                        clipboard.setPrimaryClip(clip);

                        // 提示用户地址已复制
                        Toast.makeText(TransactionActivity.this, "钱包地址已复制到剪贴板", Toast.LENGTH_SHORT).show();

                        // 清除焦点和隐藏键盘
                        editReceiverAddress.clearFocus();
                        editAmount.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(editReceiverAddress.getWindowToken(), 0);
                        }
                    } else {
                        // 提示用户钱包地址未设置
                        Toast.makeText(TransactionActivity.this, "钱包地址未设置", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 提示用户名未找到
                    Toast.makeText(TransactionActivity.this, "未找到用户名，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 历史记录按钮触摸事件
        btnRecords.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // 抬起时恢复原样
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false; // 返回 false 让点击事件继续传播
            }
        });

        // 历史记录按钮点击事件
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = sharedPreferences.getString("username", null);
                if (username != null) {
                    String walletAddress = sharedPreferences.getString(username + "_walletAddress", null);
                    if (walletAddress != null) {
                        // 跳转到历史记录页面，并传递钱包地址
                        Intent intent = new Intent(TransactionActivity.this, TransactionHistoryActivity.class);
                        intent.putExtra("walletAddress", walletAddress);
                        startActivity(intent);

                        // 清除焦点和隐藏键盘
                        editReceiverAddress.clearFocus();
                        editAmount.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(editReceiverAddress.getWindowToken(), 0);
                        }
                    } else {
                        Toast.makeText(TransactionActivity.this, "未找到钱包地址", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransactionActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 弹出密码输入框，获取用户密码并解密私钥，发送交易
    private void showPasswordDialogAndSendTransaction(final String username, final String receiverAddress, final String amount) {
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(this);
        passwordDialog.setTitle("输入密码解密私钥");

        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("请输入密码");
        passwordDialog.setView(passwordInput);

        passwordDialog.setPositiveButton("发送交易", (dialog, which) -> {
            String password = passwordInput.getText().toString();
            if (!password.isEmpty()) {
                // 解密私钥
                String privateKey = databaseHelper.getPrivateKey(username, password);
                if (privateKey != null) {
                    // 调用发送交易的方法
                    sendTransaction(privateKey, receiverAddress, amount);

                    // 清除焦点和隐藏键盘
                    editReceiverAddress.clearFocus();
                    editAmount.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editReceiverAddress.getWindowToken(), 0);
                    }
                } else {
                    Toast.makeText(TransactionActivity.this, "私钥解密失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        passwordDialog.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        passwordDialog.show(); // 显示密码输入框
    }

    // 发送交易的方法
    private void sendTransaction(String privateKey, String receiverAddress, String amount) {
        new Thread(() -> {
            try {
                // 连接到以太坊网络 (Alchemy)
                Web3j web3 = Web3j.build(new HttpService(alchemyUrl));  // 使用 SharedPreferences 中的 alchemyUrl

                // 使用用户的私钥生成 Credentials
                Credentials credentials = Credentials.create(privateKey);

                // 将金额转换为以太
                BigDecimal ethAmount = new BigDecimal(amount);

                // 发送交易
                TransactionReceipt transactionReceipt = Transfer.sendFunds(
                        web3, credentials, receiverAddress,
                        ethAmount, Convert.Unit.ETHER).send();

                // 交易成功，获取交易哈希
                String transactionHash = transactionReceipt.getTransactionHash();

                // 在 UI 线程中更新结果
                runOnUiThread(() -> {
                    Toast.makeText(TransactionActivity.this, "交易成功，哈希: " + transactionHash, Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(TransactionActivity.this, "交易失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // 获取ETH实时价格
    private void fetchEthPrice(CoinGeckoPriceService service) {
        Call<PriceResponse> call = service.getEthPrice();

        call.enqueue(new Callback<PriceResponse>() {
            @Override
            public void onResponse(Call<PriceResponse> call, Response<PriceResponse> response) {
                if (response.isSuccessful()) {
                    PriceResponse priceResponse = response.body();
                    if (priceResponse != null && priceResponse.getEthereum() != null) {
                        String ethPrice = priceResponse.getEthereum().getUsd();
                        tvCurrentPrice.setText("ETH: $" + ethPrice);
                    }
                } else {
                    tvCurrentPrice.setText("Error loading price");
                }
            }

            @Override
            public void onFailure(Call<PriceResponse> call, Throwable t) {
                tvCurrentPrice.setText("Failed to load price");
            }
        });
    }

    // 进入页面时不会自动获得焦点
    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.clearFocus();
        }
    }

}
