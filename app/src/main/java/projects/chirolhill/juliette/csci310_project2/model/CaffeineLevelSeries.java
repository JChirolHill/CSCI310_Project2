package projects.chirolhill.juliette.csci310_project2.model;

import com.androidplot.xy.XYSeries;

import java.util.ArrayList;

/**
 * This is a custom XYSeries class to represent our user caffeine level data.
 *
 * TODO: verify there won't be any concurrency issues (i.e. x/y become dif length arrays)
 */
public class CaffeineLevelSeries implements XYSeries {

    private ArrayList<Long> xSeries; // dates, stored as unsigned longs
    private ArrayList<Integer> ySeries; // caffeine levels, stored as doubles in mgs
    private String title;

    // TODO: some sort of data "default" logic for when we have incomplete weeks of data for a user

    public CaffeineLevelSeries(String title) {
        this.title = title;
        this.xSeries = new ArrayList<Long>();
        this.ySeries = new ArrayList<Integer>();
    }

    public void add(Long date, Integer caffeineLevel) {
        this.xSeries.add(date);
        this.ySeries.add(caffeineLevel);
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

}
