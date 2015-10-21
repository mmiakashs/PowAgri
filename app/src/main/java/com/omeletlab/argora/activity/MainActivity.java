package com.omeletlab.argora.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.omeletlab.argora.R;
import com.omeletlab.argora.adapter.ViewPagerAdapter;
import com.omeletlab.argora.fragment.HomeAllCropsFragment;
import com.omeletlab.argora.util.GlobalConstant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Drawer result;
    private ViewPager viewPager;
    private Toolbar toolbar;

    public static int selectOption = 0;
    public static String selectedText = "";
    private int fragmentID;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalConstant.mContext = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);*/

        PrimaryDrawerItem homeDrawerItem = new PrimaryDrawerItem().withName(R.string.nav_item_home);
        SecondaryDrawerItem inboxDrawerItem = new SecondaryDrawerItem().withName(R.string.nav_item_price);
        SecondaryDrawerItem sentDrawerItem = new SecondaryDrawerItem().withName(R.string.nav_item_expenses);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.header)
                .withSelectedItem(-1)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        homeDrawerItem,
                        new DividerDrawerItem(),
                        inboxDrawerItem,
                        sentDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.d("drawerItemClick",""+position);
                        if(position==3){
                            selectOption=-1;
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title("Select State")
                                    .items(getResources().getStringArray(R.array.state_name))
                                    .autoDismiss(false)
                                    .forceStacking(false)
                                    .positiveText("Select")
                                    .negativeText("Close")
                                    .alwaysCallSingleChoiceCallback()
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                            if (text != null) {
                                                selectOption = which;
                                                selectedText = text.toString();
                                                Log.d("selectStateName Choose", selectedText);
                                            }
                                            return true;
                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            materialDialog.dismiss();
                                            if (selectOption != -1) displayView(2);
                                        }
                                    })
                                    .show();
                        }
                        return false;
                    }
                }).build();

        displayView(0);

    }
/*
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeAllCropsFragment(), "Home");
        viewPager.setAdapter(adapter);
    }*/

    private void displayView(int position) {
        // update the main content with called Fragment
        Fragment fragment = null;
        switch (position) {
            case 0:
                bundle = new Bundle();
                bundle.putString(GlobalConstant.TAG_YEAR, "2014");
                bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "YIELD");
                bundle.putString(GlobalConstant.TAG_STATE_NAME,"");
                bundle.putString(GlobalConstant.TAG_CROP_NAME,"");
                fragment = new HomeAllCropsFragment();
                fragment.setArguments(bundle);
                fragmentID = 0;
                break;
            case 2:
                Log.d("selectStateName",selectedText);
                Bundle bundle = new Bundle();
                bundle.putString(GlobalConstant.TAG_YEAR, "2014");
                bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "YIELD");
                bundle.putString(GlobalConstant.TAG_STATE_NAME, selectedText);
                bundle.putString(GlobalConstant.TAG_CROP_NAME,"");
                fragment = new HomeAllCropsFragment();
                fragment.setArguments(bundle);
                fragmentID = 1;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        } else {
            Log.e("this is mainActivity", "Error in else case");
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
