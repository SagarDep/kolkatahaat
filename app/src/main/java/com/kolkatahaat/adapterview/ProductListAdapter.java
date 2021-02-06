package com.kolkatahaat.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.RecyclerViewRemoveClickListener;
import com.kolkatahaat.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private Context mContext;
    private List<Product> messages;
    private RecyclerViewRemoveClickListener mListener;

    public ProductListAdapter(Context mContext, List<Product> messages, RecyclerViewRemoveClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_list, parent, false);

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


    public List<Product> getEatableItem(List<Product> productsItem) {
        List<Product> productsEatable = new ArrayList<>();
       /* for (final ListIterator<Product> i = messages.listIterator(); i.hasNext();) {
            Product cartProduct = new Product();
            final Product element = i.next();
            if(element.getProductId().equals("Eatable")) {
                i.set(cartProduct);
            }
        }*/

        for (Product pojoOfJsonArray : productsItem) {
            if(pojoOfJsonArray.getProductCategory().equals("Eatable")) {
                productsEatable.add(pojoOfJsonArray);
            }
        }
        //messages = productsEatable;
        return productsEatable;
    }

    public List<Product> getClothingItem(List<Product> productsItem) {
        List<Product> productsEatable = new ArrayList<>();
        for (Product pojoOfJsonArray : productsItem) {
            if(pojoOfJsonArray.getProductCategory().equals("Clothing")) {
                productsEatable.add(pojoOfJsonArray);
            }
        }
        //messages = productsEatable;
        return productsEatable;
    }

    public List<Product> getPujaItemsItem(List<Product> productsItem) {
        List<Product> productsEatable = new ArrayList<>();
        for (Product pojoOfJsonArray : productsItem) {
            if(pojoOfJsonArray.getProductCategory().equals("Puja Items")) {
                productsEatable.add(pojoOfJsonArray);
            }
        }
        //messages = productsEatable;
        return productsEatable;
    }

    public List<Product> getOthersItem(List<Product> productsItem) {

        List<Product> productsEatable = new ArrayList<>();
        for (Product pojoOfJsonArray : productsItem) {
            if(pojoOfJsonArray.getProductCategory().equals("Others")) {
                productsEatable.add(pojoOfJsonArray);
            }
        }
        //messages = productsEatable;
        return productsEatable;
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


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Product message = messages.get(position);

        // displaying text view data
        Glide.with(mContext).load(message.getProductImg())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProduct);
        holder.txtProductName.setText(message.getProductName());
        holder.txtProductCategory.setText(message.getProductCategory());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imgProduct;
        public LinearLayout llContainer;
        public TextView txtProductName, txtProductCategory;
        public ImageView imgProductDelete;

        private RecyclerViewRemoveClickListener mListener;

        public ViewHolder(View view, RecyclerViewRemoveClickListener listener) {
            super(view);
            mListener = listener;
            imgProduct = view.findViewById(R.id.imgProduct);
            llContainer = view.findViewById(R.id.llContainer);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductCategory = view.findViewById(R.id.txtProductCategory);
            imgProductDelete = view.findViewById(R.id.imgProductDelete);
            llContainer.setOnClickListener(this);
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
