package com.alexeysafonov.tablereservationchallenge;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ClientListActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<ClientListActivity> mActivityRule = new ActivityTestRule<>(ClientListActivity.class);

    @Test
    public void mainPathCheck() {
        // See the recycler view.
        onView(withId(R.id.list)).check(matches(isDisplayed()));
        // Click on any item.
        onView(withId(R.id.list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Now grid must be visible.
        onView(withId(R.id.table_grid)).check(matches(isDisplayed()));
    }
}
