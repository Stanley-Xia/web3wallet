package com.example.web3wallet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TokenAdapter extends BaseAdapter {

    private List<Token> tokenList;
    private LayoutInflater inflater;

    public TokenAdapter(Context context, List<Token> tokenList) {
        this.tokenList = tokenList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tokenList.size();
    }

    @Override
    public Object getItem(int position) {
        return tokenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_token, parent, false);
            holder = new ViewHolder();
            holder.tokenIcon = convertView.findViewById(R.id.tokenIcon);
            holder.tokenName = convertView.findViewById(R.id.tokenName);
            holder.tokenPrice = convertView.findViewById(R.id.tokenPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前的Token对象
        Token token = tokenList.get(position);

        // 绑定数据
        holder.tokenName.setText(token.getName());
        holder.tokenPrice.setText(token.getPrice());
        holder.tokenIcon.setImageResource(token.getIconResId());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建 Intent 跳转到 TokenDetailActivity
                Intent intent = new Intent(inflater.getContext(), TokenDetailActivity.class);

                // 传递代币名称
                intent.putExtra("tokenName", token.getName());

                // 启动 TokenDetailActivity
                inflater.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView tokenIcon;
        TextView tokenName;
        TextView tokenPrice;
    }
}
