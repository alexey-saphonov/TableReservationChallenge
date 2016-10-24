package com.alexeysafonov.tablereservationchallenge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alexeysafonov.tablereservationchallenge.fragments.CustomersListFragment;
import com.alexeysafonov.tablereservationchallenge.fragments.SelectTableFragment;
import com.alexeysafonov.tablereservationchallenge.model.Customer;
import com.alexeysafonov.tablereservationchallenge.model.Model;
import com.alexeysafonov.tablereservationchallenge.model.Table;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

public class ClientListActivity extends AppCompatActivity implements SelectTableFragment.TableSelectionProtocol, CustomersListFragment.ListViewToModelProtocol {

    SelectTableFragment mSelectTableFragment = SelectTableFragment.newInstance();
    CustomersListFragment mCustomersListFragment = CustomersListFragment.newInstance();
    private Model mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        final App application = (App) getApplication();
        mModel = application.getModel();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_fragment, mCustomersListFragment)
                .commit();
    }

    @Override
    public void onCustomerSelected(Customer item) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_fragment, mSelectTableFragment)
                .addToBackStack(null)
                .commit();
        mModel.getTableObservable()
                .subscribe(new Action1<List<Table>>() {
                    @Override
                    public void call(List<Table> tables) {
                        mSelectTableFragment.setTables(tables);
                    }
                });
    }

    @Override
    public Observable<List<Customer>> getCustomers() {
        return mModel.getCustomersObservable();
    }

    @Override
    public void onTableSelected(final int tableId) {
        mModel.reserveTable(tableId);
    }
}
