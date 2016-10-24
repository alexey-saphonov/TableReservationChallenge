package com.alexeysafonov.tablereservationchallenge.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

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
import rx.functions.Func1;

import static android.content.Context.MODE_PRIVATE;

/**
 * This class contains the model of the reservation application.
 */
public class Model {

    private static final long VALIDITY_PERIOD = 10 * 60 * 1000; // 10 minutes.
    public static final int NUMBER_OF_TABLES = 70;
    public static final String RESERVATIONS_VALIDITY_KEY = "reservations-validity";
    public static final String CUSTOMER_LIST_JSON_FILE_NAME = "customer_list.json";
    public static final String RESERVATIONS_METADATA_SHP = "reservations-metadata";

    private Realm mRealm;
    private Context mContext;
    private Handler mHandler;

    public void init(@NonNull final  Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        mRealm = Realm.getInstance(realmConfiguration);
        // Initial setup.
        try {
            loadJsonFromStream(mRealm, Customer.class, CUSTOMER_LIST_JSON_FILE_NAME);
        } catch (IOException ex) {
            throw new RuntimeException("Resource file with customers is missing! Must be in the project!");
        }
        scheduleReservationReset();
    }

    private void scheduleReservationReset() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Check the last update.
                        long validTo = mContext.getSharedPreferences(RESERVATIONS_METADATA_SHP, MODE_PRIVATE).getLong(RESERVATIONS_VALIDITY_KEY, 0);
                        long now = Calendar.getInstance().getTimeInMillis();
                        if (validTo - now < 0) {
                            // Add 70 tables (0..69) all are available.
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(initTables());
                                }
                            });

                            SharedPreferences.Editor editor = mContext.getSharedPreferences(RESERVATIONS_METADATA_SHP, MODE_PRIVATE).edit();
                            editor.putLong(RESERVATIONS_VALIDITY_KEY, now + VALIDITY_PERIOD);
                            editor.apply();
                        }
                    }
                });
            }
        }, 0L, VALIDITY_PERIOD, TimeUnit.MILLISECONDS);
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
        InputStream stream = mContext.getAssets().open(fileName);

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

    public Observable<List<Table>> getTableObservable() {
        return mRealm.where(Table.class).findAll()
                .asObservable()
                .map(new Func1<RealmResults<Table>, List<Table>>() {
                    @Override
                    public List<Table> call(RealmResults<Table> tables) {
                        return Arrays.asList(tables.toArray(new Table[tables.size()]));
                    }
                });
    }

    public Observable<List<Customer>> getCustomersObservable() {
        return mRealm.where(Customer.class).findAllAsync()
                .asObservable()
                .map(new Func1<RealmResults<Customer>, List<Customer>>() {
                    @Override
                    public List<Customer> call(RealmResults<Customer> customers) {
                        return Arrays.asList(customers.toArray(new Customer[customers.size()]));
                    }
                });
    }

    public void reserveTable(final int tableId) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
                table.setAvailable(false);
            }
        });
    }
}
