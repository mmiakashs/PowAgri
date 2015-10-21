package com.omeletlab.argora.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalConstant.mContext = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        PrimaryDrawerItem homeDrawerItem = new PrimaryDrawerItem().withName(R.string.nav_item_home);
        SecondaryDrawerItem inboxDrawerItem = new SecondaryDrawerItem().withName(R.string.nav_item_price);
        SecondaryDrawerItem sentDrawerItem = new SecondaryDrawerItem().withName(R.string.nav_item_expenses);
        SecondaryDrawerItem logoutDrawerItem = new SecondaryDrawerItem().withName(R.string.nav_item_state);

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
                        sentDrawerItem,
                        logoutDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(position==1){
                            new MaterialDialog.Builder(MainActivity.this)
                                    .title("Select State")
                                    .items(getResources().getStringArray(R.array.state_name))
                                    .autoDismiss(false)
                                    .forceStacking(false)
                                    .positiveText("Select")
                                    .negativeText("Close")
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                            if (text != null) {
                                                selectOption = which;
                                            }
                                            return true;
                                        }
                                    })
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                            
                                        }
                                    })
                                    .show();
                        }
                        return false;
                    }
                }).build();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeAllCropsFragment(), "Home");
        viewPager.setAdapter(adapter);
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
