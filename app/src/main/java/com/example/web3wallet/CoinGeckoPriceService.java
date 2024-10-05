package com.example.web3wallet;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CoinGeckoPriceService {
    @GET("simple/price?ids=ethereum&vs_currencies=usd")
    Call<PriceResponse> getEthPrice();
}

