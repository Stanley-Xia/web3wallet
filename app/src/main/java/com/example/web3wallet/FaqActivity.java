package com.example.web3wallet;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    private ExpandableListView faqListView;
    private FaqAdapter faqAdapter;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        faqListView = findViewById(R.id.faq_list);

        initListData();

        faqAdapter = new FaqAdapter(this, listDataGroup, listDataChild);
        faqListView.setAdapter(faqAdapter);
    }

    private void initListData() {
        listDataGroup = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataGroup.add("什么是去中心化钱包？");
        listDataGroup.add("第一步应该做什么？");
        listDataGroup.add("如何配置 API？");
        listDataGroup.add("为什么代币列表不显示数据？");
        listDataGroup.add("为什么 Network Status 不显示？");
        listDataGroup.add("如何注册账户？");
        listDataGroup.add("如何创建钱包？");
        listDataGroup.add("账号和钱包的关系是什么？");
        listDataGroup.add("什么是公钥？");
        listDataGroup.add("什么是私钥？");
        listDataGroup.add("该开源钱包的设计者是谁？");


        List<String> answer1 = new ArrayList<>();
        answer1.add("去中心化钱包是一种让用户直接控制自己数字资产的钱包工具，它不依赖于第三方机构。用户持有并管理自己的私钥，这意味着只有用户自己能访问和操作钱包内的资产。常见的去中心化钱包包括软件钱包和硬件钱包，它们主要用于管理加密货币，如比特币和以太坊。");
        listDataChild.put(listDataGroup.get(0), answer1);

        List<String> answer2 = new ArrayList<>();
        answer2.add("获取Etherscan 和 Alchemy 的 API 密钥并配置在 app 中。");
        listDataChild.put(listDataGroup.get(1), answer2);

        List<String> answer3 = new ArrayList<>();
        answer3.add("在主页点击黄色的的接口配置按钮，输入 Etherscan 和 Alchemy 提供的 API 接口。如果没有输入正确的接口，某些功能将无法正常使用。该功能为开发版特有的功能，后续会在正式版本发布时采用硬编码的方式嵌入 API 并且移除配置入口。");
        listDataChild.put(listDataGroup.get(2), answer3);

        List<String> answer4 = new ArrayList<>();
        answer4.add("需要配置系统代理以获取代币实时信息，否则应用无法获取数据。");
        listDataChild.put(listDataGroup.get(3), answer4);

        List<String> answer5 = new ArrayList<>();
        answer5.add("需要配置系统代理以获取区块链网络信息，否则应用无法获取数据。");
        listDataChild.put(listDataGroup.get(4), answer5);

        List<String> answer6 = new ArrayList<>();
        answer6.add("在主页点击 My Account 进入账号注册页面，在此设置您的的账号和密码。");
        listDataChild.put(listDataGroup.get(5), answer6);

        List<String> answer7 = new ArrayList<>();
        answer7.add("在主页点击 Ethereum Main Network 进入钱包创建页面，在此创建您的区块链钱包。");
        listDataChild.put(listDataGroup.get(6), answer7);

        List<String> answer8 = new ArrayList<>();
        answer8.add("本地账号是指应用内的账户系统，用于在应用中登录、保存用户信息等。它和区块链钱包不同，本地账号存储在应用中，用于身份验证。区块链钱包则是用户用来管理加密货币的工具，主要保存区块链网络上的资产。该应用的设计理念是一个本地账号可以关联一个区块链钱包，帮助用户更方便地管理和访问他们的加密资产，如便捷的私钥导入和导出，使用账号密码加密和找回私钥等。");
        listDataChild.put(listDataGroup.get(7), answer8);

        List<String> answer9 = new ArrayList<>();
        answer9.add("公钥是一种加密技术中的关键部分，通常与私钥成对使用。公钥就像是你的“银行账号”，你可以将它分享给其他人，以便他们向你发送加密货币或信息。而私钥则是你个人的“密码”，只能由你自己掌握，用来访问或管理你的加密资产。公钥是由私钥通过加密算法生成的，作为非对称加密系统的一部分。在区块链中，公钥通常通过进一步的哈希运算生成地址。公钥的主要功能是验证数字签名，从而保证交易的安全性和完整性。");
        listDataChild.put(listDataGroup.get(8), answer9);

        List<String> answer10 = new ArrayList<>();
        answer10.add("私钥是一个随机生成的、极其安全的加密字符串，通常是256位的二进制数，通过加密算法与公钥配对。私钥不仅用于签署交易，确保交易的合法性，还能生成与其匹配的公钥和地址。区块链上的所有权完全依赖于私钥的持有，因此，私钥一旦丢失或泄露，资产将不可恢复或容易被他人盗取。");
        listDataChild.put(listDataGroup.get(9), answer10);

        List<String> answer11 = new ArrayList<>();
        answer11.add("Stanley Xia (夏扬）");
        listDataChild.put(listDataGroup.get(10), answer11);
    }
}
