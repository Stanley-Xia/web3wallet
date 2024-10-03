package com.example.web3wallet;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import android.util.Base64;

public class EncryptionUtil {

    // AES 加密算法
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // 使用 AES-CBC 模式
    private static final int ITERATIONS = 10000; // PBKDF2 迭代次数
    private static final int KEY_LENGTH = 128; // AES 128位密钥
    private static final int SALT_LENGTH = 16; // 盐值长度
    private static final int IV_LENGTH = 16; // 初始化向量长度

    // 从用户的密码生成对称加密密钥
    public static SecretKey generateKeyFromPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = keyFactory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    // 生成随机盐值
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // 生成初始化向量 (IV)
    public static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }

    // 加密私钥
    public static String encrypt(String data, String password) throws Exception {
        // 生成盐值和 IV
        byte[] salt = generateSalt();
        byte[] iv = generateIV();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 使用盐值生成密钥
        SecretKey key = generateKeyFromPassword(password, salt);

        // 初始化 AES 加密器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // 加密数据
        byte[] encryptedValue = cipher.doFinal(data.getBytes());

        // 将加密后的数据、盐值、IV 一起编码为 Base64 格式并返回
        return Base64.encodeToString(salt, Base64.DEFAULT) + ":" +
                Base64.encodeToString(iv, Base64.DEFAULT) + ":" +
                Base64.encodeToString(encryptedValue, Base64.DEFAULT);
    }

    // 解密私钥
    public static String decrypt(String encryptedData, String password) throws Exception {
        // 分割加密数据中的盐值、IV 和密文
        String[] parts = encryptedData.split(":");
        byte[] salt = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] iv = Base64.decode(parts[1], Base64.DEFAULT);
        byte[] encryptedValue = Base64.decode(parts[2], Base64.DEFAULT);

        // 使用盐值生成密钥
        SecretKey key = generateKeyFromPassword(password, salt);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 初始化 AES 解密器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        // 解密数据并返回明文
        byte[] decryptedValue = cipher.doFinal(encryptedValue);
        return new String(decryptedValue);
    }
}
