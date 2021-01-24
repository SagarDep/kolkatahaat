package com.kolkatahaat.interfaces;

import android.view.View;

public interface RecyclerViewProductClickListener {
    void onClick(View view, int position);
    void onClickDecrease(View view, int position);
    void onClickIncrease(View view, int position);
}
