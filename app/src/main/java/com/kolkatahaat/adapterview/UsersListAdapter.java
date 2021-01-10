package com.kolkatahaat.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.Utility;

import java.util.Date;
import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {

    private Context mContext;
    private List<Users> messages;
    private RecyclerViewClickListener mListener;

    public UsersListAdapter(Context mContext, List<Users> messages, RecyclerViewClickListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.mListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_users_list, parent, false);

        return new UserViewHolder(itemView, mListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(List<Users> dataset) {
        messages.clear();
        messages.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int position) {
        Users message = messages.get(position);

        // displaying text view data
        /*Glide.with(mContext).load(message.getProductImg())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProduct);*/
        holder.txtUserName.setText(message.getUserName());
        holder.txtUserMobile.setText(message.getUserMobile());
        //holder.txtUserDate.setText(String.valueOf(message.getUserCreatedDate()));
        Timestamp timestamp = new Timestamp(((Timestamp) message.getUserCreatedDate()).toDate());
        Date date1 = timestamp.toDate();
        holder.txtUserDate.setText(Utility.getDateTime(date1));
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imgUser;
        public LinearLayout llContainer;
        public TextView txtUserName, txtUserMobile, txtUserDate;

        private RecyclerViewClickListener mListener;

        public UserViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            imgUser = view.findViewById(R.id.imgUser);
            llContainer = view.findViewById(R.id.llContainer);

            txtUserName = view.findViewById(R.id.txtUserName);
            txtUserMobile = view.findViewById(R.id.txtUserMobile);
            txtUserDate = view.findViewById(R.id.txtUserDate);
            llContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
