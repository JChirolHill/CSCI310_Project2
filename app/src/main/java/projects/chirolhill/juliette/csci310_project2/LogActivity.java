package projects.chirolhill.juliette.csci310_project2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import projects.chirolhill.juliette.csci310_project2.R;

public class LogActivity extends AppCompatActivity {

    private ListView listOrders;
    private ImageView caffeineChart;
    private ImageView moneyChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listOrders = findViewById(R.id.listOrders);
        moneyChart = findViewById(R.id.imageMoneyBarChart);
        caffeineChart = findViewById(R.id.imageCaffeineBarChart);

        // do this for the remaining other views that are dynamic
    }
}



