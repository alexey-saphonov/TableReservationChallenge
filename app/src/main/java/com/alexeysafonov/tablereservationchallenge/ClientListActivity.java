package com.alexeysafonov.tablereservationchallenge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.alexeysafonov.tablereservationchallenge.fragments.CustomersListFragment;
import com.alexeysafonov.tablereservationchallenge.fragments.SelectTableFragment;
import com.alexeysafonov.tablereservationchallenge.model.Customer;
import com.alexeysafonov.tablereservationchallenge.model.Table;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

public class ClientListActivity extends AppCompatActivity implements SelectTableFragment.TableSelectionProtocol, CustomersListFragment.ListViewToModelProtocol {

    SelectTableFragment mSelectTableFragment = SelectTableFragment.newInstance();
    CustomersListFragment mCustomersListFragment = CustomersListFragment.newInstance();
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration);
        mRealm = Realm.getInstance(realmConfiguration);
        try {
            loadJsonFromStream(mRealm, Customer.class, "customer_list.json");
        } catch (IOException ex) {
            throw new RuntimeException("Resource file with customers is missing! Must be in the project!");
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_fragment, mCustomersListFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private List<Table> loadTables() {
        return null;
    }

    private String readTextFileFromAssets(@NonNull final String fileName) {
        String text = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return text;
    }

    private void loadJsonFromStream(@NonNull final Realm realm,
                                    @NonNull final Class<? extends RealmObject> realmClass,
                                    @NonNull final String fileName) throws IOException {
        // Use streams if you are worried about the size of the JSON whether it was persisted on disk
        // or received from the network.
        InputStream stream = getAssets().open(fileName);

        // Open a transaction to store items into the realm
        realm.beginTransaction();
        try {
            realm.createAllFromJson(realmClass, stream);
            realm.commitTransaction();
        } catch (IOException e) {
            // Remember to cancel the transaction if anything goes wrong.
            realm.cancelTransaction();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @Override
    public void onCustomerSelected(Customer item) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_fragment, mSelectTableFragment)
                .addToBackStack(null)
                .commit();
        mSelectTableFragment.setTables(Arrays.asList(new Boolean[] {true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false}));
    }

    @Override
    public Observable<List<Customer>> getCustomers() {
        return mRealm.where(Customer.class).findAllAsync()
                .asObservable()
                .map(new Func1<RealmResults<Customer>, List<Customer>>() {
            @Override
            public List<Customer> call(RealmResults<Customer> customers) {
                return Arrays.asList(customers.toArray(new Customer[customers.size()]));
            }
        });
    }

    @Override
    public void onTableSelected(int tableId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .remove(mSelectTableFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
