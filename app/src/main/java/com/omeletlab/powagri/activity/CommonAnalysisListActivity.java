package com.omeletlab.powagri.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.omeletlab.powagri.R;
import com.omeletlab.powagri.adapter.ViewPagerAdapter;
import com.omeletlab.powagri.fragment.CommonCropsCardListFragment;
import com.omeletlab.powagri.util.GlobalConstant;

public class CommonAnalysisListActivity extends AppCompatActivity {

    public Drawer result;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    public static int selectOption = 0;
    public static String selectedText = "";
    private int fragmentID;
    private Bundle bundle;
    private View parentLayout;

    private String cropName;
    private String stateName;
    private String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalConstant.mContext = CommonAnalysisListActivity.this;
        parentLayout = findViewById(R.id.root_view);

        Intent intent = getIntent();
        cropName = intent.getStringExtra(GlobalConstant.TAG_commodity_desc);
        stateName = intent.getStringExtra(GlobalConstant.TAG_state_name);
        year = intent.getStringExtra(GlobalConstant.TAG_year);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, year);
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "YIELD");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, stateName);
        bundle.putString(GlobalConstant.TAG_CROP_NAME, cropName);
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_YES);
        Fragment fragmentYield = new CommonCropsCardListFragment();
        fragmentYield.setArguments(bundle);
        adapter.addFragment(fragmentYield, "Yield");

        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, year);
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "PRICE RECEIVED");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, stateName);
        bundle.putString(GlobalConstant.TAG_CROP_NAME, cropName);
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_NO);
        Fragment fragmentPriceReceived = new CommonCropsCardListFragment();
        fragmentPriceReceived.setArguments(bundle);
        adapter.addFragment(fragmentPriceReceived, "PRICE RECEIVED");

        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, year);
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "AREA PLANTED");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, stateName);
        bundle.putString(GlobalConstant.TAG_CROP_NAME, cropName);
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_NO);
        Fragment fragmentAreaPlanned = new CommonCropsCardListFragment();
        fragmentAreaPlanned.setArguments(bundle);
        adapter.addFragment(fragmentAreaPlanned, "AREA PLANTED");

        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, year);
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, "AREA HARVESTED");
        bundle.putString(GlobalConstant.TAG_STATE_NAME, stateName);
        bundle.putString(GlobalConstant.TAG_CROP_NAME, cropName);
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_NO);
        Fragment fragmentAreaHarvested = new CommonCropsCardListFragment();
        fragmentAreaHarvested.setArguments(bundle);
        adapter.addFragment(fragmentAreaHarvested, "AREA HARVESTED");

        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText("Yield");
        tabLayout.getTabAt(2).setText("PRICE RECEIVED");
        tabLayout.getTabAt(3).setText("Area PLANTED");
        tabLayout.getTabAt(1).setText("Area Harvested");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
