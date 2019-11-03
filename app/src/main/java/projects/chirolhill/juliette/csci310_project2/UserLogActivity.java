package projects.chirolhill.juliette.csci310_project2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

import projects.chirolhill.juliette.csci310_project2.model.CaffeineLevelSeries;

public class UserLogActivity extends AppCompatActivity {

    private XYPlot caffeineChart;
    private BarRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        caffeineChart = findViewById(R.id.caffeineChart);

        // dummy values
        // IDEAL GRAPH
        // Y-AXIS (Daily Caffeine Level, in mg)
        // X-AXIS (Date, e.g. "10/27", previous 7 days)
        // final Number[] domainLabels = {1, 2, 3, 4, 5, 6, 7};
        final Integer[] domainLabels = {10_21_2019, 10_22_2019, 10_23_2019, 10_24_2019, 10_25_2019, 10_26_2019, 10_27_2019};
        Double[] coffeeLevels = {100.0, 200.0, 230.0, 260.0, 270.0, 180.0, 90.0};
        Double[] teaLevels = {100.0, 200.0, 230.0, 260.0, 270.0, 180.0, 90.0};

//        XYSeries coffeeSeries = new SimpleXYSeries(
//                Arrays.asList(coffeeLevels), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Coffee");
//        XYSeries teaSeries = new SimpleXYSeries(
//                Arrays.asList(teaLevels), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Tea");
        CaffeineLevelSeries coffeeSeries = new CaffeineLevelSeries("Coffee");
        for (int i = 0; i < coffeeLevels.length; i++) {
            coffeeSeries.add(domainLabels[i], coffeeLevels[i]);
        }
        CaffeineLevelSeries teaSeries = new CaffeineLevelSeries("Tea");
        for (int i = 0; i < teaLevels.length; i++) {
            teaSeries.add(domainLabels[i], teaLevels[i]);
        }

        // create a bar formatter with a red fill color and a white outline:
        BarFormatter bfCoffee = new BarFormatter(Color.RED, Color.WHITE);
        BarFormatter bfTea = new BarFormatter(Color.GREEN, Color.WHITE);

        caffeineChart.addSeries(coffeeSeries, bfCoffee);
        caffeineChart.addSeries(teaSeries, bfTea);

        // initialize bar renderer (must be done after set formatter and add series to the plot)
        renderer = caffeineChart.getRenderer(BarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(10));
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(5));
        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

//        caffeineChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
//            @Override
//            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                int i = Math.round(((Number) obj).floatValue());
//                return toAppendTo.append(domainLabels[i]);
//            }
//            @Override
//            public Object parseObject(String source, ParsePosition pos) {
//                return null;
//            }
//        });

        caffeineChart.setRangeBoundaries(new Integer(0), new Integer(800), BoundaryMode.FIXED);
    }



}
