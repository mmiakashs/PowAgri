package com.omeletlab.argora.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
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
    private TabLayout tabLayout;

    public static int selectOption = 0;
    public static String selectedText = "";
    private int fragmentID;
    private Bundle bundle;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalConstant.mContext = MainActivity.this;
        parentLayout = findViewById(R.id.root_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        PrimaryDrawerItem homeDrawerItem = new PrimaryDrawerItem().withName(R.string.nav_item_home);
        PrimaryDrawerItem productBasedAnalysisDrawerItem = new PrimaryDrawerItem().withName(R.string.nav_item_product_based_analysis);
        PrimaryDrawerItem stateBasedDrawerItem = new PrimaryDrawerItem().withName(R.string.nav_item_state_based_analysis);
        PrimaryDrawerItem compareBasedDrawerItem = new PrimaryDrawerItem().withName(R.string.nav_item_compare_crops_analysis);

        SecondaryDrawerItem yieldAnalysis = new SecondaryDrawerItem().withName(R.string.nav_item_yield_based_analysis);
        SecondaryDrawerItem areaHarvestedAnalysis = new SecondaryDrawerItem().withName(R.string.nav_item_area_harvested_based_analysis);
        SecondaryDrawerItem areaPlantedAnalysis = new SecondaryDrawerItem().withName(R.string.nav_item_area_planted_based_analysis);
        SecondaryDrawerItem priceReceivedAnalysis = new SecondaryDrawerItem().withName(R.string.nav_item_price_received_analysis);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.header)
                .withSelectedItem(-1)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        homeDrawerItem, //Item 1
                        new DividerDrawerItem(), //Item 2
                        compareBasedDrawerItem, //Item 3
                        new DividerDrawerItem(), //Item 4
                        stateBasedDrawerItem,  //Item 5
                        yieldAnalysis,  //Item 6
                        areaHarvestedAnalysis,  //Item 7
                        priceReceivedAnalysis,  //Item 8
                        areaPlantedAnalysis,  //Item 9
                        new DividerDrawerItem(),  //Item 10
                        productBasedAnalysisDrawerItem,  //Item 11
                        yieldAnalysis,  //Item 12
                        areaHarvestedAnalysis,  //Item 13
                        priceReceivedAnalysis,  //Item 14
                        areaPlantedAnalysis  //Item 15
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 1) {
                        }
                        else if (position == 3) {
                            compareActivity();
                        }
                        else if (position == 5) {
                            stateBasedAnalysis();
                        }
                        else if (position == 11) {
                            cropBasedAnalysis();
                        }
                        return false;
                    }
                }).build();

        //displayView(1);

        Snackbar.make(parentLayout, "You are showing previous year crops analysis acroos all state", Snackbar.LENGTH_LONG).show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, ""+GlobalConstant.getPreviousYear());
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "YIELD");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, "");
        bundle.putString(GlobalConstant.TAG_CROP_NAME, "");
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_YES);
        Fragment fragmentYield = new HomeAllCropsFragment();
        fragmentYield.setArguments(bundle);
        adapter.addFragment(fragmentYield, "Yield");

        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, ""+GlobalConstant.getPreviousYear());
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "AREA HARVESTED");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, "");
        bundle.putString(GlobalConstant.TAG_CROP_NAME, "");
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_NO);
        Fragment fragmentAreaHarvested = new HomeAllCropsFragment();
        fragmentAreaHarvested.setArguments(bundle);
        adapter.addFragment(fragmentAreaHarvested, "AREA HARVESTED");

        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, ""+GlobalConstant.getPreviousYear());
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "PRICE RECEIVED");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, "");
        bundle.putString(GlobalConstant.TAG_CROP_NAME, "");
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_NO);
        Fragment fragmentPriceReceived = new HomeAllCropsFragment();
        fragmentPriceReceived.setArguments(bundle);
        adapter.addFragment(fragmentPriceReceived, "PRICE RECEIVED");

        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, ""+GlobalConstant.getPreviousYear());
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "AREA HARVESTED");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, "");
        bundle.putString(GlobalConstant.TAG_CROP_NAME, "");
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_NO);
        Fragment fragmentAreaPlanned = new HomeAllCropsFragment();
        fragmentAreaPlanned.setArguments(bundle);
        adapter.addFragment(fragmentAreaPlanned, "AREA PLANTED");

        viewPager.setAdapter(adapter);
    }

    public void compareActivity(){
        Intent intent = new Intent(MainActivity.this, CompareActivity.class);
        intent.putExtra(GlobalConstant.TAG_state_name, getResources().getStringArray(R.array.state_name)[0]);
        intent.putExtra(GlobalConstant.TAG_commodity_desc, getResources().getStringArray(R.array.crop_name)[0]);
        intent.putExtra(GlobalConstant.TAG_statisticcat_desc, getResources().getStringArray(R.array.statistic_category)[0]);
        startActivity(intent);
    }

    public void stateBasedAnalysis(){
        selectOption = -1;
        MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
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
                        }
                        return true;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                        if (selectOption != -1) {
                            Intent in = new Intent(MainActivity.this, CommonAnalysisListActivity.class);
                            in.putExtra(GlobalConstant.TAG_commodity_desc, "");
                            in.putExtra(GlobalConstant.TAG_state_name, selectedText);
                            in.putExtra(GlobalConstant.TAG_year, ""+GlobalConstant.getPreviousYear());
                            startActivity(in);
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .show();
    }

    public void cropBasedAnalysis(){
        selectOption = -1;
        MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                .title("Select State")
                .items(getResources().getStringArray(R.array.crop_name))
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
                        }
                        return true;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                        if (selectOption != -1) {
                            Intent in = new Intent(MainActivity.this, CommonAnalysisListActivity.class);
                            in.putExtra(GlobalConstant.TAG_commodity_desc, selectedText);
                            in.putExtra(GlobalConstant.TAG_state_name, "");
                            in.putExtra(GlobalConstant.TAG_year, ""+GlobalConstant.getPreviousYear());
                            startActivity(in);
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .show();
    }

    /*
    private void displayView(int position) {
        // update the main content with called Fragment
        Fragment fragment = null;
        switch (position) {
            case 1:
                bundle = new Bundle();
                bundle.putString(GlobalConstant.TAG_YEAR, "2014");
                bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "YIELD");
                bundle.putString(GlobalConstant.TAG_STATE_NAME, "");
                bundle.putString(GlobalConstant.TAG_CROP_NAME, "");
                fragment = new HomeAllCropsFragment();
                fragment.setArguments(bundle);
                fragmentID = 0;
                break;
            case 2:
                Log.d("selectStateName", selectedText);
                Bundle bundle = new Bundle();
                bundle.putString(GlobalConstant.TAG_YEAR, "2014");
                bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "YIELD");
                bundle.putString(GlobalConstant.TAG_STATE_NAME, selectedText);
                bundle.putString(GlobalConstant.TAG_CROP_NAME, "");
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
    */

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText("Yield");
        tabLayout.getTabAt(1).setText("Area Harvested");
        tabLayout.getTabAt(2).setText("PRICE RECEIVED");
        tabLayout.getTabAt(3).setText("Area PLANTED");
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
