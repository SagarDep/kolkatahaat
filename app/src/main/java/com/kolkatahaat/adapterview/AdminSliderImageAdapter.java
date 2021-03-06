package com.kolkatahaat.adapterview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.ItemTouchHelperAdapter;
import com.kolkatahaat.interfaces.RecyclerViewSliderClickListener;
import com.kolkatahaat.model.SliderImgItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminSliderImageAdapter extends RecyclerView.Adapter<AdminSliderImageAdapter.ViewHolder>  implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<SliderImgItem> messages;
    private RecyclerViewSliderClickListener mListener;

    public AdminSliderImageAdapter(Context mContext, List<SliderImgItem> messages, RecyclerViewSliderClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    public void updateDataVal(final List<SliderImgItem> stationArrivalPOJO ) {
        messages = new ArrayList<>();
        messages.addAll(stationArrivalPOJO);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SliderImgItem message = messages.get(position);

        // displaying text view data
        Glide.with(mContext).load(message.getImgUrl())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgSlider);
        //holder.txtDate.setText("");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_slider_image, parent, false);

        return new ViewHolder(itemView, mListener);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgSlider, image_edit, image_delete;
        public RecyclerViewSliderClickListener mListener;

        public ViewHolder(View view, RecyclerViewSliderClickListener listener) {
            super(view);
            mListener = listener;
            imgSlider = view.findViewById(R.id.imgSlider);
            image_edit = view.findViewById(R.id.image_edit);
            image_delete = view.findViewById(R.id.image_delete);
            itemView.setOnClickListener(this);
            image_edit.setOnClickListener(this);
            image_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image_edit:
                    mListener.onClickEdit(view, getAdapterPosition());
                    break;
                case R.id.image_delete:
                    mListener.onClickDelete(view, getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }




    @Override
    public void onItemDismiss(int position) {
        messages.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < messages.size() && toPosition < messages.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(messages, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(messages, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    public void updateList(List<SliderImgItem> list) {
        messages = list;
        notifyDataSetChanged();
    }
}
