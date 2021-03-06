package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.Trip;
import projects.chirolhill.juliette.csci310_project2.model.User;

public class ShopInfoActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADD_DRINK = 0;

    private final String TAG = ShopInfoActivity.class.getSimpleName();
    public static final String PREF_READ_ONLY = "pref_read_only";

    private ImageView imageShop;
    private RatingBar ratingBar;
    private TextView textPrice;
    private TextView textTotalRevenue;
//    private TextView textTotalOrders;
    private TextView textPopDrink;
    private TextView textItems;
    private TextView textAddress;
    private ListView listDrinks;
    private Button btnAddDrink;

    private BasicShop currShop;
    private boolean showStats;

    private DrinkListAdapter drinkAdapter;
    private List<Drink> drinks;
    private boolean isMerchant;
    private Drink topDrink;
    private double revenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageShop = findViewById(R.id.imageShop);
        ratingBar = findViewById(R.id.ratingBar);
        textPrice = findViewById(R.id.textPrice);
        textTotalRevenue = findViewById(R.id.textTotalRevenue);
//        textTotalOrders = findViewById(R.id.textTotalOrders);
        textPopDrink = findViewById(R.id.textPopDrink);
        textItems = findViewById(R.id.textItems);
        textAddress = findViewById(R.id.textAddress);
        listDrinks = findViewById(R.id.list);
        btnAddDrink = findViewById(R.id.btnAddDrink);

        drinks = new ArrayList<>();

        // set up adapter
        drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink, drinks);
        listDrinks.setAdapter(drinkAdapter);

        // get whether merchant or not
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        isMerchant = prefs.getBoolean(User.PREF_IS_MERCHANT, false);

        // fetch intent
        Intent i = getIntent();
        if(i.getSerializableExtra(BasicShop.PREF_BASIC_SHOP) != null) { // comes from map activity when click view drinks
            currShop = (BasicShop)i.getSerializableExtra(BasicShop.PREF_BASIC_SHOP);
            showStats = false;
        }
        else if(i.getSerializableExtra(Shop.PREF_SHOP) != null) { // comes from merchant looking at shop stats
            currShop = (Shop)i.getSerializableExtra(Shop.PREF_SHOP);
            showStats = true;

            // calc top drink and revenue
            topDrink = ((Shop)currShop).getTopDrink();
            revenue = ((Shop)currShop).getRevenue();
        }

        // load content onto layout
        getSupportActionBar().setTitle(currShop.getName());
        ratingBar.setRating((float)currShop.getRating());
        textPrice.setText(currShop.getPriceRange());
        textAddress.setText(currShop.getAddress());
        Picasso.get().load(currShop.getImgURL()).into(imageShop);
        if(isMerchant) {
            btnAddDrink.setVisibility(View.VISIBLE);
            textTotalRevenue.setText(getResources().getString(R.string.totalRevenue, (float)revenue));
            textTotalRevenue.setVisibility(View.VISIBLE);
            if(topDrink != null) {
                textPopDrink.setText(getResources().getString(R.string.popDrink, topDrink.getName()));
                textPopDrink.setVisibility(View.VISIBLE);
            }
            textItems.setVisibility(View.GONE);
        } else {
            btnAddDrink.setVisibility(View.GONE);
        }

        // fetch shop from database if exists
        populateDrinks();

        btnAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DrinkActivity.class);
                i.putExtra(DrinkActivity.EXTRA_CREATE_DRINK, true);
                i.putExtra(BasicShop.PREF_BASIC_SHOP_ID, currShop.getId());
                i.putExtra(BasicShop.PREF_BASIC_SHOP_NAME, currShop.getName());
                startActivityForResult(i, REQUEST_CODE_ADD_DRINK);
            }
        });

        listDrinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(isMerchant) {
                    // launch intent to view edit drink
                    Intent i = new Intent(getApplicationContext(), DrinkActivity.class);
                    i.putExtra(DrinkActivity.EXTRA_CREATE_DRINK, false);
                    i.putExtra(BasicShop.PREF_BASIC_SHOP_ID, currShop.getId());
                    i.putExtra(BasicShop.PREF_BASIC_SHOP_NAME, currShop.getName());
                    i.putExtra(Drink.EXTRA_DRINK, drinks.get(position));
                    startActivityForResult(i, REQUEST_CODE_ADD_DRINK);
                }
                else { // log an order
                    Shop shop = new Shop(((Shop)currShop).getOwnerID(), currShop);
                    for(Drink d : drinks) {
                        shop.addDrink(d);
                    }

                    Intent i = new Intent(getApplicationContext(), CreateOrderActivity.class);
                    i.putExtra(Shop.PREF_SHOP, shop);
                    i.putExtra(Drink.EXTRA_DRINK, drinks.get(position));
                    i.putExtra(CreateOrderActivity.EXTRA_CREATE, true);
                    Trip tripForOrder =  (Trip) getIntent().getSerializableExtra(Order.EXTRA_ORDER_TRIP);
                    if (tripForOrder != null) i.putExtra(Order.EXTRA_ORDER_TRIP, tripForOrder);
                    startActivity(i);
                }
            }
        });

        listDrinks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(isMerchant) {
                    final Drink removed = drinks.remove(position);
                    drinkAdapter.notifyDataSetChanged();

                    // remove from database
                    Database.getInstance().removeDrink(removed);

                    // undo snackbar
                    Snackbar.make(findViewById(R.id.list), getResources().getString(R.string.drinkDeleted), Snackbar.LENGTH_LONG)
                            .setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // add back to database
                                    Database.getInstance().addDrink(removed);

                                    // update adapter
                                    drinks.add(removed);
                                    drinkAdapter.notifyDataSetChanged();
                                }
                            }).show();
                }

                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_DRINK) {
            if (resultCode == RESULT_OK) {
                // update the list of drinks when return from adding a drink
                populateDrinks();
            }
        }
    }

    private void populateDrinks() {
        // clear adapter before populate again
        drinkAdapter.clear();

        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                if(o != null) {
                    currShop = (Shop)o;
                    if(((Shop) currShop).getDrinks() != null) {
                        Database.getInstance().setCallback(new Database.Callback() {
                            @Override
                            public void dbCallback(Object o) {
                                drinkAdapter.add((Drink)o);
                                drinkAdapter.notifyDataSetChanged();
                            }
                        });

                        if(((Shop) currShop).getDrinks().size() == 0) { // no drinks
                            textItems.setText(getResources().getString(R.string.noDrinks));
                        }
                        else { // display all drinks
                            textItems.setText(getResources().getString(R.string.itemsListed));
                            textItems.setTextSize(getResources().getDimension(R.dimen.textsize));
                            for(Drink d : ((Shop) currShop).getDrinks()) {
                                Database.getInstance().getDrink(d.getId());
                            }
                        }
                    }
                }
            }
        });
        Database.getInstance().getShop(currShop.getId());
    }

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
            TextView textTimesOrdered = convertView.findViewById(R.id.listDrinkTimesOrdered);
            TextView textType = convertView.findViewById(R.id.listDrinkType);
            TextView textCaffeine = convertView.findViewById(R.id.listDrinkCaffeine);
            TextView textPrice = convertView.findViewById(R.id.listDrinkPrice);
            TextView textMostPop = convertView.findViewById(R.id.listMostPop);
//            ImageView image = convertView.findViewById(R.id.listShopImage);

            Drink d = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(d.getName());
            if(isMerchant) {
                textTimesOrdered.setVisibility(View.VISIBLE);
                textTimesOrdered.setText(getResources().getString(R.string.totalOrders, d.getTimesOrdered()));
                if(topDrink != null && topDrink.getId().equals(d.getId())) {
                    textMostPop.setVisibility(View.VISIBLE);
                }
                else {
                    textMostPop.setVisibility(View.GONE);
                }
            }
            else {
                textMostPop.setVisibility(View.GONE);
                textTimesOrdered.setVisibility(View.GONE);
            }
            textType.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
            textCaffeine.setText(getResources().getString(R.string.milligrams, d.getCaffeine()));
            textPrice.setText(getResources().getString(R.string.dollars, d.getPrice()));
//            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }
}
