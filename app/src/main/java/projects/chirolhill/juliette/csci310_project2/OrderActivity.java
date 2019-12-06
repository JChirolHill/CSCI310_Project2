package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Shop;

/**
 * This activity displays Order information in an easy way for the customer to understand
 * Shows total cost, total caffeine, and all drinks purchased for that order
 */
public class OrderActivity extends AppCompatActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();
    public static final int REQUEST_CODE_UPDATE_ORDER = 5;
    public static final String EXTRA_READONLY = "extra_order_readonly"; // display as view or update
    public static final String EXTRA_DRINKS_STRING = "extra_drinks_string";

    private TextView textOrderTitle;
    private TextView totalMoneySpent;
    private TextView textCaffeineLevel;
    private TextView textDate;
    private ListView listDrinks;
    private Button btnOk;
    private Button btnEdit;
    private DateFormat dateFormat;

    private boolean readonly;
    private String orderID;
    private Order currOrder;
    private List<Drink> drinks;
    private Shop currShop;

    private DrinkListAdapter drinkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textOrderTitle = findViewById(R.id.textOrderTitle);
        totalMoneySpent = findViewById(R.id.textTotalMoneySpent);
        textCaffeineLevel = findViewById(R.id.textCaffeineLevel);
        textDate = findViewById(R.id.textDate);
        listDrinks = findViewById(R.id.listDrinks);
        btnOk = findViewById(R.id.btnOk);
        btnEdit = findViewById(R.id.btnEdit);

        // either from shopinfoactivity -> readonly (review order)
        // or from profileactivity -> readonly (view/edit past orders)
        Intent i = getIntent();
        readonly = i.getBooleanExtra(EXTRA_READONLY, true);
        orderID = i.getStringExtra(Order.PREF_ORDER_ID);

        drinks = new ArrayList<>();

        // set up adapter
        drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink, drinks);
        listDrinks.setAdapter(drinkAdapter);

        // get the order from database
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                currOrder = (Order)o;

                // set values in layout based on order
                dateFormat = new SimpleDateFormat("MMM d, yyyy");
                textDate.setText(dateFormat.format(currOrder.getDate()));
                totalMoneySpent.setText(getResources().getString(R.string.dollarCost, currOrder.getTotalCost(false)));
                textCaffeineLevel.setText(getResources().getString(R.string.milligrams, currOrder.getTotalCaffeine(false)));

                // load in all the drinks
                for(Map.Entry<String, Pair<Drink, Integer>> entry : currOrder.getDrinks().entrySet()) {
                    Database.getInstance().setCallback(new Database.Callback() {
                        @Override
                        public void dbCallback(Object o) {
                            Drink tempDrink = (Drink)o;
                            int numOrdered = currOrder.getDrinks().get(tempDrink.getId()).second;
                            for(int i=0; i<numOrdered; ++i) {
                                currOrder.addDrink(tempDrink);
                                drinks.add(tempDrink);
                                drinkAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    Database.getInstance().getDrink(entry.getKey());
                }
            }
        });
        Database.getInstance().getOrder(orderID);

        renderReadOnly();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readonly) { // return to previous activity
                    setResult(RESULT_OK);
                    finish();
                }
                else { // save results and display readonly
                    // create the new order
                    currOrder = new Order(orderID, currOrder.getShop(), currOrder.getTrip().getId(), currOrder.getUser(), currOrder.getDate());
                    Database.getInstance().addOrder(currOrder);

                    renderReadOnly();
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.getInstance().setCallback(new Database.Callback() {
                    @Override
                    public void dbCallback(Object o) {
                        if (o != null) {
                            currShop = (Shop) o;

                            Intent i = new Intent(getApplicationContext(), CreateOrderActivity.class);
                            i.putExtra(CreateOrderActivity.EXTRA_CREATE, false);
                            i.putExtra(Shop.PREF_SHOP, currShop);
                            i.putExtra(Order.EXTRA_ORDER_DATE, currOrder.getDate());
                            i.putExtra(Order.PREF_ORDER_ID, currOrder.getId());
                            i.putExtra(Order.EXTRA_ORDER_TRIP, currOrder.getTrip());

                            Map<String, Pair<Drink, Integer>> map = currOrder.getDrinks();
                            StringBuilder sb = new StringBuilder();
                            for(Map.Entry<String, Pair<Drink, Integer>> entry : map.entrySet()){
                                sb.append(entry.getKey()).append(",").append(entry.getValue().second/2).append(" ");
                            }

                            i.putExtra(OrderActivity.EXTRA_DRINKS_STRING, sb.toString());
                            startActivityForResult(i, REQUEST_CODE_UPDATE_ORDER);
                        }
                    }
                });
                Database.getInstance().getShop(currOrder.getShop());
            }
        });
    }

    /**
     * Called when return from CreateOrderActivity after edit an order
     * Rerenders based on the edited order
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_UPDATE_ORDER) {
            if (resultCode == RESULT_OK) {
                // display the updated order
                orderID = data.getStringExtra(Order.PREF_ORDER_ID);

                drinks = new ArrayList<>();

                // set up adapter
                drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink, drinks);
                listDrinks.setAdapter(drinkAdapter);

                // get the order from database
                Database.getInstance().setCallback(new Database.Callback() {
                    @Override
                    public void dbCallback(Object o) {
                        currOrder = (Order)o;

                        // set values in layout based on order
                        dateFormat = new SimpleDateFormat("MMM d, yyyy");
                        textDate.setText(dateFormat.format(currOrder.getDate()));
                        totalMoneySpent.setText(getResources().getString(R.string.dollarCost, currOrder.getTotalCost(false)));
                        textCaffeineLevel.setText(getResources().getString(R.string.milligrams, currOrder.getTotalCaffeine(false)));

                        // load in all the drinks
                        for(Map.Entry<String, Pair<Drink, Integer>> entry : currOrder.getDrinks().entrySet()) {
                            Database.getInstance().setCallback(new Database.Callback() {
                                @Override
                                public void dbCallback(Object o) {
                                    Drink tempDrink = (Drink)o;
                                    int numOrdered = currOrder.getDrinks().get(tempDrink.getId()).second;
                                    for(int i=0; i<numOrdered; ++i) {
                                        currOrder.addDrink(tempDrink);
                                        drinks.add(tempDrink);
                                        drinkAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            Database.getInstance().getDrink(entry.getKey());
                        }
                    }
                });
                Database.getInstance().getOrder(orderID);
            }
        }
    }

    /**
     * Renders the visibility of views properly
     * (In the past this was an editable activity, but that logic has been moved to CreateOrder instead
     * This activity always renders as readonly)
     */
    private void renderReadOnly() {
        readonly = true;
        textOrderTitle.setText(getResources().getString(R.string.viewOrder));
        btnOk.setText(getResources().getString(R.string.ok));

        totalMoneySpent.setVisibility(View.VISIBLE);
        textCaffeineLevel.setVisibility(View.VISIBLE);
        textDate.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.VISIBLE);
    }

    /**
     * Inner class to generate and update the drinks list
     * Renders each of the list items
     */
    private class DrinkListAdapter extends ArrayAdapter<Drink> {
        public DrinkListAdapter(Context context, int resource, List<Drink> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_drink, null);
            }

            TextView textName = convertView.findViewById(R.id.listDrinkName);
            TextView textType = convertView.findViewById(R.id.listDrinkType);
            TextView textCaffeine = convertView.findViewById(R.id.listDrinkCaffeine);
            TextView textPrice = convertView.findViewById(R.id.listDrinkPrice);
//            ImageView image = convertView.findViewById(R.id.listShopImage);

            Drink d = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(d.getName());
            textType.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
            textCaffeine.setText(getResources().getString(R.string.milligrams, d.getCaffeine()));
            textPrice.setText(getString(R.string.dollarCost, d.getPrice()));
//            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }
}
