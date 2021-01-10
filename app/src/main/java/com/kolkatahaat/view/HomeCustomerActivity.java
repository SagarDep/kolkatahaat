package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.view.customer.fragments.CustomerProductCategoryFragment;
import com.kolkatahaat.view.customer.fragments.OrdersFragment;

public class HomeCustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    private MenuItem menuHome, menuPengurus, menuSaranDanKritikan, menuAbout, menuLogout;
    private DrawerLayout drawer;
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        fireAuth = FirebaseAuth.getInstance();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /*drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);*/


        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); //disable tint on each icon to use color icon svg
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
       if (navigationView.getHeaderCount() > 0) {
            View headerLayout = navigationView.getHeaderView(0);


            Gson gson = new Gson();
            Object userDetial = SharedPrefsUtils.getFromPrefs(HomeCustomerActivity.this, SharedPrefsUtils.USER_DETAIL, "");
            Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);

            TextView txtName = headerLayout.findViewById(R.id.txtName);
            TextView txtEmailAddress = headerLayout.findViewById(R.id.txtEmailAddress);

            txtName.setText(obj.getUserName());
            txtEmailAddress.setText(obj.getUserEmail());
       }

        loadFragment(new CustomerProductCategoryFragment()); //<----- enable this line


        //custom header view
        /*View headerView = navigationView.getHeaderView(0);
        LinearLayout container = headerView.findViewById(R.id.llProfileView);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditProfileFragment.class));
            }
        });*/

        /*AppCompatTextView navUserName = headerView.findViewById(R.id.atv_name_header);
        navUserName.setText("Budi");

        TextView navEmail = headerView.findViewById(R.id.tv_email_header);
        navEmail.setText("budi@gmail.com");*/
    }

    /**
     * Fragment
     **/
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            /*getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();*/

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
                fragment = new CustomerProductCategoryFragment(); //<----- enable this line
                break;
            case R.id.nav_users_list:
                //fragment = new AdminAllUserListFragment();  //<----- enable this line
                break;

            case R.id.nav_users_orders:
                fragment = new OrdersFragment();
                //fragment = new EditProfileFragment();  //<----- enable this line
                break;

            case R.id.nav_profile:
                //fragment = new EditProfileFragment();  //<----- enable this line
                break;

            case R.id.nav_about_us:
                //fragment = new AboutUsFragment();  //<----- enable this line
                break;

            case R.id.nav_logout:
                fireAuth.signOut();
                SharedPrefsUtils.removeFromPrefs(HomeCustomerActivity.this,SharedPrefsUtils.USER_DETAIL);
                Intent intent = new Intent(HomeCustomerActivity.this, LoginActivity.class);
                startActivity(intent);
                //fragment = new AboutUsFragment();  //<----- enable this line
                break;
            /*case R.id.menu_about:
                //fragment = new AboutFragment();  <----- enable this line
                break;
            case R.id.menu_logout:
                dialogExit();
                break;*/
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return loadFragment(fragment);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //Hidden Menu Bard For All Fragments
        /*menuHome = menu.findItem(R.id.nav_home);
        menuPengurus = menu.findItem(R.id.nav_gallery);
        menuSaranDanKritikan = menu.findItem(R.id.nav_slideshow);
        menuAbout = menu.findItem(R.id.menu_about);
        menuLogout = menu.findItem(R.id.menu_logout);

        if (menuHome != null && menuPengurus != null && menuSaranDanKritikan != null &&
                menuAbout != null && menuLogout != null)

            menuHome.setVisible(false);
        menuPengurus.setVisible(false);
        menuSaranDanKritikan.setVisible(false);
        menuAbout.setVisible(false);
        menuLogout.setVisible(false);*/

        return super.onPrepareOptionsMenu(menu);
    }


    /*private void dialogExit() {
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
    }*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.admin_menu, menu);
       return true;
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
}
