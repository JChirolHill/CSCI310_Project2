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

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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

    private XYPlot caffeineBarChart;
    private BarRenderer caffineBarChartRenderer;
    private XYPlot moneyXYPlot;
    private LineAndPointRenderer moneyXYPlotRenderer;

    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");

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

                //TODO: extract final order id from here (via log.orders.getOrders()
                // get all orders from database
                while (ordersStack.size() > 0) {
                    Order order = ordersStack.pop();
                    Database.getInstance().setCallback(new Database.Callback() {
                        @Override
                        public void dbCallback(Object o) {
                            orders.add((Order)o);

                            if (orders.size() == log.getOrders().size()) {
                                // TODO: store the series' as private vars and combine all extraction logic
                                //       into one method
                                // caffeineBarChart: extract dates and caffeine levels from orders
                                DateIntegerSeries caffeineSeries = extractCaffeineData(orders);

                                // moneyXYPlot: extract dates and money levels from orders
                                DateDoubleSeries expenditureSeries = extractExpenditureData(orders);

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
     * TODO: refactor, shares code with extractExpenditureData
     * Pulls out an androidplot-ready XYSeries for the caffeine bar chart.
     * @return
     */
    private DateIntegerSeries extractCaffeineData(List<Order> orders) {
        LocalDate ONE_WEEK_AGO = LocalDate.now().minusDays(7);

        ArrayList<LocalDate> orderedListOfMapDays = new ArrayList<LocalDate>();

        HashMap<LocalDate, Integer> dateToCaffeineMap = new HashMap<LocalDate, Integer>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = ONE_WEEK_AGO.plusDays(i);
            dateToCaffeineMap.put(date, 0);
            orderedListOfMapDays.add(date);
        }

        // loop through orders, comparing their days and adding their caffeine values accordingly
        for (Order order : orders) {
            // dates (in milliseconds) from orders are clocked to noon and converted to dates
            LocalDateTime tempDateResetTime = order.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().atStartOfDay();
            LocalDate tempDate = tempDateResetTime.toLocalDate();
            Log.d(TAG, "ORDER ON DAY: " + tempDate);

            if (ONE_WEEK_AGO.isBefore(tempDate) || ONE_WEEK_AGO.isEqual(tempDate)) {
                dateToCaffeineMap.put(tempDate, dateToCaffeineMap.get(tempDate) + order.getTotalCaffeine(false));
            }
        }

        // TODO create two series: coffee AND tea
        // TODO: move formatting adjustment to inside of onCreateCaffeineChart
        // for formatting, add an empty value at FRONT of series
        DateIntegerSeries caffeineSeries = new DateIntegerSeries("Caffeine");
        caffeineSeries.add(Date.from(ONE_WEEK_AGO.minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0);
        for (int i = 0; i < orderedListOfMapDays.size(); i++) {
            LocalDate localDate = orderedListOfMapDays.get(i);
            Integer caffeine = dateToCaffeineMap.get(localDate);
            Long date =  Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();

            caffeineSeries.add(date, caffeine);
        }
        // for formatting, add an empty value to BACK of series
        caffeineSeries.add(Date.from(ONE_WEEK_AGO.plusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0);

        return caffeineSeries;
    }

    /**
     * TODO: refactor, shares code with extractExpenditureData
     * Pulls out an androidplot-ready XYSeries for the expenditure line plot.
     */
    private DateDoubleSeries extractExpenditureData(List<Order> orders) {
        LocalDate ONE_WEEK_AGO = LocalDate.now().minusDays(7);

        /*
        // testing data
        ArrayList<Long> domainLabels = new ArrayList<Long>();
        for (int i = 0; i < 7; i++) {
            Long date = Date.from(ONE_WEEK_AGO.plusDays(i).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();
            domainLabels.add(date);
        }
        Double[] expenditures = {10.25, 25.25, 0.0, 4.50, 1.50, 10.50, 30.0};
        orders = new ArrayList<Order>();
        for (int i = 0; i < domainLabels.size(); i++) {
            Order newOrder = new Order("Order# " + i);
            newOrder.setDate(new Date(domainLabels.get(i)));
            newOrder.setTotalCost(expenditures[i]);
            orders.add(newOrder);
        }

         */

        ArrayList<LocalDate> orderedListOfMapDays = new ArrayList<LocalDate>();

        HashMap<LocalDate, Double> dateToExpenditureMap = new HashMap<LocalDate, Double>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = ONE_WEEK_AGO.plusDays(i);
            dateToExpenditureMap.put(date, 0.0);
            orderedListOfMapDays.add(date);
        }

        // loop through orders, comparing their days and adding their expenditure values accordingly
        for (Order order : orders) {
            // dates (in milliseconds) from orders are clocked to noon and converted to dates
            LocalDateTime tempDateResetTime = order.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().atStartOfDay();
            LocalDate tempDate = tempDateResetTime.toLocalDate();
            Log.d(TAG, "ORDER ON DAY: " + tempDate);

            if (ONE_WEEK_AGO.isBefore(tempDate) || ONE_WEEK_AGO.isEqual(tempDate)) {
                dateToExpenditureMap.put(tempDate, dateToExpenditureMap.get(tempDate) + order.getTotalCost(false));
            }
        }

        // TODO create two series: coffee AND tea
        // TODO: move formatting adjustment to inside of onCreateCaffeineChart
        // for formatting, add an empty value at FRONT of series
        DateDoubleSeries expenditureSeries = new DateDoubleSeries("Expenditures");
        expenditureSeries.add(Date.from(ONE_WEEK_AGO.minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0.0);
        for (int i = 0; i < orderedListOfMapDays.size(); i++) {
            LocalDate localDate = orderedListOfMapDays.get(i);
            Double dailyExpenditure = dateToExpenditureMap.get(localDate);
            Long date =  Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime();

            expenditureSeries.add(date, dailyExpenditure);
        }
        // for formatting, add an empty value to BACK of series
        expenditureSeries.add(Date.from(ONE_WEEK_AGO.plusDays(7).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime(), 0.0);

        return expenditureSeries;
    }

    /**
     * Setup code for the caffeine chart (formatting, incorporating data)
     * TODO: support a second series, teaSeries
     */
    private void onCreateCaffeineBarChart(DateIntegerSeries caffeineSeries) {
        // refreshes the chart (it will be loaded with default vals at this point)
        caffeineBarChart.clear();
        caffeineBarChart.redraw();

        // formatting: set y-axis increments from 0 to 1000 in increments of 200, and add extra
        // vals at beg/end to create a margin for the bars
        caffeineBarChart.setRangeBoundaries(new Integer(0), new Integer(1000), BoundaryMode.FIXED);
        caffeineBarChart.setRangeStep(StepMode.INCREMENT_BY_VAL, 200);
        caffeineBarChart.setDomainStep(StepMode.SUBDIVIDE, 9);

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
     * TODO: support a second line, teaExpenditures
     */
    private void onCreateMoneyXYPlot(DateDoubleSeries expenditureSeries) {
        // refreshes the chart (it will be loaded with default vals at this point)
        moneyXYPlot.clear();
        moneyXYPlot.redraw();

        // formatting: add extra vals at beg/end (7 days +1 on each end = 9) to create a margin for the bars
        moneyXYPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 2.5);
        Double maxYValue = expenditureSeries.getmaxYValue();
        moneyXYPlot.setRangeBoundaries(0, (maxYValue > 20 ? maxYValue.intValue() : 20), BoundaryMode.FIXED);
        moneyXYPlot.setDomainStep(StepMode.SUBDIVIDE, 9);

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
        // moneyXYPlot.addSeries(coffeeSeries, coffeeLineFormatter);
        // moneyXYPlot.addSeries(teaSeries, teaLineFormatter);

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

}