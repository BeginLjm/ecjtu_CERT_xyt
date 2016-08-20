package com.mcdull.cert.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcdull.cert.R;

public class ScoreAdapter extends BaseAdapter {

    private Context context;
    private List<List<String>> list;

    public ScoreAdapter(Context context, List<List<String>> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_score, null);
        TextView tvItemScore = (TextView) view.findViewById(R.id.tv_item_score);
        if (list.get(position).size() == 4) {
            tvItemScore.setText(list.get(position).get(0) + "：" + list.get(position).get(1) + "、补考：" + list.get(position).get(3));
        } else {
            tvItemScore.setText(list.get(position).get(0) + "：" + list.get(position).get(1));
        }
        return view;
    }

    void upDateList(List<List<String>> list) {
        this.list = list;
        notifyDataSetChanged();
    }


}
