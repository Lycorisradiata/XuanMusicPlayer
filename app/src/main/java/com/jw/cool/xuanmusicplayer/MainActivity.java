package com.jw.cool.xuanmusicplayer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.fragments.BaseFragment;
import com.jw.cool.xuanmusicplayer.fragments.SongListFragment;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    CoordinatorLayout rootLayout;
    FloatingActionButton fabBtn;
    
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<Fragment> fragmentsList;
    ArrayList<String> titles;
    SearchView mSearchView;
    String mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("mainactivity", "oncreate enter");
        initToolbar();
        initInstances();
        Intent intent = new Intent(MainActivity.this,MusicService.class);
        startService(intent);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        System.out.println("drawerLayout " + drawerLayout + " drawerToggle " + drawerToggle);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        
        titles = new ArrayList<String>();
        titles.add("Exchange");
        titles.add("Activity");
        titles.add("Me");

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragmentsList = new ArrayList<Fragment>();
        Bundle bundle = new Bundle();
        Fragment exchangeFragment = SongListFragment.newInstance(
                MainActivity.this, bundle);
        Fragment activityFragment = SongListFragment.newInstance(
                MainActivity.this, bundle);
        Fragment meFragment = SongListFragment.newInstance(
                MainActivity.this, bundle);


        fragmentsList.add(exchangeFragment);
        fragmentsList.add(activityFragment);
        fragmentsList.add(meFragment);
        TabFragmentPagerAdapter tabFragmentPagerAdapter = new TabFragmentPagerAdapter(
                getSupportFragmentManager(), fragmentsList);
        viewPager.setAdapter(new TabFragmentPagerAdapter(
                getSupportFragmentManager(), fragmentsList));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(tabFragmentPagerAdapter);    
       
    }

    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> mFragmentsList;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public TabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentsList) {
            super(fm);
            mFragmentsList = fragmentsList;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentsList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {

            switch (arg0) {
                case 0:

                    break;
                case 1:

                    break;
                case 2:

                    break;

            }
//            currIndex = arg0;

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    void initSearchView(){
//        mSearchView.setSubmitButtonEnabled(true);
        //    获取了SearchView，我们就能设置其相应的属性，比如我想让它一开始就处于显示SearchView的状态
//        mSearchView.setIconified(false);
        //    而我不想让它隐藏SearchView，则可以
//
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "doSearch query" + query);
//                mSearchView.setIconifiedByDefault(false);
//                mSearchView.setIconified(false);
                doSearch(true);
                mSearchView.clearFocus();//这样就不用按两次back键才返回了
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchText = newText;
                doSearch(false);
                return true;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "onClose ");
                return false;
            }
        });
    }



    void doSearch(boolean isCompleted){
//        Toast.makeText(this, mSearchText, Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new SearchEvent(mSearchText, isCompleted));
        Log.d(TAG, "doSearch " + mSearchText);
    }

    
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        initSearchView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

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

    @Override
    public void onBackPressed() {
        if(!((BaseFragment)fragmentsList.get(viewPager.getCurrentItem())).handleBackPressed()){
            super.onBackPressed();
        }
    }
}

