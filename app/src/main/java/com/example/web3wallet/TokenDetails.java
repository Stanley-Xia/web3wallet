package com.example.web3wallet;

import java.util.Map;

public class TokenDetails {
    private String name;
    private String symbol;
    private MarketData market_data;
    private Map<String, String> description;

    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public MarketData getMarketData() { return market_data; }
    public Map<String, String> getDescription() { return description; }

    public static class MarketData {
        private Map<String, Double> current_price;

        public Map<String, Double> getCurrentPrice() {
            return current_price;
        }
    }
}
