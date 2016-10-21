package com.alexeysafonov.tablereservationchallenge.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.alexeysafonov.tablereservationchallenge.R;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectTableFragment extends Fragment {

    private TableAdapter mAdapter;
    private TableSelectionProtocol mListener;
    private List<Boolean> mTables;

    public SelectTableFragment() {
        // Required empty public constructor
    }

    public static SelectTableFragment newInstance() {
        SelectTableFragment fragment = new SelectTableFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_table, container, false);

        final GridView tableGrid = (GridView) view.findViewById(R.id.table_grid);
        mAdapter.setState(mTables);
        tableGrid.setAdapter(mAdapter);
        tableGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.getItem(position)) {
                    mListener.onTableSelected(position);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAdapter = new TableAdapter(getContext());
        if (context instanceof CustomersListFragment.ListViewToModelProtocol) {
            mListener = (TableSelectionProtocol) context;
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

    public void setTables(List<Boolean> tables) {
        mTables = tables;
    }

    public interface TableSelectionProtocol {
        void onTableSelected(int tableId);
    }

    private class TableAdapter extends ArrayAdapter<Boolean> {

        List<Boolean> mState = Collections.emptyList();

        public TableAdapter(Context context) {
            super(context, R.layout.table_cell);
        }

        public void setState(List<Boolean> state) {
            mState = state;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mState.size();
        }

        @Nullable
        @Override
        public Boolean getItem(int position) {
            return mState.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.table_cell, parent, false);
            }
            view.setBackgroundColor(getItem(position)? Color.RED: Color.GREEN);
            ((TextView)view.findViewById(R.id.table_id)).setText(Integer.toString(position));
            return view;
        }
    }
}
