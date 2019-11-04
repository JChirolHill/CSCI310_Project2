package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();

    public static final String EXTRA_READONLY = "extra_order_readonly"; // display as view or update

    private TextView textOrderTitle;
//    private TextView totalMoneySpentPrompt;
    private TextView totalMoneySpent;
    private EditText editTotalMoneySpent;
//    private TextView textCaffeineLevelPrompt;
    private TextView textCaffeineLevel;
    private EditText editCaffeineLevel;
//    private TextView textDatePrompt;
    private TextView textDate;
    private EditText editDate;
    private ListView listDrinks;
    private Button btnOk;

    private boolean readonly;
    private String orderID;
    private Order currOrder;
    private List<Drink> drinks;

    private DrinkListAdapter drinkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textOrderTitle = findViewById(R.id.textOrderTitle);
//        totalMoneySpentPrompt = findViewById(R.id.totalMoneySpentPrompt);
        totalMoneySpent = findViewById(R.id.textTotalMoneySpent);
        editTotalMoneySpent = findViewById(R.id.editTotalMoneySpent);
//        caffeineLevelPrompt = findViewById(R.id.textCaffeineLevelPrompt);
        textCaffeineLevel = findViewById(R.id.textCaffeineLevel);
        editCaffeineLevel = findViewById(R.id.editCaffeineLevel);
        textDate = findViewById(R.id.textDate);
        editDate = findViewById(R.id.editDate);
        listDrinks = findViewById(R.id.listDrinks);
        btnOk = findViewById(R.id.btnOk);

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
                DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
                textDate.setText(dateFormat.format(currOrder.getDate()));
                totalMoneySpent.setText(getResources().getString(R.string.dollarCost, currOrder.getTotalCost(false)));
                textCaffeineLevel.setText(currOrder.getTotalCaffeine(false) + " " + getResources().getString(R.string.milligrams));

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

        // render appropriately depending on the readonly state
        if(readonly) {
            renderReadOnly();
        }
        else {
            renderEditable();
        }

        listDrinks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(!readonly) {

                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void renderReadOnly() {
        textOrderTitle.setText(getResources().getString(R.string.viewOrder));
        totalMoneySpent.setVisibility(View.VISIBLE);
        editTotalMoneySpent.setVisibility(View.GONE);
        textCaffeineLevel.setVisibility(View.VISIBLE);
        editCaffeineLevel.setVisibility(View.GONE);
    }

    private void renderEditable() {
        textOrderTitle.setText(getResources().getString(R.string.editOrder));
        totalMoneySpent.setVisibility(View.GONE);
        editTotalMoneySpent.setVisibility(View.VISIBLE);
        textCaffeineLevel.setVisibility(View.GONE);
        editCaffeineLevel.setVisibility(View.VISIBLE);
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
            TextView textType = convertView.findViewById(R.id.listDrinkType);
            TextView textCaffeine = convertView.findViewById(R.id.listDrinkCaffeine);
            TextView textPrice = convertView.findViewById(R.id.listDrinkPrice);
//            ImageView image = convertView.findViewById(R.id.listShopImage);

            Drink d = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(d.getName());
            textType.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
            textCaffeine.setText(d.getCaffeine() + " " + getResources().getString(R.string.milligrams));
            textPrice.setText("$" + Float.toString(d.getPrice()));
//            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }
}
