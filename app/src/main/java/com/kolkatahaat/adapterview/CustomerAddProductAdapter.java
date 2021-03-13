package com.kolkatahaat.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.RecyclerViewProductClickListener;
import com.kolkatahaat.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddProductAdapter extends RecyclerView.Adapter<CustomerAddProductAdapter.ViewHolder> {
    private Context mContext;
    private List<Product> messages;
    private RecyclerViewProductClickListener mListener;

    public CustomerAddProductAdapter(Context mContext, List<Product> messages, RecyclerViewProductClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_add_product, parent, false);

        return new ViewHolder(itemView, mListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<Product> dataset) {
        messages.clear();
        messages.addAll(dataset);
        notifyDataSetChanged();
    }

    public void updateDataVal(final List<Product> stationArrivalPOJO ) {
        messages = new ArrayList<>();
        messages.addAll(stationArrivalPOJO);
    }

    public void updatePositionData(int prdQuantity, final int index) {
        messages.get(index).setProductQuantity(prdQuantity);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Product message = messages.get(position);

        // displaying text view data
        Glide.with(mContext).load(message.getProductImg())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProduct);
        holder.txtProductName.setText(message.getProductName());
        //holder.txtProductCategory.setText(message.getProductCategory());
        holder.txtProductPrice.setText(String.valueOf(message.getProductPrice()));
        holder.txtProductPacking.setText(String.valueOf(message.getProductPacking()));
        holder.txtQuantitySize.setText(String.valueOf(message.getProductQuantity()));

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imgProduct;
        public LinearLayout llContainer;
        public TextView txtProductName, txtProductPacking, txtProductPrice, txtQuantitySize;
        public Button btnDecrease, btnIncrease ;

        private RecyclerViewProductClickListener mListener;

        public ViewHolder(View view, RecyclerViewProductClickListener listener) {
            super(view);
            mListener = listener;
            imgProduct = view.findViewById(R.id.imgProduct);
            llContainer = view.findViewById(R.id.llContainer);
            txtProductName = view.findViewById(R.id.txtProductName);
            //txtProductCategory = view.findViewById(R.id.txtProductCategory);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtProductPacking = view.findViewById(R.id.txtProductPacking);


            btnDecrease = view.findViewById(R.id.btnDecrease);
            txtQuantitySize = view.findViewById(R.id.txtQuantitySize);
            btnIncrease = view.findViewById(R.id.btnIncrease);
            //llContainer.setOnClickListener(this);
            imgProduct.setOnClickListener(this);
            btnDecrease.setOnClickListener(this);
            btnIncrease.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //mListener.onClick(view, getAdapterPosition());
            switch (view.getId()) {
                case R.id.btnDecrease:
                    mListener.onClickDecrease(view, getAdapterPosition());
                    break;
                case R.id.btnIncrease:
                    mListener.onClickIncrease(view, getAdapterPosition());
                    break;
                    case R.id.imgProduct:
                    mListener.onClick(view, getAdapterPosition());
                    break;

                default:
                    break;
            }
        }
    }
}
