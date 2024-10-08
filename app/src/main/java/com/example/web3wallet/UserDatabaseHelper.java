package com.example.web3wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 2; //版本号

    // 数据库表和列的定义
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PRIVATE_KEY = "private_key";

    // 构造函数，用于创建数据库实例
    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建数据库表，当数据库首次创建时调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PRIVATE_KEY + " TEXT)";
        db.execSQL(createTable);
    }

    // 更新数据库表结构，当数据库版本更新时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // 版本号为1时升级数据库，增加私钥列
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PRIVATE_KEY + " TEXT");
        }
    }

    // 插入用户信息
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase(); // 获取可写的数据库实例
        ContentValues values = new ContentValues(); // 使用 ContentValues 存储要插入的值
        values.put(COLUMN_USERNAME, username); // 插入用户名
        values.put(COLUMN_PASSWORD, password); // 插入哈希后的密码

        long result = db.insert(TABLE_USERS, null, values); // 执行插入操作
        return result != -1; // 返回插入结果，-1 表示插入失败
    }

    // 验证用户信息
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase(); // 获取可读的数据库实例

        // 查询数据库中的哈希密码
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PASSWORD},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);

        // 检查是否找到了匹配的用户名
        if (cursor != null && cursor.moveToFirst()) {
            String storedPasswordHash = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            cursor.close(); // 关闭 Cursor 以释放资源

            // 使用 Bcrypt 检查密码是否匹配
            return HashUtil.checkPassword(password, storedPasswordHash);
        }
        return false; // 如果用户名不存在或密码不匹配，返回 false
    }

    // 更新私钥信息，加密后存储
    public boolean updatePrivateKey(String username, String privateKey, String password) {
        try {
            // 使用用户密码加密私钥
            String encryptedPrivateKey = EncryptionUtil.encrypt(privateKey, password);

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRIVATE_KEY, encryptedPrivateKey); // 存储加密后的私钥

            // 更新记录
            int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
            return result > 0; // 返回是否更新成功
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取并解密用户的私钥
    public String getPrivateKey(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_PRIVATE_KEY},
                COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String encryptedPrivateKey = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIVATE_KEY));
            cursor.close(); // 关闭 Cursor 以释放资源

            // 解密私钥
            try {
                return EncryptionUtil.decrypt(encryptedPrivateKey, password);
            } catch (Exception e) {
                e.printStackTrace();
                return null; // 如果解密失败，返回 null
            }
        }
        return null; // 如果没有找到私钥，返回 null
    }

    // 在 UserDatabaseHelper 类中添加 getPrivateKeyCursor 方法
    public Cursor getPrivateKeyCursor(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, new String[]{COLUMN_PRIVATE_KEY}, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
    }

}
