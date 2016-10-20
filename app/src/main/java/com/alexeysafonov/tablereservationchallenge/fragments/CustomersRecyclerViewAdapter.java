package com.alexeysafonov.tablereservationchallenge.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alexeysafonov.tablereservationchallenge.R;
import com.alexeysafonov.tablereservationchallenge.model.Customer;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.alexeysafonov.tablereservationchallenge.model.Customer} and makes a call to the
 * specified {@link CustomersListFragment.ListViewToModelProtocol}.
 */
public class CustomersRecyclerViewAdapter extends RecyclerView.Adapter<CustomersRecyclerViewAdapter.ViewHolder> {

    private final List<Customer> mValues;
    private final CustomersListFragment.ListViewToModelProtocol mListener;

    public CustomersRecyclerViewAdapter(List<Customer> items, CustomersListFragment.ListViewToModelProtocol listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_customer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mFirstName.setText(mValues.get(position).getFirstName());
        holder.mLastName.setText(mValues.get(position).getLastName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onCustomerSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mFirstName;
        public final TextView mLastName;
        public Customer mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mFirstName = (TextView) view.findViewById(R.id.customer_firstname);
            mLastName = (TextView) view.findViewById(R.id.customer_lastname);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLastName.getText() + "'";
        }
    }
}
