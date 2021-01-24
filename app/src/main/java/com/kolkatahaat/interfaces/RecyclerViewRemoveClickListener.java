package com.kolkatahaat.interfaces;

import android.view.View;

public interface RecyclerViewRemoveClickListener {
    void onClick(View view, int position);
    void onRemoveItem(View view, int position);
}
