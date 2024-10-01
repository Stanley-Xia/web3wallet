package com.example.web3wallet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CoinGeckoService {
    @GET("coins/{id}")
    Call<TokenDetails> getTokenDetails(@Path("id") String tokenId);
}
