package com.mcdull.cert.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;
import com.mcdull.cert.R;
import com.mcdull.cert.activity.FoundDataActivity;
import com.mcdull.cert.activity.FoundListActivity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Begin on 15/10/23.
 */
public class FoundAdapter extends BaseAdapter {
    private String[] keys = {"最新", "日用品", "图书", "包类", "穿戴设备", "电子设备", "运动类", "证件", "钥匙", "其他"};

    private Map<String, List<Map<String, String>>> map;
    private Context context;

    public FoundAdapter(Activity activity, Map<String, List<Map<String, String>>> map) {
        this.context = activity;
        this.map = map;
    }

    public FoundAdapter(Activity activity) {
        this.context = activity;
    }

    public void setMap(Map<String, List<Map<String, String>>> map) {
        this.map = map;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (map == null) {
            return 0;
        } else {
            return map.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.item_found, null);
            viewHolder = new ViewHolder();
            viewHolder.itemId[0] = (TextView) convertView.findViewById(R.id.tv_item1_id);
            viewHolder.itemId[1] = (TextView) convertView.findViewById(R.id.tv_item2_id);
            viewHolder.itemId[2] = (TextView) convertView.findViewById(R.id.tv_item3_id);
            viewHolder.itemName[0] = (TextView) convertView.findViewById(R.id.tv_item1_name);
            viewHolder.itemName[1] = (TextView) convertView.findViewById(R.id.tv_item2_name);
            viewHolder.itemName[2] = (TextView) convertView.findViewById(R.id.tv_item3_name);
            viewHolder.img[0] = (SmartImageView) convertView.findViewById(R.id.siv_item1_img);
            viewHolder.img[1] = (SmartImageView) convertView.findViewById(R.id.siv_item2_img);
            viewHolder.img[2] = (SmartImageView) convertView.findViewById(R.id.siv_item3_img);
            viewHolder.item1Time = (TextView) convertView.findViewById(R.id.tv_item1_time);
            viewHolder.type = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolder.more = (TextView) convertView.findViewById(R.id.tv_more);
            viewHolder.typeId = (TextView) convertView.findViewById(R.id.type_id);
            convertView.setTag(viewHolder);
        }
        List<Map<String, String>> list = this.map.get(keys[position]);
        viewHolder.typeId.setText(position+"");
        for (int i = 0; i < 3; i++) {
            Map<String, String> map = list.get(i);
            viewHolder.itemId[i].setText(map.get("publishId"));
            viewHolder.itemName[i].setText(map.get("publishName"));
            viewHolder.img[i].setImageUrl(map.get("publishPic1"));
        }
        for (TextView v : viewHolder.itemId) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FoundDataActivity.class);
                    intent.putExtra("id", ((TextView) v).getText().toString());
                    context.startActivity(intent);
                }
            });
        }
        viewHolder.item1Time.setText(list.get(0).get("publishGetTime"));
        viewHolder.type.setText(keys[position]);
        viewHolder.typeId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动更多列表
                Intent intent = new Intent(context, FoundListActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("type", ((TextView) v).getText());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {
        SmartImageView[] img = new SmartImageView[3];
        TextView[] itemId = new TextView[3];
        TextView[] itemName = new TextView[3];
        TextView item1Time;
        TextView type;
        TextView typeId;
        TextView more;
    }

}
