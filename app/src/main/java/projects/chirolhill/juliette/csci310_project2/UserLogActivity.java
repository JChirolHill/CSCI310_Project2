package projects.chirolhill.juliette.csci310_project2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;

public class UserLogActivity extends AppCompatActivity {

    private ListView listOrders;
    private ImageView caffeineChart;
    private ImageView moneyChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listOrders = findViewById(R.id.listOrders);
        moneyChart = findViewById(R.id.imageMoneyBarChart);
        caffeineChart = findViewById(R.id.imageCaffeineBarChart);
    }
}



