package com.kolkatahaat.adapterview;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kolkatahaat.R;
import com.kolkatahaat.model.CategoryItem;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryItem> modelsAll = new ArrayList<>();

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_customer_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final CategoryItem model = modelsAll.get(position);
        holder.textView.setText(model.name);
        holder.tvDesignation.setText(model.designation);

        holder.itemView.setTag(R.string.MODEL, model);
        holder.itemView.setTag(R.string.position, position);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.rlContent.getLayoutParams();
        layoutParams.setMargins(((int) convertDpToPixel(20, context)) * model.level, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);

        switch (model.state) {

            case CLOSED:
                holder.imgArrow.setImageResource(R.drawable.arrow_right_filled);
                break;
            case OPENED:
                holder.imgArrow.setImageResource(R.drawable.arrow_down_filled);
                break;
        }

        if (model.models.isEmpty()) {
            holder.imgArrow.setVisibility(View.INVISIBLE);
            holder.viewDashed.setVisibility(View.VISIBLE);
        } else {
            holder.imgArrow.setVisibility(View.VISIBLE);
            holder.viewDashed.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = (int) v.getTag(R.string.position);
                CategoryItem rootModel = (CategoryItem) v.getTag(R.string.MODEL);
                if (rootModel.models.isEmpty()) {
                    return;
                }
                switch (rootModel.state) {

                    case CLOSED:
                        modelsAll.addAll(position + 1, rootModel.models);
                        notifyItemRangeInserted(position + 1, rootModel.models.size());
                        notifyItemRangeChanged(position + rootModel.models.size(), modelsAll.size() - (position + rootModel.models.size()));
                        notifyItemRangeChanged(position, modelsAll.size() - position);
                        rootModel.state = CategoryItem.STATE.OPENED;
                        break;

                    case OPENED:

                        int start = position + 1;
                        int end = modelsAll.size();
                        for (int i = start; i < modelsAll.size(); i++) {
                            CategoryItem model1 = modelsAll.get(i);
                            if (model1.level <= rootModel.level) {
                                end = i;
                                break;
                            } else {
                                if (model1.state == CategoryItem.STATE.OPENED) {
                                    model1.state = CategoryItem.STATE.CLOSED;
                                }
                            }
                        }

                        if (end != -1) {
                            modelsAll.subList(start, end).clear();
                            notifyItemRangeRemoved(start, end - start);
                            notifyItemRangeChanged(start, end - start);
                            notifyItemRangeChanged(position, modelsAll.size() - position);
                        }

                        rootModel.state = CategoryItem.STATE.CLOSED;
                        break;
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return modelsAll.size();
    }

    public void setData(ArrayList<CategoryItem> list) {
        modelsAll = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rlContent;
        TextView textView;
        TextView tvDesignation;
        ImageView imgArrow;
        View viewDashed;

        public ViewHolder(View itemView) {

            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.txtCategoryName);
            imgArrow = (ImageView) itemView.findViewById(R.id.imgArrow);
            rlContent = (LinearLayout) itemView.findViewById(R.id.rlContent);
            viewDashed = itemView.findViewById(R.id.viewDashed);
            tvDesignation = (TextView) itemView.findViewById(R.id.txtCategoryDes);
        }
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}
