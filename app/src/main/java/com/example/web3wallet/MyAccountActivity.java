package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MyAccountActivity extends AppCompatActivity {

    private TextView tvAccountName;
    private Button btnLogin, btnRegister;
    private ImageView ivLogout, ivMoreFeatures, ivHome, ivSettings;
    private FrameLayout ivImportKeys, ivExportKeys, ivSwitchUser;

    private SharedPreferences sharedPreferences;
    private UserDatabaseHelper userDatabaseHelper;

    private static final String PREFS_NAME = "WalletPrefs";
    private static final String KEY_USERNAME = "username";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        tvAccountName = findViewById(R.id.tv_account_name);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        ivHome = findViewById(R.id.ivHome);
        ivLogout = findViewById(R.id.ivLogout);
        ivImportKeys = findViewById(R.id.ivImportKeys);
        ivExportKeys = findViewById(R.id.ivExportKeys);
        ivMoreFeatures = findViewById(R.id.ivMoreFeatures);
        ivSwitchUser = findViewById(R.id.ivSwitchUser);
        ivSettings = findViewById(R.id.ivSettings);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userDatabaseHelper = new UserDatabaseHelper(this);

        loadSavedUsername();

        // 登录按钮触摸事件
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
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

        // 登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 注册按钮触摸事件
        btnRegister.setOnTouchListener(new View.OnTouchListener() {
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

        // 注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 导入私钥按钮触摸事件
        ivImportKeys.setOnTouchListener(new View.OnTouchListener() {
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

        // 导入私钥按钮点击事件
        ivImportKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportKeyDialog();
            }
        });

        // 导出私钥按钮触摸事件
        ivExportKeys.setOnTouchListener(new View.OnTouchListener() {
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

        // 导出私钥按钮点击事件
        ivExportKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportPrivateKey();
            }
        });

        // 切换账号按钮触摸事件
        ivSwitchUser.setOnTouchListener(new View.OnTouchListener() {
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

        // 切换账号按钮点击事件
        ivSwitchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除登录状态
                SharedPreferences sharedPreferences = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("wallet_address");
                editor.apply();

                Toast.makeText(MyAccountActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();

                // 跳转到登录页面
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // 设置按钮触摸事件
        ivSettings.setOnTouchListener(new View.OnTouchListener() {
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

        // 设置按钮点击事件
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().rotationBy(360f).setDuration(500).start();
                Toast.makeText(MyAccountActivity.this, "功能正在开发中", Toast.LENGTH_SHORT).show();
            }
        });

        // 更多功能按钮触摸事件
        ivMoreFeatures.setOnTouchListener(new View.OnTouchListener() {
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

        // 更多功能按钮点击事件
        ivMoreFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MoreFeaturesActivity.class);
                startActivity(intent);
            }
        });

        // 返回主页按钮触摸事件
        ivHome.setOnTouchListener(new View.OnTouchListener() {
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

        // 返回主页按钮点击事件
        ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // 退出登录按钮触摸事件
        ivLogout.setOnTouchListener(new View.OnTouchListener() {
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

        // 退出登录按钮点击事件
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void loadSavedUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null && !username.isEmpty()) {
            tvAccountName.setText("User: " + username);
        } else {
            tvAccountName.setText("Not Logged In");
        }
    }

    // 退出登录方法
    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.apply();

        tvAccountName.setText("Not Logged In");
        Toast.makeText(MyAccountActivity.this, "已退出登录", Toast.LENGTH_SHORT).show();
/*
        Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
*/
    }

    // 显示导入私钥对话框
    @SuppressLint("ClickableViewAccessibility")
    private void showImportKeyDialog() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            @SuppressLint("InflateParams")
            View customView = getLayoutInflater().inflate(R.layout.dialog_import_key, null);

            EditText keyInput = customView.findViewById(R.id.key_input);
            FrameLayout btnCancel = customView.findViewById(R.id.dialog_button_cancel);
            FrameLayout btnConfirm = customView.findViewById(R.id.dialog_button_confirm);

            builder.setView(customView);

            final AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            }

            dialog.show();

            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // 取消按钮触摸事件
            btnCancel.setOnTouchListener(new View.OnTouchListener() {
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

            // 确认按钮触摸事件
            btnConfirm.setOnTouchListener(new View.OnTouchListener() {
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

            // 取消按钮点击事件
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // 确认按钮点击事件
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String privateKey = keyInput.getText().toString();
                    if (!privateKey.isEmpty()) {
                        showPasswordDialogAndSaveKey(privateKey);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MyAccountActivity.this, "私钥不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        },200);
    }

    // 显示密码输入框，并保存加密后的私钥
    private void showPasswordDialogAndSaveKey(final String privateKey) {
        AlertDialog.Builder passwordDialog = new AlertDialog.Builder(this);
        passwordDialog.setTitle("输入密码加密私钥");

        final EditText passwordInput = new EditText(this);
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

        passwordDialog.show();
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
            showExportKeyDialog();
        }
    }

    // 显示导出私钥对话框
    @SuppressLint("ClickableViewAccessibility")
    private void showExportKeyDialog() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            @SuppressLint("InflateParams")
            View customView = getLayoutInflater().inflate(R.layout.dialog_export_key, null);

            EditText passwordInput = customView.findViewById(R.id.password_input);
            FrameLayout btnCancel = customView.findViewById(R.id.dialog_button_cancel);
            FrameLayout btnConfirm = customView.findViewById(R.id.dialog_button_confirm);

            builder.setView(customView);

            final AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background_3);
            }

            dialog.show();

            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // 取消按钮触摸事件
            btnCancel.setOnTouchListener(new View.OnTouchListener() {
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

            // 确认按钮触摸事件
            btnConfirm.setOnTouchListener(new View.OnTouchListener() {
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

            // 取消按钮点击事件
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // 确认按钮点击事件
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String password = passwordInput.getText().toString();
                    if (!password.isEmpty()) {
                        String username = sharedPreferences.getString(KEY_USERNAME, null);
                        if (username != null) {
                            String privateKey = userDatabaseHelper.getPrivateKey(username, password);
                            if (privateKey != null) {
                                copyToClipboard(privateKey);
                                Toast.makeText(MyAccountActivity.this, "私钥已复制到剪贴板", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyAccountActivity.this, "私钥解密失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MyAccountActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        },200);
    }

    // 将私钥复制到剪贴板
    private void copyToClipboard(String privateKey) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Private Key", privateKey);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.clearFocus();
        }
    }

}
