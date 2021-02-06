package com.kolkatahaat.adapterview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.ItemTouchHelperAdapter;
import com.kolkatahaat.interfaces.ItemTouchHelperViewHolder;
import com.kolkatahaat.interfaces.OnStartDragListener;
import com.kolkatahaat.interfaces.RecyclerViewProductClickListener;
import com.kolkatahaat.model.SliderImgItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminSliderImageAdapter extends RecyclerView.Adapter<AdminSliderImageAdapter.ViewHolder>  implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<SliderImgItem> messages;
    //private RecyclerViewProductClickListener mListener;
    OnItemClickListener mItemClickListener;
    private final OnStartDragListener mDragStartListener;

    public AdminSliderImageAdapter(Context mContext, List<SliderImgItem> messages, OnStartDragListener dragListner) {
        this.mContext = mContext;
        this.messages = messages;
        this.mDragStartListener = dragListner;
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

        holder.image_menu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_slider_image, parent, false);

        return new ViewHolder(itemView);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , ItemTouchHelperViewHolder {
        public ImageView imgSlider, image_menu;
        public TextView txtDate;

        //private RecyclerViewProductClickListener mListener;

        public ViewHolder(View view) {
            super(view);
            //mListener = listener;
            imgSlider = view.findViewById(R.id.imgSlider);
            image_menu = view.findViewById(R.id.image_menu);
            //txtDate = view.findViewById(R.id.txtDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
        /*@Override
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
        }*/
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
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
