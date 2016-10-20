package com.alexeysafonov.tablereservationchallenge.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * This class represents reservation in the local storage.
 */
public class Reservation extends RealmObject {

    // Simultaneously only one table can be reserved so it can be a valid primary key.
    @PrimaryKey
    int mTableId;

    int mCustomerId;
    long mValidForMs;

    public int getCustomerId() {
        return mCustomerId;
    }

    public int getTableId() {
        return mTableId;
    }

    public long getValidForMs() {
        return mValidForMs;
    }

    public void setCustomerId(int customerId) {
        mCustomerId = customerId;
    }

    public void setTableId(int tableId) {
        mTableId = tableId;
    }

    public void setValidForMs(long validForMs) {
        mValidForMs = validForMs;
    }
}
