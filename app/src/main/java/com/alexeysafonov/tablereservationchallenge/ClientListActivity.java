package com.alexeysafonov.tablereservationchallenge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.alexeysafonov.tablereservationchallenge.fragments.CustomersListFragment;
import com.alexeysafonov.tablereservationchallenge.fragments.SelectTableFragment;
import com.alexeysafonov.tablereservationchallenge.model.Customer;
import com.alexeysafonov.tablereservationchallenge.model.Table;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ClientListActivity extends AppCompatActivity implements SelectTableFragment.TableSelectionProtocol, CustomersListFragment.ListViewToModelProtocol {

    private static final long VALIDITY_PERIOD = 10 * 60 * 1000; // 10 minutes.
    public static final int NUMBER_OF_TABLES = 70;
    public static final String RESERVATIONS_VALIDITY_KEY = "reservations-validity";
    public static final String CUSTOMER_LIST_JSON_FILE_NAME = "customer_list.json";

    SelectTableFragment mSelectTableFragment = SelectTableFragment.newInstance();
    CustomersListFragment mCustomersListFragment = CustomersListFragment.newInstance();
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        //Realm.deleteRealm(realmConfiguration);
        mRealm = Realm.getInstance(realmConfiguration);
        // Initial setup.
        try {
            loadJsonFromStream(mRealm, Customer.class, CUSTOMER_LIST_JSON_FILE_NAME);
        } catch (IOException ex) {
            throw new RuntimeException("Resource file with customers is missing! Must be in the project!");
        }

        scheduleReservationReset();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_fragment, mCustomersListFragment)
                .commit();
    }

    private void scheduleReservationReset() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Check the last update.
                        long validTo = getPreferences(MODE_PRIVATE).getLong(RESERVATIONS_VALIDITY_KEY, 0);
                        long now = Calendar.getInstance().getTimeInMillis();
                        if (validTo - now < 0) {
                            // Add 70 tables (0..69) all are available.
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(initTables());
                                }
                            });

                            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                            editor.putLong(RESERVATIONS_VALIDITY_KEY, now + VALIDITY_PERIOD);
                            editor.apply();
                        }
                    }
                });
            }
        }, 0L, VALIDITY_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private List<Table> initTables() {
        List<Table> tables = new ArrayList<>(NUMBER_OF_TABLES);
        for (int i = 0; i < NUMBER_OF_TABLES; i++) {
            Table table = new Table();
            table.setId(i);
            table.setAvailable(true);
            tables.add(table);
        }
        return tables;
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
            realm.createOrUpdateAllFromJson(realmClass, stream);
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
        mRealm.where(Table.class).findAll()
                .asObservable()
                .subscribe(new Action1<RealmResults<Table>>() {
                    @Override
                    public void call(RealmResults<Table> tables) {
                        mSelectTableFragment.setTables(Arrays.asList(tables.toArray(new Table[tables.size()])));
                    }
                });
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
    public void onTableSelected(final int tableId) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
                table.setAvailable(false);
            }
        });
    }
}
