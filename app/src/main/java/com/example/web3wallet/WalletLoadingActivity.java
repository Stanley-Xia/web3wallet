package com.example.web3wallet;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class WalletLoadingActivity extends AppCompatActivity {

    private ImageView ivEthereumLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_loading);

        ivEthereumLogo = findViewById(R.id.ivEthereumLogo);

        // 设置旋转动画
        RotateAnimation rotate = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000); // 旋转一圈的时间
        rotate.setRepeatCount(Animation.INFINITE); // 无限循环旋转
        ivEthereumLogo.startAnimation(rotate);

        // 模拟钱包生成的延迟
        new Handler().postDelayed(() -> {
            // 钱包生成完成后结束该页面
            finish();
        }, 3000); // 3秒后结束
    }
}
