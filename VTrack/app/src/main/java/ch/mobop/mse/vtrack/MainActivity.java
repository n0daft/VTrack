/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.mobop.mse.vtrack;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;

public class MainActivity extends FragmentActivity {

    private final Handler handler = new Handler();
    private BaasBox client;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    private final static int NEW_CODE = 1;
    private final static int EDIT_CODE = 3;
    //private VerticalFragment mListFragment;

    private Drawable oldBackground = null;
    private int currentColor = 0xFF666666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the BaasBox client library
        BaasBox.Builder b = new BaasBox.Builder(this);
        client = b.setApiDomain("vmbackend.bfh.ch").setAppCode("2501").init();

        // Check if the current user is logged in
        if (BaasUser.current() == null){
            startLoginScreen();
            return;
        }


        setContentView(R.layout.activity_main);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setShouldExpand(true); //Works
        tabs.setViewPager(pager);

        //mListFragment = (VerticalFragment)getSupportFragmentManager().findFragmentById(R.id.section_list);

        changeColor(currentColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Option Item Handler.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        PopupMenu popup;


        switch (item.getItemId()) {

            case R.id.action_contact:
                // Get reference to option menu.
                View menuItemViewOther = findViewById(R.id.action_contact);
                popup = new PopupMenu(getBaseContext(),menuItemViewOther);

                // Adding menu items to the popup menu.
                popup.getMenuInflater().inflate(R.menu.main_popup, popup.getMenu());
                //Intent intent = new Intent(this,NewVoucherActivity.class);
                // Defining menu item click listener for the popup menu.
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        System.out.println(item.getItemId());
                        switch (item.getItemId()){
                            case R.id.action_refresh:


                                break;
                            case R.id.action_settings:
                                // call settings activity
                                System.out.println("Settings selected");
                                break;
                            case R.id.action_logout:
                                System.out.println("Logout selected");
                                BaasUser.current().logout(logoutHandler);
                                break;
                            default: break;
                        }
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();

                break;
            case R.id.action_new_voucher:
                View menuItemViewNew = findViewById(R.id.action_new_voucher);

                popup = new PopupMenu(getBaseContext(),menuItemViewNew);

                // Adding menu items to the popup menu.
                popup.getMenuInflater().inflate(R.menu.main_new_popup, popup.getMenu());
                //Intent intent = new Intent(this,NewVoucherActivity.class);
                // Defining menu item click listener for the popup menu.
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(MainActivity.this,NewVoucherActivity.class);
                        System.out.println(item.getItemId());
                        switch (item.getItemId()){
                            case R.id.action_newvoucher_for_me:

                                System.out.println("action_newvoucher_forme selected");
                                intent.putExtra("intentType","new");
                                intent.putExtra("type","for_me");
                                startActivityForResult(intent,NEW_CODE);

                                break;
                            case R.id.action_newvoucher_from_me:
                                // call settings activity
                                System.out.println("action_newvoucher_fromme selected");
                                intent.putExtra("intentType","new");
                                intent.putExtra("type","from_me");
                                startActivityForResult(intent,NEW_CODE);

                                break;
                            default: break;
                        }
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                /** Showing the popup menu */
                popup.show();




                //QuickContactFragment dialog = new QuickContactFragment();
                //dialog.show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==NEW_CODE){
            if (resultCode==RESULT_OK){
                Toast.makeText(this, "Added Voucher", Toast.LENGTH_LONG).show();
            } else if(resultCode==NewVoucherActivity.RESULT_SESSION_EXPIRED){
                startLoginScreen();
            } else if (resultCode==NewVoucherActivity.RESULT_FAILED){
                Toast.makeText(this, "Failed to add voucher", Toast.LENGTH_LONG).show();
            } else if (resultCode==NewVoucherActivity.RESULT_CANCELED){
                Toast.makeText(this, "Canceled new voucher", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void startLoginScreen(){
        //mDoRefresh = false;
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);

    }

    private void onLogout(){
        startLoginScreen();
    }

    private RequestToken logoutToken;
    private final BaasHandler<Void> logoutHandler =
            new BaasHandler<Void>() {
                @Override
                public void handle(BaasResult<Void> voidBaasResult) {
                    logoutToken=null;
                    onLogout();
                }
            };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "Received", "Delivered", "Archive" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0:
                    System.out.println("Case 0");
                    return ForMeRecyclerViewFragment.newInstance();
                case 1:
                    System.out.println("Case 1");
                    return FromMeRecyclerViewFragment.newInstance();
                default:
                    return SuperAwesomeCardFragment.newInstance(position);
            }
        }

    }



}