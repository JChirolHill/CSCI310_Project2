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
import android.widget.Button;
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
import com.androidplot.xy.YValueMarker;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Map;

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

    private Button btnViewOrders;

    private String userID;
    private UserLog log;
    private List<Order> orders;

    private DateIntegerSeries caffeineSeries;
    private XYPlot caffeineBarChart;
    private BarRenderer caffineBarChartRenderer;
    private DateDoubleSeries expenditureSeries;
    private XYPlot moneyXYPlot;
    private LineAndPointRenderer moneyXYPlotRenderer;

    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
    private final LocalDate ONE_WEEK_AGO = LocalDate.now().minusDays(7);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // "View Orders" button launches intent to ViewOrdersActivity
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ViewOrdersActivity.class);
                startActivity(i);
            }
        });

        // get user's orders
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        userID = prefs.getString(User.PREF_USER_ID, "invalid userid");
        orders = new ArrayList<Order>();

        // a callback within this method will gather the graph data and trigger graph creation
        populateOrdersAndMakeGraphs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_VIEW_ORDER) {
            if (resultCode == RESULT_OK) {
                // update the list of orders when return from viewing orders
                populateOrdersAndMakeGraphs();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
    }

    /**
     * This method populates the List<Order> orders stored with this class.
     * A callback is triggered to extract the current user, from which a series
     * of callbacks are triggered to extract each order from the database.
     * Upon final execution of those callbacks, the graph is created.
     */
    private void populateOrdersAndMakeGraphs() {
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                Customer tempCustomer = (Customer)o;
                log = tempCustomer.getLog();
                final Stack<Order> ordersStack = new Stack<Order>();
                ordersStack.addAll(log.getOrders().values());

                // get all orders from database
                while (ordersStack.size() > 0) {
                    Order order = ordersStack.pop();
                    Database.getInstance().setCallback(new Database.Callback() {
                        @Override
                        public void dbCallback(Object o) {
                            orders.add((Order)o);

                            if (orders.size() == log.getOrders().size()) {
                                extractDataFromOrders(orders);

                                // create charts and order list
                                caffeineBarChart = findViewById(R.id.caffeineBarChart);
                                moneyXYPlot = findViewById(R.id.moneyXYPlot);

                                // setup charts and order list
                                onCreateCaffeineBarChart(caffeineSeries);
                                onCreateMoneyXYPlot(expenditureSeries);
                            }
                        }
                    });
                    Database.getInstance().getOrder(order.getId());
                }
            }
        });
        Database.getInstance().getUser(userID);
    }

    /**
     * Pulls out the necessary data for the graphs from the list of orders:
     * - caffeineSeries: total daily caffeine consumption for the past week, plus current day (TODO: separate by beverage)
     * - expenditureSeries: total daily expenditures for the past week, plus current day
     * @param orders
     */
    private void extractDataFromOrders(List<Order> orders) {
        ArrayList<LocalDate> orderedListOfMapDays = new ArrayList<LocalDate>();

        HashMap<LocalDate, Integer> dateToCaffeineMap = new HashMap<LocalDate, Integer>();
        HashMap<LocalDate, Double> dateToExpenditureMap = new HashMap<LocalDate, Double>();
        for (int i = 0; i < 8; i++) {
            LocalDate date = ONE_WEEK_AGO.plusDays(i);
            dateToCaffeineMap.put(date, 0);
            dateToExpenditureMap.put(date, 0.0);
            orderedListOfMapDays.add(date);
        }

        // loop through orders, comparing their days and adding values accordingly
        for (Order order : orders) {
            // dates (in milliseconds) from orders are clocked to noon and converted to dates
            LocalDateTime tempDateResetTime = order.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().atStartOfDay();
            LocalDate tempDate = tempDateResetTime.toLocalDate();
            Log.d(TAG, "ORDER ON DAY: " + tempDate);

            if (ONE_WEEK_AGO.isBefore(tempDate) || ONE_WEEK_AGO.isEqual(tempDate)) {
                // CAFFEINE
                // attempt to calculate a caffeine value, either by refreshing or static; otherwise, assume 0
                int orderCaffeineLevel = 0;
                orderCaffeineLevel += (order.getTotalCaffeine(true) > 0 ? order.getTotalCaffeine(true) :
                        order.getTotalCaffeine(false) > 0 ? order.getTotalCaffeine(false) : 0);
                dateToCaffeineMap.put(tempDate, dateToCaffeineMap.get(tempDate) + orderCaffeineLevel);

                // EXPENDITURES
                // attempt to calculate an expenditure value, either by refreshing or static; otherwise, assume 0
                double dailyExpenditureLevel = 0.0;
                dailyExpenditureLevel += (order.getTotalCost(true) > 0 ? order.getTotalCost(true) :
                        order.getTotalCost(false) > 0 ? order.getTotalCost(false) : 0.0);
                dateToExpenditureMap.put(tempDate, dateToExpenditureMap.get(tempDate) + dailyExpenditureLevel);
            }
        }

        // TODO create two series: coffee AND tea
        // TODO: move formatting adjustment to inside of onCreateCaffeineChart
        // CAFFEINE SERIES
        caffeineSeries = new DateIntegerSeries("Caffeine");
        // for formatting, add an empty value at FRONT of series
        caffeineSeries.add(Date.from(ONE_WEEK_AGO.minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0);
        for (int i = 0; i < orderedListOfMapDays.size(); i++) {
            LocalDate localDate = orderedListOfMapDays.get(i);
            Integer caffeine = dateToCaffeineMap.get(localDate);
            Long date =  Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();

            caffeineSeries.add(date, caffeine);
        }
        // for formatting, add an empty value to BACK of series
        caffeineSeries.add(Date.from(ONE_WEEK_AGO.plusDays(8).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0);

        // TODO create two series: coffee AND tea
        // TODO: move formatting adjustment to inside of onCreateCaffeineChart
        // EXPENDITURE SERIES
        expenditureSeries = new DateDoubleSeries("Expenditures");
        expenditureSeries.add(Date.from(ONE_WEEK_AGO.minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0.0);
        for (int i = 0; i < orderedListOfMapDays.size(); i++) {
            LocalDate localDate = orderedListOfMapDays.get(i);
            Double dailyExpenditure = dateToExpenditureMap.get(localDate);
            Long date =  Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();

            expenditureSeries.add(date, dailyExpenditure);
        }
        // for formatting, add an empty value to BACK of series
        expenditureSeries.add(Date.from(ONE_WEEK_AGO.plusDays(8).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0.0);
    }

    /**
     * Setup code for the caffeine chart (formatting, incorporating data)
     * TODO: support a second series, teaSeries
     */
    private void onCreateCaffeineBarChart(DateIntegerSeries caffeineSeries) {
        // refreshes the chart (it will be loaded with default vals at this point)
        caffeineBarChart.clear();
        caffeineBarChart.redraw();

        // default graph: supports y from 0 to 1000 in increments of 200, but this can be
        // overridden by very large values, in which case it's MAX_VALUE rounded to next
        // increment of 200, in increments of MAX_VALUE / 5
        int maxRange = 1000;
        if (caffeineSeries.getmaxYValue() > 1000) {
            if (caffeineSeries.getmaxYValue() % 200 != 0) maxRange = caffeineSeries.getmaxYValue() + (200 - (caffeineSeries.getmaxYValue() % 200));
            else maxRange = caffeineSeries.getmaxYValue();
        }

        caffeineBarChart.setRangeBoundaries(0, maxRange, BoundaryMode.FIXED);
        caffeineBarChart.setRangeStep(StepMode.SUBDIVIDE, 5);
        caffeineBarChart.addMarker(new YValueMarker(400, null)); // red line for 400 mg
        caffeineBarChart.setDomainStep(StepMode.SUBDIVIDE, 10);

        // had to hard code the colors because they're apparently translated into ints in the backend,
        // but the ints DON'T properly translate back into the original hex string ...
        BarFormatter coffeeBarFormatter = new BarFormatter(Color.parseColor("#ac9782"), Color.WHITE);
        // BarFormatter teaBarFormatter = new BarFormatter(Color.parseColor("#4d7d55"), Color.WHITE);

        caffeineBarChart.addSeries(caffeineSeries, coffeeBarFormatter);
        // caffeineBarChart.addSeries(teaSeries, teaBarFormatter);

        // initialize bar caffineBarChartRenderer (must be done after set formatter and add series to the plot)
        caffineBarChartRenderer = caffeineBarChart.getRenderer(BarRenderer.class);
        caffineBarChartRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(25));
        caffineBarChartRenderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

        // X-AXIS = date values, formatted as "10/27"
        caffeineBarChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new xAxisDateFormat());

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
     * TODO: support a second line, teaExpenditures
     */
    private void onCreateMoneyXYPlot(DateDoubleSeries expenditureSeries) {
        // refreshes the chart (it will be loaded with default vals at this point)
        moneyXYPlot.clear();
        moneyXYPlot.redraw();

        // default graph: supports y from $0.00 to $20.00 in increments of $2.50, but this can be
        // overridden by very large values, in which case it's MAX_VALUE rounded to next
        // increment of $2.50, in increments of MAX_VALUE / 8
        double maxRange = 20;
        if (expenditureSeries.getmaxYValue() > 20) {
            if (expenditureSeries.getmaxYValue() % 5 != 0) maxRange = expenditureSeries.getmaxYValue() + (5 - (expenditureSeries.getmaxYValue() % 5));
            else maxRange = expenditureSeries.getmaxYValue();
        }

        // formatting: add extra vals at beg/end (7 days +1 on each end = 9) to create a margin for the bars
        moneyXYPlot.setRangeStep(StepMode.SUBDIVIDE, 8);
        moneyXYPlot.setRangeBoundaries(0, maxRange, BoundaryMode.FIXED);
        moneyXYPlot.setDomainStep(StepMode.SUBDIVIDE, 10);

        // format the lines (color, smoothing, removing fill)
        LineAndPointFormatter coffeeLineFormatter = new LineAndPointFormatter(Color.parseColor("#ac9782"),
                null, null, null);
        coffeeLineFormatter.getLinePaint().setStrokeWidth(8);
        coffeeLineFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(20,
                CatmullRomInterpolator.Type.Centripetal)); // smooths out the lines
        coffeeLineFormatter.setFillPaint(null); // to prevent lines from filling .. may break in the future?
        /*
        LineAndPointFormatter teaLineFormatter = new LineAndPointFormatter(Color.parseColor("#4d7d55"),
                null, null, null);
        teaLineFormatter.getLinePaint().setStrokeWidth(8);
        teaLineFormatter.setInterpolationParams(new CatmullRomInterpolator.Params(20,
                CatmullRomInterpolator.Type.Centripetal)); // smooths out the lines
        teaLineFormatter.setFillPaint(null);
        */

        moneyXYPlot.addSeries(expenditureSeries, coffeeLineFormatter);
        // moneyXYPlot.addSeries(teaSeries, teaLineFormatter);

        // initialize line and point renderer (must be done after set formatter and add series to the plot)
        moneyXYPlotRenderer = moneyXYPlot.getRenderer(LineAndPointRenderer.class);

        // X-AXIS = date values, formatted as "10/27"
        moneyXYPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new xAxisDateFormat());

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
     * A formatter class to cleanly represent dates on the graph X-axis.
     * Turns long value milliseconds into a MM/DD.
     */
    class xAxisDateFormat extends Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            long value = ((Number) obj).longValue();

            // ignore the edge dates, those were added just to create margins on the left and right
            // TODO figure out if this is actually necessary
            LocalDate valueDate = Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate();
            if (valueDate.isBefore(ONE_WEEK_AGO) ||
                    valueDate.isAfter(ONE_WEEK_AGO.plusDays(7))) return new StringBuffer();

            return new StringBuffer(formatter.format(new Date(value)) + " ");
        }

        @Override
        public Number parseObject(String string, ParsePosition position) {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }

}