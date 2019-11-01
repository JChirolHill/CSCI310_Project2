package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.Drink;
import projects.chirolhill.juliette.csci310_project2.model.Order;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private class DrinkListAdapter extends ArrayAdapter<Drink> {
        public DrinkListAdapter(Context context, int resource, List<Drink> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_view_row, null);
            }

            TextView textDrinkName = convertView.findViewById(R.id.listDrinkName);
            TextView textQuantity = convertView.findViewById(R.id.listQuantity);
            TextView textPrice = convertView.findViewById(R.id.listPrice);
            TextView textCaffeine = convertView.findViewById(R.id.listCaffeine);

            Drink d = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)

            // confused here: I need to calculate the quantity/price/caffeine for multiple drinks
            // with a same drink name within a order, is this correct? So I need to sum up these
            // values for a drink. But this doesn't make sense for me since I do not have access to
            // the order if I choose to do a "DrinkListAdapter"

            textDrinkName.setText(d.getName());
            textQuantity.setText();
            textPrice.setText(s.getPriceRange());
            textAddress.setText(s.getAddress());
            Picasso.get().load(s.getImgURL()).into(image);

            return convertView;
        }
    }

}
