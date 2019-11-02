package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.UserLog;

public class CreateOrderActivity extends AppCompatActivity {
    private Order order;
    private Shop currShop;
    private UserLog log;
    private Customer customer;
    private DrinkListAdapter drinkAdapter;
    private List<Drink> drinks;

    private TextView textNumItems;
    private TextView textTotalCaffeineOrder;
    private TextView textTotalCaffeineToday;
    private TextView textTotalCost;
    private ListView listDrinks;
    private Button btnSubmitOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textNumItems = findViewById(R.id.textNumItems);
        textTotalCaffeineOrder = findViewById(R.id.textCaffeineOrder);
        textTotalCaffeineToday = findViewById(R.id.textCaffeineToday);
        textTotalCost = findViewById(R.id.textTotalCost);
        listDrinks = findViewById(R.id.listItems);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);

        drinks = new ArrayList<>();

        // get shop from intent
        Intent i = getIntent();
        currShop = (Shop)i.getSerializableExtra(Shop.PREF_SHOP);
        for(Drink d : currShop.getDrinks()) {
            drinks.add(d);
        }

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        order = new Order(null, currShop.getId(), null, prefs.getString(User.PREF_USER_ID, "Invalid ID"));

        // set up adapter
        drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink_order, drinks);
        listDrinks.setAdapter(drinkAdapter);
//        drinkAdapter.notifyDataSetChanged();

        // get user with its log from database
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                customer = (Customer)o;

                displayInfo();
            }
        });
        Database.getInstance().getUser(prefs.getString(User.PREF_USER_ID, "Invalid ID"));

        // short click adds to the order
        listDrinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                order.addDrink(drinks.get(position));
                drinkAdapter.notifyDataSetChanged();

                displayInfo();
            }
        });

        // long click removes from order
        listDrinks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                order.removeDrink(drinks.get(position).getId());
                drinkAdapter.notifyDataSetChanged();

                displayInfo();

                return true;
            }
        });
    }

    private void displayInfo() {
        textNumItems.setText(getResources().getString(R.string.items, order.getNumItemsOrdered()));
        textTotalCaffeineOrder.setText(getResources().getString(R.string.totalCaffeineOrder, order.getTotalCaffeine()));
        if(customer.getLog() != null) {
            textTotalCaffeineToday.setText(getResources().getString(R.string.totalCaffeineToday, customer.getLog().getTotalCaffeineLevel()));
        }
        else {
            textTotalCaffeineToday.setText(getResources().getString(R.string.totalCaffeineToday, 0));
        }
        textTotalCost.setText(getResources().getString(R.string.totalCost, order.getTotalCost()));
    }

    private class DrinkListAdapter extends ArrayAdapter<Drink> {
        public DrinkListAdapter(Context context, int resource, List<Drink> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_drink_order, null);
            }

            final Drink d = getItem(position);

            TextView textName = convertView.findViewById(R.id.listDrinkName);
            TextView textType = convertView.findViewById(R.id.listDrinkType);
            TextView textCaffeine = convertView.findViewById(R.id.listDrinkCaffeine);
            TextView textPrice = convertView.findViewById(R.id.listDrinkPrice);
            TextView textNumOrdered = convertView.findViewById(R.id.textNumOrdered);
//            Button btnAddDrink = convertView.findViewById(R.id.btnAddDrink);
//            ImageView image = convertView.findViewById(R.id.listShopImage);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(d.getName());
            textType.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
            textCaffeine.setText(d.getCaffeine() + " " + getResources().getString(R.string.milligrams));
            textPrice.setText("$" + Float.toString(d.getPrice()));
            if(order.getDrinks().get(d.getId()) == null) { // none ordered yet
                textNumOrdered.setText("x0");
            }
            else {
                textNumOrdered.setText("x" + order.getDrinks().get(d.getId()).second);
            }
//            Picasso.get().load(s.getImgURL()).into(image);

//            btnAddDrink.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    order.addDrink(d);
//                }
//            });

            return convertView;
        }
    }
}