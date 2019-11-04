package projects.chirolhill.juliette.csci310_project2;


import java.text.NumberFormat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;

import projects.chirolhill.juliette.csci310_project2.model.CaffeineLevelSeries;

public class UserLogActivity extends AppCompatActivity {

    private final String TAG = UserLogActivity.class.getSimpleName();

    private XYPlot caffeineChart;
    private BarRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        caffeineChart = findViewById(R.id.caffeineChart);
        caffeineChart.setRangeBoundaries(new Integer(0), new Integer(500), BoundaryMode.FIXED);
        caffeineChart.setRangeStep(StepMode.INCREMENT_BY_VAL, 100);
        // break up into 7 days = one week, plus one blank spot at front and back to fix a formatting bug
        caffeineChart.setDomainStep(StepMode.SUBDIVIDE, 9);

        // Y-AXIS (Daily Caffeine Level, in mg)
        // X-AXIS (Date, e.g. "10/27", previous 7 days)
        final Long[] domainLabels = {1571554800000L, 1571641200000L, 1571727600000L,
                1571814000000L, 1571900400000L, 1571986800000L, 1572073200000L,
                1572159600000L, 1572246000000L};
        // final Integer[] domainLabels = {10_21_2019, 10_22_2019, 10_23_2019, 10_24_2019, 10_25_2019, 10_26_2019, 10_27_2019};
        Integer[] coffeeLevels = {0, 100, 200, 230, 260, 70, 80, 90, 0};
        Integer[] teaLevels = {0, 100, 100, 130, 160, 170, 180, 90, 0};

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

        // had to hard code the colors because they're apparently translated into ints in the backend,
        // but the ints DON'T properly translate back into the original hex string ...
        BarFormatter bfCoffee = new BarFormatter(Color.parseColor("#ac9782"), Color.WHITE);
        BarFormatter bfTea = new BarFormatter(Color.parseColor("#4d7d55"), Color.WHITE);

        caffeineChart.addSeries(coffeeSeries, bfCoffee);
        caffeineChart.addSeries(teaSeries, bfTea);

        // initialize bar renderer (must be done after set formatter and add series to the plot)
        renderer = caffeineChart.getRenderer(BarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(25));
        // renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(25));
        renderer.setBarOrientation(BarRenderer.BarOrientation.STACKED);

//        caffeineChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new NumberFormat() {
//            @Override
//            public StringBuffer format(double value, StringBuffer toAppendTo, FieldPosition pos) {
//                throw new UnsupportedOperationException("Not yet implemented.");
//            }
//
//            @Override
//            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
//                if (value == 0 || value == 8) return new StringBuffer();
//
//                Date date = new Date(value);
//                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
//
//                return new StringBuffer(formatter.format(date));
//            }
//
//            @Override
//            public Number parse(String string, ParsePosition position) {
//                throw new UnsupportedOperationException("Not yet implemented.");
//            }
//        });

        caffeineChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            // converts the Long received from the X-axis value into a date, and then neatly formats it
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                long value = ((Number) obj).longValue();
                if (value == 0 || value == 8) return new StringBuffer();

                Date date = new Date(value);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");

                return new StringBuffer(formatter.format(date) + " ");
            }

            @Override
            public Number parseObject(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });

        caffeineChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                int intNumber = (int) number;
                if (intNumber == 200) return new StringBuffer("[" + intNumber + "]");
                else return new StringBuffer(Integer.toString(intNumber));
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
    }



}
