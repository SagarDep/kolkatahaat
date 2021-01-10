package com.kolkatahaat.view.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.kolkatahaat.R;

import com.kolkatahaat.view.customer.ProductListActivity;

import java.util.ArrayList;
import java.util.List;

public class CustomerProductCategoryFragment extends Fragment implements View.OnClickListener{

    SliderLayout mDemoSlider ;
    ArrayList<String> listUrl = new ArrayList<>();
    ArrayList<String> listName = new ArrayList<>();

    RelativeLayout llEatable;
    RelativeLayout llPujaItem;
    RelativeLayout llClothing;

    public CustomerProductCategoryFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_customer_product_category, container, false);

        init(view);



        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(getActivity());

            sliderView
                    .image(listUrl.get(i))
                    .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);
                    //.setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        //mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.stopCyclingWhenTouch(false);

        return view;
    }

    private void init(View view) {
        mDemoSlider = view.findViewById(R.id.slider);
        llEatable = view.findViewById(R.id.llEatable);
        llPujaItem = view.findViewById(R.id.llPujaItem);
        llClothing = view.findViewById(R.id.llClothing);
        llEatable.setOnClickListener(this);
        llPujaItem.setOnClickListener(this);
        llClothing.setOnClickListener(this);


        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        listName.add("JPG - Github");

        listUrl.add("https://tctechcrunch2011.files.wordpress.com/2017/02/android-studio-logo.png");
        listName.add("PNG - Android Studio");

        listUrl.add("http://static.tumblr.com/7650edd3fb8f7f2287d79a67b5fec211/3mg2skq/3bdn278j2/tumblr_static_idk_what.gif");
        listName.add("GIF - Disney");

        listUrl.add("http://www.gstatic.com/webp/gallery/1.webp");
        listName.add("WEBP - Mountain");

    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.llEatable:
                Intent intent1 = new Intent(getActivity(), ProductListActivity.class);
                startActivity(intent1);
                break;

            case R.id.llPujaItem:
                Intent intent2 = new Intent(getActivity(), ProductListActivity.class);
                startActivity(intent2);
                break;

            case R.id.llClothing:
                Intent intent3 = new Intent(getActivity(), ProductListActivity.class);
                startActivity(intent3);
                break;
        }
    }


}
