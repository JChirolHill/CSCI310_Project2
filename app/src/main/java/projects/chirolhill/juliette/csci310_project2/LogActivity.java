package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.DateDoubleSeries;
import projects.chirolhill.juliette.csci310_project2.model.DateIntegerSeries;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.UserLog;

public class LogActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_VIEW_ORDER = 2;
    private final String TAG = LogActivity.class.getSimpleName();

    private FragmentPagerAdapter adapterViewPager;
//    private String userID;
//    private List<Order> orders;
//    private UserLog log;
//    private Customer currCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewpager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

//        // get user id
//        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
//        userID = prefs.getString(User.PREF_USER_ID, "invalid userid");
//
//        // fetch user and all orders from database
//        Database.getInstance().setCallback(new Database.Callback() {
//            @Override
//            public void dbCallback(Object o) {
//                Customer tempCustomer = (Customer)o;
//                log = tempCustomer.getLog();
//
//                // get all orders from database
//                Database.getInstance().setCallback(new Database.Callback() {
//                    @Override
//                    public void dbCallback(Object o) {
//                        currCustomer.getLog().addOrder((Order)o);
//                    }
//                });
//                for(Map.Entry<String, Order> entry : log.getOrders().entrySet()) {
//                    Database.getInstance().getOrder(entry.getKey());
//                }
//            }
//        });
//        Database.getInstance().getUser(userID);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ChartsFragment.newInstance();
                case 1:
                    return ViewOrdersFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}



