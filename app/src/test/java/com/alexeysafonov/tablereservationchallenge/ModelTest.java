package com.alexeysafonov.tablereservationchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import com.alexeysafonov.tablereservationchallenge.model.Model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.log.RealmLog;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Realm.class, RealmLog.class, Calendar.class, Looper.class})
public class ModelTest {

    @Mock
    Calendar mMockCalendar;
    @Mock
    Context mMockContext;
    @Mock
    SharedPreferences mMockSharedPreferences;
    @Mock
    Looper mMockLooper;
    @Mock
    Realm mMockRealm;

    Model mModel = new Model();

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        Mockito.when(mMockContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(mMockSharedPreferences);

        PowerMockito.mockStatic(Realm.class);
        PowerMockito.mockStatic(RealmLog.class);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.mockStatic(Looper.class);

        BDDMockito.given(Realm.getInstance(Mockito.any(RealmConfiguration.class))).willReturn(mMockRealm);
        BDDMockito.given(Looper.getMainLooper()).willReturn(mMockLooper);
        BDDMockito.given(Calendar.getInstance()).willReturn(mMockCalendar);
    }

    @Test
    public void testInitialize() {
        mModel.init(mMockContext);


    }
}