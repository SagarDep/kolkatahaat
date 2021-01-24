package com.kolkatahaat.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.kolkatahaat.R;
import com.kolkatahaat.fragments.AboutUsFragment;
import com.kolkatahaat.fragments.EditProfileFragment;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.view.admin.fragments.AdminAllUserListFragment;
import com.kolkatahaat.view.admin.fragments.AdminOrdersFragment;
import com.kolkatahaat.view.admin.fragments.AdminProductListFragment;

public class HomeAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    private DrawerLayout drawer;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        fireAuth = FirebaseAuth.getInstance();

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); //disable tint on each icon to use color icon svg
        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);*/
        if (navigationView.getHeaderCount() > 0) {
            View headerLayout = navigationView.getHeaderView(0);


            Gson gson = new Gson();
            Object userDetial = SharedPrefsUtils.getFromPrefs(HomeAdminActivity.this, SharedPrefsUtils.USER_DETAIL, "");
            Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);

            TextView txtName = headerLayout.findViewById(R.id.txtName);
            TextView txtEmailAddress = headerLayout.findViewById(R.id.txtEmailAddress);

            txtName.setText(obj.getUserName());
            txtEmailAddress.setText(obj.getUserEmail());
        }

        loadFragment(new AdminProductListFragment()); //<----- enable this line
    }


    /**
     * Fragment
     **/
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            // Create the transaction
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.content_frame, fragment);
            //fts.addToBackStack("optional tag");
            fts.commit();
            return true;
        }
        return false;
    }


    /**
     * Menu Bottom Navigation Drawer
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_product:
                fragment = new AdminProductListFragment();
                break;

            case R.id.nav_users_list:
                fragment = new AdminAllUserListFragment();
                break;

            case R.id.nav_users_orders:
                fragment = new AdminOrdersFragment();
                break;

            case R.id.nav_about_us:
                fragment = new AboutUsFragment();
                break;


            case R.id.nav_profile:
                fragment = new EditProfileFragment();
                break;

            case R.id.nav_logout:
                fireAuth.signOut();
                SharedPrefsUtils.removeFromPrefs(HomeAdminActivity.this,SharedPrefsUtils.USER_DETAIL);
                Intent intent = new Intent(HomeAdminActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return loadFragment(fragment);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void dialogExit() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Apakah anda yakin ingin keluar?");
        dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
}
