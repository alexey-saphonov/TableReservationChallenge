package com.alexeysafonov.tablereservationchallenge.api;

import com.alexeysafonov.tablereservationchallenge.model.Customer;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Backend APIs.
 */
public interface ReservationApi {

    @GET("/customer-list.json")
    Observable<Customer> getCutomers();

    @GET("/table-map.json")
    Observable<List<Boolean>> getTables();
}
