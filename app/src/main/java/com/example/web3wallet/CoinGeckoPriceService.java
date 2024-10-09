package com.example.web3wallet;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoinGeckoPriceService {
    @GET("simple/price?ids=ethereum&vs_currencies=usd")
    Call<PriceResponse> getEthPrice();

    @GET("simple/price?ids=ethereum,binancecoin,tether,solana,usd-coin,1inch,aave,chainlink,maker,sushi,uniswap,yearn-finance,basic-attention-token,dai,tron,bitcoin,cardano,avalanche-2&vs_currencies=usd")
    Call<Map<String, Map<String, Double>>> getMultipleTokenPrices();

    @GET("simple/price?ids=ethereum&vs_currencies=usd&include_24hr_change=true")
    Call<PriceResponse> getEthPriceWithChange();

    @GET("simple/price?ids=ethereum,binancecoin,tether,solana,usd-coin,1inch,aave,chainlink,maker,sushi,uniswap,yearn-finance,basic-attention-token,dai,tron,bitcoin,cardano,avalanche-2&vs_currencies=usd&include_24hr_change=true")
    Call<Map<String, Map<String, Double>>> getMultipleTokenPricesWithChange();

}
