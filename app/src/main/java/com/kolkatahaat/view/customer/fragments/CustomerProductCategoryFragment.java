package com.kolkatahaat.view.customer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;

import com.kolkatahaat.model.SliderImgItem;
import com.kolkatahaat.view.customer.ProductListActivity;

import java.util.ArrayList;

public class CustomerProductCategoryFragment extends Fragment implements View.OnClickListener{

    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private CollectionReference collectionReference;

    SliderLayout mDemoSlider ;
    ArrayList<SliderImgItem> listUrl = new ArrayList<>();

    RelativeLayout llEatable;
    RelativeLayout llPujaItem;
    RelativeLayout llClothing;
    RelativeLayout llOther;

    public CustomerProductCategoryFragment() {
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        collectionReference = fireStore.collection("slider");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_customer_product_category, container, false);

        init(view);



        /*RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(getActivity());

            sliderView
                    .image(listUrl.get(i).getImgUrl())
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);

            mDemoSlider.addSlider(sliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);
        mDemoSlider.stopCyclingWhenTouch(false);*/

        return view;
    }

    private void init(View view) {
        mDemoSlider = view.findViewById(R.id.slider);
        llEatable = view.findViewById(R.id.llEatable);
        llPujaItem = view.findViewById(R.id.llPujaItem);
        llClothing = view.findViewById(R.id.llClothing);
        llOther = view.findViewById(R.id.llOther);
        llEatable.setOnClickListener(this);
        llPujaItem.setOnClickListener(this);
        llClothing.setOnClickListener(this);
        llOther.setOnClickListener(this);


       /* listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");


        listUrl.add("https://tctechcrunch2011.files.wordpress.com/2017/02/android-studio-logo.png");


        listUrl.add("http://static.tumblr.com/7650edd3fb8f7f2287d79a67b5fec211/3mg2skq/3bdn278j2/tumblr_static_idk_what.gif");


        listUrl.add("http://www.gstatic.com/webp/gallery/1.webp");*/

        collectionReference.orderBy("currentDate", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.centerCrop();

                    for (DocumentSnapshot document : task.getResult()) {
                        SliderImgItem sliderImgItem = document.toObject(SliderImgItem.class);
                        listUrl.add(sliderImgItem);

                        TextSliderView sliderView = new TextSliderView(getActivity());

                        sliderView
                                .image(sliderImgItem.getImgUrl())
                                .setRequestOption(requestOptions)
                                .setProgressBarVisible(true);

                        mDemoSlider.addSlider(sliderView);
                    }

                    mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);

                    mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                    mDemoSlider.setDuration(5000);
                    mDemoSlider.stopCyclingWhenTouch(false);
                }
            }
        });


    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch(v.getId()){

            case R.id.llEatable:
                intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("EXTRA_TAB_POSITION", 0);

                startActivity(intent);
                break;

            case R.id.llPujaItem:
                intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("EXTRA_TAB_POSITION", 1);
                startActivity(intent);
                break;

            case R.id.llClothing:
                intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("EXTRA_TAB_POSITION", 2);
                startActivity(intent);
                break;

            case R.id.llOther:
                intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("EXTRA_TAB_POSITION", 3);
                startActivity(intent);
                break;
        }
    }


}
