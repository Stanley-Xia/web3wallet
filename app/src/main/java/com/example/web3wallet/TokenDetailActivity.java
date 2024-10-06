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

    private TextView tokenNameTextView;
    private TextView tokenSymbolTextView;
    private TextView tokenPriceTextView;
    private TextView tokenVolumeTextView;
    private TextView tokenMarketCapTextView;
    private TextView tokenDescriptionTextView;

    private static final String COINGECKO_BASE_URL = "https://api.coingecko.com/api/v3/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_detail);

        // 初始化 TextView
        tokenNameTextView = findViewById(R.id.tokenNameTextView);
        tokenSymbolTextView = findViewById(R.id.tokenSymbolTextView);
        tokenPriceTextView = findViewById(R.id.tokenPriceTextView);
        tokenVolumeTextView = findViewById(R.id.tokenVolumeTextView);
        tokenMarketCapTextView = findViewById(R.id.tokenMarketCapTextView);
        tokenDescriptionTextView = findViewById(R.id.tokenDescriptionTextView);

        // 获取从上一个 Activity 传递过来的代币名称
        String tokenName = getIntent().getStringExtra("tokenName");

        // 如果代币名称不为空，获取代币详细信息
        if (tokenName != null && !tokenName.isEmpty()) {
            getTokenDetails(tokenName);
        } else {
            tokenNameTextView.setText("Token details not found");
        }
    }

    // 获取代币详细信息的函数
    private void getTokenDetails(String tokenName) {
        String tokenId = getTokenId(tokenName);
        if (tokenId == null) {
            tokenNameTextView.setText("Token ID not found");
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
                    tokenNameTextView.setText("Token Name: " + tokenDetails.getName());
                    tokenSymbolTextView.setText("Symbol: " + tokenDetails.getSymbol());
                    tokenPriceTextView.setText("Current Price: $" + tokenDetails.getMarketData().getCurrentPrice().get("usd"));
                    tokenVolumeTextView.setText("Volume: $" + tokenDetails.getMarketData().getTotalVolume().get("usd"));
                    tokenMarketCapTextView.setText("Market Cap: $" + tokenDetails.getMarketData().getMarketCap().get("usd"));

                    if (tokenDetails.getDescription() != null && !tokenDetails.getDescription().get("en").isEmpty()) {
                        tokenDescriptionTextView.setText("Description: " + tokenDetails.getDescription().get("en"));
                    } else {
                        tokenDescriptionTextView.setText("No description available");
                    }
                } else {
                    tokenNameTextView.setText("Unable to fetch token details");
                }
            }

            @Override
            public void onFailure(Call<TokenDetails> call, Throwable t) {
                Toast.makeText(TokenDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 根据代币名称映射到 CoinGecko ID
    private String getTokenId(String tokenName) {
        switch (tokenName) {
            case "ETH":
                return "ethereum";
            case "BNB":
                return "binancecoin";
            case "USDT":
                return "tether";
            case "SOL":
                return "solana";
            case "USDC":
                return "usd-coin";
            case "1INCH":
                return "1inch";
            case "AAVE":
                return "aave";
            case "LINK":
                return "chainlink";
            case "MKR":
                return "maker";
            case "SUSHI":
                return "sushi";
            case "UNI":
                return "uniswap";
            case "YFI":
                return "yearn-finance";
            case "BAT":
                return "basic-attention-token";
            case "DAI":
                return "dai";
            default:
                return null;
        }
    }
}
