package com.example.web3wallet;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class NetworkStatusActivity extends AppCompatActivity {

    private TextView tvTitle, tvBlockHeight, tvGasPrice, tvEthereumPrice;
    private ImageView ivRefresh;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_status);

        tvTitle = findViewById(R.id.tvTitle);
        tvBlockHeight = findViewById(R.id.tvBlockHeight);
        tvGasPrice = findViewById(R.id.tvGasPrice);
        tvEthereumPrice = findViewById(R.id.tvEthereumPrice);
        ivRefresh = findViewById(R.id.ivRefresh);

        fetchNetworkStatus();

        // 刷新按钮触摸事件
        ivRefresh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            }
        });

        // 刷新按钮点击事件
        ivRefresh.setOnClickListener(v -> {
            v.animate().rotationBy(720f).setDuration(800).start();
            fetchNetworkStatus();
        });
    }

    private void fetchNetworkStatus() {
        // 获取 API
        SharedPreferences prefs = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        String etherscanApiKey = prefs.getString("etherscanapi", "");

        if (etherscanApiKey.isEmpty()) {
            runOnUiThread(() -> tvTitle.setText("API Key not found in SharedPreferences"));
            return;
        }

        String blockUrl = "https://api.etherscan.io/api?module=proxy&action=eth_blockNumber&apikey=" + etherscanApiKey;
        String gasPriceUrl = "https://api.etherscan.io/api?module=proxy&action=eth_gasPrice&apikey=" + etherscanApiKey;
        String priceUrl = "https://api.etherscan.io/api?module=stats&action=ethprice&apikey=" + etherscanApiKey;

        OkHttpClient client = new OkHttpClient();

        fetchBlockHeight(client, blockUrl);
        fetchGasPrice(client, gasPriceUrl);
        fetchEthereumPrice(client, priceUrl);
    }

    // 获取区块高度
    private void fetchBlockHeight(OkHttpClient client, String blockUrl) {
        Request blockRequest = new Request.Builder().url(blockUrl).build();
        client.newCall(blockRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String blockJson = response.body().string();
                        JSONObject jsonObject = new JSONObject(blockJson);
                        String blockHeight = jsonObject.getString("result");

                        runOnUiThread(() -> tvBlockHeight.setText("Block Height: " + blockHeight));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 获取Gas价格
    private void fetchGasPrice(OkHttpClient client, String gasPriceUrl) {
        Request gasPriceRequest = new Request.Builder().url(gasPriceUrl).build();
        client.newCall(gasPriceRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String gasJson = response.body().string();
                        JSONObject gasObject = new JSONObject(gasJson);
                        String gasPrice = gasObject.getString("result");

                        long gasPriceInWei = Long.parseLong(gasPrice.substring(2), 16);
                        String gasPriceInGwei = String.valueOf(gasPriceInWei / Math.pow(10, 9));

                        runOnUiThread(() -> tvGasPrice.setText("Gas Price: " + gasPriceInGwei + " Gwei"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 获取以太坊价格
    private void fetchEthereumPrice(OkHttpClient client, String priceUrl) {
        Request priceRequest = new Request.Builder().url(priceUrl).build();
        client.newCall(priceRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String priceJson = response.body().string();
                        JSONObject priceObject = new JSONObject(priceJson);
                        String ethUsdPrice = priceObject.getJSONObject("result").getString("ethusd");

                        double ethPrice = Double.parseDouble(ethUsdPrice);
                        String formattedPrice = String.format("%.2f", ethPrice);

                        runOnUiThread(() -> tvEthereumPrice.setText("ETH Price: " + formattedPrice + " USD"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
