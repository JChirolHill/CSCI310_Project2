package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import java.util.Date;
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
    public static final int REQUEST_CODE_ORDER_CONFIRMATION = 1;

    private Order order;
    private Shop currShop;
    private UserLog log;
    private Customer customer;
    private DrinkListAdapter drinkAdapter;
    private List<Drink> drinks;
    private int totalCaffeineToday;
    private String userID;

    private TextView textNumItems;
    private TextView textTotalCaffeineOrder;
    private TextView textTotalCaffeineToday;
    private TextView textTotalCost;
    private TextView textError;
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
        textError = findViewById(R.id.textError);
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

        userID = prefs.getString(User.PREF_USER_ID, "Invalid ID");
        order = new Order(null, currShop.getId(), null, prefs.getString(User.PREF_USER_ID, "Invalid ID"), new Date());

        // set up adapter
        drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink_order, drinks);
        listDrinks.setAdapter(drinkAdapter);
//        drinkAdapter.notifyDataSetChanged();

        // get user with its log from database
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                customer = (Customer)o;

                if(customer.getLog() != null) {
                    totalCaffeineToday = customer.getLog().getTotalCaffeineLevel();
                }
                else {
                    totalCaffeineToday = 0;
                }

                displayInfo();
            }
        });
        Database.getInstance().getUser(prefs.getString(User.PREF_USER_ID, "Invalid ID"));

        // short click adds to the order
        listDrinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                textError.setVisibility(View.GONE);

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

        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalCaffeineToday < User.CAFFEINE_LIMIT) { // did not pass caffeine limit
                    proceedToOrder();
                }
                else { // above caffeine threshold\
                    Snackbar.make(findViewById(R.id.layoutCreateOrder), getResources().getString(R.string.overCaffeineLevel), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.proceed), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    proceedToOrder();
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ORDER_CONFIRMATION) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private void proceedToOrder() {
        // ordered at least one item
        if(order.getDrinks().size() > 0) {
            textError.setVisibility(View.GONE);

            // add order to database
            order.setId(Database.getInstance().addOrder(order));

            // get user from database
            Database.getInstance().setCallback(new Database.Callback() {
                @Override
                public void dbCallback(Object o) {
                    Customer customer = (Customer)o;
                    customer.getLog().addOrder(order);

                    // add this user back to database
                    Database.getInstance().addUser(customer);

                    Intent i = new Intent(getApplicationContext(), OrderActivity.class);
                    i.putExtra(OrderActivity.EXTRA_READONLY, true);
                    i.putExtra(Order.PREF_ORDER_ID, order.getId());
                    startActivityForResult(i, REQUEST_CODE_ORDER_CONFIRMATION);
                }
            });
            Database.getInstance().getUser(userID);
        }
        else { // empty order
            textError.setText(getResources().getString(R.string.emptyOrder));
            textError.setVisibility(View.VISIBLE);
        }
    }

    private void displayInfo() {
        // update total caffeine
        if(customer.getLog() != null) {
            totalCaffeineToday = customer.getLog().getTotalCaffeineLevel() + order.getTotalCaffeine(true);
        }
        else {
            totalCaffeineToday = order.getTotalCaffeine(true);
        }

        // update views
        textNumItems.setText(getResources().getString(R.string.items, order.getNumItemsOrdered()));
        textTotalCaffeineOrder.setText(getResources().getString(R.string.totalCaffeineOrder, order.getTotalCaffeine(true)));
        textTotalCaffeineToday.setText(getResources().getString(R.string.totalCaffeineToday, totalCaffeineToday));
        textTotalCost.setText(getResources().getString(R.string.totalCost, order.getTotalCost(true)));

        if(totalCaffeineToday > User.CAFFEINE_LIMIT) {
            textTotalCaffeineToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.danger));
        }
        else {
            textTotalCaffeineToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }
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
