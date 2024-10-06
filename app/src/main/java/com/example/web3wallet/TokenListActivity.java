package com.example.web3wallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.web3wallet.CoinGeckoPriceService;

public class TokenListActivity extends AppCompatActivity {

    private ListView tokenListView;
    private TokenAdapter tokenAdapter;
    private List<Token> tokenList;
    private CoinGeckoPriceService service;
    private Call<Map<String, Map<String, Double>>> call;
    private Button moreInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_list);

        tokenListView = findViewById(R.id.tokenListView);

        tokenList = new ArrayList<>();
        tokenList.add(new Token("LINK", "$", R.drawable.token_link));
        tokenList.add(new Token("MKR", "$", R.drawable.token_mkr));
        tokenList.add(new Token("SUSHI", "$", R.drawable.token_sushi));
        tokenList.add(new Token("UNI", "$", R.drawable.token_uni));
        tokenList.add(new Token("YFI", "$", R.drawable.token_yfi));
        tokenList.add(new Token("BAT", "$", R.drawable.token_bat));
        tokenList.add(new Token("1INCH", "$", R.drawable.token_1inch));
        tokenList.add(new Token("AAVE", "$", R.drawable.token_aave));
        tokenList.add(new Token("ETH", "$", R.drawable.token_eth));
        tokenList.add(new Token("BNB", "$", R.drawable.token_bnb));
        tokenList.add(new Token("USDT", "$", R.drawable.token_usdt));
        tokenList.add(new Token("SOL", "$", R.drawable.token_sol));
        tokenList.add(new Token("USDC", "$", R.drawable.token_usdc));
        tokenList.add(new Token("DAI", "$", R.drawable.token_dai));

        tokenAdapter = new TokenAdapter(this, tokenList);

        tokenListView.setAdapter(tokenAdapter);

        // 初始化 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 创建 API 服务
        service = retrofit.create(CoinGeckoPriceService.class);

        updateAllTokenPrices();

        findViewById(R.id.moreInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建 Intent，跳转到币安的官网
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.binance.com"));
                startActivity(browserIntent);
            }
        });

    }
    private void updateAllTokenPrices() {
        service.getMultipleTokenPrices().enqueue(new Callback<Map<String, Map<String, Double>>>() {
            @Override
            public void onResponse(Call<Map<String, Map<String, Double>>> call, Response<Map<String, Map<String, Double>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Map<String, Double>> prices = response.body();

                    // 更新代币价格
                    tokenList.get(0).setPrice("$" + (prices.get("chainlink") != null && prices.get("chainlink").get("usd") != null ? prices.get("chainlink").get("usd").toString() : "N/A"));
                    tokenList.get(1).setPrice("$" + (prices.get("maker") != null && prices.get("maker").get("usd") != null ? prices.get("maker").get("usd").toString() : "N/A"));
                    tokenList.get(2).setPrice("$" + (prices.get("sushi") != null && prices.get("sushi").get("usd") != null ? prices.get("sushi").get("usd").toString() : "N/A"));
                    tokenList.get(3).setPrice("$" + (prices.get("uniswap") != null && prices.get("uniswap").get("usd") != null ? prices.get("uniswap").get("usd").toString() : "N/A"));
                    tokenList.get(4).setPrice("$" + (prices.get("yearn-finance") != null && prices.get("yearn-finance").get("usd") != null ? prices.get("yearn-finance").get("usd").toString() : "N/A"));
                    tokenList.get(5).setPrice("$" + (prices.get("basic-attention-token") != null && prices.get("basic-attention-token").get("usd") != null ? prices.get("basic-attention-token").get("usd").toString() : "N/A"));
                    tokenList.get(6).setPrice("$" + (prices.get("1inch") != null && prices.get("1inch").get("usd") != null ? prices.get("1inch").get("usd").toString() : "N/A"));
                    tokenList.get(7).setPrice("$" + (prices.get("aave") != null && prices.get("aave").get("usd") != null ? prices.get("aave").get("usd").toString() : "N/A"));
                    tokenList.get(8).setPrice("$" + (prices.get("ethereum") != null && prices.get("ethereum").get("usd") != null ? prices.get("ethereum").get("usd").toString() : "N/A"));
                    tokenList.get(9).setPrice("$" + (prices.get("binancecoin") != null && prices.get("binancecoin").get("usd") != null ? prices.get("binancecoin").get("usd").toString() : "N/A"));
                    tokenList.get(10).setPrice("$" + (prices.get("tether") != null && prices.get("tether").get("usd") != null ? prices.get("tether").get("usd").toString() : "N/A"));
                    tokenList.get(11).setPrice("$" + (prices.get("solana") != null && prices.get("solana").get("usd") != null ? prices.get("solana").get("usd").toString() : "N/A"));
                    tokenList.get(12).setPrice("$" + (prices.get("usd-coin") != null && prices.get("usd-coin").get("usd") != null ? prices.get("usd-coin").get("usd").toString() : "N/A"));
                    tokenList.get(13).setPrice("$" + (prices.get("dai") != null && prices.get("dai").get("usd") != null ? prices.get("dai").get("usd").toString() : "N/A"));

                    // 通知适配器数据已更改
                    tokenAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Map<String, Double>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
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
