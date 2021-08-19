package com.whitedevils.vrtex20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class reportAdapter extends RecyclerView.Adapter<reportAdapter.ViewHolder> {
    Context context;
    ArrayList<String> date, invoiceno, total;



    public reportAdapter(Context context, ArrayList<String> date, ArrayList<String> invoiceno, ArrayList<String> total) {
        this.context = context;
        this.date = date;
        this.invoiceno = invoiceno;
        this.total = total;
    }

    private ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void onItemClickListener(int position);
    }

    public void setItemClickListener(ItemClickListener listener){
        itemClickListener= listener;
    }

    int pos=0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View v = inflater.inflate(R.layout.report_adapter,parent,false);

        return new ViewHolder(v, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(String.valueOf(date.get(position)));
        holder.invo_no.setText(String.valueOf(invoiceno.get(position)));
        holder.gtotal.setText(String.valueOf(total.get(position)));

    }

    @Override
    public int getItemCount() {
        return invoiceno.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,invo_no,gtotal;
        public ViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            invo_no = itemView.findViewById(R.id.invo_number);
            gtotal = itemView.findViewById(R.id.total);

            itemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClickListener(position);
                    }
                }
            });
        }
    }
}
