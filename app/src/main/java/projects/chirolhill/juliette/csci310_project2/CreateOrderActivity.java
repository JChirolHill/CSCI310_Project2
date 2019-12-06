package projects.chirolhill.juliette.csci310_project2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.Trip;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.UserLog;

import static projects.chirolhill.juliette.csci310_project2.R.color.colorPrimaryDark;
import static projects.chirolhill.juliette.csci310_project2.R.color.colorAccent;

/**
 * This activity allows for creating and editing an order.
 * Creating functionality reached from an intent from ShopInfoActivity
 * Editing functionality reached from an intent from OrderActivity
 */
public class CreateOrderActivity extends AppCompatActivity {
    private static final String TAG = CreateOrderActivity.class.getSimpleName();
    public static final String EXTRA_CREATE = "extra_create";
    public static final int REQUEST_CODE_ORDER_CONFIRMATION = 1;

    private Order order;
    private Shop currShop;
    private UserLog log;
    private Customer customer;
    private DrinkListAdapter drinkAdapter;
    private List<Drink> drinks;
    private int totalCaffeineToday;
    private String userID;
    private Date date;
    private boolean create;
    private String drinkStr;

    private DateFormat dateFormat;
    private EditText editDate;
    private Spinner spinnerTripHrs;
    private Spinner spinnerTripMins;
    private Integer tripHrs;
    private Integer tripMins;
    private DatePickerDialog datePickerDialog;
    private TextView textDatePrompt;
    private TextView textTripPrompt;
    private TextView textCreateOrderTitle;
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

        textCreateOrderTitle = findViewById(R.id.textCreateOrderTitle);
        textNumItems = findViewById(R.id.textNumItems);
        textTotalCaffeineOrder = findViewById(R.id.textCaffeineOrder);
        textTotalCaffeineToday = findViewById(R.id.textCaffeineToday);
        textTotalCost = findViewById(R.id.textTotalCost);
        textError = findViewById(R.id.textError);
        listDrinks = findViewById(R.id.listItems);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
        editDate = findViewById(R.id.editDate);
        textDatePrompt = findViewById(R.id.textDatePrompt);
        textTripPrompt = findViewById(R.id.textTripPrompt);
        spinnerTripHrs = findViewById(R.id.spinnerTripHours);
        spinnerTripMins = findViewById(R.id.spinnerTripMinutes);

        drinks = new ArrayList<>();
        dateFormat = new SimpleDateFormat("MMM d, yyyy");

        // get user id from prefs
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        userID = prefs.getString(User.PREF_USER_ID, "Invalid ID");

        // get shop and drink and order from intent
        Intent i = getIntent();
        currShop = (Shop) i.getSerializableExtra(Shop.PREF_SHOP);
        order = new Order(null, currShop.getId(), null, prefs.getString(User.PREF_USER_ID, "Invalid ID"), new Date());
        order.setTrip((Trip)i.getSerializableExtra(Order.EXTRA_ORDER_TRIP));
        for (Drink d : currShop.getDrinks()) {
            drinks.add(d);
        }

        // add drink that was passed in to this order
        Drink passedIn = (Drink) i.getSerializableExtra(Drink.EXTRA_DRINK);
        if(passedIn != null) {
            order.addDrink(passedIn);
        }

        // render based on whether editing order or creating order
        create = (boolean)i.getSerializableExtra(EXTRA_CREATE);
        if(!create) { // edit existing order
            order.setId(i.getStringExtra(Order.PREF_ORDER_ID));
            date = (Date)i.getSerializableExtra(Order.EXTRA_ORDER_DATE);
            order.setDate(date);

            // parse drinks from intent (split by space)
            drinkStr = (String)i.getSerializableExtra(OrderActivity.EXTRA_DRINKS_STRING);
            String[] drinksStr = drinkStr.split("\\s+");
            for(String s: drinksStr){
                String[] drinkArr = s.split(",");

                // find drink in shop list if exists and add to order
                for(Drink shopDrink : currShop.getDrinks()) {
                    if(shopDrink.getId().equals(drinkArr[0])) {
                        for(int j=0; j<Integer.valueOf(drinkArr[1]); ++j) {
                            order.addDrink(shopDrink);
                        }
                    }
                }
            }

            // if trip exists, set spinners accordingly
            if(order.getTrip() != null) {
                try {
                    // set both spinners based on those values
                    tripHrs = order.getTrip().getTravelTime() / 60;
                    tripMins = order.getTrip().getTravelTime() % 60;
                }
                catch (NullPointerException npe){
                    tripHrs = 0;
                    tripMins = 0;
                }
            }
            else { // default spinners to 0hr0min
                tripHrs = 0;
                tripMins = 0;
            }
        }

        // render appropriately depending on the readonly state
        displayInfo();
        if(create) {
            renderReadOnly();
        }
        else {
            renderEditable();
        }

