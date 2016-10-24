package com.alexeysafonov.tablereservationchallenge.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * This class represents a customer in the system.
 */
@RealmClass
public class Customer extends RealmObject {

    protected String customerFirstName;
    protected String customerLastName;
    @PrimaryKey
    protected int id;

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
