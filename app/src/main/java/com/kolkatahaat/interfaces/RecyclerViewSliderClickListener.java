package com.kolkatahaat.interfaces;

import android.view.View;

public interface RecyclerViewSliderClickListener {
    void onClick(View view, int position);
    void onClickEdit(View view, int position);
    void onClickDelete(View view, int position);
}
