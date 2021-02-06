package com.kolkatahaat.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.RecyclerViewRemoveClickListener;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.model.Product;

import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ViewHolder> {
    private Context mContext;
    private List<OrdersItem> messages;
    private RecyclerViewRemoveClickListener mListener;

    public ProductCartAdapter(Context mContext, List<OrdersItem> messages, RecyclerViewRemoveClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new ViewHolder(itemView, mListener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int grandTotal(List<OrdersItem> items){
        int totalPrice = 0;
        for(int i = 0 ; i < items.size(); i++) {
            totalPrice += items.get(i).getProductTotalAmount();
        }
        return totalPrice;
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
        OrdersItem message = messages.get(position);

        // displaying text view data
        Glide.with(mContext).load(message.getProductImg())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProduct);
        holder.txtProductName.setText(message.getProductName());
        holder.txtProductCategory.setText(message.getProductCategory());
        //holder.txtProductCharge.setText(String.valueOf(message.getProductDeliveryChange()));
        holder.txtProductQuantity.setText(String.valueOf(message.getProductQuantity()));
        holder.txtProductTotal.setText(String.valueOf(message.getProductTotalAmount()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public LinearLayout llCartItem;
        public ImageView imgProduct, imgProductDelete;
        public TextView txtProductName, txtProductCategory, txtProductQuantity, txtProductTotal;//txtProductCharge, ;
        private RecyclerViewRemoveClickListener mListener;

        public ViewHolder(View view, RecyclerViewRemoveClickListener listener) {
            super(view);
            mListener = listener;
            llCartItem = view.findViewById(R.id.llCartItem);
            imgProduct = view.findViewById(R.id.imgProduct);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductCategory = view.findViewById(R.id.txtProductCategory);
            txtProductQuantity = view.findViewById(R.id.txtProductQuantity);
            //txtProductCharge = view.findViewById(R.id.txtProductCharge);
            txtProductTotal = view.findViewById(R.id.txtProductTotal);
            imgProductDelete = view.findViewById(R.id.imgProductDelete);
            llCartItem.setOnClickListener(this);
            imgProductDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.imgProductDelete) {
                mListener.onRemoveItem(view, getAdapterPosition());
            } else {
                mListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
