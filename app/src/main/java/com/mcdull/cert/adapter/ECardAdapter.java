package com.mcdull.cert.adapter;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcdull.cert.R;

/**
 * Created by mcdull on 15/7/11.
 */

public class ECardAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, String>> list;

    public ECardAdapter(Context context, List<Map<String, String>> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list==null) {
            return 0;
        }else {
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
        View view = View.inflate(context, R.layout.select_item, null);
        TextView tvItemScore = (TextView) view.findViewById(R.id.tv_select_item);
        Map<String, String> map = list.get(position);
        tvItemScore.setText("消费时间："+map.get("time")+"\n消费地点："+map.get("SystemName")+"\n消费金额："+map.get("account")+"元");
        return view;
    }

    void upDateList(List<Map<String, String>> list){
        this.list = list;
        notifyDataSetChanged();
    }


}
