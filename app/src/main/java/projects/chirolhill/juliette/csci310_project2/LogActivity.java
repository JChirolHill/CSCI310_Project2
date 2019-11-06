package projects.chirolhill.juliette.csci310_project2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.DateDoubleSeries;
import projects.chirolhill.juliette.csci310_project2.model.DateIntegerSeries;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.UserLog;

public class LogActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_VIEW_ORDER = 2;
    private final String TAG = LogActivity.class.getSimpleName();

    private String userID;
    private UserLog log;
    private List<Order> orders;
    private OrderListAdapter orderAdapter;

    private XYPlot caffeineBarChart;
    private BarRenderer caffineBarChartRenderer;
    private XYPlot moneyXYPlot;
    private LineAndPointRenderer moneyXYPlotRenderer;
    private ListView ordersList;

    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        orders = new ArrayList<Order>();
        ordersList = findViewById(R.id.ordersList);

        // set up adapter
        orderAdapter = new OrderListAdapter(this, R.layout.list_item_order, orders);
        ordersList.setAdapter(orderAdapter);

        // get user's orders
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        userID = prefs.getString(User.PREF_USER_ID, "invalid userid");

        // NOTE: this is asynchronous, see code below for workaround to populate the orders list
        populateOrders();

        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // launch intent to view order
                Intent i = new Intent(getApplicationContext(), OrderActivity.class);
                i.putExtra(OrderActivity.EXTRA_READONLY, true);
                i.putExtra(Order.PREF_ORDER_ID, orders.get(position).getId());
                startActivityForResult(i, REQUEST_CODE_VIEW_ORDER);
            }
        });

        // caffeineBarChart: extract dates and caffeine levels from orders
        DateIntegerSeries caffeineSeries = extractCaffeineData(orders);

        // moneyXYPlot: extract dates and money levels from orders
        DateDoubleSeries expenditureSeries = extractExpenditureData(orders);

        // ordersList: extract list of orders

        // create charts and order list
        caffeineBarChart = findViewById(R.id.caffeineBarChart);
        moneyXYPlot = findViewById(R.id.moneyXYPlot);
        ordersList = findViewById(R.id.ordersList);

        // setup charts and order list
        // TODO: pass in the data extracted from DB as an argument, e.g. DateIntegerSeries
        this.onCreateCaffeineBarChart();
        this.onCreateMoneyXYPlot();

        // TODO: set up the list of logs
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_VIEW_ORDER) {
            if (resultCode == RESULT_OK) {
                // update the list of orders when return from viewing orders
                populateOrders();
            }
        }
    }

    private void populateOrders() {
        orderAdapter.clear();

        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                Customer tempCustomer = (Customer)o;
                log = tempCustomer.getLog();

                //TODO: extract final order id from here (via log.orders.getOrders()
                // get all orders from database
                for(Order order : log.getOrders()) {
                    Database.getInstance().setCallback(new Database.Callback() {
                        @Override
                        public void dbCallback(Object o) {
                            orders.add((Order)o);
                            orderAdapter.notifyDataSetChanged();
                            // TODO: check if this is final order, if so, trigger extract functions
                        }
                    });
                    Database.getInstance().getOrder(order.getId());
                }
            }
        });
        Database.getInstance().getUser(userID);
    }

    private class OrderListAdapter extends ArrayAdapter<Order> {
        public OrderListAdapter(Context context, int resource, List<Order> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_order, null);
            }

            TextView textDate = convertView.findViewById(R.id.listDate);
            TextView textTotalCost = convertView.findViewById(R.id.listTotalCost);
            TextView textTotalCaffeine = convertView.findViewById(R.id.listTotalCaffeine);
            TextView textTripDuration = convertView.findViewById(R.id.listTripDuration);

            Order order = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