        // set up adapter
        drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink_order, drinks);
        listDrinks.setAdapter(drinkAdapter);

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
                btnSubmitOrder.setBackground(getResources().getDrawable(R.drawable.button_background_green));
                order.addDrink(drinks.get(position));
                drinkAdapter.notifyDataSetChanged();

                displayInfo();
            }
        });

        // long click removes from order
        listDrinks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                order.removeDrink(drinks.get(position).getId());
                if (order.getDrinks().size() == 0) {
                    btnSubmitOrder.setBackground(getResources().getDrawable(R.drawable.button_background_brown));
                }
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
                                    // triggers the calculations and updates the fields in the order
                                    order.getTotalCost(true);
                                    order.getTotalCaffeine(true);
                                    proceedToOrder();
                                }
                            }).show();
                }
            }
        });

        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                new DatePickerDialog(CreateOrderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd");
                        DateFormat dateFormat2 = new SimpleDateFormat("MMM d, yyyy");
                        try {
                            LocalDate myDate = LocalDate.of(year, month+1, dayOfMonth);
                            LocalDate now = LocalDate.now();
                            if(myDate.isAfter(now)){
                                order.setDate(dateFormat1.parse("" + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth()));
                                editDate.setText(dateFormat2.format(order.getDate()));
                            }
                            else {
                                order.setDate(dateFormat1.parse("" + year + "/" + (month + 1) + "/" + dayOfMonth));
                                editDate.setText(dateFormat2.format(order.getDate()));
                            }
                        } catch (ParseException e) {
                            Log.d(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        spinnerTripHrs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String valueStr = parent.getItemAtPosition(position).toString();
                tripHrs = Integer.valueOf(valueStr.substring(0,1));
                order.setTrip(new Trip(currShop.getId(), tripHrs * 60 + tripMins));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tripHrs = 0;
                order.setTrip(new Trip(currShop.getId(), tripHrs * 60 + tripMins));
            }
        });

        spinnerTripMins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String valueStr = parent.getItemAtPosition(position).toString();
                if(valueStr.length() == 5) {
                    tripMins = Integer.valueOf(valueStr.substring(0,1));
                    order.setTrip(new Trip(currShop.getId(), tripHrs * 60 + tripMins));
                }
                else if(valueStr.length() == 6){
                    tripMins = Integer.valueOf(valueStr.substring(0,2));
                    order.setTrip(new Trip(currShop.getId(), tripHrs * 60 + tripMins));
                }
                else {
                    tripMins = 0;
                    order.setTrip(new Trip(currShop.getId(), tripHrs * 60 + tripMins));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tripMins = 0;
                order.setTrip(new Trip(currShop.getId(), tripHrs * 60 + tripMins));
            }
        });
    }

    /**
     * Called when return from OrderActivity
     * Dummy function
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ORDER_CONFIRMATION) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    /**
     * Processes the order
     * Adds order to database and launches intent to view order activity (OrderActivity)
     * Triggered if user submits a valid order
     */
    private void proceedToOrder() {
        // ordered at least one item
        if(order.getDrinks().size() > 0) {
            textError.setVisibility(View.GONE);

            // add order to database
            if(create) { // create new order
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
            else { // update existing order
                // if create new trip
                if(order.getTrip().getId() == null) { // create new trip id in database
                    order.getTrip().setId(Database.getInstance().addTrip(order.getTrip()));
                }

                Database.getInstance().addOrder(order);

                Intent i = new Intent(getApplicationContext(), OrderActivity.class);
                i.putExtra(OrderActivity.EXTRA_READONLY, true);
                i.putExtra(Order.PREF_ORDER_ID, order.getId());
                setResult(RESULT_OK, i);
                finish();
            }
        }
        else { // empty order
            textError.setText(getResources().getString(R.string.emptyOrder));
            textError.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Basic updates based on UI and model values
     * Called when setup and whenever user clicks on a drink
     */
    private void displayInfo() {
        // update total caffeine
        if(customer != null && customer.getLog() != null) {
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

        // caffeine alert
        if(totalCaffeineToday > User.CAFFEINE_LIMIT) {
            textTotalCaffeineToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.danger));
        }
        else {
            textTotalCaffeineToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }

        // show trip duration info
        if(!create) { // editing existing order
            spinnerTripHrs.setSelection(tripHrs);
            spinnerTripMins.setSelection(tripMins);
        }
    }

    /**
     * Inner class for drink adapter to display the list of drinks for this shop
     * Renders the list items in the list of drinks
     */
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
//            ImageView image = convertView.findViewById(R.id.listShopImage);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(d.getName());
            textType.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
            textCaffeine.setText(getResources().getString(R.string.milligrams, d.getCaffeine()));
            textPrice.setText(getResources().getString((R.string.itemPrice), d.getPrice()));
            if(order.getDrinks().get(d.getId()) == null) { // none ordered yet
                textNumOrdered.setText(getString(R.string.x, 0));
            }
            else {
                textNumOrdered.setText(getString(R.string.x, order.getDrinks().get(d.getId()).second));
            }
//            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }

    /**
     * Render so as to CREATE an order
     * Hides ability to edit date
     */
    private void renderReadOnly() {
        textDatePrompt.setVisibility(View.GONE);
        editDate.setVisibility(View.GONE);
    }

    /**
     * Render so as to EDIT an order
     * Show ability to edit date
     */
    private void renderEditable() {
        textDatePrompt.setVisibility(View.VISIBLE);
        editDate.setVisibility(View.VISIBLE);
        editDate.setText(dateFormat.format(date));
        textTripPrompt.setVisibility(View.VISIBLE);
        spinnerTripHrs.setVisibility(View.VISIBLE);
        spinnerTripMins.setVisibility(View.VISIBLE);
        textCreateOrderTitle.setText(getResources().getString(R.string.editOrder));
    }
}
