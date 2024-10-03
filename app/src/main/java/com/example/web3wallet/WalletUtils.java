package com.example.web3wallet;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

public class WalletUtils {

    // 生成一个新的以太坊钱包地址
    public static String[] createWallet() throws Exception {
        try {
            // 生成密钥对
            ECKeyPair keyPair = Keys.createEcKeyPair();

            // 获取公钥
            String walletAddress = "0x" + Keys.getAddress(keyPair.getPublicKey());

            // 获取私钥
            String privateKey = keyPair.getPrivateKey().toString(16);

            // 返回钱包地址和私钥
            return new String[]{walletAddress, privateKey};
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("生成钱包失败：" + e.getMessage());
        }
    }
}
