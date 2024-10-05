package com.example.web3wallet;

public class PriceResponse {
    private Ethereum ethereum;

    public Ethereum getEthereum() {
        return ethereum;
    }

    public static class Ethereum {
        private String usd;

        public String getUsd() {
            return usd;
        }
    }
}
