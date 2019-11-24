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
import android.text.InputType;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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


public class CreateOrderActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = CreateOrderActivity.class.getSimpleName();
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
    private boolean editable;
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

        // get shop and drink from intent
        Intent i = getIntent();
        Drink passedIn = (Drink) i.getSerializableExtra(Drink.EXTRA_DRINK);
        currShop = (Shop) i.getSerializableExtra(Shop.PREF_SHOP);
        order = new Order(null, currShop.getId(), null, prefs.getString(User.PREF_USER_ID, "Invalid ID"), new Date());

        for (Drink d : currShop.getDrinks()) {
            drinks.add(d);
        }

        editable = (boolean)i.getSerializableExtra("EXTRA_EDITABLE");
        if(editable) {
            date = (Date)i.getSerializableExtra("EXTRA_DATE");

            drinkStr = (String)i.getSerializableExtra("EXTRA_DRINKS");
            // parse drinks from intent (split by space)
            String[] drinksStr = drinkStr.split("\\s+");
            for(String s: drinksStr){
                String[] drinkArr = s.split(",");
                int amount = Integer.valueOf(drinkArr[1]);
                for(int j = 0; j < amount; ++j){
                    order.addDrink(new Drink(drinkArr[0], currShop.getId()));
                }
            }

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
            else { // default to 0hr0min
                tripHrs = 0;
                tripMins = 0;
            }
        }

        // add drink that was passed in to this order
        if(passedIn != null) {
            order.addDrink(passedIn);
        }

        // render appropriately depending on the readonly state
        if(!editable) {
            displayInfo();
            renderReadOnly();
        }
        else {
            //displayInfo();
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
                cal.setTime(order.getDate());
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
                    if(!editable) { // created new order, show view orders
                        startActivityForResult(i, REQUEST_CODE_ORDER_CONFIRMATION);
                    }
                    else { // return to view order for this order
                        setResult(RESULT_OK, i);
                        finish();
                    }
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

        if(totalCaffeineToday > User.CAFFEINE_LIMIT) {
            textTotalCaffeineToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.danger));
        }
        else {
            textTotalCaffeineToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }

        if(editable) {
            spinnerTripHrs.setSelection(tripHrs);
            spinnerTripMins.setSelection(tripMins);
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

    private void renderReadOnly() {
        textDatePrompt.setVisibility(View.GONE);
        editDate.setVisibility(View.GONE);
    }

    private void renderEditable() {
        textDatePrompt.setVisibility(View.VISIBLE);
        editDate.setVisibility(View.VISIBLE);
        editDate.setText(dateFormat.format(date));
        textTripPrompt.setVisibility(View.VISIBLE);
        spinnerTripHrs.setVisibility(View.VISIBLE);
        spinnerTripMins.setVisibility(View.VISIBLE);
        textCreateOrderTitle.setText(getResources().getString(R.string.editOrder));
    }

    @Override
    public void onClick(View view) {
        datePickerDialog.show();
    }
}
