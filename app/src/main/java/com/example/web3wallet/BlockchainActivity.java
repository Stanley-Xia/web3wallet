package com.example.web3wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.widget.EditText;
import android.widget.Toast;

public class BlockchainActivity extends AppCompatActivity {

    private CardView cardSmartContract;
    private CardView cardCreateWallet;
    private CardView cardRecoverWallet;
    private CardView cardNFT;
    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_WALLET_ADDRESS_SUFFIX = "_walletAddress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockchain);

        // 初始化卡片
        cardSmartContract = findViewById(R.id.cardSmartContract);
        cardCreateWallet = findViewById(R.id.cardCreateWallet);
        cardRecoverWallet = findViewById(R.id.cardRecoverWallet);
        cardNFT = findViewById(R.id.cardNFT);

        // 智能合约卡片点击事件
        cardSmartContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 检查是否已创建钱包地址
                if (isWalletAddressCreated()) {
                    Intent intent = new Intent(BlockchainActivity.this, SmartContractActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(BlockchainActivity.this, "请先创建钱包地址", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 创建钱包卡片点击事件
        cardCreateWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 检查是否登录本地账户
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String username = sharedPreferences.getString(KEY_USERNAME, null);

                if (username == null) {
                    // 用户未登录，跳转到我的账户页面
                    Toast.makeText(BlockchainActivity.this, "请先登录或注册本地账户", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BlockchainActivity.this, MyAccountActivity.class);
                    startActivity(intent);
                } else {
                    // 用户名存在，检查是否已绑定钱包地址或私钥
                    checkWalletBindingForUser(username);
                }
            }
        });
    }

    // 检查当前用户是否已创建钱包地址
    private boolean isWalletAddressCreated() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null) {
            String walletAddress = sharedPreferences.getString(username + KEY_WALLET_ADDRESS_SUFFIX, null);
            return walletAddress != null;
        }
        return false;
    }

    // 检查账户是否绑定了私钥
    private void checkWalletBindingForUser(String username) {
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        Cursor cursor = dbHelper.getPrivateKeyCursor(username); // 获取私钥的 Cursor

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String walletAddress = sharedPreferences.getString(username + KEY_WALLET_ADDRESS_SUFFIX, null); // 检查钱包地址

        boolean hasPrivateKey = false;
        if (cursor != null && cursor.moveToFirst()) {
            String encryptedPrivateKey = cursor.getString(cursor.getColumnIndexOrThrow("private_key"));
            hasPrivateKey = encryptedPrivateKey != null && !encryptedPrivateKey.isEmpty();
            cursor.close();  // 关闭 Cursor
        }

        if (walletAddress != null || hasPrivateKey) {
            // 提示用户已经绑定了钱包地址或私钥
            Toast.makeText(BlockchainActivity.this, "该账户已绑定钱包地址，如需继续创建钱包，请先注册新的本地账户", Toast.LENGTH_LONG).show();
        } else {
            // 没有绑定钱包地址或私钥，继续创建钱包
            createWalletForUser(username);
        }
    }

    // 创建钱包并保存
    private void createWalletForUser(final String username) {
        try {
            // 调用 WalletUtils 生成新的钱包地址和私钥
            final String[] walletData = WalletUtils.createWallet();
            final String walletAddress = walletData[0];
            final String privateKey = walletData[1];

            Log.d("BlockchainActivity", "生成的钱包地址：" + walletAddress);
            Log.d("BlockchainActivity", "生成的私钥：" + privateKey);

            // 检查 walletData 是否为空
            if (walletAddress == null || privateKey == null) {
                throw new Exception("钱包地址或私钥生成失败");
            }

            // 提示用户输入密码以加密私钥
            promptUserForPasswordAndEncryptPrivateKey(username, privateKey, walletAddress);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BlockchainActivity", "生成钱包地址失败：" + e.getMessage());
            Toast.makeText(BlockchainActivity.this, "生成钱包地址失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 提示用户输入密码并加密私钥
    private void promptUserForPasswordAndEncryptPrivateKey(final String username, final String privateKey, final String walletAddress) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入密码以加密私钥");

        final EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setHint("请输入密码");
        builder.setView(passwordInput);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String password = passwordInput.getText().toString();
                if (!password.isEmpty()) {
                    try {
                        // 使用用户输入的密码加密私钥
                        String encryptedPrivateKey = EncryptionUtil.encrypt(privateKey, password);

                        // 将加密后的私钥存入数据库
                        UserDatabaseHelper dbHelper = new UserDatabaseHelper(BlockchainActivity.this);
                        boolean updateResult = dbHelper.updatePrivateKey(username, privateKey, password);

                        if (updateResult) {
                            // 保存钱包地址到 SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(username + KEY_WALLET_ADDRESS_SUFFIX, walletAddress);
                            editor.apply();

                            Log.d("BlockchainActivity", "钱包地址已保存：" + walletAddress);

                            Toast.makeText(BlockchainActivity.this, "钱包地址生成成功！", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(BlockchainActivity.this, "私钥保存失败", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BlockchainActivity.this, "私钥加密失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BlockchainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                Toast.makeText(BlockchainActivity.this, "已取消创建钱包", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
}
