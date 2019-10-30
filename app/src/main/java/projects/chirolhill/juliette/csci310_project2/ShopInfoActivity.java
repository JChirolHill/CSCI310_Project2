package projects.chirolhill.juliette.csci310_project2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;

public class ShopInfoActivity extends AppCompatActivity {
    private final String TAG = ShopInfoActivity.class.getSimpleName();
    public static final String PREF_READ_ONLY = "pref_read_only";

    private TextView textShopName;
    private ImageView imageShop;
    private TextView textRating;
    private TextView textPrice;
    private TextView textItems;
    private TextView textAddress;

    private BasicShop currShop;

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

        Intent i = getIntent();

        textShopName.setText(i.getStringExtra(BasicShop.PREF_BASIC_SHOP_NAME));
        textRating.setText(Double.toString(i.getDoubleExtra(BasicShop.PREF_BASIC_SHOP_RATING, 0.0)));
        textPrice.setText(i.getStringExtra(BasicShop.PREF_BASIC_SHOP_PRICE));
        textAddress.setText(i.getStringExtra(BasicShop.PREF_BASIC_SHOP_ADDRESS));

        // load image
        Picasso.get().load(i.getStringExtra(BasicShop.PREF_BASIC_SHOP_IMAGE)).into(imageShop);
        
        // only show items if exists as shop in database
        textItems.setVisibility(View.GONE);
    }

}
