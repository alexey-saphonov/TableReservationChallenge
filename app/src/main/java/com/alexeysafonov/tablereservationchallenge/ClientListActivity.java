package com.alexeysafonov.tablereservationchallenge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alexeysafonov.tablereservationchallenge.fragments.CustomersListFragment;
import com.alexeysafonov.tablereservationchallenge.fragments.SelectTableFragment;
import com.alexeysafonov.tablereservationchallenge.model.Customer;

public class ClientListActivity extends AppCompatActivity implements SelectTableFragment.TableSelectionProtocol, CustomersListFragment.ListViewToModelProtocol {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

    }

    @Override
    public void onCustomerSelected(Customer item) {
        SelectTableFragment fragment = SelectTableFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content_fragment, fragment).addToBackStack(null).commit();
        getSupportFragmentManager().executePendingTransactions();

        //fragment.setTables(...);
    }

    @Override
    public void onTableSelected(int tableId) {

    }
}
