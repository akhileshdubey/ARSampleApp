package com.example.intelligrape.arsampeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.intelligrape.arsampeapp.R;

/**
 * Created by Navkrishna on November 01, 2014
 */
public class AssistantBaseAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    String[] glassType;
    int[] glassResId;

    public AssistantBaseAdapter(Context context, String[] glassType, int[] glassResId) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.glassType = glassType;
        this.glassResId = glassResId;
    }

    @Override
    public int getCount() {
        return glassResId.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.layout_virtual_assistant, null);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_glass);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tv_glass_name);

        viewHolder.ivIcon.setImageResource(glassResId[position]);
        viewHolder.tvName.setText(glassType[position]);


        return view;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
    }
}
