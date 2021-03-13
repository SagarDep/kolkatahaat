package com.kolkatahaat.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.Timestamp;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.model.CategoryItem;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.utills.Utility;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrdersItemDetailsAdapter extends RecyclerView.Adapter<OrdersItemDetailsAdapter.ViewHolder> {
    private Context mContext;
    private List<OrdersItem> messages;

    public OrdersItemDetailsAdapter(Context mContext, List<OrdersItem> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    public OrdersItemDetailsAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_details, parent, false);
        return new OrdersItemDetailsAdapter.ViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public void removeAt(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messages.size());
    }

    public void updateData(List<OrdersItem> dataset) {
        messages.clear();
        messages.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(messages.size() != 0) {
            OrdersItem message = messages.get(position);

            // displaying text view data
            // displaying text view data
            Glide.with(mContext).load(message.getProductImg())
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProduct);
            holder.txtProductName.setText(message.getProductName());
            holder.txtProductQuantity.setText(message.getProductCategory());
            //holder.txtProductCharge.setText(message.getProductDeliveryChange());
            holder.txtProductTotal.setText(String.valueOf(message.getProductTotalAmount()));

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imgProduct;
        public TextView txtProductName, txtProductQuantity, txtProductTotal; //txtProductCharge, ;

        public ViewHolder(View view) {
            super(view);
            imgProduct = view.findViewById(R.id.imgProduct);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductQuantity = view.findViewById(R.id.txtProductQuantity);
            //txtProductCharge = view.findViewById(R.id.txtProductCharge);
            txtProductTotal = view.findViewById(R.id.txtProductTotal);
        }
    }
}
