package com.example.web3wallet;

import android.content.SharedPreferences;
import android.os.Bundle;
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

    private TextView tvBlockHeight, tvGasPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_status);

        tvBlockHeight = findViewById(R.id.tvBlockHeight);
        tvGasPrice = findViewById(R.id.tvGasPrice);

        fetchNetworkStatus();
    }

    private void fetchNetworkStatus() {
        // 获取 API
        SharedPreferences prefs = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        String etherscanApiKey = prefs.getString("etherscanapi", "");

        if (etherscanApiKey.isEmpty()) {
            runOnUiThread(() -> tvBlockHeight.setText("API Key not found in SharedPreferences"));
            return;
        }

        String blockUrl = "https://api.etherscan.io/api?module=proxy&action=eth_blockNumber&apikey=" + etherscanApiKey;
        String gasPriceUrl = "https://api.etherscan.io/api?module=proxy&action=eth_gasPrice&apikey=" + etherscanApiKey;

        OkHttpClient client = new OkHttpClient();

        fetchBlockHeight(client, blockUrl);
        fetchGasPrice(client, gasPriceUrl);
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
}
