package com.example.web3wallet;

import android.app.Application;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Provider;
import java.security.Security;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        installBouncyCastleProvider();
    }

    private void installBouncyCastleProvider() {
        // 移除已经存在的 BouncyCastle 提供程序
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider != null) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        }
        // 添加 BouncyCastle 提供程序
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
