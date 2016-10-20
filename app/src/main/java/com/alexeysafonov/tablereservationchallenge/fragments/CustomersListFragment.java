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

import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ListViewToModelProtocol}
 * interface.
 */
public class CustomersListFragment extends Fragment {

    private ListViewToModelProtocol mListener;

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
            recyclerView.setAdapter(new CustomersRecyclerViewAdapter(Collections.<Customer>emptyList(), mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListViewToModelProtocol) {
            mListener = (ListViewToModelProtocol) context;
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
    }
}
