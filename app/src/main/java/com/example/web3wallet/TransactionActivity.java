package com.example.web3wallet;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

public class TransactionActivity extends AppCompatActivity {

    // 定义收款地址和交易金额的输入框
    private EditText editReceiverAddress, editAmount;

    // 定义发送交易、接收钱包地址和记录按钮
    private Button btnSend, btnReceive, btnRecords;

    // 数据库助手实例
    private UserDatabaseHelper databaseHelper;

    // Alchemy API URL 和 Etherscan API Key
    private String alchemyUrl;
    private String etherscanApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // 初始化 UI 元素
        editReceiverAddress = findViewById(R.id.editReceiverAddress);  // 收款地址输入框
        editAmount = findViewById(R.id.editAmount);  // 交易金额输入框
        btnSend = findViewById(R.id.btnSend);  // 发送交易按钮
        btnReceive = findViewById(R.id.btnReceive);  // 接收钱包地址按钮
        btnRecords = findViewById(R.id.btnRecords);  // 记录按钮

        // 初始化数据库助手
        databaseHelper = new UserDatabaseHelper(this);

        // 加载本地的 API 配置
        loadApiKeysFromProperties();

        SharedPreferences sharedPreferences = getSharedPreferences("WalletPrefs", MODE_PRIVATE);

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
                        String privateKey = databaseHelper.getPrivateKey(username);  // 从数据库获取私钥
                        if (privateKey != null) {
                            // 调用发送交易的方法
                            sendTransaction(privateKey, receiverAddress, amount);
                        } else {
                            Toast.makeText(TransactionActivity.this, "私钥未设置", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TransactionActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 提示用户填写完整信息
                    Toast.makeText(TransactionActivity.this, "请输入有效的收款地址和金额", Toast.LENGTH_SHORT).show();
                }
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

        // 历史记录按钮点击事件
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);
                if (username != null) {
                    String walletAddress = sharedPreferences.getString(username + "_walletAddress", null);
                    if (walletAddress != null) {
                        // 跳转到历史记录页面，并传递钱包地址
                        Intent intent = new Intent(TransactionActivity.this, TransactionHistoryActivity.class);
                        intent.putExtra("walletAddress", walletAddress);
                        startActivity(intent);
                    } else {
                        Toast.makeText(TransactionActivity.this, "未找到钱包地址", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransactionActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 加载 API 密钥的方法
    private void loadApiKeysFromProperties() {
        Properties properties = new Properties();
        try {
            // 从 assets 文件夹中加载 config.properties 文件
            InputStream inputStream = getAssets().open("config.properties");
            properties.load(inputStream);

            // 读取 Alchemy URL 和 Etherscan API Key
            alchemyUrl = properties.getProperty("alchemy.api.url");
            etherscanApiKey = properties.getProperty("etherscan.api.key");

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "无法加载 API 密钥", Toast.LENGTH_SHORT).show();
        }
    }

    // 发送交易的方法
    private void sendTransaction(String privateKey, String receiverAddress, String amount) {
        new Thread(() -> {
            try {
                // 连接到以太坊网络 (Alchemy)
                Web3j web3 = Web3j.build(new HttpService(alchemyUrl));

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
}
