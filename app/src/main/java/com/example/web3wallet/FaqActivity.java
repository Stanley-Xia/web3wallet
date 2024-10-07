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

        listDataGroup.add("Who is Stanley Xia?");

        List<String> answer1 = new ArrayList<>();
        answer1.add("A man on many missions.");
        listDataChild.put(listDataGroup.get(0), answer1);
    }
}
