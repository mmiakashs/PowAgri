package com.omeletlab.argora.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.omeletlab.argora.R;
import com.omeletlab.argora.fragment.CommonCropsCardListFragment;
import com.omeletlab.argora.util.GlobalConstant;

public class SingleItemAnalysisActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private View parentLayout;
    private Bundle bundle;
    private int fragmentID;
    private String selectedText;
    private String selectedId;

    private String cropName;
    private String stateName;
    private String year;
    private String statisticCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_analysis);
        parentLayout = findViewById(R.id.root_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        cropName = intent.getStringExtra(GlobalConstant.TAG_commodity_desc);
        stateName = intent.getStringExtra(GlobalConstant.TAG_state_name);
        year = intent.getStringExtra(GlobalConstant.TAG_year);
        statisticCategory = intent.getStringExtra(GlobalConstant.TAG_statisticcat_desc);

        displayView();

        //Snackbar.make(parentLayout, "You are showing previous year crops analysis acroos all state", Snackbar.LENGTH_LONG).show();
    }

    private void displayView() {
        Fragment fragment = null;
        bundle = new Bundle();
        bundle.putString(GlobalConstant.TAG_YEAR, year);
        bundle.putString(GlobalConstant.TAG_ANALYSIS_TYPE, statisticCategory);
        bundle.putString(GlobalConstant.TAG_STATE_NAME, stateName);
        bundle.putString(GlobalConstant.TAG_CROP_NAME, cropName);
        bundle.putString(GlobalConstant.TAG_isShowLoadingDialog, GlobalConstant.TAG_YES);
        fragment = new CommonCropsCardListFragment();
        fragment.setArguments(bundle);
        fragmentID = 0;

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        } else {
            Log.e("Activity error", "Error in else case");
        }
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
