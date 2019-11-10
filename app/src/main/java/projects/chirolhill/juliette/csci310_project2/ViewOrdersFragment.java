package projects.chirolhill.juliette.csci310_project2;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import projects.chirolhill.juliette.csci310_project2.model.Customer;
import projects.chirolhill.juliette.csci310_project2.model.Database;
import projects.chirolhill.juliette.csci310_project2.model.Order;
import projects.chirolhill.juliette.csci310_project2.model.User;
import projects.chirolhill.juliette.csci310_project2.model.UserLog;

public class ViewOrdersFragment extends Fragment {
    public static final int REQUEST_CODE_VIEW_ORDER = 2;

    private ListView listOrders;

    private List<Order> orders;
    private OrderListAdapter orderAdapter;
    private String userID;
    private UserLog log;
    private Customer currCustomer;

    public ViewOrdersFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static ViewOrdersFragment newInstance() {
        ViewOrdersFragment viewOrdersFragment = new ViewOrdersFragment();
        return viewOrdersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_orders, container, false);

        listOrders = v.findViewById(R.id.listOrders);

        orders = new ArrayList<>();

        // set up adapter
        orderAdapter = new OrderListAdapter(getActivity().getApplicationContext(), R.layout.list_item_order, orders);
        listOrders.setAdapter(orderAdapter);

        // get user
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        userID = prefs.getString(User.PREF_USER_ID, "invalid userid");
        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                currCustomer = (Customer)o;

                // get the user's orders
                populateOrders();
            }
        });
        Database.getInstance().getUser(userID);
        populateOrders();

        listOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // launch intent to view order
                Intent i = new Intent(getActivity().getApplicationContext(), OrderActivity.class);
                i.putExtra(OrderActivity.EXTRA_READONLY, true);
                i.putExtra(Order.PREF_ORDER_ID, orders.get(position).getId());
                startActivityForResult(i, REQUEST_CODE_VIEW_ORDER);
            }
        });

        listOrders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // remove the order from adapter
                final Order removed = orders.remove(position);
                orderAdapter.notifyDataSetChanged();

                // remove from database list of orders
                Database.getInstance().removeOrder(removed);

                // remove from database customer list of orders
                currCustomer.getLog().removeOrder(removed.getId());
                Database.getInstance().addUser(currCustomer);

                // undo snackbar
                Snackbar.make(v.findViewById(R.id.listOrders), getResources().getString(R.string.orderDeleted), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // add back to database
                                Database.getInstance().addOrder(removed);
                                currCustomer.getLog().addOrder(removed);
                                Database.getInstance().addUser(currCustomer);

                                // update adapter
                                orders.add(removed);
                                orderAdapter.notifyDataSetChanged();
                            }
                        }).show();

                return true;
            }
        });

        return v;
    }

    private void populateOrders() {
        orderAdapter.clear();

        Database.getInstance().setCallback(new Database.Callback() {
            @Override
            public void dbCallback(Object o) {
                Customer tempCustomer = (Customer)o;
                log = tempCustomer.getLog();

                // get all orders from database
                Database.getInstance().setCallback(new Database.Callback() {
                    @Override
                    public void dbCallback(Object o) {
//                            currCustomer.getLog().addOrder((Order)o);
                        orders.add((Order)o);
                        orderAdapter.notifyDataSetChanged();
                    }
                });
                for(Map.Entry<String, Order> entry : log.getOrders().entrySet()) {
                    Database.getInstance().getOrder(entry.getKey());
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
//            TextView textTripDuration = convertView.findViewById(R.id.listTripDuration);

            Order order = getItem(position);

            // copy/map the data from the current item (model) to the curr row (view)
            DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            textDate.setText(dateFormat.format(order.getDate()));
            textTotalCost.setText(getResources().getString(R.string.dollarCost, order.getTotalCost(false)));
            textTotalCaffeine.setText(getResources().getString(R.string.milligrams, order.getTotalCaffeine(false)));
//            textTripDuration.setText();

            return convertView;
        }
    }
}
