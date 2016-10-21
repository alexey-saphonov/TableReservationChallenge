package com.alexeysafonov.tablereservationchallenge.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by asa on 21.10.16.
 */

public class Table extends RealmObject {
    @PrimaryKey
    int id;
    boolean available;
}
