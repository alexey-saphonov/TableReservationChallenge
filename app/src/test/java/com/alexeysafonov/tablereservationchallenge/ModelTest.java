package com.alexeysafonov.tablereservationchallenge;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;

import com.alexeysafonov.tablereservationchallenge.model.Model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.log.RealmLog;

/**
 * This class tests {@link Model} behavior: initialization (TODO, customers request, tables request and table reservation.)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Realm.class, RealmLog.class, Calendar.class, Looper.class, Model.class, Runnable.class})
public class ModelTest {

    private static final String TEST_CUSTOMERS_JSON = "[\n" +
            "  {\n" +
            "    \"customerFirstName\": \"Marilyn\",\n" +
            "    \"customerLastName\": \"Monroe\",\n" +
            "    \"id\": 0\n" +
            "  },\n" +
            "  {\n" +
            "    \"customerFirstName\": \"Abraham\",\n" +
            "    \"customerLastName\": \"Lincoln\",\n" +
            "    \"id\": 1\n" +
            "  } ]";

    Calendar mMockCalendar;
    @Mock
    Context mMockContext;
    @Mock
    SharedPreferences mMockSharedPreferences;
    @Mock
    SharedPreferences.Editor mMockSharedPreferencesEditor;
    Looper mMockLooper;
    Handler mMockMainThreadHandler;
    Realm mMockRealm;
    @Mock
    AssetManager mMockAssetManager;
    @Mock
    ScheduledExecutorService mMockScheduler;
    @Spy
    InputStream mClientListInputStream = new ByteArrayInputStream(TEST_CUSTOMERS_JSON.getBytes(Charset.forName("UTF-8")));

    Model mModel = new Model();

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        Mockito.when(mMockContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(mMockSharedPreferences);
        Mockito.when(mMockSharedPreferences.edit()).thenReturn(mMockSharedPreferencesEditor);
        Mockito.when(mMockContext.getApplicationContext()).thenReturn(mMockContext);
        Mockito.when(mMockContext.getAssets()).thenReturn(mMockAssetManager);
        Mockito.when(mMockAssetManager.open(Mockito.anyString())).thenReturn(mClientListInputStream);
        // Mock scheduler.
        Mockito.when(mMockScheduler.scheduleAtFixedRate(Mockito.any(Runnable.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class)))
                .thenAnswer(new Answer<ScheduledFuture<?>>() {
                    @Override
                    public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
                        ((Runnable)invocation.getArguments()[0]).run();
                        return null;
                    }
                });
        mModel.setScheduler(mMockScheduler);

        PowerMockito.mockStatic(Realm.class);
        PowerMockito.mockStatic(RealmLog.class);
        PowerMockito.mockStatic(Calendar.class);
        PowerMockito.mockStatic(Looper.class);

        // Mock main thread handler.
        mMockMainThreadHandler = PowerMockito.mock(Handler.class);
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable)invocation.getArguments()[0]).run();
                return true;
            }
        }).when(mMockMainThreadHandler).post(Mockito.any(Runnable.class));

        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable)invocation.getArguments()[0]).run();
                return true;
            }
        }).when(mMockMainThreadHandler).post(Mockito.any(Runnable.class));

        mMockLooper = PowerMockito.mock(Looper.class);
        mMockRealm = PowerMockito.mock(Realm.class);
        mMockCalendar = PowerMockito.mock(Calendar.class);

        PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mMockRealm);
        PowerMockito.when(Looper.getMainLooper()).thenReturn(mMockLooper);
        PowerMockito.when(Calendar.getInstance()).thenReturn(mMockCalendar);
        PowerMockito.whenNew(Handler.class).withArguments(mMockLooper).thenReturn(mMockMainThreadHandler);
    }

    @Test
    public void testInitialize() throws IOException {
        // Then init a model for the first time.
        mModel.init(mMockContext);

        // Expected that for the clean database initialization will be called.
        Mockito.verify(mMockAssetManager).open(Mockito.anyString());
        // And some input stream comes to realm.
        Mockito.verify(mMockRealm).beginTransaction();
        Mockito.verify(mMockRealm).createOrUpdateAllFromJson(Mockito.any(Class.class), Mockito.any(InputStream.class));
        Mockito.verify(mMockRealm).commitTransaction();

        // Then it should check last validity in preferences.
        Mockito.verify(mMockSharedPreferences).getLong(Mockito.anyString(), Mockito.anyLong());
        // Then it should initialize some tables.
        Mockito.verify(mMockRealm).executeTransaction(Mockito.any(Realm.Transaction.class));
        // Validity time is updated.
        Mockito.verify(mMockSharedPreferencesEditor).putLong(Mockito.anyString(), Mockito.anyLong());
    }
}