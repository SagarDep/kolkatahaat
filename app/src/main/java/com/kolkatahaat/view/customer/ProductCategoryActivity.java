package com.kolkatahaat.view.customer;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.CategoryAdapter;
import com.kolkatahaat.model.CategoryItem;

import java.util.ArrayList;

public class ProductCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_category);

        recyclerView = findViewById(R.id.mRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        categoryAdapter = new CategoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.setData(getList());
    }


    public ArrayList<CategoryItem> getList() {

        ArrayList<CategoryItem> models = new ArrayList<>();

        CategoryItem model1_1 = new CategoryItem("Carmelita Badura", 1, "Chief Executive Officer");
        CategoryItem model1_1_1 = new CategoryItem("Elisha Pickert", 2, "Accounts Manager");
        CategoryItem model1_1_2 = new CategoryItem("Patricia Netherton", 2, "Recruitment Manager");
        CategoryItem model1_1_3 = new CategoryItem("Christeen Petrey", 2, "Technology Manager ");
        CategoryItem model1_1_4 = new CategoryItem("Lolita Moreman", 2, "Store Manager");
        model1_1.models.add(model1_1_1);
        model1_1.models.add(model1_1_2);
        model1_1.models.add(model1_1_3);
        model1_1.models.add(model1_1_4);

        CategoryItem model1_2 = new CategoryItem("Manuela Kass", 1, "Chief Operating Officer");
        CategoryItem model1_2_1 = new CategoryItem("Roseanna Branham", 2, "Regional Managers");

        CategoryItem model1_2_1_1 = new CategoryItem("Dennise Lasso", 3, "Functional Managers");
        CategoryItem model1_2_1_2 = new CategoryItem("Sabrina Shively", 3, "Departmental Manager");
        CategoryItem model1_2_1_3 = new CategoryItem("Jin Haecker", 3, "Compensation and Benefits Manager");
        CategoryItem model1_2_1_4 = new CategoryItem("Season Parrett  ", 3, "General Manager");
        model1_2_1.models.add(model1_2_1_1);
        model1_2_1.models.add(model1_2_1_2);
        model1_2_1.models.add(model1_2_1_3);
        model1_2_1.models.add(model1_2_1_4);

        CategoryItem model1_2_2 = new CategoryItem("Vicky Parkhurst", 2, "IT Manager");
        CategoryItem model1_2_3 = new CategoryItem("Taisha Dragoo", 2, "Food Service Manager");
        CategoryItem model1_2_4 = new CategoryItem("Abbey Ballance", 2, "Medical Services Manager");
        model1_2.models.add(model1_2_1);

        CategoryItem model1_3 = new CategoryItem("Arlinda Fogal", 1, "Chief Financial Officer");

        CategoryItem model1_4 = new CategoryItem("Stephen Cabe", 1, "Chief Technology Officer");
        CategoryItem model1_4_1 = new CategoryItem("Cherilyn Lehn", 2, "Advertising Manager");
        CategoryItem model1_4_2 = new CategoryItem("Lashay Baumer", 2, "Affiliate Management Associate");
        CategoryItem model1_4_3 = new CategoryItem("Abbie Kilmer", 2, "Branch Manager");
        CategoryItem model1_4_4 = new CategoryItem("Clinton Boyers", 2, "Budget Manager");
        model1_4.models.add(model1_4_1);
        model1_4.models.add(model1_4_2);
        model1_4.models.add(model1_4_3);
        model1_4.models.add(model1_4_4);

        models.add(model1_1);
        models.add(model1_2);
        models.add(model1_3);
        models.add(model1_4);

        return models;
    }
}
