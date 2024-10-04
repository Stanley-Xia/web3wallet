package com.example.web3wallet;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MyAccountActivity extends AppCompatActivity {

    private TextView tvAccountName;
    private Button btnLogin, btnRegister;
    private ImageView ivLogout, ivMoreFeatures, ivHome, ivImportKeys, ivExportKeys;

    private SharedPreferences sharedPreferences;
    private UserDatabaseHelper userDatabaseHelper;

    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        // 初始化
        tvAccountName = findViewById(R.id.tv_account_name);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        ivHome = findViewById(R.id.ivHome);
        ivLogout = findViewById(R.id.ivLogout);
        ivImportKeys = findViewById(R.id.ivImportKeys);
        ivExportKeys = findViewById(R.id.ivExportKeys);
        ivMoreFeatures = findViewById(R.id.ivMoreFeatures);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userDatabaseHelper = new UserDatabaseHelper(this);

        loadSavedUsername();

        // 登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 退出登录图标点击事件
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // 返回按钮点击事件
        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // 结束当前Activity
            }
        });

        // 导入私钥按钮点击事件
        ivImportKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportKeyDialog(); // 调用方法显示导入私钥对话框
            }
        });

        // 导出私钥按钮点击事件
        ivExportKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportPrivateKey(); // 调用方法导出私钥
            }
        });

        // 更多功能按钮点击事件
        ivMoreFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MoreFeaturesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadSavedUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null && !username.isEmpty()) {
            tvAccountName.setText("Account: " + username);
        } else {
            tvAccountName.setText("Not Logged In");
        }
    }

    // 退出登录方法
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.apply();

        Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // 显示导入私钥对话框
    private void showImportKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("导入私钥");

        final EditText input = new EditText(this); // 创建一个输入框
        input.setHint("请输入私钥");
        builder.setView(input);

        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String privateKey = input.getText().toString(); // 获取用户输入的私钥
                if (!privateKey.isEmpty()) {
                    showPasswordDialogAndSaveKey(privateKey); // 调用方法让用户输入密码并加密保存私钥
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // 取消对话框
            }
        });

        builder.show(); // 显示对话框
    }

    // 显示密码输入框，并保存加密后的私钥
    private void showPasswordDialogAndSaveKey(final String privateKey) {
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(this);
        passwordDialog.setTitle("输入密码加密私钥");

        final EditText passwordInput = new EditText(this); // 创建密码输入框
        passwordInput.setHint("请输入密码");
        passwordDialog.setView(passwordInput);

        passwordDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();
                if (!password.isEmpty()) {
                    savePrivateKey(privateKey, password); // 使用密码加密私钥并保存
                }
            }
        });

        passwordDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        passwordDialog.show(); // 显示密码输入对话框
    }

    // 将加密后的私钥保存到数据库
    private void savePrivateKey(String privateKey, String password) {
        String username = sharedPreferences.getString(KEY_USERNAME, null); // 获取当前用户名
        if (username != null) {
            boolean isUpdated = userDatabaseHelper.updatePrivateKey(username, privateKey, password); // 使用加密方法更新私钥
            if (isUpdated) {
                Toast.makeText(MyAccountActivity.this, "私钥已成功保存", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyAccountActivity.this, "私钥保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 导出私钥，将私钥复制到剪贴板
    private void exportPrivateKey() {
        String username = sharedPreferences.getString(KEY_USERNAME, null); // 获取当前用户名
        if (username != null) {
            showPasswordDialogAndExportKey(username);
        }
    }

    // 显示密码输入框并导出解密后的私钥
    private void showPasswordDialogAndExportKey(final String username) {
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(this);
        passwordDialog.setTitle("输入密码解密私钥");

        final EditText passwordInput = new EditText(this); // 创建密码输入框
        passwordInput.setHint("请输入密码");
        passwordDialog.setView(passwordInput);

        passwordDialog.setPositiveButton("导出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();
                if (!password.isEmpty()) {
                    String privateKey = userDatabaseHelper.getPrivateKey(username, password); // 使用密码解密私钥
                    if (privateKey != null) {
                        copyToClipboard(privateKey);
                        Toast.makeText(MyAccountActivity.this, "私钥已复制到剪贴板", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyAccountActivity.this, "私钥解密失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        passwordDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        passwordDialog.show(); // 显示密码输入对话框
    }

    // 将私钥复制到剪贴板
    private void copyToClipboard(String privateKey) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Private Key", privateKey);
        clipboard.setPrimaryClip(clip);
    }
}
