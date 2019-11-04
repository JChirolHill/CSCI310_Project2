package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

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

    private boolean readonly;
    private String orderID;
    private Order currOrder;

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

        // either from shopinfoactivity -> readonly (review order)
        // or from profileactivity -> readonly (view/edit past orders)
        Intent i = getIntent();
        readonly = i.getBooleanExtra(EXTRA_READONLY, true);
        orderID = i.getStringExtra(Order.PREF_ORDER_ID);

        // get the order from database
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                currOrder = (Order)o;

                // set values in layout based on order
                DateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
                textDate.setText(dateFormat.format(currOrder.getDate()));
                totalMoneySpent.setText(String.format(Double.toString(currOrder.getTotalCost())));
                textCaffeineLevel.setText(currOrder.getTotalCaffeine());
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

//    private class DrinkListAdapter extends ArrayAdapter<Drink> {
//        public DrinkListAdapter(Context context, int resource, List<Drink> objects) {
//            super(context, resource, objects);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if(convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.list_view_row, null);
//            }
//
//            TextView textDrinkName = convertView.findViewById(R.id.listDrinkName);
//            TextView textQuantity = convertView.findViewById(R.id.listQuantity);
//            TextView textPrice = convertView.findViewById(R.id.listPrice);
//            TextView textCaffeine = convertView.findViewById(R.id.listCaffeine);
//
//            Drink d = getItem(position);
//
//            // copy/map the data from the current item (model) to the curr row (view)
//
//            // confused here: I need to git
//
//            textDrinkName.setText(d.getName());
//            textQuantity.setText();
//            textPrice.setText(s.getPriceRange());
//            textAddress.setText(s.getAddress());
//            Picasso.get().load(s.getImgURL()).into(image);
//
//            return convertView;
//        }
//    }
}
