package com.whitedevils.vrtex20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ListAdapter extends ArrayAdapter<productModel> {
    Context context;
    int res;
    public ListAdapter(@NonNull Context context, int resource, @NonNull List<productModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String product  = getItem(position).getProduct();
        String price = getItem(position).getPrice();
        String qty = getItem(position).getQty();
        String total = String.valueOf(Integer.parseInt(price)*Integer.parseInt(qty));
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(res,parent,false);

        TextView product_textview = convertView.findViewById(R.id.product_textview);
        TextView price_textview = convertView.findViewById(R.id.price_textview);
        TextView qty_textview = convertView.findViewById(R.id.quantity_textview);
        TextView total_textview = convertView.findViewById(R.id.total_textview);

        product_textview.setText(product);
        price_textview.setText(price);
        qty_textview.setText(qty);
        total_textview.setText(total);
        return convertView;

    }
}
