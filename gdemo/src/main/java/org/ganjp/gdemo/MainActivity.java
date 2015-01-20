package org.ganjp.gdemo;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.ganjp.gdemo.adapt.HomeNavDrawListAdapter;
import org.ganjp.gdemo.base.DemoActionBarActivity;
import org.ganjp.gdemo.fragment.UiInterfaceFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends DemoActionBarActivity {
    ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuTitles;
    private HomeNavDrawListAdapter mAdapter;
    private ListView mDrawerList;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
        mMenuTitles = getResources().getStringArray(R.array.home_menu_titles);
        for (String title : mMenuTitles) {
            Map<String,String> map = new HashMap<String, String>();
            map.put("title", title);
            maps.add(map);
        }

        mAdapter = new HomeNavDrawListAdapter(getContext(), maps);
        mDrawerList = (ListView) findViewById(R.id.drawerList);
        mDrawerList.setAdapter(mAdapter);
        int[] colors = {0, 0xFFFF0000, 0};
        mDrawerList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        mDrawerList.setDividerHeight(1);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new UiInterfaceFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Subtitle");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // TODO Auto-generated method stub
                super.onDrawerSlide(drawerView, slideOffset);
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("hello");
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("hi");
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //<---- added
        getSupportActionBar().setHomeButtonEnabled(true); //<---- added

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // <---- added
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) { // <---- added
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState(); // important statetment for drawer to identify its state
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { // <---- added
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) { // <----
            // added
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds mMenuTitles to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

