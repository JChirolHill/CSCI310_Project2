package projects.chirolhill.juliette.csci310_project2.model;

import com.androidplot.xy.XYSeries;

import java.util.ArrayList;

import projects.chirolhill.juliette.csci310_project2.model.DateIntegerSeries;

/**
 * This is a custom XYSeries class in which:
 * X values = dates in milliseconds
 * Y values = integers
 */
public class DateIntegerSeries implements XYSeries {

    protected ArrayList<Long> xSeries; // dates, stored as unsigned longs
    protected ArrayList<Integer> ySeries;
    private String title;
    private Integer maxYValue;

    public DateIntegerSeries(String title) {
        this.title = title;
        this.xSeries = new ArrayList<Long>();
        this.ySeries = new ArrayList<Integer>();
    }

    public void add(Long date, Integer value) {
        this.xSeries.add(date);
        this.ySeries.add(value);
    }

    public String getTitle() {
        return this.title;
    }

    public Long getX(int index) {
        return xSeries.get(index);
    }

    public Integer getY(int index) {
        return ySeries.get(index);
    }

    public int size() {
        if (this.xSeries.size() != this.ySeries.size()) return -1;
        else return this.xSeries.size();
    }

    // useful for determining how high to set the max Y value on the graph
    public int getMaxYValue() {
        if (maxYValue == null) {
            int tempMax = 0;
            for (int i = 0; i < ySeries.size(); i++) {
                if (ySeries.get(i) > tempMax) tempMax = ySeries.get(i);
            }
            this.maxYValue = tempMax;
        }
        return this.maxYValue;
    }
}
