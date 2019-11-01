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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Shop;

public class ShopInfoActivity extends AppCompatActivity {
    private final String TAG = ShopInfoActivity.class.getSimpleName();
    public static final String PREF_READ_ONLY = "pref_read_only";

    private TextView textShopName;
    private ImageView imageShop;
    private TextView textRating;
    private TextView textPrice;
    private TextView textItems;
    private TextView textAddress;
    private ListView listDrinks;

    private BasicShop currShop;
    private boolean showStats;

    private DrinkListAdapter drinkAdapter;
    private List<Drink> drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textShopName = findViewById(R.id.textShopName);
        imageShop = findViewById(R.id.imageShop);
        textRating = findViewById(R.id.textRating);
        textPrice = findViewById(R.id.textPrice);
        textItems = findViewById(R.id.textItems);
        textAddress = findViewById(R.id.textAddress);
        listDrinks = findViewById(R.id.list);

        drinks = new ArrayList<>();

        drinkAdapter = new DrinkListAdapter(this, R.layout.list_item_drink, drinks); // put in the XML custom row we created
        listDrinks.setAdapter(drinkAdapter);

        Intent i = getIntent();

        if(i.getSerializableExtra(BasicShop.PREF_BASIC_SHOP) != null) { // comes from map activity when click view drinks
            currShop = (BasicShop)i.getSerializableExtra(BasicShop.PREF_BASIC_SHOP);
            showStats = false;
        }
        else if(i.getSerializableExtra(Shop.PREF_SHOP) != null) { // comes from merchant looking at shop stats
            currShop = (Shop)i.getSerializableExtra(Shop.PREF_SHOP);
            showStats = true;
        }

        textShopName.setText(currShop.getName());
        textRating.setText(Double.toString(currShop.getRating()));
        textPrice.setText(currShop.getPriceRange());
        textAddress.setText(currShop.getAddress());

        // load image
        Picasso.get().load(currShop.getImgURL()).into(imageShop);

        // fetch shop from database if exists
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                if(o != null) {
                    currShop = (Shop)o;
                    if(((Shop) currShop).getDrinks() != null) {
                        for(Drink d : ((Shop) currShop).getDrinks()) {
                            drinkAdapter.add(d);
                            drinkAdapter.notifyDataSetChanged();
                        }
                    }

                    // display drinks
                    textItems.setText(getResources().getString(R.string.itemsListed));
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
            TextView textType = convertView.findViewById(R.id.listDrinkType);
            TextView textCaffeine = convertView.findViewById(R.id.listDrinkCaffeine);
            TextView textPrice = convertView.findViewById(R.id.listDrinkPrice);
//            ImageView image = convertView.findViewById(R.id.listShopImage);

            Drink d = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(d.getName());
            textType.setText(d.isCoffee() ? getResources().getString(R.string.coffee) : getResources().getString(R.string.tea));
            textCaffeine.setText(d.getCaffeine() + getResources().getString(R.string.milligrams));
            textPrice.setText(Float.toString(d.getPrice()));
//            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }
}
