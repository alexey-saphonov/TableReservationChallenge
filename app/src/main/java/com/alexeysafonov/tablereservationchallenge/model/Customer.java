package com.alexeysafonov.tablereservationchallenge.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * This class represents a customer in the system.
 */
public class Customer extends RealmObject {

    String customerFirstName;
    String customerLastName;
    @PrimaryKey
    int id;

    public String getFirstName() {
        return customerFirstName;
    }

    public String getLastName() {
        return customerLastName;
    }

    public int getId() {
        return id;
    }
}
