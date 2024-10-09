package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_list);

        tokenListView = findViewById(R.id.tokenListView);

        // 初始化代币列表
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
        tokenList.add(new Token("TRX", "$", R.drawable.token_trx));
        tokenList.add(new Token("BTC", "$", R.drawable.token_btc));
        tokenList.add(new Token("ADA", "$", R.drawable.token_ada));
        tokenList.add(new Token("AVAX", "$", R.drawable.token_avax));

        //初始化适配器
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

        moreInfoButton = findViewById(R.id.moreInfoButton);

        // 更多信息按钮触摸事件
        moreInfoButton.setOnTouchListener(new View.OnTouchListener() {
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

        // 更多信息按钮点击事件
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建 Intent，跳转到币安的官网
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.binance.com"));
                startActivity(browserIntent);
            }
        });
    }

    // 更新代币价格和涨跌幅
    private void updateAllTokenPrices() {
        service.getMultipleTokenPricesWithChange().enqueue(new Callback<Map<String, Map<String, Double>>>() {
            @Override
            public void onResponse(Call<Map<String, Map<String, Double>>> call, Response<Map<String, Map<String, Double>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Map<String, Double>> prices = response.body();

                    tokenList.get(0).setPrice("$" + (prices.get("chainlink") != null && prices.get("chainlink").get("usd") != null ? prices.get("chainlink").get("usd").toString() : "N/A"));
                    tokenList.get(0).setPriceChange24h(prices.get("chainlink") != null && prices.get("chainlink").get("usd_24h_change") != null ? prices.get("chainlink").get("usd_24h_change") : 0.0);

                    tokenList.get(1).setPrice("$" + (prices.get("maker") != null && prices.get("maker").get("usd") != null ? prices.get("maker").get("usd").toString() : "N/A"));
                    tokenList.get(1).setPriceChange24h(prices.get("maker") != null && prices.get("maker").get("usd_24h_change") != null ? prices.get("maker").get("usd_24h_change") : 0.0);

                    tokenList.get(2).setPrice("$" + (prices.get("sushi") != null && prices.get("sushi").get("usd") != null ? prices.get("sushi").get("usd").toString() : "N/A"));
                    tokenList.get(2).setPriceChange24h(prices.get("sushi") != null && prices.get("sushi").get("usd_24h_change") != null ? prices.get("sushi").get("usd_24h_change") : 0.0);

                    tokenList.get(3).setPrice("$" + (prices.get("uniswap") != null && prices.get("uniswap").get("usd") != null ? prices.get("uniswap").get("usd").toString() : "N/A"));
                    tokenList.get(3).setPriceChange24h(prices.get("uniswap") != null && prices.get("uniswap").get("usd_24h_change") != null ? prices.get("uniswap").get("usd_24h_change") : 0.0);

                    tokenList.get(4).setPrice("$" + (prices.get("yearn-finance") != null && prices.get("yearn-finance").get("usd") != null ? prices.get("yearn-finance").get("usd").toString() : "N/A"));
                    tokenList.get(4).setPriceChange24h(prices.get("yearn-finance") != null && prices.get("yearn-finance").get("usd_24h_change") != null ? prices.get("yearn-finance").get("usd_24h_change") : 0.0);

                    tokenList.get(5).setPrice("$" + (prices.get("basic-attention-token") != null && prices.get("basic-attention-token").get("usd") != null ? prices.get("basic-attention-token").get("usd").toString() : "N/A"));
                    tokenList.get(5).setPriceChange24h(prices.get("basic-attention-token") != null && prices.get("basic-attention-token").get("usd_24h_change") != null ? prices.get("basic-attention-token").get("usd_24h_change") : 0.0);

                    tokenList.get(6).setPrice("$" + (prices.get("1inch") != null && prices.get("1inch").get("usd") != null ? prices.get("1inch").get("usd").toString() : "N/A"));
                    tokenList.get(6).setPriceChange24h(prices.get("1inch") != null && prices.get("1inch").get("usd_24h_change") != null ? prices.get("1inch").get("usd_24h_change") : 0.0);

                    tokenList.get(7).setPrice("$" + (prices.get("aave") != null && prices.get("aave").get("usd") != null ? prices.get("aave").get("usd").toString() : "N/A"));
                    tokenList.get(7).setPriceChange24h(prices.get("aave") != null && prices.get("aave").get("usd_24h_change") != null ? prices.get("aave").get("usd_24h_change") : 0.0);

                    tokenList.get(8).setPrice("$" + (prices.get("ethereum") != null && prices.get("ethereum").get("usd") != null ? prices.get("ethereum").get("usd").toString() : "N/A"));
                    tokenList.get(8).setPriceChange24h(prices.get("ethereum") != null && prices.get("ethereum").get("usd_24h_change") != null ? prices.get("ethereum").get("usd_24h_change") : 0.0);

                    tokenList.get(9).setPrice("$" + (prices.get("binancecoin") != null && prices.get("binancecoin").get("usd") != null ? prices.get("binancecoin").get("usd").toString() : "N/A"));
                    tokenList.get(9).setPriceChange24h(prices.get("binancecoin") != null && prices.get("binancecoin").get("usd_24h_change") != null ? prices.get("binancecoin").get("usd_24h_change") : 0.0);

                    tokenList.get(10).setPrice("$" + (prices.get("tether") != null && prices.get("tether").get("usd") != null ? prices.get("tether").get("usd").toString() : "N/A"));
                    tokenList.get(10).setPriceChange24h(prices.get("tether") != null && prices.get("tether").get("usd_24h_change") != null ? prices.get("tether").get("usd_24h_change") : 0.0);

                    tokenList.get(11).setPrice("$" + (prices.get("solana") != null && prices.get("solana").get("usd") != null ? prices.get("solana").get("usd").toString() : "N/A"));
                    tokenList.get(11).setPriceChange24h(prices.get("solana") != null && prices.get("solana").get("usd_24h_change") != null ? prices.get("solana").get("usd_24h_change") : 0.0);

                    tokenList.get(12).setPrice("$" + (prices.get("usd-coin") != null && prices.get("usd-coin").get("usd") != null ? prices.get("usd-coin").get("usd").toString() : "N/A"));
                    tokenList.get(12).setPriceChange24h(prices.get("usd-coin") != null && prices.get("usd-coin").get("usd_24h_change") != null ? prices.get("usd-coin").get("usd_24h_change") : 0.0);

                    tokenList.get(13).setPrice("$" + (prices.get("dai") != null && prices.get("dai").get("usd") != null ? prices.get("dai").get("usd").toString() : "N/A"));
                    tokenList.get(13).setPriceChange24h(prices.get("dai") != null && prices.get("dai").get("usd_24h_change") != null ? prices.get("dai").get("usd_24h_change") : 0.0);

                    tokenList.get(14).setPrice("$" + (prices.get("tron") != null && prices.get("tron").get("usd") != null ? prices.get("tron").get("usd").toString() : "N/A"));
                    tokenList.get(14).setPriceChange24h(prices.get("tron") != null && prices.get("tron").get("usd_24h_change") != null ? prices.get("tron").get("usd_24h_change") : 0.0);

                    tokenList.get(15).setPrice("$" + (prices.get("bitcoin") != null && prices.get("bitcoin").get("usd") != null ? prices.get("bitcoin").get("usd").toString() : "N/A"));
                    tokenList.get(15).setPriceChange24h(prices.get("bitcoin") != null && prices.get("bitcoin").get("usd_24h_change") != null ? prices.get("bitcoin").get("usd_24h_change") : 0.0);

                    tokenList.get(16).setPrice("$" + (prices.get("cardano") != null && prices.get("cardano").get("usd") != null ? prices.get("cardano").get("usd").toString() : "N/A"));
                    tokenList.get(16).setPriceChange24h(prices.get("cardano") != null && prices.get("cardano").get("usd_24h_change") != null ? prices.get("cardano").get("usd_24h_change") : 0.0);

                    tokenList.get(17).setPrice("$" + (prices.get("avalanche-2") != null && prices.get("avalanche-2").get("usd") != null ? prices.get("avalanche-2").get("usd").toString() : "N/A"));
                    tokenList.get(17).setPriceChange24h(prices.get("avalanche-2") != null && prices.get("avalanche-2").get("usd_24h_change") != null ? prices.get("avalanche-2").get("usd_24h_change") : 0.0);

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
