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

import java.util.Date;
import java.util.List;

public class OrdersItemListAdapter extends RecyclerView.Adapter<OrdersItemListAdapter.ViewHolder> {
    private Context mContext;
    private List<BillItem> messages;
    private RecyclerViewClickListener mListener;

    public OrdersItemListAdapter(Context mContext, List<BillItem> messages, RecyclerViewClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    public OrdersItemListAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_order, parent, false);
        return new OrdersItemListAdapter.ViewHolder(itemView, mListener);
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

    public void updateData(List<BillItem> dataset) {
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
            BillItem message = messages.get(position);

            // displaying text view data
            Glide.with(mContext).load(mContext.getResources().getDrawable(R.mipmap.ic_launcher_round))
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
            holder.txtCategoryName.setText(message.getOrderStatus());

            Timestamp timestamp = new Timestamp(((Timestamp) message.getBillCreatedDate()).toDate());
            Date date1 = timestamp.toDate();
            holder.txtCategoryDes.setText(Utility.getDateTime(date1));

            if(message.getRejectionStatus()){
                holder.txtCategoryNote.setText(message.getRejectionNote());
            }

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;
        public TextView txtCategoryName, txtCategoryNote, txtCategoryDes;
        private RecyclerViewClickListener mListener;

        public ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            thumbnail = view.findViewById(R.id.thumbnail);
            txtCategoryName = view.findViewById(R.id.txtCategoryName);
            txtCategoryNote = view.findViewById(R.id.txtCategoryNote);
            txtCategoryDes = view.findViewById(R.id.txtCategoryDes);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
