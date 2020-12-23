package com.kolkatahaat.utills;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by MW01 on 17-03-2017.
 */

public class TextViewRegular extends AppCompatTextView {
	private Context context;
	public TextViewRegular(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	public TextViewRegular(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	public TextViewRegular(Context context) {
		super(context);
		this.context = context;
		init();
	}
	public void init() {
        if (isInEditMode())
            return;

       /* if(Constants.ISLANGUAGE){
			Typeface customFont = FontCache.getTypeface("fonts/shruti.ttf", context);
			setTypeface(customFont);
        }else {
			Typeface customFont = FontCache.getTypeface("fonts/shruti.ttf", context);
			setTypeface(customFont);
        }*/
    }
}
