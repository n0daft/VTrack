package ch.mobop.mse.vtrack;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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

import ch.mobop.mse.vtrack.ForMeRecyclerViewFragment.ArchiveListenerForMe;
import ch.mobop.mse.vtrack.FromMeRecyclerViewFragment.ArchiveListenerFromMe;
import ch.mobop.mse.vtrack.helpers.Config;
import ch.mobop.mse.vtrack.helpers.Constants;

/**
 * Main activity of the application.
 * Initializes the recycler view fragments and sets up the layout.
 * Credits for the sliding tab strip goes to Andreas Stuetz.
 * https://github.com/astuetz/PagerSlidingTabStrip
 */
public class MainActivity extends FragmentActivity {

    private final Handler handler = new Handler();
    private BaasBox mClient;

    private PagerSlidingTabStrip mTabs;
    private ViewPager mPager;
    private MyPagerAdapter mAdapter;

    private final static int NEW_CODE = 1;
    private final static int SETTINGS_CODE = 2;
    private ForMeRecyclerViewFragment mForMeFragment;
    private FromMeRecyclerViewFragment mFromMeFragment;
    private ArchiveRecyclerViewFragment mArchiveFragment;

    private SharedPreferences mSharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the BaasBox client library.
        BaasBox.Builder b = new BaasBox.Builder(this);
        mClient = b.setApiDomain("vmbackend.bfh.ch").setAppCode("2501").init();

        // Check if the current user is logged in.
        if (BaasUser.current() == null){
            startLoginScreen();
            return;
        }

        setContentView(R.layout.activity_main);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());

        mSharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        Config.currentValidityThreshold = mSharedpreferences.getInt(Constants.VALIDITY_THRESHOLD, Config.defaultValidityThreshold);
        changeColor(mSharedpreferences.getInt(Constants.actionBarColor, Config.defaultActionBarColor.getColor()));

        mPager.setAdapter(mAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mPager.setPageMargin(pageMargin);
        mTabs.setShouldExpand(true); // Expand the tabs equally in width.
        mTabs.setViewPager(mPager);


    }

    @Override
    public void onResume(){
        super.onResume();
        changeColor(mSharedpreferences.getInt(Constants.actionBarColor, Config.defaultActionBarColor.getColor()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Option Item Handler. Handles the interaction with the menu items.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        PopupMenu popup;

        switch (item.getItemId()) {

            case R.id.action_overflow:
                // Get reference to option menu.
                View menuItemViewOther = findViewById(R.id.action_overflow);
                popup = new PopupMenu(getApplicationContext(),menuItemViewOther);

                // Adding menu items to the popup menu.
                popup.getMenuInflater().inflate(R.menu.main_popup, popup.getMenu());

                // Defining menu item click listener for the popup menu.
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        System.out.println(item.getItemId());
                        switch (item.getItemId()){
                            case R.id.action_settings:
                                // call settings activity
                                System.out.println("Settings selected");
                                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                                startActivityForResult(intent,SETTINGS_CODE);
                                break;
                            case R.id.action_logout:
                                System.out.println("Logout selected");
                                BaasUser.current().logout(logoutHandler);
                                break;
                            default: break;
                        }

                        return true;
                    }
                });

                // Showing the popup menu.
                popup.show();

                break;
            case R.id.action_new_voucher:
                View menuItemViewNew = findViewById(R.id.action_new_voucher);

                popup = new PopupMenu(getApplicationContext(),menuItemViewNew);

                // Adding menu items to the popup menu.
                popup.getMenuInflater().inflate(R.menu.main_new_popup, popup.getMenu());

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

                        return true;
                    }
                });

                // Showing the popup menu
                popup.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==NEW_CODE){
            if (resultCode==RESULT_OK){
                Toast.makeText(this, getString(R.string.toast_voucherAdded), Toast.LENGTH_LONG).show();
                if(mForMeFragment != null){
                    mForMeFragment.refreshDocuments(true);}
                if(mFromMeFragment != null){
                    mFromMeFragment.refreshDocuments(true);}
            } else if(resultCode==NewVoucherActivity.RESULT_SESSION_EXPIRED){
                startLoginScreen();
            } else if (resultCode==NewVoucherActivity.RESULT_FAILED){
                Toast.makeText(this, getString(R.string.toast_addVoucherFailure), Toast.LENGTH_LONG).show();
            } else if (resultCode==NewVoucherActivity.RESULT_CANCELED){
                Toast.makeText(this, getString(R.string.toast_addVoucherCanceled), Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void changeColor(int newColor) {

        mTabs.setIndicatorColor(newColor);

        ActionBar actionBar = getActionBar();
        if(actionBar != null){

        // Change ActionBar color only if an ActionBar is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            }

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void onLogout(){
        startLoginScreen();
    }

    private RequestToken mLogoutToken;
    private final BaasHandler<Void> logoutHandler =
            new BaasHandler<Void>() {
                @Override
                public void handle(BaasResult<Void> voidBaasResult) {
                    mLogoutToken =null;
                    onLogout();
                }
            };

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

    /**
     * Custom pager adapter class for view pager.
     */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.activity_main_actionbaritem_received), getResources().getString(R.string.activity_main_actionbaritem_givenAway), getResources().getString(R.string.activity_main_actionbaritem_archive) };

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
                    mForMeFragment = ForMeRecyclerViewFragment.newInstance();
                    mForMeFragment.setArchiveListener(new ArchiveListenerForMe() {
                        @Override
                        public void voucherArchived() {
                            if (mArchiveFragment != null) {
                                mArchiveFragment.refreshDocuments(false);
                            }
                        }
                    });
                    return mForMeFragment;
                case 1:
                    mFromMeFragment = FromMeRecyclerViewFragment.newInstance();
                    mFromMeFragment.setArchiveListener(new ArchiveListenerFromMe() {
                        @Override
                        public void voucherArchived() {
                            if (mArchiveFragment != null) {
                                mArchiveFragment.refreshDocuments(false);
                            }
                        }
                    });
                    return mFromMeFragment;
                default:
                    mArchiveFragment = ArchiveRecyclerViewFragment.newInstance();
                    return mArchiveFragment;
            }
        }
    }
}