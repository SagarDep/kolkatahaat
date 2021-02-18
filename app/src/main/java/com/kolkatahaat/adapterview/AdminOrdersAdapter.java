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
import com.kolkatahaat.interfaces.ItemClickListener;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.utills.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.ViewHolder> {
    private Context mContext;
    private List<BillItem> messages;
    private RecyclerViewClickListener mListener;

    public AdminOrdersAdapter(Context mContext, List<BillItem> messages,RecyclerViewClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    public AdminOrdersAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(itemView,mListener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*public int grandTotal(List<BillItem> items){
        int totalPrice = 0;
        for(int i = 0 ; i < items.size(); i++) {
            totalPrice += items.get(i).getProductTotalAmount();
        }
        return totalPrice;
    }*/

    public void removeAt(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messages.size());
    }

    public void updateData(List<BillItem> dataset) {
        messages.clear();
        messages.addAll(dataset);
        notifyDataSetChanged();
    }

    public void updateDataVal(final List<BillItem> stationArrivalPOJO ) {
        messages = new ArrayList<>();
        messages.addAll(stationArrivalPOJO);
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(messages.size() != 0) {
            BillItem message = messages.get(position);

            // displaying text view data
            /*Glide.with(mContext).load(R.drawable.ic_cart)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);*/
            holder.thumbnail.setImageDrawable(mContext.getResources().getDrawable(R.drawable.app_logo));
            holder.txtCategoryName.setText(message.getOrderStatus());
            holder.txtOrderUserName.setText(message.getItemUsers().getUserName());
            holder.txtTotOrderItems.setText(String.valueOf(message.getItemArrayList().size()));

            Timestamp timestamp = new Timestamp(((Timestamp) message.getBillCreatedDate()).toDate());
            Date date1 = timestamp.toDate();
            holder.txtCategoryDes.setText(Utility.getDateTime(date1));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;
        public TextView txtCategoryName, txtOrderUserName, txtTotOrderItems, txtCategoryDes;
        private RecyclerViewClickListener mListener;

        public ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            thumbnail = view.findViewById(R.id.thumbnail);
            txtCategoryName = view.findViewById(R.id.txtCategoryName);
            txtOrderUserName = view.findViewById(R.id.txtOrderUserName);
            txtTotOrderItems = view.findViewById(R.id.txtTotOrderItems);
            txtCategoryDes = view.findViewById(R.id.txtCategoryDes);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
