package com.alexeysafonov.tablereservationchallenge.model;

import com.google.gson.annotations.SerializedName;

import io.realm.annotations.PrimaryKey;

/**
 * This class represents a customer in the system.
 */
public class Customer {

    @SerializedName("customerFirstName")
    String mFirstName;
    @SerializedName("customerLastName")
    String mLastName;
    @PrimaryKey
    @SerializedName("id")
    int mId;

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public int getId() {
        return mId;
    }
}