//            DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
//            textDate.setText(dateFormat.format(order.getDate()));
//            textTotalCost.setText(getResources().getString(R.string.dollarCost, order.getTotalCost(false)));
//            textTotalCaffeine.setText(order.getTotalCaffeine(false) + " " + getResources().getString(R.string.milligrams));
//            textTripDuration.setText();

            return convertView;
        }
    }

    /**
     * Pulls out an androidplot-ready XYSeries for the caffeine bar chart.
     * @return
     */
    private DateIntegerSeries extractCaffeineData(List<Order> orders) {
        // TODO: given a list of orders, extract an XYSeries of datetimes and caffeine data
        LocalDate ONE_WEEK_AGO = LocalDate.now().minusDays(7);

        Date date = new Date();
        LocalDate date_ = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date date__ = Date.from(date_.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        HashMap<LocalDate, Integer> dateToCaffeineMap = new HashMap<LocalDate, Integer>();
        for (int i = 0; i < 7; i++) {
            dateToCaffeineMap.put(ONE_WEEK_AGO.plusDays(i), 0);
        }
        // loop through orders, comparing their days and adding their caffeine values accordingly
        // dates from orders are converted to LocalDates, and clocked to the start of day before output
        for (Order order : orders) {
                LocalDate tempDate = order.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Log.d(TAG, "ORDER ON DAY: " + tempDate);

                dateToCaffeineMap.put(tempDate, dateToCaffeineMap.get(tempDate) + order.getTotalCaffeine(false));
        }

        // TODO convert contents of hashmap into a series
        for (HashMap.Entry<LocalDate, Integer> entry : dateToCaffeineMap.entrySet()) {
            Long entryDate = Date.from(entry.getKey().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            Integer entryCaffeine = entry.getValue();

            // TODO add to a list or something and eventually turn results into series
        }

        return new DateIntegerSeries("test");
    }

    /**
     * Pulls out an androidplot-ready XYSeries for the expenditure line plot.
     */
    private DateDoubleSeries extractExpenditureData(List<Order> orders) {

        return new DateDoubleSeries("test");
    }

    /**
     * Setup code for the caffeine chart (formatting, incorporating data)
     */
    private void onCreateCaffeineBarChart() {
        // formatting: set y-axis increments from 0 to 1000 in increments of 200, and add extra
        // vals at beg/end to create a margin for the bars
        caffeineBarChart.setRangeBoundaries(new Integer(0), new Integer(1000), BoundaryMode.FIXED);
        caffeineBarChart.setRangeStep(StepMode.INCREMENT_BY_VAL, 200);
        caffeineBarChart.setDomainStep(StepMode.SUBDIVIDE, 9);

        // Y-AXIS (Daily Caffeine Level, in mg)
        // X-AXIS (Date, e.g. "10/27", previous 7 days)
        final Long[] domainLabels = {1571554800000L, 1571641200000L, 1571727600000L,
                1571814000000L, 1571900400000L, 1571986800000L, 1572073200000L,
                1572159600000L, 1572246000000L};
        Integer[] coffeeLevels = {0, 100, 200, 230, 260, 70, 180, 90, 0};
        Integer[] teaLevels = {0, 100, 100, 330, 260, 270, 180, 190, 0};

        DateIntegerSeries coffeeSeries = new DateIntegerSeries("Coffee");
        for (int i = 0; i < coffeeLevels.length; i++) {
            coffeeSeries.add(domainLabels[i], coffeeLevels[i]);
        }
        DateIntegerSeries teaSeries = new DateIntegerSeries("Tea");
        for (int i = 0; i < teaLevels.length; i++) {
            teaSeries.add(domainLabels[i], teaLevels[i]);
        }

        // had to hard code the colors because they're apparently translated into ints in the backend,
        // but the ints DON'T properly translate back into the original hex string ...
        BarFormatter coffeeBarFormatter = new BarFormatter(Color.parseColor("#ac9782"), Color.WHITE);
        BarFormatter teaBarFormatter = new BarFormatter(Color.parseColor("#4d7d55"), Color.WHITE);

        caffeineBarChart.addSeries(coffeeSeries, coffeeBarFormatter);
        caffeineBarChart.addSeries(teaSeries, teaBarFormatter);

        // initialize bar caffineBarChartRenderer (must be done after set formatter and add series to the plot)
        caffineBarChartRenderer = caffeineBarChart.getRenderer(BarRenderer.class);
        caffineBarChartRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(25));
        caffineBarChartRenderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

        // TODO: define this as a real class since the other graph also uses it
        // X-AXIS = date values, formatted as "10/27"
        caffeineBarChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                long value = ((Number) obj).longValue();
                // TODO: detect edge values and render them empty
                return new StringBuffer(formatter.format(new Date(value)) + " ");
            }

            @Override
            public Number parseObject(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });

        // Y-AXIS = caffeine values, formatted as "800"
        caffeineBarChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int intNumber = ((Number) obj).intValue();
                if (intNumber == 400) return new StringBuffer("[" + intNumber + "]");
                else return new StringBuffer(Integer.toString(intNumber));
            }

            @Override
            public Number parseObject(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
    }

    /**
     * Setup code for the money chart (formatting, incorporating data)
     */
    private void onCreateMoneyXYPlot() {
        // formatting: add extra vals at beg/end (7 days +1 on each end = 9) to create a margin for the bars
        moneyXYPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 5.0);
        moneyXYPlot.setDomainStep(StepMode.SUBDIVIDE, 9);

        // Y-AXIS (Daily Expenditures, in dollars)
        // X-AXIS (Date, e.g. "10/27", previous 7 days)
        final Long[] domainLabels = {1571554800000L, 1571641200000L, 1571727600000L,
                1571814000000L, 1571900400000L, 1571986800000L, 1572073200000L,
                1572159600000L, 1572246000000L};
        Double[] coffeeExpenditures = {0.0, 10.25, 25.25, 0.0, 4.50, 1.50, 10.50, 30.0, 0.0};
        Double[] teaExpenditures = {0.0, 5.50, 5.25, 0.0, 30.0, 20.0, 10.0, 4.75, 0.0};

        DateDoubleSeries coffeeSeries = new DateDoubleSeries("Coffee");
        for (int i = 0; i < coffeeExpenditures.length; i++) {
            coffeeSeries.add(domainLabels[i], coffeeExpenditures[i]);
        }
        DateDoubleSeries teaSeries = new DateDoubleSeries("Tea");
        for (int i = 0; i < teaExpenditures.length; i++) {
            teaSeries.add(domainLabels[i], teaExpenditures[i]);
        }

        // format the lines (color, smoothing, removing fill)
        LineAndPointFormatter coffeeLineFormatter = new LineAndPointFormatter(Color.parseColor("#ac9782"),
                null, null, null);
        coffeeLineFormatter.getLinePaint().setStrokeWidth(8);
        coffeeLineFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(20,
                CatmullRomInterpolator.Type.Centripetal)); // smooths out the lines
        coffeeLineFormatter.setFillPaint(null); // to prevent lines from filling .. may break in the future?
        LineAndPointFormatter teaLineFormatter = new LineAndPointFormatter(Color.parseColor("#4d7d55"),
                null, null, null);
        teaLineFormatter.getLinePaint().setStrokeWidth(8);
        teaLineFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(20,
                CatmullRomInterpolator.Type.Centripetal)); // smooths out the lines
        teaLineFormatter.setFillPaint(null);

        moneyXYPlot.addSeries(coffeeSeries, coffeeLineFormatter);
        moneyXYPlot.addSeries(teaSeries, teaLineFormatter);

        // initialize line and point renderer (must be done after set formatter and add series to the plot)
        moneyXYPlotRenderer = moneyXYPlot.getRenderer(LineAndPointRenderer.class);

        // X-AXIS = date values, formatted as "10/27"
        moneyXYPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                long value = ((Number) obj).longValue();
                return new StringBuffer(formatter.format(new Date(value)) + " ");
            }

            @Override
            public Number parseObject(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });

        // Y-AXIS = dollar values, formatted as "$50"
        moneyXYPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                // trying to format #'s like $13.50, but doesn't work for numbers ending in .0
                // double number = Double.parseDouble(String.format("%.2f", ((Number) obj).doubleValue()));
                int number = ((Number) obj).intValue();
                return new StringBuffer("$" + number);
            }

            @Override
            public Number parseObject(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
    }

    /**
     * Setup code for the list of orders (formatting, incorporating data)
     */
    private void onCreateOrderLog() {

    }
}



