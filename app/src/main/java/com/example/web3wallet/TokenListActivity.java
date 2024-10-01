package com.example.web3wallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class TokenListActivity extends AppCompatActivity {

    // 用于显示代币列表的 ListView
    private ListView tokenListView;
    private Button moreInfoButton;

    // 主要的 ERC-20 代币名称数组
    private String[] tokens = {
            "USDT (Tether)",
            "USDC (USD Coin)",
            "BNB (Binance Coin)",
            "DAI (Dai Stablecoin)",
            "LINK (Chainlink)",
            "UNI (Uniswap)",
            "WBTC (Wrapped Bitcoin)",
            "MKR (Maker)",
            "SUSHI (SushiSwap)",
            "AAVE (Aave)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_list);

        // 初始化 ListView
        tokenListView = findViewById(R.id.tokenListView);
        moreInfoButton = findViewById(R.id.moreInfoButton);

        // 创建 ArrayAdapter，使用自定义的 simple_list_item.xml 布局文件
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, tokens);
        tokenListView.setAdapter(adapter);

        // 为 ListView 的每个项目设置点击事件监听器
        tokenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取用户点击的代币名称
                String selectedToken = tokens[position];

                // 创建 Intent，跳转到 TokenDetailActivity 并传递选中的代币名称
                Intent intent = new Intent(TokenListActivity.this, TokenDetailActivity.class);
                intent.putExtra("tokenName", selectedToken); // 通过 Intent 传递代币名称
                startActivity(intent); // 启动 TokenDetailActivity
            }
        });

        // 为 "更多信息" 按钮设置点击事件监听器
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建 Intent，跳转到币安的官网
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.binance.com"));
                startActivity(browserIntent);
            }
        });
    }
}
