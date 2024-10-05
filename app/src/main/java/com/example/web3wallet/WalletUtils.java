package com.example.web3wallet;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;


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

    // 验证私钥是否有效
    public static boolean isValidPrivateKey(String privateKey) {
        try {
            // 将私钥转换为 BigInteger，验证是否为有效的以太坊私钥
            Numeric.toBigInt(privateKey);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 从私钥生成以太坊地址
    public static String getAddressFromPrivateKey(String privateKey) throws Exception {
        try {
            // 将私钥转换为 ECKeyPair 对象
            ECKeyPair keyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));

            // 生成以太坊地址
            return "0x" + Keys.getAddress(keyPair.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("从私钥生成地址失败：" + e.getMessage());
        }
    }

}
