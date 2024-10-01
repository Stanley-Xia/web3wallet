package com.example.web3wallet;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenDetailActivity extends AppCompatActivity {

    // 用于显示代币详细信息的 TextView
    private TextView tokenNameTextView;
    private TextView tokenSymbolTextView;
    private TextView tokenPriceTextView;
    private TextView tokenDescriptionTextView;

    // CoinGecko API 的基础 URL
    private static final String COINGECKO_BASE_URL = "https://api.coingecko.com/api/v3/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_detail);

        // 初始化 TextView，用于展示代币的详细信息
        tokenNameTextView = findViewById(R.id.tokenNameTextView);
        tokenSymbolTextView = findViewById(R.id.tokenSymbolTextView);
        tokenPriceTextView = findViewById(R.id.tokenPriceTextView);
        tokenDescriptionTextView = findViewById(R.id.tokenDescriptionTextView);

        // 获取从上一个 Activity 传递过来的代币名称
        String tokenName = getIntent().getStringExtra("tokenName");

        // 如果代币名称不为空，获取代币详细信息
        if (tokenName != null && !tokenName.isEmpty()) {
            getTokenDetails(tokenName); // 调用函数获取代币详细信息
        } else {
            tokenNameTextView.setText("未找到代币详细信息");
        }
    }

    // 获取代币详细信息的函数
    private void getTokenDetails(String tokenName) {
        String tokenId = getTokenId(tokenName); // 获取 CoinGecko 的 ID
        if (tokenId == null) {
            tokenNameTextView.setText("未找到代币的 ID");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(COINGECKO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CoinGeckoService service = retrofit.create(CoinGeckoService.class);
        service.getTokenDetails(tokenId).enqueue(new Callback<TokenDetails>() {
            @Override
            public void onResponse(Call<TokenDetails> call, Response<TokenDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenDetails tokenDetails = response.body();
                    tokenNameTextView.setText("代币名称: " + tokenDetails.getName());
                    tokenSymbolTextView.setText("符号: " + tokenDetails.getSymbol());
                    tokenPriceTextView.setText("当前价格: $" + tokenDetails.getMarketData().getCurrentPrice().get("usd"));

                    // 添加代币简介展示
                    if (tokenDetails.getDescription() != null && !tokenDetails.getDescription().get("en").isEmpty()) {
                        tokenDescriptionTextView.setText("简介: " + tokenDetails.getDescription().get("en"));
                    } else {
                        tokenDescriptionTextView.setText("暂无简介信息");
                    }
                } else {
                    tokenNameTextView.setText("无法获取代币详细信息");
                }
            }

            @Override
            public void onFailure(Call<TokenDetails> call, Throwable t) {
                Toast.makeText(TokenDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 根据代币名称映射到 CoinGecko ID
    private String getTokenId(String tokenName) {
        switch (tokenName) {
            case "USDT (Tether)":
                return "tether";
            case "USDC (USD Coin)":
                return "usd-coin";
            case "BNB (Binance Coin)":
                return "binancecoin";
            case "DAI (Dai Stablecoin)":
                return "dai";
            case "LINK (Chainlink)":
                return "chainlink";
            case "UNI (Uniswap)":
                return "uniswap";
            case "WBTC (Wrapped Bitcoin)":
                return "wrapped-bitcoin";
            case "MKR (Maker)":
                return "maker";
            case "SUSHI (SushiSwap)":
                return "sushi";
            case "AAVE (Aave)":
                return "aave";
            default:
                return null;
        }
    }
}
