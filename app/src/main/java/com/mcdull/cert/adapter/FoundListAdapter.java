package com.mcdull.cert.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.mcdull.cert.R;
import com.mcdull.cert.activity.FoundDataActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by Begin on 15/10/23.
 */
public class FoundListAdapter extends BaseAdapter {
    private List<Map<String, String>> map;
    private Context context;
    private Boolean haveNext = false;
    private GetNextCallBack callBack;

    public FoundListAdapter(Activity activity, List<Map<String, String>> map, GetNextCallBack callBack) {
        this.context = activity;
        this.map = map;
        this.callBack = callBack;
    }

    public FoundListAdapter(Activity activity, GetNextCallBack callBack) {
        this.context = activity;
        this.callBack = callBack;
    }

    public void setMap(List<Map<String, String>> map) {
        this.map = map;
        notifyDataSetChanged();
    }

    public void setNext(Boolean haveNext) {
        this.haveNext = haveNext;
    }


    @Override
    public int getCount() {
        if (map == null) {
            return 0;
        } else {
            if (haveNext) {
                return map.size() + 1;
            } else {
                return map.size();
            }
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
        if (haveNext && position == map.size()) {
            convertView = new TextView(context);
            ((TextView) convertView).setText("点击加载下一页");
            ((TextView) convertView).setGravity(Gravity.CENTER);
//            convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            convertView.setBackgroundColor(0x00000000);
            convertView.setPadding(0, 30, 0, 30);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.getNextCallBack();
                }
            });
        } else {
            if (convertView != null && convertView.getTag() != null) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.item_found_list, null);
                viewHolder = new ViewHolder();
                viewHolder.img = (SmartImageView) convertView.findViewById(R.id.siv_img);
                viewHolder.id = (TextView) convertView.findViewById(R.id.tv_id);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            }
            Map<String, String> map = this.map.get(position);
            viewHolder.img.setImageUrl(map.get("publishPic1"));
            viewHolder.id.setText(map.get("publishId"));
            viewHolder.name.setText(map.get("publishName"));
            viewHolder.time.setText(map.get("publishGetTime"));
            viewHolder.id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FoundDataActivity.class);
                    intent.putExtra("id", ((TextView) v).getText().toString());
                    context.startActivity(intent);
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        SmartImageView img;
        TextView id;
        TextView name;
        TextView time;
    }

    public interface GetNextCallBack {
        public void getNextCallBack();
    }

}
