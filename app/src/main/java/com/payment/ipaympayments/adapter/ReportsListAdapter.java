package com.payment.ipaympayments.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.payment.ipaympayments.R;

import java.util.List;

public class ReportsListAdapter extends BaseAdapter {

    private List<String> dataList;
    private LayoutInflater layoutInflater = null;
    private Context context;
   // public EditListener listener;

    public ReportsListAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    public class Holder {
        TextView tvMenuText;
        ImageView icon;


        public Holder(View rowView) {
            tvMenuText = (TextView) rowView.findViewById(R.id.tvOperator);
            //icon =  rowView.findViewById(R.id.menuImg);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SpannableStringBuilder builder;
        final Holder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.operator_row, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //final ProfileMenuModel model = dataList.get(position);
        holder.tvMenuText.setText(dataList.get(position));
        //holder.icon.setImageDrawable(context.getResources().getDrawable(model.getImgUrl()));
        return convertView;
    }
}
