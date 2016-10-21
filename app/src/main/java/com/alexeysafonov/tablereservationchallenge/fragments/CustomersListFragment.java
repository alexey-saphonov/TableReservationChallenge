package com.alexeysafonov.tablereservationchallenge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexeysafonov.tablereservationchallenge.R;
import com.alexeysafonov.tablereservationchallenge.model.Customer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ListViewToModelProtocol}
 * interface.
 */
public class CustomersListFragment extends Fragment {

    private ListViewToModelProtocol mListener;
    private CustomersRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomersListFragment() {
    }

    public static CustomersListFragment newInstance() {
        CustomersListFragment fragment = new CustomersListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new CustomersRecyclerViewAdapter(new ArrayList<Customer>(), mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListViewToModelProtocol) {
            mListener = (ListViewToModelProtocol) context;
            mListener.getCustomers()
                    .observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Action1<List<Customer>>() {
                        @Override
                        public void call(List<Customer> customers) {
                            mAdapter.setValues(customers);
                        }
                    });
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListViewToModelProtocol");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ListViewToModelProtocol {
        void onCustomerSelected(Customer item);
        Observable<List<Customer>> getCustomers();
    }
}
