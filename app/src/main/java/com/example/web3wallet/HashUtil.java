package com.example.web3wallet;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {

    // 使用 Bcrypt 对密码进行哈希处理
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // 验证用户输入的密码是否与存储的哈希密码匹配
    public static boolean checkPassword(String plaintextPassword, String hashedPassword) {
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }
}
