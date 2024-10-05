package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockchain);

        // 初始化卡片
        cardSmartContract = findViewById(R.id.cardSmartContract);
        cardCreateWallet = findViewById(R.id.cardCreateWallet);
        cardRecoverWallet = findViewById(R.id.cardRecoverWallet);
        cardNFT = findViewById(R.id.cardNFT);

        // 创建钱包卡片触摸事件
        cardCreateWallet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
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

        // 恢复钱包卡片触摸事件
        cardRecoverWallet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
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

        // 恢复钱包卡片点击事件
        cardRecoverWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用恢复钱包的方法
                showRecoverWalletDialog();
            }
        });

        // 智能合约卡片触摸事件
        cardSmartContract.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
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

        // NFT卡片触摸事件
        cardNFT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时缩小
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
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

        // NFT 卡片点击事件
        cardNFT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlockchainActivity.this, "NFT 功能尚未完成，敬请期待！", Toast.LENGTH_SHORT).show();
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

    // 显示恢复钱包对话框
    private void showRecoverWalletDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("恢复钱包");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("请输入私钥");
        builder.setView(input);

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String privateKey = input.getText().toString();
                if (!privateKey.isEmpty()) {
                    recoverWallet(privateKey); // 调用恢复钱包方法
                } else {
                    Toast.makeText(BlockchainActivity.this, "私钥不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // 恢复钱包方法
    private void recoverWallet(String privateKey) {
        try {
            // 验证私钥是否有效
            if (WalletUtils.isValidPrivateKey(privateKey)) {
                // 从私钥生成钱包地址
                String walletAddress = WalletUtils.getAddressFromPrivateKey(privateKey);

                // 将地址存入剪贴板
                copyToClipboard(walletAddress);
                Toast.makeText(BlockchainActivity.this, "钱包地址已复制到剪贴板: " + walletAddress, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(BlockchainActivity.this, "无效的私钥", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(BlockchainActivity.this, "恢复钱包失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 将生成的钱包地址复制到剪贴板
    private void copyToClipboard(String walletAddress) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Wallet Address", walletAddress);
        clipboard.setPrimaryClip(clip);
    }

}
