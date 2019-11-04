package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.R;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.User;

public class LogActivity extends AppCompatActivity {

    private ListView listOrders;
    private ImageView caffeineChart;
    private ImageView moneyChart;

    private List<Order> orders;
    private OrderListAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listOrders = findViewById(R.id.listOrders);
        moneyChart = findViewById(R.id.imageMoneyBarChart);
        caffeineChart = findViewById(R.id.imageCaffeineBarChart);

        orders = new ArrayList<>();

        // set up adapter
        orderAdapter = new OrderListAdapter(this, R.layout.list_item_order, orders);
        listOrders.setAdapter(orderAdapter);

//        // get user's orders
//        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
//        Database.getInstance().setCallback(new Database.Callback() {
//            @Override
//            public void dbCallback(Object o) {
////                orders.
//            }
//        });
//        Database.getInstance().getUser(prefs.getString(User.PREF_USER_ID, "invalid userid"));
//
//        // populate orders from database
//        Database.getInstance().setCallback(new Database.Callback() {
//            @Override
//            public void dbCallback(Object o) {
//
//            }
//        });
//        Database.getInstance().getOrder();
    }

    private class OrderListAdapter extends ArrayAdapter<Order> {
        public OrderListAdapter(Context context, int resource, List<Order> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_order, null);
            }

            TextView textShopName = convertView.findViewById(R.id.listShopName);
            TextView textTotalCost = convertView.findViewById(R.id.listTotalCost);
            TextView textTotalCaffeine = convertView.findViewById(R.id.listTotalCaffeine);
            TextView textTripDuration = convertView.findViewById(R.id.listTripDuration);

            Order order = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
//            textShopName.setText(order.getShop());
//            textTotalCost.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
//            textTotalCaffeine.setText(d.getCaffeine() + " " + getResources().getString(R.string.milligrams));
//            textTripDuration.setText("$" + Float.toString(d.getPrice()));

            return convertView;
        }
    }
}



