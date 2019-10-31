package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Merchant;
import projects.chirolhill.juliette.csci310_project2.model.Shop;
import projects.chirolhill.juliette.csci310_project2.model.User;

public class ShopListing extends AppCompatActivity {
    private static final String TAG = ShopListing.class.getSimpleName();

    private TextView textListDescr;
    private ListView listShops;

    private ShopListAdapter shopAdapter;
    private List<Shop> shops;
    private String ownerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textListDescr = findViewById(R.id.textListDescr);
        listShops = findViewById(R.id.list);

        shops = new ArrayList<>();

        shopAdapter = new ShopListAdapter(this, R.layout.list_item_shop, shops); // put in the XML custom row we created
        listShops.setAdapter(shopAdapter);

        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        this.ownerID = prefs.getString(User.PREF_USER_ID, "Invalid ID");

        // load in shops from database
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                Merchant m = (Merchant)o;
                for (Map.Entry<String, Shop> entry : m.getShops().entrySet()) {
                    Database.getInstance().setCallback(new Database.Callback() {
                        @Override
                        public void dbCallback(Object o) {
                            Shop s = (Shop)o;
                            if(!s.isPendingApproval()) {
                                shopAdapter.add(s);
                                shopAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    Database.getInstance().getShop(entry.getKey());
                }
            }
        });
        Database.getInstance().getUser(ownerID);

        listShops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // launch intent to view Shop details
                Log.d(TAG, "clicked on item at position: " + position);
                Intent i = new Intent(getApplicationContext(), ShopInfoActivity.class);
//                i.putExtra()
                startActivity(i);
            }
        });
    }

    private class ShopListAdapter extends ArrayAdapter<Shop> {
        public ShopListAdapter(Context context, int resource, List<Shop> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_shop, null);
            }

            TextView textName = convertView.findViewById(R.id.listShopName);
            TextView textRating = convertView.findViewById(R.id.listShopRating);
            TextView textPrice = convertView.findViewById(R.id.listShopPrice);
            TextView textAddress = convertView.findViewById(R.id.listShopAddress);
            ImageView image = convertView.findViewById(R.id.listShopImage);

            Shop s = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
            textName.setText(s.getName());
            textRating.setText(Double.toString(s.getRating()));
            textPrice.setText(s.getPriceRange());
            textAddress.setText(s.getAddress());
            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }
}
