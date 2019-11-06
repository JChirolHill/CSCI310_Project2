package projects.chirolhill.juliette.csci310_project2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import projects.chirolhill.juliette.csci310_project2.model.BasicShop;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Shop;

public class DrinkActivity extends AppCompatActivity {
    public static final String EXTRA_CREATE_DRINK = "extra_create_drink";

    private TextView textToShopValue;
    private EditText editName;
    private EditText editPrice;
    private EditText editCaffeine;
    private RadioGroup radioIsCoffee;
    private RadioButton radioBtnCoffee;
    private RadioButton radioBtnTea;
    private Button btnSave;

    private String shopID;
    private Drink drink;
    private boolean createDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textToShopValue = findViewById(R.id.textToShopValue);
        editName = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editPrice);
        editCaffeine = findViewById(R.id.editCaffeine);
        radioIsCoffee = findViewById(R.id.radioTypeDrink);
        radioBtnCoffee = findViewById(R.id.radio_coffee);
        radioBtnTea = findViewById(R.id.radio_tea);
        btnSave = findViewById(R.id.btnSave);

        // get intent
        Intent i = getIntent();
        createDrink = i.getBooleanExtra(EXTRA_CREATE_DRINK, true);
        shopID = i.getStringExtra(BasicShop.PREF_BASIC_SHOP_ID);

        // set up layout
        textToShopValue.setText(i.getStringExtra(BasicShop.PREF_BASIC_SHOP_NAME));

        // set up layout if edit drink
        if(!createDrink) {
            drink = (Drink)i.getSerializableExtra(Drink.EXTRA_DRINK);
            editName.setText(drink.getName());
            editPrice.setText(Float.toString(drink.getPrice()));
            editCaffeine.setText(Integer.toString(drink.getCaffeine()));
            if(drink.isCoffee()) {
                radioBtnCoffee.setChecked(true);
                radioBtnTea.setChecked(false);
            }
            else {
                radioBtnCoffee.setChecked(false);
                radioBtnTea.setChecked(true);
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make new drink
                if(createDrink) {
                    drink = new Drink((radioIsCoffee.getCheckedRadioButtonId() == R.id.radio_coffee), shopID);
                }
                drink.setIsCoffee((radioIsCoffee.getCheckedRadioButtonId() == R.id.radio_coffee));
                drink.setName(editName.getText().toString().trim());
                drink.setPrice(Float.parseFloat(editPrice.getText().toString().trim()));
                drink.setCaffeine(Integer.parseInt(editCaffeine.getText().toString().trim()));

                // add drink to database
                drink.setId(Database.getInstance().addDrink(drink));

                if(createDrink) {
                    // get shop to database to add drink
                    Database.getInstance().setCallback(new Database.Callback() {
                        @Override
                        public void dbCallback(Object o) {
                            // add drink to shop
                            Shop s = (Shop)o;
                            s.addDrink(drink);
                            Database.getInstance().addShop(s);

                            // return to previous activity
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    Database.getInstance().getShop(shopID);
                }
                else {
                    // return to previous activity
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

}
